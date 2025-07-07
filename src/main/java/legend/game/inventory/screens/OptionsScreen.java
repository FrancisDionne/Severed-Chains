package legend.game.inventory.screens;

import legend.core.GameEngine;
import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputButton;
import legend.core.platform.input.InputKey;
import legend.core.platform.input.InputMod;
import legend.game.combat.AdditionButtonMode;
import legend.game.combat.AdditionCounterDifficulty;
import legend.game.combat.AdditionDifficulty;
import legend.game.combat.AdditionMode;
import legend.game.combat.AdditionTimingMode;
import legend.game.combat.BattleDifficulty;
import legend.game.combat.BattleTransitionMode;
import legend.game.combat.DragoonAdditionDifficulty;
import legend.game.combat.DragoonAdditionMode;
import legend.game.combat.MashMode;
import legend.game.combat.Monsters;
import legend.game.combat.PreferredBattleCameraAngle;
import legend.game.combat.effects.TransformationMode;
import legend.game.combat.ui.AdditionOverlayMode;
import legend.game.combat.ui.FooterActionColor;
import legend.game.combat.ui.FooterActions;
import legend.game.combat.ui.FooterActionsHud;
import legend.game.i18n.I18n;
import legend.game.inventory.IconSet;
import legend.game.inventory.screens.controls.Background;
import legend.game.inventory.screens.controls.Label;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.coremod.config.BattleDifficultyConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;
import legend.game.submap.EncounterRateMode;
import legend.game.types.MessageBoxResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legendofdragoon.modloader.registries.RegistryId;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static legend.core.GameEngine.CONFIG;
import static legend.game.SItem.menuStack;
import static legend.game.Scus94491BpeSegment.startFadeEffect;
import static legend.game.Scus94491BpeSegment_8002.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.Scus94491BpeSegment_8002.textWidth;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DELETE;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_HELP;

public class OptionsScreen extends VerticalLayoutScreen {
  private static final Logger LOGGER = LogManager.getFormatterLogger(OptionsScreen.class);
  private final Runnable unload;

  private final Map<Control, Label> helpLabels = new HashMap<>();
  private final Map<Control, ConfigEntry<?>> helpEntries = new HashMap<>();

  private final ConfigCollection config;
  private final Set<ConfigStorageLocation> validLocations;
  private final ConfigCategory category;

  private MessageBoxResults recommendedMessageBoxResult;

  private final String[] recommendedMessageBoxTexts = {
    "Normal", "Original Gameplay + Quality of Life",
    "Veteran", "Challenging Gameplay + Quality of Life",
    "Zealous", "Hardcore Gameplay + Quality of Life",
    "Nostalgia", "Original Everything",
    "Casual", "Easier Gameplay + Quality of Life"
  };

