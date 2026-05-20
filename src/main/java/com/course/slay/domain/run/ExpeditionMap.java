package com.course.slay.domain.run;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExpeditionMap {
    private final List<MapNode> nodes;
    private final Map<String, MapNode> nodeById;

    public ExpeditionMap(List<MapNode> nodes) {
        this.nodes = List.copyOf(nodes);
        this.nodeById = nodes.stream()
                .collect(Collectors.toUnmodifiableMap(MapNode::getId, node -> node));
    }

    public List<MapNode> getNodes() {
        return nodes;
    }

    public List<MapNode> getNodesByFloor(int floor) {
        return nodes.stream()
                .filter(node -> node.getFloor() == floor)
                .sorted(Comparator.comparingInt(MapNode::getLane)
                        .thenComparing(MapNode::getId))
                .toList();
    }

    public List<Integer> getFloors() {
        return nodes.stream()
                .map(MapNode::getFloor)
                .distinct()
                .sorted()
                .toList();
    }

    public Optional<MapNode> findNode(String id) {
        return Optional.ofNullable(nodeById.get(id));
    }

    public void unlockStartingNodes() {
        int firstFloor = nodes.stream()
                .mapToInt(MapNode::getFloor)
                .min()
                .orElse(0);
        nodes.forEach(node -> node.setAvailable(node.getFloor() == firstFloor));
    }

    public void completeNode(MapNode completedNode) {
        Objects.requireNonNull(completedNode, "completedNode");
        completedNode.setCompleted(true);
        nodes.forEach(node -> node.setAvailable(false));
        completedNode.getNextNodeIds().stream()
                .map(nodeById::get)
                .filter(Objects::nonNull)
                .filter(node -> !node.isCompleted())
                .forEach(node -> node.setAvailable(true));
    }
}
