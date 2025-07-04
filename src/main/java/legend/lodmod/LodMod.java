package legend.lodmod;

import legend.core.GameEngine;
import legend.core.platform.input.AxisInputActivation;
import legend.core.platform.input.ButtonInputActivation;
import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputActionRegistryEvent;
import legend.core.platform.input.InputAxis;
import legend.core.platform.input.InputAxisDirection;
import legend.core.platform.input.InputButton;
import legend.core.platform.input.InputKey;
import legend.core.platform.input.KeyInputActivation;
import legend.core.platform.input.ScancodeInputActivation;
import legend.game.characters.Element;
import legend.game.characters.ElementRegistryEvent;
import legend.game.characters.FractionalStat;
import legend.game.characters.FractionalStatMod;
import legend.game.characters.FractionalStatModConfig;
import legend.game.characters.FractionalStatModType;
import legend.game.characters.StatModType;
import legend.game.characters.StatModTypeRegistryEvent;
import legend.game.characters.StatType;
import legend.game.characters.StatTypeRegistryEvent;
import legend.game.characters.UnaryStat;
import legend.game.characters.UnaryStatMod;
import legend.game.characters.UnaryStatModConfig;
import legend.game.characters.UnaryStatModType;
import legend.game.characters.VitalsStat;
import legend.game.combat.bent.BattleEntityType;
import legend.game.combat.bent.BattleEntityTypeRegistryEvent;
import legend.game.combat.deff.RegisterDeffsEvent;
import legend.game.inventory.Equipment;
import legend.game.inventory.EquipmentRegistryEvent;
import legend.game.inventory.IconMapEvent;
import legend.game.inventory.IconSet;
import legend.game.inventory.ItemIcon;
import legend.game.inventory.ItemRegistryEvent;
import legend.game.inventory.ShopRegistryEvent;
import legend.game.inventory.SpellRegistryEvent;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.coremod.elements.DarkElement;
import legend.game.modding.coremod.elements.DivineElement;
import legend.game.modding.coremod.elements.EarthElement;
import legend.game.modding.coremod.elements.FireElement;
import legend.game.modding.coremod.elements.LightElement;
import legend.game.modding.coremod.elements.NoElement;
import legend.game.modding.coremod.elements.ThunderElement;
import legend.game.modding.coremod.elements.WaterElement;
import legend.game.modding.coremod.elements.WindElement;
import legend.game.modding.events.battle.RegisterBattleEntityStatsEvent;
import legend.game.modding.events.gamestate.NewGameEvent;
import legend.game.modding.events.input.RegisterDefaultInputBindingsEvent;
import legend.game.modding.events.inventory.GatherAttackItemsEvent;
import legend.game.modding.events.inventory.GatherRecoveryItemsEvent;
import legend.game.types.EquipmentSlot;
import legend.game.types.SpellStats0c;
import legend.game.unpacker.Loader;
import org.legendofdragoon.modloader.Mod;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.Map;

import static legend.core.GameEngine.CONFIG;
import static legend.game.Scus94491BpeSegment_8005.spellCombatDescriptions_80052018;
import static legend.game.Scus94491BpeSegment_8005.spells_80052734;
import static legend.game.combat.Battle.spellStats_800fa0b8;

/** Will eventually contain standard LOD content. Will be able to be disabled for total overhaul mods. */
@Mod(id = LodMod.MOD_ID, version = "^3.0.0")
@EventListener
public class LodMod {
  public static final String MOD_ID = "lod";

  public static RegistryId id(final String entryId) {
    return new RegistryId(MOD_ID, entryId);
  }

