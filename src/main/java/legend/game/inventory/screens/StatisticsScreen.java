package legend.game.inventory.screens;

import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputMod;
import legend.game.combat.ui.FooterActions;
import legend.game.combat.ui.FooterActionsHud;
import legend.game.statistics.Statistics;
import legend.game.types.Translucency;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
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
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_END;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_HOME;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_LEFT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_PAGE_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_PAGE_UP;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_RIGHT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_SORT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_UP;

public class StatisticsScreen extends MenuScreen {

  private static class StatisticPage {
    public List<StatisticRow> statRows;
    public String name;
    public StatisticPage(final String name, final List<StatisticRow> statRows) {
      this.name = name;
      this.statRows = statRows;
    }
  }

  private static class StatisticRow {
    public Statistics.Stats stat;
    public Statistics.Stats statGroupEnd;
    public StatisticRow(final Statistics.Stats stat) {
      this(stat, null);
    }
    public StatisticRow(final Statistics.Stats stat, @Nullable final Statistics.Stats statGroupEnd) {
      this.stat = stat;
      this.statGroupEnd = statGroupEnd;
    }
  }

  private int loadingStage;
  private final Runnable unload;

  private HashMap<Integer, StatisticPage> statisticPages;
  private int pageIndex;
  private int highlightIndex;
  private int displayMode;

