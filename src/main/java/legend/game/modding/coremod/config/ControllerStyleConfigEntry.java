package legend.game.modding.coremod.config;

import legend.core.platform.input.InputGamepadType;
import legend.game.combat.ui.ControllerStyle;
import legend.game.modding.coremod.CoreMod;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.PLATFORM;

public class ControllerStyleConfigEntry extends EnumConfigEntry<ControllerStyle> {
  public ControllerStyleConfigEntry() {
    super(ControllerStyle.class, ControllerStyle.PLAYSTATION, ConfigStorageLocation.GLOBAL, ConfigCategory.CONTROLS);
  }

  public ControllerStyle getStyle() {
    final ControllerStyle style = CONFIG.getConfig(CoreMod.CONTROLLER_STYLE_CONFIG.get());
    if (style == ControllerStyle.AUTO) {
      final InputGamepadType gamepadType = PLATFORM.getGamepadType();
      if (gamepadType == InputGamepadType.STANDARD || gamepadType == InputGamepadType.PLAYSTATION) return ControllerStyle.PLAYSTATION;
      else if (gamepadType == InputGamepadType.XBOX_360 || gamepadType == InputGamepadType.XBOX_ONE) return ControllerStyle.XBOX;
      else if (gamepadType == InputGamepadType.SWITCH) return ControllerStyle.NINTENDO;
     }
    return style;
  }
}
