package com.course.slay.domain;

import com.course.slay.domain.card.Card;

import java.util.List;

public class Player extends Combatant {
    public Player(String name, int maxHealth, List<Card> deck, int maxEnergy, int handSize) {
        super(name, maxHealth, deck, maxEnergy, handSize);
    }

    public Player(String name, int maxHealth) {
        this(name, maxHealth, List.of(), 3, 5);
    }
}
