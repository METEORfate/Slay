package com.course.slay.engine;

import com.course.slay.domain.BattleState;
import com.course.slay.domain.Combatant;
import com.course.slay.domain.GameStatus;
import com.course.slay.domain.Player;
import com.course.slay.domain.card.Card;
import com.course.slay.domain.card.CardFactory;
import com.course.slay.domain.card.CardRarity;
import com.course.slay.domain.card.CardVisualEffect;
import com.course.slay.domain.card.EffectContext;
import com.course.slay.domain.character.CharacterCatalog;
import com.course.slay.domain.character.PlayableCharacter;
import com.course.slay.domain.enemy.EnemyAction;
import com.course.slay.domain.enemy.Enemy;
import com.course.slay.domain.enemy.EnemyFactory;
import com.course.slay.domain.run.MapNode;
import com.course.slay.domain.run.MapNodeType;
import com.course.slay.domain.run.RouteMapFactory;
import com.course.slay.domain.run.RunPhase;
import com.course.slay.domain.run.RunState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class GameEngine {
    public static final int NORMAL_COMBAT_GOLD = 12;
    public static final int ELITE_COMBAT_GOLD = 25;
    public static final int SKIP_REWARD_GOLD = 5;
    public static final int REST_HEAL_PERCENT = 30;
    public static final int SHOP_CARD_PRICE = 35;
    public static final int SHOP_REMOVE_CARD_PRICE = 50;
    private static final List<RarityWeight> REWARD_RARITY_WEIGHTS = List.of(
            new RarityWeight(CardRarity.COMMON, 60),
            new RarityWeight(CardRarity.UNCOMMON, 30),
            new RarityWeight(CardRarity.RARE, 10)
    );

    private final Random random;
    private BattleState state;
    private RunState runState;

    public GameEngine() {
        this(new Random());
    }

    public GameEngine(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public BattleState startNewBattle() {
        PlayableCharacter character = CharacterCatalog.defaultCharacter();
        runState = null;
        return startBattle(
                character.createPlayer(),
                EnemyFactory.createFirstEnemy(),
                character.createStarterDeck(),
                true
        );
    }

    public List<PlayableCharacter> getAvailableCharacters() {
        return CharacterCatalog.availableCharacters();
    }

    public RunState startNewRun() {
        return startNewRun(CharacterCatalog.defaultCharacter());
    }

    public RunState startNewRun(String characterId) {
        PlayableCharacter character = CharacterCatalog.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown character id: " + characterId));
        return startNewRun(character);
    }

    public RunState startNewRun(PlayableCharacter character) {
        Objects.requireNonNull(character, "character");
        List<Card> starterDeck = new ArrayList<>(character.createStarterDeck());
        runState = new RunState(
                character,
                character.createPlayer(),
                starterDeck,
                RouteMapFactory.createRandomMap(random)
        );
        state = null;
        runState.addLog("选择角色：" + character.name() + "（" + character.archetype() + "）。");
        runState.addLog("远征开始：地图已随机生成，从入口路线中选择一个节点。");
        return runState;
    }

    public RunState getRunState() {
        return runState;
    }

    public BattleState getState() {
        return state;
    }

    public boolean selectMapNode(String nodeId) {
        if (runState == null || runState.getPhase() != RunPhase.MAP) {
            return false;
        }

        MapNode node = runState.getMap()
                .findNode(nodeId)
                .orElse(null);
        if (node == null || !node.isAvailable()) {
            runState.addLog("该路线节点当前不可进入。");
            return false;
        }

        runState.setCurrentNode(node);
        runState.addLog("进入节点：" + node.getName() + "（" + node.getType().getDisplayName() + "）。");
        if (node.getType() == MapNodeType.REST) {
            state = null;
            runState.setPhase(RunPhase.REST_SITE);
            runState.addLog("抵达营地：可以休息恢复最大生命的 " + REST_HEAL_PERCENT + "%，或升级一张牌。");
            return true;
        }
        if (node.getType() == MapNodeType.EVENT) {
            state = null;
            resolveEventNode();
            if (runState.getPhase() == RunPhase.RUN_DEFEAT) {
                return true;
            }
            completeRunNode();
            return true;
        }
        if (node.getType() == MapNodeType.SHOP) {
            state = null;
            runState.setShopCards(createRewardChoices(3));
            runState.setPhase(RunPhase.SHOP);
            runState.addLog("进入商店：可以购买卡牌、删牌或直接离开。");
            return true;
        }

        Enemy enemy = EnemyFactory.createEnemyFor(node.getType(), random);
        startBattle(runState.getPlayer(), enemy, runState.getDeck(), true);
        state.addLog("地图节点：" + node.getName() + "。");
        runState.setPhase(RunPhase.BATTLE);
        return true;
    }

    public BattleState startBattle(Player player, Enemy enemy, List<Card> deck) {
        return startBattle(player, enemy, deck, true);
    }

    BattleState startBattle(Player player, Enemy enemy, List<Card> deck, boolean shuffleDeck) {
        List<Card> copiedDeck = deck.stream()
                .map(CardFactory::copyOf)
                .collect(Collectors.toCollection(ArrayList::new));

        player.setDeck(copiedDeck);
        player.prepareDeck(random, shuffleDeck);
        enemy.prepareDeck(random, shuffleDeck);
        enemy.resetActionPattern();
        state = new BattleState(player, enemy);
        state.addLog("遭遇敌人：" + enemy.getName() + "。");
        state.addLog(enemy.getName() + " 拥有 " + enemy.getActionPattern().size() + " 步固定行动循环。");
        startPlayerTurn();
        return state;
    }

    public boolean playCard(int handIndex) {
        if (!hasActiveBattle()) {
            return false;
        }
        if (handIndex < 0 || handIndex >= state.getHand().size()) {
            state.addLog("无效的手牌位置。");
            return false;
        }

        Card card = state.getHand().get(handIndex);
        if (card.getCost() > state.getEnergy()) {
            state.addLog("能量不足，无法打出【" + card.getName() + "】。");
            return false;
        }

        state.spendEnergy(card.getCost());
        state.getHand().remove(handIndex);
        state.addLog("打出【" + card.getName() + "】。");
        card.play(new PlayerEffectContext());
        state.recordPlayedCard(card.getType());
        state.getDiscardPile().add(card);

        if (!state.getPlayer().isAlive()) {
            if (runState != null) {
                runState.setPhase(RunPhase.RUN_DEFEAT);
                runState.addLog("远征失败：生命耗尽。");
            }
            return true;
        }
        if (!state.getEnemy().isAlive()) {
            completeVictory();
        }
        return true;
    }

    public List<Set<CardVisualEffect>> endTurn() {
        if (!hasActiveBattle()) {
            return List.of();
        }

        discardHand();
        List<Set<CardVisualEffect>> enemyVisualEffects = resolveEnemyTurn();
        if (runState != null && state.getStatus() == GameStatus.DEFEAT) {
            runState.setPhase(RunPhase.RUN_DEFEAT);
            runState.addLog("远征失败：" + state.getEnemy().getName() + " 阻止了前进。");
        }
        if (!hasActiveBattle()) {
            return enemyVisualEffects;
        }

        startPlayerTurn();
        return enemyVisualEffects;
    }

    public boolean claimReward(int rewardIndex) {
        if (state == null || state.getStatus() != GameStatus.VICTORY) {
            return false;
        }
        if (rewardIndex < 0 || rewardIndex >= state.getRewardChoices().size()) {
            state.addLog("无效的奖励选择。");
            return false;
        }

        Card selected = CardFactory.copyOf(state.getRewardChoices().get(rewardIndex));
        state.getDeck().add(selected);
        if (runState != null && runState.getPhase() == RunPhase.REWARD && runState.getCurrentNode() != null) {
            runState.addLog("获得奖励牌【" + selected.getName() + "】。");
            completeRunNode();
            state.setStatus(GameStatus.REWARD_CLAIMED);
            state.addLog("获得奖励牌【" + selected.getName() + "】，返回地图继续远征。");
            return true;
        }

        state.setStatus(GameStatus.REWARD_CLAIMED);
        state.addLog("获得奖励牌【" + selected.getName() + "】。首阶段流程结束。");
        return true;
    }

    public boolean skipReward() {
        if (state == null || state.getStatus() != GameStatus.VICTORY) {
            return false;
        }
        if (runState != null && runState.getPhase() == RunPhase.REWARD && runState.getCurrentNode() != null) {
            runState.addGold(SKIP_REWARD_GOLD);
            runState.addLog("跳过奖励牌，获得 " + SKIP_REWARD_GOLD + " 金币。");
            completeRunNode();
            state.setStatus(GameStatus.REWARD_CLAIMED);
            state.addLog("跳过奖励牌，获得 " + SKIP_REWARD_GOLD + " 金币，返回地图继续远征。");
            return true;
        }
        state.setStatus(GameStatus.REWARD_CLAIMED);
        state.addLog("跳过奖励牌。");
        return true;
    }

    public boolean restAtCamp() {
        if (!isAtRestSite()) {
            return false;
        }
        int healed = runState.getPlayer().heal(restHealAmount(runState.getPlayer().getMaxHealth()));
        runState.getPlayer().resetBlock();
        runState.addLog("在营地休息，恢复 " + healed + " 点生命。");
        completeRunNode();
        return true;
    }

    public static int restHealAmount(int maxHealth) {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("maxHealth must be positive");
        }
        return Math.max(1, (int) Math.ceil(maxHealth * REST_HEAL_PERCENT / 100.0));
    }

    public boolean upgradeCardAtCamp(int deckIndex) {
        if (!isAtRestSite() || !isValidDeckIndex(deckIndex)) {
            return false;
        }
        Card card = runState.getDeck().get(deckIndex);
        if (!CardFactory.canUpgrade(card)) {
            runState.addLog("【" + card.getName() + "】无法继续升级。");
            return false;
        }
        Card upgraded = CardFactory.upgradeOf(card);
        runState.getDeck().set(deckIndex, upgraded);
        runState.addLog("在营地升级【" + card.getName() + "】为【" + upgraded.getName() + "】。");
        completeRunNode();
        return true;
    }

    public boolean buyShopCard(int shopIndex) {
        if (runState == null || runState.getPhase() != RunPhase.SHOP) {
            return false;
        }
        if (shopIndex < 0 || shopIndex >= runState.getShopCards().size()) {
            return false;
        }
        if (!runState.spendGold(SHOP_CARD_PRICE)) {
            runState.addLog("金币不足，无法购买该卡牌。");
            return false;
        }
        Card bought = CardFactory.copyOf(runState.removeShopCard(shopIndex));
        runState.getDeck().add(bought);
        runState.addLog("花费 " + SHOP_CARD_PRICE + " 金币购买【" + bought.getName() + "】。");
        return true;
    }

    public boolean removeDeckCardAtShop(int deckIndex) {
        if (runState == null || runState.getPhase() != RunPhase.SHOP || !isValidDeckIndex(deckIndex)) {
            return false;
        }
        if (runState.getDeck().size() <= 1) {
            runState.addLog("牌组至少需要保留 1 张牌，无法删牌。");
            return false;
        }
        if (!runState.spendGold(SHOP_REMOVE_CARD_PRICE)) {
            runState.addLog("金币不足，无法删牌。");
            return false;
        }
        Card removed = runState.getDeck().remove(deckIndex);
        runState.addLog("花费 " + SHOP_REMOVE_CARD_PRICE + " 金币移除【" + removed.getName() + "】。");
        return true;
    }

    public boolean leaveShop() {
        if (runState == null || runState.getPhase() != RunPhase.SHOP || runState.getCurrentNode() == null) {
            return false;
        }
        runState.clearShopCards();
        runState.addLog("离开商店，继续推进路线。");
        completeRunNode();
        return true;
    }

    private boolean hasActiveBattle() {
        return state != null && state.getStatus() == GameStatus.IN_PROGRESS;
    }

    private boolean isAtRestSite() {
        return runState != null
                && runState.getPhase() == RunPhase.REST_SITE
                && runState.getCurrentNode() != null;
    }

    private boolean isValidDeckIndex(int deckIndex) {
        return runState != null && deckIndex >= 0 && deckIndex < runState.getDeck().size();
    }

    private void startPlayerTurn() {
        state.getPlayer().resetBlock();
        state.getPlayer().setEnergy(state.getPlayer().getMaxEnergy());
        state.nextTurn();
        state.addLog("第 " + state.getTurnNumber() + " 回合开始，恢复 "
                + state.getPlayer().getMaxEnergy() + " 点能量。");
        drawCards(state.getPlayer(), state.getPlayer().getHandSize());
        prepareEnemyIntent();
    }

    private void discardHand() {
        int count = discardHand(state.getPlayer());
        state.addLog("结束回合，弃掉 " + count + " 张手牌。");
    }

    private List<Set<CardVisualEffect>> resolveEnemyTurn() {
        Enemy enemy = state.getEnemy();
        enemy.resetBlock();
        enemy.setEnergy(0);
        state.addLog(enemy.getName() + " 回合开始，执行预告意图。");
        if (state.consumeSkipNextEnemyTurn()) {
            state.setEnemyIntent(null);
            state.addLog(enemy.getName() + " 被沉默，跳过了本次行动。");
            return List.of();
        }

        EnemyAction action = enemy.takeNextAction();
        state.setEnemyIntent(null);
        int beforePlayerHealth = state.getPlayer().getHealth();
        List<Set<CardVisualEffect>> visualEffects = List.of(
                action.execute(enemy, state.getPlayer(), state::addLog)
        );
        int healthDamage = Math.max(0, beforePlayerHealth - state.getPlayer().getHealth());
        if (healthDamage > 0) {
            applyOnDamageBuff();
        }

        if (!state.getPlayer().isAlive()) {
            state.setStatus(GameStatus.DEFEAT);
            state.addLog("你倒在远征途中。");
        }
        enemy.setEnergy(0);
        return visualEffects;
    }

    private void prepareEnemyIntent() {
        Enemy enemy = state.getEnemy();
        state.setEnemyIntent(enemy.peekNextAction().toIntent());
        state.addLog(enemy.getName() + " 正在准备：" + state.getEnemyIntent().summary() + "。");
    }

    private void drawCards(int amount) {
        drawCards(state.getPlayer(), amount);
    }

    private void drawCards(Combatant actor, int amount) {
        int drawn = 0;
        for (int i = 0; i < amount; i++) {
            if (actor.getDrawPile().isEmpty()) {
                if (actor.getDiscardPile().isEmpty()) {
                    break;
                }
                actor.getDrawPile().addAll(actor.getDiscardPile());
                actor.getDiscardPile().clear();
                Collections.shuffle(actor.getDrawPile(), random);
                state.addLog(actor.getName() + " 的弃牌堆洗入抽牌堆。");
            }
            actor.getHand().add(actor.getDrawPile().remove(0));
            drawn++;
        }

        if (drawn > 0) {
            state.addLog(actor.getName() + " 抽牌 " + drawn + " 张。");
        }
    }

    private int discardHand(Combatant actor) {
        int count = actor.getHand().size();
        actor.getDiscardPile().addAll(actor.getHand());
        actor.getHand().clear();
        if (actor == state.getPlayer() && count > 0) {
            applyDiscardEnergyBonus(count);
        }
        return count;
    }

    private void applyDiscardEnergyBonus(int discardedCards) {
        int energyPerCard = state.getEnergyPerDiscardThisTurn();
        if (energyPerCard <= 0) {
            return;
        }
        int gained = energyPerCard * discardedCards;
        state.setEnergy(state.getEnergy() + gained);
        state.addLog("弃掉 " + discardedCards + " 张牌，战术大师额外获得 " + gained + " 点能量。");
    }

    private void applyOnDamageBuff() {
        int strength = state.getStrengthOnDamage();
        int block = state.getBlockOnDamage();
        if (strength <= 0 && block <= 0) {
            return;
        }
        if (strength > 0) {
            state.getPlayer().gainStrength(strength);
        }
        if (block > 0) {
            state.getPlayer().gainBlock(block);
        }
        state.addLog("受伤触发：获得 " + strength + " 点力量和 " + block + " 点格挡。");
    }

    private void completeVictory() {
        state.setStatus(GameStatus.VICTORY);

        if (runState != null && runState.getCurrentNode() != null
                && runState.getCurrentNode().getType() == MapNodeType.BOSS) {
            state.getRewardChoices().clear();
            state.addLog("首领倒下，远征通关。");
            runState.addLog("击败首领【" + state.getEnemy().getName() + "】，远征通关。");
            runState.getMap().completeNode(runState.getCurrentNode());
            runState.setCurrentNode(null);
            runState.setPhase(RunPhase.RUN_VICTORY);
            return;
        }

        state.getRewardChoices().clear();
        state.getRewardChoices().addAll(createRewardChoices(3));
        if (runState != null && runState.getCurrentNode() != null) {
            int goldReward = combatGoldReward(runState.getCurrentNode().getType());
            if (goldReward > 0) {
                runState.addGold(goldReward);
                runState.addLog("获得战斗金币：" + goldReward + "。");
                state.addLog("获得战斗金币：" + goldReward + "。");
            }
            runState.setPhase(RunPhase.REWARD);
            runState.addLog("完成战斗节点：" + runState.getCurrentNode().getName() + "。");
            state.addLog("敌人倒下，战斗胜利。请选择一张奖励牌后返回地图。");
            return;
        }
        state.addLog("敌人倒下，战斗胜利。请选择一张奖励牌。");
    }

    private void completeRunNode() {
        MapNode node = runState.getCurrentNode();
        runState.getMap().completeNode(node);
        runState.addLog("路线推进：" + node.getName() + " 已完成。");
        runState.setCurrentNode(null);
        runState.clearShopCards();
        runState.setPhase(RunPhase.MAP);
    }

    private int combatGoldReward(MapNodeType type) {
        return switch (type) {
            case NORMAL -> NORMAL_COMBAT_GOLD;
            case ELITE -> ELITE_COMBAT_GOLD;
            case REST, EVENT, SHOP, BOSS -> 0;
        };
    }

    private void resolveEventNode() {
        int roll = random.nextInt(3);
        if (roll == 0) {
            int healed = runState.getPlayer().heal(10);
            runState.addLog("特殊事件：发现安全藏身处，恢复 " + healed + " 点生命。");
            return;
        }
        if (roll == 1) {
            int before = runState.getPlayer().getHealth();
            runState.getPlayer().takeDamage(6);
            int damage = Math.max(0, before - runState.getPlayer().getHealth());
            runState.addLog("特殊事件：穿过塌陷街区，失去 " + damage + " 点生命。");
            if (!runState.getPlayer().isAlive()) {
                runState.setPhase(RunPhase.RUN_DEFEAT);
                runState.addLog("远征失败：事件伤害耗尽了生命。");
            }
            return;
        }

        Card found = CardFactory.copyOf(randomRewardCard());
        runState.getDeck().add(found);
        runState.addLog("特殊事件：找到遗落战术牌【" + found.getName() + "】。");
    }

    private List<Card> createRewardChoices(int count) {
        List<Card> pool = new ArrayList<>(currentRewardPool());
        List<Card> choices = new ArrayList<>();
        while (choices.size() < count && !pool.isEmpty()) {
            Card selected = selectWeightedRewardCard(pool);
            choices.add(CardFactory.copyOf(selected));
            pool.removeIf(card -> card.getId().equals(selected.getId()));
        }
        return choices;
    }

    private Card randomRewardCard() {
        List<Card> pool = new ArrayList<>(currentRewardPool());
        if (pool.isEmpty()) {
            throw new IllegalStateException("reward pool must not be empty");
        }
        return selectWeightedRewardCard(pool);
    }

    private Card selectWeightedRewardCard(List<Card> pool) {
        CardRarity rarity = rollRewardRarity(pool);
        List<Card> candidates = pool.stream()
                .filter(card -> card.getRarity() == rarity)
                .toList();
        if (candidates.isEmpty()) {
            candidates = pool;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private CardRarity rollRewardRarity(List<Card> pool) {
        List<RarityWeight> availableWeights = REWARD_RARITY_WEIGHTS.stream()
                .filter(weight -> pool.stream().anyMatch(card -> card.getRarity() == weight.rarity()))
                .toList();
        if (availableWeights.isEmpty()) {
            return pool.get(random.nextInt(pool.size())).getRarity();
        }

        int totalWeight = availableWeights.stream()
                .mapToInt(RarityWeight::weight)
                .sum();
        int roll = random.nextInt(totalWeight);
        int cursor = 0;
        for (RarityWeight weight : availableWeights) {
            cursor += weight.weight();
            if (roll < cursor) {
                return weight.rarity();
            }
        }
        return availableWeights.get(availableWeights.size() - 1).rarity();
    }

    private List<Card> currentRewardPool() {
        if (runState == null) {
            return CardFactory.rewardPool();
        }
        return runState.getPlayableCharacter().createRewardPool();
    }

    private final class PlayerEffectContext implements EffectContext {
        @Override
        public void dealDamageToOpponent(int amount) {
            int beforeHealth = state.getEnemy().getHealth();
            int beforeBlock = state.getEnemy().getBlock();
            int totalDamage = amount + state.getPlayer().getStrength();
            state.getEnemy().takeDamage(totalDamage);
            int blocked = Math.max(0, beforeBlock - state.getEnemy().getBlock());
            int healthDamage = Math.max(0, beforeHealth - state.getEnemy().getHealth());
            state.addLog("造成 " + healthDamage + " 点生命伤害，敌人格挡抵消 " + blocked + " 点。");
        }

        @Override
        public void gainBlock(int amount) {
            state.getPlayer().gainBlock(amount);
            state.addLog("获得 " + amount + " 点格挡。");
        }

        @Override
        public int currentBlock() {
            return state.getPlayer().getBlock();
        }

        @Override
        public boolean hasPlayedAttackThisTurn() {
            return state.hasPlayedAttackThisTurn();
        }

        @Override
        public void drawCards(int amount) {
            GameEngine.this.drawCards(amount);
        }

        @Override
        public void gainEnergy(int amount) {
            state.setEnergy(state.getEnergy() + amount);
            state.addLog("获得 " + amount + " 点能量。");
        }

        @Override
        public void healSelf(int amount) {
            int healed = state.getPlayer().heal(amount);
            state.addLog("恢复 " + healed + " 点生命。");
        }

        @Override
        public void gainStrength(int amount) {
            state.getPlayer().gainStrength(amount);
            state.addLog("获得 " + amount + " 点力量。");
        }

        @Override
        public void gainPermanentStrength(int amount) {
            state.getPlayer().gainPermanentStrength(amount);
            state.addLog("永久获得 " + amount + " 点力量。");
        }

        @Override
        public void loseHealth(int amount) {
            int lost = state.getPlayer().loseHealth(amount);
            state.addLog("失去 " + lost + " 点生命。");
            if (!state.getPlayer().isAlive()) {
                state.setStatus(GameStatus.DEFEAT);
                state.addLog("你倒在远征途中。");
            }
        }

        @Override
        public void skipNextEnemyTurn() {
            state.skipNextEnemyTurn();
        }

        @Override
        public void addEnergyPerDiscardThisTurn(int amount) {
            state.addEnergyPerDiscardThisTurn(amount);
        }

        @Override
        public void addOnDamageBuff(int strength, int block) {
            state.addOnDamageBuff(strength, block);
        }

        @Override
        public void log(String message) {
            state.addLog(message);
        }
    }

    private record RarityWeight(CardRarity rarity, int weight) {
    }
}
