package com.course.slay.domain.run;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class RouteMapFactory {
    public static final int DEFAULT_FLOOR_COUNT = 14;
    private static final int LANE_COUNT = 7;

    private static final List<String> NORMAL_NAMES = List.of(
            "废道巡逻", "雾港伏击", "碎桥哨兵", "黑巷争斗", "灰街拦截", "旧塔守卫",
            "断墙战线", "潮湿矿巷", "残灯街口", "铁栅哨点"
    );
    private static final List<String> ELITE_NAMES = List.of(
            "黑灯骑士", "残钟构装体", "铁誓卫队", "鸦影猎手", "焦骨卫士"
    );
    private static final List<String> REST_NAMES = List.of(
            "静火营地", "避风篝火", "石阶营火", "旧井休整"
    );
    private static final List<String> EVENT_NAMES = List.of(
            "低语门廊", "无名货箱", "破损祭坛", "迷雾岔口", "旧信使遗物"
    );
    private static final List<String> SHOP_NAMES = List.of(
            "游商帐篷", "烛火小铺", "黑市补给", "铁柜商人"
    );

    private RouteMapFactory() {
    }

    public static ExpeditionMap createDefaultMap() {
        return createRandomMap(new Random());
    }

    public static ExpeditionMap createRandomMap(Random random) {
        List<List<NodeDraft>> floors = createFloorDrafts(random);
        connectFloors(floors);

        List<MapNode> nodes = new ArrayList<>();
        for (List<NodeDraft> floor : floors) {
            for (NodeDraft draft : floor) {
                nodes.add(new MapNode(
                        draft.id,
                        draft.name,
                        draft.type,
                        draft.floor,
                        draft.lane,
                        draft.nextIds.stream()
                                .sorted()
                                .toList()
                ));
            }
        }

        ExpeditionMap map = new ExpeditionMap(nodes);
        map.unlockStartingNodes();
        return map;
    }

    private static List<List<NodeDraft>> createFloorDrafts(Random random) {
        List<List<NodeDraft>> floors = new ArrayList<>();
        for (int floor = 1; floor <= DEFAULT_FLOOR_COUNT; floor++) {
            List<Integer> lanes = pickLanes(random, nodeCountForFloor(floor, random));
            List<NodeDraft> drafts = new ArrayList<>();
            for (int index = 0; index < lanes.size(); index++) {
                MapNodeType type = typeForFloor(floor, random);
                drafts.add(new NodeDraft(
                        "f" + floor + "n" + index,
                        nameFor(type, random),
                        type,
                        floor,
                        lanes.get(index)
                ));
            }
            floors.add(drafts);
        }
        return floors;
    }

    private static int nodeCountForFloor(int floor, Random random) {
        if (floor == DEFAULT_FLOOR_COUNT) {
            return 1;
        }
        if (floor == 1) {
            return 4;
        }
        if (floor == 5 || floor == 9 || floor == 13) {
            return 3 + random.nextInt(2);
        }
        return 4 + random.nextInt(2);
    }

    private static MapNodeType typeForFloor(int floor, Random random) {
        if (floor == DEFAULT_FLOOR_COUNT) {
            return MapNodeType.BOSS;
        }
        if (floor == 1) {
            return MapNodeType.NORMAL;
        }
        if (floor == 5 || floor == 9 || floor == 13) {
            int roll = random.nextInt(100);
            if (roll < 55) {
                return MapNodeType.REST;
            }
            return roll < 78 ? MapNodeType.SHOP : MapNodeType.EVENT;
        }

        int roll = random.nextInt(100);
        if (roll < 54) {
            return MapNodeType.NORMAL;
        }
        if (roll < 72) {
            return MapNodeType.ELITE;
        }
        if (roll < 86) {
            return MapNodeType.EVENT;
        }
        if (roll < 95) {
            return MapNodeType.SHOP;
        }
        return MapNodeType.REST;
    }

    private static List<Integer> pickLanes(Random random, int count) {
        if (count == 1) {
            return List.of(LANE_COUNT / 2);
        }

        Set<Integer> lanes = new HashSet<>();
        while (lanes.size() < count) {
            lanes.add(random.nextInt(LANE_COUNT));
        }
        return lanes.stream()
                .sorted()
                .toList();
    }

    private static void connectFloors(List<List<NodeDraft>> floors) {
        for (int i = 0; i < floors.size() - 1; i++) {
            List<NodeDraft> current = floors.get(i);
            List<NodeDraft> next = floors.get(i + 1);
            Map<String, Integer> incoming = new HashMap<>();

            for (NodeDraft node : current) {
                next.stream()
                        .sorted(Comparator.comparingInt(target -> Math.abs(target.lane - node.lane)))
                        .limit(node.type == MapNodeType.REST ? 1 : 2)
                        .forEach(target -> {
                            node.nextIds.add(target.id);
                            incoming.merge(target.id, 1, Integer::sum);
                        });
            }

            for (NodeDraft target : next) {
                if (!incoming.containsKey(target.id)) {
                    NodeDraft closest = current.stream()
                            .min(Comparator.comparingInt(source -> Math.abs(source.lane - target.lane)))
                            .orElseThrow();
                    closest.nextIds.add(target.id);
                }
            }
        }
    }

    private static String nameFor(MapNodeType type, Random random) {
        return switch (type) {
            case NORMAL -> pick(NORMAL_NAMES, random);
            case ELITE -> pick(ELITE_NAMES, random);
            case REST -> pick(REST_NAMES, random);
            case EVENT -> pick(EVENT_NAMES, random);
            case SHOP -> pick(SHOP_NAMES, random);
            case BOSS -> "余烬审判厅";
        };
    }

    private static String pick(List<String> values, Random random) {
        return values.get(random.nextInt(values.size()));
    }

    private static final class NodeDraft {
        private final String id;
        private final String name;
        private final MapNodeType type;
        private final int floor;
        private final int lane;
        private final Set<String> nextIds = new HashSet<>();

        private NodeDraft(String id, String name, MapNodeType type, int floor, int lane) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.floor = floor;
            this.lane = lane;
        }
    }
}
