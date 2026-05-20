package com.course.slay.domain.card;

public interface EffectContext {
    void dealDamageToOpponent(int amount);

    void gainBlock(int amount);

    void drawCards(int amount);

    void gainEnergy(int amount);

    void healSelf(int amount);

    void log(String message);
}
