package legend.game.inventory.screens;

import legend.core.Config;
import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.game.characters.Element;
import legend.game.combat.types.EnemyRewards08;
import legend.game.combat.types.MonsterStats1c;
import legend.game.combat.ui.UiBox;
import legend.game.i18n.I18n;
import legend.game.statistics.Statistics;
import legend.game.types.Translucency;
import org.joml.Matrix4f;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static legend.core.GameEngine.RENDERER;
import static legend.game.SItem.FUN_801034cc;
import static legend.game.SItem.renderItemIcon;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.combat.Monsters.enemyRewards_80112868;
import static legend.game.combat.Monsters.monsterNames_80112068;
import static legend.game.combat.Monsters.monsterStats_8010ba98;

public class ArchiveBestiaryRenderer {

  private static class BestiaryRecord {
    public int charId;
    public String location;
    public MonsterStats1c stats;
    public String lore;
    public int kill;
    public int rank;
    public String name;

    public BestiaryRecord(final int charId, final String location, final String lore) {
      this.charId = charId;
      this.location = location;
      this.stats = monsterStats_8010ba98[charId];
      this.lore = lore;
      this.kill = Statistics.getMonsterKill(this.charId);
      this.name = monsterNames_80112068[this.charId];

      if(this.kill >= 10) {
        this.rank = 3;
      } else if(this.kill >= 5) {
        this.rank = 2;
      } else if(this.kill >= 1) {
        this.rank = 1;
      } else {
        this.rank = 0;
      }
      this.rank = 3;
    }
  }

  private static final int LIST_ITEM_COUNT = 27;

  private final Matrix4f m;
  private final Obj quad;
  private final Texture[] textures;
  private Texture headerTexture;
  private Texture modelTexture;
  private List<BestiaryRecord> bestiaryPages;
  private BestiaryRecord monster;
  private UiBox listBox;

  private final FontOptions headerFont;
  private final FontOptions statsFont;
  private final FontOptions titleFont;
  private final FontOptions resistFont;
  private final FontOptions resistNumberFont;
  private final FontOptions rewardTitleFont;
  private final FontOptions listNumberFont;
  private final FontOptions listNumberHighlightFont;
  private final FontOptions listFont;
  private final FontOptions listHighlightFont;

  public int pageIndex;
  private float currentBoxOffsetX;
  public boolean isListVisible;
  private int listFirstVisibleItem;

  private final NumberFormat nf = new DecimalFormat("000");

  public int getPageCount() {
    return this.bestiaryPages.size();
  }

  private BestiaryRecord getCurrentRecord() {
    return this.bestiaryPages.get(this.pageIndex);
  }

