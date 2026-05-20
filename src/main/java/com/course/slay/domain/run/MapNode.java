package com.course.slay.domain.run;

import java.util.List;
import java.util.Objects;

public class MapNode {
    private final String id;
    private final String name;
    private final MapNodeType type;
    private final int floor;
    private final int lane;
    private final List<String> nextNodeIds;
    private boolean available;
    private boolean completed;

    public MapNode(String id, String name, MapNodeType type, int floor, List<String> nextNodeIds) {
        this(id, name, type, floor, 0, nextNodeIds);
    }

    public MapNode(String id, String name, MapNodeType type, int floor, int lane, List<String> nextNodeIds) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.floor = floor;
        this.lane = lane;
        this.nextNodeIds = List.copyOf(nextNodeIds);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MapNodeType getType() {
        return type;
    }

    public int getFloor() {
        return floor;
    }

    public int getLane() {
        return lane;
    }

    public List<String> getNextNodeIds() {
        return nextNodeIds;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
