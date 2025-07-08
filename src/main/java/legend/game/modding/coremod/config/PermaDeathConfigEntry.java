package legend.game.modding.coremod.config;

import legend.game.modding.coremod.CoreMod;
import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.statistics.Statistics;
import legend.game.types.CharacterData2c;

import java.util.ArrayList;
import java.util.List;

import static legend.core.GameEngine.CONFIG;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;

public class PermaDeathConfigEntry extends BoolConfigEntry {
  public PermaDeathConfigEntry() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, 3001, PermaDeathConfigEntry::callback);
  }

  private static void callback() {
    if(CONFIG.getConfig(CoreMod.PERMA_DEATH.get())) {
      CONFIG.setConfig(CoreMod.UNLOCK_PARTY_CONFIG.get(), true);
    }
  }

  public static boolean assessParty() {
    if(CONFIG.getConfig(CoreMod.PERMA_DEATH.get())) {
      final List<CharacterPermaDeathStatus> validCharacters = new ArrayList<>();

      //Load characters who haven't died and are available to put in party slots
      for(int i = 0; i < gameState_800babc8.charData_32c.length; i++) {
        final CharacterPermaDeathStatus character = new CharacterPermaDeathStatus(i);
        if(character.isAvailable && !character.hasDied) {
          validCharacters.add(character);
        }
      }

      //Go through the party slots to find out who died and should be removed
      for(int i = 0; i < gameState_800babc8.charIds_88.length; i++) {
        final int charId = gameState_800babc8.charIds_88[i];
        final CharacterPermaDeathStatus character = find(validCharacters, charId);
        if(character == null) {
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
        gameState_800babc8.charIds_88[0] = validCharacters.getFirst().charId;
      }

      return checkCharIds(true);
    }
    return true;
  }

  private static class CharacterPermaDeathStatus{
    public int charId;
    public boolean isAvailable;
    public boolean hasDied;
    public CharacterPermaDeathStatus(final int charId) {
      this.charId = charId;
      this.isAvailable = gameState_800babc8.charData_32c[charId].partyFlags_04 != 0;
      this.hasDied = Statistics.getStat(Math.abs(Statistics.Stats.TOTAL_DEATH.asInt()) + charId + 1) > 0;
    }
  }

  private static CharacterPermaDeathStatus find(final List<CharacterPermaDeathStatus> list, final int charId) {
    for(final CharacterPermaDeathStatus character : list) {
      if(character.charId == charId) {
        return character;
      }
    }
    return null;
  }

  private static void remove(final List<CharacterPermaDeathStatus> list, final int charId) {
    final CharacterPermaDeathStatus character = find(list, charId);
    if(character != null) {
      list.remove(character);
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
    if(charId != -1 && CONFIG.getConfig(CoreMod.PERMA_DEATH.get())) {
      final CharacterPermaDeathStatus character = new CharacterPermaDeathStatus(charId);
      return character.hasDied;
    }
    return false;
  }
}
