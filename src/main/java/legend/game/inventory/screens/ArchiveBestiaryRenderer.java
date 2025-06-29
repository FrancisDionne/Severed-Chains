package legend.game.inventory.screens;

import legend.core.QueuedModelStandard;
import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.core.opengl.Texture;
import legend.game.characters.Element;
import legend.game.combat.types.EnemyRewards08;
import legend.game.combat.types.MonsterStats1c;
import legend.game.i18n.I18n;
import org.joml.Matrix4f;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static legend.core.GameEngine.RENDERER;
import static legend.game.SItem.FUN_801034cc;
import static legend.game.SItem.renderItemIcon;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.combat.Monsters.enemyRewards_80112868;
import static legend.game.combat.Monsters.monsterNames_80112068;
import static legend.game.combat.Monsters.monsterStats_8010ba98;
import static legend.game.wmap.WmapStatics.places_800f0234;

public class ArchiveBestiaryRenderer {
  private final Matrix4f m;
  private final Obj quad;
  private final Texture[] textures;
  private Texture headerTexture;
  private Texture modelTexture;
  private List<Integer> bestiaryPages;
  private MonsterStats1c monsterStats;

  private final FontOptions headerFont;
  private final FontOptions statsFont;
  private final FontOptions titleFont;
  private final FontOptions resistFont;
  private final FontOptions resistNumberFont;
  private final FontOptions rewardTitleFont;

  public int pageIndex;

  public int getPageCount() {
    return this.bestiaryPages.size();
  }

  private int charId() {
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

    this.textures = new Texture[] {
      Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\bestiary_graphics.png")),  //0
      Texture.png(Path.of("gfx", "ui", "action_attack.png")),  //1
      Texture.png(Path.of("gfx", "ui", "action_guard.png")),   //2
    };

    this.pageIndex = 0;

    this.loadPages();
    this.loadCurrentPage();
  }

