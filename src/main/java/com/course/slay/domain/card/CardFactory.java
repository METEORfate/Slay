package com.course.slay.domain.card;

import com.course.slay.domain.card.effects.BlockEffect;
import com.course.slay.domain.card.effects.CompositeEffect;
import com.course.slay.domain.card.effects.DamageEffect;
import com.course.slay.domain.card.effects.DrawCardEffect;
import com.course.slay.domain.card.effects.EnergyEffect;
import com.course.slay.domain.card.effects.HealEffect;

import java.util.List;
import java.util.function.Supplier;

public final class CardFactory {
    private static final List<Supplier<Card>> REWARD_SUPPLIERS = List.of(
            CardFactory::dawnBreaker,
            CardFactory::nightCounter,
            CardFactory::emberGuard,
            CardFactory::swiftStep,
            CardFactory::ashNeedle,
            CardFactory::shieldBash,
            CardFactory::lanternFlare,
            CardFactory::deepBreath,
            CardFactory::sparkFocus,
            CardFactory::fieldDressing,
            CardFactory::stormOfCinders,
            CardFactory::sentinelOath,
            CardFactory::quickRead,
            CardFactory::emberSurge
    );

    private CardFactory() {
    }

    public static Card emberStrike() {
        return card("ember_strike", "余烬斩", 1, CardType.ATTACK, "造成 6 点伤害。",
                new DamageEffect(6), false);
    }

    public static Card emberStrikePlus() {
        return card("ember_strike_plus", "余烬斩+", 1, CardType.ATTACK, "造成 9 点伤害。",
                new DamageEffect(9), true);
    }

    public static Card ironCurtain() {
        return card("iron_curtain", "铁幕", 1, CardType.SKILL, "获得 5 点格挡。",
                new BlockEffect(5), false);
    }

    public static Card ironCurtainPlus() {
        return card("iron_curtain_plus", "铁幕+", 1, CardType.SKILL, "获得 8 点格挡。",
                new BlockEffect(8), true);
    }

    public static Card swiftStep() {
        return card("swift_step", "疾行", 0, CardType.TACTIC, "抽 1 张牌。",
                new DrawCardEffect(1), false);
    }

    public static Card swiftStepPlus() {
        return card("swift_step_plus", "疾行+", 0, CardType.TACTIC, "抽 2 张牌。",
                new DrawCardEffect(2), true);
    }

    public static Card dawnBreaker() {
        return card("dawn_breaker", "破晓重击", 2, CardType.ATTACK, "造成 12 点伤害。",
                new DamageEffect(12), false);
    }

    public static Card dawnBreakerPlus() {
        return card("dawn_breaker_plus", "破晓重击+", 2, CardType.ATTACK, "造成 16 点伤害。",
                new DamageEffect(16), true);
    }

    public static Card nightCounter() {
        return card("night_counter", "守夜反击", 1, CardType.SKILL, "获得 3 点格挡，造成 3 点伤害。",
                CompositeEffect.of(new BlockEffect(3), new DamageEffect(3)), false);
    }

    public static Card nightCounterPlus() {
        return card("night_counter_plus", "守夜反击+", 1, CardType.SKILL, "获得 5 点格挡，造成 5 点伤害。",
                CompositeEffect.of(new BlockEffect(5), new DamageEffect(5)), true);
    }

    public static Card emberGuard() {
        return card("ember_guard", "余火壁垒", 2, CardType.SKILL, "获得 9 点格挡。",
                new BlockEffect(9), false);
    }

    public static Card emberGuardPlus() {
        return card("ember_guard_plus", "余火壁垒+", 2, CardType.SKILL, "获得 13 点格挡。",
                new BlockEffect(13), true);
    }

    public static Card ashNeedle() {
        return card("ash_needle", "灰针", 0, CardType.ATTACK, "造成 3 点伤害。",
                new DamageEffect(3), false);
    }

    public static Card ashNeedlePlus() {
        return card("ash_needle_plus", "灰针+", 0, CardType.ATTACK, "造成 5 点伤害。",
                new DamageEffect(5), true);
    }

