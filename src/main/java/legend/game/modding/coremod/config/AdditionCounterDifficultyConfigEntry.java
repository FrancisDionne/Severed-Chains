package legend.game.modding.coremod.config;

import legend.game.combat.AdditionCounterDifficulty;
import legend.game.modding.coremod.CoreMod;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;

import static legend.core.GameEngine.CONFIG;

public class AdditionCounterDifficultyConfigEntry extends EnumConfigEntry<AdditionCounterDifficulty> {
  public AdditionCounterDifficultyConfigEntry() {
    super(AdditionCounterDifficulty.class, AdditionCounterDifficulty.NORMAL, ConfigStorageLocation.CAMPAIGN, ConfigCategory.ADDITIONS, 7);
  }

  public static int adjustHitCounterFrameThreshold(final int threshold) {
    if(threshold > 0) {
      return switch(CONFIG.getConfig(CoreMod.ADDITION_COUNTER_DIFFICULTY_CONFIG.get())) {
        case AdditionCounterDifficulty.NONE -> 0;
        //case AdditionCounterDifficulty.EASIER -> Math.round(threshold * 1.75f);
        case AdditionCounterDifficulty.EASY -> Math.round(threshold * 1.5f);
        case AdditionCounterDifficulty.HARD -> Math.round(threshold * 0.375f);
        //case AdditionCounterDifficulty.HARDER -> Math.round(threshold * 0.125f);
        default -> threshold;
      };
    }
    return threshold;
  }
}
