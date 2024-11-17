package legend.game.combat.ui;

import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.game.SItem;
import legend.game.input.Input;
import legend.game.input.InputAction;
import legend.game.inventory.screens.TextColour;
import legend.game.modding.coremod.CoreMod;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Arrays;

import static legend.core.GameEngine.RENDERER;
import static legend.game.Scus94491BpeSegment_8002.textWidth;

public final class MenuFooter {
  private static final Matrix4f m = new Matrix4f();
  private static final Obj quad = new QuadBuilder("Footer Text background")
    .size(1.0f, 1.0f)
    .uv(0.0f, 0.0f)
    .uvSize(1.0f, 1.0f)
    .bpp(Bpp.BITS_24)
    .build();

  private static final FooterAction[] actions = new FooterAction[5];

  public static Texture[] textures = {
    Texture.png(Path.of("gfx", "ui", "menuButton_Cross.png")),     //0
    Texture.png(Path.of("gfx", "ui", "menuButton_Square.png")),    //1
    Texture.png(Path.of("gfx", "ui", "menuButton_Triangle.png")),  //2
    Texture.png(Path.of("gfx", "ui", "menuButton_Circle.png")),    //3
  };

  private static Texture getTexture(final InputAction inputAction) {
    return switch(inputAction) {
      case InputAction.BUTTON_SOUTH -> textures[0];
      case InputAction.BUTTON_WEST -> textures[1];
      case InputAction.BUTTON_NORTH -> textures[2];
      case InputAction.BUTTON_EAST -> textures[3];
      default -> null;
    };
  }

  private static String getText(final FooterActions footerAction) {
    return switch(footerAction) {
      case FooterActions.DELETE -> "Delete";
      case FooterActions.SORT -> "Sort";
      case FooterActions.FILTER -> "Filter";
      case FooterActions.DISCARD -> "Discard";
      case FooterActions.SELECT -> "Select";
      case FooterActions.BACK -> "Back";
    };
  }

  private MenuFooter() {
  }

  public static void render() {
    int x = 358 + 8 + (int)RENDERER.getWidescreenOrthoOffsetX();

    for(final FooterAction footAction : actions) {
      if(footAction != null) {
        x -= 8;

        final String text = getText(footAction.action);
        final int textWidth = textWidth(text);
        x -= textWidth;

        SItem.renderText(text, x, 226, TextColour.BROWN);

        x -= 14;

        m.translation(x, 226, 120);
        m.scale(12, 12, 1f);

        RENDERER
          .queueOrthoModel(quad, m, QueuedModelStandard.class)
          .texture(getTexture(footAction.input));
      }
    }
  }

  private static FooterAction newFooterAction(@Nullable final FooterActions action) {
    if(action == null) {
      return null;
    }
    final InputAction input = switch(action) {
      case FooterActions.BACK -> InputAction.BUTTON_EAST;
      case FooterActions.DELETE, FooterActions.FILTER, FooterActions.DISCARD -> InputAction.BUTTON_WEST;
      case FooterActions.SORT -> InputAction.BUTTON_NORTH;
      case FooterActions.SELECT -> InputAction.BUTTON_SOUTH;
    };
    return new FooterAction(action, input);
  }

  public static void setFootActions(@Nullable final FooterActions action1, @Nullable final FooterActions action2, @Nullable final FooterActions action3, @Nullable final FooterActions action4, @Nullable final FooterActions action5) {
    actions[0] = newFooterAction(action1);
    actions[1] = newFooterAction(action2);
    actions[2] = newFooterAction(action3);
    actions[3] = newFooterAction(action4);
    actions[4] = newFooterAction(action5);
  }

  public static void setTypicalActions() {
    setFootActions(FooterActions.SELECT, FooterActions.BACK, null, null, null);
  }

  public static void renderFooterActions(@Nullable final FooterActions action1, @Nullable final FooterActions action2, @Nullable final FooterActions action3, @Nullable final FooterActions action4, @Nullable final FooterActions action5) {
    if (!compareFooterActions(action1, action2, action3, action4, action5)) {
      setFootActions(action1, action2, action3, action4, action5);
    }
    render();
  }

  public static void renderFooterActions(final FooterActions action1) {
    renderFooterActions(action1, null, null, null, null);
  }

  public static void renderFooterActions(final FooterActions action1, final FooterActions action2) {
    renderFooterActions(action1, action2, null, null, null);
  }

  public static void renderFooterActions(final FooterActions action1, final FooterActions action2, final FooterActions action3) {
    renderFooterActions(action1, action2, action3, null, null);
  }

  public static void renderFooterActions(final FooterActions action1, final FooterActions action2, final FooterActions action3, final FooterActions action4) {
    renderFooterActions(action1, action2, action3, action4, null);
  }

  public static void renderTypicalFooterActions(@Nullable final FooterActions action3, @Nullable final FooterActions action4, @Nullable final FooterActions action5) {
    renderFooterActions(FooterActions.SELECT, FooterActions.BACK, action3, action4, action5);
  }

  public static void renderTypicalFooterActions(@Nullable final FooterActions action3, @Nullable final FooterActions action4) {
    renderTypicalFooterActions(action3, action4, null);
  }

  public static void renderTypicalFooterActions(@Nullable final FooterActions action3) {
    renderTypicalFooterActions(action3, null, null);
  }

  public static void renderTypicalFooterActions() {
    renderTypicalFooterActions(null, null, null);
  }

  private static boolean compareFooterActions(@Nullable final FooterActions action1, @Nullable final FooterActions action2, @Nullable final FooterActions action3, @Nullable final FooterActions action4, @Nullable final FooterActions action5) {
    if (!compareFooterAction(actions[0], action1)) return false;
    if (!compareFooterAction(actions[1], action2)) return false;
    if (!compareFooterAction(actions[2], action3)) return false;
    if (!compareFooterAction(actions[3], action4)) return false;
    if (!compareFooterAction(actions[4], action5)) return false;
    return true;
  }

  private static boolean compareFooterAction(@Nullable final FooterAction action1, @Nullable final FooterActions action2) {
    return (action1 == null && action2 == null) || (action1 != null && action2 != null && action1.action == action2);
  }

  public static void clearFooterActions() {
    Arrays.fill(actions, null);
  }
}