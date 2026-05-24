package com.course.slay.domain.card;

import java.util.Objects;
import java.util.Set;

public final class Card {
    private final String id;
    private final String name;
    private final int cost;
    private final CardType type;
    private final CardRarity rarity;
    private final String description;
    private final CardEffect effect;
    private final boolean upgraded;

    public Card(String id, String name, int cost, CardType type, String description, CardEffect effect) {
        this(id, name, cost, type, description, effect, false);
    }

    public Card(
            String id,
            String name,
            int cost,
            CardType type,
            String description,
            CardEffect effect,
            boolean upgraded
    ) {
        this(id, name, cost, type, CardRarity.COMMON, description, effect, upgraded);
    }

    public Card(
            String id,
            String name,
            int cost,
            CardType type,
            CardRarity rarity,
            String description,
            CardEffect effect,
            boolean upgraded
    ) {
        if (cost < 0) {
            throw new IllegalArgumentException("cost must not be negative");
        }
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.cost = cost;
        this.type = Objects.requireNonNull(type, "type");
        this.rarity = Objects.requireNonNull(rarity, "rarity");
        this.description = Objects.requireNonNull(description, "description");
        this.effect = Objects.requireNonNull(effect, "effect");
        this.upgraded = upgraded;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public CardType getType() {
        return type;
    }

    public CardRarity getRarity() {
        return rarity;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUpgraded() {
        return upgraded;
    }

    public void play(EffectContext context) {
        effect.apply(context);
    }

    public Set<CardVisualEffect> getVisualEffects() {
        return effect.visualEffects();
    }
}
