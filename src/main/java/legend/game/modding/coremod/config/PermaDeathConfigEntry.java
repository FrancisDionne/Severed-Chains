package legend.game.modding.coremod.config;

import legend.game.combat.bent.BattleEntity27c;
import legend.game.combat.bent.BattleEntityStat;
import legend.game.combat.bent.MonsterBattleEntity;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.modding.coremod.CoreMod;
import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.scripting.ScriptState;
import legend.game.statistics.Statistics;

import java.util.ArrayList;
import java.util.List;

import static legend.core.GameEngine.CONFIG;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;

public class PermaDeathConfigEntry extends BoolConfigEntry {
  public PermaDeathConfigEntry() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.CHALLENGES, 1, PermaDeathConfigEntry::callback);
  }

  private static void callback() {
    if(CONFIG.getConfig(CoreMod.PERMA_DEATH.get())) {
      if(!CONFIG.getConfig(CoreMod.UNLOCK_PARTY_CONFIG.get())) {
        CONFIG.setConfig(CoreMod.UNLOCK_PARTY_CONFIG.get(), true);
      }
    }
  }

  public static boolean evaluateParty() {
    if(CONFIG.getConfig(CoreMod.PERMA_DEATH.get())) {
      final List<Integer> validCharacters = new ArrayList<>();

      //Load characters who haven't died and are available to put in party slots
      for(int i = 0; i < gameState_800babc8.charData_32c.length; i++) {
        if(isAvailable(i, true) && !hasDied(i, true)) {
          validCharacters.add(i);
        }
      }

      //Go through the party slots to find out who died and should be removed
      for(int i = 0; i < gameState_800babc8.charIds_88.length; i++) {
        final int charId = gameState_800babc8.charIds_88[i];
        if(!validCharacters.contains(charId)) {
          gameState_800babc8.charIds_88[i] = -1; //Remove from party slot if died
        }
      }

      //If there are no valid characters, means everyone who can be put in party slot has died, fall back and put the first character in 1st slot
      //This is unavoidable for story events where the only character to slot has died
      if(validCharacters.isEmpty()) {
        for(int i = 0; i < gameState_800babc8.charData_32c.length; i++) {
          if(gameState_800babc8.charData_32c[i].partyFlags_04 != 0) {
            gameState_800babc8.charIds_88[0] = i;
            return true;
          }
        }
        return false;
      }

      //If the 2nd slot character died, move 3rd slot up
      if(gameState_800babc8.charIds_88[1] == -1) {
        gameState_800babc8.charIds_88[1] = gameState_800babc8.charIds_88[2];
        gameState_800babc8.charIds_88[2] = -1;
        remove(validCharacters, gameState_800babc8.charIds_88[1]); //Remove from validCharacters as we know they will stay in the party and need no further consideration
      }

      //The 1st slot always needs a character so if empty move 2nd slot up
      if(gameState_800babc8.charIds_88[0] == -1) {
        gameState_800babc8.charIds_88[0] = gameState_800babc8.charIds_88[1];
        gameState_800babc8.charIds_88[1] = -1;
        remove(validCharacters, gameState_800babc8.charIds_88[0]); //Remove from validCharacters as we know they will stay in the party and need no further consideration
      }

      //If the 2nd slot was emptied because 1st slot died, move 3rd slot up
      if(gameState_800babc8.charIds_88[1] == -1) {
        gameState_800babc8.charIds_88[1] = gameState_800babc8.charIds_88[2];
        gameState_800babc8.charIds_88[2] = -1;
        remove(validCharacters, gameState_800babc8.charIds_88[1]); //Remove from validCharacters as we know they will stay in the party and need no further consideration
      }

      //Check that the 1st slot in fact has a character, if not slot in the first valid character
      if(gameState_800babc8.charIds_88[0] == -1 && !validCharacters.isEmpty()) {
        gameState_800babc8.charIds_88[0] = validCharacters.getFirst();
      }

      return checkCharIds(true);
    }
    return true;
  }

  private static void remove(final List<Integer> list, final int charId) {
    final int index = list.indexOf(charId);
    if(index > -1) {
      list.remove(index);
    }
  }

  private static boolean checkCharIds(final boolean isEmpty) {
    for(int i = 0; i < gameState_800babc8.charIds_88.length; i++) {
      if((isEmpty && gameState_800babc8.charIds_88[i] != -1) || (!isEmpty && gameState_800babc8.charIds_88[i] == -1)) {
        return isEmpty;
      }
    }
    return !isEmpty;
  }

  public static boolean hasDied(final int charId) {
    return hasDied(charId, CONFIG.getConfig(CoreMod.PERMA_DEATH.get()));
  }

  public static boolean hasDied(final int charId, final boolean permaDeathEnabled) {
    if(permaDeathEnabled && charId != -1) {
      return Statistics.getStat(Math.abs(Statistics.Stats.TOTAL_DEATH.asInt()) + charId + 1) > 0;
    }
    return false;
  }

  public static boolean isAvailable(final int charId) {
    return isAvailable(charId, CONFIG.getConfig(CoreMod.PERMA_DEATH.get()));
  }

  public static boolean isAvailable(final int charId, final boolean permaDeathEnabled) {
    if(permaDeathEnabled) {
      return gameState_800babc8.charData_32c[charId].partyFlags_04 != 0;
    }
    return true;
  }

  public static boolean isUsed(final int charId) {
    for(int i = 0; i < gameState_800babc8.charIds_88.length; i++) {
      if(gameState_800babc8.charIds_88[i] == charId) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasNotDiedOrIsInUse(final int charId) {
    return !hasDied(charId) || isUsed(charId);
  }

  public static boolean isBlockRevive(final BattleEntityStat stat, final BattleEntity27c bent, final ScriptState<? extends BattleEntity27c> currentTurnBent, final int value, final boolean dead) {
    if(stat == BattleEntityStat.CURRENT_HP && value != 0 && !dead && bent instanceof PlayerBattleEntity && bent.getStat(BattleEntityStat.CURRENT_HP) <= 0 && CONFIG.getConfig(CoreMod.PERMA_DEATH.get())) {
      if(bent.charId_272 == currentTurnBent.innerStruct_00.charId_272 || currentTurnBent.innerStruct_00 instanceof MonsterBattleEntity) {
        return false; // For auto revive with Angel Robe
      }
      return true;
    }
    return false;
  }
}
