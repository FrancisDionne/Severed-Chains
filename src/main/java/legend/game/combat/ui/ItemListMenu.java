package legend.game.combat.ui;

import legend.core.Config;
import legend.core.memory.Method;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.i18n.I18n;
import legend.game.inventory.Item;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.TextColour;
import legend.game.modding.events.inventory.RepeatItemReturnEvent;
import legend.game.scripting.RunningScript;
import legend.game.statistics.Statistics;
import legend.lodmod.items.AngelsPrayerItem;
import legend.lodmod.items.AttackItem;
import legend.lodmod.items.BattleItem;
import legend.lodmod.items.BuffItem;
import legend.lodmod.items.CauseStatusItem;
import legend.lodmod.items.CharmPotionItem;
import legend.lodmod.items.PandemoniumItem;
import legend.lodmod.items.RecoverHpItem;
import legend.lodmod.items.RecoverMpItem;
import legend.lodmod.items.RecoverSpItem;
import legend.lodmod.items.RecoverStatusItem;
import legend.lodmod.items.SachetItem;
import legend.lodmod.items.SignetStoneItem;
import legend.lodmod.items.TotalVanishingItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static legend.core.GameEngine.EVENTS;
import static legend.game.Scus94491BpeSegment_8002.getInventoryEntryQuantity;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.Scus94491BpeSegment_8002.takeItemId;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;

public class ItemListMenu extends ListMenu {
  private final FontOptions fontOptions = new FontOptions().colour(TextColour.WHITE);

  private UiBox description;

  private List<CombatItem02> combatItems_800c6988 = new ArrayList<>();

  public ItemListMenu(final BattleHud hud, final PlayerBattleEntity activePlayer, final ListPosition lastPosition, final Runnable onClose) {
    super(hud, activePlayer, 186, lastPosition, onClose);
    this.cacheItems();

    this.flags_02 |= 0x20;
  }

  @Override
  protected int getListCount() {
    return this.combatItems_800c6988.size();
  }

  @Override
  protected void drawListEntry(final int index, final int x, final int y, final int trim) {
    final Item item = this.combatItems_800c6988.get(index).item;
    this.fontOptions.trim(trim).horizontalAlign(HorizontalAlign.LEFT);

    if(item.isRepeat()) {
      this.fontOptions.colour(TextColour.STATS_YELLOW);
    }

    renderText(I18n.translate(item), x, y, this.fontOptions);

    this.fontOptions.colour(TextColour.WHITE);

    renderText("\u011d", x + 143, y, this.fontOptions);

    this.fontOptions.horizontalAlign(HorizontalAlign.RIGHT);
    renderText(String.valueOf(getInventoryEntryQuantity(item)), x + 168, y, this.fontOptions);
  }

  @Override
  protected void onSelection(final int index) {
    //LAB_800f50b8
    this.player_08.setActiveItem(this.combatItems_800c6988.get(index).item);
  }

  @Override
  protected void onUse(final int index) {
    final Item item = this.player_08.item_d4;
    takeItemId(item);

    final RepeatItemReturnEvent repeatItemReturnEvent = EVENTS.postEvent(new RepeatItemReturnEvent(item, item.isRepeat()));

    if(repeatItemReturnEvent.returnItem) {
      this.hud.battle.usedRepeatItems_800c6c3c.add(item);
    }

    if(this.player_08.item_d4.canTarget(Item.TargetType.ENEMIES)) {
      Statistics.appendStat(this.player_08, Statistics.Stats.TOTAL_MAGICAL_ATTACK, 1);
    }
  }

  @Override
  protected void onClose() {
  }

  @Override
  protected int handleTargeting() {
    //TODO
    return this.hud.handleTargeting(this.player_08.item_d4.canTarget(Item.TargetType.ALLIES) ? 0 : 1, this.player_08.item_d4.canTarget(Item.TargetType.ALL));
  }

  @Override
  public void getTargetingInfo(final RunningScript<?> script) {
    script.params_20[0].set(this.selectionState_a0);

    if(this.selectionState_a0 < 1) {
      return;
    }

    script.params_20[1].set(this.hud.battleMenu_800c6c34.target_48);
    script.params_20[2].set(-1); // Used to be item ID
    script.params_20[3].set(this.player_08.item_d4.getRegistryId());

    // If it's a target all item, -1 the target
    if(this.selectionState_a0 == 1 && this.player_08.item_d4.canTarget(Item.TargetType.ALL)) {
      script.params_20[1].set(-1);
    }
  }

  @Method(0x800f83c8L)
  private void cacheItems() {
    //LAB_800f83dc
    this.combatItems_800c6988.clear();

    //LAB_800f8420
    for(int itemSlot = 0; itemSlot < gameState_800babc8.items_2e9.size(); itemSlot++) {
      final Item item = gameState_800babc8.items_2e9.get(itemSlot);

      boolean found = false;

      //LAB_800f843c
      for(final CombatItem02 combatItem : this.combatItems_800c6988) {
        if(combatItem.item == item) {
          found = true;
          combatItem.count++;
          break;
        }
      }

      if(!found) {
        this.combatItems_800c6988.add(new CombatItem02(item));
      }
    }

    final Comparator<CombatItem02> comparator = Comparator
      .comparing((CombatItem02 x) -> x.item instanceof RecoverHpItem)
      .thenComparing(x -> x.item instanceof AngelsPrayerItem)
      .thenComparing(x -> x.item instanceof RecoverMpItem)
      .thenComparing(x -> x.item instanceof RecoverSpItem)
      .thenComparing(x -> x.item instanceof RecoverStatusItem)
      .thenComparing(x -> x.item instanceof final AttackItem attackItem && !attackItem.isTargetAll())
      .thenComparing(x -> x.item instanceof final AttackItem attackItem && attackItem.isTargetAll())
      .thenComparing(x -> x.item instanceof CauseStatusItem)
      .thenComparing(x -> x.item instanceof final BattleItem battleItem && battleItem.isRepeat())
      .thenComparing(x -> x.item instanceof BuffItem)
      .thenComparing(x -> x.item instanceof SachetItem)
      .thenComparing(x -> x.item instanceof SignetStoneItem)
      .thenComparing(x -> x.item instanceof PandemoniumItem)
      .thenComparing(x -> x.item instanceof TotalVanishingItem)
      .thenComparing(x -> x.item instanceof CharmPotionItem).reversed()
      .thenComparing(x -> I18n.translate(x.item.getNameTranslationKey()));

    this.combatItems_800c6988 = this.combatItems_800c6988.stream().sorted(comparator).collect(Collectors.toList());
  }

  @Override
  public void draw() {
    super.draw();

    if(this.menuState_00 != 0 && (this.flags_02 & 0x1) != 0) {
      //LAB_800f5f50
      if((this.flags_02 & 0x40) != 0) {
        //Selected item description
        if(this.description == null) {
          this.description = new UiBox("Battle UI Item Description", 44, 156, 232, 14);
        }

        this.description.render(Config.changeBattleRgb() ? Config.getBattleRgb() : Config.defaultUiColour);

        this.fontOptions.trim(0);
        this.fontOptions.horizontalAlign(HorizontalAlign.CENTRE);
        renderText(I18n.translate(this.combatItems_800c6988.get(this.listScroll_1e + this.listIndex_24).item.getBattleDescriptionTranslationKey()), 160, 157, this.fontOptions);
      }
    }
  }

  @Override
  public void delete() {
    super.delete();

    if(this.description != null) {
      this.description.delete();
      this.description = null;
    }
  }
}
