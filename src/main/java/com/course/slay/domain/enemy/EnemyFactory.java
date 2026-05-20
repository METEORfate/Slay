package com.course.slay.domain.enemy;

import com.course.slay.domain.run.MapNodeType;

import java.util.List;
import java.util.Random;

public final class EnemyFactory {
    private EnemyFactory() {
    }

    public static Enemy createFirstEnemy() {
        return fogHarborHunter();
    }

    public static Enemy createEnemyFor(MapNodeType type, Random random) {
        return switch (type) {
            case NORMAL -> createNormalEnemy(random);
            case ELITE -> createEliteEnemy(random);
            case BOSS -> createBossEnemy();
            case REST, EVENT, SHOP -> throw new IllegalArgumentException(type + " nodes do not create enemies");
        };
    }

    public static Enemy createNormalEnemy(Random random) {
        int index = random.nextInt(3);
        return switch (index) {
            case 0 -> fogHarborHunter();
            case 1 -> grayCandleCultist();
            default -> hollowArmorGuard();
        };
    }

    public static Enemy createEliteEnemy(Random random) {
        int index = random.nextInt(2);
        if (index == 0) {
            return blackLanternKnight();
        }
        return brokenBellConstruct();
    }

    public static Enemy createBossEnemy() {
        return new Enemy(
                "余烬审判者",
                96,
                actions(
                        action("审判护焰", 0, 10, 0),
                        action("余烬裁决", 12, 0, 0),
                        action("焰盾横扫", 8, 6, 0),
                        action("炉心复燃", 0, 0, 6),
                        action("终末重判", 18, 0, 0)
                )
        );
    }

    private static Enemy fogHarborHunter() {
        return new Enemy(
                "雾港巡猎者",
                42,
                actions(
                        action("短弩射击", 6, 0, 0),
                        action("翻滚掩护", 0, 5, 0),
                        action("猎手突刺", 8, 0, 0)
                )
        );
    }

    private static Enemy grayCandleCultist() {
        return new Enemy(
                "灰烛信徒",
                36,
                actions(
                        action("烛焰反扑", 4, 3, 0),
                        action("灰烛祷告", 0, 0, 4),
                        action("焚香突袭", 8, 0, 0)
                )
        );
    }

    private static Enemy hollowArmorGuard() {
        return new Enemy(
                "空甲守卫",
                48,
                actions(
                        action("架盾", 0, 8, 0),
                        action("盾刃横扫", 5, 4, 0),
                        action("铁靴重踏", 7, 0, 0)
                )
        );
    }

    private static Enemy blackLanternKnight() {
        return new Enemy(
                "黑灯骑士",
                64,
                actions(
                        action("黑灯斩", 12, 0, 0),
                        action("骑士守势", 0, 8, 0),
                        action("反击步伐", 6, 5, 0),
                        action("处刑重劈", 14, 0, 0)
                )
        );
    }

    private static Enemy brokenBellConstruct() {
        return new Enemy(
                "残钟构装体",
                70,
                actions(
                        action("残钟护壁", 0, 10, 0),
                        action("钟锤挥击", 8, 0, 0),
                        action("修复仪式", 0, 5, 5),
                        action("裂音重锤", 14, 0, 0)
                )
        );
    }

    private static List<EnemyAction> actions(EnemyAction... actions) {
        return List.of(actions);
    }

    private static EnemyAction action(String name, int damage, int block, int healing) {
        return new EnemyAction(name, describe(damage, block, healing), damage, block, healing);
    }

    private static String describe(int damage, int block, int healing) {
        List<String> parts = new java.util.ArrayList<>();
        if (damage > 0) {
            parts.add("造成 " + damage + " 点伤害");
        }
        if (block > 0) {
            parts.add("获得 " + block + " 点格挡");
        }
        if (healing > 0) {
            parts.add("恢复 " + healing + " 点生命");
        }
        return String.join("，", parts) + "。";
    }
}
