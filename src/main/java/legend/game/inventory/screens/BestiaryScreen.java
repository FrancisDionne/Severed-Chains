package legend.game.inventory.screens;

import legend.core.Config;
import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputMod;
import legend.game.characters.Element;
import legend.game.combat.types.EnemyRewards08;
import legend.game.combat.ui.FooterActions;
import legend.game.combat.ui.FooterActionsHud;
import legend.game.combat.ui.UiBox;
import legend.game.i18n.I18n;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.events.battle.MonsterStatsEvent;
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
import java.util.Set;
import java.util.stream.Collectors;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.RENDERER;
import static legend.game.SItem.FUN_801034cc;
import static legend.game.SItem.allocateUiElement;
import static legend.game.SItem.renderItemIcon;
import static legend.game.Scus94491BpeSegment.startFadeEffect;
import static legend.game.Scus94491BpeSegment_8002.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.combat.Monsters.enemyRewards_80112868;
import static legend.game.combat.Monsters.monsterNames_80112068;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_CONFIRM;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DELETE;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_END;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_HOME;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_LEFT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_PAGE_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_PAGE_UP;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_RIGHT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_SORT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_UP;

public class BestiaryScreen extends MenuScreen {

  private static class BestiaryEntry {
    public int entryNumber;
    public int charId;
    public String location;
    public MonsterStatsEvent stats;
    public String lore;
    public int kill;
    public int rank;
    public String name;
    public int maxKill;
    public float[] elementRGB;
    public List<BestiaryEntry> subEntries;
    public boolean isSubEntry;

    public BestiaryEntry(final BestiaryScreen bestiary, final int charId, final int subEntryParentId, final int maxKill, @Nullable final String name, final String map, final String region, final String lore) {
      this.charId = charId;
      this.stats = new MonsterStatsEvent(charId);
      this.lore = devMode ? lore : "";
      this.name = name == null ? monsterNames_80112068[this.charId] : name;
      this.kill = Statistics.getMonsterKill(this.charId);
      this.maxKill = maxKill;

      final int[] elementRGB = BestiaryScreen.getElementBackgroundRGB(this.stats.elementFlag.flag);
      this.elementRGB = new float[] { elementRGB[0] / 255f, elementRGB[1] / 255f, elementRGB[2] / 255f, elementRGB[3] / 100f * 0.8f, elementRGB[4] / 255f, elementRGB[5] / 255f, elementRGB[6] / 255f, elementRGB[7] / 100f * 1f};

      if(subEntryParentId > -1) {
        this.isSubEntry = true;
        final BestiaryEntry parentEntry = bestiary.getEntryByCharId(subEntryParentId);
        if(parentEntry != null) {
          if(parentEntry.subEntries == null) {
            parentEntry.subEntries = new ArrayList<>();
          }
          parentEntry.subEntries.add(this);
          this.entryNumber = parentEntry.entryNumber;
          this.location = parentEntry.location;
        }
      } else {
        this.entryNumber = bestiary.bestiaryEntries.size() + 1;
        this.location = map + (region.isEmpty() ? "" : " - " + region);
      }

      if(this.kill >= RANK_3 || (this.maxKill > -1 && this.kill >= this.maxKill) || (this.maxKill == -2 && this.kill == -1)) {
        this.rank = 3;
      } else if(this.kill >= RANK_2) {
        this.rank = 2;
      } else if(this.kill >= 1 || this.isSubEntry) {
        this.rank = 1;
      } else if (this.kill == -1) {
        this.rank = -1;
      } else {
        this.rank = 0;
      }

      if(devMode) {
        this.rank = 3;
      }
    }

    public boolean isComplete() {
      return this.rank >= 3;
    }
  }

  private static final boolean devMode = false;
  private static final int LIST_ITEM_COUNT = 24;
  private static final int SUB_ENTRY_ARROW_TICK_LENGTH = 92;
  private static final int RANK_2 = 3;
  private static final int RANK_3 = 5;
  private static final String QUESTION_MARK_5 = "?????";
  private static final String QUESTION_MARK_3 = "???";
  private static final String LORE_DEFAULT = "Lore coming one day to a QoL mod near you...";
  private static int lastEntryIndex;
  private static int lastSort;

  private int loadingStage;
  private final Runnable unload;
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
  private final NumberFormat nf = new DecimalFormat("000");
  private final String battleDifficulty = I18n.translate("lod_core.config." + CoreMod.BATTLE_DIFFICULTY.getId().entryId() + '.' + CONFIG.getConfig(CoreMod.BATTLE_DIFFICULTY.get()).name());

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
  private final FontOptions sortFont;
  private final FontOptions gemFont;
  private final FontOptions difficultyFont;

  private int subEntryArrowTick;
  private float currentBoxOffsetX;
  private int listFirstVisibleItem;
  private int currentSort;
  public int entryIndex;
  public int subEntryIndex;
  public boolean isListVisible;

