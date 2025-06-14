package legend.game.modding.coremod.config;

import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;

public class BattleGroupConfigEntry extends ConfigEntry<Void> {
  public BattleGroupConfigEntry() {
    super(null, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, o -> new byte[0], bytes -> null, 3000, true);
  }
}
