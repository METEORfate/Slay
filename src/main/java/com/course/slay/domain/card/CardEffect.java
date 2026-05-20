package com.course.slay.domain.card;

import java.util.Set;

@FunctionalInterface
public interface CardEffect {
    void apply(EffectContext context);

    default Set<CardVisualEffect> visualEffects() {
        return Set.of();
    }
}
