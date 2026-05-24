package com.course.slay.domain.run;

public enum MapNodeType {
    NORMAL("敌人", "☠"),
    ELITE("精英", "♛"),
    REST("休息", "♨"),
    EVENT("不明", "?"),
    SHOP("商人", "$"),
    BOSS("首领", "☠");

    private final String displayName;
    private final String iconText;

    MapNodeType(String displayName, String iconText) {
        this.displayName = displayName;
        this.iconText = iconText;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconText() {
        return iconText;
    }
}
