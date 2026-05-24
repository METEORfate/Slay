package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.CardVisualEffect;
import com.course.slay.domain.card.EffectContext;

import java.util.Set;

public final class AttackPlayedDamageEffect implements CardEffect {
    private final int amount;

    public AttackPlayedDamageEffect(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        this.amount = amount;
    }

    @Override
    public void apply(EffectContext context) {
        context.dealDamageToOpponent(context.hasPlayedAttackThisTurn() ? amount * 2 : amount);
    }

    @Override
    public Set<CardVisualEffect> visualEffects() {
        return Set.of(CardVisualEffect.ATTACK);
    }
}
