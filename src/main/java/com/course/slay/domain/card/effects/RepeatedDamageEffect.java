package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.CardVisualEffect;
import com.course.slay.domain.card.EffectContext;

import java.util.Set;

public final class RepeatedDamageEffect implements CardEffect {
    private final int hits;
    private final int damagePerHit;

    public RepeatedDamageEffect(int hits, int damagePerHit) {
        if (hits < 0) {
            throw new IllegalArgumentException("hits must not be negative");
        }
        if (damagePerHit < 0) {
            throw new IllegalArgumentException("damagePerHit must not be negative");
        }
        this.hits = hits;
        this.damagePerHit = damagePerHit;
    }

    @Override
    public void apply(EffectContext context) {
        for (int i = 0; i < hits; i++) {
            context.dealDamageToOpponent(damagePerHit);
        }
    }

    @Override
    public Set<CardVisualEffect> visualEffects() {
        return Set.of(CardVisualEffect.ATTACK);
    }
}
