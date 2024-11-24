package legend.game.combat.ui;

import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.game.SItem;
import legend.game.input.InputAction;
import legend.game.inventory.screens.TextColour;
import legend.game.modding.coremod.CoreMod;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Arrays;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.RENDERER;
import static legend.game.Scus94491BpeSegment_8002.textWidth;

public final class FooterActionsHud {
  private static final Matrix4f m = new Matrix4f();
  private static final Obj quad = new QuadBuilder("Footer Text Background")
    .size(1.0f, 1.0f)
    .uv(0.0f, 0.0f)
    .uvSize(1.0f, 1.0f)
    .bpp(Bpp.BITS_24)
    .build();

  private static final FooterAction[] actions = new FooterAction[5];
  private static int style = 0; //0 = Menu, 1 = Battle

  public static Texture[] textures = {
    Texture.png(Path.of("gfx", "ui", "Small_Button_Playstation_Cross.png")),      //0
    Texture.png(Path.of("gfx", "ui", "Small_Button_Playstation_Square.png")),     //1
    Texture.png(Path.of("gfx", "ui", "Small_Button_Playstation_Triangle.png")),   //2
    Texture.png(Path.of("gfx", "ui", "Small_Button_Playstation_Circle.png")),     //3
    Texture.png(Path.of("gfx", "ui", "icon-all-equip.png")),        //4
    Texture.png(Path.of("gfx", "ui", "icon-weapon.png")),           //5
    Texture.png(Path.of("gfx", "ui", "icon-helmet.png")),           //6
    Texture.png(Path.of("gfx", "ui", "icon-armor.png")),            //7
    Texture.png(Path.of("gfx", "ui", "icon-boots.png")),            //8
    Texture.png(Path.of("gfx", "ui", "icon-ring.png")),             //9
    Texture.png(Path.of("gfx", "ui", "Small_Button_Xbox_A.png")),     //10
    Texture.png(Path.of("gfx", "ui", "Small_Button_Xbox_B.png")),     //11
    Texture.png(Path.of("gfx", "ui", "Small_Button_Xbox_X.png")),     //12
    Texture.png(Path.of("gfx", "ui", "Small_Button_Xbox_Y.png")),     //13
    Texture.png(Path.of("gfx", "ui", "Small_Button_Nintendo_A.png")),     //14
    Texture.png(Path.of("gfx", "ui", "Small_Button_Nintendo_B.png")),     //15
    Texture.png(Path.of("gfx", "ui", "Small_Button_Nintendo_X.png")),     //16
    Texture.png(Path.of("gfx", "ui", "Small_Button_Nintendo_Y.png")),     //17
    Texture.png(Path.of("gfx", "ui", "icon-sort-alpha-asc.png")),     //18
    Texture.png(Path.of("gfx", "ui", "icon-sort-power-desc.png")),     //19
    Texture.png(Path.of("gfx", "ui", "icon-sort-icon-asc.png")),     //20
  };

