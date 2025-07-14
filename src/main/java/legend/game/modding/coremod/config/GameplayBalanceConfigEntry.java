package legend.game.modding.coremod.config;

import legend.game.combat.bent.BattleEntity27c;
import legend.game.combat.bent.BattleEntityStat;
import legend.game.modding.coremod.CoreMod;
import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import org.joml.Math;

import javax.annotation.Nullable;
import java.util.HashMap;

import static legend.core.GameEngine.CONFIG;

public class GameplayBalanceConfigEntry extends BoolConfigEntry {
  public GameplayBalanceConfigEntry() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.CHALLENGES, 5);
  }

  public static int adjustValue(@Nullable final BattleEntity27c currentTurnBent, final int bentCharId, final int lastSelectedAction, final HashMap<Integer, int[]> playerLastActions, int value, final boolean isSetStat) {
    if(CONFIG.getConfig(CoreMod.GAMEPLAY_BALANCE_CONFIG.get())) {
      if(currentTurnBent != null && playerLastActions.containsKey(bentCharId) && currentTurnBent.charId_272 == bentCharId && lastSelectedAction == 1) {
        value = adjustGuardHealValue(currentTurnBent, playerLastActions.get(bentCharId)[1], value, isSetStat);
      }
    }
    return value;
  }

  public static int adjustGuardHealValue(final BattleEntity27c bent, final int playerLastActionCount, final int value, final boolean isSetStat) {
    final int maxHp = bent.getStat(BattleEntityStat.MAX_HP);
    final int possibleHpRestored = Math.round(maxHp * Math.max(0.025f, 0.1f - ((playerLastActionCount - 1) * 0.025f)));
    return Math.min(isSetStat ? bent.getStat(BattleEntityStat.CURRENT_HP) + possibleHpRestored : possibleHpRestored, value);
  }
}
