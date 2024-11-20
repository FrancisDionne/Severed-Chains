package legend.game.inventory.screens;

import legend.core.GameEngine;
import legend.game.i18n.I18n;
import legend.game.input.InputAction;
import legend.game.inventory.screens.controls.Background;
import legend.game.inventory.screens.controls.Label;
import legend.game.modding.coremod.CoreMod;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static legend.game.Scus94491BpeSegment.startFadeEffect;
import static legend.game.Scus94491BpeSegment_8002.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;

public class AdditionSettingsScreen extends VerticalLayoutScreen {
  private final Runnable unload;

  public AdditionSettingsScreen(final ConfigCollection config, final Set<ConfigStorageLocation> validLocations, final ConfigCategory category, final Runnable unload) {
    deallocateRenderables(0xff);
    startFadeEffect(2, 10);

    this.unload = unload;

    this.addControl(new Background());

    final Map<RegistryId, String> translations = new HashMap<>();

    for(final RegistryId configId : GameEngine.REGISTRIES.config) {
      final ConfigEntry<?> entry = GameEngine.REGISTRIES.config.getEntry(configId).get();

      if(entry.category == category) {
        translations.put(configId, I18n.translate(configId.modId() + ".config." + configId.entryId() + ".label"));
      }
    }

    translations.entrySet().stream()
      .sorted(Comparator.comparingInt(o -> CoreMod.configOrder.indexOf(o.getKey().toString())))
      .forEach(entry -> {
        final RegistryId configId = entry.getKey();
        final String label = entry.getValue();

        //noinspection rawtypes
        final ConfigEntry configEntry = GameEngine.REGISTRIES.config.getEntry(configId).get();

        if(validLocations.contains(configEntry.storageLocation)) {
          //noinspection unchecked
          if (configEntry.hasEditControl()) {
            this.addRow(label, configEntry.makeEditControl(config.getConfig(configEntry), config)).setZ(35);
          } else {
            this.addRow(label, new Label("")).setZ(35);
          }
        }
      });
  }

  @Override
  public InputPropagation pressedThisFrame(final InputAction inputAction) {
    if(super.pressedThisFrame(inputAction) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(inputAction == InputAction.BUTTON_EAST) {
      playMenuSound(3);
      this.unload.run();
      return InputPropagation.HANDLED;
    }

    return InputPropagation.PROPAGATE;
  }
}