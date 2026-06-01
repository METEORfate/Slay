package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.EffectContext;

public final class VulnerableEffect implements CardEffect {
    private final int stacks;

    public VulnerableEffect(int stacks) {
        if (stacks < 0) {
            throw new IllegalArgumentException("stacks must not be negative");
        }
        this.stacks = stacks;
    }

    @Override
    public void apply(EffectContext context) {
        context.addVulnerableToOpponent(stacks);
    }
}
