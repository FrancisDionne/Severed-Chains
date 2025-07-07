package legend.game.inventory.screens;

import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputMod;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;

import static legend.game.SItem.setMessageBoxFontOptions;
import static legend.game.SItem.setMessageBoxText;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_CONFIRM;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_LEFT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_RIGHT;

public class MessageBoxCycleScreen extends MessageBoxScreen {
  /** Allows list wrapping, but only on new input */

  private static final int ARROWS_MAX_TICKS = 90;

  private final String[] texts;
  private final FontOptions[] fonts;
  private int currentTextIndex;
  private int arrowsTick;

  private final FontOptions titleFont = new FontOptions().colour(TextColour.GOLD).shadowColour(TextColour.DARKER_GREY).horizontalAlign(HorizontalAlign.CENTRE).size(0.9f);

  public MessageBoxCycleScreen(final String[] texts, final int type, final Consumer<MessageBoxResults> onResult) {
    this(texts, null, "Yes", "No", type, onResult);
  }

  public MessageBoxCycleScreen(final String[] texts, final String yes, final String no, final int type, final Consumer<MessageBoxResults> onResult) {
    this(texts, null, yes, no, type, onResult);
  }

  public MessageBoxCycleScreen(final String[] texts, final FontOptions[] fonts, final int type, final Consumer<MessageBoxResults> onResult) {
    this(texts, fonts, "Yes", "No", type, onResult);
  }

  public MessageBoxCycleScreen(final String[] texts, @Nullable final FontOptions[] fonts, final String yes, final String no, final int type, final Consumer<MessageBoxResults> onResult) {
    super("", yes, no, type, onResult);
    this.texts = texts;
    this.fonts = fonts;
    this.currentTextIndex = 0;
    this.arrowsTick = 0;
    this.setText();
  }

  private void menuNavigateLeft() {
    playMenuSound(1);
    this.currentTextIndex = this.currentTextIndex - 2 < 0 ? this.texts.length - 2 : this.currentTextIndex - 2;
    this.arrowsTick = 0;
    this.setText();
  }

  private void menuNavigateRight() {
    playMenuSound(1);
    this.currentTextIndex = this.currentTextIndex + 2 > this.texts.length - 2 ? 0 : this.currentTextIndex + 2;
    this.arrowsTick = 0;
    this.setText();
  }

  private void setText() {
    setMessageBoxText(this.messageBox, this.getText(this.currentTextIndex));
    if(this.fonts != null) {
      setMessageBoxFontOptions(this.messageBox, 0, this.titleFont);
      for(int i = 1; i < this.messageBox.text_00.length; i++) {
        setMessageBoxFontOptions(this.messageBox, i, this.fonts[this.currentTextIndex / 2]);
      }
    }
  }

  private String getText(final int textIndex) {
    final boolean b = this.arrowsTick < (ARROWS_MAX_TICKS / 2);
    return (b ? "< " : "<< ") + this.texts[textIndex] + (b ? " >" : " >>")  + '\n' + this.texts[textIndex + 1];
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
    super.menuSelect(this.currentTextIndex / 2);
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
    return super.mouseClick(x, y, button, mods, this.currentTextIndex / 2);
  }
}
