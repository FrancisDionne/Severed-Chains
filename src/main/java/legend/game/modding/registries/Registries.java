package legend.game.modding.registries;

import legend.game.characters.Element;
import legend.game.characters.ElementRegistry;
import legend.game.characters.ElementRegistryEvent;
import legend.game.characters.StatType;
import legend.game.characters.StatTypeRegistry;
import legend.game.characters.StatTypeRegistryEvent;
import legend.game.combat.bobj.BattleObjectType;
import legend.game.combat.bobj.BattleObjectTypeRegistry;
import legend.game.combat.bobj.BattleObjectTypeRegistryEvent;
import legend.game.inventory.Equipment;
import legend.game.inventory.EquipmentRegistry;
import legend.game.inventory.EquipmentRegistryEvent;
import legend.game.inventory.Item;
import legend.game.inventory.ItemRegistry;
import legend.game.inventory.ItemRegistryEvent;
import legend.game.inventory.Spell;
import legend.game.inventory.SpellRegistry;
import legend.game.inventory.SpellRegistryEvent;
import legend.game.modding.events.EventManager;
import legend.game.modding.events.registries.RegistryEvent;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigRegistry;
import legend.game.saves.ConfigRegistryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Registries {
  private final List<MutableRegistry<?>> registries = new ArrayList<>();
  private final List<Function<MutableRegistry<?>, RegistryEvent.Register<?>>> registryEvents = new ArrayList<>();

  public final Registry<StatType<?>> stats = this.addRegistry(new StatTypeRegistry(), StatTypeRegistryEvent::new);
  public final Registry<Element> elements = this.addRegistry(new ElementRegistry(), ElementRegistryEvent::new);
  public final Registry<BattleObjectType> battleObjectTypes = this.addRegistry(new BattleObjectTypeRegistry(), BattleObjectTypeRegistryEvent::new);
  public final Registry<Item> items = this.addRegistry(new ItemRegistry(), ItemRegistryEvent::new);
  public final Registry<Equipment> equipment = this.addRegistry(new EquipmentRegistry(), EquipmentRegistryEvent::new);
  public final Registry<Spell> spell = this.addRegistry(new SpellRegistry(), SpellRegistryEvent::new);
  public final Registry<ConfigEntry<?>> config = this.addRegistry(new ConfigRegistry(), ConfigRegistryEvent::new);

  private <Type extends RegistryEntry> Registry<Type> addRegistry(final Registry<Type> registry, final Function<MutableRegistry<Type>, RegistryEvent.Register<Type>> registryEvent) {
    this.registries.add((MutableRegistry<Type>)registry);
    //noinspection unchecked
    this.registryEvents.add((Function<MutableRegistry<?>, RegistryEvent.Register<?>>)(Object)registryEvent);
    return registry;
  }

  public class Access {
    public void initialize() {
      for(int i = 0; i < Registries.this.registries.size(); i++) {
        final MutableRegistry<?> registry = Registries.this.registries.get(i);
        EventManager.INSTANCE.postEvent(Registries.this.registryEvents.get(i).apply(registry));
        registry.lock();
      }
    }
  }
}
