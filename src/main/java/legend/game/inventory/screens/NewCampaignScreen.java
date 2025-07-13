package legend.game.inventory.screens;

import legend.core.GameEngine;
import legend.core.platform.input.InputAction;
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
import legend.game.combat.PreferredBattleCameraAngle;
import legend.game.combat.effects.TransformationMode;
import legend.game.combat.ui.AdditionOverlayMode;
import legend.game.combat.ui.FooterActionColor;
import legend.game.combat.ui.FooterActions;
import legend.game.combat.ui.FooterActionsHud;
import legend.game.i18n.I18n;
import legend.game.inventory.IconSet;
import legend.game.inventory.WhichMenu;
import legend.game.inventory.screens.controls.Background;
import legend.game.inventory.screens.controls.Button;
import legend.game.inventory.screens.controls.Label;
import legend.game.inventory.screens.controls.Textbox;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.coremod.config.BattleDifficultyConfigEntry;
import legend.game.modding.events.gamestate.GameLoadedEvent;
import legend.game.modding.events.gamestate.NewGameEvent;
import legend.game.saves.Campaign;
import legend.game.saves.ConfigStorage;
import legend.game.saves.ConfigStorageLocation;
import legend.game.statistics.Statistics;
import legend.game.submap.EncounterRateMode;
import legend.game.types.GameState52c;
import legend.game.types.MessageBoxResult;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.EVENTS;
import static legend.core.GameEngine.MODS;
import static legend.core.GameEngine.SAVES;
import static legend.core.GameEngine.bootMods;
import static legend.game.SItem.menuStack;
import static legend.game.Scus94491BpeSegment.startFadeEffect;
import static legend.game.Scus94491BpeSegment_8002.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Scus94491BpeSegment_800b.loadingNewGameState_800bdc34;
import static legend.game.Scus94491BpeSegment_800b.whichMenu_800bdc38;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DELETE;

public class NewCampaignScreen extends VerticalLayoutScreen {
  private final GameState52c state = new GameState52c();
  private final Set<String> enabledMods = new HashSet<>();

  private final Textbox campaignName;

  private boolean unload;

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

  public NewCampaignScreen() {
    CONFIG.clearConfig(ConfigStorageLocation.CAMPAIGN);
    this.enabledMods.addAll(MODS.getAllModIds());

    deallocateRenderables(0xff);
    startFadeEffect(2, 10);

    this.addControl(new Background());

    this.campaignName = new Textbox();
    this.campaignName.setText(SAVES.generateCampaignName());
    this.campaignName.setMaxLength(15);
    this.campaignName.setZ(35);
    this.addRow("Campaign name", this.campaignName);

    final Button challenges = new Button("Challenges");
    this.addRow("", challenges);
    challenges.onPressed(() ->
      this.getStack().pushScreen(new CampaingChallengesScreen(CONFIG, EnumSet.allOf(ConfigStorageLocation.class), () -> {
        startFadeEffect(2, 10);
        this.getStack().popScreen();

        // Update global config but don't save campaign config until an actual save file is made so we don't end up with orphan campaigns
        ConfigStorage.saveConfig(CONFIG, ConfigStorageLocation.GLOBAL, Path.of("config.dcnf"));
      }))
    );

    final Button options = new Button("Options");
    this.addRow("", options);
    options.onPressed(() ->
      this.getStack().pushScreen(new OptionsCategoryScreen(CONFIG, EnumSet.allOf(ConfigStorageLocation.class), () -> {
        startFadeEffect(2, 10);
        this.getStack().popScreen();

        // Update global config but don't save campaign config until an actual save file is made so we don't end up with orphan campaigns
        ConfigStorage.saveConfig(CONFIG, ConfigStorageLocation.GLOBAL, Path.of("config.dcnf"));
      }))
    );

    final Button mods = new Button("Mods");
    this.addRow("", mods);
    mods.onPressed(() ->
      this.deferAction(() ->
        this.getStack().pushScreen(new ModsScreen(this.enabledMods, () -> {
          bootMods(this.enabledMods);

          startFadeEffect(2, 10);
          this.getStack().popScreen();
        }))
      )
    );

    final Button startGame = new Button("Start Game");
    this.addRow("", startGame);
    startGame.onPressed(() -> {
      if(SAVES.campaignExists(this.campaignName.getText())) {
        this.deferAction(() -> this.getStack().pushScreen(new MessageBoxScreen("Campaign name already\nin use", 0, result1 -> { })));
      } else {
        this.unload = true;
      }
    });

    final Label saveSlots = this.addControl(new Label("Severed Chains has unlimited save slots and we recommend\nyou save in a new slot each time."));
    saveSlots.setWidth(this.getWidth());
    saveSlots.getFontOptions().size(0.66f).horizontalAlign(HorizontalAlign.CENTRE);
    saveSlots.setY(200);

    this.addHotkey(I18n.translate("lod_core.ui.options.delete"), INPUT_ACTION_MENU_DELETE, this::recommended);
  }

