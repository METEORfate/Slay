package com.course.slay.domain.run;

public enum MapNodeType {
    NORMAL("普通战斗", "剑"),
    ELITE("精英战斗", "冠"),
    REST("营地休整", "火"),
    EVENT("特殊事件", "?"),
    SHOP("商店补给", "$"),
    BOSS("首领战", "门");

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
