package legend.game.modding.coremod;

import legend.core.GameEngine;
import legend.game.combat.DragoonAdditionMode;
import legend.game.combat.formula.Formula;
import legend.game.combat.formula.PhysicalDamageFormula;
import legend.game.combat.ui.ControllerStyle;
import legend.game.input.InputAction;
import legend.game.modding.coremod.config.AdditionButtonModeConfigEntry;
import legend.game.modding.coremod.config.AdditionButtonStyleConfigEntry;
import legend.game.modding.coremod.config.AdditionDifficultyConfigEntry;
import legend.game.modding.coremod.config.AdditionGroupConfigEntry;
import legend.game.modding.coremod.config.DragoonAdditionGroupConfigEntry;
import legend.game.modding.coremod.config.DragoonAdditionModeConfigEntry;
import legend.game.modding.coremod.config.DragoonAdditionDifficultyConfigEntry;
import legend.game.modding.coremod.config.GeneralAdditionGroupConfigEntry;
import legend.game.modding.coremod.config.AdditionModeConfigEntry;
import legend.game.modding.coremod.config.AdditionOverlayConfigEntry;
import legend.game.modding.coremod.config.AdditionTimingModeConfigEntry;
import legend.game.modding.coremod.config.AdditionTimingOffsetConfigEntry;
import legend.game.modding.coremod.config.PreferredBattleCameraAngleConfigEntry;
import legend.game.modding.coremod.config.AllowWidescreenConfigEntry;
import legend.game.modding.coremod.config.AudioDeviceConfig;
import legend.game.modding.coremod.config.BattleTransitionModeConfigEntry;
import legend.game.modding.coremod.config.ControllerConfigEntry;
import legend.game.modding.coremod.config.ControllerDeadzoneConfigEntry;
import legend.game.modding.coremod.config.ControllerKeybindConfigEntry;
import legend.game.modding.coremod.config.ControllerKeybindsConfigEntry;
import legend.game.modding.coremod.config.AdditionSettingsConfigEntry;
import legend.game.modding.coremod.config.ControllerStyleConfigEntry;
import legend.game.modding.coremod.config.EnabledModsConfigEntry;
import legend.game.modding.coremod.config.EncounterRateConfigEntry;
import legend.game.modding.coremod.config.FooterActionColorConfigEntry;
import legend.game.modding.coremod.config.FullscreenConfigEntry;
import legend.game.modding.coremod.config.HighQualityProjectionConfigEntry;
import legend.game.modding.coremod.config.IndicatorModeConfigEntry;
import legend.game.modding.coremod.config.InventorySizeConfigEntry;
import legend.game.modding.coremod.config.MashModeConfigEntry;
import legend.game.modding.coremod.config.MonitorConfigEntry;
import legend.game.modding.coremod.config.MusicEffectsOverTimeGranularityConfigEntry;
import legend.game.modding.coremod.config.MusicInterpolationPrecisionConfigEntry;
import legend.game.modding.coremod.config.MusicPitchResolutionConfigEntry;
import legend.game.modding.coremod.config.MusicSampleRateConfigEntry;
import legend.game.modding.coremod.config.MusicVolumeConfigEntry;
import legend.game.modding.coremod.config.DisableMouseInputConfigEntry;
import legend.game.modding.coremod.config.ResolutionConfig;
import legend.game.modding.coremod.config.SecondaryCharacterXpMultiplierConfigEntry;
import legend.game.modding.coremod.config.SubmapWidescreenModeConfig;
import legend.game.modding.coremod.config.TransformationModeConfigEntry;
import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigRegistryEvent;
import legend.game.saves.ConfigStorageLocation;
import org.legendofdragoon.modloader.Mod;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F10;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;

/** Core mod that contains engine-level content. Game can not run without it. */
@Mod(id = CoreMod.MOD_ID)
@EventListener
public class CoreMod {
  public static final String MOD_ID = "lod_core";
  public static ArrayList<String> configOrder = new ArrayList<String>();