    public static Card shieldBash() {
        return card("shield_bash", "盾击", 1, CardType.ATTACK, "获得 4 点格挡，造成 5 点伤害。",
                CompositeEffect.of(new BlockEffect(4), new DamageEffect(5)), false);
    }

    public static Card shieldBashPlus() {
        return card("shield_bash_plus", "盾击+", 1, CardType.ATTACK, "获得 6 点格挡，造成 7 点伤害。",
                CompositeEffect.of(new BlockEffect(6), new DamageEffect(7)), true);
    }

    public static Card lanternFlare() {
        return card("lantern_flare", "提灯耀斑", 1, CardType.TACTIC, "造成 4 点伤害，抽 1 张牌。",
                CompositeEffect.of(new DamageEffect(4), new DrawCardEffect(1)), false);
    }

    public static Card lanternFlarePlus() {
        return card("lantern_flare_plus", "提灯耀斑+", 1, CardType.TACTIC, "造成 6 点伤害，抽 1 张牌。",
                CompositeEffect.of(new DamageEffect(6), new DrawCardEffect(1)), true);
    }

    public static Card deepBreath() {
        return card("deep_breath", "沉息", 1, CardType.SKILL, "获得 4 点格挡，抽 1 张牌。",
                CompositeEffect.of(new BlockEffect(4), new DrawCardEffect(1)), false);
    }

    public static Card deepBreathPlus() {
        return card("deep_breath_plus", "沉息+", 1, CardType.SKILL, "获得 6 点格挡，抽 1 张牌。",
                CompositeEffect.of(new BlockEffect(6), new DrawCardEffect(1)), true);
    }

    public static Card sparkFocus() {
        return card("spark_focus", "火星凝神", 0, CardType.TACTIC, "获得 1 点能量。",
                new EnergyEffect(1), false);
    }

    public static Card sparkFocusPlus() {
        return card("spark_focus_plus", "火星凝神+", 0, CardType.TACTIC, "获得 1 点能量，抽 1 张牌。",
                CompositeEffect.of(new EnergyEffect(1), new DrawCardEffect(1)), true);
    }

    public static Card fieldDressing() {
        return card("field_dressing", "战地包扎", 1, CardType.SKILL, "恢复 4 点生命。",
                new HealEffect(4), false);
    }

    public static Card fieldDressingPlus() {
        return card("field_dressing_plus", "战地包扎+", 1, CardType.SKILL, "恢复 6 点生命。",
                new HealEffect(6), true);
    }

    public static Card stormOfCinders() {
        return card("storm_of_cinders", "烬雨", 2, CardType.ATTACK, "造成 8 点伤害，抽 1 张牌。",
                CompositeEffect.of(new DamageEffect(8), new DrawCardEffect(1)), false);
    }

    public static Card stormOfCindersPlus() {
        return card("storm_of_cinders_plus", "烬雨+", 2, CardType.ATTACK, "造成 11 点伤害，抽 1 张牌。",
                CompositeEffect.of(new DamageEffect(11), new DrawCardEffect(1)), true);
    }

    public static Card sentinelOath() {
        return card("sentinel_oath", "哨卫誓言", 2, CardType.SKILL, "获得 7 点格挡，恢复 3 点生命。",
                CompositeEffect.of(new BlockEffect(7), new HealEffect(3)), false);
    }

    public static Card sentinelOathPlus() {
        return card("sentinel_oath_plus", "哨卫誓言+", 2, CardType.SKILL, "获得 10 点格挡，恢复 4 点生命。",
                CompositeEffect.of(new BlockEffect(10), new HealEffect(4)), true);
    }

    public static Card quickRead() {
        return card("quick_read", "战术速读", 1, CardType.TACTIC, "抽 2 张牌。",
                new DrawCardEffect(2), false);
    }

    public static Card quickReadPlus() {
        return card("quick_read_plus", "战术速读+", 1, CardType.TACTIC, "抽 3 张牌。",
                new DrawCardEffect(3), true);
    }

    public static Card emberSurge() {
        return card("ember_surge", "余烬涌动", 1, CardType.TACTIC, "获得 1 点能量，造成 4 点伤害。",
                CompositeEffect.of(new EnergyEffect(1), new DamageEffect(4)), false);
    }

