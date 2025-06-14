package legend.game.modding.coremod.config;

import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;

public class MiscGroupConfigEntry extends ConfigEntry<Void> {
  public MiscGroupConfigEntry() {
    super(null, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, o -> new byte[0], bytes -> null, 9000, true);
  }
}
