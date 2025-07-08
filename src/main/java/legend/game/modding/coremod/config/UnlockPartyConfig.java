package legend.game.modding.coremod.config;

import legend.game.modding.coremod.CoreMod;
import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;

import static legend.core.GameEngine.CONFIG;

public class UnlockPartyConfig extends BoolConfigEntry {
  public UnlockPartyConfig() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, 5001, UnlockPartyConfig::callback);
  }

  @Override
  public boolean hasHelp() {
    return true;
  }

  private static void callback() {
    if(CONFIG.getConfig(CoreMod.PERMA_DEATH.get())) {
      CONFIG.setConfig(CoreMod.PERMA_DEATH.get(), true);
    }
  }
}
