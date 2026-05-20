package com.course.slay.domain.character;

import com.course.slay.domain.card.Card;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}
