package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.EffectContext;

public final class OnDamageBuffEffect implements CardEffect {
    private final int strength;
    private final int block;

    public OnDamageBuffEffect(int strength, int block) {
        if (strength < 0) {
            throw new IllegalArgumentException("strength must not be negative");
        }
        if (block < 0) {
            throw new IllegalArgumentException("block must not be negative");
        }
        this.strength = strength;
        this.block = block;
    }

    @Override
    public void apply(EffectContext context) {
        context.addOnDamageBuff(strength, block);
        context.log("每当本场战斗受到伤害时，获得 " + strength + " 点力量和 " + block + " 点格挡。");
    }
}
