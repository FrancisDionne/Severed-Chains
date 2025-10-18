package legend.game.saves;

import legend.game.inventory.screens.controls.Checkbox;
import legend.game.inventory.screens.HorizontalAlign;

import javax.annotation.Nullable;

/** Convenience class for simple bool-backed configs */
public class BoolConfigEntry extends ConfigEntry<Boolean> {
  public BoolConfigEntry(final boolean defaultValue, final ConfigStorageLocation storageLocation, final ConfigCategory category) {
    this(defaultValue, storageLocation, category, 0, null);
  }

  public BoolConfigEntry(final boolean defaultValue, final ConfigStorageLocation storageLocation, final ConfigCategory category, final double order) {
    this(defaultValue, storageLocation, category, order, null);
  }

  public BoolConfigEntry(final boolean defaultValue, final ConfigStorageLocation storageLocation, final ConfigCategory category, final Runnable callback) {
    this(defaultValue, storageLocation, category, 0, callback);
  }

  public BoolConfigEntry(final boolean defaultValue, final ConfigStorageLocation storageLocation, final ConfigCategory category, final double order, @Nullable final Runnable callback) {
    this(defaultValue, storageLocation, category, true, order, callback);
  }

  public BoolConfigEntry(final boolean defaultValue, final ConfigStorageLocation storageLocation, final ConfigCategory category, final boolean editable, final double order, @Nullable final Runnable callback) {
    super(
      defaultValue,
      storageLocation,
      category,
      BoolConfigEntry::serialize,
      bytes -> deserialize(bytes, defaultValue),
      order
    );

    if(editable) {
      this.setEditControl((current, gameState) -> {
        final Checkbox checkbox = new Checkbox();
        checkbox.setHorizontalAlign(HorizontalAlign.RIGHT);
        checkbox.setChecked(current, false);
        checkbox.onToggled(val -> {
          gameState.setConfig(this, val);
          if(callback != null) {
            callback.run();
            checkbox.setChecked(gameState.getConfig(this), true);
          }
        });
        return checkbox;
      });
    }
  }

  private static byte[] serialize(final boolean val) {
    return new byte[] {(byte)(val ? 1 : 0)};
  }

  private static boolean deserialize(final byte[] bytes, final boolean defaultValue) {
    if(bytes.length == 1) {
      if(bytes[0] == 1) {
        return true;
      }

      if(bytes[0] == 0) {
        return false;
      }
    }

    return defaultValue;
  }
}
