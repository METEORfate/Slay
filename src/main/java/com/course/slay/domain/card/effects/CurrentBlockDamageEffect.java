package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.CardVisualEffect;
import com.course.slay.domain.card.EffectContext;

import java.util.Set;

public final class CurrentBlockDamageEffect implements CardEffect {
    @Override
    public void apply(EffectContext context) {
        context.dealDamageToOpponent(context.currentBlock());
    }

    @Override
    public Set<CardVisualEffect> visualEffects() {
        return Set.of(CardVisualEffect.ATTACK);
    }
}
