package com.course.slay.domain.character;

import com.course.slay.domain.card.CardFactory;
import com.course.slay.domain.card.StarterDeckFactory;

import java.util.List;
import java.util.Optional;

public final class CharacterCatalog {
    private static final List<PlayableCharacter> CHARACTERS = List.of(
            new PlayableCharacter(
                    "night_watch",
                    "狂战士",
                    "均衡防守",
                    "稳定的攻击、格挡和过牌，适合熟悉基础远征节奏。",
                    72,
                    3,
                    5,
                    StarterDeckFactory::createBerserkerStarterDeck,
                    CardFactory::berserkerRewardPool
            ),
            new PlayableCharacter(
                    "cinder_seeker",
                    "刺客",
                    "机动作战",
                    "围绕小刀、连击、易伤和短期爆发展开，依靠精准时机压低敌人生命。",
                    72,
                    3,
                    5,
                    StarterDeckFactory::createAssassinStarterDeck,
                    CardFactory::assassinRewardPool
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
