package com.course.slay.domain.card;

import com.course.slay.domain.card.effects.AddKnifeCardsEffect;
import com.course.slay.domain.card.effects.AttackPlayedDamageEffect;
import com.course.slay.domain.card.effects.BattlePlayedDamageEffect;
import com.course.slay.domain.card.effects.BlockEffect;
import com.course.slay.domain.card.effects.CompositeEffect;
import com.course.slay.domain.card.effects.CurrentBlockDamageEffect;
import com.course.slay.domain.card.effects.DamageEffect;
import com.course.slay.domain.card.effects.DrawCardEffect;
import com.course.slay.domain.card.effects.EnergyPerDiscardEffect;
import com.course.slay.domain.card.effects.EnergyEffect;
import com.course.slay.domain.card.effects.HealthLossEffect;
import com.course.slay.domain.card.effects.HealEffect;
import com.course.slay.domain.card.effects.KnifeAtTurnStartEffect;
import com.course.slay.domain.card.effects.KnifeDamageBonusEffect;
import com.course.slay.domain.card.effects.KnifeDamageEffect;
import com.course.slay.domain.card.effects.KnifeDamageThisTurnEffect;
import com.course.slay.domain.card.effects.NextCardCostReductionEffect;
import com.course.slay.domain.card.effects.NextDamageToOneEffect;
import com.course.slay.domain.card.effects.OnDamageBuffEffect;
import com.course.slay.domain.card.effects.PiercingDamageEffect;
import com.course.slay.domain.card.effects.RepeatedDamageEffect;
import com.course.slay.domain.card.effects.RetainBlockEffect;
import com.course.slay.domain.card.effects.ScaleCurrentBlockEffect;
import com.course.slay.domain.card.effects.SkipEnemyTurnEffect;
import com.course.slay.domain.card.effects.StrengthEffect;
import com.course.slay.domain.card.effects.VulnerableEffect;

import java.util.List;
import java.util.function.Supplier;

public final class CardFactory {
    private static final List<Supplier<Card>> REWARD_SUPPLIERS = List.of(
            CardFactory::assassinBladeSurge,
            CardFactory::assassinVampiricStrike,
            CardFactory::assassinBackstab,
            CardFactory::assassinAdvance,
            CardFactory::assassinSilence,
            CardFactory::assassinTacticalMaster,
            CardFactory::assassinEnrage,
            CardFactory::assassinSacrifice,
            CardFactory::assassinPrecision,
            CardFactory::assassinInfiniteBlades,
            CardFactory::assassinFinisher
    );
    private static final List<Supplier<Card>> BERSERKER_REWARD_SUPPLIERS = List.of(
            CardFactory::berserkerBloodletting,
            CardFactory::berserkerVoid,
            CardFactory::berserkerPierce,
            CardFactory::berserkerTranquility,
            CardFactory::berserkerTacticalMaster,
            CardFactory::berserkerSilence,
            CardFactory::berserkerAshStrike,
            CardFactory::berserkerIronChop,
            CardFactory::berserkerImmovable
    );
    private CardFactory() {
    }

    public static Card berserkerStrike() {
        return card("berserker_strike", "打击", 1, CardType.ATTACK, CardRarity.COMMON, "造成 6 点伤害。",
                new DamageEffect(6), false);
    }

    public static Card berserkerStrikePlus() {
        return card("berserker_strike_plus", "打击+", 1, CardType.ATTACK, CardRarity.COMMON, "造成 9 点伤害。",
                new DamageEffect(9), true);
    }

    public static Card berserkerDefend() {
        return card("berserker_defend", "防御", 1, CardType.DEFENSE, CardRarity.COMMON, "获得 5 点格挡。",
                new BlockEffect(5), false);
    }

    public static Card berserkerDefendPlus() {
        return card("berserker_defend_plus", "防御+", 1, CardType.DEFENSE, CardRarity.COMMON, "获得 8 点格挡。",
                new BlockEffect(8), true);
    }

    public static Card berserkerComboPunch() {
        return card("berserker_combo_punch", "连续拳", 1, CardType.ATTACK, CardRarity.COMMON,
                "连续攻击 4 次，每次造成 2 点伤害。", new RepeatedDamageEffect(4, 2), false);
    }

    public static Card berserkerComboPunchPlus() {
        return card("berserker_combo_punch_plus", "连续拳+", 1, CardType.ATTACK, CardRarity.COMMON,
                "连续攻击 4 次，每次造成 3 点伤害。", new RepeatedDamageEffect(4, 3), true);
    }

    public static Card berserkerPerfectStrike() {
        return card("berserker_perfect_strike", "完美打击", 2, CardType.ATTACK, CardRarity.COMMON,
                "造成 16 点伤害。", new DamageEffect(16), false);
    }

    public static Card berserkerPerfectStrikePlus() {
        return card("berserker_perfect_strike_plus", "完美打击+", 2, CardType.ATTACK, CardRarity.COMMON,
                "造成 20 点伤害。", new DamageEffect(20), true);
    }

