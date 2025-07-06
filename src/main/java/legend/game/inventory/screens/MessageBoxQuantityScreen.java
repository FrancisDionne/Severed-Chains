package legend.game.inventory.screens;

import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputMod;

import java.util.Set;
import java.util.function.Consumer;

import static legend.game.SItem.setMessageBoxText;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_CONFIRM;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_LEFT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_RIGHT;

public class MessageBoxQuantityScreen extends MessageBoxScreen {
  /** Allows list wrapping, but only on new input */

  private final String messageText;
  private final int minQuantity;
  private final int maxQuantity;
  private int currentQuantity;

  public MessageBoxQuantityScreen(final String text, final int minQuantity, final int maxQuantity, final int type, final Consumer<MessageBoxResults> onResult) {
    this(text, "Yes", "No", minQuantity, maxQuantity, type, onResult);
  }

  public MessageBoxQuantityScreen(final String text, final String yes, final String no, final int minQuantity, final int maxQuantity, final int type, final Consumer<MessageBoxResults> onResult) {
    super(text, yes, no, type, onResult);
    this.messageText = text;
    this.minQuantity = minQuantity;
    this.maxQuantity = maxQuantity;
    this.currentQuantity = 1;
    this.setQuantityText();
  }

  private void menuNavigateLeft() {
    playMenuSound(1);
    this.currentQuantity = this.currentQuantity - 1 < this.minQuantity ? this.maxQuantity : this.currentQuantity - 1;
    this.setQuantityText();
  }

  private void menuNavigateRight() {
    playMenuSound(1);
    this.currentQuantity = this.currentQuantity + 1 > this.maxQuantity ? this.minQuantity : this.currentQuantity + 1;
    this.setQuantityText();
  }

  private void setQuantityText() {
    final String newText = this.messageText.replace("[#]", Integer.toString(this.currentQuantity));
    setMessageBoxText(this.messageBox, newText);
  }

  @Override
  protected void menuSelect() {
    super.menuSelect(this.currentQuantity);
  }

  @Override
  public InputPropagation inputActionPressed(final InputAction action, final boolean repeat) {
    if(super.inputActionPressed(action, repeat) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.skipInput()) {
      return InputPropagation.PROPAGATE;
    }

    if(this.messageBox.type_15 == 2) {
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
    return super.mouseClick(x, y, button, mods, this.currentQuantity);
  }
}
