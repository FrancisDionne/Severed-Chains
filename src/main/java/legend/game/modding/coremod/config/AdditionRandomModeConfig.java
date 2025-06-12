package legend.game.modding.coremod.config;

import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;

public class AdditionRandomModeConfig extends BoolConfigEntry {
  public AdditionRandomModeConfig() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.ADDITIONS);
  }

  @Override
  public boolean hasHelp() {
    return true;
  }
}
