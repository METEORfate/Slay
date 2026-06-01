package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.EffectContext;

public final class KnifeDamageBonusEffect implements CardEffect {
    private final int amount;

    public KnifeDamageBonusEffect(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        this.amount = amount;
    }

    @Override
    public void apply(EffectContext context) {
        context.addKnifeDamageBonus(amount);
        context.log("本场战斗中，每张小刀牌伤害提升 " + amount + " 点。");
    }
}
