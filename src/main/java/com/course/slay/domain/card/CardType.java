package com.course.slay.domain.card;

public enum CardType {
    ATTACK("攻击"),
    SKILL("技能"),
    TACTIC("战术"),
    DEFENSE("防御"),
    BUFF("增益"),
    DEBUFF("减益");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