  private static final Registrar<InputAction, InputActionRegistryEvent> INPUT_ACTION_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.inputActions, MOD_ID);

  public static final RegistryDelegate<InputAction> INPUT_ACTION_GENERAL_OPEN_INVENTORY = INPUT_ACTION_REGISTRAR.register("general_open_inventory", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_GENERAL_MOVE_UP = INPUT_ACTION_REGISTRAR.register("general_move_up", InputAction.make().visible().editable().useMovementDeadzone().build());
  public static final RegistryDelegate<InputAction> INPUT_ACTION_GENERAL_MOVE_DOWN = INPUT_ACTION_REGISTRAR.register("general_move_down", InputAction.make().visible().editable().useMovementDeadzone().build());
  public static final RegistryDelegate<InputAction> INPUT_ACTION_GENERAL_MOVE_LEFT = INPUT_ACTION_REGISTRAR.register("general_move_left", InputAction.make().visible().editable().useMovementDeadzone().build());
  public static final RegistryDelegate<InputAction> INPUT_ACTION_GENERAL_MOVE_RIGHT = INPUT_ACTION_REGISTRAR.register("general_move_right", InputAction.make().visible().editable().useMovementDeadzone().build());
  public static final RegistryDelegate<InputAction> INPUT_ACTION_GENERAL_RUN = INPUT_ACTION_REGISTRAR.register("general_run", InputAction::editable);

  public static final RegistryDelegate<InputAction> INPUT_ACTION_WMAP_ROTATE_RIGHT = INPUT_ACTION_REGISTRAR.register("wmap_rotate_right", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_WMAP_ROTATE_LEFT = INPUT_ACTION_REGISTRAR.register("wmap_rotate_left", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_WMAP_ZOOM_OUT = INPUT_ACTION_REGISTRAR.register("wmap_zoom_out", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_WMAP_ZOOM_IN = INPUT_ACTION_REGISTRAR.register("wmap_zoom_in", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_WMAP_COOLON = INPUT_ACTION_REGISTRAR.register("wmap_coolon", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_WMAP_QUEEN_FURY = INPUT_ACTION_REGISTRAR.register("wmap_queen_fury", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_WMAP_SERVICES = INPUT_ACTION_REGISTRAR.register("wmap_services", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_WMAP_DESTINATIONS = INPUT_ACTION_REGISTRAR.register("wmap_destinations", InputAction::editable);

  public static final RegistryDelegate<InputAction> INPUT_ACTION_SMAP_INTERACT = INPUT_ACTION_REGISTRAR.register("smap_interact", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_SMAP_TOGGLE_INDICATORS = INPUT_ACTION_REGISTRAR.register("smap_toggle_indicators", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_SMAP_SNOWFIELD_WARP = INPUT_ACTION_REGISTRAR.register("smap_snowfield_warp", InputAction::hidden);

  public static final RegistryDelegate<InputAction> INPUT_ACTION_BTTL_ATTACK = INPUT_ACTION_REGISTRAR.register("bttl_attack", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_BTTL_COUNTER = INPUT_ACTION_REGISTRAR.register("bttl_counter", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_BTTL_ROTATE_CAMERA = INPUT_ACTION_REGISTRAR.register("bttl_rotate_camera", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_BTTL_ADDITIONS = INPUT_ACTION_REGISTRAR.register("bttl_additions", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_BTTL_TRANSFORM = INPUT_ACTION_REGISTRAR.register("bttl_transform", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_BTTL_SPECIAL = INPUT_ACTION_REGISTRAR.register("bttl_special", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_BTTL_ESCAPE = INPUT_ACTION_REGISTRAR.register("bttl_escape", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_BTTL_GUARD = INPUT_ACTION_REGISTRAR.register("bttl_guard", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_BTTL_ITEMS = INPUT_ACTION_REGISTRAR.register("bttl_items", InputAction::editable);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_BTTL_OPTIONS = INPUT_ACTION_REGISTRAR.register("bttl_options", InputAction::editable);

  private static final Registrar<StatType<?>, StatTypeRegistryEvent> STAT_TYPE_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.statTypes, MOD_ID);
  public static final RegistryDelegate<StatType<VitalsStat>> HP_STAT = STAT_TYPE_REGISTRAR.register("hp", () -> new StatType<>(VitalsStat::new));
  public static final RegistryDelegate<StatType<VitalsStat>> MP_STAT = STAT_TYPE_REGISTRAR.register("mp", () -> new StatType<>(VitalsStat::new));
  public static final RegistryDelegate<StatType<VitalsStat>> SP_STAT = STAT_TYPE_REGISTRAR.register("sp", () -> new StatType<>(VitalsStat::new));

  public static final RegistryDelegate<StatType<UnaryStat>> SPEED_STAT = STAT_TYPE_REGISTRAR.register("speed", () -> new StatType<>(UnaryStat::new));

  private static final Registrar<StatModType<?, ?, ?>, StatModTypeRegistryEvent> STAT_MOD_TYPE_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.statModTypes, MOD_ID);
  public static final RegistryDelegate<StatModType<UnaryStat, UnaryStatMod, UnaryStatModConfig>> UNARY_STAT_MOD_TYPE = STAT_MOD_TYPE_REGISTRAR.register("unary", UnaryStatModType::new);
  public static final RegistryDelegate<StatModType<FractionalStat, FractionalStatMod, FractionalStatModConfig>> FRACTIONAL_STAT_MOD_TYPE = STAT_MOD_TYPE_REGISTRAR.register("fractional", FractionalStatModType::new);

  private static final Registrar<Element, ElementRegistryEvent> ELEMENT_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.elements, MOD_ID);
  public static final RegistryDelegate<Element> NO_ELEMENT = ELEMENT_REGISTRAR.register("none", NoElement::new);
  public static final RegistryDelegate<Element> WATER_ELEMENT = ELEMENT_REGISTRAR.register("water", WaterElement::new);
  public static final RegistryDelegate<Element> EARTH_ELEMENT = ELEMENT_REGISTRAR.register("earth", EarthElement::new);
  public static final RegistryDelegate<Element> DARK_ELEMENT = ELEMENT_REGISTRAR.register("dark", DarkElement::new);
  public static final RegistryDelegate<Element> DIVINE_ELEMENT = ELEMENT_REGISTRAR.register("divine", DivineElement::new);
  public static final RegistryDelegate<Element> THUNDER_ELEMENT = ELEMENT_REGISTRAR.register("thunder", ThunderElement::new);
  public static final RegistryDelegate<Element> LIGHT_ELEMENT = ELEMENT_REGISTRAR.register("light", LightElement::new);
  public static final RegistryDelegate<Element> WIND_ELEMENT = ELEMENT_REGISTRAR.register("wind", WindElement::new);
  public static final RegistryDelegate<Element> FIRE_ELEMENT = ELEMENT_REGISTRAR.register("fire", FireElement::new);

  private static final Registrar<BattleEntityType, BattleEntityTypeRegistryEvent> BENT_TYPE_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.battleEntityTypes, MOD_ID);
  public static final RegistryDelegate<BattleEntityType> PLAYER_TYPE = BENT_TYPE_REGISTRAR.register("player", BattleEntityType::new);
  public static final RegistryDelegate<BattleEntityType> MONSTER_TYPE = BENT_TYPE_REGISTRAR.register("monster", BattleEntityType::new);

  public static final String[] ITEM_IDS = {
    "", "detonate_rock", "spark_net", "burn_out", "", "pellet", "spear_frost", "spinning_gale",
    "attack_ball", "trans_light", "dark_mist", "healing_potion", "depetrifier", "mind_purifier", "body_purifier", "thunderbolt",
    "meteor_fall", "gushing_magma", "dancing_ray", "spirit_potion", "panic_bell", "", "fatal_blizzard", "stunning_hammer",
    "black_rain", "poison_needle", "midnight_terror", "", "rave_twister", "total_vanishing", "angels_prayer", "charm_potion",
    "pandemonium", "recovery_ball", "", "magic_shield", "material_shield", "sun_rhapsody", "smoke_ball", "healing_fog",
    "magic_sig_stone", "healing_rain", "moon_serenade", "power_up", "power_down", "speed_up", "speed_down", "enemy_healing_potion",
    "sachet", "psyche_bomb", "burning_wave", "frozen_jet", "down_burst", "gravity_grabber", "spectral_flash", "night_raid",
    "flash_hall", "healing_breeze", "psyche_bomb_x", "", "", "", "", ""
  };

  public static final String[] EQUIPMENT_IDS = {
    "broad_sword", "bastard_sword", "heat_blade", "falchion", "mind_crush", "fairy_sword", "claymore", "soul_eater",
    "axe", "tomahawk", "battle_axe", "great_axe", "indoras_axe", "rapier", "shadow_cutter", "dancing_dagger",
    "flamberge", "gladius", "dragon_buster", "demon_stiletto", "spear", "lance", "glaive", "spear_of_terror",
    "partisan", "halberd", "twister_glaive", "short_bow", "sparkle_arrow", "long_bow", "bemusing_arrow", "virulent_arrow",
    "detonate_arrow", "arrow_of_force", "mace", "morning_star", "war_hammer", "heavy_mace", "basher", "pretty_hammer",
    "iron_knuckle", "beast_fang", "diamond_claw", "thunder_fist", "destroyer_mace", "brass_knuckle", "leather_armor", "scale_armor",
    "chain_mail", "plate_mail", "saint_armor", "red_dg_armor", "jade_dg_armor", "lion_fur", "breast_plate", "giganto_armor",
    "gold_dg_armor", "disciple_vest", "warrior_dress", "masters_vest", "energy_girdle", "violet_dg_armor", "clothes", "leather_jacket",
    "silver_vest", "sparkle_dress", "robe", "silver_dg_armor", "dark_dg_armor", "blue_dg_armor", "armor_of_yore", "satori_vest",
    "rainbow_dress", "angel_robe", "armor_of_legend", "", "bandana", "sallet", "armet", "knight_helm",
    "giganto_helm", "soul_headband", "felt_hat", "cape", "tiara", "jeweled_crown", "roses_hair_band", "",
    "phoenix_plume", "legend_casque", "dragon_helm", "magical_hat", "", "leather_boots", "iron_kneepiece", "combat_shoes",
    "leather_shoes", "soft_boots", "stardust_boots", "magical_greaves", "dancers_shoes", "bandits_shoes", "", "poison_guard",
    "active_ring", "protector", "panic_guard", "stun_guard", "bravery_amulet", "magic_ego_bell", "destone_amulet", "power_wrist",
    "knight_shield", "magical_ring", "spiritual_ring", "attack_badge", "guard_badge", "giganto_ring", "elude_cloak", "spirit_cloak",
    "sages_cloak", "physical_ring", "amulet", "wargods_sash", "spirit_ring", "therapy_ring", "mage_ring", "wargods_amulet",
    "talisman", "", "holy_ankh", "dancers_ring", "", "bandits_ring", "red_eye_stone", "jade_stone",
    "silver_stone", "darkness_stone", "blue_sea_stone", "violet_stone", "golden_stone", "", "ruby_ring", "sapphire_pin",
    "rainbow_earring", "", "emerald_earring", "", "platinum_collar", "phantom_shield", "dragon_shield", "angel_scarf",
    "bracelet", "fake_power_wrist", "fake_shield", "", "wargod_calling", "ultimate_wargod", "", "",
    "", "", "", "", "", "", "", "",
    "", "", "", "", "", "", "", "",
    "", "", "", "", "", "", "", "",
    "", "", "", "", "", "", "", ""
  };

  public static final String[] SHOP_IDS = {
    "bale_equipment_shop", "serdio_item_shop", "lohan_equipment_shop", "lohan_item_shop",
    "kazas_equipment_shop", "kazas_fort_item_shop", "fletz_equipment_shop", "fletz_item_shop",
    "donau_equipment_shop", "donau_item_shop", "queen_fury_equipment_shop", "queen_fury_item_shop",
    "fueno_equipment_shop", "fueno_item_shop", "furni_equipment_shop", "furni_item_shop",
    "deningrad_equipment_shop", "deningrad_item_shop", "wingly_forest_equipment_shop", "wingly_forest_item_shop",
    "vellweb_equipment_shop", "vellweb_item_shop", "ulara_equipment_shop", "ulara_item_shop",
    "rouge_equipment_shop", "rouge_item_shop", "moon_equipment_shop", "moon_item_shop",
    "hellena_01_item_shop", "kashua_equipment_shop", "kashua_item_shop", "fletz_accessory_shop",
    "forest_item_shop", "kazas_fort_equipment_shop", "volcano_item_shop", "zenebatos_equipment_shop",
    "zenebatos_item_shop", "hellena_02_item_shop", "unknown_shop_01", "empty_shop", "empty_shop", "empty_shop"
    , "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop"
    , "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop",
    "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop", "empty_shop",
  };

  @EventListener
  public static void registerInputActions(final InputActionRegistryEvent event) {
    INPUT_ACTION_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public static void registerDefaultInputBindings(final RegisterDefaultInputBindingsEvent event) {
    event
      .add(INPUT_ACTION_GENERAL_OPEN_INVENTORY.get(), new ScancodeInputActivation(InputKey.E))
      .add(INPUT_ACTION_GENERAL_OPEN_INVENTORY.get(), new ButtonInputActivation(InputButton.Y))

      .add(INPUT_ACTION_GENERAL_MOVE_UP.get(), new ButtonInputActivation(InputButton.DPAD_UP))
      .add(INPUT_ACTION_GENERAL_MOVE_UP.get(), new AxisInputActivation(InputAxis.LEFT_Y, InputAxisDirection.NEGATIVE))
      .add(INPUT_ACTION_GENERAL_MOVE_UP.get(), new ScancodeInputActivation(InputKey.UP))
      .add(INPUT_ACTION_GENERAL_MOVE_UP.get(), new ScancodeInputActivation(InputKey.W))
      .add(INPUT_ACTION_GENERAL_MOVE_DOWN.get(), new ButtonInputActivation(InputButton.DPAD_DOWN))
      .add(INPUT_ACTION_GENERAL_MOVE_DOWN.get(), new AxisInputActivation(InputAxis.LEFT_Y, InputAxisDirection.POSITIVE))
      .add(INPUT_ACTION_GENERAL_MOVE_DOWN.get(), new ScancodeInputActivation(InputKey.DOWN))
      .add(INPUT_ACTION_GENERAL_MOVE_DOWN.get(), new ScancodeInputActivation(InputKey.S))
      .add(INPUT_ACTION_GENERAL_MOVE_LEFT.get(), new ButtonInputActivation(InputButton.DPAD_LEFT))
      .add(INPUT_ACTION_GENERAL_MOVE_LEFT.get(), new AxisInputActivation(InputAxis.LEFT_X, InputAxisDirection.NEGATIVE))
      .add(INPUT_ACTION_GENERAL_MOVE_LEFT.get(), new ScancodeInputActivation(InputKey.LEFT))
      .add(INPUT_ACTION_GENERAL_MOVE_LEFT.get(), new ScancodeInputActivation(InputKey.A))
      .add(INPUT_ACTION_GENERAL_MOVE_RIGHT.get(), new ButtonInputActivation(InputButton.DPAD_RIGHT))
      .add(INPUT_ACTION_GENERAL_MOVE_RIGHT.get(), new AxisInputActivation(InputAxis.LEFT_X, InputAxisDirection.POSITIVE))
      .add(INPUT_ACTION_GENERAL_MOVE_RIGHT.get(), new ScancodeInputActivation(InputKey.RIGHT))
      .add(INPUT_ACTION_GENERAL_MOVE_RIGHT.get(), new ScancodeInputActivation(InputKey.D))
      .add(INPUT_ACTION_GENERAL_RUN.get(), new ScancodeInputActivation(InputKey.LEFT_SHIFT))
      .add(INPUT_ACTION_GENERAL_RUN.get(), new ButtonInputActivation(InputButton.B))

      .add(INPUT_ACTION_WMAP_ROTATE_LEFT.get(), new ScancodeInputActivation(InputKey.R))
      .add(INPUT_ACTION_WMAP_ROTATE_LEFT.get(), new ButtonInputActivation(InputButton.LEFT_BUMPER))
      .add(INPUT_ACTION_WMAP_ROTATE_RIGHT.get(), new ScancodeInputActivation(InputKey.T))
      .add(INPUT_ACTION_WMAP_ROTATE_RIGHT.get(), new ButtonInputActivation(InputButton.RIGHT_BUMPER))
      .add(INPUT_ACTION_WMAP_ZOOM_OUT.get(), new ScancodeInputActivation(InputKey.Z))
      .add(INPUT_ACTION_WMAP_ZOOM_OUT.get(), new AxisInputActivation(InputAxis.LEFT_TRIGGER, InputAxisDirection.POSITIVE))
      .add(INPUT_ACTION_WMAP_ZOOM_IN.get(), new ScancodeInputActivation(InputKey.X))
      .add(INPUT_ACTION_WMAP_ZOOM_IN.get(), new AxisInputActivation(InputAxis.RIGHT_TRIGGER, InputAxisDirection.POSITIVE))
      .add(INPUT_ACTION_WMAP_COOLON.get(), new ScancodeInputActivation(InputKey.Q))
      .add(INPUT_ACTION_WMAP_COOLON.get(), new ButtonInputActivation(InputButton.X))
      .add(INPUT_ACTION_WMAP_QUEEN_FURY.get(), new ScancodeInputActivation(InputKey.Q))
      .add(INPUT_ACTION_WMAP_QUEEN_FURY.get(), new ButtonInputActivation(InputButton.X))
      .add(INPUT_ACTION_WMAP_SERVICES.get(), new ScancodeInputActivation(InputKey.Q))
      .add(INPUT_ACTION_WMAP_SERVICES.get(), new ButtonInputActivation(InputButton.X))
      .add(INPUT_ACTION_WMAP_DESTINATIONS.get(), new KeyInputActivation(InputKey.L))
      .add(INPUT_ACTION_WMAP_DESTINATIONS.get(), new ButtonInputActivation(InputButton.START))

      .add(INPUT_ACTION_SMAP_INTERACT.get(), new ScancodeInputActivation(InputKey.SPACE))
      .add(INPUT_ACTION_SMAP_INTERACT.get(), new ScancodeInputActivation(InputKey.RETURN))
      .add(INPUT_ACTION_SMAP_INTERACT.get(), new ButtonInputActivation(InputButton.A))
      .add(INPUT_ACTION_SMAP_TOGGLE_INDICATORS.get(), new ScancodeInputActivation(InputKey.Q))
      .add(INPUT_ACTION_SMAP_TOGGLE_INDICATORS.get(), new ButtonInputActivation(InputButton.RIGHT_BUMPER))
      .add(INPUT_ACTION_SMAP_SNOWFIELD_WARP.get(), new ButtonInputActivation(InputButton.START))

      .add(INPUT_ACTION_BTTL_ATTACK.get(), new ScancodeInputActivation(InputKey.SPACE))
      .add(INPUT_ACTION_BTTL_ATTACK.get(), new ButtonInputActivation(InputButton.A))
      .add(INPUT_ACTION_BTTL_COUNTER.get(), new ScancodeInputActivation(InputKey.LEFT_SHIFT))
      .add(INPUT_ACTION_BTTL_COUNTER.get(), new ButtonInputActivation(InputButton.B))
      .add(INPUT_ACTION_BTTL_ROTATE_CAMERA.get(), new ScancodeInputActivation(InputKey.Q))
      .add(INPUT_ACTION_BTTL_ROTATE_CAMERA.get(), new ButtonInputActivation(InputButton.RIGHT_BUMPER))
      .add(INPUT_ACTION_BTTL_ADDITIONS.get(), new ScancodeInputActivation(InputKey.E))
      .add(INPUT_ACTION_BTTL_ADDITIONS.get(), new ButtonInputActivation(InputButton.Y))
      .add(INPUT_ACTION_BTTL_OPTIONS.get(), new ScancodeInputActivation(InputKey.ESCAPE))
      .add(INPUT_ACTION_BTTL_OPTIONS.get(), new ButtonInputActivation(InputButton.START))
    ;
  }

  @EventListener
  public static void registerItems(final ItemRegistryEvent event) {
    LodItems.register(event);
  }

  @EventListener
  public static void registerEquipment(final EquipmentRegistryEvent event) {
    LodEquipment.register(event);
  }

  @EventListener
  public static void registerShops(final ShopRegistryEvent event) {
    LodShops.register(event);
  }

  @EventListener
  public static void registerSpells(final SpellRegistryEvent event) {
    for(int spellId = 0; spellId < spellStats_800fa0b8.length; spellId++) {
      if(spellStats_800fa0b8[spellId] == null) {
        final String name = spellId < 84 ? spells_80052734[spellId] : "Spell " + spellId;
        final String desc = spellId < 84 ? spellCombatDescriptions_80052018[spellId] : "";
        spellStats_800fa0b8[spellId] = SpellStats0c.fromFile(name, desc, Loader.loadFile("spells/" + spellId + ".dspl"));
      }
    }
  }

  @EventListener
  public static void registerStatTypes(final StatTypeRegistryEvent event) {
    STAT_TYPE_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public static void registerStatModTypes(final StatModTypeRegistryEvent event) {
    STAT_MOD_TYPE_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public static void registerElements(final ElementRegistryEvent event) {
    ELEMENT_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public static void registerBentTypes(final BattleEntityTypeRegistryEvent event) {
    BENT_TYPE_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public static void registerBentStats(final RegisterBattleEntityStatsEvent event) {
    event.addStat(HP_STAT.get());

    if(event.type == PLAYER_TYPE.get()) {
      event.addStat(MP_STAT.get());
      event.addStat(SP_STAT.get());
    }

    event.addStat(SPEED_STAT.get());
  }

  @EventListener
  public static void registerDeffs(final RegisterDeffsEvent event) {
    LodDeffs.register(event);
  }

  @EventListener
  public static void gatherAttackItems(final GatherAttackItemsEvent event) {
    event.add(LodItems.SPARK_NET.get());
    event.add(LodItems.BURN_OUT.get());
    event.add(LodItems.PELLET.get());
    event.add(LodItems.SPEAR_FROST.get());
    event.add(LodItems.SPINNING_GALE.get());
    event.add(LodItems.TRANS_LIGHT.get());
    event.add(LodItems.DARK_MIST.get());
    event.add(LodItems.PANIC_BELL.get());
    event.add(LodItems.STUNNING_HAMMER.get());
    event.add(LodItems.POISON_NEEDLE.get());
    event.add(LodItems.MIDNIGHT_TERROR.get());
    event.add(LodItems.THUNDERBOLT.get());
    event.add(LodItems.METEOR_FALL.get());
    event.add(LodItems.GUSHING_MAGMA.get());
    event.add(LodItems.DANCING_RAY.get());
    event.add(LodItems.FATAL_BLIZZARD.get());
    event.add(LodItems.BLACK_RAIN.get());
    event.add(LodItems.RAVE_TWISTER.get());
    event.add(LodItems.BURNING_WAVE.get());
    event.add(LodItems.FROZEN_JET.get());
    event.add(LodItems.DOWN_BURST.get());
    event.add(LodItems.GRAVITY_GRABBER.get());
    event.add(LodItems.SPECTRAL_FLASH.get());
    event.add(LodItems.NIGHT_RAID.get());
    event.add(LodItems.FLASH_HALL.get());
  }

  @EventListener
  public static void gatherRecoveryItems(final GatherRecoveryItemsEvent event) {
    event.add(LodItems.SPIRIT_POTION.get());
    event.add(LodItems.SUN_RHAPSODY.get());
    event.add(LodItems.HEALING_POTION.get());
    event.add(LodItems.HEALING_FOG.get());
    event.add(LodItems.MOON_SERENADE.get());
    event.add(LodItems.HEALING_RAIN.get());
    event.add(LodItems.HEALING_BREEZE.get());
  }

  @EventListener
  public static void newGame(final NewGameEvent event) {
    event.gameState.items_2e9.add(LodItems.BURN_OUT.get());
    event.gameState.items_2e9.add(LodItems.HEALING_POTION.get());
    event.gameState.items_2e9.add(LodItems.HEALING_POTION.get());

    final Map<EquipmentSlot, Equipment> dart = event.gameState.charData_32c[0].equipment_14;
    dart.put(EquipmentSlot.WEAPON, LodEquipment.BROAD_SWORD.get());
    dart.put(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
    dart.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
    dart.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_BOOTS.get());
    dart.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

    final Map<EquipmentSlot, Equipment> lavitz = event.gameState.charData_32c[1].equipment_14;
    lavitz.put(EquipmentSlot.WEAPON, LodEquipment.SPEAR.get());
    lavitz.put(EquipmentSlot.HELMET, LodEquipment.SALLET.get());
    lavitz.put(EquipmentSlot.ARMOUR, LodEquipment.SCALE_ARMOR.get());
    lavitz.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_BOOTS.get());
    lavitz.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

    final Map<EquipmentSlot, Equipment> shana = event.gameState.charData_32c[2].equipment_14;
    shana.put(EquipmentSlot.WEAPON, LodEquipment.SHORT_BOW.get());
    shana.put(EquipmentSlot.HELMET, LodEquipment.FELT_HAT.get());
    shana.put(EquipmentSlot.ARMOUR, LodEquipment.CLOTHES.get());
    shana.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_SHOES.get());
    shana.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

    final Map<EquipmentSlot, Equipment> rose = event.gameState.charData_32c[3].equipment_14;
    rose.put(EquipmentSlot.WEAPON, LodEquipment.RAPIER.get());
    rose.put(EquipmentSlot.HELMET, LodEquipment.FELT_HAT.get());
    rose.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_JACKET.get());
    rose.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_SHOES.get());
    rose.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

    final Map<EquipmentSlot, Equipment> haschel = event.gameState.charData_32c[4].equipment_14;
    haschel.put(EquipmentSlot.WEAPON, LodEquipment.IRON_KNUCKLE.get());
    haschel.put(EquipmentSlot.HELMET, LodEquipment.ARMET.get());
    haschel.put(EquipmentSlot.ARMOUR, LodEquipment.DISCIPLE_VEST.get());
    haschel.put(EquipmentSlot.BOOTS, LodEquipment.IRON_KNEEPIECE.get());
    haschel.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

    final Map<EquipmentSlot, Equipment> albert = event.gameState.charData_32c[5].equipment_14;
    albert.put(EquipmentSlot.WEAPON, LodEquipment.SPEAR.get());
    albert.put(EquipmentSlot.HELMET, LodEquipment.SALLET.get());
    albert.put(EquipmentSlot.ARMOUR, LodEquipment.SCALE_ARMOR.get());
    albert.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_BOOTS.get());
    albert.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

    final Map<EquipmentSlot, Equipment> meru = event.gameState.charData_32c[6].equipment_14;
    meru.put(EquipmentSlot.WEAPON, LodEquipment.MACE.get());
    meru.put(EquipmentSlot.HELMET, LodEquipment.TIARA.get());
    meru.put(EquipmentSlot.ARMOUR, LodEquipment.SILVER_VEST.get());
    meru.put(EquipmentSlot.BOOTS, LodEquipment.SOFT_BOOTS.get());
    meru.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

    final Map<EquipmentSlot, Equipment> kongol = event.gameState.charData_32c[7].equipment_14;
    kongol.put(EquipmentSlot.WEAPON, LodEquipment.AXE.get());
    kongol.put(EquipmentSlot.HELMET, LodEquipment.ARMET.get());
    kongol.put(EquipmentSlot.ARMOUR, LodEquipment.LION_FUR.get());
    kongol.put(EquipmentSlot.BOOTS, LodEquipment.IRON_KNEEPIECE.get());
    kongol.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

    final Map<EquipmentSlot, Equipment> miranda = event.gameState.charData_32c[8].equipment_14;
    miranda.put(EquipmentSlot.WEAPON, LodEquipment.SHORT_BOW.get());
    miranda.put(EquipmentSlot.HELMET, LodEquipment.FELT_HAT.get());
    miranda.put(EquipmentSlot.ARMOUR, LodEquipment.CLOTHES.get());
    miranda.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_SHOES.get());
    miranda.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

    event.gameState.gold_94 = 20;
  }

  @EventListener
  public static void createIconMapping(final IconMapEvent event) {
    if(CONFIG.getConfig(CoreMod.ICON_SET.get()) == IconSet.RETAIL) {
      // Remap all the expanded icons to retail icons
      event.addMapping(ItemIcon.AXE, ItemIcon.SWORD);
      event.addMapping(ItemIcon.HAMMER, ItemIcon.SWORD);
      event.addMapping(ItemIcon.SPEAR, ItemIcon.SWORD);
      event.addMapping(ItemIcon.BOW, ItemIcon.SWORD);
      event.addMapping(ItemIcon.MACE, ItemIcon.SWORD);
      event.addMapping(ItemIcon.KNUCKLE, ItemIcon.SWORD);
      event.addMapping(ItemIcon.BOXING_GLOVE, ItemIcon.SWORD);

      event.addMapping(ItemIcon.CLOTHES, ItemIcon.ARMOR);
      event.addMapping(ItemIcon.ROBE, ItemIcon.ARMOR);
      event.addMapping(ItemIcon.BREASTPLATE, ItemIcon.ARMOR);
      event.addMapping(ItemIcon.RED_DRESS, ItemIcon.ARMOR);
      event.addMapping(ItemIcon.LOINCLOTH, ItemIcon.ARMOR);
      event.addMapping(ItemIcon.WARRIOR_DRESS, ItemIcon.ARMOR);

      event.addMapping(ItemIcon.CAPE, ItemIcon.HELM);
      event.addMapping(ItemIcon.CROWN, ItemIcon.HELM);
      event.addMapping(ItemIcon.HAIRBAND, ItemIcon.HELM);
      event.addMapping(ItemIcon.BANDANA, ItemIcon.HELM);
      event.addMapping(ItemIcon.HAT, ItemIcon.HELM);

      event.addMapping(ItemIcon.SHOES, ItemIcon.BOOTS);
      event.addMapping(ItemIcon.KNEEPIECE, ItemIcon.BOOTS);

      event.addMapping(ItemIcon.BRACELET, ItemIcon.RING);
      event.addMapping(ItemIcon.AMULET, ItemIcon.RING);
      event.addMapping(ItemIcon.STONE, ItemIcon.RING);
      event.addMapping(ItemIcon.JEWELLERY, ItemIcon.RING);
      event.addMapping(ItemIcon.PIN, ItemIcon.RING);
      event.addMapping(ItemIcon.BELL, ItemIcon.RING);
      event.addMapping(ItemIcon.BAG, ItemIcon.RING);
      event.addMapping(ItemIcon.CLOAK, ItemIcon.RING);
      event.addMapping(ItemIcon.SCARF, ItemIcon.RING);
      event.addMapping(ItemIcon.GLOVE, ItemIcon.RING);
      event.addMapping(ItemIcon.HORN, ItemIcon.RING);
      event.addMapping(ItemIcon.SHIELD, ItemIcon.RING);
    }
  }
}
