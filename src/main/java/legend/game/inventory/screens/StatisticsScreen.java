package legend.game.inventory.screens;

import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputMod;
import legend.game.combat.ui.FooterActionsHud;
import legend.game.statistics.Statistics;
import legend.game.types.Translucency;
import org.joml.Matrix4f;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static legend.core.GameEngine.RENDERER;
import static legend.game.SItem.FUN_801034cc;
import static legend.game.SItem.allocateUiElement;
import static legend.game.Scus94491BpeSegment.startFadeEffect;
import static legend.game.Scus94491BpeSegment_8002.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_END;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_HOME;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_LEFT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_PAGE_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_PAGE_UP;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_RIGHT;

public class StatisticsScreen extends MenuScreen {

  private static class StatisticPage {
    public List<Statistics.Stats> stats;
    public String name;
    public StatisticPage(final String name, final List<Statistics.Stats> stats) {
      this.name = name;
      this.stats = stats;
    }
  }

  private int loadingStage;
  private final Runnable unload;

  private HashMap<Integer, StatisticPage> statisticPages;
  private int pageIndex;

  private static final Matrix4f m = new Matrix4f();
  private static final Obj quad = new QuadBuilder("Statistics Quad")
    .rgb(1f, 1f, 1f)
    .size(1.0f, 1.0f)
    .uv(0.0f, 0.0f)
    .uvSize(1.0f, 1.0f)
    .bpp(Bpp.BITS_24)
    .build();

  public static Texture[] portraits = {
    Texture.png(Path.of("gfx", "portraits", "dart.png")),    //0
    Texture.png(Path.of("gfx", "portraits", "lavitz.png")),  //1
    Texture.png(Path.of("gfx", "portraits", "shana.png")),   //2
    Texture.png(Path.of("gfx", "portraits", "rose.png")),    //3
    Texture.png(Path.of("gfx", "portraits", "haschel.png")), //4
    Texture.png(Path.of("gfx", "portraits", "albert.png")),  //5
    Texture.png(Path.of("gfx", "portraits", "meru.png")),    //6
    Texture.png(Path.of("gfx", "portraits", "kongol.png")),  //7
    Texture.png(Path.of("gfx", "portraits", "miranda.png")), //8
    Texture.png(Path.of("gfx", "ui", "background-lines.png")),  //9
    Texture.png(Path.of("gfx", "ui", "column-line.png")),       //10
  };

