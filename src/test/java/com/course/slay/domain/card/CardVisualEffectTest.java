package com.course.slay.domain.card;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardVisualEffectTest {
    @Test
    void damageAndBlockEffectsExposeVisualEffects() {
        assertEquals(SetOf.attack(), CardFactory.emberStrike().getVisualEffects());
        assertEquals(SetOf.shield(), CardFactory.ironCurtain().getVisualEffects());

        var visualEffects = CardFactory.shieldBash().getVisualEffects();
        assertTrue(visualEffects.contains(CardVisualEffect.ATTACK));
        assertTrue(visualEffects.contains(CardVisualEffect.SHIELD));
        assertEquals(2, visualEffects.size());
    }

    private static final class SetOf {
        private SetOf() {
        }

        private static java.util.Set<CardVisualEffect> attack() {
            return java.util.Set.of(CardVisualEffect.ATTACK);
        }

        private static java.util.Set<CardVisualEffect> shield() {
            return java.util.Set.of(CardVisualEffect.SHIELD);
        }
    }
}
