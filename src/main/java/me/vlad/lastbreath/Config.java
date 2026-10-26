package me.vlad.lastbreath;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue HARDCORE_ONLY = B.comment("Only new deaths in Hardcore become spirits. Existing spirits remain spirits.").define("hardcoreOnly", true);
    public static final ModConfigSpec.DoubleValue HEALTH_COST = B.comment("Health points paid by rescuer; 2 points = 1 heart. Must leave at least 1 point.").defineInRange("healthCost", 6.0, 0.0, 1000.0);
    public static final ModConfigSpec.DoubleValue REPEAT_COST = B.comment("Additional health cost for each previous revival of the target.").defineInRange("extraCostPerRevival", 0.0, 0.0, 100.0);
    public static final ModConfigSpec.DoubleValue RETURN_HEALTH = B.defineInRange("returnHealth", 8.0, 1.0, 1000.0);
    public static final ModConfigSpec.IntValue RESCUER_WEAKNESS = B.defineInRange("rescuerWeaknessSeconds", 30, 0, 3600);
    public static final ModConfigSpec.IntValue RETURN_WEAKNESS = B.defineInRange("returnedWeaknessSeconds", 60, 0, 3600);
    public static final ModConfigSpec.IntValue GRACE = B.comment("Short damage protection after revival.").defineInRange("graceSeconds", 5, 0, 60);
    public static final ModConfigSpec.BooleanValue GHOST_PARTICLES = B.comment("Subtle spirit particles.").define("ghostParticles", true);
    public static final ModConfigSpec.IntValue REVIVE_RADIUS = B.defineInRange("reviveRadius", 8, 1, 32);
    public static final ModConfigSpec.BooleanValue RITUAL_PARTICLES = B.define("ritualParticles", true);
    public static final ModConfigSpec.DoubleValue RITUAL_VOLUME = B.comment("Ritual sound multiplier; 0 mutes ritual sounds.").defineInRange("ritualVolume", 0.6, 0.0, 1.0);
    public static final ModConfigSpec.DoubleValue BLUE_CHANCE = B.defineInRange("peacefulSoulChance", .05, 0, 1);
    public static final ModConfigSpec.DoubleValue RED_CHANCE = B.defineInRange("monsterSoulChance", .05, 0, 1);
    public static final ModConfigSpec.DoubleValue PURPLE_CHANCE = B.defineInRange("bossSoulChance", 1.0, 0, 1);
    public static final ModConfigSpec.DoubleValue HARVEST_BONUS = B.defineInRange("harvestBonusPerRank", .025, 0, 1);
    public static final ModConfigSpec.BooleanValue REQUIRE_SWORD = B.define("requireExecutionerSword", true);
    public static final ModConfigSpec.ConfigValue<java.util.List<? extends String>> BOSSES = B.comment("Entity IDs. Boss category takes priority. Entity type tag lastbreath:bosses also supported.").defineListAllowEmpty("bossEntities", java.util.List.of("minecraft:ender_dragon", "minecraft:wither"), ()->"minecraft:wither", o->o instanceof String);
    public static final ModConfigSpec.ConfigValue<java.util.List<? extends String>> MONSTERS = B.comment("Additional monster entity IDs; vanilla Enemy mobs and lastbreath:monsters tag also count.").defineListAllowEmpty("monsterEntities", java.util.List.<String>of(), ()->"minecraft:zombie", o->o instanceof String);
    public static final ModConfigSpec.ConfigValue<java.util.List<? extends String>> EXCLUDED = B.defineListAllowEmpty("excludedSoulEntities", java.util.List.of("minecraft:armor_stand"), ()->"minecraft:armor_stand", o->o instanceof String);
    public static final ModConfigSpec.DoubleValue COST_SCALE = B.defineInRange("skillCostMultiplier", 1.0, .1, 100);
    public static final ModConfigSpec.BooleanValue TRIALS = B.comment("Final powers require a boss kill; Soul Bond also requires a successful rescue.").define("requireSkillTrials", true);
    public static final ModConfigSpec.BooleanValue LIVING_RITUAL = B.define("allowLivingRitual", true);
    public static final ModConfigSpec.IntValue RITUAL_SECONDS = B.defineInRange("tableChannelSeconds", 4, 1, 30);
    public static final ModConfigSpec.IntValue RITUAL_COST = B.defineInRange("tableRedSoulCost", 1, 0, 100);
    public static final ModConfigSpec.IntValue RITUAL_SESSION = B.defineInRange("tableSessionSeconds", 120, 10, 600);
    public static final ModConfigSpec.IntValue ABILITY_COOLDOWN = B.defineInRange("advancedAbilityCooldownSeconds", 60, 1, 3600);
    public static final ModConfigSpec.IntValue BORROW_SECONDS = B.defineInRange("borrowedTimeSeconds", 15, 1, 120);
    public static final ModConfigSpec.IntValue ECHO_SECONDS = B.defineInRange("echoSeconds", 10, 1, 60);
    public static final ModConfigSpec.IntValue BOND_SECONDS = B.defineInRange("bondSeconds", 30, 1, 300);
    public static final ModConfigSpec.IntValue BOND_RADIUS = B.defineInRange("bondRadius", 16, 2, 64);
    public static final ModConfigSpec.BooleanValue ECHO = B.define("enableEcho", true);
    public static final ModConfigSpec.BooleanValue BORROW = B.define("enableBorrowedTime", true);
    public static final ModConfigSpec.BooleanValue BOND = B.define("enableSoulBond", true);
    public static final ModConfigSpec.BooleanValue ANIMATIONS = B.define("animateRitualsAndSkills", true);
    public static final ModConfigSpec.BooleanValue GEAR_ENABLED = B.define("enableSoulGearPowers", true);
    public static final ModConfigSpec.IntValue GEAR_CAPACITY = B.defineInRange("gearChargeCapacity",100,100,30000);
    public static final ModConfigSpec.IntValue GEAR_BLUE = B.defineInRange("gearBlueSoulCharge",10,1,100);
    public static final ModConfigSpec.IntValue GEAR_RED = B.defineInRange("gearRedSoulCharge",25,1,100);
    public static final ModConfigSpec.IntValue GEAR_PURPLE = B.defineInRange("gearPurpleSoulCharge",100,1,100);
    public static final ModConfigSpec.DoubleValue GEAR_COST = B.defineInRange("gearUpgradeCostMultiplier",1.0,.1,100);
    public static final ModConfigSpec.DoubleValue GEAR_HARVEST_CHANCE = B.defineInRange("gearHarvestExtraChance",.10,0,1);
    public static final ModConfigSpec.IntValue GEAR_HARVEST_COST = B.defineInRange("gearHarvestChargeCost",5,1,100);
    public static final ModConfigSpec.DoubleValue GEAR_HEAL = B.defineInRange("gearHealHealth",2.0,0,20);
    public static final ModConfigSpec.IntValue GEAR_HEAL_COST = B.defineInRange("gearHealChargeCost",10,1,100);
    public static final ModConfigSpec.IntValue GEAR_HEAL_COOLDOWN = B.defineInRange("gearHealCooldownSeconds",10,1,600);
    public static final ModConfigSpec.DoubleValue GEAR_GUARD_THRESHOLD = B.defineInRange("gearGuardMinimumDamage",4.0,1,100);
    public static final ModConfigSpec.DoubleValue GEAR_GUARD_REDUCTION = B.defineInRange("gearGuardDamageReduction",.40,0,.9);
    public static final ModConfigSpec.IntValue GEAR_GUARD_COST = B.defineInRange("gearGuardChargeCost",15,1,100);
    public static final ModConfigSpec.IntValue GEAR_CLOAK_COOLDOWN = B.defineInRange("gearCloakCooldownSeconds",30,1,600);
    public static final ModConfigSpec.DoubleValue GEAR_SHROUD_HEALTH = B.defineInRange("gearShroudHealthThreshold",6.0,1,20);
    public static final ModConfigSpec.IntValue GEAR_SHROUD_COST = B.defineInRange("gearShroudChargeCost",20,1,100);
    public static final ModConfigSpec.IntValue GEAR_SHROUD_SECONDS = B.defineInRange("gearShroudDecoySeconds",5,1,30);
    public static final ModConfigSpec.IntValue TALENT_COOLDOWN=B.defineInRange("talentCooldownSeconds",25,1,600);
    public static final ModConfigSpec.IntValue GOLD_COOLDOWN=B.defineInRange("goldTalentCooldownSeconds",60,1,1200);
    public static final ModConfigSpec.IntValue SHRINE_SECONDS=B.defineInRange("shrineTimeLimitSeconds",120,20,1200);
    public static final ModConfigSpec.IntValue SHRINE_GUARDS=B.defineInRange("shrineGuardCount",3,1,12);
    public static final ModConfigSpec.IntValue ANCIENT_GUARDS=B.defineInRange("ancientShrineGuardCount",5,1,16);
    public static final ModConfigSpec.BooleanValue SURVIVAL_SPIRITS=B.comment("Allow spirit deaths in ordinary Survival, including worlds with the legacy hardcoreOnly setting enabled.").define("enableSurvivalSpirits",true);
    public static final ModConfigSpec.BooleanValue SPIRIT_DEATHS=B.comment("Controls new deaths only. Existing spirits can still be revived; admin commands remain available.").define("enableSpiritDeaths",true);
    public static final ModConfigSpec.BooleanValue SOUL_DROPS=B.define("enableSoulDrops",true);
    public static final ModConfigSpec.BooleanValue TALENTS_ENABLED=B.define("enableTalents",true);
    public static final ModConfigSpec.ConfigValue<java.util.List<? extends Integer>> DISABLED_TALENTS=B.comment("Talent IDs 0-27. Disabling preserves learned progress and prevents activation and passive bonuses.").defineListAllowEmpty("disabledTalents",java.util.List.<Integer>of(),()->0,o->o instanceof Integer n&&n>=0&&n<28);
    public static final ModConfigSpec.BooleanValue SELF_REVIVAL=B.define("enableTotemSelfRevival",true);
    public static final ModConfigSpec.BooleanValue ALLY_REVIVAL=B.define("enableAllyRevival",true);
    public static final ModConfigSpec.BooleanValue ALTAR_REVIVAL=B.define("enableAltarRevival",true);
    public static final ModConfigSpec.BooleanValue SHRINES=B.define("enableMemoryTrials",true);
    public static final ModConfigSpec.BooleanValue RUINS=B.comment("Affects generation in new chunks only; existing ruins are preserved.").define("generateMemoryRuins",true);
    public static final ModConfigSpec SPEC = B.build();
    /** Client previews may render before joining a server and receiving its synced settings. */
    public static <T> T value(ModConfigSpec.ConfigValue<T> value) {
        return SPEC.isLoaded() ? value.get() : value.getDefault();
    }
    private Config() {}
}
