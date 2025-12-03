package legend.game.inventory.screens;

import legend.core.Config;
import legend.core.GameEngine;
import legend.game.combat.ui.FooterActions;
import legend.game.combat.ui.FooterActionsHud;
import legend.game.combat.ui.UiBox;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.controls.Background;
import legend.game.inventory.screens.controls.Label;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.coremod.config.BattleUIColourSettingsConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;
import legend.game.types.MessageBoxResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.DEFAULT_FONT;
import static legend.core.GameEngine.RENDERER;
import static legend.game.Audio.playMenuSound;
import static legend.game.FullScreenEffects.startFadeEffect;
import static legend.game.Menus.deallocateRenderables;
import static legend.game.SItem.menuStack;
import static legend.game.Text.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DELETE;

public class BattleUIColourScreen extends VerticalLayoutScreen {
  private final Runnable unload;
  private final Map<Control, ConfigEntry<?>> helpEntries = new HashMap<>();
  private final Map<Control, Label> helpLabels = new HashMap<>();
  private static final Logger LOGGER = LogManager.getFormatterLogger(OptionsScreen.class);
  public static boolean dirty;
  private UiBox listBox;
  private float currentBoxOffsetX;
  private final FontOptions font = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.DARKER_GREY).size(1f).horizontalAlign(HorizontalAlign.CENTRE);

  private final ConfigCollection config;
  private final Set<ConfigStorageLocation> validLocations;
  private final ConfigCategory category;

  public BattleUIColourScreen(final ConfigCollection config, final Set<ConfigStorageLocation> validLocations, final ConfigCategory category, final Runnable unload) {
    deallocateRenderables(0xff);
    startFadeEffect(2, 10);

    this.unload = unload;
    this.config = config;
    this.validLocations = validLocations;
    this.category = category;

    this.addControl(new Background());
    this.loadControls();

    FooterActionsHud.setMenuActions(FooterActions.DEFAULT, null, null);
    this.addHotkey(null, INPUT_ACTION_MENU_DELETE, this::setDefaultColor);
    this.addHotkey(null, INPUT_ACTION_MENU_BACK, this::back);
  }

  private void back() {
    playMenuSound(3);
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

  private void loadControls() {

    final Map<ConfigEntry<?>, SettingEntry> translations = new HashMap<>();

    for(final RegistryId configId : GameEngine.REGISTRIES.config) {
      final ConfigEntry<?> entry = GameEngine.REGISTRIES.config.getEntry(configId).get();
      if(entry.category == category) {
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

        if(this.validLocations.contains(configEntry.storageLocation) && (configEntry.hasEditControl() || configEntry.header)) {
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
            help.setPos((int)(DEFAULT_FONT.textWidth(text) * label.getScale()) + 2, 1);
            help.onHoverIn(() -> this.getStack().pushScreen(new TooltipScreen(I18n.translate(configEntry.getHelpTranslationKey()), this.mouseX, this.mouseY)));
            this.helpLabels.put(label, help);
            this.helpEntries.put(label, configEntry);
          }
        }
      });
  }

  private void reloadControls() {
    final int highlight = this.highlightedRow;
    this.deleteControls();
    this.addControl(new Background());
    this.loadControls();
    this.highlightedRow = highlight;
    this.redrawHighlightRow();
  }

  private void setDefaultColor() {
    menuStack.pushScreen(new MessageBoxScreen("Reset to default colour?", 2, result2 -> {
      if(result2.messageBoxResult == MessageBoxResult.YES) {
        CONFIG.setConfig(CoreMod.BATTLE_UI_COLOUR_RED_CONFIG.get(), 0);
        CONFIG.setConfig(CoreMod.BATTLE_UI_COLOUR_GREEN_CONFIG.get(), 41);
        CONFIG.setConfig(CoreMod.BATTLE_UI_COLOUR_BLUE_CONFIG.get(), 159);
        dirty = true;
        this.reloadControls();
      }
    }));
  }

  @Override
  protected void render() {
    super.render();

    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    if(this.listBox == null || this.currentBoxOffsetX != xOffset || dirty) {
      BattleUIColourSettingsConfigEntry.setRGB();
      this.currentBoxOffsetX = xOffset;
      this.listBox = new UiBox("Battle UI Colour Screen List", 368 * 0.09f, 100f, 368 * 0.82f, 80f, 0.7f);
      dirty = false;
    }

    this.listBox.render(Config.changeBattleRgb() ? Config.getBattleRgb() : Config.defaultUiColour, 150);
    renderText("Adjust the numbers to change the colour", 368 * 0.5f, 133f, this.font, 149);
  }
}