  private static final Matrix4f m = new Matrix4f();
  private static final Obj quad = new QuadBuilder("Statistics Quad")
    .rgb(1f, 1f, 1f)
    .size(1.0f, 1.0f)
    .uv(0.0f, 0.0f)
    .uvSize(1.0f, 1.0f)
    .bpp(Bpp.BITS_24)
    .build();

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
    Texture.png(Path.of("gfx", "ui", "stats_screen\\background-lines.png")),  //9
    Texture.png(Path.of("gfx", "ui", "stats_screen\\column-line.png")),       //10
    Texture.png(Path.of("gfx", "ui", "stats_screen\\highlight_center.png")),  //11
    Texture.png(Path.of("gfx", "ui", "stats_screen\\highlight_left.png")),    //12
    Texture.png(Path.of("gfx", "ui", "stats_screen\\highlight_right.png")),   //13
    Texture.png(Path.of("gfx", "ui", "stats_screen\\box-top-left.png")),    //14
    Texture.png(Path.of("gfx", "ui", "stats_screen\\box-top.png")),         //15
    Texture.png(Path.of("gfx", "ui", "stats_screen\\box-top-right.png")),   //16
    Texture.png(Path.of("gfx", "ui", "stats_screen\\box-right.png")),       //17
    Texture.png(Path.of("gfx", "ui", "stats_screen\\box-bottom.png")),      //18
    Texture.png(Path.of("gfx", "ui", "stats_screen\\box-bottom-left.png")), //19
    Texture.png(Path.of("gfx", "ui", "stats_screen\\box-left.png")),        //20
    Texture.png(Path.of("gfx", "ui", "stats_screen\\box-separator.png")),   //21
    Texture.png(Path.of("gfx", "ui", "stats_screen\\portrait-separator.png")),   //22
    Texture.png(Path.of("gfx", "ui", "stats_screen\\header-background.png")),    //23
    Texture.png(Path.of("gfx", "ui", "stats_screen\\group-center.png")),  //24
    Texture.png(Path.of("gfx", "ui", "stats_screen\\group-top.png")),     //25
    Texture.png(Path.of("gfx", "ui", "stats_screen\\group-bottom.png")),  //26
  };

  private static final FontOptions labelFont = new FontOptions().colour(TextColour.BROWN).shadowColour(TextColour.MIDDLE_BROWN).size(0.8f).horizontalAlign(HorizontalAlign.RIGHT);
  private static final FontOptions statFont = new FontOptions().colour(TextColour.LIGHT_GREY_WHITE).shadowColour(TextColour.DARK_GREY).size(0.38f).horizontalAlign(HorizontalAlign.RIGHT);
  private static final FontOptions numberFont = new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.MIDDLE_BROWN).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions highNumberFont = new FontOptions().colour(TextColour.STATS_GREEN).shadowColour(TextColour.DARK_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions lowNumberFont = new FontOptions().colour(TextColour.FOOTER_RED).shadowColour(TextColour.DARK_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions totalFont = new FontOptions().colour(TextColour.STATS_YELLOW).shadowColour(TextColour.DARK_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions notApplicableFont = new FontOptions().colour(TextColour.GREY).shadowColour(TextColour.MIDDLE_BROWN).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
  private static final FontOptions displayModeFont = new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.MIDDLE_BROWN).size(0.4f).horizontalAlign(HorizontalAlign.LEFT);

  public StatisticsScreen(final Runnable unload) {
    this.unload = unload;
    this.loadPages();
    this.pageIndex = 0;
    this.highlightIndex = 0;
    this.displayMode = 0;
  }

  private void loadPages() {
    this.statisticPages = new HashMap<>();
    this.addPage(new StatisticPage("Battle", this.getPage0()));
    this.addPage(new StatisticPage("Battle", this.getPage1()));
    this.addPage(new StatisticPage("Additions", this.getPage2()));
    this.addPage(new StatisticPage("Misc", this.getPage3()));
  }

  private void addPage(final StatisticPage page) {
    this.statisticPages.put(this.statisticPages.size(), page);
  }

  private ArrayList<StatisticRow> getPage0() {
    final ArrayList<StatisticRow> l = new ArrayList<>();
    l.add(new StatisticRow(Statistics.Stats.TOTAL_DAMAGE, Statistics.Stats.TOTAL_MAGICAL_DAMAGE));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_PHYSICAL_DAMAGE));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_MAGICAL_DAMAGE));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_TAKEN, Statistics.Stats.TOTAL_MAGICAL_TAKEN));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_PHYSICAL_TAKEN));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_MAGICAL_TAKEN));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_ATTACK, Statistics.Stats.TOTAL_DRAGOON_MAGICAL_ATTACK));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_PHYSICAL_ATTACK));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_MAGICAL_ATTACK));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_DRAGOON_PHYSICAL_ATTACK));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_DRAGOON_MAGICAL_ATTACK));
    return l;
  }

  private ArrayList<StatisticRow> getPage1() {
    final ArrayList<StatisticRow> l = new ArrayList<>();
    l.add(new StatisticRow(Statistics.Stats.TOTAL_GUARD));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_EVADE));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_HP_RECOVER));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_MP_RECOVER));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_SP_RECOVER));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_DEATH));
    //l.add(new StatisticRow(Statistics.Stats.TOTAL_REVIVE));
    //l.add(new StatisticRow(Statistics.Stats.TOTAL_REVIVED));
    return l;
  }

  private ArrayList<StatisticRow> getPage2() {
    final ArrayList<StatisticRow> l = new ArrayList<>();
    l.add(new StatisticRow(Statistics.Stats.TOTAL_ADDITION, Statistics.Stats.TOTAL_ADDITION_FLAWLESS));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_ADDITION_COMPLETE));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_ADDITION_FLAWLESS));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_ADDITION_HIT));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_ADDITION_COUNTER, Statistics.Stats.TOTAL_ADDITION_COUNTER_BLOCK));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_ADDITION_COUNTER_BLOCK));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_DRAGOON_ADDITION, Statistics.Stats.TOTAL_DRAGOON_ADDITION_COMPLETED));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_DRAGOON_ADDITION_COMPLETED));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_DRAGOON_ADDITION_HIT));
    return l;
  }

  private ArrayList<StatisticRow> getPage3() {
    final ArrayList<StatisticRow> l = new ArrayList<>();
    l.add(new StatisticRow(Statistics.Stats.GOLD));
    l.add(new StatisticRow(Statistics.Stats.CHEST));
    l.add(new StatisticRow(Statistics.Stats.ENCOUNTERS));
    l.add(new StatisticRow(Statistics.Stats.DISTANCE));
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
    FooterActionsHud.renderActions(0, FooterActions.BACK, null, null, null, null);
  }

  private void renderAll() {
    this.renderGraphics();
    this.renderStats();
    this.renderHighlight();
    FUN_801034cc(this.pageIndex, this.statisticPages.size(), -10); // Left/right arrows
  }

  private void renderStats() {
    final List<StatisticRow> pageStats = this.statisticPages.get(this.pageIndex).statRows;
    Statistics.Stats statGroupEnd = null;
    for(int i = 0; i < pageStats.size(); i++) {
      final StatisticRow statRow = pageStats.get(i);
      final Statistics.Stats stat = statRow.stat;
      final float[] stats = Statistics.getStats(stat);
      final float max = this.getStatHighscore(stats);
      //final float min = this.getStatLowscore(stats);
      final float y = 14.7f * i + 42.2f;
      float total = 0;
      for(int j = 0; j < 9; j++) {
        final float x = 28 * j + 86f;
        if(j < stats.length && this.isCharacterUnlocked(j)) {
          final float value = stats[j];
          total += value;
          renderText(Statistics.getDisplayValue(value, stat, j, false, this.displayMode), x, y, value == (int)max && max > 0 && stats.length > 1 ? highNumberFont : numberFont, 120);
        } else {
          renderText("-", x, y, notApplicableFont, 120);
        }
      }
      final String statName = stat.getName();
      renderText(statName, 70f, y + 0.2f + (statName.contains("\n") ? -2.7f : 0), statFont, 120);
      renderText(stats.length > 1 ? Statistics.getDisplayValue(total, stat, 0, true, this.displayMode) : "-", 28 * 9 + 89.5f, y, stats.length > 1 ? totalFont : notApplicableFont, 120);

      statGroupEnd = this.renderGroup(statRow, statGroupEnd, y);
    }
  }

  private Statistics.Stats renderGroup(final StatisticRow statRow, @Nullable Statistics.Stats statGroupEnd, float y) {
    if(statRow.statGroupEnd != null || statGroupEnd != null) {
      final Texture texture;

      if(statGroupEnd == null) {
        statGroupEnd = statRow.statGroupEnd;
        texture = textures[25];
        y = y - 4;
      } else if(statGroupEnd == statRow.stat) {
        statGroupEnd = null;
        texture = textures[26];
        y = y - 27.6f;
      } else {
        texture = textures[24];
        y = y - 19f;
      }

      final int xOffset = (int)RENDERER.getWidescreenOrthoOffsetX();
      m.translation(5 + xOffset, y, 120);
      m.scale(3.2f, 34.8f, 1);

      RENDERER
        .queueOrthoModel(quad, m, QueuedModelStandard.class)
        .texture(texture);
    }
    return statGroupEnd;
  }

  private void renderGraphics() {
    final int xOffset = (int)RENDERER.getWidescreenOrthoOffsetX();
    int x;
    int y;

    for(int j = 0; j < 4; j++) {
      for(int i = 0; i < 4; i++) {
        x = 83 * i + 10;
        y = 44 * j + 37;

        m.translation(x + xOffset, y, 122);
        m.scale(100, 44, 1);

        RENDERER
          .queueOrthoModel(quad, m, QueuedModelStandard.class)
          .texture(textures[9]); //Backgrounds
      }
    }

    for(int j = 0; j < 4; j++) {
      for(int i = 0; i < 10; i++) {
        x = 28 * i + 72;
        y = 44 * j + 37;

        m.translation(x + xOffset, y, 121);
        m.scale(2, 44, 1);

        RENDERER
          .queueOrthoModel(quad, m, QueuedModelStandard.class)
          .texture(textures[10]); //Columns
      }
    }

    for(int i = 0; i < 10; i++) {
      x = 28 * i + 72;

      m.translation(x + xOffset, 13, 125);
      m.scale(2, 44, 1);

      RENDERER
        .queueOrthoModel(quad, m, QueuedModelStandard.class)
        .texture(textures[22]);
    }

    for(int charIndex = 0; charIndex < 9; charIndex++) {
      final Texture portrait = textures[charIndex];

      x = 28 * charIndex + 76;

      m.translation(x + xOffset, 14, 120);
      m.scale(22, 22, 120);

      if(this.isCharacterUnlocked(charIndex)) {
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

    m.translation(10.2f + xOffset, 13.8f, 125);
    m.scale(349f, 22f, 1);

    RENDERER
      .queueOrthoModel(quad, m, QueuedModelStandard.class)
      .texture(textures[23]); //Header Background

    m.translation(8.2f + xOffset, 12, 124);
    m.scale(16f, 16f, 1);

    RENDERER
      .queueOrthoModel(quad, m, QueuedModelStandard.class)
      .texture(textures[14]); //Top Left

    m.translation(352 + xOffset, 12f, 124);
    m.scale(9.2f, 7.3f, 1);

    RENDERER
      .queueOrthoModel(quad, m, QueuedModelStandard.class)
      .texture(textures[16]); //Top Right

    m.translation(8.2f + xOffset, 205, 121);
    m.scale(8.5f, 9.7f, 1);

    RENDERER
      .queueOrthoModel(quad, m, QueuedModelStandard.class)
      .texture(textures[19]); //Bottom Left

    for(int i = 0; i < 3; i++) {
      m.translation(92f * i + 24f + xOffset, 12, 124);
      m.scale(145, 2f, 1);

      RENDERER
        .queueOrthoModel(quad, m, QueuedModelStandard.class)
        .texture(textures[15]); //Top
    }

    for(int i = 0; i < 3; i++) {
      m.translation(102.2f * i + 10f + xOffset, 35, 124);
      m.scale(145, 2f, 1);

      RENDERER
        .queueOrthoModel(quad, m, QueuedModelStandard.class)
        .texture(textures[21]); //Top Middle
    }

    for(int i = 0; i < 4; i++) {
      m.translation(359.21f + xOffset, 46 * i + 20, 124);
      m.scale(2f, 55, 1);

      RENDERER
        .queueOrthoModel(quad, m, QueuedModelStandard.class)
        .texture(textures[17]); //Right
    }

    for(int i = 0; i < 3; i++) {
      m.translation(99.3f * i + 16f + xOffset, 212, 120);
      m.scale(145, 2f, 1);

      RENDERER
        .queueOrthoModel(quad, m, QueuedModelStandard.class)
        .texture(textures[18]); //Bottom
    }

    for(int i = 0; i < 5; i++) {
      m.translation(8.2f + xOffset, 34f * i + 28, 121);
      m.scale(2.4f, 41, 1);

      RENDERER
        .queueOrthoModel(quad, m, QueuedModelStandard.class)
        .texture(textures[20]); //Left
    }

    m.translation(10 + xOffset, 215, 120);
    m.scale(6, 6, 120);

    RENDERER
      .queueOrthoModel(quad, m, QueuedModelStandard.class)
      .texture(FooterActionsHud.getTexture(INPUT_ACTION_MENU_SORT.get()));

    renderText(this.statisticPages.get(this.pageIndex).name, 70, 25, labelFont, 120);
    renderText("Total", 354.8f, 26, labelFont, 120);
    renderText("Display Mode: " + this.getDisplayModeName(), 17, 216.2f, displayModeFont, 120);
  }

  private void renderHighlight() {
    final int xOffset = (int)RENDERER.getWidescreenOrthoOffsetX();
    final float x = 10.2f;
    final float y = 14.66f * this.highlightIndex + 37f;
    m.translation(x + xOffset, y, 121);
    m.scale(349, 13.5f, 1);

    RENDERER
      .queueOrthoModel(quad, m, QueuedModelStandard.class)
      .texture(textures[11])
      .translucency(Translucency.HALF_B_PLUS_HALF_F);

    m.translation(x + xOffset, y, 120);
    m.scale(4, 13.5f, 1);

    RENDERER
      .queueOrthoModel(quad, m, QueuedModelStandard.class)
      .texture(textures[12]);

    m.translation(x + 345.1f + xOffset, y, 120);
    m.scale(4, 13.5f, 1);

    RENDERER
      .queueOrthoModel(quad, m, QueuedModelStandard.class)
      .texture(textures[13]);
  }

  private boolean isCharacterUnlocked(final int charIndex) {
    return Statistics.getStat(Statistics.Stats.DART_UNLOCKED, charIndex) > 0;
  }

  private String getDisplayModeName() {
    return switch(this.displayMode) {
      case 1 -> "Group %";
      case 2 -> "Row %";
      default -> "Number";
    };
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
      playMenuSound(1);
      this.pageIndex--;
    }
  }

  private void menuNavigateRight() {
    if(this.pageIndex < this.statisticPages.size() - 1) {
      playMenuSound(1);
      this.pageIndex++;
    }
  }

  private void menuNavigateUp() {
    playMenuSound(1);
    if(this.highlightIndex > 0) {
      this.highlightIndex--;
    } else {
      this.highlightIndex = 11;
    }
  }

  private void menuNavigateDown() {
    playMenuSound(1);
    if(this.highlightIndex < 11) {
      this.highlightIndex++;
    } else {
      this.highlightIndex = 0;
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

    if(action == INPUT_ACTION_MENU_UP.get()) {
      this.menuNavigateUp();
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_DOWN.get()) {
      this.menuNavigateDown();
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

    if(action == INPUT_ACTION_MENU_SORT.get()) {
      playMenuSound(2);
      if(this.displayMode + 1 > 2) {
        this.displayMode = 0;
      } else {
        this.displayMode++;
      }
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
