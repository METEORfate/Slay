package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.EffectContext;

public final class StrengthEffect implements CardEffect {
    private final int amount;
    private final boolean permanent;

    public StrengthEffect(int amount, boolean permanent) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        this.amount = amount;
        this.permanent = permanent;
    }

    @Override
    public void apply(EffectContext context) {
        if (permanent) {
            context.gainPermanentStrength(amount);
            return;
        }
        context.gainStrength(amount);
    }
}
