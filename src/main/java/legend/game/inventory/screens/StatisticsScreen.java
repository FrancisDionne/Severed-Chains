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
  private final Matrix4f m;
  private final Obj quad;
  private final Texture[] textures;
  private final HashMap<Integer, StatisticPage> statisticPages;

  private final FontOptions labelFont;
  private final FontOptions statFont;
  private final FontOptions numberFont;
  private final FontOptions highNumberFont;
  private final FontOptions lowNumberFont;
  private final FontOptions totalFont;
  private final FontOptions notApplicableFont;

  public int pageIndex;
  public int highlightIndex;
  public int displayMode;

  public int getPageCount() {
    return this.statisticPages.size();
  }

  public StatisticsScreen(final Runnable unload) {
    this.unload = unload;
    this.m = new Matrix4f();
    this.quad = new QuadBuilder("Statistics Quad")
      .rgb(1f, 1f, 1f)
      .size(1.0f, 1.0f)
      .uv(0.0f, 0.0f)
      .uvSize(1.0f, 1.0f)
      .bpp(Bpp.BITS_24)
      .build();

    this.labelFont = new FontOptions().colour(TextColour.BROWN).shadowColour(TextColour.MIDDLE_BROWN).size(0.8f).horizontalAlign(HorizontalAlign.RIGHT);
    this.statFont = new FontOptions().colour(TextColour.LIGHT_GREY_WHITE).shadowColour(TextColour.DARK_GREY).size(0.38f).horizontalAlign(HorizontalAlign.RIGHT);
    this.numberFont = new FontOptions().colour(TextColour.DARK_GREY).shadowColour(TextColour.MIDDLE_BROWN).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
    this.highNumberFont = new FontOptions().colour(TextColour.STATS_GREEN).shadowColour(TextColour.DARK_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
    this.lowNumberFont = new FontOptions().colour(TextColour.FOOTER_RED).shadowColour(TextColour.DARK_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
    this.totalFont = new FontOptions().colour(TextColour.STATS_YELLOW).shadowColour(TextColour.DARK_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
    this.notApplicableFont = new FontOptions().colour(TextColour.GREY).shadowColour(TextColour.MIDDLE_BROWN).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);

    this.textures = new Texture[] {
      Texture.png(Path.of("gfx", "portraits", "dart.png")),    //0
      Texture.png(Path.of("gfx", "portraits", "lavitz.png")),  //1
      Texture.png(Path.of("gfx", "portraits", "shana.png")),   //2
      Texture.png(Path.of("gfx", "portraits", "rose.png")),    //3
      Texture.png(Path.of("gfx", "portraits", "haschel.png")), //4
      Texture.png(Path.of("gfx", "portraits", "albert.png")),  //5
      Texture.png(Path.of("gfx", "portraits", "meru.png")),    //6
      Texture.png(Path.of("gfx", "portraits", "kongol.png")),  //7
      Texture.png(Path.of("gfx", "portraits", "miranda.png")), //8
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\background-lines.png")),  //9
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\column-line.png")),       //10
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\highlight_center.png")),  //11
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\highlight_left.png")),    //12
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\highlight_right.png")),   //13
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\box-top-left.png")),    //14
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\box-top.png")),         //15
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\box-top-right.png")),   //16
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\box-right.png")),       //17
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\box-bottom.png")),      //18
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\box-bottom-left.png")), //19
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\box-left.png")),        //20
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\box-separator.png")),   //21
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\portrait-separator.png")),   //22
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\header-background.png")),    //23
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\group-center.png")),  //24
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\group-top.png")),     //25
      Texture.png(Path.of("gfx", "ui", "archive_screen\\stats\\group-bottom.png")),  //26
      Texture.png(Path.of("gfx", "portraits", "lavitz_dead.png")),  //27
    };

    this.statisticPages = new HashMap<>();
    this.pageIndex = 0;
    this.highlightIndex = 0;
    this.displayMode = 0;

    this.loadPages();

    FooterActionsHud.setFooterActions(0, FooterActions.BACK, FooterActions.DISPLAY_MODE, null, null, null);
    FooterActionsHud.setSecondaryText(1, ": " + this.getDisplayModeName());
  }

  private void loadPages() {
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
    l.add(new StatisticRow(Statistics.Stats.TOTAL_ESCAPE));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_DEATH));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_KILL));
    //l.add(new StatisticRow(Statistics.Stats.TOTAL_REVIVE));
    //l.add(new StatisticRow(Statistics.Stats.TOTAL_REVIVED));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_EXP));
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
    l.add(new StatisticRow(Statistics.Stats.DISTANCE));
    l.add(new StatisticRow(Statistics.Stats.GOLD));
    l.add(new StatisticRow(Statistics.Stats.CHEST));
    l.add(new StatisticRow(Statistics.Stats.TOTAL_ENCOUNTER));
    return l;
  }

  public void renderAll() {
    this.renderGraphics();
    this.renderStats();
    this.renderHighlight();
    FUN_801034cc(this.pageIndex, this.getPageCount(), -10, false); // Left/right arrows
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
          renderText(Statistics.getDisplayValue(value, stat, j, false, this.displayMode), x, y, value == (int)max && max > 0 && stats.length > 1 ? this.highNumberFont : this.numberFont, 120);
        } else {
          renderText("-", x, y, this.notApplicableFont, 120);
        }
      }
      final String statName = stat.getName();
      renderText(statName, 70f, y + 0.2f + (statName.contains("\n") ? -2.7f : 0), this.statFont, 120);
      renderText(stats.length > 1 ? Statistics.getDisplayValue(total, stat, 0, true, this.displayMode) : "-", 28 * 9 + 89.5f, y, stats.length > 1 ? this.totalFont : this.notApplicableFont, 120);

      statGroupEnd = this.renderGroup(statRow, statGroupEnd, y);
    }
  }

  private Statistics.Stats renderGroup(final StatisticRow statRow, @Nullable Statistics.Stats statGroupEnd, float y) {
    if(statRow.statGroupEnd != null || statGroupEnd != null) {
      final Texture texture;

      if(statGroupEnd == null) {
        statGroupEnd = statRow.statGroupEnd;
        texture = this.textures[25];
        y = y - 4;
      } else if(statGroupEnd == statRow.stat) {
        statGroupEnd = null;
        texture = this.textures[26];
        y = y - 27.6f;
      } else {
        texture = this.textures[24];
        y = y - 19f;
      }

      final float xOffset = RENDERER.getWidescreenOrthoOffsetX();
      this.m.translation(5 + xOffset, y, 120);
      this.m.scale(3.2f, 34.8f, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(texture);
    }
    return statGroupEnd;
  }

  private void renderGraphics() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();
    int x;
    int y;

    for(int j = 0; j < 4; j++) {
      for(int i = 0; i < 4; i++) {
        x = 83 * i + 10;
        y = 44 * j + 37;

        this.m.translation(x + xOffset, y, 122);
        this.m.scale(100, 44, 1);

        RENDERER
          .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
          .texture(this.textures[9]); //Backgrounds
      }
    }

    for(int j = 0; j < 4; j++) {
      for(int i = 0; i < 10; i++) {
        x = 28 * i + 72;
        y = 44 * j + 37;

        this.m.translation(x + xOffset, y, 121);
        this.m.scale(2, 44, 1);

        RENDERER
          .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
          .texture(this.textures[10]); //Columns
      }
    }

    for(int i = 0; i < 10; i++) {
      x = 28 * i + 72;

      this.m.translation(x + xOffset, 13, 125);
      this.m.scale(2, 44, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[22]);
    }

    for(int charIndex = 0; charIndex < 9; charIndex++) {
      final Texture portrait = charIndex == 1 && this.isCharacterUnlocked(5) ? this.textures[27] : this.textures[charIndex];

      x = 28 * charIndex + 76;

      this.m.translation(x + xOffset, 14, 120);
      this.m.scale(22, 22, 120);

      if(this.isCharacterUnlocked(charIndex)) {
        RENDERER
          .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
          .texture(portrait);
      } else {
        RENDERER
          .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
          .colour(0, 0, 0)
          .alpha(0.85f)
          .translucency(Translucency.HALF_B_PLUS_HALF_F)
          .texture(portrait);
      }
    }

    this.m.translation(10.2f + xOffset, 13.8f, 125);
    this.m.scale(349f, 22f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[23]); //Header Background

    this.m.translation(8.2f + xOffset, 12, 124);
    this.m.scale(16f, 16f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[14]); //Top Left

    this.m.translation(352 + xOffset, 12f, 124);
    this.m.scale(9.2f, 7.3f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[16]); //Top Right

    this.m.translation(8.2f + xOffset, 205, 121);
    this.m.scale(8.5f, 9.7f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[19]); //Bottom Left

    for(int i = 0; i < 3; i++) {
      this.m.translation(92f * i + 24f + xOffset, 12, 124);
      this.m.scale(145, 2f, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[15]); //Top
    }

    for(int i = 0; i < 3; i++) {
      this.m.translation(102.2f * i + 10f + xOffset, 35, 124);
      this.m.scale(145, 2f, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[21]); //Top Middle
    }

    for(int i = 0; i < 4; i++) {
      this.m.translation(359.21f + xOffset, 46 * i + 20, 124);
      this.m.scale(2f, 55, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[17]); //Right
    }

    for(int i = 0; i < 3; i++) {
      this.m.translation(99.3f * i + 16f + xOffset, 212, 120);
      this.m.scale(145, 2f, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[18]); //Bottom
    }

    for(int i = 0; i < 5; i++) {
      this.m.translation(8.2f + xOffset, 34f * i + 28, 121);
      this.m.scale(2.4f, 41, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[20]); //Left
    }

    renderText(this.statisticPages.get(this.pageIndex).name, 70, 25, this.labelFont, 120);
    renderText("Total", 354.8f, 26, this.labelFont, 120);
  }

  private void renderHighlight() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();
    final float x = 10.2f;
    final float y = 14.66f * this.highlightIndex + 37f;
    this.m.translation(x + xOffset, y, 121);
    this.m.scale(349, 13.5f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[11])
      .translucency(Translucency.HALF_B_PLUS_HALF_F);

    this.m.translation(x + xOffset, y, 120);
    this.m.scale(4, 13.5f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[12]);

    this.m.translation(x + 345.1f + xOffset, y, 120);
    this.m.scale(4, 13.5f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[13]);
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

        allocateUiElement(69, 69, 0, 1);   // Background left
        allocateUiElement(70, 70, 192, 1); // Background right

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

    FooterActionsHud.renderActions();
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

  private void menuNavigateLeft(final int steps, final boolean alt) {
    if(this.pageIndex > 0) {
      playMenuSound(1);
      this.pageIndex--;
    }
  }

  private void menuNavigateRight(final int steps, final boolean alt) {
    if(this.pageIndex < this.getPageCount() - 1) {
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

    if(action == INPUT_ACTION_MENU_PAGE_UP.get()) {
      this.menuNavigateLeft(10, true);
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_PAGE_DOWN.get()) {
      this.menuNavigateRight(10, true);
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_LEFT.get()) {
      this.menuNavigateLeft(1, false);
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_RIGHT.get()) {
      this.menuNavigateRight(1, false);
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_DOWN.get()) {
      this.menuNavigateDown();
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_UP.get()) {
      this.menuNavigateUp();
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_HOME.get()) {
      playMenuSound(1);
      this.pageIndex = 0;
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_END.get()) {
      playMenuSound(1);
      this.pageIndex = this.getPageCount() - 1;
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_SORT.get()) {
      playMenuSound(2);
      if(this.displayMode + 1 > 2) {
        this.displayMode = 0;
      } else {
        this.displayMode++;
      }
      FooterActionsHud.setSecondaryText(1, ": " + this.getDisplayModeName());
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
}
