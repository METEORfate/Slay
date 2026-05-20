package com.course.slay.domain.character;

import com.course.slay.domain.Player;
import com.course.slay.domain.card.Card;
import com.course.slay.domain.card.CardFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public record PlayableCharacter(
        String id,
        String name,
        String archetype,
        String description,
        int maxHealth,
        int maxEnergy,
        int handSize,
        Supplier<List<Card>> starterDeckSupplier,
        Supplier<List<Card>> rewardPoolSupplier
) {
    public PlayableCharacter {
        id = requireText(id, "id");
        name = requireText(name, "name");
        archetype = requireText(archetype, "archetype");
        description = requireText(description, "description");
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("maxHealth must be positive");
        }
        if (maxEnergy < 0) {
            throw new IllegalArgumentException("maxEnergy must not be negative");
        }
        if (handSize < 0) {
            throw new IllegalArgumentException("handSize must not be negative");
        }
        starterDeckSupplier = Objects.requireNonNull(starterDeckSupplier, "starterDeckSupplier");
        rewardPoolSupplier = Objects.requireNonNull(rewardPoolSupplier, "rewardPoolSupplier");
    }

    public Player createPlayer() {
        return new Player(name, maxHealth, List.of(), maxEnergy, handSize);
    }

    public List<Card> createStarterDeck() {
        return copyDeck(starterDeckSupplier.get(), "starter deck");
    }

    public List<Card> createRewardPool() {
        return copyDeck(rewardPoolSupplier.get(), "reward pool");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    private static List<Card> copyDeck(List<Card> cards, String name) {
        Objects.requireNonNull(cards, name);
        if (cards.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return cards.stream()
                .map(CardFactory::copyOf)
                .toList();
    }
}
