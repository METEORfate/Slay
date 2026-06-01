package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.EffectContext;

public final class KnifeDamageThisTurnEffect implements CardEffect {
    private final int amount;

    public KnifeDamageThisTurnEffect(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        this.amount = amount;
    }

    @Override
    public void apply(EffectContext context) {
        context.setKnifeDamageThisTurn(amount);
        context.log("本回合所有小刀牌伤害变为 " + amount + " 点。");
    }
}