  private static final Registrar<ConfigEntry<?>, ConfigRegistryEvent> CONFIG_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.config, MOD_ID);

  // Global config
  public static final RegistryDelegate<ControllerConfigEntry> CONTROLLER_CONFIG = register("controller", ControllerConfigEntry::new);
  public static final RegistryDelegate<ControllerDeadzoneConfigEntry> CONTROLLER_DEADZONE_CONFIG = register("controller_deadzone", ControllerDeadzoneConfigEntry::new);
  public static final RegistryDelegate<BoolConfigEntry> RECEIVE_INPUT_ON_INACTIVE_WINDOW_CONFIG = register("receive_input_on_inactive_window", () -> new BoolConfigEntry(false, ConfigStorageLocation.GLOBAL, ConfigCategory.CONTROLS));
  public static final RegistryDelegate<BoolConfigEntry> DISABLE_MOUSE_INPUT_CONFIG = CONFIG_REGISTRAR.register("disable_mouse_input", DisableMouseInputConfigEntry::new);
  public static final RegistryDelegate<BoolConfigEntry> RUMBLE_CONFIG = register("rumble", () -> new BoolConfigEntry(true, ConfigStorageLocation.GLOBAL, ConfigCategory.CONTROLS));
  public static final RegistryDelegate<BoolConfigEntry> ALLOW_WIDESCREEN_CONFIG = register("allow_widescreen", AllowWidescreenConfigEntry::new);
  public static final RegistryDelegate<SubmapWidescreenModeConfig> SUBMAP_WIDESCREEN_MODE_CONFIG = register("submap_widescreen_mode", SubmapWidescreenModeConfig::new);
  public static final RegistryDelegate<BoolConfigEntry> HIGH_QUALITY_PROJECTION_CONFIG = register("high_quality_projection", HighQualityProjectionConfigEntry::new);
  public static final RegistryDelegate<BoolConfigEntry> FULLSCREEN_CONFIG = register("fullscreen", FullscreenConfigEntry::new);
  public static final RegistryDelegate<ResolutionConfig> RESOLUTION_CONFIG = register("resolution", ResolutionConfig::new);
  public static final RegistryDelegate<MonitorConfigEntry> MONITOR_CONFIG = CONFIG_REGISTRAR.register("monitor", MonitorConfigEntry::new);

  public static final RegistryDelegate<AudioDeviceConfig> AUDIO_DEVICE_CONFIG = register("audio_device", AudioDeviceConfig::new);
  public static final RegistryDelegate<MusicVolumeConfigEntry> MUSIC_VOLUME_CONFIG = register("music_volume", MusicVolumeConfigEntry::new);  public static final RegistryDelegate<MusicInterpolationPrecisionConfigEntry> MUSIC_INTERPOLATION_PRECISION_CONFIG = CONFIG_REGISTRAR.register("music_interpolation_precision", MusicInterpolationPrecisionConfigEntry::new);
  public static final RegistryDelegate<MusicPitchResolutionConfigEntry> MUSIC_PITCH_RESOLUTION_CONFIG = CONFIG_REGISTRAR.register("music_pitch_resolution", MusicPitchResolutionConfigEntry::new);
  public static final RegistryDelegate<MusicSampleRateConfigEntry> MUSIC_SAMPLE_RATE_CONFIG = CONFIG_REGISTRAR.register("music_sample_rate", MusicSampleRateConfigEntry::new);
  public static final RegistryDelegate<MusicEffectsOverTimeGranularityConfigEntry> MUSIC_EFFECTS_OVER_TIME_GRANULARITY_CONFIG = CONFIG_REGISTRAR.register("music_effects_over_time_granularity", MusicEffectsOverTimeGranularityConfigEntry::new);

  /** Config isn't actually used, but adds a button to the options screen to open the keybinds screen */
  public static final RegistryDelegate<ConfigEntry<Void>> CONTROLLER_KEYBINDS_CONFIG = register("controller_keybinds", ControllerKeybindsConfigEntry::new);
  public static final RegistryDelegate<ConfigEntry<Void>> ADDITION_SETTINGS_CONFIG = register("addition_settings", AdditionSettingsConfigEntry::new);
  public static final RegistryDelegate<ControllerStyleConfigEntry> CONTROLLER_STYLE_CONFIG = CONFIG_REGISTRAR.register("controller_style", ControllerStyleConfigEntry::new);

  public static final int ALT_ENTER_KEY = (GLFW_MOD_ALT << 9) | GLFW_KEY_ENTER;

  public static final Map<InputAction, RegistryDelegate<ControllerKeybindConfigEntry>> KEYBIND_CONFIGS = new EnumMap<>(InputAction.class);
  static {
    KEYBIND_CONFIGS.put(InputAction.DPAD_UP, CONFIG_REGISTRAR.register("keybind_dpad_up", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_UP)));
    KEYBIND_CONFIGS.put(InputAction.DPAD_RIGHT, CONFIG_REGISTRAR.register("keybind_dpad_right", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_RIGHT)));
    KEYBIND_CONFIGS.put(InputAction.DPAD_DOWN, CONFIG_REGISTRAR.register("keybind_dpad_down", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_DOWN)));
    KEYBIND_CONFIGS.put(InputAction.DPAD_LEFT, CONFIG_REGISTRAR.register("keybind_dpad_left", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_LEFT)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_NORTH, CONFIG_REGISTRAR.register("keybind_triangle", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_W)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_EAST, CONFIG_REGISTRAR.register("keybind_circle", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_D, GLFW_KEY_ESCAPE)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_SOUTH, CONFIG_REGISTRAR.register("keybind_cross", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_S, GLFW_KEY_ENTER)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_WEST, CONFIG_REGISTRAR.register("keybind_square", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_A)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_CENTER_1, CONFIG_REGISTRAR.register("keybind_select", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_SPACE)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_CENTER_2, CONFIG_REGISTRAR.register("keybind_start", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_ENTER)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_SHOULDER_LEFT_1, CONFIG_REGISTRAR.register("keybind_l1", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_Q)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_SHOULDER_RIGHT_1, CONFIG_REGISTRAR.register("keybind_r1", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_E)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_SHOULDER_LEFT_2, CONFIG_REGISTRAR.register("keybind_l2", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_1)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_SHOULDER_RIGHT_2, CONFIG_REGISTRAR.register("keybind_r2", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_3)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_THUMB_1, CONFIG_REGISTRAR.register("keybind_l3", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_Z)));
    KEYBIND_CONFIGS.put(InputAction.BUTTON_THUMB_2, CONFIG_REGISTRAR.register("keybind_r3", () -> new ControllerKeybindConfigEntry(true, GLFW_KEY_C)));
    KEYBIND_CONFIGS.put(InputAction.BATTLE_DRAGOON, CONFIG_REGISTRAR.register("keybind_dragoon", () -> new ControllerKeybindConfigEntry(false)));
    KEYBIND_CONFIGS.put(InputAction.BATTLE_SPECIAL, CONFIG_REGISTRAR.register("keybind_special", () -> new ControllerKeybindConfigEntry(false)));
    KEYBIND_CONFIGS.put(InputAction.BATTLE_ESCAPE, CONFIG_REGISTRAR.register("keybind_escape", () -> new ControllerKeybindConfigEntry(false)));
    KEYBIND_CONFIGS.put(InputAction.BATTLE_GUARD, CONFIG_REGISTRAR.register("keybind_guard", () -> new ControllerKeybindConfigEntry(false)));
    KEYBIND_CONFIGS.put(InputAction.BATTLE_ITEMS, CONFIG_REGISTRAR.register("keybind_items", () -> new ControllerKeybindConfigEntry(false)));
    KEYBIND_CONFIGS.put(InputAction.SPEED_UP, CONFIG_REGISTRAR.register("keybind_speed_up", () -> new ControllerKeybindConfigEntry(false, GLFW_KEY_EQUAL)));
    KEYBIND_CONFIGS.put(InputAction.SLOW_DOWN, CONFIG_REGISTRAR.register("keybind_slow_down", () -> new ControllerKeybindConfigEntry(false, GLFW_KEY_MINUS)));
    KEYBIND_CONFIGS.put(InputAction.DEBUGGER, CONFIG_REGISTRAR.register("keybind_debugger", () -> new ControllerKeybindConfigEntry(false, GLFW_KEY_F12)));
    KEYBIND_CONFIGS.put(InputAction.PAUSE, CONFIG_REGISTRAR.register("keybind_pause", () -> new ControllerKeybindConfigEntry(false, GLFW_KEY_F11)));
    KEYBIND_CONFIGS.put(InputAction.FRAME_ADVANCE, CONFIG_REGISTRAR.register("keybind_frame_advance", () -> new ControllerKeybindConfigEntry(false, GLFW_KEY_F9)));
    KEYBIND_CONFIGS.put(InputAction.FRAME_ADVANCE_HOLD, CONFIG_REGISTRAR.register("keybind_frame_advance_hold", () -> new ControllerKeybindConfigEntry(false, GLFW_KEY_F10)));
    KEYBIND_CONFIGS.put(InputAction.KILL_STUCK_SOUNDS, CONFIG_REGISTRAR.register("keybind_kill_stuck_sounds", () -> new ControllerKeybindConfigEntry(false, GLFW_KEY_F4)));
    KEYBIND_CONFIGS.put(InputAction.TOGGLE_FULL_SCREEN, CONFIG_REGISTRAR.register("keybind_toggle_full_screen", () -> new ControllerKeybindConfigEntry(false, ALT_ENTER_KEY)));
  }

  // Per-campaign config
  public static final RegistryDelegate<EnabledModsConfigEntry> ENABLED_MODS_CONFIG = register("enabled_mods", EnabledModsConfigEntry::new);
  public static final RegistryDelegate<IndicatorModeConfigEntry> INDICATOR_MODE_CONFIG = register("indicator_mode", IndicatorModeConfigEntry::new);
  public static final RegistryDelegate<InventorySizeConfigEntry> INVENTORY_SIZE_CONFIG = register("inventory_size", InventorySizeConfigEntry::new);
  public static final RegistryDelegate<EncounterRateConfigEntry> ENCOUNTER_RATE_CONFIG = register("encounter_rate", EncounterRateConfigEntry::new);
  public static final RegistryDelegate<TransformationModeConfigEntry> TRANSFORMATION_MODE_CONFIG = register("transformation_mode", TransformationModeConfigEntry::new);
  public static final RegistryDelegate<BoolConfigEntry> QUICK_TEXT_CONFIG = register("quick_text", () -> new BoolConfigEntry(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY));
  public static final RegistryDelegate<BoolConfigEntry> AUTO_TEXT_CONFIG = register("auto_text", () -> new BoolConfigEntry(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY));
  public static final RegistryDelegate<BoolConfigEntry> SAVE_ANYWHERE_CONFIG = register("save_anywhere", () -> new BoolConfigEntry(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY));
  public static final RegistryDelegate<BoolConfigEntry> DISABLE_STATUS_EFFECTS_CONFIG = register("disable_status_effects", () -> new BoolConfigEntry(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY));
  public static final RegistryDelegate<BoolConfigEntry> ENEMY_HP_BARS_CONFIG = register("enemy_hp_bars", () -> new BoolConfigEntry(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY));
  public static final RegistryDelegate<MashModeConfigEntry> MASH_MODE_CONFIG = register("mash_mode", MashModeConfigEntry::new);
  public static final RegistryDelegate<SecondaryCharacterXpMultiplierConfigEntry> SECONDARY_CHARACTER_XP_MULTIPLIER_CONFIG = register("secondary_character_xp_multiplier", SecondaryCharacterXpMultiplierConfigEntry::new);
  public static final RegistryDelegate<BattleTransitionModeConfigEntry> BATTLE_TRANSITION_MODE_CONFIG = register("battle_transition_mode", BattleTransitionModeConfigEntry::new);
  public static final RegistryDelegate<PreferredBattleCameraAngleConfigEntry> PREFERRED_BATTLE_CAMERA_ANGLE = register("preferred_battle_camera_angle", PreferredBattleCameraAngleConfigEntry::new);
  public static final RegistryDelegate<BoolConfigEntry> UNLOCK_PARTY_CONFIG = register("unlock_party", () -> new BoolConfigEntry(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY));
  public static final RegistryDelegate<FooterActionColorConfigEntry> FOOTER_ACTION_COLOR_CONFIG = CONFIG_REGISTRAR.register("footer_action_color", FooterActionColorConfigEntry::new);
  
  public static final RegistryDelegate<ConfigEntry<Void>> GENERAL_ADDITION_GROUP_CONFIG = register("general_addition_group", GeneralAdditionGroupConfigEntry::new);
  public static final RegistryDelegate<AdditionButtonStyleConfigEntry> ADDITION_BUTTON_STYLE_CONFIG = register("addition_button_style", AdditionButtonStyleConfigEntry::new);

  public static final RegistryDelegate<ConfigEntry<Void>> ADDITION_GROUP_CONFIG = register("addition_group", AdditionGroupConfigEntry::new);
  public static final RegistryDelegate<AdditionModeConfigEntry> ADDITION_MODE_CONFIG = register("addition_mode", AdditionModeConfigEntry::new);
  public static final RegistryDelegate<AdditionDifficultyConfigEntry> ADDITION_DIFFICULTY_CONFIG = register("addition_difficulty", AdditionDifficultyConfigEntry::new);
  public static final RegistryDelegate<AdditionTimingModeConfigEntry> ADDITION_TIMING_MODE_CONFIG = register("addition_timing_mode", AdditionTimingModeConfigEntry::new);
  public static final RegistryDelegate<AdditionTimingOffsetConfigEntry> ADDITION_TIMING_OFFSET_CONFIG = register("addition_timing_offset", AdditionTimingOffsetConfigEntry::new);
  public static final RegistryDelegate<AdditionOverlayConfigEntry> ADDITION_OVERLAY_CONFIG = register("addition_overlay_mode", AdditionOverlayConfigEntry::new);
  public static final RegistryDelegate<AdditionButtonModeConfigEntry> ADDITION_BUTTON_MODE_CONFIG = register("addition_button_mode", AdditionButtonModeConfigEntry::new);

  public static final RegistryDelegate<ConfigEntry<Void>> DRAGOON_ADDITION_GROUP_CONFIG = register("dragoon_addition_group", DragoonAdditionGroupConfigEntry::new);
  public static final RegistryDelegate<DragoonAdditionModeConfigEntry> DRAGOON_ADDITION_MODE_CONFIG = register("dragoon_addition_mode", DragoonAdditionModeConfigEntry::new);
  public static final RegistryDelegate<DragoonAdditionDifficultyConfigEntry> DRAGOON_ADDITION_DIFFICULTY_CONFIG = register("dragoon_addition_difficulty", DragoonAdditionDifficultyConfigEntry::new);



  public static final Formula<Integer, Integer> PHYSICAL_DAMAGE_FORMULA = Formula.make(PhysicalDamageFormula::calculatePhysicalDamage, builder -> builder
    .then(PhysicalDamageFormula::applyElementalInteractions)
    .then(PhysicalDamageFormula::applyPower)
    .then(PhysicalDamageFormula::applyDragoonSpace)
    .then(PhysicalDamageFormula.minimum(0))
    .then(PhysicalDamageFormula::applyDamageMultipliers)
    .then(PhysicalDamageFormula::applyAttackEffects)
    .then(PhysicalDamageFormula.minimum(1))
    .then(PhysicalDamageFormula::applyResistanceAndImmunity)
    .then(PhysicalDamageFormula::applyElementalResistanceAndImmunity)
  );

  private static <T extends ConfigEntry<?>> RegistryDelegate<T> register(final String entryId, final Supplier<T> entry) {
    return register(entryId, entry, MOD_ID);
  }

  public static <T extends ConfigEntry<?>> RegistryDelegate<T> register(final String entryId, final Supplier<T> entry, final String modID) {
    configOrder.add(modID + ':' + entryId);
    return CONFIG_REGISTRAR.register(entryId, entry);
  }

  public static RegistryId id(final String entryId) {
    return new RegistryId(MOD_ID, entryId);
  }

  @EventListener
  public static void registerConfig(final ConfigRegistryEvent event) {
    CONFIG_REGISTRAR.registryEvent(event);
  }
}
