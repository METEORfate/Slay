package com.course.slay.domain.enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class EnemyIntent {
    private final String name;
    private final String description;
    private final int damage;
    private final int block;
    private final int healing;

    public EnemyIntent(String name, String description, int damage, int block, int healing) {
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
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDamage() {
        return damage;
    }

    public int getBlock() {
        return block;
    }

    public int getHealing() {
        return healing;
    }

    public String summary() {
        List<String> parts = new ArrayList<>();
        if (damage > 0) {
            parts.add("攻击 " + damage);
        }
        if (block > 0) {
            parts.add("防御 " + block);
        }
        if (healing > 0) {
            parts.add("治疗 " + healing);
        }
        if (parts.isEmpty()) {
            parts.add("其他效果");
        }
        return String.join(" / ", parts) + "（" + name + "）";
    }

    public String detail() {
        return "准备执行【" + name + "】：" + description;
    }
}
