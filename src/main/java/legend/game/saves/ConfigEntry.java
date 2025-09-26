package legend.game.saves;

import legend.game.i18n.I18n;
import legend.game.inventory.screens.Control;
import org.legendofdragoon.modloader.registries.RegistryEntry;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConfigEntry<T> extends RegistryEntry {
  public final T defaultValue;
  public final ConfigStorageLocation storageLocation;
  public final ConfigCategory category;
  public final Function<T, byte[]> serializer;
  public final Function<byte[], T> deserializer;
  public final double order;
  public final boolean header;
  public final Runnable callback;

  private BiFunction<T, ConfigCollection, Control> editControl;

  public ConfigEntry(final T defaultValue, final ConfigStorageLocation storageLocation, final ConfigCategory category, final Function<T, byte[]> serializer, final Function<byte[], T> deserializer) {
    this(defaultValue, storageLocation, category, serializer, deserializer, 0);
  }

  public ConfigEntry(@Nullable final T defaultValue, final ConfigStorageLocation storageLocation, final ConfigCategory category, final Function<T, byte[]> serializer, final Function<byte[], T> deserializer, final double order) {
    this(defaultValue, storageLocation, category, serializer, deserializer, order, false, null);
  }

  public ConfigEntry(@Nullable final T defaultValue, final ConfigStorageLocation storageLocation, final ConfigCategory category, final Function<T, byte[]> serializer, final Function<byte[], T> deserializer, final double order, @Nullable final Runnable callback) {
    this(defaultValue, storageLocation, category, serializer, deserializer, order, false, callback);
  }

  public ConfigEntry(@Nullable final T defaultValue, final ConfigStorageLocation storageLocation, final ConfigCategory category, final Function<T, byte[]> serializer, final Function<byte[], T> deserializer, final double order, final boolean header) {
    this(defaultValue, storageLocation, category, serializer, deserializer, order, header, null);
  }

  public ConfigEntry(@Nullable final T defaultValue, final ConfigStorageLocation storageLocation, final ConfigCategory category, final Function<T, byte[]> serializer, final Function<byte[], T> deserializer, final double order, final boolean header, @Nullable final Runnable callback) {
    this.defaultValue = defaultValue;
    this.storageLocation = storageLocation;
    this.category = category;
    this.serializer = serializer;
    this.deserializer = deserializer;
    this.order = order;
    this.header = header;
    this.callback = callback;
  }

  protected void setEditControl(final BiFunction<T, ConfigCollection, Control> editControl) {
    this.editControl = editControl;
  }

  public boolean hasEditControl() {
    return this.editControl != null;
  }

  public Control makeEditControl(final T value, final ConfigCollection config) {
    return this.editControl.apply(value, config);
  }

  public String getLabelTranslationKey() {
    return this.getTranslationKey("label");
  }

  public String getHelpTranslationKey() {
    return this.getTranslationKey("help");
  }

  public boolean hasHelp() {
    final String key = this.getHelpTranslationKey();
    final String text = I18n.translate(key);
    return text != null && !text.isEmpty() && !key.equals(text);
  }

  public boolean isAdvanced() {
    return false;
  }

  /**
   * Whether this config entry will appear in the battle options menu
   */
  public boolean availableInBattle() {
    return true;
  }

  public void onChange(final ConfigCollection configCollection, final T oldValue, final T newValue) {
    if(this.callback != null) {
      this.callback.run();
    }
  }
}
