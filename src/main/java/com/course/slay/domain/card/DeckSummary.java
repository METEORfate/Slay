package com.course.slay.domain.card;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DeckSummary {
    private final int totalCards;
    private final int attackCount;
    private final int skillCount;
    private final int tacticCount;
    private final double averageCost;
    private final List<CardEntry> entries;

    private DeckSummary(
            int totalCards,
            int attackCount,
            int skillCount,
            int tacticCount,
            double averageCost,
            List<CardEntry> entries
    ) {
        this.totalCards = totalCards;
        this.attackCount = attackCount;
        this.skillCount = skillCount;
        this.tacticCount = tacticCount;
        this.averageCost = averageCost;
        this.entries = List.copyOf(entries);
    }

    public static DeckSummary from(List<Card> cards) {
        Objects.requireNonNull(cards, "cards");
        int total = cards.size();
        int attack = countByType(cards, CardType.ATTACK);
        int skill = countByType(cards, CardType.SKILL);
        int tactic = countByType(cards, CardType.TACTIC);
        double average = cards.stream()
                .mapToInt(Card::getCost)
                .average()
                .orElse(0);

        Map<String, List<Card>> grouped = cards.stream()
                .collect(Collectors.groupingBy(Card::getId, LinkedHashMap::new, Collectors.toList()));
        List<CardEntry> entries = grouped.values().stream()
                .map(group -> {
                    Card first = group.get(0);
                    return new CardEntry(
                            first.getId(),
                            first.getName(),
                            first.getType(),
                            first.getRarity(),
                            first.getCost(),
                            first.getDescription(),
                            group.size()
                    );
                })
                .sorted(Comparator
                        .comparing((CardEntry entry) -> entry.rarity().ordinal())
                        .thenComparing(entry -> entry.type().ordinal())
                        .thenComparingInt(CardEntry::cost)
                        .thenComparing(CardEntry::name))
                .toList();

        return new DeckSummary(total, attack, skill, tactic, average, entries);
    }

    private static int countByType(List<Card> cards, CardType type) {
        return (int) cards.stream()
                .filter(card -> card.getType() == type)
                .count();
    }

    public int getTotalCards() {
        return totalCards;
    }

    public int getAttackCount() {
        return attackCount;
    }

    public int getSkillCount() {
        return skillCount;
    }

    public int getTacticCount() {
        return tacticCount;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public List<CardEntry> getEntries() {
        return entries;
    }

    public record CardEntry(
            String id,
            String name,
            CardType type,
            CardRarity rarity,
            int cost,
            String description,
            int count
    ) {
    }
}
