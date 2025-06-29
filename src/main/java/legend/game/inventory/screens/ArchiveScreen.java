package legend.game.inventory.screens;

import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputMod;
import legend.game.combat.ui.FooterActions;
import legend.game.combat.ui.FooterActionsHud;

import java.util.Set;

import static legend.game.SItem.allocateUiElement;
import static legend.game.Scus94491BpeSegment.startFadeEffect;
import static legend.game.Scus94491BpeSegment_8002.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DELETE;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_END;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_HOME;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_LEFT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_PAGE_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_PAGE_UP;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_RIGHT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_SORT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_UP;

public class ArchiveScreen extends MenuScreen {
  private int loadingStage;
  private final Runnable unload;
  private int currentArchiveType;
  private final ArchiveStatisticsRenderer statisticsRenderer;
  private final ArchiveBestiaryRenderer bestiaryRenderer;

  public ArchiveScreen(final Runnable unload) {
    this.unload = unload;
    this.currentArchiveType = 1;
    this.statisticsRenderer = new ArchiveStatisticsRenderer();
    this.bestiaryRenderer = new ArchiveBestiaryRenderer();
  }

  @Override
  protected void render() {
    switch(this.loadingStage) {
      case 0 -> {
        startFadeEffect(2, 10);
        deallocateRenderables(0xff);
        this.loadingStage++;
      }

      case 1 -> {
        deallocateRenderables(0);

        allocateUiElement(69, 69, 0, 1); // Background left
        allocateUiElement(70, 70, 192, 1); // Background right

        this.renderArchive();
        this.loadingStage++;
      }

      case 2 -> {
        this.renderArchive();
      }

      // Fade out
      case 100 -> {
        this.renderArchive();
        this.unload.run();
      }
    }
    FooterActionsHud.renderActions(0, FooterActions.BACK, this.currentArchiveType == 0 ? FooterActions.ARCHIVE_BESTIARY : FooterActions.ARCHIVE_STATS, this.currentArchiveType == 1 ? FooterActions.LIST : null, null, null);
  }

  private void renderArchive() {
    switch(this.currentArchiveType) {
      case 0:
        this.statisticsRenderer.render();
        break;
      case 1:
        this.bestiaryRenderer.render();
        break;
    }
  }

  @Override
  protected InputPropagation mouseClick(final int x, final int y, final int button, final Set<InputMod> mods) {
    if(super.mouseClick(x, y, button, mods) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.loadingStage != 2 || !mods.isEmpty()) {
      return InputPropagation.PROPAGATE;
    }

    return InputPropagation.PROPAGATE;
  }

  private void menuEscape() {
    playMenuSound(3);
    this.loadingStage = 100;
  }

  private void menuNavigateLeft(final int steps) {
    switch(this.currentArchiveType) {
      case 0:
        if(this.statisticsRenderer.pageIndex > 0) {
          playMenuSound(1);
          this.statisticsRenderer.pageIndex--;
        }
        break;
      case 1:
        if(this.bestiaryRenderer.previous(steps)) {
          playMenuSound(1);
          this.bestiaryRenderer.loadCurrentPage();
        }
        break;
    }
  }

  private void menuNavigateRight(final int steps) {
    switch(this.currentArchiveType) {
      case 0:
        if(this.statisticsRenderer.pageIndex < this.statisticsRenderer.getPageCount() - 1) {
          playMenuSound(1);
          this.statisticsRenderer.pageIndex++;
        }
        break;
      case 1:
        if(this.bestiaryRenderer.next(steps)) {
          playMenuSound(1);
          this.bestiaryRenderer.loadCurrentPage();
        }
        break;
    }
  }

  private void menuNavigateUp() {
    switch(this.currentArchiveType) {
      case 0:
        playMenuSound(1);
        if(this.statisticsRenderer.highlightIndex > 0) {
          this.statisticsRenderer.highlightIndex--;
        } else {
          this.statisticsRenderer.highlightIndex = 11;
        }
        break;
    }
  }

