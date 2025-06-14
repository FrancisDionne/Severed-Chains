package legend.game.modding.coremod.config;

import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;

public class TextGroupConfigEntry extends ConfigEntry<Void> {
  public TextGroupConfigEntry() {
    super(null, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, o -> new byte[0], bytes -> null, 7000, true);
  }
}