  @Override
  protected void render() {
    if(this.unload) {
      GameEngine.bootRegistries();

      this.state.campaign = Campaign.create(SAVES, this.campaignName.getText().strip());

      final NewGameEvent newGameEvent = EVENTS.postEvent(new NewGameEvent(this.state));
      final GameLoadedEvent gameLoadedEvent = EVENTS.postEvent(new GameLoadedEvent(newGameEvent.gameState));

      gameState_800babc8 = gameLoadedEvent.gameState;

      this.state.campaign.loadConfigInto(CONFIG);
      CONFIG.setConfig(CoreMod.ENABLED_MODS_CONFIG.get(), this.enabledMods.toArray(String[]::new));

      Statistics.load(null, null);

      loadingNewGameState_800bdc34 = true;
      playMenuSound(2);
      whichMenu_800bdc38 = WhichMenu.UNLOAD;
    }
    FooterActionsHud.renderMenuActions(FooterActions.PRESETS, null, null);
  }

  private void menuEscape() {
    playMenuSound(3);
    whichMenu_800bdc38 = WhichMenu.UNLOAD;

    bootMods(MODS.getAllModIds());
  }

  @Override
  protected InputPropagation inputActionPressed(final InputAction action, final boolean repeat) {
    if(super.inputActionPressed(action, repeat) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.getFocus() == this.campaignName) {
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_BACK.get()) {
      this.menuEscape();
      return InputPropagation.HANDLED;
    }

    return InputPropagation.PROPAGATE;
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
      CONFIG.setConfig(CoreMod.PERMA_DEATH.get(), false);
      CONFIG.setConfig(CoreMod.QUICK_TEXT_CONFIG.get(), false);

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
        CONFIG.setConfig(CoreMod.PERMA_DEATH.get(), false);
        CONFIG.setConfig(CoreMod.QUICK_TEXT_CONFIG.get(), false);

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

        CONFIG.setConfig(CoreMod.ADDITION_COUNTER_DIFFICULTY_CONFIG.get(), AdditionCounterDifficulty.HARD);
        CONFIG.setConfig(CoreMod.DRAGOON_ADDITION_DIFFICULTY_CONFIG.get(), DragoonAdditionDifficulty.HARD);
        break;
      case 2:  //Zealous
        CONFIG.setConfig(CoreMod.BATTLE_DIFFICULTY.get(), BattleDifficulty.EXTREME);
        CONFIG.setConfig(CoreMod.SECONDARY_CHARACTER_XP_MULTIPLIER_CONFIG.get(), 0.25f);
        CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 30);
        CONFIG.setConfig(CoreMod.SAVE_ANYWHERE_CONFIG.get(), false);
        CONFIG.setConfig(CoreMod.TURBO_TOGGLE_CONFIG.get(), true);
        CONFIG.setConfig(CoreMod.UNLOCK_PARTY_CONFIG.get(), true);
        CONFIG.setConfig(CoreMod.QUICK_TEXT_CONFIG.get(), true);

        CONFIG.setConfig(CoreMod.ADDITION_OVERLAY_CONFIG.get(), AdditionOverlayMode.OFF);
        CONFIG.setConfig(CoreMod.ADDITION_COUNTER_DIFFICULTY_CONFIG.get(), AdditionCounterDifficulty.HARD);
        CONFIG.setConfig(CoreMod.ADDITION_RANDOM_MODE_CONFIG.get(), true);
        CONFIG.setConfig(CoreMod.DRAGOON_ADDITION_DIFFICULTY_CONFIG.get(), DragoonAdditionDifficulty.HARD);
        break;
    }

    BattleDifficultyConfigEntry.reloadMonsters();
  }
}