  private void menuNavigateDown() {
    switch(this.currentArchiveType) {
      case 0:
        playMenuSound(1);
        if(this.statisticsRenderer.highlightIndex < 11) {
          this.statisticsRenderer.highlightIndex++;
        } else {
          this.statisticsRenderer.highlightIndex = 0;
        }
        break;
    }
  }

  @Override
  public InputPropagation inputActionPressed(final InputAction action, final boolean repeat) {
    if(super.inputActionPressed(action, repeat) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.loadingStage != 2) {
      return InputPropagation.PROPAGATE;
    }

    if(action == INPUT_ACTION_MENU_BACK.get() && !repeat) {
      if(this.currentArchiveType == 1 && this.bestiaryRenderer.isListVisible) {
        playMenuSound(3);
        this.bestiaryRenderer.isListVisible = false;
      } else {
        this.menuEscape();
      }
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_PAGE_UP.get()) {
      this.menuNavigateLeft(10);
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_PAGE_DOWN.get()) {
      this.menuNavigateRight(10);
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_LEFT.get()) {
      if(this.currentArchiveType == 1) {
        if(!this.bestiaryRenderer.isListVisible) {
          this.menuNavigateLeft(1);
          return InputPropagation.HANDLED;
        }
      } else {
        this.menuNavigateLeft(1);
        return InputPropagation.HANDLED;
      }
    }

    if(action == INPUT_ACTION_MENU_RIGHT.get()) {
      if(this.currentArchiveType == 1) {
        if(!this.bestiaryRenderer.isListVisible) {
          this.menuNavigateRight(1);
          return InputPropagation.HANDLED;
        }
      } else {
        this.menuNavigateRight(1);
        return InputPropagation.HANDLED;
      }
    }

    if(action == INPUT_ACTION_MENU_DOWN.get()) {
      if(this.currentArchiveType == 1) {
        if(this.bestiaryRenderer.isListVisible) {
          this.menuNavigateRight(1);
          return InputPropagation.HANDLED;
        }
      } else {
        this.menuNavigateDown();
        return InputPropagation.HANDLED;
      }
    }

    if(action == INPUT_ACTION_MENU_UP.get()) {
      if(this.currentArchiveType == 1) {
        if (this.bestiaryRenderer.isListVisible) {
          this.menuNavigateLeft(1);
          return InputPropagation.HANDLED;
        }
      } else {
        this.menuNavigateUp();
        return InputPropagation.HANDLED;
      }
    }

    if(action == INPUT_ACTION_MENU_HOME.get()) {
      switch(this.currentArchiveType) {
        case 0:
          playMenuSound(1);
          this.statisticsRenderer.pageIndex = 0;
          break;
        case 1:
          if(this.bestiaryRenderer.jump(0)) {
            playMenuSound(1);
            this.bestiaryRenderer.loadCurrentPage();
          }
          break;
      }
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_END.get()) {
      switch(this.currentArchiveType) {
        case 0:
          playMenuSound(1);
          this.statisticsRenderer.pageIndex = this.statisticsRenderer.getPageCount() - 1;
          break;
        case 1:
          if(this.bestiaryRenderer.jump(this.bestiaryRenderer.getPageCount() - 1)) {
            playMenuSound(1);
            this.bestiaryRenderer.loadCurrentPage();
          }
          break;
      }
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_SORT.get()) {
      playMenuSound(2);
      switch(this.currentArchiveType) {
        case 0:
          if(this.statisticsRenderer.displayMode + 1 > 2) {
            this.statisticsRenderer.displayMode = 0;
          } else {
            this.statisticsRenderer.displayMode++;
          }
          break;
        case 1:
          this.bestiaryRenderer.isListVisible = !this.bestiaryRenderer.isListVisible;
          break;
      }
    }

    if(action == INPUT_ACTION_MENU_DELETE.get()) {
      playMenuSound(2);
      if(this.currentArchiveType + 1 > 1) {
        this.currentArchiveType = 0;
      } else {
        this.currentArchiveType++;
      }
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  public InputPropagation inputActionReleased(final InputAction action) {
    if(super.inputActionReleased(action) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.loadingStage != 2) {
      return InputPropagation.PROPAGATE;
    }

    return InputPropagation.PROPAGATE;
  }
}
