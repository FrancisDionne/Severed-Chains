package legend.game.inventory.screens;

import legend.game.combat.ui.FooterActions;
import legend.game.combat.ui.FooterActionsHud;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.controls.Background;
import legend.game.inventory.screens.controls.BigList;
import legend.game.inventory.screens.controls.Glyph;
import legend.game.inventory.screens.controls.SaveCard;
import legend.game.inventory.screens.controls.SaveCardData;
import legend.game.saves.Campaign;
import legend.game.saves.SavedGame;
import legend.game.types.MessageBoxResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.function.Consumer;

import static legend.game.SItem.UI_TEXT_CENTERED;
import static legend.game.SItem.menuStack;
import static legend.game.Scus94491BpeSegment.startFadeEffect;
import static legend.game.Scus94491BpeSegment_8002.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DELETE;

public class LoadGameScreen extends MenuScreen {
  private static final Logger LOGGER = LogManager.getFormatterLogger(LoadGameScreen.class);

  private final Campaign campaign;
  private final BigList<SaveCardData> saveList;
  private final Consumer<SaveCardData> saveSelected;
  private final Runnable closed;

  public LoadGameScreen(final Consumer<SaveCardData> saveSelected, final Runnable closed, final SaveCardData data) {
    this.saveSelected = saveSelected;
    this.closed = closed;
    this.campaign = data.campaign;

    deallocateRenderables(0xff);
    startFadeEffect(2, 10);

    this.addControl(new Background());

    // Bottom line
    this.addControl(Glyph.glyph(78)).setPos(26, 155);
    this.addControl(Glyph.glyph(79)).setPos(192, 155);

    final SaveCard saveCard = this.addControl(new SaveCard());
    saveCard.setPos(16, 160);

    this.saveList = this.addControl(new BigList<>(SaveCardData::saveName));
    this.saveList.setPos(16, 16);
    this.saveList.setSize(360, 144);
    this.saveList.onHighlight(saveCard::setSaveData);
    this.saveList.onSelection(this::onSelection);
    this.setFocus(this.saveList);

    for(final SavedGame save : this.campaign.loadAllSaves()) {
      this.saveList.addEntry(new SaveCardData(this.campaign, save));
    }

    this.addHotkey(null, INPUT_ACTION_MENU_DELETE, this::menuDelete);
    this.addHotkey(null, INPUT_ACTION_MENU_BACK, this::menuEscape);
  }

  private void onSelection(final SaveCardData data) {
    if(data.saveGame.isValid()) {
      playMenuSound(2);
      menuStack.pushScreen(new MessageBoxScreen("Load this save?", 2, result -> this.onMessageboxResult(result.messageBoxResult, data)));
    } else {
      playMenuSound(4);
      menuStack.pushScreen(new MessageBoxScreen("This save cannot be loaded", 0, result -> { }));
    }
  }

  private void onMessageboxResult(final MessageBoxResult result, final SaveCardData data) {
    if(result == MessageBoxResult.YES) {
      this.saveSelected.accept(data);
    }
  }

  @Override
  public void setFocus(@Nullable final Control control) {
    super.setFocus(this.saveList);
  }

  @Override
  protected void render() {
    renderText("Load Game", 188, 10, UI_TEXT_CENTERED);
    FooterActionsHud.renderMenuActions(FooterActions.DELETE, null, null);
  }

  private void menuDelete() {
    playMenuSound(40);

    if(this.saveList.size() == 1) {
      menuStack.pushScreen(new MessageBoxScreen("Can't delete last save", 0, result -> {}));
      return;
    }

    if(this.saveList.getSelected() != null) {
      menuStack.pushScreen(new MessageBoxScreen("Are you sure you want to\ndelete this save?", 2, result -> {
        if(result.messageBoxResult == MessageBoxResult.YES) {
          try {
            this.campaign.deleteSave(this.saveList.getSelected().saveGame.fileName);
            this.saveList.removeEntry(this.saveList.getSelected());
          } catch(final IOException e) {
            LOGGER.error("Failed to delete save", e);
            this.deferAction(() -> menuStack.pushScreen(new MessageBoxScreen("Failed to delete save", 0, result1 -> {})));
          }
        }
      }));
    }
  }

  private void menuEscape() {
    playMenuSound(3);
    this.closed.run();
  }
}
