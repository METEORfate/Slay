package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.EffectContext;

public final class EnergyPerDiscardEffect implements CardEffect {
    private final int amount;

    public EnergyPerDiscardEffect(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        this.amount = amount;
    }

    @Override
    public void apply(EffectContext context) {
        context.addEnergyPerDiscardThisTurn(amount);
        context.log("本回合每弃 1 张牌，获得 " + amount + " 点能量。");
    }
}
