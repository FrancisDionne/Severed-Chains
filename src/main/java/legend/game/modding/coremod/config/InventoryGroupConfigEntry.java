package legend.game.modding.coremod.config;

import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;

public class InventoryGroupConfigEntry extends ConfigEntry<Void> {
  public InventoryGroupConfigEntry() {
    super(null, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, o -> new byte[0], bytes -> null, 5000, true);
  }
}