  private static Texture getTexture(final InputAction inputAction) {
    final ControllerStyle style = CONFIG.getConfig(CoreMod.CONTROLLER_STYLE_CONFIG.get());
    if (style == ControllerStyle.XBOX) {
      return switch(inputAction) {
        case InputAction.BUTTON_SOUTH -> textures[10];
        case InputAction.BUTTON_WEST -> textures[12];
        case InputAction.BUTTON_NORTH -> textures[13];
        case InputAction.BUTTON_EAST -> textures[11];
        default -> null;
      };
    }
    if (style == ControllerStyle.NINTENDO) {
      return switch(inputAction) {
        case InputAction.BUTTON_SOUTH -> textures[14];
        case InputAction.BUTTON_WEST -> textures[16];
        case InputAction.BUTTON_NORTH -> textures[17];
        case InputAction.BUTTON_EAST -> textures[15];
        default -> null;
      };
    }
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
      case FooterActions.ADDITIONS -> "Additions";
    };
  }

  private static TextColour getColor() {
    return switch(CONFIG.getConfig(CoreMod.FOOTER_ACTION_COLOR_CONFIG.get())) {
      case FooterActionColor.FOOTER_BROWN -> TextColour.FOOTER_BROWN;
      case FooterActionColor.FOOTER_WHITE -> TextColour.FOOTER_WHITE;
      case FooterActionColor.FOOTER_GREY -> TextColour.FOOTER_GREY;
      case FooterActionColor.FOOTER_RED -> TextColour.FOOTER_RED;
      case FooterActionColor.FOOTER_PINK -> TextColour.FOOTER_PINK;
      case FooterActionColor.FOOTER_PURPLE -> TextColour.FOOTER_PURPLE;
      case FooterActionColor.FOOTER_BLUE -> TextColour.FOOTER_BLUE;
      case FooterActionColor.FOOTER_AQUA -> TextColour.FOOTER_AQUA;
      case FooterActionColor.FOOTER_GREEN -> TextColour.FOOTER_GREEN;
      case FooterActionColor.FOOTER_LIME -> TextColour.FOOTER_LIME;
      case FooterActionColor.FOOTER_YELLOW -> TextColour.FOOTER_YELLOW;
      case FooterActionColor.FOOTER_ORANGE -> TextColour.FOOTER_ORANGE;
      case FooterActionColor.FOOTER_HIDDEN -> null;
    };
  }

  private FooterActionsHud() {
  }

  public static void render() {
    final TextColour color = getColor();


    if (color != null) {
      final int xOffset = (int)RENDERER.getWidescreenOrthoOffsetX();
      int x;
      final int y;
      String text;
      int textWidth;

      if(style == 1) { // Battle
        x = 250;
        y = 227;
      } else { // Menu
        x = 358;
        y = 226;
      }

      x += 8;

      for(final FooterAction footAction : actions) {
        if(footAction != null) {
          x -= 8;

          if(footAction.secondaryTexture != null) {
            text = ")";
            textWidth = textWidth(text);
            x -= textWidth;

            SItem.renderText(text, x, y + 1, color);

            x -= 11;

            m.translation(x + xOffset, y, 120);
            m.scale(12, 12, 1);

            RENDERER
              .queueOrthoModel(quad, m, QueuedModelStandard.class)
              .texture(footAction.secondaryTexture);

            x -= 1;

            text = "(";
            textWidth = textWidth(text);
            x -= textWidth;

            SItem.renderText(text, x, y + 1, color);

            x -= 2;
          }

          text = getText(footAction.action);
          textWidth = textWidth(text);
          x -= textWidth;

          SItem.renderText(text, x, y, color);

          x -= 14;

          m.translation(x + xOffset, y, 120);
          m.scale(12, 12, 1);

          RENDERER
            .queueOrthoModel(quad, m, QueuedModelStandard.class)
            .texture(getTexture(footAction.input));
        }
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
      case FooterActions.SORT, FooterActions.ADDITIONS -> InputAction.BUTTON_NORTH;
      case FooterActions.SELECT -> InputAction.BUTTON_SOUTH;
    };
    return new FooterAction(action, input);
  }

  public static void setFootActions(final int style, @Nullable final FooterActions action1, @Nullable final FooterActions action2, @Nullable final FooterActions action3, @Nullable final FooterActions action4, @Nullable final FooterActions action5) {
    FooterActionsHud.style = style;
    if(!compareFooterActions(action1, action2, action3, action4, action5)) {
      actions[0] = newFooterAction(action1);
      actions[1] = newFooterAction(action2);
      actions[2] = newFooterAction(action3);
      actions[3] = newFooterAction(action4);
      actions[4] = newFooterAction(action5);
    }
  }

  public static void renderActions(final int style, @Nullable final FooterActions action1, @Nullable final FooterActions action2, @Nullable final FooterActions action3, @Nullable final FooterActions action4, @Nullable final FooterActions action5) {
    if(!compareFooterActions(action1, action2, action3, action4, action5)) {
      setFootActions(style, action1, action2, action3, action4, action5);
    }
    render();
  }

  public static void setMenuActions(@Nullable final FooterActions action3, @Nullable final FooterActions action4, @Nullable final FooterActions action5) {
    setFootActions(0, FooterActions.SELECT, FooterActions.BACK, action3, action4, action5);
  }

  public static void renderMenuActions(@Nullable final FooterActions action3, @Nullable final FooterActions action4, @Nullable final FooterActions action5) {
    renderActions(0, FooterActions.SELECT, FooterActions.BACK, action3, action4, action5);
  }

  public static void renderMenuActions() {
    renderActions(0, FooterActions.SELECT, FooterActions.BACK, null, null, null);
  }

  public static void setBattleActions(@Nullable final FooterActions action4, @Nullable final FooterActions action5) {
    setFootActions(1, FooterActions.SELECT, FooterActions.BACK, FooterActions.ADDITIONS, action4, action5);
  }

  public static void renderBattleActions(@Nullable final FooterActions action4, @Nullable final FooterActions action5) {
    renderActions(1, FooterActions.SELECT, FooterActions.BACK, FooterActions.ADDITIONS, action4, action5);
  }

  public static void renderBattleActions() {
    renderActions(1, FooterActions.SELECT, FooterActions.BACK, FooterActions.ADDITIONS, null, null);
  }

  public static void setSecondaryText(final int actionIndex, final Texture texture) {
    if(actions[actionIndex] != null) {
      actions[actionIndex].secondaryTexture = texture;
    }
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