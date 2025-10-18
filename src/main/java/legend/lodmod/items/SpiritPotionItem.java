package legend.lodmod.items;

import legend.game.combat.bent.BattleEntity27c;
import legend.game.inventory.ItemIcon;
import legend.game.inventory.ItemStack;
import legend.game.scripting.ScriptState;

import static legend.game.Scus94491BpeSegment_800b.characterIndices_800bdbb8;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Scus94491BpeSegment_800b.stats_800be5f8;

public class SpiritPotionItem extends RecoverSpItem {
  public SpiritPotionItem() {
    super(10, false, 0, 100);
  }

  @Override
  public boolean canBeUsed(final ItemStack stack, final UsageLocation location) {
    return location == UsageLocation.BATTLE || location == UsageLocation.MENU;
  }

  public boolean canBeUsedNow(final UsageLocation location) {
    boolean canRecover = false;
    for(int i = 0; i < characterIndices_800bdbb8.length; i++) {
      if((gameState_800babc8.charData_32c[i].partyFlags_04 & 0x3) != 0 && stats_800be5f8[i].dlevel_0f * 100 > stats_800be5f8[i].sp_08) {
        canRecover = true;
        break;
      }
    }

    return canRecover;
  }

  @Override
  protected int getUseItemScriptEntrypoint() {
    return 2;
  }

  @Override
  protected void useItemScriptLoaded(final ScriptState<BattleEntity27c> user, final int targetBentIndex) {
    user.storage_44[8] = 0x6868ff; // Colour
    user.storage_44[28] = targetBentIndex;
    user.storage_44[30] = user.index;
  }
}
