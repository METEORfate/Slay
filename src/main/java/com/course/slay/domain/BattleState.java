package com.course.slay.domain;

import com.course.slay.domain.card.Card;
import com.course.slay.domain.card.CardType;
import com.course.slay.domain.enemy.Enemy;
import com.course.slay.domain.enemy.EnemyIntent;

import java.util.List;
import java.util.Objects;

public class BattleState {
    private static final int NO_KNIFE_DAMAGE_OVERRIDE = -1;

    private final Player player;
    private final Enemy enemy;
    private final List<String> battleLog;
    private final List<Card> rewardChoices;
    private EnemyIntent enemyIntent;
    private int turnNumber;
    private GameStatus status;
    private boolean skipNextEnemyTurn;
    private int energyPerDiscardThisTurn;
    private int attackCardsPlayedThisTurn;
    private int cardsPlayedThisBattle;
    private int nextCardCostReduction;
    private int strengthOnDamage;
    private int blockOnDamage;
    private boolean retainBlockNextTurn;
    private int knifeDamageBonus;
    private int knifeDamageThisTurn = NO_KNIFE_DAMAGE_OVERRIDE;
    private int knifeCardsAtTurnStart;

    public BattleState(Player player, Enemy enemy) {
        this.player = Objects.requireNonNull(player, "player");
        this.enemy = Objects.requireNonNull(enemy, "enemy");
        this.battleLog = new java.util.ArrayList<>();
        this.rewardChoices = new java.util.ArrayList<>();
        this.status = GameStatus.IN_PROGRESS;
    }

    public Player getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public List<Card> getDeck() {
        return player.getDeck();
    }

    public List<Card> getDrawPile() {
        return player.getDrawPile();
    }

    public List<Card> getHand() {
        return player.getHand();
    }

    public List<Card> getDiscardPile() {
        return player.getDiscardPile();
    }

    public List<String> getBattleLog() {
        return battleLog;
    }

    public List<Card> getRewardChoices() {
        return rewardChoices;
    }

    public EnemyIntent getEnemyIntent() {
        return enemyIntent;
    }

    public void setEnemyIntent(EnemyIntent enemyIntent) {
        this.enemyIntent = enemyIntent;
    }

    public int getMaxEnergy() {
        return player.getMaxEnergy();
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void nextTurn() {
        turnNumber++;
        energyPerDiscardThisTurn = 0;
        attackCardsPlayedThisTurn = 0;
        nextCardCostReduction = 0;
        knifeDamageThisTurn = NO_KNIFE_DAMAGE_OVERRIDE;
    }

    public int getEnergy() {
        return player.getEnergy();
    }

    public void setEnergy(int energy) {
        player.setEnergy(energy);
    }

    public void spendEnergy(int amount) {
        player.spendEnergy(amount);
    }

    public int effectiveCost(Card card) {
        Objects.requireNonNull(card, "card");
        return Math.max(0, card.getCost() - nextCardCostReduction);
    }

    public void consumeNextCardCostReduction() {
        nextCardCostReduction = 0;
    }

    public void reduceNextCardCost(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        nextCardCostReduction += amount;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    public void skipNextEnemyTurn() {
        skipNextEnemyTurn = true;
    }

    public void retainBlockNextTurn() {
        retainBlockNextTurn = true;
    }

    public boolean consumeRetainBlockNextTurn() {
        boolean value = retainBlockNextTurn;
        retainBlockNextTurn = false;
        return value;
    }

    public boolean consumeSkipNextEnemyTurn() {
        boolean value = skipNextEnemyTurn;
        skipNextEnemyTurn = false;
        return value;
    }

    public void addEnergyPerDiscardThisTurn(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        energyPerDiscardThisTurn += amount;
    }

    public int getEnergyPerDiscardThisTurn() {
        return energyPerDiscardThisTurn;
    }

    public void recordPlayedCard(CardType type) {
        cardsPlayedThisBattle++;
        if (type == CardType.ATTACK) {
            attackCardsPlayedThisTurn++;
        }
    }

    public int getCardsPlayedThisBattle() {
        return cardsPlayedThisBattle;
    }

    public boolean hasPlayedAttackThisTurn() {
        return attackCardsPlayedThisTurn > 0;
    }

    public int knifeDamage(int baseDamage) {
        if (baseDamage < 0) {
            throw new IllegalArgumentException("baseDamage must not be negative");
        }
        if (knifeDamageThisTurn >= 0) {
            return knifeDamageThisTurn;
        }
        return baseDamage + knifeDamageBonus;
    }

    public void addKnifeDamageBonus(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        knifeDamageBonus += amount;
    }

    public void setKnifeDamageThisTurn(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        knifeDamageThisTurn = amount;
    }

    public void addKnifeCardsAtTurnStart(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        knifeCardsAtTurnStart += amount;
    }

    public int getKnifeCardsAtTurnStart() {
        return knifeCardsAtTurnStart;
    }

    public void addOnDamageBuff(int strength, int block) {
        if (strength < 0) {
            throw new IllegalArgumentException("strength must not be negative");
        }
        if (block < 0) {
            throw new IllegalArgumentException("block must not be negative");
        }
        strengthOnDamage += strength;
        blockOnDamage += block;
    }

    public int getStrengthOnDamage() {
        return strengthOnDamage;
    }

    public int getBlockOnDamage() {
        return blockOnDamage;
    }

    public void addLog(String message) {
        battleLog.add(Objects.requireNonNull(message, "message"));
    }
}
