package com.course.slay.domain.enemy;

import com.course.slay.domain.Combatant;
import com.course.slay.domain.card.CardVisualEffect;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class EnemyAction {
    private final String name;
    private final String description;
    private final int damage;
    private final int block;
    private final int healing;
    private final Set<CardVisualEffect> visualEffects;

    public EnemyAction(String name, String description, int damage, int block, int healing) {
        if (damage < 0) {
            throw new IllegalArgumentException("damage must not be negative");
        }
        if (block < 0) {
            throw new IllegalArgumentException("block must not be negative");
        }
        if (healing < 0) {
            throw new IllegalArgumentException("healing must not be negative");
        }
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
        this.damage = damage;
        this.block = block;
        this.healing = healing;
        this.visualEffects = createVisualEffects(damage, block);
    }

    public static EnemyAction attack(String name, int damage) {
        return new EnemyAction(name, "造成 " + damage + " 点伤害。", damage, 0, 0);
    }

    public static EnemyAction block(String name, int block) {
        return new EnemyAction(name, "获得 " + block + " 点格挡。", 0, block, 0);
    }

    public static EnemyAction heal(String name, int healing) {
        return new EnemyAction(name, "恢复 " + healing + " 点生命。", 0, 0, healing);
    }

    public EnemyIntent toIntent() {
        return new EnemyIntent(name, description, damage, block, healing);
    }

    public Set<CardVisualEffect> getVisualEffects() {
        return visualEffects;
    }

    public Set<CardVisualEffect> execute(Combatant actor, Combatant opponent, Consumer<String> log) {
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(opponent, "opponent");
        Objects.requireNonNull(log, "log");

        log.accept(actor.getName() + " 执行【" + name + "】。");
        if (block > 0) {
            actor.gainBlock(block);
            log.accept(actor.getName() + " 获得 " + block + " 点格挡。");
        }
        if (healing > 0) {
            int healed = actor.heal(healing);
            log.accept(actor.getName() + " 恢复 " + healed + " 点生命。");
        }
        if (damage > 0) {
            int beforeHealth = opponent.getHealth();
            int beforeBlock = opponent.getBlock();
            opponent.takeDamage(damage);
            int blocked = Math.max(0, beforeBlock - opponent.getBlock());
            int healthDamage = Math.max(0, beforeHealth - opponent.getHealth());
            log.accept(actor.getName() + " 造成 " + healthDamage
                    + " 点生命伤害，格挡抵消 " + blocked + " 点。");
        }
        return visualEffects;
    }

    private Set<CardVisualEffect> createVisualEffects(int damage, int block) {
        Set<CardVisualEffect> effects = new LinkedHashSet<>();
        if (damage > 0) {
            effects.add(CardVisualEffect.ATTACK);
        }
        if (block > 0) {
            effects.add(CardVisualEffect.SHIELD);
        }
        return Set.copyOf(effects);
    }
}
