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
import legend.game.combat.ui.TrackerHud;
import legend.game.combat.ui.UiBox;
import legend.game.i18n.I18n;
import legend.game.modding.coremod.CoreMod;
import legend.game.statistics.Bestiary;
import legend.game.statistics.BestiaryEntry;
import legend.game.types.Translucency;
import org.joml.Matrix4f;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.RENDERER;
import static legend.game.Audio.playMenuSound;
import static legend.game.FullScreenEffects.startFadeEffect;
import static legend.game.Menus.deallocateRenderables;
import static legend.game.SItem.FUN_801034cc;
import static legend.game.SItem.allocateUiElement;
import static legend.game.SItem.renderItemIcon;
import static legend.game.Text.renderText;
import static legend.game.combat.Monsters.enemyRewards_80112868;
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
import static legend.game.statistics.Bestiary.RANK_2;
import static legend.game.statistics.Bestiary.RANK_3;
import static legend.game.statistics.Bestiary.bestiaryEntries;

public class BestiaryScreen extends MenuScreen {
  private static final int LIST_ITEM_COUNT = 24;
  private static final int SUB_ENTRY_ARROW_TICK_LENGTH = 92;

  private final String QUESTION_MARK_5 = "?????";
  private final String QUESTION_MARK_3 = "???";
  private final String LORE_DEFAULT = "Lore coming one day to a QoL mod near you...";
  private static int lastEntryIndex;
  private static int lastSort;

  private int loadingStage;
  private final Runnable unload;
  private final Matrix4f m;
  private final Obj quad;
  private final Texture[] textures;
  private Texture headerTexture;
  private Texture modelTexture;

  private BestiaryEntry monster;
  private boolean bestiaryPerfect;
  private int bestiarySeenCount;
  private UiBox listBox;
  private final NumberFormat nf = new DecimalFormat("000");
  private final String battleDifficulty = I18n.translate("lod_core.config." + CoreMod.BATTLE_DIFFICULTY.getId().entryId() + '.' + CONFIG.getConfig(CoreMod.BATTLE_DIFFICULTY.get()).name());

  private final FontOptions headerFont;
  private final FontOptions headerTrackedFont;
  private final FontOptions headerNumberFont;
  private final FontOptions locationFont;
  private final FontOptions statsFont;
  private final FontOptions loreFont;
  private final FontOptions titleFont;
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
  private final FontOptions trackedFont;
  private final FontOptions trackedHighlightFont;

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