  private void loadPages() {
    this.bestiaryPages = new ArrayList<>();
    this.addPage(70);
    this.addPage(132);
    this.addPage(17);
    this.addPage(258);
    this.addPage(36);
    this.addPage(119);
    this.addPage(3);
    this.addPage(61);
    this.addPage(89);
    this.addPage(85);
    this.addPage(18);
    this.addPage(86);
    this.addPage(387);
    this.addPage(352);
    this.addPage(353);
    this.addPage(354);
    this.addPage(382);
    this.addPage(381);
    this.addPage(371);
    this.addPage(370);
    this.addPage(369);
    this.addPage(368);
    this.addPage(366);
    this.addPage(365);
    this.addPage(363);
    this.addPage(364);
    this.addPage(362);
    this.addPage(361);
    this.addPage(360);
    this.addPage(349);
    this.addPage(347);
    this.addPage(346);
    this.addPage(344);
    this.addPage(343);
    this.addPage(340);
    this.addPage(341);
    this.addPage(335);
    this.addPage(334);
    this.addPage(333);
    this.addPage(332);
    this.addPage(329);
    this.addPage(325);
    this.addPage(320);
    this.addPage(311);
    this.addPage(308);
    this.addPage(305);
    this.addPage(304);
    this.addPage(303);
    this.addPage(302);
    this.addPage(301);
    this.addPage(299);
    this.addPage(298);
    this.addPage(297);
    this.addPage(296);
    this.addPage(295);
    this.addPage(294);
    this.addPage(279);
    this.addPage(293);
    this.addPage(288);
    this.addPage(284);
    this.addPage(275);
    this.addPage(287);
    this.addPage(270);
    this.addPage(269);
    this.addPage(268);
    this.addPage(267);
    this.addPage(264);
    this.addPage(263);
    this.addPage(260);
    this.addPage(261);
    this.addPage(259);
    this.addPage(102);
    this.addPage(256);
    this.addPage(257);
    this.addPage(123);
    this.addPage(127);
    this.addPage(71);
    this.addPage(81);
    this.addPage(128);
    this.addPage(34);
    this.addPage(126);
    this.addPage(7);
    this.addPage(98);
    this.addPage(62);
    this.addPage(77);
    this.addPage(125);
    this.addPage(101);
    this.addPage(10);
    this.addPage(78);
    this.addPage(84);
    this.addPage(118);
    this.addPage(122);
    this.addPage(97);
    this.addPage(40);
    this.addPage(80);
    this.addPage(121);
    this.addPage(9);
    this.addPage(72);
    this.addPage(60);
    this.addPage(75);
    this.addPage(120);
    this.addPage(93);
    this.addPage(41);
    this.addPage(63);
    this.addPage(65);
    this.addPage(47);
    this.addPage(39);
    this.addPage(116);
    this.addPage(20);
    this.addPage(50);
    this.addPage(31);
    this.addPage(57);
    this.addPage(56);
    this.addPage(11);
    this.addPage(25);
    this.addPage(49);
    this.addPage(67);
    this.addPage(5);
    this.addPage(114);
    this.addPage(48);
    this.addPage(32);
    this.addPage(37);
    this.addPage(113);
    this.addPage(112);
    this.addPage(6);
    this.addPage(96);
    this.addPage(111);
    this.addPage(2);
    this.addPage(30);
    this.addPage(43);
    this.addPage(44);
    this.addPage(117);
    this.addPage(95);
    this.addPage(51);
    this.addPage(73);
    this.addPage(54);
    this.addPage(12);
    this.addPage(110);
    this.addPage(91);
    this.addPage(76);
    this.addPage(23);
    this.addPage(109);
    this.addPage(79);
    this.addPage(82);
    this.addPage(83);
    this.addPage(106);
    this.addPage(14);
    this.addPage(16);
    this.addPage(29);
    this.addPage(52);
    this.addPage(26);
    this.addPage(108);
    this.addPage(99);
    this.addPage(2);
    this.addPage(69);
    this.addPage(138);
    this.addPage(137);
    this.addPage(136);
    this.addPage(54);
    this.addPage(42);
    this.addPage(139);
    this.addPage(124);
    this.addPage(87);
    this.addPage(55);
    this.addPage(58);
    this.addPage(15);
    this.addPage(107);
    this.addPage(66);
    this.addPage(115);
    this.addPage(88);
    this.addPage(19);
    this.addPage(35);
    this.addPage(90);
    this.addPage(33);
    this.addPage(92);
    this.addPage(100);
    this.addPage(74);
    this.addPage(22);
    this.addPage(28);
    this.addPage(68);
    this.addPage(4);
    this.addPage(13);
    this.addPage(94);
    this.addPage(64);
    this.addPage(104);
    this.addPage(46);
    this.addPage(0);
    this.addPage(59);
    this.addPage(45);
    this.addPage(27);
    this.addPage(8);
    this.addPage(38);
    this.addPage(21);
    this.addPage(24);
  }

  private void addPage(final int charId) {
    this.bestiaryPages.add(charId);
  }

  public void loadCurrentPage() {
    final int charID = this.charId();
    this.monsterStats = monsterStats_8010ba98[charID];
    this.headerTexture = Texture.png(Path.of("gfx", "ui", "archive_screen\\bestiary\\header_element_" + this.getElement(this.monsterStats.elementFlag_0f).getRegistryId().entryId() + ".png"));

    try {
      this.modelTexture = Texture.png(Path.of("gfx", "models", charID + ".png"));
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
    FUN_801034cc(this.pageIndex, this.getPageCount(), -10); // Left/right arrows
  }

  private void renderGraphics() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    this.m.translation(xOffset, 0f, 125);
    this.m.scale(368f, 240f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[0]);
  }

