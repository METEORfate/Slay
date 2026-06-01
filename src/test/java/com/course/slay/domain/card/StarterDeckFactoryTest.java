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

        assertEquals(8, deck.size());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("berserker_strike")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("berserker_defend")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("berserker_combo_punch")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("berserker_perfect_strike")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("berserker_body_slam")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("berserker_shelter")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("berserker_enrage")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("berserker_bash")).count());
        assertFalse(deck.stream().anyMatch(card -> card.getId().equals("ember_strike")));
        assertFalse(deck.stream().anyMatch(card -> card.getId().equals("iron_curtain")));
        assertTrue(deck.stream().allMatch(card -> card.getCost() >= 0));
        assertFalse(deck.stream().anyMatch(card -> card.getName().isBlank()));
    }

    @Test
    void createsAssassinStarterDeckFromDesignDocument() {
        List<Card> deck = StarterDeckFactory.createAssassinStarterDeck();

        assertEquals(8, deck.size());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("assassin_strike")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("assassin_defend")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("assassin_combo_punch")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("assassin_perfect_strike")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("assassin_body_slam")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("assassin_entrench")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("assassin_bash")).count());
        assertEquals(1, deck.stream().filter(card -> card.getId().equals("assassin_blade_dance")).count());
        assertFalse(deck.stream().anyMatch(card -> card.getId().equals("ember_strike")));
        assertFalse(deck.stream().anyMatch(card -> card.getId().equals("iron_curtain")));
        assertTrue(deck.stream().allMatch(card -> card.getRarity() == CardRarity.COMMON));
    }

    @Test
    void allCurrentCardsExposeExplicitSingleUpgrade() {
        List<Card> cards = List.of(
                CardFactory.berserkerStrike(),
                CardFactory.berserkerDefend(),
                CardFactory.berserkerComboPunch(),
                CardFactory.berserkerPerfectStrike(),
                CardFactory.berserkerBodySlam(),
                CardFactory.berserkerShelter(),
                CardFactory.berserkerEnrage(),
                CardFactory.berserkerBash(),
                CardFactory.berserkerBloodletting(),
                CardFactory.berserkerVoid(),
                CardFactory.berserkerPierce(),
                CardFactory.berserkerTranquility(),
                CardFactory.berserkerTacticalMaster(),
                CardFactory.berserkerSilence(),
                CardFactory.berserkerAshStrike(),
                CardFactory.berserkerIronChop(),
                CardFactory.berserkerImmovable(),
                CardFactory.assassinStrike(),
                CardFactory.assassinDefend(),
                CardFactory.assassinComboPunch(),
                CardFactory.assassinPerfectStrike(),
                CardFactory.assassinBodySlam(),
                CardFactory.assassinEntrench(),
                CardFactory.assassinBash(),
                CardFactory.assassinBladeDance(),
                CardFactory.assassinBladeSurge(),
                CardFactory.assassinVampiricStrike(),
                CardFactory.assassinBackstab(),
                CardFactory.assassinAdvance(),
                CardFactory.assassinSilence(),
                CardFactory.assassinTacticalMaster(),
                CardFactory.assassinEnrage(),
                CardFactory.assassinSacrifice(),
                CardFactory.assassinPrecision(),
                CardFactory.assassinInfiniteBlades(),
                CardFactory.assassinFinisher(),
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
                CardFactory.emberSurge(),
                CardFactory.bloodletting(),
                CardFactory.comboPunch(),
                CardFactory.perfectStrike(),
                CardFactory.tacticalMaster(),
                CardFactory.silence(),
                CardFactory.bodySlam(),
                CardFactory.entrench(),
                CardFactory.enrage(),
                CardFactory.bladeSurge(),
                CardFactory.vampiricStrike(),
                CardFactory.backstab(),
                CardFactory.advance()
        );

        for (Card card : cards) {
            assertTrue(CardFactory.canUpgrade(card), card.getName());
            Card upgraded = CardFactory.upgradeOf(card);
            assertTrue(upgraded.isUpgraded(), upgraded.getName());
            assertFalse(CardFactory.canUpgrade(upgraded), upgraded.getName());
            assertTrue(upgraded.getName().endsWith("+"), upgraded.getName());
            assertEquals(card.getRarity(), upgraded.getRarity(), card.getName());
        }

        assertEquals("造成 9 点伤害。", CardFactory.upgradeOf(CardFactory.emberStrike()).getDescription());
        assertEquals("造成 9 点伤害。", CardFactory.upgradeOf(CardFactory.berserkerStrike()).getDescription());
        assertEquals("获得 1 点能量，抽 1 张牌。", CardFactory.upgradeOf(CardFactory.sparkFocus()).getDescription());
        assertEquals("跳过下一次敌方回合。", CardFactory.upgradeOf(CardFactory.berserkerSilence()).getDescription());
        assertEquals(2, CardFactory.silence().getCost());
        assertEquals(3, CardFactory.berserkerSilence().getCost());
        assertFalse(CardFactory.canUpgrade(CardFactory.assassinKnife()));
    }

    @Test
    void cardsExposeFrontEndRarityTemplates() {
        assertEquals(CardRarity.COMMON, CardFactory.berserkerStrike().getRarity());
        assertEquals(CardRarity.COMMON, CardFactory.berserkerBloodletting().getRarity());
        assertEquals(CardRarity.RARE, CardFactory.berserkerTacticalMaster().getRarity());
        assertEquals(CardRarity.RARE, CardFactory.berserkerAshStrike().getRarity());
        assertEquals(CardRarity.LEGENDARY, CardFactory.berserkerSilence().getRarity());
        assertEquals(CardRarity.LEGENDARY, CardFactory.berserkerImmovable().getRarity());
        assertEquals(CardRarity.COMMON, CardFactory.assassinStrike().getRarity());
        assertEquals(CardRarity.COMMON, CardFactory.assassinBladeDance().getRarity());
        assertEquals(CardRarity.RARE, CardFactory.assassinBackstab().getRarity());
        assertEquals(CardRarity.RARE, CardFactory.assassinPrecision().getRarity());
        assertEquals(CardRarity.LEGENDARY, CardFactory.assassinSilence().getRarity());
        assertEquals(CardRarity.LEGENDARY, CardFactory.assassinInfiniteBlades().getRarity());
        assertEquals(CardRarity.SPECIAL, CardFactory.assassinKnife().getRarity());
        assertEquals(CardRarity.COMMON, CardFactory.emberStrike().getRarity());
        assertEquals(CardRarity.COMMON, CardFactory.bloodletting().getRarity());
        assertEquals(CardRarity.COMMON, CardFactory.bladeSurge().getRarity());
        assertEquals(CardRarity.UNCOMMON, CardFactory.dawnBreaker().getRarity());
        assertEquals(CardRarity.UNCOMMON, CardFactory.tacticalMaster().getRarity());
        assertEquals(CardRarity.UNCOMMON, CardFactory.enrage().getRarity());
        assertEquals(CardRarity.RARE, CardFactory.stormOfCinders().getRarity());
        assertEquals(CardRarity.RARE, CardFactory.silence().getRarity());
    }
}
