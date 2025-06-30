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

import javax.annotation.Nullable;
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
    public int maxKill;

    public BestiaryRecord(final int charId, @Nullable final String name, final int maxKill, final String map, final String region, final String lore) {
      this.charId = charId;
      this.location = map + " - " + region;
      this.stats = monsterStats_8010ba98[charId];
      this.lore = lore;
      this.kill = Statistics.getMonsterKill(this.charId);
      this.name = name == null ? monsterNames_80112068[this.charId] : name;
      this.maxKill = maxKill;

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
    this.addPage(0, null, -1, "Prairie", "Southern Serdio", "This is lore");
    this.addPage(1, null, -1, "Barrens", "Tiberoa", "This is lore");
    this.addPage(2, null, -1, "Kadessa", "", "This is lore");
    this.addPage(3, null, -1, "Death Frontier", "Gloriano", "This is lore");
    this.addPage(4, null, -1, "Marshland", "", "This is lore");
    this.addPage(5, null, -1, "Kashua Glacier", "", "This is lore");
    this.addPage(6, null, -1, "Mortal Dragon Mountain", "Mille Seseau", "This is lore");
    this.addPage(7, null, -1, "Divine Tree", "Gloriano", "This is lore");
    this.addPage(8, null, -1, "Forest", "Southern Serdio", "This is lore");
    this.addPage(9, null, -1, "Aglis", "", "This is lore");
    this.addPage(10, null, -1, "Mayfil", "", "This is lore");
    this.addPage(11, null, -1, "Flanvel Tower", "", "This is lore");
    this.addPage(12, null, -1, "Undersea Cavern", "", "This is lore");
    this.addPage(13, null, -1, "Limestone Cave", "", "This is lore");
    this.addPage(14, null, -1, "Valley of Corrupted Gravity", "", "This is lore");
    this.addPage(15, null, -1, "Shirley's Shrine", "", "This is lore");
    this.addPage(16, null, -1, "Valley of Corrupted Gravity", "", "This is lore");
    this.addPage(17, null, -1, "Black Castle", "", "This is lore");
    this.addPage(18, null, -1, "Phantom Ship", "", "This is lore");
    this.addPage(19, null, -1, "Dragon's Nest", "", "This is lore");
    this.addPage(20, null, -1, "Snowfield", "", "This is lore");
    this.addPage(21, null, -1, "Forest", "", "This is lore");
    this.addPage(22, null, -1, "Marshland", "", "This is lore");
    this.addPage(23, null, -1, "Undersea Cavern", "", "This is lore");
    this.addPage(24, null, -1, "Forest", "", "This is lore");
    this.addPage(25, null, -1, "Flanvel Tower", "", "This is lore");
    this.addPage(26, null, -1, "Barrens", "", "This is lore");
    this.addPage(27, null, -1, "Prairie", "", "This is lore");
    this.addPage(28, null, -1, "Marshland", "", "This is lore");
    this.addPage(29, null, -1, "Valley of Corrupted Gravity", "", "This is lore");
    this.addPage(30, null, -1, "Kadessa", "", "This is lore");
    this.addPage(31, null, -1, "Snowfield", "", "This is lore");
    this.addPage(32, null, -1, "Kashua Glacier", "", "This is lore");
    this.addPage(33, null, -1, "Villude Volcano", "variant: 8 AT, 10% drop", "This is lore");
    this.addPage(34, null, -1, "Moon", "", "This is lore");
    this.addPage(35, null, -1, "Dragon's Nest", "", "This is lore");
    this.addPage(36, null, -1, "Death Frontier", "", "This is lore");
    this.addPage(37, null, -1, "Mortal Dragon Mountain", "", "This is lore");
    this.addPage(38, null, -1, "Forest", "", "This is lore");
    this.addPage(39, null, -1, "Vellweb", "", "This is lore");
    this.addPage(40, null, -1, "Zenebatos", "", "This is lore");
    this.addPage(41, null, -1, "Vellweb", "", "This is lore");
    this.addPage(42, null, -1, "Moon", "", "This is lore");
    this.addPage(43, null, -1, "Kadessa", "", "This is lore");
    this.addPage(44, null, -1, "Kadessa", "", "This is lore");
    this.addPage(45, null, -1, "Prairie", "", "This is lore");
    this.addPage(46, null, -1, "Limestone Cave", "", "This is lore");
    this.addPage(47, null, -1, "Vellweb", "", "This is lore");
    this.addPage(48, null, -1, "Kashua Glacier", "", "This is lore");
    this.addPage(49, null, -1, "Flanvel Tower", "", "This is lore");
    this.addPage(50, null, -1, "Snowfield", "", "This is lore");
    this.addPage(51, null, -1, "Evergreen Forest", "", "This is lore");
    this.addPage(52, null, -1, "Valley of Corrupted Gravity", "", "This is lore");
    this.addPage(53, null, -1, "Evergreen Forest", "", "This is lore");
    this.addPage(54, null, -1, "World Map", "", "This is lore");
    this.addPage(55, null, -1, "Shirley's Shrine", "", "This is lore");
    this.addPage(56, null, -1, "Flanvel Tower", "", "This is lore");
    this.addPage(57, null, -1, "Snowfield", "", "This is lore");
    this.addPage(58, null, -1, "Shirley's Shrine", "", "This is lore");
    this.addPage(59, null, -1, "Prairie", "", "This is lore");
    this.addPage(60, null, -1, "Aglis", "", "This is lore");
    this.addPage(61, null, -1, "Death Frontier", "", "This is lore");
    this.addPage(62, null, -1, "Divine Tree", "", "This is lore");
    this.addPage(63, null, -1, "Vellweb", "", "This is lore");
    this.addPage(64, null, -1, "Limestone Cave", "", "This is lore");
    this.addPage(65, null, -1, "Vellweb", "", "This is lore");
    this.addPage(66, null, -1, "Shirley's Shrine", "", "This is lore");
    this.addPage(67, null, -1, "Kashua Glacier", "", "This is lore");
    this.addPage(68, null, -1, "Marshland", "", "This is lore");
    this.addPage(69, null, -1, "Barrens", "", "This is lore");
    this.addPage(70, null, -1, "Phantom Ship", "", "This is lore");
    this.addPage(71, null, -1, "Moon", "", "This is lore");
    this.addPage(72, null, -1, "Aglis", "", "This is lore");
    this.addPage(73, null, -1, "Evergreen Forest", "", "This is lore");
    this.addPage(74, null, -1, "Villude Volcano", "", "This is lore");
    this.addPage(75, null, -1, "Aglis", "", "This is lore");
    this.addPage(76, null, -1, "Undersea Cavern", "", "This is lore");
    this.addPage(77, null, -1, "Divine Tree", "", "This is lore");
    this.addPage(78, null, -1, "Mayfil", "", "This is lore");
    this.addPage(79, null, -1, "Giganto Home", "", "This is lore");
    this.addPage(80, null, -1, "Zenebatos", "", "This is lore");
    this.addPage(81, null, -1, "Moon", "", "This is lore");
    this.addPage(82, null, -1, "Giganto Home", "", "This is lore");
    this.addPage(83, null, -1, "Giganto Home", "", "This is lore");
    this.addPage(84, null, -1, "Mayfil", "", "This is lore");
    this.addPage(85, null, -1, "Phantom Ship", "", "This is lore");
    this.addPage(86, null, -1, "Phantom Ship", "", "This is lore");
    this.addPage(87, null, -1, "", "", "This is lore");
    this.addPage(88, null, -1, "Dragon's Nest", "", "This is lore");
    this.addPage(89, null, -1, "Death Frontier", "", "This is lore");
    this.addPage(90, null, -1, "Dragon's Nest", "", "This is lore");
    this.addPage(91, null, -1, "Undersea Cavern", "", "This is lore");
    this.addPage(92, null, -1, "Villude Volcano", "", "This is lore");
    this.addPage(93, null, -1, "Moon", "", "This is lore");
    this.addPage(94, null, -1, "Limestone Cave", "", "This is lore");
    this.addPage(95, null, -1, "Evergreen Forest", "", "This is lore");
    this.addPage(96, null, -1, "Mortal Dragon Mountain", "", "This is lore");
    this.addPage(97, null, -1, "Zenebatos", "", "This is lore");
    this.addPage(98, null, -1, "Divine Tree", "", "This is lore");
    this.addPage(99, null, -1, "Giganto Home", "", "This is lore");
    this.addPage(100, null, -1, "Villude Volcano", "", "This is lore");
    this.addPage(101, null, -1, "Phantom Ship", "", "This is lore");
    this.addPage(102, null, -1, "Hoax", "", "This is lore");
    this.addPage(103, null, -1, "", "", "This is lore");
    this.addPage(104, null, -1, "Limestone Cave", "", "This is lore");
    this.addPage(105, null, -1, "Hellena Prison", "", "This is lore");
    this.addPage(106, null, -1, "Valley of Corrupted Gravity", "", "This is lore");
    this.addPage(107, null, -1, "Shirley's Shrine", "", "This is lore");
    this.addPage(108, null, -1, "Barrens", "", "This is lore");
    this.addPage(109, null, -1, "Giganto Home", "", "This is lore");
    this.addPage(110, null, -1, "Undersea Cavern", "", "This is lore");
    this.addPage(111, null, -1, "Kadessa", "", "This is lore");
    this.addPage(112, null, -1, "Mortal Dragon Mountain", "", "This is lore");
    this.addPage(113, null, -1, "Mortal Dragon Mountain", "", "This is lore");
    this.addPage(114, null, -1, "Kashua Glacier", "", "This is lore");
    this.addPage(115, null, -1, "Dragon's Nest", "", "This is lore");
    this.addPage(116, null, -1, "Snowfield", "", "This is lore");
    this.addPage(117, null, -1, "Evergreen Forest", "", "This is lore");
    this.addPage(118, null, -1, "Zenebatos", "", "This is lore");
    this.addPage(119, null, -1, "Death Frontier", "", "This is lore");
    this.addPage(120, null, -1, "Moon", "", "This is lore");
    this.addPage(121, null, -1, "Aglis", "", "This is lore");
    this.addPage(122, null, -1, "Zenebatos", "", "This is lore");
    this.addPage(123, null, -1, "Moon", "", "This is lore");
    this.addPage(124, null, -1, "World Map", "", "This is lore");
    this.addPage(125, null, -1, "Mayfil", "", "This is lore");
    this.addPage(126, null, -1, "Divine Tree", "", "This is lore");
    this.addPage(127, null, -1, "Moon", "", "This is lore");
    this.addPage(128, null, -1, "Moon", "", "This is lore");
    this.addPage(129, null, -1, "Hellena Prison", "", "This is lore");
    this.addPage(130, null, -1, "", "", "This is lore");
    this.addPage(131, null, -1, "", "", "This is lore");
    this.addPage(132, null, -1, "Marshland", "", "This is lore");
    this.addPage(133, null, -1, "Hellena Prison", "", "This is lore");
    this.addPage(134, null, -1, "Hellena Prison", "", "This is lore");
    this.addPage(135, null, -1, "", "", "This is lore");
    this.addPage(136, null, -1, "", "", "This is lore");
    this.addPage(137, null, -1, "", "", "This is lore");
    this.addPage(138, null, -1, "", "", "This is lore");
    this.addPage(139, null, -1, "", "", "This is lore");
    this.addPage(144, null, -1, "", "", "This is lore");
    this.addPage(146, null, -1, "Villude Volcano", "", "This is lore");
    this.addPage(148, null, -1, "", "", "This is lore");
    this.addPage(150, null, -1, "", "", "This is lore");
    this.addPage(152, null, -1, "", "", "This is lore");
    this.addPage(154, null, -1, "", "", "This is lore");
    this.addPage(155, null, -1, "", "", "This is lore");
    this.addPage(157, null, -1, "", "", "This is lore");
    this.addPage(256, null, -1, "Seles", "", "This is lore");
    this.addPage(257, null, -1, "Seles", "", "This is lore");
    this.addPage(258, null, -1, "Black Castle", "", "This is lore");
    this.addPage(259, null, -1, "", "", "This is lore");
    this.addPage(260, null, -1, "", "", "This is lore");
    this.addPage(261, null, -1, "", "", "This is lore");
    this.addPage(262, null, -1, "", "", "This is lore");
    this.addPage(263, null, -1, "Hellena Prison", "", "This is lore");
    this.addPage(264, null, -1, "Hellena Prison", "", "This is lore");
    this.addPage(267, null, -1, "Black Castle", "", "This is lore");
    this.addPage(268, null, -1, "Black Castle", "", "This is lore");
    this.addPage(269, null, -1, "Lohan", "", "This is lore");
    this.addPage(270, null, -1, "Flanvel Tower", "", "This is lore");
    this.addPage(273, null, -1, "", "", "This is lore");
    this.addPage(275, null, -1, "Dragon's Nest", "", "This is lore");
    this.addPage(279, null, -1, "Undersea Cavern", "", "This is lore");
    this.addPage(283, null, -1, "Mortal Dragon Mountain", "", "This is lore");
    this.addPage(287, null, -1, "Dragon's Nest", "", "This is lore");
    this.addPage(288, null, -1, "Shirley's Shrine", "", "This is lore");
    this.addPage(293, null, -1, "Fletz", "", "This is lore");
    this.addPage(294, null, -1, "Undersea Cavern", "", "This is lore");
    this.addPage(295, null, -1, "Vellweb", "", "This is lore");
    this.addPage(296, null, -1, "Vellweb", "", "This is lore");
    this.addPage(297, null, -1, "Vellweb", "", "This is lore");
    this.addPage(298, null, -1, "Vellweb", "", "This is lore");
    this.addPage(299, null, -1, "Barrens", "", "This is lore");
    this.addPage(301, null, -1, "Giganto Home", "", "This is lore");
    this.addPage(302, null, -1, "Lohan", "", "This is lore");
    this.addPage(303, null, -1, "Lohan", "", "This is lore");
    this.addPage(304, null, -1, "Lohan", "", "This is lore");
    this.addPage(305, null, -1, "Lohan", "", "This is lore");
    this.addPage(308, null, -1, "Villude Volcano", "", "This is lore");
    this.addPage(311, null, -1, "Valley of Corrupted Gravity", "", "This is lore");
    this.addPage(316, null, -1, "Moon", "", "This is lore");
    this.addPage(325, null, -1, "Shirley's Shrine", "", "This is lore");
    this.addPage(329, null, -1, "Hellena Prison", "", "This is lore");
    this.addPage(332, null, -1, "Limestone Cave", "", "This is lore");
    this.addPage(333, null, -1, "Villude Volcano", "", "This is lore");
    this.addPage(334, null, -1, "Villude Volcano", "", "This is lore");
    this.addPage(335, null, -1, "Kadessa", "", "This is lore");
    this.addPage(340, null, -1, "Phantom Ship", "", "This is lore");
    this.addPage(341, null, -1, "", "", "This is lore");
    this.addPage(343, null, 1, "Evergreen Forest", "", "This is lore");
    this.addPage(344, null, 1, "Flanvel Tower", "", "This is lore");
    this.addPage(346, null, 1, "Kashua Glacier", "", "This is lore");
    this.addPage(349, "Polter", 1, "Snowfield", "", "This is lore");
    this.addPage(352, "Dragon Spirit (Divine Dragon)", 1, "Mayfil", "", "This is lore");
    this.addPage(353, "Dragon Spirit (Regole)", 1, "Mayfil", "", "This is lore");
    this.addPage(354, "Dragon Spirit (Feyrbrand)", 1, "Mayfil", "", "This is lore");
    this.addPage(360, null, 1, "Zenebatos", "", "This is lore");
    this.addPage(361, null, 1, "Zenebatos", "", "This is lore");
    this.addPage(362, null, 1, "Zenebatos", "", "This is lore");
    this.addPage(363, null, 1, "Mayfil", "", "This is lore");
    this.addPage(364, null, 1, "Mayfil", "", "This is lore");
    this.addPage(365, null, 1, "Aglis", "", "This is lore");
    this.addPage(366, null, 1, "", "", "This is lore");
    this.addPage(368, null, 1, "Divine Tree", "", "This is lore");
    this.addPage(369, null, 1, "Divine Tree", "", "This is lore");
    this.addPage(370, null, 1, "Divine Tree", "", "This is lore");
    this.addPage(371, null, 1, "Moon", "", "This is lore");
    this.addPage(378, null, 1, "Moon", "", "This is lore");
    this.addPage(381, null, 1, "Moon", "", "This is lore");
    this.addPage(382, null, 1, "Moon", "", "This is lore");
    this.addPage(387, null, 1, "Moon", "", "This is lore");
    this.addPage(388, null, 1, "Moon", "", "This is lore");
  }

  private void addPage(final int charId, @Nullable final String name, final int killCount, final String map, final String region, final String lore) {
    this.bestiaryPages.add(new BestiaryRecord(charId, name, killCount, map, region, lore));
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

    if(this.isListVisible) {
      this.renderList();
    }

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

  private void renderList() {
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
