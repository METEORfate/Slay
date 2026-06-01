package com.course.slay.engine;

import com.course.slay.domain.BattleState;
import com.course.slay.domain.Combatant;
import com.course.slay.domain.GameStatus;
import com.course.slay.domain.Player;
import com.course.slay.domain.card.Card;
import com.course.slay.domain.card.CardFactory;
import com.course.slay.domain.card.CardRarity;
import com.course.slay.domain.card.CardVisualEffect;
import com.course.slay.domain.enemy.Enemy;
import com.course.slay.domain.enemy.EnemyAction;
import com.course.slay.domain.enemy.EnemyFactory;
import com.course.slay.domain.enemy.EnemyIntent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEngineTest {
    @Test
    void startsBattleWithOpeningHandAndEnergy() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(testPlayer(), passiveEnemy(30), starterLikeDeck(), false);

        assertEquals(GameStatus.IN_PROGRESS, state.getStatus());
        assertEquals(1, state.getTurnNumber());
        assertEquals(3, state.getEnergy());
        assertEquals(5, state.getHand().size());
        assertEquals(5, state.getDrawPile().size());
    }

    @Test
    void playingAttackConsumesEnergyAndDamagesEnemy() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(30),
                List.of(CardFactory.dawnBreaker()),
                false
        );

        assertTrue(engine.playCard(0));

        assertEquals(1, state.getEnergy());
        assertEquals(18, state.getEnemy().getHealth());
        assertEquals(1, state.getDiscardPile().size());
    }

    @Test
    void blockReducesEnemyDamageAndResetsNextTurn() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                attackerEnemy(6),
                List.of(CardFactory.ironCurtain()),
                false
        );

        assertTrue(engine.playCard(0));
        engine.endTurn();

        assertEquals(39, state.getPlayer().getHealth());
        assertEquals(0, state.getPlayer().getBlock());
        assertEquals(2, state.getTurnNumber());
    }

    @Test
    void drawCardEffectPullsFromDrawPile() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(30),
                List.of(
                        CardFactory.swiftStep(),
                        CardFactory.emberStrike(),
                        CardFactory.ironCurtain(),
                        CardFactory.emberStrike(),
                        CardFactory.ironCurtain(),
                        CardFactory.dawnBreaker()
                ),
                false
        );

        assertTrue(engine.playCard(0));

        assertEquals(5, state.getHand().size());
        assertTrue(state.getHand().stream().anyMatch(card -> card.getId().equals("dawn_breaker")));
        assertEquals(0, state.getDrawPile().size());
    }

    @Test
    void energyAndHealingCardsApplyEffects() {
        GameEngine engine = new GameEngine(new Random(1));
        Player player = testPlayer();
        player.takeDamage(10);
        BattleState state = engine.startBattle(
                player,
                passiveEnemy(30),
                List.of(CardFactory.sparkFocus(), CardFactory.fieldDressing()),
                false
        );

        assertTrue(engine.playCard(0));
        assertEquals("field_dressing", state.getHand().get(0).getId());
        assertEquals(4, state.getEnergy());

        assertTrue(engine.playCard(0));

        assertEquals(3, state.getEnergy());
        assertEquals(34, state.getPlayer().getHealth());
    }

    @Test
    void discardsHandAndShufflesDiscardWhenDrawPileIsEmpty() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(30),
                List.of(
                        CardFactory.emberStrike(),
                        CardFactory.emberStrike(),
                        CardFactory.ironCurtain(),
                        CardFactory.ironCurtain(),
                        CardFactory.swiftStep()
                ),
                false
        );

        engine.endTurn();

        assertEquals(2, state.getTurnNumber());
        assertEquals(5, state.getHand().size());
        assertEquals(0, state.getDiscardPile().size());
        assertEquals(0, state.getDrawPile().size());
        assertTrue(state.getBattleLog().stream().anyMatch(line -> line.contains("弃牌堆洗入抽牌堆")));
    }

    @Test
    void victoryCreatesRewardChoicesAndRewardCanBeClaimed() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(10),
                List.of(CardFactory.dawnBreaker()),
                false
        );

        assertTrue(engine.playCard(0));

        assertEquals(GameStatus.VICTORY, state.getStatus());
        assertEquals(3, state.getRewardChoices().size());
        int beforeDeckSize = state.getDeck().size();

        assertTrue(engine.claimReward(0));

        assertEquals(GameStatus.REWARD_CLAIMED, state.getStatus());
        assertEquals(beforeDeckSize + 1, state.getDeck().size());
    }

    @Test
    void rewardChoicesUseConfiguredRarityWeights() {
        GameEngine engine = new GameEngine(new FixedRandom(0, 0, 60, 0, 70, 0));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(10),
                List.of(CardFactory.dawnBreaker()),
                false
        );

        assertTrue(engine.playCard(0));

        assertEquals(CardRarity.COMMON, state.getRewardChoices().get(0).getRarity());
        assertEquals(CardRarity.RARE, state.getRewardChoices().get(1).getRarity());
        assertEquals(CardRarity.LEGENDARY, state.getRewardChoices().get(2).getRarity());
    }

    @Test
    void bloodlettingGainsEnergyAndLosesHealthWithoutConsumingBlock() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(30),
                List.of(CardFactory.ironCurtain(), CardFactory.bloodletting()),
                false
        );

        assertTrue(engine.playCard(0));
        assertTrue(engine.playCard(0));

        assertEquals(37, state.getPlayer().getHealth());
        assertEquals(5, state.getPlayer().getBlock());
        assertEquals(4, state.getEnergy());
    }

    @Test
    void comboPunchResolvesAsSeparateHitsAgainstBlock() {
        GameEngine engine = new GameEngine(new Random(1));
        Enemy enemy = passiveEnemy(30);
        enemy.gainBlock(3);
        BattleState state = engine.startBattle(
                testPlayer(),
                enemy,
                List.of(CardFactory.comboPunch()),
                false
        );

        assertTrue(engine.playCard(0));

        assertEquals(25, state.getEnemy().getHealth());
        assertEquals(0, state.getEnemy().getBlock());
    }

    @Test
    void bodySlamAndEntrenchUseCurrentBlock() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(30),
                List.of(CardFactory.ironCurtain(), CardFactory.entrench(), CardFactory.bodySlam()),
                false
        );

        assertTrue(engine.playCard(0));
        assertTrue(engine.playCard(0));
        assertTrue(engine.playCard(0));

        assertEquals(10, state.getPlayer().getBlock());
        assertEquals(20, state.getEnemy().getHealth());
    }

    @Test
    void silenceSkipsNextEnemyTurn() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                attackerEnemy(8),
                List.of(CardFactory.silence()),
                false
        );

        assertTrue(engine.playCard(0));
        engine.endTurn();

        assertEquals(40, state.getPlayer().getHealth());
        assertEquals(2, state.getTurnNumber());
    }

    @Test
    void tacticalMasterRewardsCardsDiscardedThisTurn() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(30),
                List.of(CardFactory.tacticalMaster(), CardFactory.emberStrike(), CardFactory.ironCurtain()),
                false
        );

        assertTrue(engine.playCard(0));
        engine.endTurn();

        assertTrue(state.getBattleLog().stream().anyMatch(line -> line.contains("额外获得 2 点能量")));
    }

    @Test
    void cinderSeekerCardsApplyDamageHealingAndDrawEffects() {
        GameEngine engine = new GameEngine(new Random(1));
        Player player = testPlayer();
        player.takeDamage(10);
        BattleState state = engine.startBattle(
                player,
                passiveEnemy(40),
                List.of(
                        CardFactory.bladeSurge(),
                        CardFactory.vampiricStrike(),
                        CardFactory.emberStrike(),
                        CardFactory.ironCurtain(),
                        CardFactory.swiftStep(),
                        CardFactory.backstab(),
                        CardFactory.ashNeedle()
                ),
                false
        );

        assertTrue(engine.playCard(0));
        assertEquals(6, state.getHand().size());
        assertTrue(state.getHand().stream().anyMatch(card -> card.getId().equals("backstab")));

        assertTrue(engine.playCard(0));
        assertEquals(34, state.getPlayer().getHealth());
        assertEquals(24, state.getEnemy().getHealth());
    }

    @Test
    void backstabDoublesAfterAnotherAttackCardWasPlayed() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(30),
                List.of(CardFactory.emberStrike(), CardFactory.backstab()),
                false
        );

        assertTrue(engine.playCard(0));
        assertTrue(engine.playCard(0));

        assertEquals(12, state.getEnemy().getHealth());
    }

    @Test
    void assassinBladeDanceAddsKnifeCardsToHand() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(40),
                List.of(CardFactory.assassinBladeDance()),
                false
        );

        assertTrue(engine.playCard(0));

        assertEquals(3, state.getHand().stream().filter(card -> card.getId().equals("assassin_knife")).count());
        assertTrue(engine.playCard(0));
        assertEquals(37, state.getEnemy().getHealth());
    }

    @Test
    void assassinPrecisionBoostsKnifeDamageAndFinisherOverridesItThisTurn() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(50),
                List.of(
                        CardFactory.assassinPrecision(),
                        CardFactory.assassinBladeDance(),
                        CardFactory.assassinFinisher()
                ),
                false
        );
        state.setEnergy(99);

        assertTrue(engine.playCard(0));
        assertTrue(engine.playCard(0));
        assertTrue(engine.playCard(1));
        assertTrue(engine.playCard(0));
        assertTrue(engine.playCard(0));

        assertEquals(37, state.getEnemy().getHealth());
    }

    @Test
    void assassinInfiniteBladesAddsKnifeAtNextTurnStart() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(40),
                List.of(CardFactory.assassinInfiniteBlades()),
                false
        );

        assertTrue(engine.playCard(0));
        engine.endTurn();

        assertEquals(2, state.getTurnNumber());
        assertTrue(state.getHand().stream().anyMatch(card -> card.getId().equals("assassin_knife")));
    }

    @Test
    void enrageTriggersWhenPlayerTakesHealthDamage() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                attackerEnemy(6),
                List.of(CardFactory.berserkerEnrage()),
                false
        );

        assertTrue(engine.playCard(0));
        engine.endTurn();

        assertEquals(34, state.getPlayer().getHealth());
        assertEquals(1, state.getPlayer().getStrength());
        assertTrue(state.getBattleLog().stream().anyMatch(line -> line.contains("受伤触发")));
    }

    @Test
    void berserkerBashAppliesVulnerableToNextDamage() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(40),
                List.of(CardFactory.berserkerBash(), CardFactory.berserkerStrike()),
                false
        );

        assertTrue(engine.playCard(0));
        assertTrue(engine.playCard(0));

        assertEquals(26, state.getEnemy().getHealth());
    }

    @Test
    void berserkerPierceIgnoresBlock() {
        GameEngine engine = new GameEngine(new Random(1));
        Enemy enemy = passiveEnemy(20);
        enemy.gainBlock(10);
        BattleState state = engine.startBattle(
                testPlayer(),
                enemy,
                List.of(CardFactory.berserkerPierce()),
                false
        );

        assertTrue(engine.playCard(0));

        assertEquals(14, state.getEnemy().getHealth());
        assertEquals(10, state.getEnemy().getBlock());
    }

    @Test
    void berserkerVoidLimitsNextIncomingDamageToOne() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                attackerEnemy(12),
                List.of(CardFactory.berserkerVoid()),
                false
        );

        assertTrue(engine.playCard(0));
        engine.endTurn();

        assertEquals(39, state.getPlayer().getHealth());
    }

    @Test
    void berserkerTranquilityDiscountsTheNextCard() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(50),
                List.of(CardFactory.berserkerTranquility(), CardFactory.berserkerPerfectStrike()),
                false
        );

        assertTrue(engine.playCard(0));
        assertEquals(2, state.getEnergy());
        assertTrue(engine.playCard(0));

        assertEquals(1, state.getEnergy());
        assertEquals(34, state.getEnemy().getHealth());
    }

    @Test
    void berserkerAshStrikeScalesWithCardsPlayedThisBattle() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                passiveEnemy(50),
                List.of(CardFactory.berserkerStrike(), CardFactory.berserkerAshStrike()),
                false
        );

        assertTrue(engine.playCard(0));
        assertTrue(engine.playCard(0));

        assertEquals(36, state.getEnemy().getHealth());
    }

    @Test
    void berserkerImmovableRetainsRemainingBlockForNextTurn() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                attackerEnemy(8),
                List.of(CardFactory.berserkerImmovable()),
                false
        );

        assertTrue(engine.playCard(0));
        engine.endTurn();

        assertEquals(22, state.getPlayer().getBlock());
        assertEquals(40, state.getPlayer().getHealth());
    }

    @Test
    void advanceGrantsPermanentStrengthAcrossBattles() {
        GameEngine engine = new GameEngine(new Random(1));
        Player player = testPlayer();
        BattleState firstBattle = engine.startBattle(
                player,
                passiveEnemy(30),
                List.of(CardFactory.advance()),
                false
        );

        assertTrue(engine.playCard(0));
        assertEquals(1, firstBattle.getPlayer().getStrength());

        BattleState secondBattle = engine.startBattle(
                player,
                passiveEnemy(30),
                List.of(CardFactory.emberStrike()),
                false
        );
        assertTrue(engine.playCard(0));

        assertEquals(23, secondBattle.getEnemy().getHealth());
    }

    @Test
    void defeatStopsBattleAfterEnemyAttack() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                new Player("测试玩家", 20),
                attackerEnemy(50),
                List.of(CardFactory.emberStrike()),
                false
        );

        engine.endTurn();

        assertEquals(GameStatus.DEFEAT, state.getStatus());
        assertFalse(state.getPlayer().isAlive());
    }

    @Test
    void enemyTurnExecutesPreviewedActionAndReportsVisualEffects() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                new Enemy(
                        "出招训练敌人",
                        30,
                        List.of(enemyAction("精准刺击", 6, 0, 0))
                ),
                List.of(CardFactory.ironCurtain()),
                false
        );
        EnemyIntent intent = state.getEnemyIntent();
        assertNotNull(intent);
        assertEquals("精准刺击", intent.getName());
        assertEquals(6, intent.getDamage());

        List<Set<CardVisualEffect>> visualEffects = engine.endTurn();

        assertEquals(2, state.getTurnNumber());
        assertEquals(34, state.getPlayer().getHealth());
        assertEquals(0, state.getEnemy().getHand().size());
        assertEquals(0, state.getEnemy().getEnergy());
        assertEquals(1, visualEffects.size());
        assertFalse(visualEffects.get(0).isEmpty());
    }

    @Test
    void enemyAiFollowsFixedActionPattern() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                new Enemy(
                        "循环训练敌人",
                        30,
                        List.of(
                                enemyAction("第一击", 4, 0, 0),
                                enemyAction("第二步防御", 0, 4, 0)
                        )
                ),
                List.of(CardFactory.ironCurtain()),
                false
        );

        assertEquals("第一击", state.getEnemyIntent().getName());
        engine.endTurn();
        assertEquals("第二步防御", state.getEnemyIntent().getName());
        engine.endTurn();
        assertEquals("第一击", state.getEnemyIntent().getName());
    }

    @Test
    void enemyIntentMatchesActionResolvedOnEnemyTurn() {
        GameEngine engine = new GameEngine(new Random(2));
        BattleState state = engine.startBattle(
                testPlayer(),
                new Enemy(
                        "意图训练敌人",
                        30,
                        List.of(enemyAction("预告重击", 7, 0, 0))
                ),
                List.of(CardFactory.ironCurtain()),
                false
        );

        EnemyIntent intent = state.getEnemyIntent();
        assertNotNull(intent);
        assertEquals(7, intent.getDamage());

        engine.endTurn();

        assertEquals(33, state.getPlayer().getHealth());
        assertTrue(state.getBattleLog().stream().anyMatch(line -> line.contains("执行【预告重击】")));
    }

    @Test
    void enemyAiAdvancesOneActionPerEnemyTurn() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                new Enemy(
                        "单步训练敌人",
                        30,
                        List.of(
                                enemyAction("行动一", 3, 0, 0),
                                enemyAction("行动二", 0, 5, 0)
                        )
                ),
                List.of(CardFactory.ironCurtain()),
                false
        );

        List<Set<CardVisualEffect>> visualEffects = engine.endTurn();

        assertEquals(1, visualEffects.size());
        assertEquals(0, state.getEnemy().getHand().size());
        assertEquals("行动二", state.getEnemyIntent().getName());
    }

    @Test
    void enemyActionRunsWithoutEnemyEnergy() {
        GameEngine engine = new GameEngine(new Random(1));
        BattleState state = engine.startBattle(
                testPlayer(),
                new Enemy(
                        "无能量训练敌人",
                        30,
                        List.of(enemyAction("固定强攻", 12, 0, 0))
                ),
                List.of(CardFactory.ironCurtain()),
                false
        );

        engine.endTurn();

        assertEquals(28, state.getPlayer().getHealth());
        assertEquals(0, state.getEnemy().getEnergy());
    }

    @Test
    void eliteEnemiesUseStrongerActionPatternsThanNormalEnemies() {
        Enemy normal = EnemyFactory.createFirstEnemy();
        Enemy elite = EnemyFactory.createEliteEnemy(new Random(1));
        Enemy boss = EnemyFactory.createBossEnemy();

        assertTrue(maxDamage(elite) > maxDamage(normal));
        assertTrue(maxDamage(boss) > maxDamage(elite));
        assertTrue(boss.getActionPattern().size() > normal.getActionPattern().size());
    }

    @Test
    void playerAndEnemyUseSameCombatantTemplate() {
        assertEquals(Combatant.class, Player.class.getSuperclass());
        assertEquals(Combatant.class, Enemy.class.getSuperclass());
    }

    private Player testPlayer() {
        return new Player("测试玩家", 40);
    }

    private Enemy passiveEnemy(int health) {
        return new Enemy("木桩", health, List.of(enemyAction("观察", 0, 0, 0)));
    }

    private Enemy attackerEnemy(int damage) {
        return new Enemy("训练敌人", 30, List.of(enemyAction("测试攻击", damage, 0, 0)));
    }

    private EnemyAction enemyAction(String name, int damage, int block, int healing) {
        List<String> parts = new ArrayList<>();
        if (damage > 0) {
            parts.add("造成 " + damage + " 点伤害");
        }
        if (block > 0) {
            parts.add("获得 " + block + " 点格挡");
        }
        if (healing > 0) {
            parts.add("恢复 " + healing + " 点生命");
        }
        String description = parts.isEmpty() ? "等待。" : String.join("，", parts) + "。";
        return new EnemyAction(name, description, damage, block, healing);
    }

    private int maxDamage(Enemy enemy) {
        return enemy.getActionPattern().stream()
                .map(EnemyAction::toIntent)
                .mapToInt(EnemyIntent::getDamage)
                .max()
                .orElse(0);
    }

    private static final class FixedRandom extends Random {
        private final int[] values;
        private int index;

        private FixedRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            if (index >= values.length) {
                return 0;
            }
            int value = values[index++];
            if (value < 0 || value >= bound) {
                throw new IllegalArgumentException("Fixed random value " + value + " outside bound " + bound);
            }
            return value;
        }
    }

    private List<Card> starterLikeDeck() {
        List<Card> deck = new ArrayList<>();
        deck.add(CardFactory.emberStrike());
        deck.add(CardFactory.emberStrike());
        deck.add(CardFactory.emberStrike());
        deck.add(CardFactory.ironCurtain());
        deck.add(CardFactory.ironCurtain());
        deck.add(CardFactory.emberStrike());
        deck.add(CardFactory.ironCurtain());
        deck.add(CardFactory.swiftStep());
        deck.add(CardFactory.emberStrike());
        deck.add(CardFactory.ironCurtain());
        return deck;
    }
}