    public static Card berserkerBodySlam() {
        return card("berserker_body_slam", "全身撞击", 1, CardType.ATTACK, CardRarity.COMMON,
                "造成等同于当前格挡值的伤害。", new CurrentBlockDamageEffect(), false);
    }

    public static Card berserkerBodySlamPlus() {
        return card("berserker_body_slam_plus", "全身撞击+", 0, CardType.ATTACK, CardRarity.COMMON,
                "造成等同于当前格挡值的伤害。", new CurrentBlockDamageEffect(), true);
    }

    public static Card berserkerShelter() {
        return card("berserker_shelter", "庇护", 1, CardType.DEFENSE, CardRarity.COMMON,
                "获得 9 点格挡。", new BlockEffect(9), false);
    }

    public static Card berserkerShelterPlus() {
        return card("berserker_shelter_plus", "庇护+", 1, CardType.DEFENSE, CardRarity.COMMON,
                "获得 12 点格挡。", new BlockEffect(12), true);
    }

    public static Card berserkerEnrage() {
        return card("berserker_enrage", "激怒", 1, CardType.BUFF, CardRarity.RARE,
                "每当本场战斗受到伤害时，获得 1 点力量和 4 点格挡。",
                new OnDamageBuffEffect(1, 4), false);
    }

    public static Card berserkerEnragePlus() {
        return card("berserker_enrage_plus", "激怒+", 1, CardType.BUFF, CardRarity.RARE,
                "每当本场战斗受到伤害时，获得 2 点力量和 4 点格挡。",
                new OnDamageBuffEffect(2, 4), true);
    }

    public static Card berserkerBash() {
        return card("berserker_bash", "痛击", 1, CardType.ATTACK, CardRarity.COMMON,
                "造成 5 点伤害，附加 1 层易伤。",
                CompositeEffect.of(new DamageEffect(5), new VulnerableEffect(1)), false);
    }

    public static Card berserkerBashPlus() {
        return card("berserker_bash_plus", "痛击+", 1, CardType.ATTACK, CardRarity.COMMON,
                "造成 8 点伤害，附加 1 层易伤。",
                CompositeEffect.of(new DamageEffect(8), new VulnerableEffect(1)), true);
    }

    public static Card berserkerBloodletting() {
        return card("berserker_bloodletting", "放血", 0, CardType.BUFF, CardRarity.COMMON,
                "获得 2 点能量，失去 3 点生命。",
                CompositeEffect.of(new EnergyEffect(2), new HealthLossEffect(3)), false);
    }

    public static Card berserkerBloodlettingPlus() {
        return card("berserker_bloodletting_plus", "放血+", 0, CardType.BUFF, CardRarity.COMMON,
                "获得 3 点能量，失去 3 点生命。",
                CompositeEffect.of(new EnergyEffect(3), new HealthLossEffect(3)), true);
    }

    public static Card berserkerVoid() {
        return card("berserker_void", "虚无", 2, CardType.DEFENSE, CardRarity.COMMON,
                "下一次受到的伤害变为 1 点。", new NextDamageToOneEffect(), false);
    }

    public static Card berserkerVoidPlus() {
        return card("berserker_void_plus", "虚无+", 1, CardType.DEFENSE, CardRarity.COMMON,
                "下一次受到的伤害变为 1 点。", new NextDamageToOneEffect(), true);
    }

    public static Card berserkerPierce() {
        return card("berserker_pierce", "穿刺", 1, CardType.ATTACK, CardRarity.COMMON,
                "无视格挡造成 6 点伤害。", new PiercingDamageEffect(6), false);
    }

    public static Card berserkerPiercePlus() {
        return card("berserker_pierce_plus", "穿刺+", 1, CardType.ATTACK, CardRarity.COMMON,
                "无视格挡造成 9 点伤害。", new PiercingDamageEffect(9), true);
    }

    public static Card berserkerTranquility() {
        return card("berserker_tranquility", "平和", 1, CardType.BUFF, CardRarity.COMMON,
                "获得 3 点格挡，下一张牌能量消耗减少 1 点。",
                CompositeEffect.of(new BlockEffect(3), new NextCardCostReductionEffect(1)), false);
    }

    public static Card berserkerTranquilityPlus() {
        return card("berserker_tranquility_plus", "平和+", 1, CardType.BUFF, CardRarity.COMMON,
                "获得 5 点格挡，下一张牌能量消耗减少 1 点。",
                CompositeEffect.of(new BlockEffect(5), new NextCardCostReductionEffect(1)), true);
    }

    public static Card berserkerTacticalMaster() {
        return card("berserker_tactical_master", "战术大师", 1, CardType.BUFF, CardRarity.RARE,
                "本回合每弃 1 张牌，获得 1 点能量。", new EnergyPerDiscardEffect(1), false);
    }

    public static Card berserkerTacticalMasterPlus() {
        return card("berserker_tactical_master_plus", "战术大师+", 1, CardType.BUFF, CardRarity.RARE,
                "本回合每弃 1 张牌，获得 2 点能量。", new EnergyPerDiscardEffect(2), true);
    }

