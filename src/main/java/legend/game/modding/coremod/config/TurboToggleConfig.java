package legend.game.modding.coremod.config;

import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;

public class TurboToggleConfig extends BoolConfigEntry {
  public TurboToggleConfig() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, 9009);
  }

  @Override
  public boolean hasHelp() {
    return true;
  }
}

