package com.course.slay.domain.card;

public enum CardRarity {
    COMMON("普通"),
    UNCOMMON("罕见"),
    RARE("稀有"),
    LEGENDARY("传说"),
    SPECIAL("特殊");

    private final String displayName;

    CardRarity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
