package legend.game.modding.coremod.config;

import legend.core.Random;
import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;

import java.util.ArrayList;
import java.util.List;

import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;

public class CharacterRandomModeConfig extends BoolConfigEntry {
  public CharacterRandomModeConfig() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.CHALLENGES, 3);
  }

  public static void setRandomParty() {
    final List<Integer> characters = new ArrayList<>();
    for(int i = 0; i < gameState_800babc8.charData_32c.length; i++) {
      if(PermaDeathConfigEntry.isAvailable(i) && !PermaDeathConfigEntry.hasDied(i) ) {
        characters.add(i);
      }
    }
    for(int i = 0; i < 3; i++) {
      if(!characters.isEmpty()) {
        final int randomValue = new Random().nextInt(characters.size());
        gameState_800babc8.charIds_88[i] = characters.get(randomValue);
        characters.remove(randomValue);
      } else {
        gameState_800babc8.charIds_88[i] = -1;
      }
    }
  }
}
