package com.course.slay.domain.card;

import java.util.ArrayList;
import java.util.List;

public final class StarterDeckFactory {
    private StarterDeckFactory() {
    }

    public static List<Card> createStarterDeck() {
        return createBerserkerStarterDeck();
    }

    public static List<Card> createNightWatchStarterDeck() {
        return createBerserkerStarterDeck();
    }

    public static List<Card> createBerserkerStarterDeck() {
        List<Card> deck = new ArrayList<>();
        deck.add(CardFactory.berserkerStrike());
        deck.add(CardFactory.berserkerDefend());
        deck.add(CardFactory.berserkerComboPunch());
        deck.add(CardFactory.berserkerPerfectStrike());
        deck.add(CardFactory.berserkerBodySlam());
        deck.add(CardFactory.berserkerShelter());
        deck.add(CardFactory.berserkerEnrage());
        deck.add(CardFactory.berserkerBash());
        return deck;
    }

    public static List<Card> createLegacyNightWatchStarterDeck() {
        return createSharedStarterDeck();
    }

    public static List<Card> createCinderSeekerStarterDeck() {
        return createAssassinStarterDeck();
    }

    public static List<Card> createAssassinStarterDeck() {
        List<Card> deck = new ArrayList<>();
        deck.add(CardFactory.assassinStrike());
        deck.add(CardFactory.assassinDefend());
        deck.add(CardFactory.assassinComboPunch());
        deck.add(CardFactory.assassinPerfectStrike());
        deck.add(CardFactory.assassinBodySlam());
        deck.add(CardFactory.assassinEntrench());
        deck.add(CardFactory.assassinBash());
        deck.add(CardFactory.assassinBladeDance());
        return deck;
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
