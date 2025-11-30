package legend.lodmod;

import legend.core.GameEngine;
import legend.game.additions.Addition;
import legend.game.additions.AdditionHitProperties10;
import legend.game.additions.AdditionRegistryEvent;
import legend.game.additions.AdditionSound;
import legend.game.additions.LevelLockedAddition;
import legend.game.additions.MasterAddition;
import legend.game.additions.SimpleAddition;
import legend.game.combat.AdditionConfigs;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

public final class LodAdditions {
  private LodAdditions() { }

  private static final Registrar<Addition, AdditionRegistryEvent> REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.additions, LodMod.MOD_ID);

  // Dart
  public static final RegistryDelegate<Addition> DOUBLE_SLASH = REGISTRAR.register("double_slash", AdditionConfigs::createAdditionHits_Dart_DoubleSlash);
  public static final RegistryDelegate<Addition> VOLCANO = REGISTRAR.register("volcano", AdditionConfigs::createAdditionHits_Dart_Volcano);
  public static final RegistryDelegate<Addition> BURNING_RUSH = REGISTRAR.register("burning_rush", AdditionConfigs::createAdditionHits_Dart_BurningRush);
  public static final RegistryDelegate<Addition> CRUSH_DANCE = REGISTRAR.register("crush_dance", AdditionConfigs::createAdditionHits_Dart_CrushDance);
  public static final RegistryDelegate<Addition> MADNESS_HERO = REGISTRAR.register("madness_hero", AdditionConfigs::createAdditionHits_Dart_MadnessHero);
  public static final RegistryDelegate<Addition> MOON_STRIKE = REGISTRAR.register("moon_strike", AdditionConfigs::createAdditionHits_Dart_MoonStrike);
  public static final RegistryDelegate<Addition> BLAZING_DYNAMO = REGISTRAR.register("blazing_dynamo", AdditionConfigs::createAdditionHits_Dart_BlazingDynamo);

  // Lavitz
  public static final RegistryDelegate<Addition> HARPOON = REGISTRAR.register("harpoon", AdditionConfigs::createAdditionHits_Lavitz_Harpoon);
  public static final RegistryDelegate<Addition> SPINNING_CANE = REGISTRAR.register("spinning_cane", AdditionConfigs::createAdditionHits_Lavitz_SpinningCane);
  public static final RegistryDelegate<Addition> ROD_TYPHOON = REGISTRAR.register("rod_typhoon", AdditionConfigs::createAdditionHits_Lavitz_RodTyphoon);
  public static final RegistryDelegate<Addition> GUST_OF_WIND_DANCE = REGISTRAR.register("gust_of_wind_dance", AdditionConfigs::createAdditionHits_Lavitz_GustOfWindDance);
  public static final RegistryDelegate<Addition> FLOWER_STORM = REGISTRAR.register("flower_storm", AdditionConfigs::createAdditionHits_Lavitz_FlowerStorm);

  // Rose
  public static final RegistryDelegate<Addition> WHIP_SMACK = REGISTRAR.register("whip_smack", AdditionConfigs::createAdditionHits_Rose_WhipSmack);
  public static final RegistryDelegate<Addition> MORE_MORE = REGISTRAR.register("more_more", AdditionConfigs::createAdditionHits_Rose_MoreAndMore);
  public static final RegistryDelegate<Addition> HARD_BLADE = REGISTRAR.register("hard_blade", AdditionConfigs::createAdditionHits_Rose_HardBlade);
  public static final RegistryDelegate<Addition> DEMONS_DANCE = REGISTRAR.register("demons_dance", AdditionConfigs::createAdditionHits_Rose_DemonsDance);

  // Haschel
  public static final RegistryDelegate<Addition> DOUBLE_PUNCH = REGISTRAR.register("double_punch", AdditionConfigs::createAdditionHits_Haschel_DoublePunch);
  public static final RegistryDelegate<Addition> FERRY_OF_STYX = REGISTRAR.register("ferry_of_styx", AdditionConfigs::createAdditionHits_Haschel_FlurryOfStyx);
  public static final RegistryDelegate<Addition> SUMMON_4_GODS = REGISTRAR.register("summon_4_gods", AdditionConfigs::createAdditionHits_Haschel_Summon4Gods);
  public static final RegistryDelegate<Addition> FIVE_RING_SHATTERING = REGISTRAR.register("five_ring_shattering", AdditionConfigs::createAdditionHits_Haschel_5RingShattering);
  public static final RegistryDelegate<Addition> HEX_HAMMER = REGISTRAR.register("hex_hammer", AdditionConfigs::createAdditionHits_Haschel_HexHammer);
  public static final RegistryDelegate<Addition> OMNI_SWEEP = REGISTRAR.register("omni_sweep", AdditionConfigs::createAdditionHits_Haschel_OmniSweep);

  // Albert
  public static final RegistryDelegate<Addition> ALBERT_HARPOON = REGISTRAR.register("albert_harpoon", AdditionConfigs::createAdditionHits_Albert_Harpoon);
  public static final RegistryDelegate<Addition> ALBERT_SPINNING_CANE = REGISTRAR.register("albert_spinning_cane", AdditionConfigs::createAdditionHits_Albert_SpinningCane);
  public static final RegistryDelegate<Addition> ALBERT_ROD_TYPHOON = REGISTRAR.register("albert_rod_typhoon", AdditionConfigs::createAdditionHits_Albert_RodTyphoon);
  public static final RegistryDelegate<Addition> ALBERT_GUST_OF_WIND_DANCE = REGISTRAR.register("albert_gust_of_wind_dance", AdditionConfigs::createAdditionHits_Albert_GustOfWindDance);
  public static final RegistryDelegate<Addition> ALBERT_FLOWER_STORM = REGISTRAR.register("albert_flower_storm", AdditionConfigs::createAdditionHits_Albert_FlowerStorm);

  // Meru
  public static final RegistryDelegate<Addition> DOUBLE_SMACK = REGISTRAR.register("double_smack", AdditionConfigs::createAdditionHits_Meru_DoubleSmack);
  public static final RegistryDelegate<Addition> HAMMER_SPIN = REGISTRAR.register("hammer_spin", AdditionConfigs::createAdditionHits_Meru_HammerSpin);
  public static final RegistryDelegate<Addition> COOL_BOOGIE = REGISTRAR.register("cool_boogie", AdditionConfigs::createAdditionHits_Meru_CoolBoogie);
  public static final RegistryDelegate<Addition> CATS_CRADLE = REGISTRAR.register("cats_cradle", AdditionConfigs::createAdditionHits_Meru_CatsCradle);
  public static final RegistryDelegate<Addition> PERKY_STEP = REGISTRAR.register("perky_step", AdditionConfigs::createAdditionHits_Meru_PerkyStep);

  // Kongol
  public static final RegistryDelegate<Addition> PURSUIT = REGISTRAR.register("pursuit", AdditionConfigs::createAdditionHits_Kongol_Pursuit);
  public static final RegistryDelegate<Addition> INFERNO = REGISTRAR.register("inferno", AdditionConfigs::createAdditionHits_Kongol_Inferno);
  public static final RegistryDelegate<Addition> BONE_CRUSH = REGISTRAR.register("bone_crush", AdditionConfigs::createAdditionHits_Kongol_BoneCrush);

  static void register(final AdditionRegistryEvent event) {
    REGISTRAR.registryEvent(event);
  }
}
