package legend.game.modding.coremod.config;

import legend.core.Config;
import legend.core.IoHelper;
import legend.game.inventory.screens.BattleUIColourScreen;
import legend.game.inventory.screens.controls.NumberSpinner;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;
import legend.game.scripting.ScriptReadable;

public class BattleUIColourBlueConfigEntry extends ConfigEntry<Integer> implements ScriptReadable {
  public BattleUIColourBlueConfigEntry() {
    super(159, ConfigStorageLocation.CAMPAIGN, ConfigCategory.BATTLE_UI_COLOR, BattleUIColourBlueConfigEntry::serializer, BattleUIColourBlueConfigEntry::deserializer, 3, BattleUIColourBlueConfigEntry::callback);

    this.setEditControl((number, gameState) -> {
      final NumberSpinner<Integer> spinner = NumberSpinner.intSpinner(number, 0, 255);
      spinner.onChange(val -> gameState.setConfig(this, val));
      return spinner;
    });
  }

  private static byte[] serializer(final int val) {
    return new byte[] {(byte)(val)};
  }

  private static int deserializer(final byte[] data) {
    if(data.length == 1) {
      return IoHelper.readUByte(data, 0);
    }

    return 1;
  }

  private static void callback() {
    BattleUIColourScreen.dirty = true;
    Config.setChangeBattleUiColour(true);
  }
}