    public static Card berserkerSilence() {
        return card("berserker_silence", "沉默", 3, CardType.DEBUFF, CardRarity.LEGENDARY,
                "跳过下一次敌方回合。", new SkipEnemyTurnEffect(), false);
    }

    public static Card berserkerSilencePlus() {
        return card("berserker_silence_plus", "沉默+", 2, CardType.DEBUFF, CardRarity.LEGENDARY,
                "跳过下一次敌方回合。", new SkipEnemyTurnEffect(), true);
    }

    public static Card berserkerAshStrike() {
        return card("berserker_ash_strike", "灰烬打击", 1, CardType.ATTACK, CardRarity.RARE,
                "造成 5 点伤害，本场战斗每打出过 1 张牌，额外造成 3 点伤害。",
                new BattlePlayedDamageEffect(5, 3), false);
    }

    public static Card berserkerAshStrikePlus() {
        return card("berserker_ash_strike_plus", "灰烬打击+", 1, CardType.ATTACK, CardRarity.RARE,
                "造成 8 点伤害，本场战斗每打出过 1 张牌，额外造成 3 点伤害。",
                new BattlePlayedDamageEffect(8, 3), true);
    }

    public static Card berserkerIronChop() {
        return card("berserker_iron_chop", "铁斩波", 1, CardType.ATTACK, CardRarity.COMMON,
                "造成 5 点伤害，获得 5 点格挡。",
                CompositeEffect.of(new DamageEffect(5), new BlockEffect(5)), false);
    }

    public static Card berserkerIronChopPlus() {
        return card("berserker_iron_chop_plus", "铁斩波+", 1, CardType.ATTACK, CardRarity.COMMON,
                "造成 7 点伤害，获得 7 点格挡。",
                CompositeEffect.of(new DamageEffect(7), new BlockEffect(7)), true);
    }

    public static Card berserkerImmovable() {
        return card("berserker_immovable", "岿然不动", 2, CardType.DEFENSE, CardRarity.LEGENDARY,
                "获得 30 点格挡，本回合格挡不会消散。",
                CompositeEffect.of(new BlockEffect(30), new RetainBlockEffect()), false);
    }

    public static Card berserkerImmovablePlus() {
        return card("berserker_immovable_plus", "岿然不动+", 2, CardType.DEFENSE, CardRarity.LEGENDARY,
                "获得 40 点格挡，本回合格挡不会消散。",
                CompositeEffect.of(new BlockEffect(40), new RetainBlockEffect()), true);
    }

    public static Card assassinStrike() {
        return card("assassin_strike", "打击", 1, CardType.ATTACK, CardRarity.COMMON, "造成 6 点伤害。",
                new DamageEffect(6), false);
    }

    public static Card assassinStrikePlus() {
        return card("assassin_strike_plus", "打击+", 1, CardType.ATTACK, CardRarity.COMMON, "造成 9 点伤害。",
                new DamageEffect(9), true);
    }

    public static Card assassinDefend() {
        return card("assassin_defend", "防御", 1, CardType.DEFENSE, CardRarity.COMMON, "获得 5 点格挡。",
                new BlockEffect(5), false);
    }

    public static Card assassinDefendPlus() {
        return card("assassin_defend_plus", "防御+", 1, CardType.DEFENSE, CardRarity.COMMON, "获得 8 点格挡。",
                new BlockEffect(8), true);
    }

    public static Card assassinComboPunch() {
        return card("assassin_combo_punch", "连续拳", 1, CardType.ATTACK, CardRarity.COMMON,
                "连续攻击 4 次，每次造成 2 点伤害。", new RepeatedDamageEffect(4, 2), false);
    }

    public static Card assassinComboPunchPlus() {
        return card("assassin_combo_punch_plus", "连续拳+", 1, CardType.ATTACK, CardRarity.COMMON,
                "连续攻击 4 次，每次造成 3 点伤害。", new RepeatedDamageEffect(4, 3), true);
    }

    public static Card assassinPerfectStrike() {
        return card("assassin_perfect_strike", "完美打击", 2, CardType.ATTACK, CardRarity.COMMON,
                "造成 16 点伤害。", new DamageEffect(16), false);
    }

    public static Card assassinPerfectStrikePlus() {
        return card("assassin_perfect_strike_plus", "完美打击+", 2, CardType.ATTACK, CardRarity.COMMON,
                "造成 20 点伤害。", new DamageEffect(20), true);
    }

    public static Card assassinBodySlam() {
        return card("assassin_body_slam", "全身撞击", 1, CardType.ATTACK, CardRarity.COMMON,
                "造成等同于当前格挡值的伤害。", new CurrentBlockDamageEffect(), false);
    }

    public static Card assassinBodySlamPlus() {
        return card("assassin_body_slam_plus", "全身撞击+", 0, CardType.ATTACK, CardRarity.COMMON,
                "造成等同于当前格挡值的伤害。", new CurrentBlockDamageEffect(), true);
    }

    public static Card assassinEntrench() {
        return card("assassin_entrench", "固守", 1, CardType.DEFENSE, CardRarity.COMMON,
                "当前格挡翻倍。", new ScaleCurrentBlockEffect(1), false);
    }

