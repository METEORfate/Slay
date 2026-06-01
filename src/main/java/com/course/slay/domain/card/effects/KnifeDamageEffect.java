package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.CardVisualEffect;
import com.course.slay.domain.card.EffectContext;

import java.util.Set;

public final class KnifeDamageEffect implements CardEffect {
    private final int baseDamage;

    public KnifeDamageEffect(int baseDamage) {
        if (baseDamage < 0) {
            throw new IllegalArgumentException("baseDamage must not be negative");
        }
        this.baseDamage = baseDamage;
    }

    @Override
    public void apply(EffectContext context) {
        context.dealDamageToOpponent(context.knifeDamage(baseDamage));
    }

    @Override
    public Set<CardVisualEffect> visualEffects() {
        return Set.of(CardVisualEffect.ATTACK);
    }
}
