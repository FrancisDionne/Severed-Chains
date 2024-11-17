package legend.game.combat.ui;

import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.game.SItem;
import legend.game.input.InputAction;
import legend.game.inventory.screens.TextColour;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static legend.core.GameEngine.RENDERER;
import static legend.game.Scus94491BpeSegment_8002.textWidth;

public final class MenuFooter {
  private static final Matrix4f m = new Matrix4f();
  private static final Obj quad = new QuadBuilder("Footer Text background")
    .rgb(1.0f, 1.0f, 1.0f)
    .size(1.0f, 1.0f)
    .uv(0.0f, 0.0f)
    .uvSize(1.0f, 1.0f)
    .bpp(Bpp.BITS_24)
    .build();

  private static final HashMap<String, List<FooterAction>> actionsMap = new HashMap<>();

  public static Texture[] textures = {
    Texture.png(Path.of("gfx", "ui", "menuButton_Cross.png")),     //0
    Texture.png(Path.of("gfx", "ui", "menuButton_Square.png")),    //1
    Texture.png(Path.of("gfx", "ui", "menuButton_Triangle.png")),  //2
    Texture.png(Path.of("gfx", "ui", "menuButton_Circle.png")),    //3
  };

  private static Texture getTexture(final FooterAction footerAction) {
    return switch(footerAction.input) {
      case InputAction.BUTTON_SOUTH -> textures[0];
      case InputAction.BUTTON_NORTH -> textures[2];
      case InputAction.BUTTON_WEST -> textures[1];
      case InputAction.BUTTON_EAST -> textures[3];
      default -> null;
    };
  }

  private static String getText(final FooterAction footerAction) {
    return switch(footerAction.action) {
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

  public static void render(final String key) {
    if(!actionsMap.containsKey(key)) {
      return;
    }

    int x = 358;

    for(final FooterAction footAction : actionsMap.get(key)) {
      final int orthoOffsetX = (int)RENDERER.getWidescreenOrthoOffsetX();
      final String text = getText(footAction);
      final int textWidth = textWidth(text);
      x = x - textWidth;

      SItem.renderText(text, orthoOffsetX + x, 226, TextColour.BROWN);

      x -= 14;

      m.translation(orthoOffsetX + x, 226, 120);
      m.scale(12, 12, 1f);

      RENDERER
        .queueOrthoModel(quad, m, QueuedModelStandard.class)
        .texture(getTexture(footAction));

      x -= 8;
    }
  }

  public static void setFooterActions(final String key, @Nullable final FooterAction action1, @Nullable final FooterAction action2, @Nullable final FooterAction action3, @Nullable final FooterAction action4, @Nullable final FooterAction action5) {
    final List<FooterAction> actions;
    if(!actionsMap.containsKey(key)) {
      actions = new ArrayList<>();
      actionsMap.put(key, actions);
    } else {
      actions = actionsMap.get(key);
    }
    actions.clear();
    if(action1 != null) actions.add(action1);
    if(action2 != null) actions.add(action2);
    if(action3 != null) actions.add(action3);
    if(action4 != null) actions.add(action4);
    if(action5 != null) actions.add(action5);
  }

  public static void setFooterActions(final String key, final FooterAction action1) {
    setFooterActions(key, action1, null, null, null, null);
  }

  public static void setFooterActions(final String key, final FooterAction action1, final FooterAction action2) {
    setFooterActions(key, action1, action2, null, null, null);
  }

  public static void setFooterActions(final String key, final FooterAction action1, final FooterAction action2, final FooterAction action3) {
    setFooterActions(key, action1, action2, action3, null, null);
  }

  public static void setFooterActions(final String key, final FooterAction action1, final FooterAction action2, final FooterAction action3, final FooterAction action4) {
    setFooterActions(key, action1, action2, action3, action4, null);
  }

  public static void setFooterActions(final String key, final boolean typicalButtons, @Nullable final FooterAction action3, @Nullable final FooterAction action4, @Nullable final FooterAction action5) {
    setFooterActions(key, new FooterAction(FooterActions.SELECT, InputAction.BUTTON_SOUTH), new FooterAction(FooterActions.BACK, InputAction.BUTTON_EAST), action3, action4, action5);
  }

  public static void setFooterActions(final String key, final boolean typicalButtons, @Nullable final FooterAction action3, @Nullable final FooterAction action4) {
    setFooterActions(key, typicalButtons, action3, action4, null);
  }

  public static void setFooterActions(final String key, final boolean typicalButtons, @Nullable final FooterAction action3) {
    setFooterActions(key, typicalButtons, action3, null, null);
  }

  public static void setTypicalFooterActions(final String key) {
    setFooterActions(key, true, null, null, null);
  }

  public static void clearFooterActions(final String key) {
    actionsMap.remove(key);
  }
}