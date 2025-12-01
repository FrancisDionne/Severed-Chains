package legend.game.combat.ui;

import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.TextColour;
import legend.game.types.Translucency;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static legend.core.GameEngine.DEFAULT_FONT;
import static legend.core.GameEngine.RENDERER;
import static legend.game.Text.renderText;

public final class TrackerHud {
  private static final Matrix4f m = new Matrix4f();
  private static final Obj quad = new QuadBuilder("Turn Order Hud")
    .size(1.0f, 1.0f)
    .uv(0.0f, 0.0f)
    .uvSize(1.0f, 1.0f)
    .bpp(Bpp.BITS_24)
    .build();

  private static final Texture[] textures = {
    Texture.png(Path.of("gfx", "ui", "archive_screen", "bestiary", "black.png")), //0
  };

  private static final FontOptions textFont = new FontOptions().colour(TextColour.WHITE).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
  private static final FontOptions progressFont = new FontOptions().colour(TextColour.YELLOW).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);

  private static final LinkedHashMap<String, Tracker> trackers = new LinkedHashMap<>();
  private static boolean isVisible;

  public static class Tracker {
    public String text;
    public int number;
    public int total;
    public Tracker(final String text, final int number, final int total) {
      this.text = text;
      this.number = number;
      this.total = total;
    }
  }

  private TrackerHud() {
  }

  public static void add(final String key, final String text, final int number, final int total) {
    trackers.put(key, new Tracker(text, number, total));
    isVisible = true;
  }

  public static void remove(final String key) {
    trackers.remove(key);
    isVisible = !trackers.isEmpty();
  }

  public static boolean exists(final String key) {
    return trackers.containsKey(key);
  }

  public static void show() {
    isVisible = true;
  }

  public static void hide() {
    isVisible = false;
  }

  public static void removeTrackers(@Nullable final String type) {
    final ArrayList<String> trackersToRemove = new ArrayList<>();
    for(final String key : trackers.keySet()) {
      if(type == null || key.startsWith(type)) {
        trackersToRemove.add(key);
      }
    }
    for(final String key : trackersToRemove) {
      trackers.remove(key);
    }
  }

  public static HashMap<String, Tracker> getTrackers(final String type) {
    final HashMap<String, Tracker> t = new HashMap<>();
    for(final Map.Entry<String, Tracker> entry : trackers.entrySet()) {
      if(entry.getKey().startsWith(type + ':')) {
        t.put(entry.getKey(), entry.getValue());
      }
    }
    return t;
  }

  public static void render() {
    if(!isVisible || trackers.isEmpty()) {
      return;
    }

    final int xOffset = (int)RENDERER.getWidescreenOrthoOffsetX();

    float maxWidth = 0;
    int y = 6;
    for(final Tracker tracker : trackers.values()) {
      final String progressText = tracker.number + "/" + tracker.total;
      final float textWidth1 = DEFAULT_FONT.textWidth(tracker.text) * textFont.getSize();
      final float textWidth2 = DEFAULT_FONT.textWidth(progressText) * textFont.getSize();
      final float fullWidth = textWidth1 + textWidth2 + 8;

      renderText(tracker.text, 5 - xOffset, y, textFont, 120);
      renderText(progressText, textWidth1 + 8 - xOffset, y, progressFont, 120);

      y += 7;

      if(fullWidth > maxWidth) {
        maxWidth = fullWidth;
      }
    }

    m.translation(3 - xOffset, 3, 121);
    m.scale(maxWidth - 1, (trackers.size() * 7) + 3, 1);

    RENDERER
      .queueOrthoModel(quad, m, QueuedModelStandard.class)
      .texture(textures[0]) //Black
      .alpha(0.6f)
      .translucency(Translucency.HALF_B_PLUS_HALF_F);
  }
}