    public static Card assassinEntrenchPlus() {
        return card("assassin_entrench_plus", "固守+", 1, CardType.DEFENSE, CardRarity.COMMON,
                "当前格挡变为三倍。", new ScaleCurrentBlockEffect(2), true);
    }

    public static Card assassinBash() {
        return card("assassin_bash", "痛击", 1, CardType.ATTACK, CardRarity.COMMON,
                "造成 5 点伤害，附加 1 层易伤。",
                CompositeEffect.of(new DamageEffect(5), new VulnerableEffect(1)), false);
    }

    public static Card assassinBashPlus() {
        return card("assassin_bash_plus", "痛击+", 1, CardType.ATTACK, CardRarity.COMMON,
                "造成 8 点伤害，附加 1 层易伤。",
                CompositeEffect.of(new DamageEffect(8), new VulnerableEffect(1)), true);
    }

    public static Card assassinBladeDance() {
        return card("assassin_blade_dance", "刀刃之舞", 1, CardType.ATTACK, CardRarity.COMMON,
                "获得 3 张小刀牌。", new AddKnifeCardsEffect(3), false);
    }

    public static Card assassinBladeDancePlus() {
        return card("assassin_blade_dance_plus", "刀刃之舞+", 1, CardType.ATTACK, CardRarity.COMMON,
                "获得 4 张小刀牌。", new AddKnifeCardsEffect(4), true);
    }

    public static Card assassinKnife() {
        return card("assassin_knife", "小刀", 0, CardType.ATTACK, CardRarity.SPECIAL,
                "造成 3 点伤害。", new KnifeDamageEffect(3), false);
    }

    public static Card assassinBladeSurge() {
        return card("assassin_blade_surge", "锋涌", 2, CardType.ATTACK, CardRarity.COMMON,
                "造成 10 点伤害，抽 2 张牌。",
                CompositeEffect.of(new DamageEffect(10), new DrawCardEffect(2)), false);
    }

    public static Card assassinBladeSurgePlus() {
        return card("assassin_blade_surge_plus", "锋涌+", 2, CardType.ATTACK, CardRarity.COMMON,
                "造成 14 点伤害，抽 2 张牌。",
                CompositeEffect.of(new DamageEffect(14), new DrawCardEffect(2)), true);
    }

    public static Card assassinVampiricStrike() {
        return card("assassin_vampiric_strike", "吸血攻击", 1, CardType.ATTACK, CardRarity.COMMON,
                "造成 6 点伤害，恢复 4 点生命。",
                CompositeEffect.of(new DamageEffect(6), new HealEffect(4)), false);
    }

    public static Card assassinVampiricStrikePlus() {
        return card("assassin_vampiric_strike_plus", "吸血攻击+", 1, CardType.ATTACK, CardRarity.COMMON,
                "造成 9 点伤害，恢复 5 点生命。",
                CompositeEffect.of(new DamageEffect(9), new HealEffect(5)), true);
    }

    public static Card assassinBackstab() {
        return card("assassin_backstab", "背刺", 0, CardType.ATTACK, CardRarity.RARE,
                "造成 6 点伤害；若本回合已打出攻击牌，伤害翻倍。",
                new AttackPlayedDamageEffect(6), false);
    }

    public static Card assassinBackstabPlus() {
        return card("assassin_backstab_plus", "背刺+", 0, CardType.ATTACK, CardRarity.RARE,
                "造成 8 点伤害；若本回合已打出攻击牌，伤害翻倍。",
                new AttackPlayedDamageEffect(8), true);
    }

    public static Card assassinAdvance() {
        return card("assassin_advance", "进阶", 1, CardType.BUFF, CardRarity.RARE,
                "永久获得 1 点力量。", new StrengthEffect(1, true), false);
    }

    public static Card assassinAdvancePlus() {
        return card("assassin_advance_plus", "进阶+", 1, CardType.BUFF, CardRarity.RARE,
                "永久获得 2 点力量。", new StrengthEffect(2, true), true);
    }

    public static Card assassinSilence() {
        return card("assassin_silence", "沉默", 2, CardType.DEBUFF, CardRarity.LEGENDARY,
                "跳过下一次敌方回合。", new SkipEnemyTurnEffect(), false);
    }

    public static Card assassinSilencePlus() {
        return card("assassin_silence_plus", "沉默+", 1, CardType.DEBUFF, CardRarity.LEGENDARY,
                "跳过下一次敌方回合。", new SkipEnemyTurnEffect(), true);
    }

    public static Card assassinTacticalMaster() {
        return card("assassin_tactical_master", "战术大师", 1, CardType.BUFF, CardRarity.RARE,
                "本回合每弃 1 张牌，获得 1 点能量。", new EnergyPerDiscardEffect(1), false);
    }

    public static Card assassinTacticalMasterPlus() {
        return card("assassin_tactical_master_plus", "战术大师+", 1, CardType.BUFF, CardRarity.RARE,
                "本回合每弃 1 张牌，获得 2 点能量。", new EnergyPerDiscardEffect(2), true);
    }

