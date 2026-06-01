package com.course.slay.domain.card;

public interface EffectContext {
    void dealDamageToOpponent(int amount);

    void dealDamageToOpponentIgnoringBlock(int amount);

    void addVulnerableToOpponent(int stacks);

    void gainBlock(int amount);

    int currentBlock();

    boolean hasPlayedAttackThisTurn();

    int cardsPlayedThisBattle();

    void addKnifeCardsToHand(int amount);

    int knifeDamage(int baseDamage);

    void addKnifeDamageBonus(int amount);

    void setKnifeDamageThisTurn(int amount);

    void addKnifeCardsAtTurnStart(int amount);

    void drawCards(int amount);

    void gainEnergy(int amount);

    void reduceNextCardCost(int amount);

    void healSelf(int amount);

    void gainStrength(int amount);

    void gainPermanentStrength(int amount);

    void loseHealth(int amount);

    void skipNextEnemyTurn();

    void limitNextDamageTakenToOne();

    void retainBlockNextTurn();

    void addEnergyPerDiscardThisTurn(int amount);

    void addOnDamageBuff(int strength, int block);

    void log(String message);
}
