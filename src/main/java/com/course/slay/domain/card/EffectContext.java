package com.course.slay.domain.card;

public interface EffectContext {
    void dealDamageToOpponent(int amount);

    void gainBlock(int amount);

    int currentBlock();

    boolean hasPlayedAttackThisTurn();

    void drawCards(int amount);

    void gainEnergy(int amount);

    void healSelf(int amount);

    void gainStrength(int amount);

    void gainPermanentStrength(int amount);

    void loseHealth(int amount);

    void skipNextEnemyTurn();

    void addEnergyPerDiscardThisTurn(int amount);

    void addOnDamageBuff(int strength, int block);

    void log(String message);
}
