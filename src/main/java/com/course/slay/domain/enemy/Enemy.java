package com.course.slay.domain.enemy;

import com.course.slay.domain.Combatant;

import java.util.List;
import java.util.Objects;

public class Enemy extends Combatant {
    private final List<EnemyAction> actionPattern;
    private int nextActionIndex;

    public Enemy(String name, int maxHealth, List<EnemyAction> actionPattern) {
        super(name, maxHealth, List.of(), 0, 0);
        if (Objects.requireNonNull(actionPattern, "actionPattern").isEmpty()) {
            throw new IllegalArgumentException("actionPattern must not be empty");
        }
        this.actionPattern = List.copyOf(actionPattern);
    }

    public List<EnemyAction> getActionPattern() {
        return actionPattern;
    }

    public EnemyAction peekNextAction() {
        return actionPattern.get(nextActionIndex);
    }

    public EnemyAction takeNextAction() {
        EnemyAction action = peekNextAction();
        nextActionIndex = (nextActionIndex + 1) % actionPattern.size();
        return action;
    }

    public void resetActionPattern() {
        nextActionIndex = 0;
    }
}
