package com.course.slay.engine;

import com.course.slay.domain.BattleState;
import com.course.slay.domain.GameStatus;
import com.course.slay.domain.card.Card;
import com.course.slay.domain.card.CardFactory;
import com.course.slay.domain.character.PlayableCharacter;
import com.course.slay.domain.run.MapNode;
import com.course.slay.domain.run.MapNodeType;
import com.course.slay.domain.run.RunPhase;
import com.course.slay.domain.run.RunState;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RunFlowTest {
    @Test
    void newRunStartsOnRandomLongMapWithMultipleEntranceChoices() {
        GameEngine engine = new GameEngine(new Random(1));
        RunState run = engine.startNewRun();

        assertEquals("守夜人", run.getPlayableCharacter().name());
        assertEquals(RunPhase.MAP, run.getPhase());
        assertEquals(12, run.getDeck().size());
        assertEquals(4, run.getMap().getNodesByFloor(1).stream().filter(MapNode::isAvailable).count());
        assertEquals(14, run.getMap().getFloors().size());
        assertTrue(run.getMap().getNodes().stream().anyMatch(node -> node.getType() == MapNodeType.EVENT));
        assertTrue(run.getMap().getNodes().stream().anyMatch(node -> node.getType() == MapNodeType.SHOP));
        assertTrue(run.getMap().getNodes().stream().anyMatch(node -> node.getType() == MapNodeType.REST));
        assertTrue(run.getMap().getNodes().stream().anyMatch(node -> node.getType() == MapNodeType.BOSS));
    }

    @Test
    void selectedCharacterControlsStarterDeckAndRewardPool() {
        GameEngine engine = new GameEngine(new Random(1));
        PlayableCharacter testCharacter = new PlayableCharacter(
                "test_role",
                "测试角色",
                "测试定位",
                "用于验证角色配置会驱动远征牌组。",
                40,
                3,
                5,
                () -> List.of(CardFactory.emberStrike()),
                () -> List.of(CardFactory.fieldDressing())
        );

        RunState run = engine.startNewRun(testCharacter);

        assertEquals("测试角色", run.getPlayer().getName());
        assertEquals("test_role", run.getPlayableCharacter().id());
        assertEquals(1, run.getDeck().size());
        assertEquals("ember_strike", run.getDeck().get(0).getId());

        MapNode selected = firstAvailable(run);
        assertTrue(engine.selectMapNode(selected.getId()));
        defeatCurrentEnemy(engine);

        assertEquals(RunPhase.REWARD, run.getPhase());
        assertEquals(1, engine.getState().getRewardChoices().size());
        assertEquals("field_dressing", engine.getState().getRewardChoices().get(0).getId());
    }

    @Test
    void combatRewardReturnsToMapAndUnlocksConnectedRoute() {
        GameEngine engine = new GameEngine(new Random(1));
        RunState run = engine.startNewRun();
        MapNode selected = firstAvailable(run);

        assertTrue(engine.selectMapNode(selected.getId()));
        assertEquals(RunPhase.BATTLE, run.getPhase());

        defeatCurrentEnemy(engine);
        assertEquals(RunPhase.REWARD, run.getPhase());
        assertEquals(GameStatus.VICTORY, engine.getState().getStatus());
        assertEquals(GameEngine.NORMAL_COMBAT_GOLD, run.getGold());

        assertTrue(engine.claimReward(0));
        assertEquals(RunPhase.MAP, run.getPhase());
        assertNull(run.getCurrentNode());
        assertTrue(selected.isCompleted());
        for (String nextId : selected.getNextNodeIds()) {
            assertTrue(run.getMap().findNode(nextId).orElseThrow().isAvailable());
        }
        assertEquals(13, run.getDeck().size());
    }

    @Test
    void restNodeHealsAndContinuesRouteWithoutBattle() {
        GameEngine engine = new GameEngine(new Random(1));
        RunState run = engine.startNewRun();

        MapNode rest = advanceUntilAvailableType(engine, run, MapNodeType.REST);
        run.getPlayer().takeDamage(20);
        int before = run.getPlayer().getHealth();

        assertTrue(engine.selectMapNode(rest.getId()));

        assertEquals(RunPhase.REST_SITE, run.getPhase());
        assertTrue(engine.restAtCamp());
        assertEquals(RunPhase.MAP, run.getPhase());
        assertTrue(run.getPlayer().getHealth() > before);
        assertTrue(rest.isCompleted());
    }

    @Test
    void skipRewardLeavesDeckUnchangedAndGrantsGold() {
        GameEngine engine = new GameEngine(new Random(1));
        RunState run = engine.startNewRun();
        MapNode selected = firstAvailable(run);

        assertTrue(engine.selectMapNode(selected.getId()));
        defeatCurrentEnemy(engine);

        int deckSize = run.getDeck().size();
        assertEquals(GameEngine.NORMAL_COMBAT_GOLD, run.getGold());

        assertTrue(engine.skipReward());

        assertEquals(RunPhase.MAP, run.getPhase());
        assertEquals(deckSize, run.getDeck().size());
        assertEquals(GameEngine.NORMAL_COMBAT_GOLD + GameEngine.SKIP_REWARD_GOLD, run.getGold());
        assertTrue(selected.isCompleted());
    }

    @Test
    void eliteCombatGrantsLargerGoldReward() {
        GameEngine engine = new GameEngine(new Random(1));
        RunState run = engine.startNewRun();

        MapNode elite = advanceUntilAvailableType(engine, run, MapNodeType.ELITE);
        int beforeGold = run.getGold();

        assertTrue(engine.selectMapNode(elite.getId()));
        defeatCurrentEnemy(engine);

        assertEquals(RunPhase.REWARD, run.getPhase());
        assertEquals(beforeGold + GameEngine.ELITE_COMBAT_GOLD, run.getGold());
    }

    @Test
    void restSiteCanUpgradeOneCardAndCannotUpgradeItAgain() {
        GameEngine engine = new GameEngine(new Random(1));
        RunState run = engine.startNewRun();

        MapNode rest = advanceUntilAvailableType(engine, run, MapNodeType.REST);
        Card before = run.getDeck().get(0);

        assertTrue(engine.selectMapNode(rest.getId()));
        assertEquals(RunPhase.REST_SITE, run.getPhase());
        assertTrue(engine.upgradeCardAtCamp(0));

        Card upgraded = run.getDeck().get(0);
        assertEquals(RunPhase.MAP, run.getPhase());
        assertEquals(before.getName() + "+", upgraded.getName());
        assertTrue(upgraded.isUpgraded());
        assertTrue(rest.isCompleted());

        assertEquals(false, CardFactory.canUpgrade(upgraded));
    }

    @Test
    void shopCanBuyCardsRemoveCardsAndLeave() {
        GameEngine engine = new GameEngine(new Random(1));
        RunState run = engine.startNewRun();

        MapNode shop = advanceUntilAvailableType(engine, run, MapNodeType.SHOP);
        int baseGold = run.getGold();
        run.addGold(100);

        assertTrue(engine.selectMapNode(shop.getId()));
        assertEquals(RunPhase.SHOP, run.getPhase());
        assertEquals(3, run.getShopCards().size());

        int beforeDeck = run.getDeck().size();
        assertTrue(engine.buyShopCard(0));
        assertEquals(beforeDeck + 1, run.getDeck().size());
        assertEquals(2, run.getShopCards().size());
        assertEquals(baseGold + 65, run.getGold());

        assertTrue(engine.removeDeckCardAtShop(0));
        assertEquals(beforeDeck, run.getDeck().size());
        assertEquals(baseGold + 15, run.getGold());

        assertTrue(engine.leaveShop());
        assertEquals(RunPhase.MAP, run.getPhase());
        assertTrue(run.getShopCards().isEmpty());
        assertTrue(shop.isCompleted());
    }

    @Test
    void shopRejectsPurchasesAndRemovalWithoutEnoughGold() {
        GameEngine engine = new GameEngine(new Random(1));
        RunState run = engine.startNewRun();

        MapNode shop = advanceUntilAvailableType(engine, run, MapNodeType.SHOP);
        run.spendGold(run.getGold());
        assertTrue(engine.selectMapNode(shop.getId()));

        int deckSize = run.getDeck().size();
        assertEquals(0, run.getGold());
        assertEquals(false, engine.buyShopCard(0));
        assertEquals(false, engine.removeDeckCardAtShop(0));
        assertEquals(deckSize, run.getDeck().size());
        assertEquals(3, run.getShopCards().size());
    }

    @Test
    void shopCannotRemoveTheLastDeckCard() {
        GameEngine engine = new GameEngine(new Random(1));
        PlayableCharacter testCharacter = new PlayableCharacter(
                "single_card",
                "单牌角色",
                "测试定位",
                "用于验证商店不会清空牌组。",
                40,
                3,
                5,
                () -> List.of(CardFactory.emberStrike()),
                () -> List.of(CardFactory.fieldDressing())
        );
        RunState run = engine.startNewRun(testCharacter);

        MapNode shop = advanceUntilAvailableType(engine, run, MapNodeType.SHOP);
        run.getDeck().clear();
        run.getDeck().add(CardFactory.emberStrike());
        int beforeGold = run.getGold();
        run.addGold(GameEngine.SHOP_REMOVE_CARD_PRICE);

        assertTrue(engine.selectMapNode(shop.getId()));

        assertEquals(false, engine.removeDeckCardAtShop(0));
        assertEquals(1, run.getDeck().size());
        assertEquals(beforeGold + GameEngine.SHOP_REMOVE_CARD_PRICE, run.getGold());
    }

    @Test
    void defeatingBossCompletesRun() {
        GameEngine engine = new GameEngine(new Random(1));
        RunState run = engine.startNewRun();

        MapNode boss = advanceUntilAvailableType(engine, run, MapNodeType.BOSS);
        assertTrue(engine.selectMapNode(boss.getId()));
        defeatCurrentEnemy(engine);

        assertEquals(RunPhase.RUN_VICTORY, run.getPhase());
        assertEquals(GameStatus.VICTORY, engine.getState().getStatus());
        assertTrue(boss.isCompleted());
    }

    private MapNode advanceUntilAvailableType(GameEngine engine, RunState run, MapNodeType type) {
        for (int i = 0; i < 40; i++) {
            MapNode target = run.getMap().getNodes().stream()
                    .filter(MapNode::isAvailable)
                    .filter(node -> node.getType() == type)
                    .findFirst()
                    .orElse(null);
            if (target != null) {
                return target;
            }
            MapNode next = firstAvailable(run);
            assertTrue(engine.selectMapNode(next.getId()));
            resolveCurrentNode(engine, run);
            assertEquals(RunPhase.MAP, run.getPhase());
        }
        throw new AssertionError("No available node of type " + type);
    }

    private MapNode firstAvailable(RunState run) {
        return run.getMap().getNodes().stream()
                .filter(MapNode::isAvailable)
                .findFirst()
                .orElseThrow();
    }

    private void resolveCurrentNode(GameEngine engine, RunState run) {
        if (run.getPhase() == RunPhase.BATTLE) {
            defeatCurrentEnemy(engine);
        }
        if (run.getPhase() == RunPhase.REWARD) {
            assertTrue(engine.claimReward(0));
        }
        if (run.getPhase() == RunPhase.REST_SITE) {
            assertTrue(engine.restAtCamp());
        }
        if (run.getPhase() == RunPhase.SHOP) {
            assertTrue(engine.leaveShop());
        }
    }

    private void defeatCurrentEnemy(GameEngine engine) {
        BattleState state = engine.getState();
        state.getHand().clear();
        for (int i = 0; i < 12; i++) {
            state.getHand().add(CardFactory.dawnBreaker());
        }
        state.setEnergy(99);
        while (state.getStatus() == GameStatus.IN_PROGRESS) {
            engine.playCard(0);
        }
    }
}