  private static final FontOptions labelFont = new FontOptions().colour(TextColour.BROWN).shadowColour(TextColour.MIDDLE_BROWN).size(0.8f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions statFont = new FontOptions().colour(TextColour.LIGHT_GREY_WHITE).shadowColour(TextColour.DARK_GREY).size(0.38f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions numberFont = new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.MIDDLE_BROWN).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions highNumberFont = new FontOptions().colour(TextColour.FOOTER_GREEN).shadowColour(TextColour.DARK_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions lowNumberFont = new FontOptions().colour(TextColour.FOOTER_RED).shadowColour(TextColour.DARK_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions totalFont = new FontOptions().colour(TextColour.FOOTER_YELLOW).shadowColour(TextColour.DARK_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions notApplicableFont = new FontOptions().colour(TextColour.GREY).shadowColour(TextColour.MIDDLE_BROWN).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);

  public StatisticsScreen(final Runnable unload) {
    this.unload = unload;
    this.loadPages();
    this.pageIndex = 0;
  }

  private void loadPages() {
    this.statisticPages = new HashMap<>();
    this.addPage(new StatisticPage("Battle", this.getPage0()));
    this.addPage(new StatisticPage("Something", this.getPage1()));
    this.addPage(new StatisticPage("Thing", this.getPage2()));
  }

  private void addPage(final StatisticPage page) {
    this.statisticPages.put(this.statisticPages.size(), page);
  }

  private ArrayList<Statistics.Stats> getPage0() {
    final ArrayList<Statistics.Stats> l = new ArrayList<>();
    l.add(Statistics.Stats.TOTAL_DAMAGE);
    l.add(Statistics.Stats.TOTAL_PHYSICAL_DAMAGE);
    l.add(Statistics.Stats.TOTAL_MAGICAL_DAMAGE);
    l.add(Statistics.Stats.TOTAL_TAKEN);
    l.add(Statistics.Stats.TOTAL_PHYSICAL_TAKEN);
    l.add(Statistics.Stats.TOTAL_MAGICAL_TAKEN);
    l.add(Statistics.Stats.TOTAL_ATTACK);
    l.add(Statistics.Stats.TOTAL_PHYSICAL_ATTACK);
    l.add(Statistics.Stats.TOTAL_MAGICAL_ATTACK);
    l.add(Statistics.Stats.TOTAL_DRAGOON_PHYSICAL_ATTACK);
    l.add(Statistics.Stats.TOTAL_DRAGOON_MAGICAL_ATTACK);
    l.add(Statistics.Stats.TOTAL_EVADE);
    return l;
  }

  private ArrayList<Statistics.Stats> getPage1() {
    final ArrayList<Statistics.Stats> l = new ArrayList<>();
    l.add(Statistics.Stats.TOTAL_HP_RECOVER);
    l.add(Statistics.Stats.TOTAL_MP_RECOVER);
    l.add(Statistics.Stats.TOTAL_SP_RECOVER);
    return l;
  }

  private ArrayList<Statistics.Stats> getPage2() {
    final ArrayList<Statistics.Stats> l = new ArrayList<>();
    l.add(Statistics.Stats.TOTAL_TAKEN);
    return l;
  }

  @Override
  protected void render() {
    switch(this.loadingStage) {
      case 0 -> {
        startFadeEffect(2, 10);
        deallocateRenderables(0xff);
        this.loadingStage++;
      }

      case 1 -> {
        deallocateRenderables(0);

        allocateUiElement(69, 69, 0, 0); // Background left
        allocateUiElement(70, 70, 192, 0); // Background right

        this.renderAll();
        this.loadingStage++;
      }

      case 2 -> {
        this.renderAll();
      }

      // Fade out
      case 100 -> {
        this.renderAll();
        this.unload.run();
      }
    }
    FooterActionsHud.renderMenuActions();
  }

  private void renderAll() {
    this.renderGraphics();
    this.renderStats();
    FUN_801034cc(this.pageIndex, this.statisticPages.size(), -10); // Left/right arrows
  }

  private void renderStats() {
    final List<Statistics.Stats> pageStats = this.statisticPages.get(this.pageIndex).stats;
    for(int i = 0; i < pageStats.size(); i++) {
      final Statistics.Stats stat = pageStats.get(i);
      final float[] stats = Statistics.getStats(stat);
      final float max = this.getStatHighscore(stats);
      //final float min = this.getStatLowscore(stats);
      int total = 0;
      for(int j = 0; j < 9; j++) {
        if (j < stats.length) {
          final int value = (int)stats[j];
          total += value;
          renderText(String.valueOf(value), 28 * j + 86f, 14.7f * i + 42.2f, value == (int)max && max > 0 ? highNumberFont : numberFont, 100);
        } else {
          renderText("-", 28 * j + 86f, 14.7f * i + 42.2f, notApplicableFont, 100);
        }
      }
      final String statName = stat.getName();
      renderText(statName, 40.5f, 14.7f * i + 42.4f + (statName.contains("\n") ? -2.7f : 0), statFont, 100);
      renderText(String.valueOf(total), 28 * 9 + 89.5f, 14.7f * i + 42.2f, totalFont, 100);
    }
  }

  private void renderGraphics() {
    final Texture background = portraits[9];
    final Texture column = portraits[10];
    final int xOffset = (int)RENDERER.getWidescreenOrthoOffsetX();
    int x;
    int y;

    for (int j = 0; j < 4; j++) {
      for(int i = 0; i < 4; i++) {
        x = 83 * i + 10;
        y = 44 * j + 37;

        m.translation(x + xOffset, y, 122);
        m.scale(100, 44, 1);

        RENDERER
          .queueOrthoModel(quad, m, QueuedModelStandard.class)
          .texture(background);
      }
    }

    for (int j = 0; j < 4; j++) {
      for(int i = 0; i < 10; i++) {
        x = 28 * i + 72;
        y = 44 * j + 37;

        m.translation(x + xOffset, y, 121);
        m.scale(2, 44, 1);

        RENDERER
          .queueOrthoModel(quad, m, QueuedModelStandard.class)
          .texture(column);
      }
    }

    for(int charIndex = 0; charIndex < 9; charIndex++) {
      final Texture portrait = portraits[charIndex];

      x = 28 * charIndex + 76;

      m.translation(x + xOffset, 15, 3);
      m.scale(22, 22, 120);

      if(isCharacterUnlocked(charIndex)) {
        RENDERER
          .queueOrthoModel(quad, m, QueuedModelStandard.class)
          .texture(portrait);
      } else {
        RENDERER
          .queueOrthoModel(quad, m, QueuedModelStandard.class)
          .colour(0,0,0)
          .translucency(Translucency.HALF_B_PLUS_HALF_F)
          .texture(portrait);
      }
    }

    renderText(this.statisticPages.get(this.pageIndex).name, 41, 26, labelFont, 100);
    renderText("Total", 341, 26, labelFont, 100);
  }

  private static boolean isCharacterUnlocked(final int charIndex) {
    return true;
  }

  @Override
  protected InputPropagation mouseClick(final int x, final int y, final int button, final Set<InputMod> mods) {
    if(super.mouseClick(x, y, button, mods) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.loadingStage != 2 || !mods.isEmpty()) {
      return InputPropagation.PROPAGATE;
    }

    return InputPropagation.PROPAGATE;
  }

  private void menuEscape() {
    playMenuSound(3);
    this.loadingStage = 100;
  }

  private void menuNavigateLeft() {
    if(this.pageIndex > 0) {
      this.pageIndex--;
    }
  }

  private void menuNavigateRight() {
    if(this.pageIndex < this.statisticPages.size() - 1) {
      this.pageIndex++;
    }
  }

  @Override
  public InputPropagation inputActionPressed(final InputAction action, final boolean repeat) {
    if(super.inputActionPressed(action, repeat) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.loadingStage != 2) {
      return InputPropagation.PROPAGATE;
    }

    if(action == INPUT_ACTION_MENU_BACK.get() && !repeat) {
      this.menuEscape();
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_LEFT.get() || action == INPUT_ACTION_MENU_PAGE_UP.get()) {
      this.menuNavigateLeft();
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_RIGHT.get() || action == INPUT_ACTION_MENU_PAGE_DOWN.get()) {
      this.menuNavigateRight();
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_HOME.get()) {
      this.pageIndex = 0;
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_END.get()) {
      this.pageIndex = this.statisticPages.size() - 1;
      return InputPropagation.HANDLED;
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  public InputPropagation inputActionReleased(final InputAction action) {
    if(super.inputActionReleased(action) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.loadingStage != 2) {
      return InputPropagation.PROPAGATE;
    }

    return InputPropagation.PROPAGATE;
  }

  private float getStatHighscore(final float[] array) {
    boolean hasValue = false;
    float max = 0f;
    for(final float value : array) {
      max = hasValue ? Math.max(max, value) : value;
      hasValue = true;
    }
    return max;
  }

  private float getStatLowscore(final float[] array) {
    boolean hasValue = false;
    float min = 0f;
    for(final float value : array) {
      min = hasValue ? Math.min(min, value) : value;
      hasValue = true;
    }
    return min;
  }
}