  public BestiaryScreen(final Runnable unload) {
    this.unload = unload;

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
    this.sortFont = new FontOptions().colour(TextColour.YELLOW).shadowColour(TextColour.DARKER_GREY).size(0.4f).horizontalAlign(HorizontalAlign.CENTRE);
    this.gemFont = new FontOptions().colour(TextColour.GOLD).shadowColour(TextColour.DARKER_GREY).size(0.5f).horizontalAlign(HorizontalAlign.CENTRE);
    this.difficultyFont = new FontOptions().colour(TextColour.LIGHTER_GREY).shadowColour(TextColour.DARKER_GREY).size(0.4f).horizontalAlign(HorizontalAlign.LEFT);

    this.textures = new Texture[] {
      Texture.png(Path.of("gfx", "ui", "archive_screen", "bestiary", "bestiary_graphics.png")), //0
      Texture.png(Path.of("gfx", "ui", "action_attack.png")), //1
      Texture.png(Path.of("gfx", "ui", "action_guard.png")),  //2
      Texture.png(Path.of("gfx", "ui", "archive_screen", "bestiary", "black.png")),     //3
      Texture.png(Path.of("gfx", "ui", "archive_screen", "bestiary", "highlight.png")), //4
      Texture.png(Path.of("gfx", "ui", "arrow_blue_up.png")),    //5
      Texture.png(Path.of("gfx", "ui", "arrow_blue_down.png")),  //6
      Texture.png(Path.of("gfx", "ui", "archive_screen", "bestiary", "list_underline.png")), //7
      Texture.png(Path.of("gfx", "ui", "archive_screen", "bestiary", "white.png")), //8
      Texture.png(Path.of("gfx", "ui", "archive_screen", "bestiary", "bestiary_graphics_frames.png")), //9
      Texture.png(Path.of("gfx", "ui", "archive_screen", "bestiary", "rank_gem.png")), //10
      Texture.png(Path.of("gfx", "ui", "arrow_up.png")),   //11
      Texture.png(Path.of("gfx", "ui", "arrow_down.png")), //12
    };

    this.loadEntries();
    this.setBestiaryStatus();

    this.entryIndex = 0;
    this.subEntryIndex = 0;
    this.currentSort = lastSort;

    this.sortList(false);

    if(lastEntryIndex > -1 && lastEntryIndex < this.bestiaryEntries.size()) {
      this.jump(lastEntryIndex, true);
    }

    this.loadCurrentEntry();
  }

