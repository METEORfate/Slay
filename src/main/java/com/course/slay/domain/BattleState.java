package com.course.slay.domain;

import com.course.slay.domain.card.Card;
import com.course.slay.domain.enemy.Enemy;
import com.course.slay.domain.enemy.EnemyIntent;

import java.util.List;
import java.util.Objects;

public class BattleState {
    private final Player player;
    private final Enemy enemy;
    private final List<String> battleLog;
    private final List<Card> rewardChoices;
    private EnemyIntent enemyIntent;
    private int turnNumber;
    private GameStatus status;

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

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    public void addLog(String message) {
        battleLog.add(Objects.requireNonNull(message, "message"));
    }
}
