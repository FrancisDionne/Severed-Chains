package legend.game.inventory.screens;

import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static legend.game.SItem.setMessageBoxText;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_CONFIRM;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_LEFT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_RIGHT;

public class MessageBoxInfoScreen extends MessageBoxScreen {
  /** Allows list wrapping, but only on new input */

  private final List<String> messageTexts;
  private int currentPage;

  public MessageBoxInfoScreen(final String text, final String ok, final int type) {
    super(text, ok, type, null);
    this.messageTexts = new ArrayList<>();
    this.messageTexts.add(text);
  }

  public MessageBoxInfoScreen(final String text, final String ok, final int type, final Consumer<MessageBoxResults> onResult) {
    super(text, ok, type, onResult);
    this.messageTexts = new ArrayList<>();
    this.messageTexts.add(text);
  }

  public MessageBoxInfoScreen(final List<String> texts, final String ok, final int type) {
    super(texts.getFirst(), ok, type, null);
    this.messageTexts = new ArrayList<>();
    this.messageTexts.addAll(texts);
  }

  public MessageBoxInfoScreen(final List<String> texts, final String ok, final int type, final Consumer<MessageBoxResults> onResult) {
    super(texts.getFirst(), ok, type, onResult);
    this.messageTexts = new ArrayList<>();
    this.messageTexts.addAll(texts);
  }

  private void setText() {
    setMessageBoxText(this.messageBox, this.messageTexts.get(this.currentPage));
  }

  private void menuNavigateLeft() {
    if(this.currentPage > 0) {
      playMenuSound(1);
      this.currentPage--;
      this.setText();
    }
  }

  private void menuNavigateRight() {
    if(this.currentPage < this.messageTexts.size() - 1) {
      playMenuSound(1);
      this.currentPage++;
      this.setText();
    }
  }

  @Override
  protected void menuSelect() {
    super.menuSelect();
  }

  @Override
  public InputPropagation inputActionPressed(final InputAction action, final boolean repeat) {
    if(super.inputActionPressed(action, repeat) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.skipInput()) {
      return InputPropagation.PROPAGATE;
    }

    if(this.messageBox.type_15 == 2 || this.messageBox.type_15 == 1) {
      if(action == INPUT_ACTION_MENU_CONFIRM.get() && !repeat) {
        this.menuSelect();
        return InputPropagation.HANDLED;
      }

      if(action == INPUT_ACTION_MENU_LEFT.get()) {
        this.menuNavigateLeft();
        return InputPropagation.HANDLED;
      }

      if(action == INPUT_ACTION_MENU_RIGHT.get()) {
        this.menuNavigateRight();
        return InputPropagation.HANDLED;
      }
    }

    return super.inputActionPressed(action, repeat);
  }

  @Override
  protected InputPropagation mouseClick(final int x, final int y, final int button, final Set<InputMod> mods) {
    return super.mouseClick(x, y, button, mods);
  }
}
