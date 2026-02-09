package legend.game.inventory.screens;

import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputMod;

import java.util.Set;
import java.util.function.Consumer;

import static legend.game.Audio.playMenuSound;
import static legend.game.SItem.setMessageBoxFontOptions;
import static legend.game.SItem.setMessageBoxText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_CONFIRM;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_LEFT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_RIGHT;

public class MessageBoxQuantityScreen extends MessageBoxScreen {
  /** Allows list wrapping, but only on new input */

  private static final int ARROWS_MAX_TICKS = 90;

  private final String messageText;
  private final int minQuantity;
  private final int maxQuantity;
  private int currentQuantity;
  private int arrowsTick;

  private final FontOptions titleFont = new FontOptions().colour(TextColour.BROWN).shadowColour(TextColour.MIDDLE_BROWN).horizontalAlign(HorizontalAlign.CENTRE).size(0.95f);
  private final FontOptions quantityFont = new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.LIGHT_BROWN).horizontalAlign(HorizontalAlign.CENTRE).size(0.85f);

  public MessageBoxQuantityScreen(final String text, final int minQuantity, final int maxQuantity, final int type, final Consumer<MessageBoxResults> onResult) {
    this(text, "Yes", "No", minQuantity, maxQuantity, type, onResult);
  }

  public MessageBoxQuantityScreen(final String text, final String yes, final String no, final int minQuantity, final int maxQuantity, final int type, final Consumer<MessageBoxResults> onResult) {
    super(text, yes, no, type, onResult);
    this.messageText = text;
    this.minQuantity = minQuantity;
    this.maxQuantity = maxQuantity;
    this.currentQuantity = 1;
    this.arrowsTick = 0;
    this.setText();
  }

  private void menuNavigateLeft() {
    playMenuSound(1);
    this.currentQuantity = this.currentQuantity - 1 < this.minQuantity ? this.maxQuantity : this.currentQuantity - 1;
    this.arrowsTick = 0;
    this.setText();
  }

  private void menuNavigateRight() {
    playMenuSound(1);
    this.currentQuantity = this.currentQuantity + 1 > this.maxQuantity ? this.minQuantity : this.currentQuantity + 1;
    this.arrowsTick = 0;
    this.setText();
  }

  private void setText() {
    setMessageBoxText(this.messageBox, this.getText());
    setMessageBoxFontOptions(this.messageBox, 0, this.titleFont);
    setMessageBoxFontOptions(this.messageBox, 1, this.quantityFont);
  }

  private String getText() {
    final boolean b = this.arrowsTick < (ARROWS_MAX_TICKS / 2);
    return this.messageText + '\n' + (b ? "< " : "<< ") + '×' + this.currentQuantity + (b ? " >" : " >>");
  }

  @Override
  protected void render() {
    super.render();

    this.arrowsTick++;
    if(this.arrowsTick > ARROWS_MAX_TICKS) {
      this.arrowsTick = 0;
      this.setText();
    } else if(this.arrowsTick == ARROWS_MAX_TICKS / 2) {
      this.setText();
    }
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