  private void renderModel() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    this.m.translation(-22f + xOffset, 27.8f, 125f);
    this.m.scale(210f, 87.3f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.modelTexture); //Model
  }

  private void renderEnemyName() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();

    this.m.translation(14.5f + xOffset, 8f, 124f);
    this.m.scale(338.6f, 19.9f, 1);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.headerTexture); //Header

    //renderText(monsterNames_80112068[this.charId()], 184, 10.5f, this.headerFont, 120);
    renderText(monsterNames_80112068[this.charId()] + " [" + this.charId() + ']', 184, 10.5f, this.headerFont, 120);
  }

  private void renderEnemyStats() {
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();
    float x = 200f;
    float y = 45.5f;

    for(int i = 0; i < 8; i++) {
      renderText(this.getStatName(i), x, y, this.statsFont, 120);
      renderText(String.valueOf(this.getStat(i)), x + 28f, y, this.statsFont, 120);
      y += 11.45f;
    }

    x = 272;
    y = 47.4f - 11.45f;

    if(this.monsterStats.elementalImmunityFlag_10 > 0) {
      y += 11.45f;
      this.renderElementMultipliers(this.monsterStats.elementFlag_0f, x, y, xOffset, 100, 50, false);
    } else if(this.monsterStats.elementFlag_0f != 8) {
      y += 11.45f;
      this.renderElementMultipliers(this.monsterStats.elementFlag_0f, x, y, xOffset, 50, 50, false);
    }

    final int counterElement = this.getCounterElement(this.monsterStats.elementFlag_0f);
    if(counterElement != 0) {
      y += 11.45f;
      this.renderElementMultipliers(counterElement, x, y, xOffset, 50, 50, true);
    }
  }

  private void renderElementMultipliers(final int elementFlag, final float x, final float y, final float xOffset, final int defPercent, final int attPercent, final boolean counter) {
    final String guardSign = counter ? "-" : "+";
    final String attackSign = counter ? "+" : "-";

    this.m.translation(x + 27.5f + xOffset, y - 1.5f, 124f);
    this.m.scale(7, 7, 1);

    renderText(I18n.translate(this.getElement(elementFlag).getTranslationKey()), x, y, this.resistFont, 120);

    RENDERER
      .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
      .texture(this.textures[2]); //Guard

    renderText(guardSign + defPercent + '%', x + 35f, y + 0.3f, this.resistNumberFont, 120);

    if(attPercent != 0) {
      this.m.translation(x + 53f + xOffset, y - 1.5f, 124f);
      this.m.scale(7, 7, 1);

      RENDERER
        .queueOrthoModel(this.quad, this.m, QueuedModelStandard.class)
        .texture(this.textures[1]); //Attack

      renderText(attackSign + attPercent + '%', x + 60f, y + 0.3f, this.resistNumberFont, 120);
    }
  }

  private void renderRewards() {
    final EnemyRewards08 rewards = enemyRewards_80112868[this.charId()];
    final float xOffset = RENDERER.getWidescreenOrthoOffsetX();
    final float x = 200f;
    float y = 166f;

    renderText("Battle Rewards", x + 74, 150f, this.titleFont, 120);

    if(rewards.xp_00 > 0) {
      renderText("Exp.", x, y, this.rewardTitleFont, 120);
      renderText(String.valueOf(rewards.xp_00), x + 28f, y, this.rewardTitleFont, 120);
      y += 13.5f;
    }

    if(rewards.gold_02 > 0) {
      renderText("Gold", x, y, this.rewardTitleFont, 120);
      renderText(String.valueOf(rewards.gold_02), x + 28f, y, this.rewardTitleFont, 120);
      y += 13.5f;
    }

    if(rewards.itemDrop_05 != null) {
      renderText(I18n.translate(rewards.itemDrop_05.get().getNameTranslationKey()) + " (" + rewards.itemChance_04 + "%)", x + 28f, y, this.rewardTitleFont, 120);

      this.m.translation(x + 53f + xOffset, y - 1.5f, 124f);
      this.m.scale(7, 7, 1);

      renderItemIcon(rewards.itemDrop_05.get().getIcon(), x + 10f, y - 5f, 20, 0.9f, 0.9f, 0x8);
    }
  }

  private int getStat(final int statIndex) {
    return switch(statIndex) {
      case 0 -> this.monsterStats.hp_00;
      case 1 -> this.monsterStats.attack_04;
      case 2 -> this.monsterStats.defence_09;
      case 3 -> this.monsterStats.magicAttack_06;
      case 4 -> this.monsterStats.magicDefence_0a;
      case 5 -> this.monsterStats.speed_08;
      case 6 -> this.monsterStats.attackAvoid_0b;
      case 7 -> this.monsterStats.magicAvoid_0c;
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
