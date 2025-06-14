package legend.game.modding.coremod.config;

import legend.game.combat.ui.FooterActionColor;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;

public class FooterActionColorConfigEntry extends EnumConfigEntry<FooterActionColor> {
  public FooterActionColorConfigEntry() {
    super(FooterActionColor.class, FooterActionColor.FOOTER_BROWN, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, 5001);
  }
}
