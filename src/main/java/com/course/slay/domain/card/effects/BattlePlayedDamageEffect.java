package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.CardVisualEffect;
import com.course.slay.domain.card.EffectContext;

import java.util.Set;

public final class BattlePlayedDamageEffect implements CardEffect {
    private final int baseDamage;
    private final int damagePerPlayedCard;

    public BattlePlayedDamageEffect(int baseDamage, int damagePerPlayedCard) {
        if (baseDamage < 0) {
            throw new IllegalArgumentException("baseDamage must not be negative");
        }
        if (damagePerPlayedCard < 0) {
            throw new IllegalArgumentException("damagePerPlayedCard must not be negative");
        }
        this.baseDamage = baseDamage;
        this.damagePerPlayedCard = damagePerPlayedCard;
    }

    @Override
    public void apply(EffectContext context) {
        context.dealDamageToOpponent(baseDamage + context.cardsPlayedThisBattle() * damagePerPlayedCard);
    }

    @Override
    public Set<CardVisualEffect> visualEffects() {
        return Set.of(CardVisualEffect.ATTACK);
    }
}