  private final FontOptions[] recommendedMessageBoxFonts = {
    new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.LIGHT_BROWN).horizontalAlign(HorizontalAlign.CENTRE).size(0.7f),
    new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.LIGHT_BROWN).horizontalAlign(HorizontalAlign.CENTRE).size(0.7f),
    new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.LIGHT_BROWN).horizontalAlign(HorizontalAlign.CENTRE).size(0.7f),
    new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.LIGHT_BROWN).horizontalAlign(HorizontalAlign.CENTRE).size(0.7f),
    new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.LIGHT_BROWN).horizontalAlign(HorizontalAlign.CENTRE).size(0.7f),
  };

  private final FontOptions recommendMessageBoxConfirmFont = new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.LIGHT_BROWN).horizontalAlign(HorizontalAlign.CENTRE).size(0.7f);

  public OptionsScreen(final ConfigCollection config, final Set<ConfigStorageLocation> validLocations, final ConfigCategory category, final Runnable unload) {
    deallocateRenderables(0xff);

    this.unload = unload;
    this.init();

    this.config = config;
    this.validLocations = validLocations;
    this.category = category;

    this.loadControls();

    this.addHotkey(I18n.translate("lod_core.ui.options.help"), INPUT_ACTION_MENU_HELP, this::help);
    this.addHotkey(I18n.translate("lod_core.ui.options.back"), INPUT_ACTION_MENU_BACK, this::back);
    this.addHotkey(I18n.translate("lod_core.ui.options.delete"), INPUT_ACTION_MENU_DELETE, this::recommended);
  }

  protected void init() {
    startFadeEffect(2, 10);
    this.reloadControls();
  }

  private void reloadControls() {
    final int highlight = this.highlightedRow;
    this.deleteControls();
    this.addControl(new Background());
    this.loadControls();
    this.highlightedRow = highlight;
    this.redrawHighlightRow();
  }

  private void loadControls() {
    final Map<ConfigEntry<?>, SettingEntry> translations = new HashMap<>();

    for(final RegistryId configId : GameEngine.REGISTRIES.config) {
      final ConfigEntry<?> entry = GameEngine.REGISTRIES.config.getEntry(configId).get();
      if(entry.category == this.category) {
        translations.put(entry, new SettingEntry(I18n.translate(entry.getLabelTranslationKey()), entry.order));
      }
    }

    translations.entrySet().stream()
      .sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getValue().label, o2.getValue().label))
      .sorted(Comparator.comparingDouble(o -> o.getValue().order))
      .forEach(entry -> {
        //noinspection rawtypes
        final ConfigEntry configEntry = entry.getKey();
        final String text = entry.getValue().label;

        if(this.validLocations.contains(configEntry.storageLocation) && (configEntry.hasEditControl()|| configEntry.header) && (!this.hideNonBattleEntries() || configEntry.availableInBattle())) {
          Control editControl = null;
          boolean error = false;

          if(!configEntry.header) {
            try {
              //noinspection unchecked
              editControl = configEntry.makeEditControl(this.config.getConfig(configEntry), this.config);
            } catch(final Throwable ex) {
              editControl = this.createErrorLabel("Error creating control", ex, false);
              error = true;
            }
            editControl.setZ(35);
          }

          final Label label = this.addRow(text, editControl);

          if(error) {
            label.getFontOptions().colour(0.30f, 0.0f, 0.0f).shadowColour(TextColour.LIGHT_BROWN);
          } else if (configEntry.header) {
            label.getFontOptions().colour(TextColour.DARKER_GREY).shadowColour(TextColour.MIDDLE_BROWN);
          }

          if(configEntry.hasHelp()) {
            final Label help = label.addControl(new Label("?"));
            help.setScale(0.4f);
            help.setPos((int)(textWidth(text) * label.getScale()) + 2, 1);
            help.onHoverIn(() -> this.getStack().pushScreen(new TooltipScreen(I18n.translate(configEntry.getHelpTranslationKey()), this.mouseX, this.mouseY)));
            this.helpLabels.put(label, help);
            this.helpEntries.put(label, configEntry);
          }
        }
      });
  }

  protected boolean hideNonBattleEntries() {
    return false;
  }

  private Label createErrorLabel(final String log, final Throwable ex, final boolean setSize) {
    LOGGER.warn(log, ex);
    final Label l = new Label(I18n.translate("lod_core.ui.options.error"));
    l.getFontOptions().colour(0.30f, 0.0f, 0.0f).shadowColour(TextColour.LIGHT_BROWN);

    if(setSize) {
      l.setSize(140, 11);
      l.setPos(this.getWidth() - 64 - l.getWidth(), 0);
      l.setScale(0.66f);
    }

    return l;
  }

  private void replaceControlWithErrorLabel(final String log, final Throwable ex) {
    final Label row = this.getHighlightedRow();
    if(row != null) {
      row.getFontOptions().colour(0.30f, 0.0f, 0.0f).shadowColour(TextColour.LIGHT_BROWN);
      for(int i = row.getControls().size() - 1; i > -1; i--) {
        row.removeControl(row.getControl(i));
      }
      row.addControl(this.createErrorLabel(log, ex, true));
    }
  }

  private void back() {
    playMenuSound(3);
    FooterActionsHud.setMenuActions(null, null, null);
    this.unload.run();
  }

  private void help() {
    final ConfigEntry<?> configEntry = this.helpEntries.get(this.getHighlightedRow());
    if(configEntry != null) {
      playMenuSound(2);
      final Label helpLabel = this.helpLabels.get(this.getHighlightedRow());
      this.getStack().pushScreen(new TooltipScreen(I18n.translate(configEntry.getHelpTranslationKey()), helpLabel.calculateTotalX() + helpLabel.getWidth() / 2, helpLabel.calculateTotalY() + helpLabel.getHeight() / 2));
    }
  }

  private void recommended() {
    menuStack.pushScreen(new MessageBoxCycleScreen(this.recommendedMessageBoxTexts, this.recommendedMessageBoxFonts, "Set", "Cancel", 2, result1 -> {
      if(result1.messageBoxResult == MessageBoxResult.YES) {
        this.recommendedMessageBoxResult = result1;
        if(this.recommendedMessageBoxResult.intValue > -1) {
          menuStack.pushScreen(new MessageBoxScreen("Use '" + this.recommendedMessageBoxTexts[this.recommendedMessageBoxResult.intValue * 2] + "' preset?\nSeveral options will be changed", 2, this.recommendMessageBoxConfirmFont, result2 -> {
            if(result2.messageBoxResult == MessageBoxResult.YES) {
              this.setRecommendedOptions(this.recommendedMessageBoxResult.intValue);
              this.recommendedMessageBoxResult = null;
            }
          }));
        }
      }
    }));
  }

  private void setRecommendedOptions(final int recommendId) {

    if(recommendId != 3) { //if not Nostalgia preset
      //Common/Default Options
      CONFIG.setConfig(CoreMod.PREFERRED_BATTLE_CAMERA_ANGLE.get(), PreferredBattleCameraAngle.PLAYER);
      CONFIG.setConfig(CoreMod.BATTLE_TRANSITION_MODE_CONFIG.get(), BattleTransitionMode.INSTANT);
      CONFIG.setConfig(CoreMod.MASH_MODE_CONFIG.get(), MashMode.MASH);
      CONFIG.setConfig(CoreMod.DISABLE_STATUS_EFFECTS_CONFIG.get(), false);
      CONFIG.setConfig(CoreMod.TRANSFORMATION_MODE_CONFIG.get(), TransformationMode.SHORT);
      CONFIG.setConfig(CoreMod.ENCOUNTER_RATE_CONFIG.get(), EncounterRateMode.AVERAGE);
      CONFIG.setConfig(CoreMod.ENEMY_HP_BARS_CONFIG.get(), false);
      CONFIG.setConfig(CoreMod.SECONDARY_CHARACTER_XP_MULTIPLIER_CONFIG.get(), 0.50f);
      CONFIG.setConfig(CoreMod.FOOTER_ACTION_COLOR_CONFIG.get(), FooterActionColor.FOOTER_WHITE);
      CONFIG.setConfig(CoreMod.ICON_SET.get(), IconSet.ENHANCED);
      CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 50);
      CONFIG.setConfig(CoreMod.UNLOCK_PARTY_CONFIG.get(), true);
      CONFIG.setConfig(CoreMod.SAVE_ANYWHERE_CONFIG.get(), true);

      CONFIG.setConfig(CoreMod.ADDITION_MODE_CONFIG.get(), AdditionMode.NORMAL);
      CONFIG.setConfig(CoreMod.ADDITION_DIFFICULTY_CONFIG.get(), AdditionDifficulty.NORMAL);
      CONFIG.setConfig(CoreMod.ADDITION_TIMING_MODE_CONFIG.get(), AdditionTimingMode.ADJUSTED);
      CONFIG.setConfig(CoreMod.ADDITION_BUTTON_MODE_CONFIG.get(), AdditionButtonMode.FEEDBACK);
      CONFIG.setConfig(CoreMod.ADDITION_OVERLAY_CONFIG.get(), AdditionOverlayMode.FULL);
      CONFIG.setConfig(CoreMod.ADDITION_OVERLAY_SIZE_CONFIG.get(), 1f);
      CONFIG.setConfig(CoreMod.ADDITION_COUNTER_DIFFICULTY_CONFIG.get(), AdditionCounterDifficulty.NORMAL);
      CONFIG.setConfig(CoreMod.ADDITION_GAMEPLAY_ENHANCE_CONFIG.get(), true);
      CONFIG.setConfig(CoreMod.DRAGOON_ADDITION_MODE_CONFIG.get(), DragoonAdditionMode.NORMAL);
      CONFIG.setConfig(CoreMod.DRAGOON_ADDITION_DIFFICULTY_CONFIG.get(), DragoonAdditionDifficulty.NORMAL);
    }

    switch(recommendId) {
      case 3:  //Nostalgia
        CONFIG.setConfig(CoreMod.BATTLE_DIFFICULTY.get(), BattleDifficulty.NORMAL);
        CONFIG.setConfig(CoreMod.PREFERRED_BATTLE_CAMERA_ANGLE.get(), PreferredBattleCameraAngle.NORMAL);
        CONFIG.setConfig(CoreMod.BATTLE_TRANSITION_MODE_CONFIG.get(), BattleTransitionMode.NORMAL);
        CONFIG.setConfig(CoreMod.MASH_MODE_CONFIG.get(), MashMode.MASH);
        CONFIG.setConfig(CoreMod.DISABLE_STATUS_EFFECTS_CONFIG.get(), false);
        CONFIG.setConfig(CoreMod.TRANSFORMATION_MODE_CONFIG.get(), TransformationMode.NORMAL);
        CONFIG.setConfig(CoreMod.ENCOUNTER_RATE_CONFIG.get(), EncounterRateMode.RETAIL);
        CONFIG.setConfig(CoreMod.ENEMY_HP_BARS_CONFIG.get(), false);
        CONFIG.setConfig(CoreMod.SECONDARY_CHARACTER_XP_MULTIPLIER_CONFIG.get(), 0.50f);
        CONFIG.setConfig(CoreMod.FOOTER_ACTION_COLOR_CONFIG.get(), FooterActionColor.FOOTER_HIDDEN);
        CONFIG.setConfig(CoreMod.ICON_SET.get(), IconSet.RETAIL);
        CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 50);
        CONFIG.setConfig(CoreMod.UNLOCK_PARTY_CONFIG.get(), false);
        CONFIG.setConfig(CoreMod.SAVE_ANYWHERE_CONFIG.get(), false);
        CONFIG.setConfig(CoreMod.TURBO_TOGGLE_CONFIG.get(), false);

        CONFIG.setConfig(CoreMod.ADDITION_MODE_CONFIG.get(), AdditionMode.NORMAL);
        CONFIG.setConfig(CoreMod.ADDITION_DIFFICULTY_CONFIG.get(), AdditionDifficulty.NORMAL);
        CONFIG.setConfig(CoreMod.ADDITION_TIMING_MODE_CONFIG.get(), AdditionTimingMode.RETAIL);
        CONFIG.setConfig(CoreMod.ADDITION_BUTTON_MODE_CONFIG.get(), AdditionButtonMode.RETAIL);
        CONFIG.setConfig(CoreMod.ADDITION_OVERLAY_CONFIG.get(), AdditionOverlayMode.FULL);
        CONFIG.setConfig(CoreMod.ADDITION_OVERLAY_SIZE_CONFIG.get(), 1f);
        CONFIG.setConfig(CoreMod.ADDITION_COUNTER_DIFFICULTY_CONFIG.get(), AdditionCounterDifficulty.NORMAL);
        CONFIG.setConfig(CoreMod.ADDITION_GAMEPLAY_ENHANCE_CONFIG.get(), false);
        CONFIG.setConfig(CoreMod.DRAGOON_ADDITION_MODE_CONFIG.get(), DragoonAdditionMode.NORMAL);
        CONFIG.setConfig(CoreMod.DRAGOON_ADDITION_DIFFICULTY_CONFIG.get(), DragoonAdditionDifficulty.NORMAL);
      case 4:  //Casual
        CONFIG.setConfig(CoreMod.BATTLE_DIFFICULTY.get(), BattleDifficulty.EASY);
        CONFIG.setConfig(CoreMod.MASH_MODE_CONFIG.get(), MashMode.HOLD);
        CONFIG.setConfig(CoreMod.DISABLE_STATUS_EFFECTS_CONFIG.get(), true);
        CONFIG.setConfig(CoreMod.ENEMY_HP_BARS_CONFIG.get(), true);
        CONFIG.setConfig(CoreMod.SECONDARY_CHARACTER_XP_MULTIPLIER_CONFIG.get(), 1f);
        CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 100);

        CONFIG.setConfig(CoreMod.ADDITION_DIFFICULTY_CONFIG.get(), AdditionDifficulty.EASY);
        CONFIG.setConfig(CoreMod.ADDITION_OVERLAY_SIZE_CONFIG.get(), 1.2f);
        CONFIG.setConfig(CoreMod.ADDITION_COUNTER_DIFFICULTY_CONFIG.get(), AdditionCounterDifficulty.EASIER);
        CONFIG.setConfig(CoreMod.DRAGOON_ADDITION_DIFFICULTY_CONFIG.get(), DragoonAdditionDifficulty.EASY);
        break;
      case 0: //Normal
        CONFIG.setConfig(CoreMod.BATTLE_DIFFICULTY.get(), BattleDifficulty.NORMAL);
        break;
      case 1:  //Veteran
        CONFIG.setConfig(CoreMod.BATTLE_DIFFICULTY.get(), BattleDifficulty.HARD);
        CONFIG.setConfig(CoreMod.DRAGOON_ADDITION_DIFFICULTY_CONFIG.get(), DragoonAdditionDifficulty.HARD);
        break;
      case 2:  //Zealous
        CONFIG.setConfig(CoreMod.BATTLE_DIFFICULTY.get(), BattleDifficulty.HARDER);
        CONFIG.setConfig(CoreMod.SECONDARY_CHARACTER_XP_MULTIPLIER_CONFIG.get(), 0.25f);
        CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 30);
        CONFIG.setConfig(CoreMod.SAVE_ANYWHERE_CONFIG.get(), false);
        CONFIG.setConfig(CoreMod.TURBO_TOGGLE_CONFIG.get(), true);

        CONFIG.setConfig(CoreMod.ADDITION_OVERLAY_CONFIG.get(), AdditionOverlayMode.OFF);
        CONFIG.setConfig(CoreMod.ADDITION_RANDOM_MODE_CONFIG.get(), true);
        CONFIG.setConfig(CoreMod.DRAGOON_ADDITION_DIFFICULTY_CONFIG.get(), DragoonAdditionDifficulty.HARD);
        break;
    }

    Monsters.loadMonsters();
    this.reloadControls();
  }

  @Override
  public InputPropagation inputActionPressed(final InputAction action, final boolean repeat) {
    try {
      return super.inputActionPressed(action, repeat);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on pressedThisFrame", ex);
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected void renderControls(final int parentX, final int parentY) {
    try {
      super.renderControls(parentX, parentY);
      FooterActionsHud.renderMenuActions(FooterActions.HELP, FooterActions.PRESETS, null);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on renderControls", ex);
    }
  }

  @Override
  protected InputPropagation mouseMove(final int x, final int y) {
    try {
      return super.mouseMove(x, y);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on keyPress", ex);
    }
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation mouseScroll(final int deltaX, final int deltaY) {
    try {
      return super.mouseScroll(deltaX, deltaY);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on mouseScroll", ex);
    }
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation keyPress(final InputKey key, final InputKey scancode, final Set<InputMod> mods, final boolean repeat) {
    try {
      return super.keyPress(key, scancode, mods, repeat);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on keyPress", ex);
    }
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation keyRelease(final InputKey key, final InputKey scancode, final Set<InputMod> mods) {
    try {
      return super.keyRelease(key, scancode, mods);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on keyRelease", ex);
    }
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation buttonPress(final InputButton button, final boolean repeat) {
    try {
      return super.buttonPress(button, repeat);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on buttonPress", ex);
    }
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation buttonRelease(final InputButton button) {
    try {
      return super.buttonRelease(button);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on buttonRelease", ex);
    }
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation charPress(final int codepoint) {
    try {
      return super.charPress(codepoint);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on charPress", ex);
    }
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation inputActionReleased(final InputAction action) {
    try {
      return super.inputActionReleased(action);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on inputActionReleased", ex);
    }
    return InputPropagation.PROPAGATE;
  }

  @Override
  public void setFocus(@Nullable final Control control) {
    try {
      super.setFocus(control);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on setFocus", ex);
    }
  }

  @Override
  protected InputPropagation mouseScrollHighRes(final double deltaX, final double deltaY) {
    try {
      return super.mouseScrollHighRes(deltaX, deltaY);
    } catch(final Throwable ex) {
      this.replaceControlWithErrorLabel("Error on mouseScrollHighRes", ex);
    }
    return InputPropagation.PROPAGATE;
  }
}
