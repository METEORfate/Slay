package com.course.slay.domain.card;

import java.util.ArrayList;
import java.util.List;

public final class StarterDeckFactory {
    private StarterDeckFactory() {
    }

    public static List<Card> createStarterDeck() {
        return createNightWatchStarterDeck();
    }

    public static List<Card> createNightWatchStarterDeck() {
        return createSharedStarterDeck();
    }

    public static List<Card> createCinderSeekerStarterDeck() {
        return createSharedStarterDeck();
    }

    private static List<Card> createSharedStarterDeck() {
        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            deck.add(CardFactory.emberStrike());
        }
        for (int i = 0; i < 4; i++) {
            deck.add(CardFactory.ironCurtain());
        }
        for (int i = 0; i < 2; i++) {
            deck.add(CardFactory.swiftStep());
        }
        deck.add(CardFactory.deepBreath());
        return deck;
    }
}
