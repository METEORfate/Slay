package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.EffectContext;

public final class KnifeAtTurnStartEffect implements CardEffect {
    private final int amount;

    public KnifeAtTurnStartEffect(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        this.amount = amount;
    }

    @Override
    public void apply(EffectContext context) {
        context.addKnifeCardsAtTurnStart(amount);
        context.log("每回合开始自动获得 " + amount + " 张小刀牌。");
    }
}