    public static Card assassinEnrage() {
        return card("assassin_enrage", "激怒", 1, CardType.BUFF, CardRarity.RARE,
                "每当本场战斗受到伤害时，获得 1 点力量和 4 点格挡。",
                new OnDamageBuffEffect(1, 4), false);
    }

    public static Card assassinEnragePlus() {
        return card("assassin_enrage_plus", "激怒+", 1, CardType.BUFF, CardRarity.RARE,
                "每当本场战斗受到伤害时，获得 2 点力量和 4 点格挡。",
                new OnDamageBuffEffect(2, 4), true);
    }

    public static Card assassinSacrifice() {
        return card("assassin_sacrifice", "献祭", 0, CardType.BUFF, CardRarity.RARE,
                "失去 6 点生命，抽 3 张牌，获得 1 点能量。",
                CompositeEffect.of(new HealthLossEffect(6), new DrawCardEffect(3), new EnergyEffect(1)), false);
    }

    public static Card assassinSacrificePlus() {
        return card("assassin_sacrifice_plus", "献祭+", 0, CardType.BUFF, CardRarity.RARE,
                "失去 6 点生命，抽 4 张牌，获得 1 点能量。",
                CompositeEffect.of(new HealthLossEffect(6), new DrawCardEffect(4), new EnergyEffect(1)), true);
    }

    public static Card assassinPrecision() {
        return card("assassin_precision", "精准", 1, CardType.BUFF, CardRarity.RARE,
                "本场战斗中，每张小刀牌伤害提升 4 点。", new KnifeDamageBonusEffect(4), false);
    }

    public static Card assassinPrecisionPlus() {
        return card("assassin_precision_plus", "精准+", 1, CardType.BUFF, CardRarity.RARE,
                "本场战斗中，每张小刀牌伤害提升 6 点。", new KnifeDamageBonusEffect(6), true);
    }

    public static Card assassinInfiniteBlades() {
        return card("assassin_infinite_blades", "无限之刃", 1, CardType.BUFF, CardRarity.LEGENDARY,
                "每回合开始自动获得 1 张小刀牌。", new KnifeAtTurnStartEffect(1), false);
    }

    public static Card assassinInfiniteBladesPlus() {
        return card("assassin_infinite_blades_plus", "无限之刃+", 1, CardType.BUFF, CardRarity.LEGENDARY,
                "每回合开始自动获得 2 张小刀牌。", new KnifeAtTurnStartEffect(2), true);
    }

    public static Card assassinFinisher() {
        return card("assassin_finisher", "终结技", 2, CardType.BUFF, CardRarity.RARE,
                "本回合所有小刀牌伤害变为 6 点。", new KnifeDamageThisTurnEffect(6), false);
    }

