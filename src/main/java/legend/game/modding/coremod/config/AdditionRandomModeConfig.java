package legend.game.modding.coremod.config;

import legend.core.Random;
import legend.game.combat.ui.AdditionListMenu;
import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.types.CharacterData2c;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;

public class AdditionRandomModeConfig extends BoolConfigEntry {
  public AdditionRandomModeConfig() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.ADDITIONS, 8);
  }

  public static int getRandomAddition(final int charId) {
    final HashMap<Integer, String> additions = AdditionListMenu.getAdditions(charId);
    int additionIndex = -1;

    if(additions.size() > 1) {
      List<RandomAdditionBagEntry> bag = new ArrayList<>();
      final CharacterData2c charData = gameState_800babc8.charData_32c[charId];
      final boolean balancedOdds = new Random().nextInt(100) < 70;
      int highestXp = 0;

      for(final int additionSlot : additions.keySet()) {
        bag.add(new RandomAdditionBagEntry(additionSlot, charData.additionXp_22[additionSlot]));
        highestXp = Math.max(highestXp, charData.additionXp_22[additionSlot]);
      }

      bag = bag.stream().sorted(Comparator.comparingInt(o -> o.weight)).toList();

      int sum = 0;
      int lastXp = -1;
      int lastWeight = 0;
      int currentWeight = 0;
      for(int i = bag.size() - 1; i >= 0; i--) {
        final RandomAdditionBagEntry entry = bag.get(i);
        if(balancedOdds) {
          if(entry.weight != lastXp) {
            lastXp = entry.weight;
            currentWeight++;
            final float modifier = (1 + (float)(additions.size() - i) / additions.size()) * 0.75f;
            entry.weight = Math.max(1, Math.round(currentWeight * modifier));
          } else {
            entry.weight = lastWeight;
          }
          lastWeight = entry.weight;
        } else {
          entry.weight = 1;
        }
        sum += entry.weight;
      }

      final int randomValue = new Random().nextInt(sum);
      currentWeight = 0;
      for(final RandomAdditionBagEntry entry : bag) {
        if(randomValue < currentWeight + entry.weight) {
          additionIndex = entry.additionSlot;
          break;
        }
        currentWeight += entry.weight;
      }
    }
    return additionIndex;
  }

  private static class RandomAdditionBagEntry {
    public final int additionSlot;
    public int weight;
    private RandomAdditionBagEntry(final int additionSlot, final int weight) {
      this.additionSlot = additionSlot;
      this.weight = weight;
    }
  }
}
