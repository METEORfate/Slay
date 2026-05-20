package com.course.slay.domain.card;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeckSummaryTest {
    @Test
    void summarizesDeckCompositionAndCardCounts() {
        DeckSummary summary = DeckSummary.from(List.of(
                CardFactory.emberStrike(),
                CardFactory.emberStrike(),
                CardFactory.ironCurtain(),
                CardFactory.swiftStep()
        ));

        assertEquals(4, summary.getTotalCards());
        assertEquals(2, summary.getAttackCount());
        assertEquals(1, summary.getSkillCount());
        assertEquals(1, summary.getTacticCount());
        assertEquals(0.75, summary.getAverageCost(), 0.001);
        assertEquals(3, summary.getEntries().size());
        assertEquals(2, summary.getEntries().stream()
                .filter(entry -> entry.id().equals("ember_strike"))
                .findFirst()
                .orElseThrow()
                .count());
    }
}
