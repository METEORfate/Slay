package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.CardVisualEffect;
import com.course.slay.domain.card.EffectContext;

import java.util.Set;

public final class ScaleCurrentBlockEffect implements CardEffect {
    private final int extraCopies;

    public ScaleCurrentBlockEffect(int extraCopies) {
        if (extraCopies < 0) {
            throw new IllegalArgumentException("extraCopies must not be negative");
        }
        this.extraCopies = extraCopies;
    }

    @Override
    public void apply(EffectContext context) {
        context.gainBlock(context.currentBlock() * extraCopies);
    }

    @Override
    public Set<CardVisualEffect> visualEffects() {
        return Set.of(CardVisualEffect.SHIELD);
    }
}