    public static Card assassinFinisherPlus() {
        return card("assassin_finisher_plus", "终结技+", 2, CardType.BUFF, CardRarity.RARE,
                "本回合所有小刀牌伤害变为 9 点。", new KnifeDamageThisTurnEffect(9), true);
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

    public static Card bloodletting() {
        return card("bloodletting", "放血", 0, CardType.TACTIC, "获得 2 点能量，失去 3 点生命。",
                CompositeEffect.of(new EnergyEffect(2), new HealthLossEffect(3)), false);
    }

    public static Card bloodlettingPlus() {
        return card("bloodletting_plus", "放血+", 0, CardType.TACTIC, "获得 3 点能量，失去 3 点生命。",
                CompositeEffect.of(new EnergyEffect(3), new HealthLossEffect(3)), true);
    }

    public static Card comboPunch() {
        return card("combo_punch", "连续拳", 1, CardType.ATTACK, "连续攻击 4 次，每次造成 2 点伤害。",
                new RepeatedDamageEffect(4, 2), false);
    }

    public static Card comboPunchPlus() {
        return card("combo_punch_plus", "连续拳+", 1, CardType.ATTACK, "连续攻击 4 次，每次造成 3 点伤害。",
                new RepeatedDamageEffect(4, 3), true);
    }

    public static Card perfectStrike() {
        return card("perfect_strike", "完美打击", 2, CardType.ATTACK, "造成 16 点伤害。",
                new DamageEffect(16), false);
    }

    public static Card perfectStrikePlus() {
        return card("perfect_strike_plus", "完美打击+", 2, CardType.ATTACK, "造成 20 点伤害。",
                new DamageEffect(20), true);
    }

    public static Card tacticalMaster() {
        return card("tactical_master", "战术大师", 1, CardType.TACTIC, "本回合每弃 1 张牌，获得 1 点能量。",
                new EnergyPerDiscardEffect(1), false);
    }

    public static Card tacticalMasterPlus() {
        return card("tactical_master_plus", "战术大师+", 1, CardType.TACTIC, "本回合每弃 1 张牌，获得 2 点能量。",
                new EnergyPerDiscardEffect(2), true);
    }

    public static Card silence() {
        return card("silence", "沉默", 2, CardType.TACTIC, "跳过下一次敌方回合。",
                new SkipEnemyTurnEffect(), false);
    }

    public static Card silencePlus() {
        return card("silence_plus", "沉默+", 1, CardType.TACTIC, "跳过下一次敌方回合。",
                new SkipEnemyTurnEffect(), true);
    }

    public static Card bodySlam() {
        return card("body_slam", "全身撞击", 1, CardType.ATTACK, "造成等同于当前格挡值的伤害。",
                new CurrentBlockDamageEffect(), false);
    }

    public static Card bodySlamPlus() {
        return card("body_slam_plus", "全身撞击+", 0, CardType.ATTACK, "造成等同于当前格挡值的伤害。",
                new CurrentBlockDamageEffect(), true);
    }

    public static Card entrench() {
        return card("entrench", "固守", 1, CardType.SKILL, "当前格挡翻倍。",
                new ScaleCurrentBlockEffect(1), false);
    }

    public static Card entrenchPlus() {
        return card("entrench_plus", "固守+", 1, CardType.SKILL, "当前格挡变为三倍。",
                new ScaleCurrentBlockEffect(2), true);
    }

    public static Card enrage() {
        return card("enrage", "激怒", 1, CardType.TACTIC, "每当本场战斗受到伤害时，获得 1 点力量和 4 点格挡。",
                new OnDamageBuffEffect(1, 4), false);
    }

    public static Card enragePlus() {
        return card("enrage_plus", "激怒+", 1, CardType.TACTIC, "每当本场战斗受到伤害时，获得 2 点力量和 4 点格挡。",
                new OnDamageBuffEffect(2, 4), true);
    }

    public static Card bladeSurge() {
        return card("blade_surge", "锋涌", 2, CardType.ATTACK, "造成 10 点伤害，抽 2 张牌。",
                CompositeEffect.of(new DamageEffect(10), new DrawCardEffect(2)), false);
    }

    public static Card bladeSurgePlus() {
        return card("blade_surge_plus", "锋涌+", 2, CardType.ATTACK, "造成 14 点伤害，抽 2 张牌。",
                CompositeEffect.of(new DamageEffect(14), new DrawCardEffect(2)), true);
    }

    public static Card vampiricStrike() {
        return card("vampiric_strike", "吸血攻击", 1, CardType.ATTACK, "造成 6 点伤害，恢复 4 点生命。",
                CompositeEffect.of(new DamageEffect(6), new HealEffect(4)), false);
    }

    public static Card vampiricStrikePlus() {
        return card("vampiric_strike_plus", "吸血攻击+", 1, CardType.ATTACK, "造成 9 点伤害，恢复 5 点生命。",
                CompositeEffect.of(new DamageEffect(9), new HealEffect(5)), true);
    }

    public static Card backstab() {
        return card("backstab", "背刺", 0, CardType.ATTACK, "造成 6 点伤害；若本回合已打出攻击牌，伤害翻倍。",
                new AttackPlayedDamageEffect(6), false);
    }

    public static Card backstabPlus() {
        return card("backstab_plus", "背刺+", 0, CardType.ATTACK, "造成 8 点伤害；若本回合已打出攻击牌，伤害翻倍。",
                new AttackPlayedDamageEffect(8), true);
    }

    public static Card advance() {
        return card("advance", "进阶", 1, CardType.TACTIC, "永久获得 1 点力量。",
                new StrengthEffect(1, true), false);
    }

    public static Card advancePlus() {
        return card("advance_plus", "进阶+", 1, CardType.TACTIC, "永久获得 2 点力量。",
                new StrengthEffect(2, true), true);
    }

    public static List<Card> rewardPool() {
        return REWARD_SUPPLIERS.stream()
                .map(Supplier::get)
                .toList();
    }

    public static List<Card> berserkerRewardPool() {
        return BERSERKER_REWARD_SUPPLIERS.stream()
                .map(Supplier::get)
                .toList();
    }

    public static List<Card> assassinRewardPool() {
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
            case "berserker_strike" -> berserkerStrike();
            case "berserker_strike_plus" -> berserkerStrikePlus();
            case "berserker_defend" -> berserkerDefend();
            case "berserker_defend_plus" -> berserkerDefendPlus();
            case "berserker_combo_punch" -> berserkerComboPunch();
            case "berserker_combo_punch_plus" -> berserkerComboPunchPlus();
            case "berserker_perfect_strike" -> berserkerPerfectStrike();
            case "berserker_perfect_strike_plus" -> berserkerPerfectStrikePlus();
            case "berserker_body_slam" -> berserkerBodySlam();
            case "berserker_body_slam_plus" -> berserkerBodySlamPlus();
            case "berserker_shelter" -> berserkerShelter();
            case "berserker_shelter_plus" -> berserkerShelterPlus();
            case "berserker_enrage" -> berserkerEnrage();
            case "berserker_enrage_plus" -> berserkerEnragePlus();
            case "berserker_bash" -> berserkerBash();
            case "berserker_bash_plus" -> berserkerBashPlus();
            case "berserker_bloodletting" -> berserkerBloodletting();
            case "berserker_bloodletting_plus" -> berserkerBloodlettingPlus();
            case "berserker_void" -> berserkerVoid();
            case "berserker_void_plus" -> berserkerVoidPlus();
            case "berserker_pierce" -> berserkerPierce();
            case "berserker_pierce_plus" -> berserkerPiercePlus();
            case "berserker_tranquility" -> berserkerTranquility();
            case "berserker_tranquility_plus" -> berserkerTranquilityPlus();
            case "berserker_tactical_master" -> berserkerTacticalMaster();
            case "berserker_tactical_master_plus" -> berserkerTacticalMasterPlus();
            case "berserker_silence" -> berserkerSilence();
            case "berserker_silence_plus" -> berserkerSilencePlus();
            case "berserker_ash_strike" -> berserkerAshStrike();
            case "berserker_ash_strike_plus" -> berserkerAshStrikePlus();
            case "berserker_iron_chop" -> berserkerIronChop();
            case "berserker_iron_chop_plus" -> berserkerIronChopPlus();
            case "berserker_immovable" -> berserkerImmovable();
            case "berserker_immovable_plus" -> berserkerImmovablePlus();
            case "assassin_strike" -> assassinStrike();
            case "assassin_strike_plus" -> assassinStrikePlus();
            case "assassin_defend" -> assassinDefend();
            case "assassin_defend_plus" -> assassinDefendPlus();
            case "assassin_combo_punch" -> assassinComboPunch();
            case "assassin_combo_punch_plus" -> assassinComboPunchPlus();
            case "assassin_perfect_strike" -> assassinPerfectStrike();
            case "assassin_perfect_strike_plus" -> assassinPerfectStrikePlus();
            case "assassin_body_slam" -> assassinBodySlam();
            case "assassin_body_slam_plus" -> assassinBodySlamPlus();
            case "assassin_entrench" -> assassinEntrench();
            case "assassin_entrench_plus" -> assassinEntrenchPlus();
            case "assassin_bash" -> assassinBash();
            case "assassin_bash_plus" -> assassinBashPlus();
            case "assassin_blade_dance" -> assassinBladeDance();
            case "assassin_blade_dance_plus" -> assassinBladeDancePlus();
            case "assassin_knife" -> assassinKnife();
            case "assassin_blade_surge" -> assassinBladeSurge();
            case "assassin_blade_surge_plus" -> assassinBladeSurgePlus();
            case "assassin_vampiric_strike" -> assassinVampiricStrike();
            case "assassin_vampiric_strike_plus" -> assassinVampiricStrikePlus();
            case "assassin_backstab" -> assassinBackstab();
            case "assassin_backstab_plus" -> assassinBackstabPlus();
            case "assassin_advance" -> assassinAdvance();
            case "assassin_advance_plus" -> assassinAdvancePlus();
            case "assassin_silence" -> assassinSilence();
            case "assassin_silence_plus" -> assassinSilencePlus();
            case "assassin_tactical_master" -> assassinTacticalMaster();
            case "assassin_tactical_master_plus" -> assassinTacticalMasterPlus();
            case "assassin_enrage" -> assassinEnrage();
            case "assassin_enrage_plus" -> assassinEnragePlus();
            case "assassin_sacrifice" -> assassinSacrifice();
            case "assassin_sacrifice_plus" -> assassinSacrificePlus();
            case "assassin_precision" -> assassinPrecision();
            case "assassin_precision_plus" -> assassinPrecisionPlus();
            case "assassin_infinite_blades" -> assassinInfiniteBlades();
            case "assassin_infinite_blades_plus" -> assassinInfiniteBladesPlus();
            case "assassin_finisher" -> assassinFinisher();
            case "assassin_finisher_plus" -> assassinFinisherPlus();
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
            case "bloodletting" -> bloodletting();
            case "bloodletting_plus" -> bloodlettingPlus();
            case "combo_punch" -> comboPunch();
            case "combo_punch_plus" -> comboPunchPlus();
            case "perfect_strike" -> perfectStrike();
            case "perfect_strike_plus" -> perfectStrikePlus();
            case "tactical_master" -> tacticalMaster();
            case "tactical_master_plus" -> tacticalMasterPlus();
            case "silence" -> silence();
            case "silence_plus" -> silencePlus();
            case "body_slam" -> bodySlam();
            case "body_slam_plus" -> bodySlamPlus();
            case "entrench" -> entrench();
            case "entrench_plus" -> entrenchPlus();
            case "enrage" -> enrage();
            case "enrage_plus" -> enragePlus();
            case "blade_surge" -> bladeSurge();
            case "blade_surge_plus" -> bladeSurgePlus();
            case "vampiric_strike" -> vampiricStrike();
            case "vampiric_strike_plus" -> vampiricStrikePlus();
            case "backstab" -> backstab();
            case "backstab_plus" -> backstabPlus();
            case "advance" -> advance();
            case "advance_plus" -> advancePlus();
            default -> throw new IllegalArgumentException("Unknown card id: " + card.getId());
        };
    }

