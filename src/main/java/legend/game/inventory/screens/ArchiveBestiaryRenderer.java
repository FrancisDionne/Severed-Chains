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
import legend.game.wmap.MapMarker;
import legend.game.wmap.WMapModelAndAnimData258;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static legend.core.GameEngine.RENDERER;
import static legend.game.SItem.FUN_801034cc;
import static legend.game.SItem.renderItemIcon;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.combat.Monsters.enemyRewards_80112868;
import static legend.game.combat.Monsters.monsterNames_80112068;
import static legend.game.combat.Monsters.monsterStats_8010ba98;

public class ArchiveBestiaryRenderer {

  private static class BestiaryEntry {
    public int entryNumber;
    public int charId;
    public String location;
    public MonsterStats1c stats;
    public String lore;
    public int kill;
    public int rank;
    public String name;
    public int maxKill;
    public float[] elementRGB;

    public BestiaryEntry(final int entryNumber, final int charId, final int maxKill, @Nullable final String name, final String map, final String region, final String lore) {
      this.entryNumber = entryNumber;
      this.charId = charId;
      this.location = map + (region.isEmpty() ? "" : " - " + region);
      this.stats = monsterStats_8010ba98[charId];
      this.lore = devMode ? lore : "";
      this.kill = Statistics.getMonsterKill(this.charId);
      this.name = name == null ? monsterNames_80112068[this.charId] : name;
      this.maxKill = maxKill;

      final int[] elementRGB = ArchiveBestiaryRenderer.getElementBackgroundRGB(this.stats.elementFlag_0f);
      this.elementRGB = new float[] { elementRGB[0] / 255f, elementRGB[1] / 255f, elementRGB[2] / 255f, elementRGB[3] / 100f * 0.8f};

      if(this.kill >= 10 || (this.maxKill > -1 && this.kill >= this.maxKill)) {
        this.rank = 3;
      } else if(this.kill >= 5) {
        this.rank = 2;
      } else if(this.kill >= 1) {
        this.rank = 1;
      } else {
        this.rank = 0;
      }

      if(devMode) {
        this.rank = 3;
      }
    }

    public boolean isPerfect() {
      return this.rank >= 3;
    }
  }

  public static final boolean devMode = true;
  private static final int LIST_ITEM_COUNT = 24;
  private static final String QUESTION_MARK_5 = "?????";
  private static final String QUESTION_MARK_3 = "???";
  private static final String LORE_DEFAULT = "Lore coming one day to a QoL mod near you...";

  private final Matrix4f m;
  private final Obj quad;
  private final Texture[] textures;
  private Texture headerTexture;
  private Texture modelTexture;
  private List<BestiaryEntry> bestiaryEntries;
  private BestiaryEntry monster;
  private boolean bestiaryPerfect;
  private int bestiarySeenCount;
  private UiBox listBox;
  private final WMapModelAndAnimData258 modelAndAnimData_800c66a8;

  private final FontOptions headerFont;
  private final FontOptions headerNumberFont;
  private final FontOptions locationFont;
  private final FontOptions statsFont;
  private final FontOptions loreFont;
  private final FontOptions titleFont;
  private final FontOptions resistFont;
  private final FontOptions resistNumberFont;
  private final FontOptions rewardTitleFont;
  private final FontOptions listNumberFont;
  private final FontOptions listNumberHighlightFont;
  private final FontOptions listNumberPerfectHighlightFont;
  private final FontOptions listFont;
  private final FontOptions listPerfectFont;
  private final FontOptions listHighlightFont;
  private final FontOptions listPerfectHighlightFont;
  private final FontOptions listTotalFont;
  private final FontOptions listTotalPerfectFont;
  private final FontOptions completeFont;
  private final FontOptions sortFont;

  public int entryIndex;
  private float currentBoxOffsetX;
  public boolean isListVisible;
  private int listFirstVisibleItem;
  private int currentSort;

  private final NumberFormat nf = new DecimalFormat("000");

  public int getEntryCount() {
    return this.bestiaryEntries.size();
  }