  private void loadEntries() {
    this.bestiaryEntries = new ArrayList<>();
    this.addEntry(257, -1, 4, null, "Seles", "Southern Serdio", "A rank-and-file knight in the service of Sandora.\nUses a combination of ranged knives and sword attacks.");
    this.addEntry(256, -1, 1, null, "Seles", "Southern Serdio", "Commander lingering near Seles after it was raided by Sandora.\nHas a basic attack, combo slash, and items at their disposal.");
    this.addEntry(21, -1, -1, null, "Forest", "Southern Serdio", "A cocky bird. Will slash with talons and crow at its enemies.");
    this.addEntry(24, -1, -1, null, "Forest", "Southern Serdio", "A crazed rodent in the Forest. Will often bite.\nAlso has a strange \"Chisel\" attack.");
    this.addEntry(8, -1, -1, null, "Forest", "Southern Serdio", "Strong fighter in the Prairie.\nSeems weak, but does very high damage\nwhen at low health.");
    this.addEntry(38, -1, -1, null, "Forest", "Southern Serdio", "The most durable creature in the Forest. High physical defense.\nWill snap its branches to attack, as well as Pellet.");
    this.addEntry(134, -1, -1, null, "Hellena Prison", "Southern Serdio", "The standard-issue guards of Hellena Prison. They use a long mace in\ncombat, and also carry magical attack items. Their job is to patrol\nthe prison and keep it secure, ensuring no-one escapes.");
    this.addEntry(261, -1, 1, null, "Hellena Prison", "Southern Serdio", "Age: 48\nHeight: 190 cm / 6'3\"\n\nHated by his own country, Fruegel is relegated to lording\nover Hellena Prison. A vain pleasure-seeker adorned with decorations. \nHis pets Guftas and Rodriguez are his only friends.\n\nCombat: club bash, body slam, boulder throw, call allies.");
    this.addEntry(259, 261, 1, null, "Hellena Prison", "Southern Serdio", "The standard-issue guards of Hellena Prison. They use a long mace in\ncombat, and also carry magical attack items. Their job is to patrol\nthe prison and keep it secure, ensuring no-one escapes.");
    this.addEntry(260, 261, 1, null, "Hellena Prison", "Southern Serdio", "The Senior Warden is stronger and more deadly. They wield\ndouble-edged spears and carry magic attack items. \n\nThe Senior Warden also has access to Power Up. This makes them\nharder to defeat, but it can also make their magical attack deadly.");
    this.addEntry(59, -1, -1, null, "Prairie", "Southern Serdio", "bzzzzzt!");
    this.addEntry(0, -1, -1, null, "Prairie", "Southern Serdio", "A strong creature,featuring high physical stats\nand Power Up. Weaker to magic.");
    this.addEntry(45, -1, -1, null, "Prairie", "Southern Serdio", "");
    this.addEntry(27, -1, -1, null, "Prairie", "Southern Serdio", "Notable for its long proboscis and large ears. it's mostly harmless.\nHowever, when its health is low it will suck blood to restore itself.");
    this.addEntry(13, -1, -1, null, "Limestone Cave", "Northern Serdio", "");
    this.addEntry(94, -1, -1, null, "Limestone Cave", "Northern Serdio", "");
    this.addEntry(46, -1, -1, null, "Limestone Cave", "Northern Serdio", "A common bat residing in the Limestone Cave. Although very weak,\nits special ability can confuse people into attacking themselves\nor their friends.");
    this.addEntry(104, -1, -1, null, "Limestone Cave", "Northern Serdio", "A living ooze, the Slime's standard attack can render enemies unable to\nattack in return. Its special ability deals increased damage.");
    //this.addEntry(148, -1, -1, null, "Dragon's Nest", "Southern Serdio", "Unknown variant - pending investigation");
    this.addEntry(64, -1, -1, null, "Limestone Cave", "Northern Serdio", "The Ugly Balloon is a creature of the Limestone Cave. It can fly at high\nspeeds. Though it has low defense and attack, it can poison its enemies\nfor high damage over time.");
    this.addEntry(332, -1, 1, null, "Limestone Cave", "Northern Serdio", "A large snake-like creature, Urobolus is the guardian of Limestone Cave.\nIt has strong physical attacks and can inflict poison frequently. \nPeriodically it will retreat to a high place, avoiding melee attacks.");
    this.addEntry(138, -1, 1, null, "Endiness", "Limestone Cave <-> Bale", "A rare bird seen only along the road between Limestone Cave and\nBale. \n\nLike all Rare creatures, it has a high chance of running away, but\ndrops great rewards if defeated.");
    this.addEntry(130, -1, 1, null, "Hoax", "Northern Serdio", "");
    this.addEntry(258, -1, 1, null, "Hoax", "Northern Serdio", "The first known human who can use innate magic. However, the Elite\nwas not born with it. Researchers at the black castle figured out a way\nto infuse a human with magical powers - possibly with the help\nof Emperor Diaz.\n\nThe Elite has a few physical attacks, but its signature moves are magical.\nThe first is a green flame that deals moderate AoE damage. \nThe second summons apparitions which take no damage.");
    this.addEntry(273, 258, 1, "Sandora Elite Clone", "Black Castle", "Southern Serdio", "Pending investigation");
    this.addEntry(265, -1, 1, null, "Hoax", "Northern Serdio", "Age: \nHeight: 250 cm / \n\nThe last of his species, Kongol is a towering giant with unmatched\nphysical prowess. He wields a large axe, but can fight without it.\n\nAs a child his hometown was raided by bandits. At the last moment he\nwas saved by Doel, who then raised him.");
    this.addEntry(68, -1, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(22, -1, -1, null, "Marshland", "Northern Serdio", "An aquatic creature found in the Marshland. Although the basic attack\ndeals little damage, Mermen also have Water magic that is lethal to\nenemies with low defense or a Fire attribute.");
    this.addEntry(28, -1, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(4, -1, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(131, -1, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(132, -1, -1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(135, -1, 1, null, "Marshland", "Northern Serdio", "");
    this.addEntry(33, -1, -1, null, "Villude Volcano", "Southern Serdio", "");
    this.addEntry(150, -1, -1, null, "Villude Volcano", "Southern Serdio", "");
    this.addEntry(74, -1, -1, null, "Villude Volcano", "Southern Serdio", "Pending investigation");
    this.addEntry(157, -1, -1, null, "Villude Volcano", "Southern Serdio", "Pending investigation");
    this.addEntry(100, -1, -1, null, "Villude Volcano", "Southern Serdio", "Pending investigation");
    this.addEntry(146, -1, -1, null, "Villude Volcano", "Southern Serdio", "Pending investigation");
    this.addEntry(92, -1, -1, null, "Villude Volcano", "Southern Serdio", "A large creature with its back constantly ablaze. The Salamander's\nbasic attack can stun enemies, which becomes dangerous when it\nattacks the same target multiple times.");
    this.addEntry(308, -1, 1, "Wounded Virage", "Villude Volcano", "Southern Serdio", "A relic of the Dragon Campaign, this Virage was seemingly petrified\nwithin the Volcano for millenia. It activates to attack Dart's party.\n\nThis Virage is not at full strength. It is clearly damaged:\nmissing one arm, both legs, and more. Despite this, it is still deadly.\nThis Virage can inflict an assortment of status ailments, and uses a\nstaggeringly powerful beam laser.");
    this.addEntry(333, -1, 1, null, "Villude Volcano", "Southern Serdio", "An elemental creature that patrols the volcano. Fire Bird may pursue\npassersby who attempt to traverse the molten crags. \n\nMost of its abilities have an area of effect, and are of the Fire element.\n\nAlso known as Piton.");
    this.addEntry(334, 333, 1, null, "Villude Volcano", "Southern Serdio", "An elemental creature that patrols the volcano. Fire Bird may pursue\npassersby who attempt to traverse the molten crags. \n\nMost of its abilities have an area of effect, and are of the Fire element.\n\nAlso known as Piton.");
    this.addEntry(19, -1, -1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(88, -1, -1, "Man-Eating Bud", "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(90, -1, -1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(35, -1, -1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(115, -1, -1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(275, -1, 1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(287, -1, 1, null, "Dragon's Nest", "Southern Serdio", "");
    this.addEntry(87, -1, 1, null, "Endiness", "Dragon's Nest <-> Lohan", "");
    this.addEntry(302, -1, 1, null, "Lohan", "Southern Serdio", "");
    this.addEntry(303, -1, 1, null, "Lohan", "Southern Serdio", "");
    this.addEntry(304, -1, 1, null, "Lohan", "Southern Serdio", "");
    this.addEntry(305, -1, 1, null, "Lohan", "Southern Serdio", "");
    this.addEntry(269, -1, -2, null, "Lohan", "Southern Serdio", "");
    this.addEntry(66, -1, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(58, -1, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(55, -1, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(107, -1, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(15, -1, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(325, -1, 1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(326, 325, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(327, 325, -1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(288, -1, 1, null, "Shirley's Shrine", "Southern Serdio", "");
    this.addEntry(105, -1, 4, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(129, -1, -1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(133, -1, -1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(329, -1, 1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(262, -1, 1, "Fruegel II", "Hellena Prison", "Southern Serdio", "");
    this.addEntry(264, 262, 1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(263, 262, 1, null, "Hellena Prison", "Southern Serdio", "");
    this.addEntry(42, -1, 1, null, "Endiness / Moon", "Road Near Kazas", "");
    this.addEntry(17, -1, -1, null, "Black Castle", "Southern Serdio", "");
    this.addEntry(103, -1, -1, null, "Black Castle", "Southern Serdio", "");
    this.addEntry(102, -1, 1, null, "Black Castle", "Southern Serdio", "");
    this.addEntry(266, -1, 1, "Kongol II", "Black Castle", "Southern Serdio", "");
    this.addEntry(267, -1, 1, null, "Black Castle", "Southern Serdio", "");
    this.addEntry(268, -1, 1, "Doel (Dragoon)", "Black Castle", "Southern Serdio", "");
    this.addEntry(99, -1, -1, null, "Barrens", "Tiberoa", "");
    this.addEntry(26, -1, -1, null, "Barrens", "Tiberoa", "");
    this.addEntry(69, -1, -1, null, "Barrens", "Tiberoa", "");
    this.addEntry(1, -1, -1, null, "Barrens", "Tiberoa", "Decent physical attacker with good defense.\nWeaker to magic.");
    this.addEntry(108, -1, -1, null, "Barrens", "Tiberoa", "");
    this.addEntry(299, -1, 1, null, "Barrens", "Tiberoa", "");
    this.addEntry(274, 299, -1, null, "Barrens", "Tiberoa", "");
    this.addEntry(136, -1, 1, null, "Endiness", "Barrens <-> Home of Gigantos", "");
    this.addEntry(16, -1, -1, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(52, -1, -1, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(106, -1, -1, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(29, -1, -1, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(14, -1, -1, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    this.addEntry(311, -1, 1, "Virage", "Valley of Corrupted Gravity", "Tiberoa", "A relic of the Dragon Campaign, this Virage laid dormant in the Valley\nfollowing a magic-intensive battle between Winglies and Humans.\n\nUnlike the one found in Serdio, this Virage is at full strength. It has all\nlimbs intact, and has many more abilities at its disposal. It can stomp\nwith its legs, conjure an ethereal energy wave, and can kill someone\nby smashing them into the central green energy cluster on its head.");
    this.addEntry(79, -1, -1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(83, -1, -1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(82, -1, -1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(109, -1, -1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(301, -1, 1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(300, -1, 1, null, "Giganto Home", "Tiberoa", "");
    this.addEntry(293, -1, 1, null, "Fletz", "Tiberoa", "");
    this.addEntry(18, -1, -1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(85, -1, -1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(101, -1, -1, null, "Phantom Ship", "Illisa Bay", "Pending investigation");
    this.addEntry(152, -1, -1, null, "Phantom Ship", "Illisa Bay", "Pending investigation");
    this.addEntry(86, -1, -1, null, "Phantom Ship", "Illisa Bay", "Pending investigation");
    this.addEntry(154, -1, -1, null, "Phantom Ship", "Illisa Bay", "Pending investigation");
    this.addEntry(155, -1, -1, null, "Phantom Ship", "Illisa Bay", "Pending investigation");
    this.addEntry(70, -1, 4, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(340, -1, 1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(341, -1, 1, null, "Phantom Ship", "Illisa Bay", "");
    this.addEntry(12, -1, -1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(23, -1, -1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(91, -1, -1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(76, -1, -1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(110, -1, -1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(294, -1, 1, "Lenus (Dragoon)", "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(279, -1, 1, null, "Undersea Cavern", "Illisa Bay", "");
    this.addEntry(124, -1, 1, null, "Endiness", "Undersea Cavern <-> Fueno", "");
    this.addEntry(137, -1, 1, null, "Endiness", "Furni <-> Deningrad", "");
    this.addEntry(95, -1, -1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(53, -1, -1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(73, -1, -1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(117, -1, -1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(51, -1, -1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(343, -1, 1, null, "Evergreen Forest", "Mille Seseau", "");
    this.addEntry(43, -1, -1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(30, -1, -1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(44, -1, -1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(111, -1, -1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(2, -1, -1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(316, -1, 1, "Wounded Super Virage", "Kadessa", "Mille Seseau", "A relic of the Dragon Campaign, this is a more powerful type of Virage.\nIt was nearly defeated by Kanzas during the final battle of the Dragon\nCampaign, at the Wingly capital Kadessa.\n\nThis Virage barely survived, but is still formiddable. It has a variety of\nattacks, and will self-destruct if not defeated quickly.");
    this.addEntry(335, -1, 1, null, "Kadessa", "Mille Seseau", "");
    this.addEntry(96, -1, -1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(6, -1, -1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(113, -1, -1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(112, -1, -1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(37, -1, -1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(283, -1, 1, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    this.addEntry(32, -1, -1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(114, -1, -1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(5, -1, -1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(48, -1, -1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(67, -1, -1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(346, -1, 1, null, "Kashua Glacier", "Mille Seseau", "");
    this.addEntry(270, -1, 1, "Lloyd (Wingly Armor)", "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(11, -1, -1, null, "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(25, -1, -1, null, "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(56, -1, -1, null, "Flanvel Tower", "Mille Seseau", "Pending investigation");
    this.addEntry(144, -1, -1, null, "Flanvel Tower", "Mille Seseau", "Pending investigation");
    this.addEntry(49, -1, -1, null, "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(344, -1, 1, null, "Flanvel Tower", "Mille Seseau", "");
    this.addEntry(31, -1, -1, null, "Snowfield", "Gloriano", "");
    this.addEntry(116, -1, -1, null, "Snowfield", "Gloriano", "");
    this.addEntry(20, -1, -1, null, "Snowfield", "Gloriano", "");
    this.addEntry(57, -1, -1, null, "Snowfield", "Gloriano", "");
    this.addEntry(50, -1, -1, null, "Snowfield", "Gloriano", "");
    this.addEntry(349, -1, 1, "Polter Armor", "Snowfield", "Gloriano", "");
    this.addEntry(65, -1, -1, null, "Vellweb", "Gloriano", "");
    this.addEntry(47, -1, -1, null, "Vellweb", "Gloriano", "");
    this.addEntry(39, -1, -1, null, "Vellweb", "Gloriano", "");
    this.addEntry(41, -1, -1, null, "Vellweb", "Gloriano", "");
    this.addEntry(63, -1, -1, null, "Vellweb", "Gloriano", "");
    this.addEntry(89, -1, -1, null, "Death Frontier", "Death Frontier", "");
    this.addEntry(36, -1, -1, null, "Death Frontier", "Death Frontier", "");
    this.addEntry(3, -1, -1, null, "Death Frontier", "Death Frontier", "Physical attacker often accompanied by other\ncreatures. Has a special sand attack.");
    this.addEntry(119, -1, -1, null, "Death Frontier", "Death Frontier", "");
    this.addEntry(61, -1, -1, null, "Death Frontier", "Death Frontier", "");
    this.addEntry(139, -1, 1, null, "Endiness", "Death Frontier <-> Ulara", "");
    this.addEntry(54, -1, 1, null, "Endiness", "Fletz <-> Rouge", "");
    this.addEntry(121, -1, -1, null, "Aglis", "Broken Islands", "");
    this.addEntry(60, -1, -1, null, "Aglis", "Broken Islands", "");
    this.addEntry(9, -1, -1, null, "Aglis", "Broken Islands", "");
    this.addEntry(75, -1, -1, null, "Aglis", "Broken Islands", "");
    this.addEntry(72, -1, -1, null, "Aglis", "Broken Islands", "");
    this.addEntry(365, -1, 1, null, "Aglis", "Broken Islands", "");
    this.addEntry(366, -1, 1, null, "Aglis", "Broken Islands", "");
    this.addEntry(118, -1, -1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(40, -1, -1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(97, -1, -1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(80, -1, -1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(122, -1, -1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(362, -1, 1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(361, -1, 1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(360, -1, 1, null, "Zenebatos", "Gloriano", "");
    this.addEntry(297, -1, 1, null, "Vellweb", "Gloriano", "");
    this.addEntry(295, -1, 1, null, "Vellweb", "Gloriano", "");
    this.addEntry(298, -1, 1, null, "Vellweb", "Gloriano", "");
    this.addEntry(296, -1, 1, null, "Vellweb", "Gloriano", "");
    this.addEntry(125, -1, -1, null, "Mayfil", "Gloriano", "");
    this.addEntry(10, -1, -1, null, "Mayfil", "Gloriano", "");
    this.addEntry(78, -1, -1, null, "Mayfil", "Gloriano", "");
    this.addEntry(84, -1, -1, null, "Mayfil", "Gloriano", "");
    this.addEntry(354, -1, 1, "Feyrbrand Spirit", "Mayfil", "Gloriano", "");
    this.addEntry(353, -1, 1, "Regole Spirit", "Mayfil", "Gloriano", "");
    this.addEntry(352, -1, 1, "Divine Dragon Spirit", "Mayfil", "Gloriano", "");
    this.addEntry(364, -1, 1, null, "Mayfil", "Gloriano", "");
    this.addEntry(363, -1, 1, null, "Mayfil", "Gloriano", "");
    this.addEntry(98, -1, -1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(7, -1, -1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(126, -1, -1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(77, -1, -1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(62, -1, -1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(368, -1, 1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(369, -1, 1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(370, -1, 1, null, "Divine Tree", "Gloriano", "");
    this.addEntry(123, -1, -1, null, "Moon", "Gloriano", "");
    this.addEntry(34, -1, -1, null, "Moon", "Gloriano", "");
    this.addEntry(81, -1, -1, null, "Moon", "Gloriano", "");
    this.addEntry(127, -1, -1, null, "Moon", "Gloriano", "");
    this.addEntry(128, -1, -1, null, "Moon", "Gloriano", "");
    this.addEntry(120, -1, -1, null, "Moon", "Gloriano", "");
    this.addEntry(71, -1, -1, null, "Moon", "Gloriano", "");
    this.addEntry(93, -1, -1, null, "Moon", "Gloriano", "");
    this.addEntry(371, -1, 1, null, "Moon", "Gloriano", "");
    this.addEntry(373, -1, 1, null, "Moon", "Gloriano", "");
    this.addEntry(375, -1, 1, null, "Moon", "Gloriano", "");
    this.addEntry(378, -1, 1, null, "Moon", "Gloriano", "");
    this.addEntry(381, -1, 1, null, "Moon", "Gloriano", "");
    this.addEntry(382, -1, 1, null, "Moon", "Gloriano", "");
    this.addEntry(320, -1, 1, "Super Virage", "Moon", "Gloriano", "A relic of the Dragon Campaign, this Super Virage is the last natural line\nof defense for the primary Virage Embryo. Unlike the one found in the\nruins of Kadessa, this one won't self-detonate, and must be defeated.");
    this.addEntry(387, -1, 1, null, "Moon", "Gloriano", "Age: 28\nHeight: 181 cm / 5'9\"\n\nZieg is a legendary warrior from the time of the Dragon Campaign.\nHe normally would have perished long ago, but in the final battle he\nwas petrified by Melbu Frahma for over 11,000 years. \n\nWhen the spell wore off, he tried to start a new life. However, as his\nhometown was attacked, he tried to save it. When he activated\nhis Dragoon Spirit, Melbu's spirit came out instead, possessing him. \n\nZieg was not himself for 18 years. Melbu would use his body to create\na new scheme to destroy the world, and remake it in his image.\n\nOn the Moon, Melbu (as ZIeg) swipes Dart's Dargoon Spirit and transforms.\nHe has his own style of Dart's Dragoon abilities, proving very powerful.\n\nAfter being defeated, Zieg is finally himself again, and gets to speak\nwith his fiancé Rose one last time.");
    //this.addEntry(388, -1, 1, null, "Moon", "Gloriano", "A Wingly dictator, Melbu Frahma was obsessed with power. He sees\nhimself as superior, both during the Dragon Campaign and now. \nHis magic was so strong that he needed a restraining device to keep\nit under control. Under his rule, many species were subjugated.\n\nAlthough defeated in the final battle of the Dragon Campaign, he would\npreserve himself within Zieg's Dragoon Spirit, biding his time.\n\nOnce ZIeg unwittingly released him, Melbu began a new plot to\nremake the world in his image. Eventually, Melbu would reach the Moon\nand stop the");
  }

  private void addEntry(final int charId, final int subEntryParentId, final int killCount, @Nullable final String name, final String map, final String region, final String lore) {
    final BestiaryEntry newEntry = new BestiaryEntry(this, charId, subEntryParentId, killCount, name, map, region, lore);
    if(!newEntry.isSubEntry) {
      this.bestiaryEntries.add(newEntry);
    }
  }

  public void loadCurrentEntry() {
    final BestiaryEntry monster = this.bestiaryEntries.get(this.entryIndex);
    if(this.subEntryIndex > 0) {
      this.monster = monster.subEntries.get(this.subEntryIndex - 1);
    } else {
      this.monster = monster;
      lastEntryIndex = this.entryIndex;
    }

    this.headerTexture = Texture.png(Path.of("gfx", "ui", "archive_screen", "bestiary", "header_element_" + this.getElement(this.monster.stats.elementFlag.flag).getRegistryId().entryId() + ".png"));

    try {
      this.modelTexture = Texture.png(Path.of("gfx", "models", this.monster.charId + ".png"));
    } catch(final Exception e) {
      this.modelTexture = Texture.png(Path.of("gfx", "models", "-1.png"));
    }
  }

  public void renderAll() {
    this.renderGraphics();
    this.renderModel();
    this.renderEnemyName();
    this.renderEnemyStats();
    this.renderRewards();
    this.renderLore();
    this.renderSubEntryArrows();

    if(this.isListVisible) {
      this.renderList();
    }

    FUN_801034cc(this.entryIndex, this.bestiaryEntries.size(), -10, this.isListVisible); // Left/right arrows
  }

  private void renderGraphics() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    this.m.translation(xOffset, 0, 128);
    this.m.scale(368f, 240f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[0]); //Background+Frames

    if(this.monster.rank > 0) {
      this.m.translation(xOffset, 0, 128);
      this.m.scale(368f, 240f, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[9]) //Frames
        .colour(this.monster.elementRGB[0], this.monster.elementRGB[1], this.monster.elementRGB[2])
        .alpha(this.monster.elementRGB[3])
        .translucency(Translucency.HALF_B_PLUS_HALF_F);
    }

    renderText("Difficulty: " + this.battleDifficulty, 0.5f, 235f, this.difficultyFont, 127);
  }

  private void renderModel() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    this.m.translation(-22f + xOffset, 27.8f, 126);
    this.m.scale(210f, 87.3f, 1);

    if(this.monster.rank > 0) {
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
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    if(this.monster.rank > 0) {
      this.m.translation(14.5f + xOffset, 8f, 127);
      this.m.scale(338.6f, 19.9f, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.headerTexture); //Header
    }

    if(this.monster.rank > 0 || this.monster.rank == -1) {
      if(devMode) {
        renderText(this.monster.name + " [" + this.monster.charId + ']', 184, 10.5f, this.headerFont, 126);
      } else {
        renderText(this.monster.name, 184, 10.5f, this.headerFont, 126);
      }
    } else {
      renderText(QUESTION_MARK_5, 184, 10.5f, this.headerFont, 126);
    }

    if(this.monster.rank > 0 || this.monster.rank == -1) {
      renderText(this.monster.location, 31, 206.5f, this.locationFont, 127);
    } else {
      renderText(QUESTION_MARK_5, 31, 206.5f, this.locationFont, 126);
    }

//    int u = (int)(tickCount_800bb0fc / (3.0f / vsyncMode_8007a3b8)) & 0x7;
//    this.modelAndAnimData_800c66a8.mapArrow.render(u, 2, 19.8f, 200, 126f);

//    u = (int)(tickCount_800bb0fc / 5 / (3.0f / vsyncMode_8007a3b8) % 3);
//    this.modelAndAnimData_800c66a8.coolonPlaceMarker.render(u, 2, 21f, 206f, 127f);

    renderText(this.nf.format(this.monster.entryNumber), 15f, 28.5f, this.headerNumberFont, 125);
    renderText("Defeated: " + Math.max(0, this.monster.maxKill > -1 ? Math.min(this.monster.maxKill, this.monster.kill) : this.monster.kill), 23, 123, this.statsFont, 127);

    if(this.monster.rank > 0 || this.monster.rank == -1) {
      float x = 0;
      for(int i = 2; i >= 0; i--) {
        this.m.translation(169.5f + x + xOffset, 121f, 127);
        this.m.scale(10, 10, 1);

        int rankValue = switch(i) {
          case 1 -> RANK_2;
          case 2 -> RANK_3;
          default -> 1;
        };

        int lastRankValue = switch(i) {
          case 1 -> 1;
          case 2 -> RANK_2;
          default -> 0;
        };

        if(this.monster.maxKill == -2 || this.monster.isSubEntry) {
          rankValue = i == 2 ? 1 : 0;
          lastRankValue = 0;
        }

        final int value = this.monster.maxKill > 0 ? this.monster.maxKill : rankValue;

        if(value > lastRankValue) {
          final float colorMultiplier = this.monster.rank > i || this.monster.isSubEntry ? 1 : 0.35f;

          RENDERER
            .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
            .texture(this.textures[10]) //Rank Gem
            .colour(this.monster.elementRGB[4] * colorMultiplier, this.monster.elementRGB[5] * colorMultiplier, this.monster.elementRGB[6] * colorMultiplier)
            .alpha(this.monster.elementRGB[7])
            .translucency(Translucency.HALF_B_PLUS_HALF_F);

          if(this.monster.rank <= i && !this.monster.isSubEntry) {
            renderText(String.valueOf(Math.min(value, rankValue)), 174.25f + x, 124f, this.gemFont, 126);
          }

          x -= 11.5f;
        }
      }
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

    if((this.monster.stats.specialEffectFlag & 0x4) == 0) { //Check for no Magical Resist
      for (final Element e : this.monster.stats.elementalImmunityFlag) {
        y += 11.45f;
        this.renderElementMultipliers(e.flag, x, y, xOffset, 100, false);
      }

      if(this.monster.stats.elementFlag.flag != 8 && !this.monster.stats.elementalImmunityFlag.contains(this.monster.stats.elementFlag)) {
        y += 11.45f;
        this.renderElementMultipliers(this.monster.stats.elementFlag.flag, x, y, xOffset, 50, false);
      }

      final int counterElement = this.getCounterElement(this.monster.stats.elementFlag.flag);
      if(counterElement != 0) {
        y += 11.45f;
        this.renderElementMultipliers(counterElement, x, y, xOffset, 50, true);
      }
    }

    if((this.monster.stats.specialEffectFlag & 0x8) != 0) { //Physical Resist
      y += 11.45f;
      this.renderStatus("Physical", x, y, xOffset, 100, false);
    }

    if((this.monster.stats.specialEffectFlag & 0x4) != 0) { //Magical Resist
      y += 11.45f;
      this.renderStatus("Magical", x, y, xOffset, 100, false);
    }

    if((this.monster.stats.statusResistFlag & 0x4) != 0) { //Confusion Resist
      y += 11.45f;
      this.renderStatus("Confusion", x, y, xOffset, 999, false);
    }

    if((this.monster.stats.statusResistFlag & 0x8) != 0) { //Fear Resist
      y += 11.45f;
      this.renderStatus("Fear", x, y, xOffset, 999, false);
    }

    if((this.monster.stats.statusResistFlag & 0x10) != 0) { //Stun Resist
      y += 11.45f;
      this.renderStatus("Stun", x, y, xOffset, 999, false);
    }

    if((this.monster.stats.statusResistFlag & 0x80) != 0) { //Poison Resist
      y += 11.45f;
      this.renderStatus("Poison", x, y, xOffset, 999, false);
    }
  }

  private void renderElementMultipliers(final int elementFlag, final float x, final float y, final float xOffset, final int defPercent, final boolean counter) {
    this.renderStatus(I18n.translate(this.getElement(elementFlag).getTranslationKey()), x, y, xOffset, defPercent, counter);
  }

  private void renderStatus(final String status, final float x, final float y, final float xOffset, final int defPercent, final boolean counter) {
    final String guardSign = counter ? "-" : "+";

    renderText(this.monster.rank >= 2 ? status : QUESTION_MARK_5, x, y, this.statsFont, 127);

    if(this.monster.rank >= 2) {
      this.m.translation(x + 50.5f + xOffset, y - 1f, 125f);
      this.m.scale(8, 8, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[2]); //Guard

      renderText(defPercent != 999 ? guardSign + defPercent + '%' : "Resist", x + 59f, y + 2f, this.resistNumberFont, 127);
    }
  }

  private void renderRewards() {
    final EnemyRewards08 rewards = enemyRewards_80112868[this.monster.charId];
    final float x = 200f;
    float y = 166f;

    renderText("Battle Rewards", x + 74, 150f, this.titleFont, 127);

    if(rewards.xp_00 > 0) {
      renderText(this.monster.isComplete() ? "Exp." : QUESTION_MARK_3, x, y, this.rewardTitleFont, 127);
      renderText(this.monster.isComplete() ? String.valueOf(rewards.xp_00) : QUESTION_MARK_5, x + 28f, y, this.rewardTitleFont, 127);
      y += 13.5f;
    }

    if(rewards.gold_02 > 0) {
      renderText(this.monster.isComplete() ? "Gold" : QUESTION_MARK_3, x, y, this.rewardTitleFont, 127);
      renderText(this.monster.isComplete() ? String.valueOf(rewards.gold_02) : QUESTION_MARK_5, x + 28f, y, this.rewardTitleFont, 127);
      y += 13.5f;
    }

    if(rewards.itemDrop_05 != null) {
      renderText(this.monster.isComplete() ? I18n.translate(rewards.itemDrop_05.get().getNameTranslationKey()) + " (" + rewards.itemChance_04 + "%)" : QUESTION_MARK_5, x + 28f, y, this.rewardTitleFont, 127);

      if(this.monster.isComplete()) {
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

    this.m.translation(xOffset - 10, 0, 125);
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
        renderText(String.valueOf(c), x + charX, y, highlighted ? (entry.isComplete() ? this.listNumberPerfectHighlightFont : this.listNumberHighlightFont) : this.listNumberFont, 123);
        charX += 3.7f;
      }
      renderText(":", x + 16f, y, highlighted ? (entry.isComplete() ? this.listNumberPerfectHighlightFont : this.listNumberHighlightFont) : this.listNumberFont, 123);
      renderText(entry.rank > 0 || entry.rank == -1 ? entry.name : QUESTION_MARK_5, x + 19f, y, highlighted ? (entry.isComplete() ? this.listPerfectHighlightFont : this.listHighlightFont) : (entry.isComplete() ? this.listPerfectFont : this.listFont), 123);

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

  private void renderSubEntryArrows() {
    final BestiaryEntry parentEntry = this.getEntryParent(this.monster);
    if(parentEntry.subEntries != null && parentEntry.rank > 0) {
      final float xOffset = RENDERER.getWidescreenOrthoOffsetX();
      final int ticks = Math.clamp(this.subEntryArrowTick, 0, SUB_ENTRY_ARROW_TICK_LENGTH);
      final float yOffset = 0.49f * (ticks > -1 ? (float)Math.floor((ticks> SUB_ENTRY_ARROW_TICK_LENGTH * 0.5f ? SUB_ENTRY_ARROW_TICK_LENGTH - ticks : ticks) * 0.12f) : 1f);

      if(this.subEntryIndex > 0) {
        this.m.translation(xOffset + 177f, 32f + yOffset, 125);
        this.m.scale(11, 11, 1);

        RENDERER
          .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
          .texture(this.textures[11]); //Up Arrow
      }

      if(this.subEntryIndex < parentEntry.subEntries.size()) {
        this.m.translation(xOffset + 177f, 100f - yOffset, 125);
        this.m.scale(11, 11, 1);

        RENDERER
          .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
          .texture(this.textures[12]); //Down Arrow
      }
    }

    if(++this.subEntryArrowTick > SUB_ENTRY_ARROW_TICK_LENGTH + 20) {
      this.subEntryArrowTick = 0;
    }
  }

  public boolean next(final int steps) {
    boolean b = false;
    for(int i = 0; i < steps; i++) {
      if(this.entryIndex < this.bestiaryEntries.size() - 1) {
        b = true;
        this.entryIndex++;
        this.subEntryIndex = 0;
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
        this.subEntryIndex = 0;
        if(this.listFirstVisibleItem > 0 && this.listFirstVisibleItem + LIST_ITEM_COUNT * 0.12f > this.entryIndex) {
          this.listFirstVisibleItem--;
        }
      } else {
        break;
      }
    }
    return b;
  }

  public boolean jump(final int index, final boolean centerItemInList) {
    if(index != this.entryIndex) {
      this.entryIndex = index;
      this.subEntryIndex = 0;
      this.listFirstVisibleItem = Math.clamp(index, 0, this.bestiaryEntries.size() - LIST_ITEM_COUNT);
      if(centerItemInList) { //Hack to center entry in the list
        this.previous(10);
        this.next(10);
      }
      return true;
    }
    return false;
  }

  public boolean nextSub() {
    final BestiaryEntry parentEntry = this.getEntryParent(this.monster);
    if(parentEntry.subEntries != null && parentEntry.rank > 0 && this.subEntryIndex < parentEntry.subEntries.size()) {
      this.subEntryIndex++;
      return true;
    }
    return false;
  }

  public boolean previousSub() {
    final BestiaryEntry parentEntry = this.getEntryParent(this.monster);
    if(parentEntry.subEntries != null && parentEntry.rank > 0 && this.subEntryIndex > 0) {
      this.subEntryIndex--;
      return true;
    }
    return false;
  }

  private BestiaryEntry getEntryParent(final BestiaryEntry entry) {
    if(entry.isSubEntry) {
      return this.bestiaryEntries.get(this.entryIndex);
    }
    return entry;
  }

  private int getStat(final int statIndex) {
    return switch(statIndex) {
      case 0 -> this.monster.stats.hp;
      case 1 -> this.monster.stats.attack;
      case 2 -> this.monster.stats.defence;
      case 3 -> this.monster.stats.magicAttack;
      case 4 -> this.monster.stats.magicDefence;
      case 5 -> this.monster.stats.speed;
      case 6 -> this.monster.stats.attackAvoid;
      case 7 -> this.monster.stats.magicAvoid;
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
      case 1 -> new int[] { 0, 196, 255, 35, 0, 144, 255, 80 };      //Water
      case 2 -> new int[] { 81, 55, 0, 35, 159, 108, 0, 80 };          //Earth
      case 4 -> new int[] { 0, 30, 255, 40, 40, 30, 227, 80 };        //Dark
      case 8 -> new int[] { 200, 200, 200, 40, 230, 230, 230, 80 };  //Divine
      case 16 -> new int[] { 97, 0, 196, 35, 129, 45, 255, 80 };       //Thunder
      case 32 -> new int[] { 255, 255, 53, 40, 255, 246, 0, 80 };   //Light
      case 64 -> new int[] { 0, 236, 94, 35, 0, 236, 94, 80 };       //Wind
      case 128 -> new int[] { 255, 15, 0, 25, 225, 6, 0, 80 };      //Fire
      default -> new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    };
  }

  private void setBestiaryStatus() {
    int totalAtMaxRank = 0;
    for(final BestiaryEntry r : this.bestiaryEntries) {
      if(r.kill > 0) {
        this.bestiarySeenCount++;
        if(r.isComplete()) {
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
    lastSort = this.currentSort;
    this.sortList(true);
  }

  private void sortList(final boolean jumpToEntry) {
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
        .sorted(Comparator.comparingInt(o -> o.stats.elementFlag.flag))
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

    if(jumpToEntry) {
      this.jump(this.indexOfEntry(this.monster.entryNumber), true);
    }
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

  private BestiaryEntry getEntryByCharId(final int charId) {
    for(final BestiaryEntry entry : this.bestiaryEntries) {
      if(entry.charId == charId) {
        return entry;
      }
    }
    return null;
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
    FooterActionsHud.renderActions(0, FooterActions.BACK, FooterActions.LIST, FooterActions.JUMP, null, null);
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
    if(this.isListVisible && !alt) {
      playMenuSound(2);
      this.cycleSort(-1);
    } else {
      if(this.previous(steps)) {
        playMenuSound(1);
        this.loadCurrentEntry();
      }
    }
  }

  private void menuNavigateRight(final int steps, final boolean alt) {
    if(this.isListVisible && !alt) {
      playMenuSound(2);
      this.cycleSort(1);
    } else {
      if(this.next(steps)) {
        playMenuSound(1);
        this.loadCurrentEntry();
      }
    }
  }

  private void menuNavigateUp() {
    if(this.isListVisible) {
      if(this.previous(1)) {
        playMenuSound(1);
        this.loadCurrentEntry();
      }
    } else {
      if(this.previousSub()) {
        playMenuSound(1);
        this.loadCurrentEntry();
      }
    }
  }

  private void menuNavigateDown() {
      if(this.isListVisible) {
        if(this.next(1)) {
          playMenuSound(1);
          this.loadCurrentEntry();
        }
      } else {
        if(this.nextSub()) {
          playMenuSound(1);
          this.loadCurrentEntry();
        }
      }
  }

  private void goToNextIncomplete() {
    if(this.entryIndex < this.bestiaryEntries.size()) {
      for(int i = this.entryIndex + 1; i < this.bestiaryEntries.size(); i++) {
        if(!this.bestiaryEntries.get(i).isComplete()) {
          if(this.jump(i, true)) {
            playMenuSound(1);
            this.loadCurrentEntry();
            return;
          }
        }
      }
    }
    for(int i = 0; i < this.bestiaryEntries.size(); i++) {
      if(!this.bestiaryEntries.get(i).isComplete()) {
        if(this.jump(i, true)) {
          playMenuSound(1);
          this.loadCurrentEntry();
          return;
        }
      }
    }
    playMenuSound(40);
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
      if(this.isListVisible) {
        playMenuSound(3);
        this.isListVisible = false;
      } else {
        this.menuEscape();
      }
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_CONFIRM.get()) {
      if(this.isListVisible) {
        playMenuSound(2);
        this.isListVisible = !this.isListVisible;
      }
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
      if(this.jump(0, false)) {
        playMenuSound(1);
        this.loadCurrentEntry();
      }
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_END.get()) {
      if(this.jump(this.bestiaryEntries.size() - 1, false)) {
        playMenuSound(1);
        this.loadCurrentEntry();
      }
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_SORT.get()) {
      playMenuSound(2);
      this.isListVisible = !this.isListVisible;
    }

    if(action == INPUT_ACTION_MENU_DELETE.get()) {
      this.goToNextIncomplete();
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
