package com.course.slay.domain.character;

import com.course.slay.domain.card.Card;
import com.course.slay.domain.card.CardFactory;
import com.course.slay.domain.card.CardRarity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CharacterCatalogTest {
    @Test
    void exposesMultiplePlayableCharactersWithDeckAndRewardPools() {
        List<PlayableCharacter> characters = CharacterCatalog.availableCharacters();

        assertTrue(characters.size() >= 2);

        for (PlayableCharacter character : characters) {
            assertFalse(character.id().isBlank());
            assertFalse(character.name().isBlank());
            assertFalse(character.createStarterDeck().isEmpty());
            assertFalse(character.createRewardPool().isEmpty());
        }
    }

    @Test
    void characterDecksAreCreatedAsFreshCardInstances() {
        PlayableCharacter character = CharacterCatalog.defaultCharacter();
        List<Card> firstDeck = character.createStarterDeck();
        List<Card> secondDeck = character.createStarterDeck();

        assertNotSame(firstDeck.get(0), secondDeck.get(0));
    }

    @Test
    void berserkerUsesDesignDocumentStarterAndRewardPools() {
        PlayableCharacter berserker = CharacterCatalog.defaultCharacter();

        assertEquals("狂战士", berserker.name());
        assertEquals(Set.of(
                        "berserker_strike",
                        "berserker_defend",
                        "berserker_combo_punch",
                        "berserker_perfect_strike",
                        "berserker_body_slam",
                        "berserker_shelter",
                        "berserker_enrage",
                        "berserker_bash"
                ),
                berserker.createStarterDeck().stream().map(Card::getId).collect(Collectors.toSet()));
        assertEquals(Set.of(
                        "berserker_bloodletting",
                        "berserker_void",
                        "berserker_pierce",
                        "berserker_tranquility",
                        "berserker_tactical_master",
                        "berserker_silence",
                        "berserker_ash_strike",
                        "berserker_iron_chop",
                        "berserker_immovable"
                ),
                berserker.createRewardPool().stream().map(Card::getId).collect(Collectors.toSet()));
        assertTrue(berserker.createRewardPool().stream().anyMatch(card -> card.getRarity() == CardRarity.LEGENDARY));
        assertFalse(berserker.createRewardPool().stream().anyMatch(card -> card.getId().equals("ember_strike")));
        assertFalse(berserker.createRewardPool().stream().anyMatch(card -> card.getId().equals("iron_curtain")));
    }

    @Test
    void assassinUsesDesignDocumentStarterAndRewardPools() {
        PlayableCharacter assassin = CharacterCatalog.findById("cinder_seeker").orElseThrow();

        assertEquals("刺客", assassin.name());
        assertEquals(Set.of(
                        "assassin_strike",
                        "assassin_defend",
                        "assassin_combo_punch",
                        "assassin_perfect_strike",
                        "assassin_body_slam",
                        "assassin_entrench",
                        "assassin_bash",
                        "assassin_blade_dance"
                ),
                assassin.createStarterDeck().stream().map(Card::getId).collect(Collectors.toSet()));
        assertEquals(Set.of(
                        "assassin_blade_surge",
                        "assassin_vampiric_strike",
                        "assassin_backstab",
                        "assassin_advance",
                        "assassin_silence",
                        "assassin_tactical_master",
                        "assassin_enrage",
                        "assassin_sacrifice",
                        "assassin_precision",
                        "assassin_infinite_blades",
                        "assassin_finisher"
                ),
                assassin.createRewardPool().stream().map(Card::getId).collect(Collectors.toSet()));
        assertTrue(assassin.createStarterDeck().stream().allMatch(card -> card.getRarity() == CardRarity.COMMON));
        assertTrue(assassin.createRewardPool().stream().anyMatch(card -> card.getRarity() == CardRarity.LEGENDARY));
        assertEquals(
                assassin.createRewardPool().stream().map(Card::getId).collect(Collectors.toSet()),
                CardFactory.rewardPool().stream().map(Card::getId).collect(Collectors.toSet())
        );
        assertFalse(assassin.createRewardPool().stream().anyMatch(card -> card.getId().equals("assassin_blade_dance")));
        assertFalse(assassin.createStarterDeck().stream().anyMatch(card -> card.getId().equals("assassin_blade_surge")));
        assertFalse(assassin.createStarterDeck().stream().anyMatch(card -> card.getId().equals("ember_strike")));
        assertFalse(assassin.createRewardPool().stream().anyMatch(card -> card.getId().equals("ember_strike")));
    }
}
