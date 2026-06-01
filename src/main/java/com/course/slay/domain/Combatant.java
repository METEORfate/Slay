package com.course.slay.domain;

import com.course.slay.domain.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Combatant {
    private final String name;
    private final int maxHealth;
    private final int maxEnergy;
    private final int handSize;
    private final List<Card> deck;
    private final List<Card> drawPile;
    private final List<Card> hand;
    private final List<Card> discardPile;
    private int health;
    private int block;
    private int energy;
    private int temporaryStrength;
    private int permanentStrength;
    private int vulnerable;
    private boolean limitNextDamageToOne;

    public Combatant(String name, int maxHealth, List<Card> deck, int maxEnergy, int handSize) {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("maxHealth must be positive");
        }
        if (maxEnergy < 0) {
            throw new IllegalArgumentException("maxEnergy must not be negative");
        }
        if (handSize < 0) {
            throw new IllegalArgumentException("handSize must not be negative");
        }
        this.name = Objects.requireNonNull(name, "name");
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.maxEnergy = maxEnergy;
        this.handSize = handSize;
        this.deck = new ArrayList<>(Objects.requireNonNull(deck, "deck"));
        this.drawPile = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.discardPile = new ArrayList<>();
    }

    public void setDeck(List<Card> deck) {
        this.deck.clear();
        this.deck.addAll(Objects.requireNonNull(deck, "deck"));
    }

    public void prepareDeck(Random random, boolean shuffleDeck) {
        Objects.requireNonNull(random, "random");
        drawPile.clear();
        drawPile.addAll(deck);
        if (shuffleDeck) {
            Collections.shuffle(drawPile, random);
        }
        hand.clear();
        discardPile.clear();
        energy = 0;
        temporaryStrength = 0;
        vulnerable = 0;
        limitNextDamageToOne = false;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getBlock() {
        return block;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public List<Card> getDrawPile() {
        return drawPile;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getEnergy() {
        return energy;
    }

    public int getStrength() {
        return temporaryStrength + permanentStrength;
    }

    public int getVulnerable() {
        return vulnerable;
    }

    public void setEnergy(int energy) {
        if (energy < 0) {
            throw new IllegalArgumentException("energy must not be negative");
        }
        this.energy = energy;
    }

    public void spendEnergy(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        if (amount > energy) {
            throw new IllegalArgumentException("not enough energy");
        }
        energy -= amount;
    }

    public int getHandSize() {
        return handSize;
    }

    public void gainBlock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        block += amount;
    }

    public void gainStrength(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        temporaryStrength += amount;
    }

    public void gainPermanentStrength(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        permanentStrength += amount;
    }

    public void addVulnerable(int stacks) {
        if (stacks < 0) {
            throw new IllegalArgumentException("stacks must not be negative");
        }
        vulnerable += stacks;
    }

    public void limitNextDamageToOne() {
        limitNextDamageToOne = true;
    }

    public int takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        int adjustedAmount = adjustedIncomingDamage(amount);
        int blocked = Math.min(block, adjustedAmount);
        block -= blocked;
        int healthDamage = adjustedAmount - blocked;
        health = Math.max(0, health - healthDamage);
        return healthDamage;
    }

    public int takeDamageIgnoringBlock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        int healthDamage = adjustedIncomingDamage(amount);
        health = Math.max(0, health - healthDamage);
        return healthDamage;
    }

    public int loseHealth(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        int before = health;
        health = Math.max(0, health - amount);
        return before - health;
    }

    public int heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        int before = health;
        health = Math.min(maxHealth, health + amount);
        return health - before;
    }

    public void resetBlock() {
        block = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }

    private int adjustedIncomingDamage(int amount) {
        if (amount <= 0) {
            return 0;
        }

        int adjustedAmount = amount;
        if (vulnerable > 0) {
            adjustedAmount = (int) Math.ceil(adjustedAmount * 1.5);
            vulnerable--;
        }
        if (limitNextDamageToOne) {
            adjustedAmount = Math.min(adjustedAmount, 1);
            limitNextDamageToOne = false;
        }
        return adjustedAmount;
    }
}
