package com.course.slay.domain.card.effects;

import com.course.slay.domain.card.CardEffect;
import com.course.slay.domain.card.EffectContext;

public final class SkipEnemyTurnEffect implements CardEffect {
    @Override
    public void apply(EffectContext context) {
        context.skipNextEnemyTurn();
        context.log("下一次敌方回合将被跳过。");
    }
}