  private BestiaryEntry getCurrentEntry() {
    return this.bestiaryEntries.get(this.entryIndex);
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

    this.modelAndAnimData_800c66a8 = new WMapModelAndAnimData258();
    this.modelAndAnimData_800c66a8.mapArrow = new MapMarker("MapArrow", 8, 16.0f, 16, 32, false);
    this.modelAndAnimData_800c66a8.mapArrow.setSize(10.0f);
    this.modelAndAnimData_800c66a8.coolonPlaceMarker = new MapMarker("CoolonPlaceMarker", 3, 10.0f, 16, 0, true);
    this.modelAndAnimData_800c66a8.coolonPlaceMarker.setSize(7.0f);

    this.headerFont = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.DARK_GREY).size(1.1f).horizontalAlign(HorizontalAlign.CENTRE);
    this.headerNumberFont = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.DARK_GREY).size(0.9f).horizontalAlign(HorizontalAlign.LEFT);
    this.statsFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.65f).horizontalAlign(HorizontalAlign.LEFT);
    this.loreFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
    this.locationFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.55f).horizontalAlign(HorizontalAlign.LEFT);
    this.titleFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.75f).horizontalAlign(HorizontalAlign.RIGHT);
    this.resistFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.50f).horizontalAlign(HorizontalAlign.LEFT);
    this.resistNumberFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
    this.rewardTitleFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.65f).horizontalAlign(HorizontalAlign.LEFT);
    this.listNumberFont = new FontOptions().colour(TextColour.LIGHTER_GREY).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.CENTRE);
    this.listNumberHighlightFont = new FontOptions().colour(TextColour.YELLOW).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.CENTRE);
    this.listNumberPerfectHighlightFont = new FontOptions().colour(TextColour.GOLD).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.CENTRE);
    this.listFont = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
    this.listPerfectFont = new FontOptions().colour(TextColour.LIGHT_GOLD).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
    this.listHighlightFont = new FontOptions().colour(TextColour.YELLOW).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
    this.listPerfectHighlightFont = new FontOptions().colour(TextColour.GOLD).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
    this.listTotalFont = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.DARKER_GREY).size(0.55f).horizontalAlign(HorizontalAlign.RIGHT);
    this.listTotalPerfectFont = new FontOptions().colour(TextColour.LIGHT_GOLD).shadowColour(TextColour.DARKER_GREY).size(0.55f).horizontalAlign(HorizontalAlign.RIGHT);
    this.completeFont = new FontOptions().colour(TextColour.GOLD).shadowColour(TextColour.DARKER_GREY).size(0.7f).horizontalAlign(HorizontalAlign.RIGHT);
    this.sortFont = new FontOptions().colour(TextColour.YELLOW).shadowColour(TextColour.DARKER_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);

    this.textures = new Texture[] {
      Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\bestiary_graphics.png")),  //0
      Texture.png(Path.of("gfx", "ui", "action_attack.png")),  //1
      Texture.png(Path.of("gfx", "ui", "action_guard.png")),   //2
      Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\black.png")),   //3
      Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\highlight.png")),   //4
      Texture.png(Path.of("gfx", "ui", "arrow_blue_up.png")),    //5
      Texture.png(Path.of("gfx", "ui", "arrow_blue_down.png")),  //6
      Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\list_underline.png")),   //7
      Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\white.png")),   //8
      Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\bestiary_graphics_frames.png")),   //9
    };

    this.entryIndex = 0;

    this.loadEntries();
    this.setBestiaryStatus();
    this.loadCurrentEntry();
  }

  private void loadEntries() {
    this.bestiaryEntries = new ArrayList<>();
    this.addEntry(257, -1, null, "Seles", "Southern Serdio", "");
    this.addEntry(256, -1, null, "Seles", "Southern Serdio", "");
    this.addEntry(24, -1, null, "Forest", "Southern Serdio", "");
    this.addEntry(21, -1, null, "Forest", "Southern Serdio", "");
    this.addEntry(8, -1, null, "Forest", "Southern Serdio", "Strong fighter in the Prairie.\nSeems weak, but does very high damage\nwhen at low health.");
    this.addEntry(42, -1, null, "Endiness / Moon", "", "");
    this.addEntry(54, -1, null, "Endiness", "", "");
    this.addEntry(87, -1, null, "Endiness", "", "");
    this.addEntry(138, -1, null, "Endiness", "", "");
    this.addEntry(38, -1, null, "Forest", "Southern Serdio", "");
    this.addEntry(124, -1, null, "Endiness", "", "");
    this.addEntry(136, -1, null, "Endiness", "", "");
    this.addEntry(137, -1, null, "Endiness", "", "");
    this.addEntry(139, -1, null, "Endiness", "", "");
    this.addEntry(59, -1, null, "Prairie", "Southern Serdio", "");
    this.addEntry(134, -1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(46, -1, null, "Limestone Cave", "Northern Serdio", "");
    this.addEntry(259, -1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(27, -1, null, "Prairie", "Southern Serdio", "");
    this.addEntry(45, -1, null, "Prairie", "Southern Serdio", "");
    this.addEntry(0, -1, null, "Prairie", "Southern Serdio", "The Mantis is a strong creature,\nfeaturing high physical stats and Power Up.\nWeaker to magic.");
    this.addEntry(104, -1, null, "Limestone Cave", "Northern Serdio", "");
    this.addEntry(148, -1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(260, -1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(94, -1, null, "Limestone Cave", "Northern Serdio", "");
    this.addEntry(157, -1, null, "Villude Volcano", "Southern Serdio", "");
    this.addEntry(74, -1, null, "Villude Volcano", "Southern Serdio", "");
    this.addEntry(33, -1, null, "Villude Volcano", "Southern Serdio", "");
    this.addEntry(150, -1, null, "Villude Volcano", "Southern Serdio", "");
    this.addEntry(13, -1, null, "Limestone Cave", "Northern Serdio", "");
    this.addEntry(28, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(68, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(115, -1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(4, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(64, -1, null, "Limestone Cave", "Northern Serdio", "");
    this.addEntry(19, -1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(100, -1, null, "Villude Volcano", "Southern Serdio", "");
    this.addEntry(130, -1, null, "Hoax", "Northern Serdio", "");
    this.addEntry(146, -1, null, "Villude Volcano", "Southern Serdio", "");
    this.addEntry(92, -1, null, "Villude Volcano", "Southern Serdio", "");
    this.addEntry(22, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(55, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(131, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(35, -1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(107, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(108, -1, null, "Barrens", "Tiberoa", "");
    this.addEntry(132, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(1, -1, null, "Barrens", "Tiberoa", "Decent physical attacker with good defense.\nWeaker to magic.\nNot named after the perfect emo movie.");
    this.addEntry(15, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(261, 1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(90, -1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(14, -1, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(58, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(105, -1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(52, -1, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(129, -1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(69, -1, null, "Barrens", "Tiberoa", "");
    this.addEntry(88, -1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(111, -1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(135, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(106, -1, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(17, -1, null, "Black Castle", "Southern Serdio", "");
    this.addEntry(133, -1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(66, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(76, -1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(86, -1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(109, -1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(114, -1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(154, -1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(155, -1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(302, 1, null, "Lohan", "Southern Serdio", "");
    this.addEntry(99, -1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(112, -1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(103, -1, null, "Black Castle", "Southern Serdio", "");
    this.addEntry(18, -1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(26, -1, null, "Barrens", "Tiberoa", "");
    this.addEntry(83, -1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(85, -1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(303, 1, null, "Lohan", "Southern Serdio", "");
    this.addEntry(29, -1, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(96, -1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(304, 1, null, "Lohan", "Southern Serdio", "");
    this.addEntry(30, -1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(53, -1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(101, -1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(152, -1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(305, 1, null, "Lohan", "Southern Serdio", "");
    this.addEntry(82, -1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(110, -1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(119, -1, null, "Death Frontier", "Death Frontier", "");
    this.addEntry(258, -1, null, "Black Castle", "Southern Serdio", "");
    this.addEntry(273, 1, null, "Black Castle", "Southern Serdio", "");
    this.addEntry(332, 1, null, "Limestone Cave", "Northern Serdio", "");
    this.addEntry(16, -1, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(117, -1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(341, 1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(44, -1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(5, -1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(23, -1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(43, -1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(50, -1, null, "Snowfield", "Gloriano", "");
    this.addEntry(89, -1, null, "Death Frontier", "Death Frontier", "");
    this.addEntry(113, -1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(6, -1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(287, 1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(125, -1, null, "Mayfil", "Gloriano", "");
    this.addEntry(32, -1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(63, -1, null, "Vellweb", "Gloriano", "");
    this.addEntry(73, -1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(308, 1, "Wounded Virage", "Villude Volcano", "Southern Serdio", "");
    this.addEntry(102, -1, null, "Hoax", "Northern Serdio", "");
    this.addEntry(2, -1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(269, 1, null, "Lohan", "Southern Serdio", "");
    this.addEntry(31, -1, null, "Snowfield", "Gloriano", "");
    this.addEntry(39, -1, null, "Vellweb", "Gloriano", "");
    this.addEntry(75, -1, null, "Aglis", "Broken Islands", "");
    this.addEntry(79, -1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(91, -1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(263, 1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(41, -1, null, "Vellweb", "Gloriano", "");
    this.addEntry(47, -1, null, "Vellweb", "Gloriano", "");
    this.addEntry(95, -1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(116, -1, null, "Snowfield", "Gloriano", "");
    this.addEntry(61, -1, null, "Death Frontier", "Death Frontier", "");
    this.addEntry(275, 1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(25, -1, null, "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(20, -1, null, "Snowfield", "Gloriano", "");
    this.addEntry(264, 1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(36, -1, null, "Death Frontier", "Death Frontier", "");
    this.addEntry(118, -1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(37, -1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(12, -1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(51, -1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(65, -1, null, "Vellweb", "Gloriano", "");
    this.addEntry(67, -1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(77, -1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(84, -1, null, "Mayfil", "Gloriano", "");
    this.addEntry(97, -1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(267, 1, null, "Black Castle", "Southern Serdio", "");
    this.addEntry(40, -1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(60, -1, null, "Aglis", "Broken Islands", "");
    this.addEntry(98, -1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(121, -1, null, "Aglis", "Broken Islands", "");
    this.addEntry(288, 1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(299, 1, null, "Barrens", "Tiberoa", "");
    this.addEntry(333, 1, null, "Villude Volcano", "Southern Serdio", "");
    this.addEntry(11, -1, null, "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(49, -1, null, "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(122, -1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(57, -1, null, "Snowfield", "Gloriano", "");
    this.addEntry(78, -1, null, "Mayfil", "Gloriano", "");
    this.addEntry(34, -1, null, "Moon", "Gloriano", "");
    this.addEntry(80, -1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(70, -1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(72, -1, null, "Aglis", "Broken Islands", "");
    this.addEntry(128, -1, null, "Moon", "Gloriano", "");
    this.addEntry(7, -1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(9, -1, null, "Aglis", "Broken Islands", "");
    this.addEntry(10, -1, null, "Mayfil", "Gloriano", "");
    this.addEntry(126, -1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(262, 1, "Fruegel II", "Hellena Prison", "Southern Serdio", "");
    this.addEntry(56, -1, null, "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(144, -1, null, "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(123, -1, null, "Moon", "Gloriano", "");
    this.addEntry(62, -1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(300, 1, null, "Barrens", "Tiberoa", "");
    this.addEntry(325, 1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(48, -1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(93, -1, null, "Moon", "Gloriano", "");
    this.addEntry(329, 1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(366, 1, null, "Aglis", "Broken Islands", "");
    this.addEntry(3, -1, null, "Death Frontier", "Death Frontier", "Physical attacker often accompanied by other creatures.\nHas a special sand attack.\nWishes it was a Graboid from the Tremors films.");
    this.addEntry(381, 1, null, "Moon", "Gloriano", "");
    this.addEntry(311, 1, "Virage", "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(120, -1, null, "Moon", "Gloriano", "");
    this.addEntry(340, 1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(268, 1, "Doel (Dragoon)", "Black Castle", "Southern Serdio", "");
    this.addEntry(81, -1, null, "Moon", "Gloriano", "");
    this.addEntry(301, 1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(127, -1, null, "Moon", "Gloriano", "");
    this.addEntry(349, 1, "Polter Armor", "Snowfield", "Gloriano", "");
    this.addEntry(371, 1, null, "Moon", "Gloriano", "");
    this.addEntry(369, 1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(279, 1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(294, 1, "Lenus (Dragoon)", "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(361, 1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(382, 1, null, "Moon", "Gloriano", "");
    this.addEntry(71, -1, null, "Moon", "Gloriano", "");
    this.addEntry(293, 1, null, "Fletz", "Tiberoa", "");
    this.addEntry(362, 1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(343, 1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(360, 1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(335, 1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(283, 1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(364, 1, null, "Mayfil", "Gloriano", "");
    this.addEntry(270, 1, "Lloyd (Wingly Armor)", "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(368, 1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(354, 1, "Feyrbrand Spirit", "Mayfil", "Gloriano", "");
    this.addEntry(363, 1, null, "Mayfil", "Gloriano", "");
    this.addEntry(295, 1, null, "Vellweb", "Gloriano", "");
    this.addEntry(378, 1, null, "Moon", "Gloriano", "");
    this.addEntry(296, 1, null, "Vellweb", "Gloriano", "");
    this.addEntry(320, 1, "Super Virage", "Moon", "Gloriano", "");
    this.addEntry(346, 1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(365, 1, null, "Aglis", "Broken Islands", "");
    this.addEntry(316, 1, "Wounded Super Virage", "Moon", "Gloriano", "");
    this.addEntry(298, 1, null, "Vellweb", "Gloriano", "");
    this.addEntry(353, 1, "Regole Spirit", "Mayfil", "Gloriano", "");
    this.addEntry(370, 1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(387, 1, null, "Moon", "Gloriano", "");
    this.addEntry(297, 1, null, "Vellweb", "Gloriano", "");
    this.addEntry(352, 1, "Divine Dragon Spirit", "Mayfil", "Gloriano", "");
    this.addEntry(344, 1, null, "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(388, 1, null, "Moon", "Gloriano", "");
  }

  private void addEntry(final int charId, final int killCount, @Nullable final String name, final String map, final String region, final String lore) {
    this.bestiaryEntries.add(new BestiaryEntry(this.bestiaryEntries.size() + 1, charId, killCount, name, map, region, lore));
  }

  public void loadCurrentEntry() {
    this.monster = this.bestiaryEntries.get(this.entryIndex);
    this.headerTexture = Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\header_element_" + this.getElement(this.monster.stats.elementFlag_0f).getRegistryId().entryId() + ".png"));

    try {
      this.modelTexture = Texture.png(Path.of("gfx", "models", this.monster.charId + ".png"));
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

    FUN_801034cc(this.entryIndex, this.bestiaryEntries.size(), -10, this.isListVisible); // Left/right arrows
  }

  private void renderGraphics() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    this.m.translation(-0.5f - xOffset, 0, 128);
    this.m.scale(369f, 241f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[0]);

    if(this.monster.rank > 0) {
      this.m.translation(-0.5f - xOffset, 0, 128);
      this.m.scale(369f, 241f, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[9]) //Frames
        .colour(this.monster.elementRGB[0], this.monster.elementRGB[1], this.monster.elementRGB[2])
        .alpha(this.monster.elementRGB[3])
        .translucency(Translucency.HALF_B_PLUS_HALF_F);
    }
  }

  private void renderModel() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    this.m.translation(-22f + xOffset, 27.8f, 126);
    this.m.scale(210f, 87.3f, 1);

    if(this.monster.rank >= 1) {
      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.modelTexture); //Model
    } else {
      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.modelTexture)
        .colour(0, 0, 0)
        .alpha(0.85f)
        .translucency(Translucency.HALF_B_PLUS_HALF_F);
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

      if(devMode) {
        renderText(this.monster.name + " [" + this.getCurrentEntry().charId + ']', 184, 10.5f, this.headerFont, 126);
      } else {
        renderText(this.monster.name, 184, 10.5f, this.headerFont, 126);
      }
      renderText(this.monster.location, 31, 206.5f, this.locationFont, 127);
    } else {
      renderText(QUESTION_MARK_5, 184, 10.5f, this.headerFont, 126);
      renderText(QUESTION_MARK_5, 31, 206.5f, this.locationFont, 126);
    }

//    int u = (int)(tickCount_800bb0fc / (3.0f / vsyncMode_8007a3b8)) & 0x7;
//    this.modelAndAnimData_800c66a8.mapArrow.render(u, 2, 19.8f, 200, 126f);

//    u = (int)(tickCount_800bb0fc / 5 / (3.0f / vsyncMode_8007a3b8) % 3);
//    this.modelAndAnimData_800c66a8.coolonPlaceMarker.render(u, 2, 21f, 206f, 127f);

    renderText(this.nf.format(this.monster.entryNumber), 15f, 28.5f, this.headerNumberFont, 125);
    renderText("Defeated: " + (this.monster.maxKill > -1 ? Math.min(this.monster.maxKill, this.monster.kill) : this.monster.kill), 23, 123, this.statsFont, 127);

    if(this.monster.isPerfect()) {
      renderText("COMPLETE", 180.5f, 122.5f, this.completeFont, 126);
    }
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
        renderText(QUESTION_MARK_3, x + 28f, y, this.statsFont, 127);
      }
      y += 11.45f;
    }

    x = 272;
    y = 45.4f - 11.45f;

    if(this.monster.stats.elementalImmunityFlag_10 > 0) {
      y += 11.45f;
      this.renderElementMultipliers(this.monster.stats.elementFlag_0f, x, y, xOffset, 100, false);
    } else if(this.monster.stats.elementFlag_0f != 8) {
      y += 11.45f;
      this.renderElementMultipliers(this.monster.stats.elementFlag_0f, x, y, xOffset, 50, false);
    }

    final int counterElement = this.getCounterElement(this.monster.stats.elementFlag_0f);
    if(counterElement != 0) {
      y += 11.45f;
      this.renderElementMultipliers(counterElement, x, y, xOffset, 50, true);
    }
  }

  private void renderElementMultipliers(final int elementFlag, final float x, final float y, final float xOffset, final int defPercent, final boolean counter) {
    final String guardSign = counter ? "-" : "+";

    renderText(this.monster.rank >= 2 ? I18n.translate(this.getElement(elementFlag).getTranslationKey()) : QUESTION_MARK_5, x, y, this.statsFont, 127);

    if(this.monster.rank >= 2) {
      this.m.translation(x + 51.5f + xOffset, y - 1f, 124f);
      this.m.scale(8, 8, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[2]); //Guard

      renderText(guardSign + defPercent + '%', x + 60f, y + 2f, this.resistNumberFont, 127);
    }
  }

  private void renderRewards() {
    final EnemyRewards08 rewards = enemyRewards_80112868[this.getCurrentEntry().charId];
    final float x = 200f;
    float y = 166f;

    renderText("Battle Rewards", x + 74, 150f, this.titleFont, 127);

    if(rewards.xp_00 > 0) {
      renderText(this.monster.isPerfect() ? "Exp." : QUESTION_MARK_3, x, y, this.rewardTitleFont, 127);
      renderText(this.monster.isPerfect() ? String.valueOf(rewards.xp_00) : QUESTION_MARK_5, x + 28f, y, this.rewardTitleFont, 127);
      y += 13.5f;
    }

    if(rewards.gold_02 > 0) {
      renderText(this.monster.isPerfect() ? "Gold" : QUESTION_MARK_3, x, y, this.rewardTitleFont, 127);
      renderText(this.monster.isPerfect() ? String.valueOf(rewards.gold_02) : QUESTION_MARK_5, x + 28f, y, this.rewardTitleFont, 127);
      y += 13.5f;
    }

    if(rewards.itemDrop_05 != null) {
      renderText(this.monster.isPerfect() ? I18n.translate(rewards.itemDrop_05.get().getNameTranslationKey()) + " (" + rewards.itemChance_04 + "%)" : QUESTION_MARK_5, x + 28f, y, this.rewardTitleFont, 127);

      if(this.monster.isPerfect()) {
        renderItemIcon(rewards.itemDrop_05.get().getIcon(), x + 10f, y - 5f, 32, 0.9f, 0.9f, 0x8);
      } else {
        renderText(QUESTION_MARK_3, x, y, this.rewardTitleFont, 127);
      }
    }
  }

  private void renderLore() {
    if(this.monster.rank >= 2) {
      renderText(this.monster.lore.isEmpty() ? LORE_DEFAULT : this.monster.lore, 23f, 146f, this.loreFont, 127);
    } else {
      renderText(QUESTION_MARK_5, 23f, 146f, this.statsFont, 127);
    }
  }

  private void renderList() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();
    final float x = 6f;

    if(this.listBox == null || this.currentBoxOffsetX != xOffset) {
      this.currentBoxOffsetX = xOffset;
      this.listBox = new UiBox("Bestiary List", x, 14f, 95f, 210f, 0.7f);
    }

    this.m.translation(xOffset - 10, 0, 126);
    this.m.scale(390, 240, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[3]) //Black
      .alpha(0.25f)
      .translucency(Translucency.HALF_B_PLUS_HALF_F);

    this.listBox.render(Config.changeBattleRgb() ? Config.getBattleRgb() : Config.defaultUiColour);

    this.m.translation(xOffset + 10.5f, 26.25f, 124);
    this.m.scale(85, 1, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[7]); //Underline

    renderText(this.bestiarySeenCount + "/" + this.bestiaryEntries.size(), x + 88f, 19.5f, this.bestiaryPerfect ? this.listTotalPerfectFont : this.listTotalFont, 123);

    float y = 32f;
    for(int i = 0; i < LIST_ITEM_COUNT; i++) {
      final int entryIndex = this.listFirstVisibleItem + i;
      final BestiaryEntry entry = this.bestiaryEntries.get(entryIndex);
      final boolean highlighted = this.entryIndex == entryIndex;
      float charX = 6;
      for(final char c : this.nf.format(entry.entryNumber).toCharArray()) {
        renderText(String.valueOf(c), x + charX, y, highlighted ? (entry.isPerfect() ? this.listNumberPerfectHighlightFont : this.listNumberHighlightFont) : this.listNumberFont, 123);
        charX += 3.7f;
      }
      renderText(":", x + 16f, y, highlighted ? (entry.isPerfect() ? this.listNumberPerfectHighlightFont : this.listNumberHighlightFont) : this.listNumberFont, 123);
      renderText(entry.rank > 0 ? entry.name : QUESTION_MARK_5, x + 19f, y, highlighted ? (entry.isPerfect() ? this.listPerfectHighlightFont : this.listHighlightFont) : (entry.isPerfect() ? this.listPerfectFont : this.listFont), 123);

      if(highlighted) {
        this.m.translation(xOffset - x + 14.5f , y - 1.5f, 124);
        this.m.scale(83f, 7.5f, 1);

        RENDERER
          .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
          .texture(this.textures[4]); //Highlight
      }

      y += 7.5f;
    }

    if(this.listFirstVisibleItem > 0) {
      this.m.translation(xOffset - x + 97.5f, 30f, 124);
      this.m.scale(8, 8, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[5]); //Up Arrow
    }

    if(this.listFirstVisibleItem < this.bestiaryEntries.size() - LIST_ITEM_COUNT) {
      this.m.translation(xOffset - x + 97.5f, 203f, 124);
      this.m.scale(8, 8, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[6]); //Down Arrow
    }

    renderText("< Sort: " + this.getSortName() + " >", x + 46.75f, 216f, this.sortFont, 123);
  }

  public boolean next(final int steps) {
    boolean b = false;
    for(int i = 0; i < steps; i++) {
      if(this.entryIndex < this.bestiaryEntries.size() - 1) {
        b = true;
        this.entryIndex++;
        if(this.listFirstVisibleItem < this.bestiaryEntries.size() - LIST_ITEM_COUNT && this.listFirstVisibleItem + LIST_ITEM_COUNT * 0.85f < this.entryIndex) {
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
      if(this.entryIndex > 0) {
        b = true;
        this.entryIndex--;
        if(this.listFirstVisibleItem > 0 && this.listFirstVisibleItem + LIST_ITEM_COUNT * 0.12f > this.entryIndex) {
          this.listFirstVisibleItem--;
        }
      } else {
        break;
      }
    }
    return b;
  }

  public boolean jump(final int index, final boolean fromSort) {
    if(index != this.entryIndex) {
      this.entryIndex = index;
      this.listFirstVisibleItem = Math.clamp(index, 0, this.bestiaryEntries.size() - LIST_ITEM_COUNT);
      if(fromSort) { //Hack to center entry in the list
        this.previous(10);
        this.next(10);
      }
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

  private static int[] getElementBackgroundRGB(final int elementFlag) {
    return switch(elementFlag) {
      case 1 -> new int[] { 0, 196, 255, 35 };    //Water
      case 2 -> new int[] { 81, 55, 0, 35 };      //Earth
      case 4 -> new int[] { 0, 30, 255, 40 };     //Dark
      case 8 -> new int[] { 200, 200, 200, 40 };  //Divine
      case 16 -> new int[] { 97, 0, 196, 35 };   //Thunder
      case 32 -> new int[] { 255, 255, 53, 40 }; //Light
      case 64 -> new int[] { 0, 236, 94, 35 };    //Wind
      case 128 -> new int[] { 255, 15, 0, 35 };   //Fire
      default -> new int[] { 0, 0, 0, 0 };
    };
  }

  private void setBestiaryStatus() {
    int totalAtMaxRank = 0;
    for(final BestiaryEntry r : this.bestiaryEntries) {
      if(r.kill > 0) {
        this.bestiarySeenCount++;
        if(r.isPerfect()) {
          totalAtMaxRank++;
        }
      }
    }
    this.bestiaryPerfect = totalAtMaxRank >= this.bestiaryEntries.size();
  }

  public void cycleSort(final int n) {
    if(n > 0 && this.currentSort + n > 4) {
      this.currentSort = 0;
    } else if(n < 0 && this.currentSort + n < 0) {
      this.currentSort = 4;
    } else {
      this.currentSort += n;
    }
    this.sortList();
  }

  private void sortList() {
    final Comparator<BestiaryEntry> defeatedComparator = Comparator
      .comparing((BestiaryEntry x) -> x.kill, Comparator.reverseOrder())
      .thenComparing(x -> x.entryNumber);

    this.bestiaryEntries = switch(this.currentSort) {
      case 1 -> this.bestiaryEntries.stream()
        .sorted(Comparator.comparingInt(o -> o.entryNumber))
        .sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.name, o2.name))
        .collect(Collectors.toList());
      case 2 -> this.bestiaryEntries.stream()
        .sorted(Comparator.comparingInt(o -> o.entryNumber))
        .sorted(Comparator.comparingInt(o -> o.stats.elementFlag_0f))
        .collect(Collectors.toList());
      case 3 -> this.bestiaryEntries.stream()
        .sorted(Comparator.comparingInt(o -> o.entryNumber))
        .sorted(Comparator.comparingInt(o -> o.rank))
        .sorted(Comparator.comparing(o -> o.rank != 0))
        .collect(Collectors.toList());
      case 4 -> this.bestiaryEntries.stream()
        .sorted(defeatedComparator)
        .collect(Collectors.toList());
      default -> this.bestiaryEntries.stream()
        .sorted(Comparator.comparingInt(o -> o.entryNumber))
        .collect(Collectors.toList());
    };

    this.jump(this.indexOfEntry(this.monster.entryNumber), true);
  }

  private int indexOfEntry(final int number) {
    for(int i = 0; i < this.bestiaryEntries.size(); i++) {
      if(this.bestiaryEntries.get(i).entryNumber == number) {
        return i;
      }
    }
    return -1;
  }

  private String getSortName() {
    return switch(this.currentSort) {
      case 1 -> "Alpha";
      case 2 -> "Element";
      case 3 -> "Completion";
      case 4 -> "Defeated";
      default -> "#";
    };
  }
}
