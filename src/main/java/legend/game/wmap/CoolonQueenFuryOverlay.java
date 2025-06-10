package legend.game.wmap;

import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.game.combat.AdditionButtonStyle;
import legend.game.combat.ui.ControllerStyle;
import legend.game.combat.ui.FooterActionsHud;
import legend.game.modding.coremod.CoreMod;
import org.joml.Matrix4f;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.GPU;
import static legend.core.GameEngine.RENDERER;
import static legend.game.Scus94491BpeSegment_8007.vsyncMode_8007a3b8;
import static legend.game.Scus94491BpeSegment_800b.tickCount_800bb0fc;

public class CoolonQueenFuryOverlay {
  /** Wmap.coolonIconStateIndices_800ef154 */
  private static final int[] coolonIconStates = {0, 1, 2, 3, 0};
  /** Wmap.queenFuryIconStateIndices_800ef158 */
  private static final int[] queenFuryIconStates = {0, 0, 0, 0, 1, 2, 3, 4, 4, 4, 4, 3, 2, 1, 0};
  /** Wmap.squareButtonUs_800ef168 */
  private static final int[] buttonStates = {0, 0, 1, 2, 2, 1, 0};

  private final Obj[] buttonSprites = new Obj[3];
  private final Obj[] coolonSprites = new Obj[4];
  private final Obj[] queenFurySprites = new Obj[5];
  private final Matrix4f buttonMatrix = new Matrix4f();

  private ControllerStyle currentStyle;

  public CoolonQueenFuryOverlay() {
    this.buildButton(CONFIG.getConfig(CoreMod.CONTROLLER_STYLE_CONFIG.get()));
    this.buildCoolonIcon();
    this.buildQueenFuryIcon();
  }

  private void buildButton(final ControllerStyle style) {
    this.currentStyle = style;
    for(int i = 0; i < 3; i++) {
      if (style != ControllerStyle.PLAYSTATION) {
        this.buttonSprites[i] = new QuadBuilder("CoolonQfButton")
          .pos(GPU.getOffsetX() + 86.0f, GPU.getOffsetY() + 88.0f, 52.0f)
          .size(16.0f, 16.0f)
          .uv(64 + i * 16, 168)
          .uvSize(1.0f, 1.0f)
          .bpp(Bpp.BITS_24)
          .build();
      } else {
        this.buttonSprites[i] = new QuadBuilder("CoolonQfButton")
          .bpp(Bpp.BITS_4)
          .clut(640, 508)
          .vramPos(640, 256)
          .monochrome(1.0f)
          .pos(GPU.getOffsetX() + 86.0f, GPU.getOffsetY() + 88.0f, 52.0f)
          .size(16.0f, 16.0f)
          .uv(64 + i * 16, 168)
          .build();
      }
    }
  }

  private void buildCoolonIcon() {
    for(int i = 0; i < 4; i++) {
      this.coolonSprites[i] = new QuadBuilder("CoolonIcon")
        .bpp(Bpp.BITS_4)
        .clut(640, 506)
        .vramPos(640, 256)
        .monochrome(1.0f)
        .pos(GPU.getOffsetX() + 106.0f, GPU.getOffsetY() + 80.0f, 52.0f)
        .size(32.0f, 16.0f)
        .uv(i * 32.0f, 128.0f)
        .build();
    }
  }

  private void buildQueenFuryIcon() {
    for(int i = 0; i < 5; i++) {
      this.queenFurySprites[i] = new QuadBuilder("QueenFuryIcon")
        .bpp(Bpp.BITS_4)
        .clut(640, 507)
        .vramPos(640, 256)
        .monochrome(1.0f)
        .pos(GPU.getOffsetX() + 106.0f, GPU.getOffsetY() + 80.0f, 52.0f)
        .size(24.0f, 24.0f)
        .uv(i * 24.0f, 144.0f)
        .build();
    }
  }

  /** @param mode 0: Coolon icon, 1: Queen Fury icon */
  public void render(final int mode) {
    final int buttonState = buttonStates[(int)(tickCount_800bb0fc / 2 / (3.0f / vsyncMode_8007a3b8) % 7)];
    final Obj button = this.buttonSprites[buttonState];
    final ControllerStyle style = CONFIG.getConfig(CoreMod.CONTROLLER_STYLE_CONFIG.get());

    if (this.currentStyle != style) {
      this.buildButton(style);
    }

    if (style != ControllerStyle.PLAYSTATION) {
      Texture texture = null;
      if (style == ControllerStyle.XBOX) texture = FooterActionsHud.textures[12];
      else if (style == ControllerStyle.NINTENDO) texture = FooterActionsHud.textures[16];
      for (int i = 0; i <= buttonState; i++) {
        this.buttonMatrix.translation((int)RENDERER.getWidescreenOrthoOffsetX(), i - buttonState, 52f + i);
        RENDERER
          .queueOrthoModel(button, this.buttonMatrix, QueuedModelStandard.class)
          .texture(texture);
      }
    } else {
      RENDERER.queueOrthoModel(button, QueuedModelStandard.class);
    }

    final int iconState;
    final Obj icon;
    if(mode == 0) {
      iconState = coolonIconStates[(int)(tickCount_800bb0fc / 2 / (3.0f / vsyncMode_8007a3b8) % 5)];
      icon = this.coolonSprites[iconState];
    } else {
      iconState = queenFuryIconStates[(int)(tickCount_800bb0fc / 3 / (3.0f / vsyncMode_8007a3b8) % 15)];
      icon = this.queenFurySprites[iconState];
    }

    RENDERER.queueOrthoModel(icon, QueuedModelStandard.class);
  }

  public void deallocate() {
    for(final Obj button : this.buttonSprites) {
      button.delete();
    }

    for(final Obj icon : this.coolonSprites) {
      icon.delete();
    }

    for(final Obj icon : this.queenFurySprites) {
      icon.delete();
    }
  }
}