    this.headerFont = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.DARK_GREY).size(1.1f).horizontalAlign(HorizontalAlign.CENTRE);
    this.headerTrackedFont = new FontOptions().colour(TextColour.RED).shadowColour(TextColour.DARK_GREY).size(1.1f).horizontalAlign(HorizontalAlign.CENTRE);
    this.headerNumberFont = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.DARK_GREY).size(0.9f).horizontalAlign(HorizontalAlign.LEFT);
    this.statsFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.65f).horizontalAlign(HorizontalAlign.LEFT);
    this.loreFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
    this.locationFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.55f).horizontalAlign(HorizontalAlign.LEFT);
    this.titleFont = new FontOptions().colour(TextColour.CRUNCHY_TEXT_BROWN).shadowColour(TextColour.CRUNCHY_TEXT_SHADOW_BROWN).size(0.75f).horizontalAlign(HorizontalAlign.RIGHT);
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
    this.trackedFont = new FontOptions().colour(TextColour.RED).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);
    this.trackedHighlightFont = new FontOptions().colour(TextColour.RED).shadowColour(TextColour.DARKER_GREY).size(0.45f).horizontalAlign(HorizontalAlign.LEFT);

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

    Bestiary.loadEntries();
    this.setBestiaryStatus();

    this.entryIndex = 0;
    this.subEntryIndex = 0;
    this.currentSort = lastSort;

    this.sortList(false);

    if(lastEntryIndex > -1 && lastEntryIndex < bestiaryEntries.size()) {
      this.jump(lastEntryIndex, true);
      this.jump(lastEntryIndex, false);
    }

    this.loadCurrentEntry();
  }

  public static void resetCache() {
    lastEntryIndex = 0;
    lastSort = 0;
  }

  public void loadCurrentEntry() {
    final BestiaryEntry monster = bestiaryEntries.get(this.entryIndex);
    if(this.subEntryIndex > 0) {
      this.monster = monster.subEntries.get(this.subEntryIndex - 1);
    } else {
      this.monster = monster;
      lastEntryIndex = this.entryIndex;
    }

    this.headerTexture = Texture.png(Path.of("gfx", "ui", "archive_screen", "bestiary", "header_element_" + this.getElement(this.monster.stats.elementFlag.flag).getRegistryId().entryId() + ".png"));
    this.modelTexture = this.getModelTexture();
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

    FUN_801034cc(this.entryIndex, bestiaryEntries.size(), -10, this.isListVisible); // Left/right arrows
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

    renderText("Tracked: " + TrackerHud.getTrackers("bestiary").size() + "/10", 0.5f, 229f, this.difficultyFont, 127);
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
      if(Bestiary.devMode) {
        renderText(this.monster.name + " [" + this.monster.charId + ']', 184, 10.5f, this.isTracked(this.monster) ? this.headerTrackedFont : this.headerFont, 126);
      } else {
        renderText(this.monster.name, 184, 10.5f, this.isTracked(this.monster) ? this.headerTrackedFont : this.headerFont, 126);
      }
    } else {
      renderText(this.QUESTION_MARK_5, 184, 10.5f, this.headerFont, 126);
    }

    if(this.monster.rank > 0 || this.monster.rank == -1) {
      renderText(this.monster.location, 31, 206.5f, this.locationFont, 127);
    } else {
      renderText(this.QUESTION_MARK_5, 31, 206.5f, this.locationFont, 126);
    }

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
        renderText(this.QUESTION_MARK_3, x + 28f, y, this.statsFont, 127);
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

    renderText(this.monster.rank >= 2 ? status : this.QUESTION_MARK_5, x, y, this.statsFont, 127);

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
      renderText(this.monster.isComplete() ? "Exp." : this.QUESTION_MARK_3, x, y, this.rewardTitleFont, 127);
      renderText(this.monster.isComplete() ? String.valueOf(rewards.xp_00) : this.QUESTION_MARK_5, x + 28f, y, this.rewardTitleFont, 127);
      y += 13.5f;
    }

    if(rewards.gold_02 > 0) {
      renderText(this.monster.isComplete() ? "Gold" : this.QUESTION_MARK_3, x, y, this.rewardTitleFont, 127);
      renderText(this.monster.isComplete() ? String.valueOf(rewards.gold_02) : this.QUESTION_MARK_5, x + 28f, y, this.rewardTitleFont, 127);
      y += 13.5f;
    }

    if(rewards.itemDrop_05 != null) {
      renderText(this.monster.isComplete() ? I18n.translate(rewards.itemDrop_05.get().getNameTranslationKey()) + " (" + rewards.itemChance_04 + "%)" : this.QUESTION_MARK_5, x + 28f, y, this.rewardTitleFont, 127);

      if(this.monster.isComplete()) {
        renderItemIcon(rewards.itemDrop_05.get().getIcon(), x + 10f, y - 5f, 32, 0.9f, 0.9f, 0x8);
      } else {
        renderText(this.QUESTION_MARK_3, x, y, this.rewardTitleFont, 127);
      }
    }
  }

  private void renderLore() {
    if(this.monster.rank >= 2) {
      renderText(this.monster.lore.isEmpty() ? this.LORE_DEFAULT : this.monster.lore, 23f, 146f, this.loreFont, 127);
    } else {
      renderText(this.QUESTION_MARK_5, 23f, 146f, this.statsFont, 127);
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

    renderText(this.bestiarySeenCount + "/" + bestiaryEntries.size(), x + 88f, 19.5f, this.bestiaryPerfect ? this.listTotalPerfectFont : this.listTotalFont, 123);

    float y = 32f;
    for(int i = 0; i < LIST_ITEM_COUNT; i++) {
      final int entryIndex = this.listFirstVisibleItem + i;
      final BestiaryEntry entry = bestiaryEntries.get(entryIndex);
      final boolean highlighted = this.entryIndex == entryIndex;
      float charX = 6;
      for(final char c : this.nf.format(entry.entryNumber).toCharArray()) {
        renderText(String.valueOf(c), x + charX, y, highlighted ? (entry.isComplete() ? this.listNumberPerfectHighlightFont : this.listNumberHighlightFont) : this.listNumberFont, 123);
        charX += 3.7f;
      }

      final FontOptions textFont = this.isTracked(entry) ? this.trackedFont : this.listFont;
      final FontOptions textHighlightFont = this.isTracked(entry) ? this.trackedHighlightFont : this.listHighlightFont;

      renderText(":", x + 16f, y, highlighted ? (entry.isComplete() ? this.listNumberPerfectHighlightFont : this.listNumberHighlightFont) : this.listNumberFont, 123);
      renderText(entry.rank > 0 || entry.rank == -1 ? (entry.listName == null ? entry.name : entry.listName) : this.QUESTION_MARK_5, x + 19f, y, highlighted ? (entry.isComplete() ? this.listPerfectHighlightFont : textHighlightFont) : (entry.isComplete() ? this.listPerfectFont : textFont), 123);

      if(highlighted) {
        this.m.translation(xOffset - x + 14.5f , y - 1.5f, 124);
        this.m.scale(83.5f, 7.5f, 1);

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

    if(this.listFirstVisibleItem < bestiaryEntries.size() - LIST_ITEM_COUNT) {
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
      if(this.entryIndex < bestiaryEntries.size() - 1) {
        b = true;
        this.entryIndex++;
        this.subEntryIndex = 0;
        if(this.listFirstVisibleItem < bestiaryEntries.size() - LIST_ITEM_COUNT && this.listFirstVisibleItem + LIST_ITEM_COUNT * 0.85f < this.entryIndex) {
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
      this.listFirstVisibleItem = Math.clamp(index, 0, bestiaryEntries.size() - LIST_ITEM_COUNT);
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
      return bestiaryEntries.get(this.entryIndex);
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



  private void setBestiaryStatus() {
    int totalAtMaxRank = 0;
    for(final BestiaryEntry r : bestiaryEntries) {
      if(r.kill > 0) {
        this.bestiarySeenCount++;
        if(r.isComplete()) {
          totalAtMaxRank++;
        }
      }
    }
    this.bestiaryPerfect = totalAtMaxRank >= bestiaryEntries.size();
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

    bestiaryEntries = switch(this.currentSort) {
      case 1 -> bestiaryEntries.stream()
        .sorted(Comparator.comparingInt(o -> o.entryNumber))
        .sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.name, o2.name))
        .collect(Collectors.toList());
      case 2 -> bestiaryEntries.stream()
        .sorted(Comparator.comparingInt(o -> o.entryNumber))
        .sorted(Comparator.comparingInt(o -> o.stats.elementFlag.flag))
        .collect(Collectors.toList());
      case 3 -> bestiaryEntries.stream()
        .sorted(Comparator.comparingInt(o -> o.entryNumber))
        .sorted(Comparator.comparingInt(o -> o.rank))
        .sorted(Comparator.comparing(o -> o.rank != 0))
        .collect(Collectors.toList());
      case 4 -> bestiaryEntries.stream()
        .sorted(defeatedComparator)
        .collect(Collectors.toList());
      default -> bestiaryEntries.stream()
        .sorted(Comparator.comparingInt(o -> o.entryNumber))
        .collect(Collectors.toList());
    };

    if(jumpToEntry) {
      this.jump(this.indexOfEntry(this.monster.entryNumber), true);
    }
  }

  private int indexOfEntry(final int number) {
    for(int i = 0; i < bestiaryEntries.size(); i++) {
      if(bestiaryEntries.get(i).entryNumber == number) {
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

  private Texture getModelTexture() {
    Texture texture = null;
    try {
      texture = Texture.png(Path.of("gfx", "models", this.monster.charId + ".png"));
    } catch(final Exception e1) {
      try {
        if(this.monster.isSubEntry) {
          texture = Texture.png(Path.of("gfx", "models", this.getEntryParent(this.monster).charId + ".png"));
        }
      } catch(final Exception ignored) {
      }
    }
    if(texture == null) {
      texture = Texture.png(Path.of("gfx", "models", "-1.png"));
    }
    return texture;
  }

  private void toggleTracker() {
    Bestiary.toggleTracker(this.monster);
  }

  private boolean isTracked(final BestiaryEntry entry) {
    return TrackerHud.exists("bestiary:" + entry.charId);
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

    final boolean canTrackEntry = this.monster.rank > 0 && this.monster.rank < 3 || this.monster.rank == -1;
    if(canTrackEntry) {
      FooterActionsHud.renderActions(0, FooterActions.BACK, FooterActions.LIST, FooterActions.JUMP, this.isTracked(this.monster) ? FooterActions.UNTRACK : FooterActions.TRACK, null);
    } else {
      FooterActionsHud.renderActions(0, FooterActions.BACK, FooterActions.LIST, FooterActions.JUMP, null, null);
    }
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
    if(this.entryIndex < bestiaryEntries.size()) {
      for(int i = this.entryIndex + 1; i < bestiaryEntries.size(); i++) {
        if(!bestiaryEntries.get(i).isComplete()) {
          if(this.jump(i, true)) {
            playMenuSound(1);
            this.loadCurrentEntry();
            return;
          }
        }
      }
    }
    for(int i = 0; i < bestiaryEntries.size(); i++) {
      if(!bestiaryEntries.get(i).isComplete()) {
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
      this.toggleTracker();
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
      if(this.jump(bestiaryEntries.size() - 1, false)) {
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
