package legend.game.modding.coremod.config;

import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;

public class AdditionGameplayEnhanceConfigEntry extends BoolConfigEntry {
  public AdditionGameplayEnhanceConfigEntry() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.ADDITIONS, 7);
  }
}
