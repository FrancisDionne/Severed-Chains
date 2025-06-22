package legend.game.combat.ui;

import legend.core.Config;
import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.TextColour;
import org.joml.Matrix4f;

import java.nio.file.Path;
import java.util.List;

import static legend.core.GameEngine.RENDERER;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.Scus94491BpeSegment_8002.textWidth;

public final class TurnOrderHud {
  private static final Matrix4f m = new Matrix4f();
  private static final Obj quad = new QuadBuilder("Turn Order Hud")
    .size(1.0f, 1.0f)
    .uv(0.0f, 0.0f)
    .uvSize(1.0f, 1.0f)
    .bpp(Bpp.BITS_24)
    .build();

  private static final FontOptions activeNameFont = new FontOptions().colour(TextColour.WHITE).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
  private static final FontOptions nameFont = new FontOptions().colour(TextColour.LIGHT_GREY).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
  private static final FontOptions titleFont = new FontOptions().colour(TextColour.YELLOW).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
  private static UiBox turnOrderBox = null;

  private static float currentBoxOffsetX = 0f;
  private static float currentBoxWidth = 0f;
  private static boolean isVisible;

  public static Texture[] textures = {
    Texture.png(Path.of("gfx", "portraits", "dart.png")),    //0
    Texture.png(Path.of("gfx", "portraits", "lavitz.png")),  //1
    Texture.png(Path.of("gfx", "portraits", "shana.png")),   //2
    Texture.png(Path.of("gfx", "portraits", "rose.png")),    //3
    Texture.png(Path.of("gfx", "portraits", "haschel.png")), //4
    Texture.png(Path.of("gfx", "portraits", "albert.png")),  //5
    Texture.png(Path.of("gfx", "portraits", "meru.png")),    //6
    Texture.png(Path.of("gfx", "portraits", "kongol.png")),  //7
    Texture.png(Path.of("gfx", "portraits", "miranda.png")), //8
    Texture.png(Path.of("gfx", "ui", "skull_icon.png")),       //9
    Texture.png(Path.of("gfx", "ui", "turn_order_arrow.png")), //10
  };

  private TurnOrderHud() {
  }

  public static boolean toggleVisibility() {
    isVisible = !isVisible;
    return isVisible;
  }

  public static void show() {
    isVisible = true;
  }

  public static void hide() {
    isVisible = false;
  }

  public static boolean isVisible() {
    return isVisible;
  }

  public static void render(final List<TurnOrderInfo> sortedBents) {
    final int xOffset = (int)RENDERER.getWidescreenOrthoOffsetX();
    float longestName = 0f;
    for(int bentIndex = 0; bentIndex < sortedBents.size(); bentIndex++) {
      final TurnOrderInfo info = sortedBents.get(bentIndex);
      final String name = info.bent.getName();
      final boolean isPlayer = info.bent instanceof PlayerBattleEntity;
      final Texture portrait = textures[isPlayer ? info.bent.charId_272 : 9];
      final float y = bentIndex * 8f + 6 + 6;

      longestName = Math.max(textWidth(name) * (nameFont.getSize() + 0.55f), longestName);

      m.translation(2, y - (isPlayer ? 2.7f : 2.2f), 120);
      m.scale(isPlayer ? 6f : 6.2f, isPlayer ? 7f : 6.8f, 1);

      RENDERER
        .queueOrthoModel(quad, m, QueuedModelStandard.class)
        .texture(portrait);

      renderText(name, 9 - xOffset, y, bentIndex == 0 ? activeNameFont : nameFont, 120);
    }

    final float boxWidth = Math.max(longestName - 13, 44);
    if (turnOrderBox == null || currentBoxWidth != boxWidth || currentBoxOffsetX != xOffset) {
      currentBoxWidth = boxWidth;
      currentBoxOffsetX = xOffset;
      turnOrderBox = new UiBox("Turn Order Box", -1 - xOffset, 0f, currentBoxWidth, 67f);
    }

    turnOrderBox.render(Config.changeBattleRgb() ? Config.getBattleRgb() : Config.defaultUiColour);
    renderText("Turn Order", 1 - xOffset, 2, titleFont, 120);

    m.translation(36, 0.5f, 120);
    m.scale(6, 6, 1);

    RENDERER
      .queueOrthoModel(quad, m, QueuedModelStandard.class)
      .texture(textures[10]);
  }
}