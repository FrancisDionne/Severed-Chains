package legend.game.modding.coremod.config;

import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;

public class IronmanModeConfigEntry extends BoolConfigEntry {
  public IronmanModeConfigEntry() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.CHALLENGES, 2);
  }
}