    public static Card emberSurgePlus() {
        return card("ember_surge_plus", "余烬涌动+", 1, CardType.TACTIC, "获得 1 点能量，造成 7 点伤害。",
                CompositeEffect.of(new EnergyEffect(1), new DamageEffect(7)), true);
    }

    public static List<Card> rewardPool() {
        return REWARD_SUPPLIERS.stream()
                .map(Supplier::get)
                .toList();
    }

    public static boolean canUpgrade(Card card) {
        return !card.isUpgraded() && upgradeSupplier(card.getId()) != null;
    }

    public static Card upgradeOf(Card card) {
        Supplier<Card> supplier = upgradeSupplier(card.getId());
        if (card.isUpgraded() || supplier == null) {
            throw new IllegalArgumentException("Card cannot be upgraded: " + card.getId());
        }
        return supplier.get();
    }

    public static Card copyOf(Card card) {
        return switch (card.getId()) {
            case "ember_strike" -> emberStrike();
            case "ember_strike_plus" -> emberStrikePlus();
            case "iron_curtain" -> ironCurtain();
            case "iron_curtain_plus" -> ironCurtainPlus();
            case "swift_step" -> swiftStep();
            case "swift_step_plus" -> swiftStepPlus();
            case "dawn_breaker" -> dawnBreaker();
            case "dawn_breaker_plus" -> dawnBreakerPlus();
            case "night_counter" -> nightCounter();
            case "night_counter_plus" -> nightCounterPlus();
            case "ember_guard" -> emberGuard();
            case "ember_guard_plus" -> emberGuardPlus();
            case "ash_needle" -> ashNeedle();
            case "ash_needle_plus" -> ashNeedlePlus();
            case "shield_bash" -> shieldBash();
            case "shield_bash_plus" -> shieldBashPlus();
            case "lantern_flare" -> lanternFlare();
            case "lantern_flare_plus" -> lanternFlarePlus();
            case "deep_breath" -> deepBreath();
            case "deep_breath_plus" -> deepBreathPlus();
            case "spark_focus" -> sparkFocus();
            case "spark_focus_plus" -> sparkFocusPlus();
            case "field_dressing" -> fieldDressing();
            case "field_dressing_plus" -> fieldDressingPlus();
            case "storm_of_cinders" -> stormOfCinders();
            case "storm_of_cinders_plus" -> stormOfCindersPlus();
            case "sentinel_oath" -> sentinelOath();
            case "sentinel_oath_plus" -> sentinelOathPlus();
            case "quick_read" -> quickRead();
            case "quick_read_plus" -> quickReadPlus();
            case "ember_surge" -> emberSurge();
            case "ember_surge_plus" -> emberSurgePlus();
            default -> throw new IllegalArgumentException("Unknown card id: " + card.getId());
        };
    }

    private static Supplier<Card> upgradeSupplier(String id) {
        return switch (id) {
            case "ember_strike" -> CardFactory::emberStrikePlus;
            case "iron_curtain" -> CardFactory::ironCurtainPlus;
            case "swift_step" -> CardFactory::swiftStepPlus;
            case "dawn_breaker" -> CardFactory::dawnBreakerPlus;
            case "night_counter" -> CardFactory::nightCounterPlus;
            case "ember_guard" -> CardFactory::emberGuardPlus;
            case "ash_needle" -> CardFactory::ashNeedlePlus;
            case "shield_bash" -> CardFactory::shieldBashPlus;
            case "lantern_flare" -> CardFactory::lanternFlarePlus;
            case "deep_breath" -> CardFactory::deepBreathPlus;
            case "spark_focus" -> CardFactory::sparkFocusPlus;
            case "field_dressing" -> CardFactory::fieldDressingPlus;
            case "storm_of_cinders" -> CardFactory::stormOfCindersPlus;
            case "sentinel_oath" -> CardFactory::sentinelOathPlus;
            case "quick_read" -> CardFactory::quickReadPlus;
            case "ember_surge" -> CardFactory::emberSurgePlus;
            default -> null;
        };
    }

    private static Card card(
            String id,
            String name,
            int cost,
            CardType type,
            String description,
            CardEffect effect,
            boolean upgraded
    ) {
        return new Card(id, name, cost, type, description, effect, upgraded);
    }
}
