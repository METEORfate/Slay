package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.CardVisualEffect;
import com.course.slay.domain.card.EffectContext;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class CompositeEffect implements CardEffect {
    private final List<CardEffect> effects;

    private CompositeEffect(List<CardEffect> effects) {
        this.effects = List.copyOf(effects);
    }

    public static CompositeEffect of(CardEffect... effects) {
        return new CompositeEffect(Arrays.asList(effects));
    }

    @Override
    public void apply(EffectContext context) {
        effects.forEach(effect -> effect.apply(context));
    }

    @Override
    public Set<CardVisualEffect> visualEffects() {
        Set<CardVisualEffect> visualEffects = new LinkedHashSet<>();
        for (CardEffect effect : effects) {
            visualEffects.addAll(effect.visualEffects());
        }
        return Set.copyOf(visualEffects);
    }
}