  public ArchiveBestiaryRenderer() {
    this.m = new Matrix4f();
    this.quad = new QuadBuilder("Bestiary Quad")
      .rgb(1f, 1f, 1f)
      .size(1.0f, 1.0f)
      .uv(0.0f, 0.0f)
      .uvSize(1.0f, 1.0f)
      .bpp(Bpp.BITS_24)
      .build();

    this.headerFont = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.DARK_GREY).size(1.1f).horizontalAlign(HorizontalAlign.CENTRE);
    this.statsFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.65f).horizontalAlign(HorizontalAlign.LEFT);
    this.titleFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.75f).horizontalAlign(HorizontalAlign.RIGHT);
    this.resistFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.50f).horizontalAlign(HorizontalAlign.LEFT);
    this.resistNumberFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.40f).horizontalAlign(HorizontalAlign.LEFT);
    this.rewardTitleFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.65f).horizontalAlign(HorizontalAlign.LEFT);
    this.listNumberFont = new FontOptions().colour(TextColour.LIGHTER_GREY).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.CENTRE);
    this.listNumberHighlightFont = new FontOptions().colour(TextColour.YELLOW).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.CENTRE);
    this.listFont = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
    this.listHighlightFont = new FontOptions().colour(TextColour.YELLOW).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);

    this.textures = new Texture[] {
      Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\bestiary_graphics.png")),  //0
      Texture.png(Path.of("gfx", "ui", "action_attack.png")),  //1
      Texture.png(Path.of("gfx", "ui", "action_guard.png")),   //2
      Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\black.png")),   //3
      Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\highlight.png")),   //4
      Texture.png(Path.of("gfx", "ui", "arrow_blue_up.png")),    //5
      Texture.png(Path.of("gfx", "ui", "arrow_blue_down.png")),  //6
    };

    this.pageIndex = 0;

    this.loadPages();
    this.loadCurrentPage();
  }

  private void loadPages() {
    this.bestiaryPages = new ArrayList<>();
    this.addPage(70, "Forest", "This is lore");
    this.addPage(132, "Forest", "This is lore");
    this.addPage(17, "Forest", "This is lore");
    this.addPage(258, "Forest", "This is lore");
    this.addPage(36, "Forest", "This is lore");
    this.addPage(119, "Forest", "This is lore");
    this.addPage(3, "Forest", "This is lore");
    this.addPage(61, "Forest", "This is lore");
    this.addPage(89, "Forest", "This is lore");
    this.addPage(85, "Forest", "This is lore");
    this.addPage(18, "Forest", "This is lore");
    this.addPage(86, "Forest", "This is lore");
    this.addPage(387, "Forest", "This is lore");
    this.addPage(352, "Forest", "This is lore");
    this.addPage(353, "Forest", "This is lore");
    this.addPage(354, "Forest", "This is lore");
    this.addPage(382, "Forest", "This is lore");
    this.addPage(381, "Forest", "This is lore");
    this.addPage(371, "Forest", "This is lore");
    this.addPage(370, "Forest", "This is lore");
    this.addPage(369, "Forest", "This is lore");
    this.addPage(368, "Forest", "This is lore");
    this.addPage(366, "Forest", "This is lore");
    this.addPage(365, "Forest", "This is lore");
    this.addPage(363, "Forest", "This is lore");
    this.addPage(364, "Forest", "This is lore");
    this.addPage(362, "Forest", "This is lore");
    this.addPage(361, "Forest", "This is lore");
    this.addPage(360, "Forest", "This is lore");
    this.addPage(349, "Forest", "This is lore");
    this.addPage(347, "Forest", "This is lore");
    this.addPage(346, "Forest", "This is lore");
    this.addPage(344, "Forest", "This is lore");
    this.addPage(343, "Forest", "This is lore");
    this.addPage(340, "Forest", "This is lore");
    this.addPage(341, "Forest", "This is lore");
    this.addPage(335, "Forest", "This is lore");
    this.addPage(334, "Forest", "This is lore");
    this.addPage(333, "Forest", "This is lore");
    this.addPage(332, "Forest", "This is lore");
    this.addPage(329, "Forest", "This is lore");
    this.addPage(325, "Forest", "This is lore");
    this.addPage(320, "Forest", "This is lore");
    this.addPage(311, "Forest", "This is lore");
    this.addPage(308, "Forest", "This is lore");
    this.addPage(305, "Forest", "This is lore");
    this.addPage(304, "Forest", "This is lore");
    this.addPage(303, "Forest", "This is lore");
    this.addPage(302, "Forest", "This is lore");
    this.addPage(301, "Forest", "This is lore");
    this.addPage(299, "Forest", "This is lore");
    this.addPage(298, "Forest", "This is lore");
    this.addPage(297, "Forest", "This is lore");
    this.addPage(296, "Forest", "This is lore");
    this.addPage(295, "Forest", "This is lore");
    this.addPage(294, "Forest", "This is lore");
    this.addPage(279, "Forest", "This is lore");
    this.addPage(293, "Forest", "This is lore");
    this.addPage(288, "Forest", "This is lore");
    this.addPage(284, "Forest", "This is lore");
    this.addPage(275, "Forest", "This is lore");
    this.addPage(287, "Forest", "This is lore");
    this.addPage(270, "Forest", "This is lore");
    this.addPage(269, "Forest", "This is lore");
    this.addPage(268, "Forest", "This is lore");
    this.addPage(267, "Forest", "This is lore");
    this.addPage(264, "Forest", "This is lore");
    this.addPage(263, "Forest", "This is lore");
    this.addPage(260, "Forest", "This is lore");
    this.addPage(261, "Forest", "This is lore");
    this.addPage(259, "Forest", "This is lore");
    this.addPage(102, "Forest", "This is lore");
    this.addPage(256, "Forest", "This is lore");
    this.addPage(257, "Forest", "This is lore");
    this.addPage(123, "Forest", "This is lore");
    this.addPage(127, "Forest", "This is lore");
    this.addPage(71, "Forest", "This is lore");
    this.addPage(81, "Forest", "This is lore");
    this.addPage(128, "Forest", "This is lore");
    this.addPage(34, "Forest", "This is lore");
    this.addPage(126, "Forest", "This is lore");
    this.addPage(7, "Forest", "This is lore");
    this.addPage(98, "Forest", "This is lore");
    this.addPage(62, "Forest", "This is lore");
    this.addPage(77, "Forest", "This is lore");
    this.addPage(125, "Forest", "This is lore");
    this.addPage(101, "Forest", "This is lore");
    this.addPage(10, "Forest", "This is lore");
    this.addPage(78, "Forest", "This is lore");
    this.addPage(84, "Forest", "This is lore");
    this.addPage(118, "Forest", "This is lore");
    this.addPage(122, "Forest", "This is lore");
    this.addPage(97, "Forest", "This is lore");
    this.addPage(40, "Forest", "This is lore");
    this.addPage(80, "Forest", "This is lore");
    this.addPage(121, "Forest", "This is lore");
    this.addPage(9, "Forest", "This is lore");
    this.addPage(72, "Forest", "This is lore");
    this.addPage(60, "Forest", "This is lore");
    this.addPage(75, "Forest", "This is lore");
    this.addPage(120, "Forest", "This is lore");
    this.addPage(93, "Forest", "This is lore");
    this.addPage(41, "Forest", "This is lore");
    this.addPage(63, "Forest", "This is lore");
    this.addPage(65, "Forest", "This is lore");
    this.addPage(47, "Forest", "This is lore");
    this.addPage(39, "Forest", "This is lore");
    this.addPage(116, "Forest", "This is lore");
    this.addPage(20, "Forest", "This is lore");
    this.addPage(50, "Forest", "This is lore");
    this.addPage(31, "Forest", "This is lore");
    this.addPage(57, "Forest", "This is lore");
    this.addPage(56, "Forest", "This is lore");
    this.addPage(11, "Forest", "This is lore");
    this.addPage(25, "Forest", "This is lore");
    this.addPage(49, "Forest", "This is lore");
    this.addPage(67, "Forest", "This is lore");
    this.addPage(5, "Forest", "This is lore");
    this.addPage(114, "Forest", "This is lore");
    this.addPage(48, "Forest", "This is lore");
    this.addPage(32, "Forest", "This is lore");
    this.addPage(37, "Forest", "This is lore");
    this.addPage(113, "Forest", "This is lore");
    this.addPage(112, "Forest", "This is lore");
    this.addPage(6, "Forest", "This is lore");
    this.addPage(96, "Forest", "This is lore");
    this.addPage(111, "Forest", "This is lore");
    this.addPage(2, "Forest", "This is lore");
    this.addPage(30, "Forest", "This is lore");
    this.addPage(43, "Forest", "This is lore");
    this.addPage(44, "Forest", "This is lore");
    this.addPage(117, "Forest", "This is lore");
    this.addPage(95, "Forest", "This is lore");
    this.addPage(51, "Forest", "This is lore");
    this.addPage(73, "Forest", "This is lore");
    this.addPage(54, "Forest", "This is lore");
    this.addPage(12, "Forest", "This is lore");
    this.addPage(110, "Forest", "This is lore");
    this.addPage(91, "Forest", "This is lore");
    this.addPage(76, "Forest", "This is lore");
    this.addPage(23, "Forest", "This is lore");
    this.addPage(109, "Forest", "This is lore");
    this.addPage(79, "Forest", "This is lore");
    this.addPage(82, "Forest", "This is lore");
    this.addPage(83, "Forest", "This is lore");
    this.addPage(106, "Forest", "This is lore");
    this.addPage(14, "Forest", "This is lore");
    this.addPage(16, "Forest", "This is lore");
    this.addPage(29, "Forest", "This is lore");
    this.addPage(52, "Forest", "This is lore");
    this.addPage(26, "Forest", "This is lore");
    this.addPage(108, "Forest", "This is lore");
    this.addPage(99, "Forest", "This is lore");
    this.addPage(2, "Forest", "This is lore");
    this.addPage(69, "Forest", "This is lore");
    this.addPage(138, "Forest", "This is lore");
    this.addPage(137, "Forest", "This is lore");
    this.addPage(136, "Forest", "This is lore");
    this.addPage(54, "Forest", "This is lore");
    this.addPage(42, "Forest", "This is lore");
    this.addPage(139, "Forest", "This is lore");
    this.addPage(124, "Forest", "This is lore");
    this.addPage(87, "Forest", "This is lore");
    this.addPage(55, "Forest", "This is lore");
    this.addPage(58, "Forest", "This is lore");
    this.addPage(15, "Forest", "This is lore");
    this.addPage(107, "Forest", "This is lore");
    this.addPage(66, "Forest", "This is lore");
    this.addPage(115, "Forest", "This is lore");
    this.addPage(88, "Forest", "This is lore");
    this.addPage(19, "Forest", "This is lore");
    this.addPage(35, "Forest", "This is lore");
    this.addPage(90, "Forest", "This is lore");
    this.addPage(33, "Forest", "This is lore");
    this.addPage(92, "Forest", "This is lore");
    this.addPage(100, "Forest", "This is lore");
    this.addPage(74, "Forest", "This is lore");
    this.addPage(22, "Forest", "This is lore");
    this.addPage(28, "Forest", "This is lore");
    this.addPage(68, "Forest", "This is lore");
    this.addPage(4, "Forest", "This is lore");
    this.addPage(13, "Forest", "This is lore");
    this.addPage(94, "Forest", "This is lore");
    this.addPage(64, "Forest", "This is lore");
    this.addPage(104, "Forest", "This is lore");
    this.addPage(46, "Forest", "This is lore");
    this.addPage(0, "Forest", "This is lore");
    this.addPage(59, "Forest", "This is lore");
    this.addPage(45, "Forest", "This is lore");
    this.addPage(27, "Forest", "This is lore");
    this.addPage(8, "Forest", "This is lore");
    this.addPage(38, "Forest", "This is lore");
    this.addPage(21, "Forest", "This is lore");
    this.addPage(24, "Forest", "This is lore");
  }

  private void addPage(final int charId, final String location, final String lore) {
    this.bestiaryPages.add(new BestiaryRecord(charId, location, lore));
  }

  public void loadCurrentPage() {
    this.monster = this.bestiaryPages.get(this.pageIndex);
    this.headerTexture = Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\header_element_" + this.getElement(this.monster.stats.elementFlag_0f).getRegistryId().entryId() + ".png"));

    try {
      this.modelTexture = Texture.png(Path.of("gfx", "models", monster.charId + ".png"));
    } catch(final Exception e) {
      this.modelTexture = Texture.png(Path.of("gfx", "models", "-1.png"));
    }
  }

  public void render() {
    this.renderGraphics();
    this.renderModel();
    this.renderEnemyName();
    this.renderEnemyStats();
    this.renderRewards();
    this.renderLore();
    this.renderListBox();

    FUN_801034cc(this.pageIndex, this.getPageCount(), -10, this.isListVisible); // Left/right arrows
  }

  private void renderGraphics() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    this.m.translation(xOffset, 0f, 128);
    this.m.scale(368f, 240f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[0]);
  }

  private void renderModel() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    this.m.translation(-22f + xOffset, 27.8f, 129);
    this.m.scale(210f, 87.3f, 1);

    if(this.monster.rank >= 1) {
      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.modelTexture); //Model
    } else {
      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.modelTexture)
        .colour(0,0,0);
    }
  }

  private void renderEnemyName() {
    if(this.monster.rank >= 1) {
      final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

      this.m.translation(14.5f + xOffset, 8f, 127);
      this.m.scale(338.6f, 19.9f, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.headerTexture); //Header

      //renderText(monsterNames_80112068[this.getCurrentRecord().charId], 184, 10.5f, this.headerFont, 127);
      renderText(monsterNames_80112068[this.getCurrentRecord().charId] + " [" + this.getCurrentRecord().charId + ']', 184, 10.5f, this.headerFont, 126);
      renderText("Found In: " + this.monster.location, 23, 206, this.statsFont, 127);
    } else {
      renderText("?????", 184, 10.5f, this.headerFont, 126);
      renderText("Found In: ?????", 23, 206, this.statsFont, 126);
    }

    renderText("Defeated: " + this.monster.kill, 23, 123, this.statsFont, 127);
  }

  private void renderEnemyStats() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();
    float x = 200f;
    float y = 45.5f;

    for(int i = 0; i < 8; i++) {
      renderText(this.getStatName(i), x, y, this.statsFont, 127);
      if(this.monster.rank >= 2) {
        renderText(String.valueOf(this.getStat(i)), x + 28f, y, this.statsFont, 127);
      } else {
        renderText("???", x + 28f, y, this.statsFont, 127);
      }
      y += 11.45f;
    }

    x = 272;
    y = 47.4f - 11.45f;

    if(this.monster.stats.elementalImmunityFlag_10 > 0) {
      y += 11.45f;
      this.renderElementMultipliers(this.monster.stats.elementFlag_0f, x, y, xOffset, 100, 50, false);
    } else if(this.monster.stats.elementFlag_0f != 8) {
      y += 11.45f;
      this.renderElementMultipliers(this.monster.stats.elementFlag_0f, x, y, xOffset, 50, 50, false);
    }

    final int counterElement = this.getCounterElement(this.monster.stats.elementFlag_0f);
    if(counterElement != 0) {
      y += 11.45f;
      this.renderElementMultipliers(counterElement, x, y, xOffset, 50, 50, true);
    }
  }

  private void renderElementMultipliers(final int elementFlag, final float x, final float y, final float xOffset, final int defPercent, final int attPercent, final boolean counter) {
    final String guardSign = counter ? "-" : "+";
    final String attackSign = counter ? "+" : "-";

    renderText(this.monster.rank >= 2 ? I18n.translate(this.getElement(elementFlag).getTranslationKey()) : "?????", x, y, this.resistFont, 127);

    if(this.monster.rank >= 2) {
      this.m.translation(x + 27.5f + xOffset, y - 1.5f, 124f);
      this.m.scale(7, 7, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[2]); //Guard

      renderText(guardSign + defPercent + '%', x + 35f, y + 0.3f, this.resistNumberFont, 127);

      if(attPercent != 0) {
        this.m.translation(x + 53f + xOffset, y - 1.5f, 124f);
        this.m.scale(7, 7, 1);

        RENDERER
          .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
          .texture(this.textures[1]); //Attack

        renderText(attackSign + attPercent + '%', x + 60f, y + 0.3f, this.resistNumberFont, 127);
      }
    }
  }

  private void renderRewards() {
    final EnemyRewards08 rewards = enemyRewards_80112868[this.getCurrentRecord().charId];
    final float x = 200f;
    float y = 166f;

    renderText("Battle Rewards", x + 74, 150f, this.titleFont, 127);

    if(rewards.xp_00 > 0) {
      renderText(this.monster.rank >= 3 ? "Exp." : "???", x, y, this.rewardTitleFont, 127);
      renderText(this.monster.rank >= 3 ? String.valueOf(rewards.xp_00) : "?????", x + 28f, y, this.rewardTitleFont, 127);
      y += 13.5f;
    }

    if(rewards.gold_02 > 0) {
      renderText(this.monster.rank >= 3 ? "Gold" : "???", x, y, this.rewardTitleFont, 127);
      renderText(this.monster.rank >= 3 ? String.valueOf(rewards.gold_02) : "?????", x + 28f, y, this.rewardTitleFont, 127);
      y += 13.5f;
    }

    if(rewards.itemDrop_05 != null) {
      renderText(this.monster.rank >= 3 ? I18n.translate(rewards.itemDrop_05.get().getNameTranslationKey()) + " (" + rewards.itemChance_04 + "%)" : "?????", x + 28f, y, this.rewardTitleFont, 127);

      if(this.monster.rank >= 3) {
        renderItemIcon(rewards.itemDrop_05.get().getIcon(), x + 10f, y - 5f, 32, 0.9f, 0.9f, 0x8);
      } else {
        renderText("???", x, y, this.rewardTitleFont, 127);
      }
    }
  }

  private void renderLore() {
    if(this.monster.rank >= 2) {
      renderText(this.monster.lore, 23f, 146f, this.statsFont, 127);
    } else {
      renderText("?????", 23f, 146f, this.statsFont, 127);
    }
  }

  private void renderListBox() {
    if(this.isListVisible) {
      final float xOffset = RENDERER.getWidescreenOrthoOffsetX();
      final float x = 6f;

      if(this.listBox == null || this.currentBoxOffsetX != xOffset) {
        this.currentBoxOffsetX = xOffset;
        this.listBox = new UiBox("Bestiary List", x - xOffset, 14f, 95f, 210f, 0.7f);
      }

      this.m.translation(0 - xOffset, 0, 126);
      this.m.scale(368, 240, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[3]) //Black
        .alpha(0.25f)
        .translucency(Translucency.HALF_B_PLUS_HALF_F);

      this.listBox.render(Config.changeBattleRgb() ? Config.getBattleRgb() : Config.defaultUiColour);

      float y = 20f;
      for(int i = 0; i < LIST_ITEM_COUNT; i++) {
        final int recordIndex = this.listFirstVisibleItem + i;
        final BestiaryRecord record = this.bestiaryPages.get(recordIndex);
        final boolean highlighted = this.pageIndex == recordIndex;
        float charX = 6;
        for(final char c : this.nf.format(recordIndex).toCharArray()) {
          renderText(String.valueOf(c), x + charX, y, highlighted ? this.listNumberHighlightFont : this.listNumberFont, 123);
          charX += 3.7f;
        }
        renderText(":", x + 16f, y, highlighted ? this.listNumberHighlightFont : this.listNumberFont, 123);
        renderText(record.rank > 0 ? record.name : "?????", x + 19f, y, highlighted ? this.listHighlightFont : this.listFont, 123);

        if(highlighted) {
          this.m.translation(x + 3f - xOffset, y - 1.5f, 124);
          this.m.scale(83f, 7.5f, 1);

          RENDERER
            .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
            .texture(this.textures[4]); //Highlight
        }

        y += 7.5f;
      }

      if(this.listFirstVisibleItem > 0) {
        this.m.translation(x + 86f - xOffset, 17f, 124);
        this.m.scale(8, 8, 1);

        RENDERER
          .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
          .texture(this.textures[5]); //Up Arrow
      }

      if(this.listFirstVisibleItem < this.bestiaryPages.size() - LIST_ITEM_COUNT) {
        this.m.translation(x + 86f - xOffset, 213f, 124);
        this.m.scale(8, 8, 1);

        RENDERER
          .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
          .texture(this.textures[6]); //Down Arrow
      }
    }
  }

  public boolean next(final int steps) {
    boolean b = false;
    for(int i = 0; i < steps; i++) {
      if(this.pageIndex < this.bestiaryPages.size() - 1) {
        b = true;
        this.pageIndex++;
        if(this.listFirstVisibleItem < this.bestiaryPages.size() - LIST_ITEM_COUNT && this.listFirstVisibleItem + LIST_ITEM_COUNT * 0.85f < this.pageIndex) {
          this.listFirstVisibleItem++;
        }
      } else {
        break;
      }
    }
    return b;
  }

  public boolean previous(final int steps) {
    boolean b = false;
    for(int i = 0; i < steps; i++) {
      if(this.pageIndex > 0) {
        b = true;
        this.pageIndex--;
        if(this.listFirstVisibleItem > 0 && this.listFirstVisibleItem + LIST_ITEM_COUNT * 0.14f > this.pageIndex) {
          this.listFirstVisibleItem--;
        }
      } else {
        break;
      }
    }
    return b;
  }

  public boolean jump(final int index) {
    if(index != this.pageIndex) {
      this.pageIndex = index;
      this.listFirstVisibleItem = Math.clamp(index, 0, this.bestiaryPages.size() - LIST_ITEM_COUNT);
      return true;
    }
    return false;
  }

  private int getStat(final int statIndex) {
    return switch(statIndex) {
      case 0 -> this.monster.stats.hp_00;
      case 1 -> this.monster.stats.attack_04;
      case 2 -> this.monster.stats.defence_09;
      case 3 -> this.monster.stats.magicAttack_06;
      case 4 -> this.monster.stats.magicDefence_0a;
      case 5 -> this.monster.stats.speed_08;
      case 6 -> this.monster.stats.attackAvoid_0b;
      case 7 -> this.monster.stats.magicAvoid_0c;
      default -> 0;
    };
  }

  private String getStatName(final int statIndex) {
    return switch(statIndex) {
      case 0 -> "HP";
      case 1 -> "AT";
      case 2 -> "DF";
      case 3 -> "M-AT";
      case 4 -> "M-DF";
      case 5 -> "SPD";
      case 6 -> "A-AV";
      case 7 -> "M-AV";
      default -> "";
    };
  }

  private Element getElement(final int elementFlag) {
    return Element.fromFlag(elementFlag).get();
  }

  private int getCounterElement(final int elementFlag) { //Water 1, Earth 2, Dark 4, Divine 8, Light 32, Wind 64, Fire 128
    return switch(elementFlag) {
      case 1 -> 128;
      case 128 -> 1;
      case 2 -> 64;
      case 64 -> 2;
      case 4 -> 32;
      case 32 -> 4;
      default -> 0;
    };
  }
}
