package com.course.slay.domain.card;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StarterDeckFactoryTest {
    @Test
    void createsCompactStarterDeck() {
        List<Card> deck = StarterDeckFactory.createStarterDeck();

        assertEquals(12, deck.size());
        assertEquals(5, deck.stream().filter(card -> card.getId().equals("ember_strike")).count());
        assertEquals(4, deck.stream().filter(card -> card.getId().equals("iron_curtain")).count());
        assertEquals(2, deck.stream().filter(card -> card.getId().equals("swift_step")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("deep_breath")).count());
        assertTrue(deck.stream().allMatch(card -> card.getCost() >= 0));
        assertFalse(deck.stream().anyMatch(card -> card.getName().isBlank()));
    }

    @Test
    void allCurrentCardsExposeExplicitSingleUpgrade() {
        List<Card> cards = List.of(
                CardFactory.emberStrike(),
                CardFactory.ironCurtain(),
                CardFactory.swiftStep(),
                CardFactory.dawnBreaker(),
                CardFactory.nightCounter(),
                CardFactory.emberGuard(),
                CardFactory.ashNeedle(),
                CardFactory.shieldBash(),
                CardFactory.lanternFlare(),
                CardFactory.deepBreath(),
                CardFactory.sparkFocus(),
                CardFactory.fieldDressing(),
                CardFactory.stormOfCinders(),
                CardFactory.sentinelOath(),
                CardFactory.quickRead(),
                CardFactory.emberSurge()
        );

        for (Card card : cards) {
            assertTrue(CardFactory.canUpgrade(card), card.getName());
            Card upgraded = CardFactory.upgradeOf(card);
            assertTrue(upgraded.isUpgraded(), upgraded.getName());
            assertFalse(CardFactory.canUpgrade(upgraded), upgraded.getName());
            assertTrue(upgraded.getName().endsWith("+"), upgraded.getName());
        }

        assertEquals("造成 9 点伤害。", CardFactory.upgradeOf(CardFactory.emberStrike()).getDescription());
        assertEquals("获得 1 点能量，抽 1 张牌。", CardFactory.upgradeOf(CardFactory.sparkFocus()).getDescription());
    }
}
