package com.course.slay.domain.character;

import com.course.slay.domain.card.CardFactory;
import com.course.slay.domain.card.StarterDeckFactory;

import java.util.List;
import java.util.Optional;

public final class CharacterCatalog {
    private static final List<PlayableCharacter> CHARACTERS = List.of(
            new PlayableCharacter(
                    "night_watch",
                    "守夜人",
                    "均衡防守",
                    "稳定的攻击、格挡和过牌，适合熟悉基础远征节奏。",
                    72,
                    3,
                    5,
                    StarterDeckFactory::createNightWatchStarterDeck,
                    CardFactory::rewardPool
            ),
            new PlayableCharacter(
                    "cinder_seeker",
                    "烬痕旅人",
                    "机动作战",
                    "当前与守夜人复用同一套牌，后续可替换为更偏机动的初始牌和奖励池。",
                    72,
                    3,
                    5,
                    StarterDeckFactory::createCinderSeekerStarterDeck,
                    CardFactory::rewardPool
            )
    );

    private CharacterCatalog() {
    }

    public static List<PlayableCharacter> availableCharacters() {
        return CHARACTERS;
    }

    public static PlayableCharacter defaultCharacter() {
        return CHARACTERS.get(0);
    }

    public static Optional<PlayableCharacter> findById(String id) {
        return CHARACTERS.stream()
                .filter(character -> character.id().equals(id))
                .findFirst();
    }
}
