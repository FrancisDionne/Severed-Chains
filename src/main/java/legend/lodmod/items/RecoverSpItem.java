package legend.lodmod.items;

import legend.core.memory.Method;
import legend.game.combat.bent.BattleEntity27c;
import legend.game.inventory.Item;
import legend.game.inventory.ItemIcon;
import legend.game.inventory.UseItemResponse;

import static legend.game.Scus94491BpeSegment_8002.addSp;

public class RecoverSpItem extends BattleItem {
  private final boolean targetAll;
  private final int percentage;
  private final int amount;

  public RecoverSpItem(final int price, final boolean targetAll, final int percentage, final int amount) {
    super(ItemIcon.RED_POTION, price);
    this.targetAll = targetAll;
    this.percentage = percentage;
    this.amount = amount;
  }

  @Override
  public boolean canBeUsed(final UsageLocation location) {
    return location == UsageLocation.BATTLE;
  }

  @Override
  public boolean canTarget(final TargetType type) {
    return type == TargetType.ALLIES || type == TargetType.ALL && this.targetAll;
  }

  @Override
  @Method(0x80022d88L)
  public void useInMenu(final UseItemResponse response, final int charId) {
    final int amount;

    if(this.amount > 0) {
      amount = this.amount;
    } else if(this.percentage == 100) {
      amount = -1;
    } else {
      amount = this.percentage;
    }

    response._00 = 6;
    response.value_04 = addSp(charId, amount);
  }

  @Override
  public boolean isStatMod() {
    return true;
  }

  @Override
  public int calculateStatMod(final BattleEntity27c user, final BattleEntity27c target) {
    return 0;
  }

  @Override
  public boolean alwaysHits() {
    return true;
  }
}