    private static Supplier<Card> upgradeSupplier(String id) {
        return switch (id) {
            case "berserker_strike" -> CardFactory::berserkerStrikePlus;
            case "berserker_defend" -> CardFactory::berserkerDefendPlus;
            case "berserker_combo_punch" -> CardFactory::berserkerComboPunchPlus;
            case "berserker_perfect_strike" -> CardFactory::berserkerPerfectStrikePlus;
            case "berserker_body_slam" -> CardFactory::berserkerBodySlamPlus;
            case "berserker_shelter" -> CardFactory::berserkerShelterPlus;
            case "berserker_enrage" -> CardFactory::berserkerEnragePlus;
            case "berserker_bash" -> CardFactory::berserkerBashPlus;
            case "berserker_bloodletting" -> CardFactory::berserkerBloodlettingPlus;
            case "berserker_void" -> CardFactory::berserkerVoidPlus;
            case "berserker_pierce" -> CardFactory::berserkerPiercePlus;
            case "berserker_tranquility" -> CardFactory::berserkerTranquilityPlus;
            case "berserker_tactical_master" -> CardFactory::berserkerTacticalMasterPlus;
            case "berserker_silence" -> CardFactory::berserkerSilencePlus;
            case "berserker_ash_strike" -> CardFactory::berserkerAshStrikePlus;
            case "berserker_iron_chop" -> CardFactory::berserkerIronChopPlus;
            case "berserker_immovable" -> CardFactory::berserkerImmovablePlus;
            case "assassin_strike" -> CardFactory::assassinStrikePlus;
            case "assassin_defend" -> CardFactory::assassinDefendPlus;
            case "assassin_combo_punch" -> CardFactory::assassinComboPunchPlus;
            case "assassin_perfect_strike" -> CardFactory::assassinPerfectStrikePlus;
            case "assassin_body_slam" -> CardFactory::assassinBodySlamPlus;
            case "assassin_entrench" -> CardFactory::assassinEntrenchPlus;
            case "assassin_bash" -> CardFactory::assassinBashPlus;
            case "assassin_blade_dance" -> CardFactory::assassinBladeDancePlus;
            case "assassin_blade_surge" -> CardFactory::assassinBladeSurgePlus;
            case "assassin_vampiric_strike" -> CardFactory::assassinVampiricStrikePlus;
            case "assassin_backstab" -> CardFactory::assassinBackstabPlus;
            case "assassin_advance" -> CardFactory::assassinAdvancePlus;
            case "assassin_silence" -> CardFactory::assassinSilencePlus;
            case "assassin_tactical_master" -> CardFactory::assassinTacticalMasterPlus;
            case "assassin_enrage" -> CardFactory::assassinEnragePlus;
            case "assassin_sacrifice" -> CardFactory::assassinSacrificePlus;
            case "assassin_precision" -> CardFactory::assassinPrecisionPlus;
            case "assassin_infinite_blades" -> CardFactory::assassinInfiniteBladesPlus;
            case "assassin_finisher" -> CardFactory::assassinFinisherPlus;
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
            case "bloodletting" -> CardFactory::bloodlettingPlus;
            case "combo_punch" -> CardFactory::comboPunchPlus;
            case "perfect_strike" -> CardFactory::perfectStrikePlus;
            case "tactical_master" -> CardFactory::tacticalMasterPlus;
            case "silence" -> CardFactory::silencePlus;
            case "body_slam" -> CardFactory::bodySlamPlus;
            case "entrench" -> CardFactory::entrenchPlus;
            case "enrage" -> CardFactory::enragePlus;
            case "blade_surge" -> CardFactory::bladeSurgePlus;
            case "vampiric_strike" -> CardFactory::vampiricStrikePlus;
            case "backstab" -> CardFactory::backstabPlus;
            case "advance" -> CardFactory::advancePlus;
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
        return new Card(id, name, cost, type, rarityFor(id), description, effect, upgraded);
    }

    private static Card card(
            String id,
            String name,
            int cost,
            CardType type,
            CardRarity rarity,
            String description,
            CardEffect effect,
            boolean upgraded
    ) {
        return new Card(id, name, cost, type, rarity, description, effect, upgraded);
    }

    private static CardRarity rarityFor(String id) {
        String baseId = id.endsWith("_plus") ? id.substring(0, id.length() - "_plus".length()) : id;
        return switch (baseId) {
            case "ember_strike", "iron_curtain", "swift_step", "ash_needle", "deep_breath",
                    "bloodletting", "combo_punch", "perfect_strike", "body_slam", "entrench",
                    "blade_surge", "vampiric_strike" -> CardRarity.COMMON;
            case "dawn_breaker", "night_counter", "ember_guard", "shield_bash", "lantern_flare",
                    "field_dressing", "quick_read", "spark_focus", "ember_surge",
                    "tactical_master", "enrage", "backstab", "advance" -> CardRarity.UNCOMMON;
            case "storm_of_cinders", "sentinel_oath", "silence" -> CardRarity.RARE;
            default -> CardRarity.COMMON;
        };
    }
}
