package legend.game.modding.coremod.config;

import legend.game.combat.ui.ControllerStyle;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;

public class ControllerStyleConfigEntry extends EnumConfigEntry<ControllerStyle> {
  public ControllerStyleConfigEntry() {
    super(ControllerStyle.class, ControllerStyle.PLAYSTATION, ConfigStorageLocation.GLOBAL, ConfigCategory.CONTROLS);
  }
}
