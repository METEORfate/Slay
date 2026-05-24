package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.EffectContext;

public final class HealthLossEffect implements CardEffect {
    private final int amount;

    public HealthLossEffect(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        this.amount = amount;
    }

    @Override
    public void apply(EffectContext context) {
        context.loseHealth(amount);
    }
}
