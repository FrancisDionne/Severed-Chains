package legend.lodmod.equipment;

import legend.game.characters.ElementSet;
import legend.game.inventory.Equipment;
import legend.game.scripting.Param;
import legend.lodmod.LodMod;

public class DetonateArrowEquipment extends Equipment {
  public DetonateArrowEquipment(final int equipmentId, final int price) {
    super(equipmentId, price, 0x8, 0x80, 0x80, 0x2, LodMod.NO_ELEMENT.get(), 0, new ElementSet(), new ElementSet(), 0, 0, 50, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0);
  }

  @Override
  public void read(final int index, final Param out) {
    switch(index) {
      case 1000 -> out.set(1);
      default -> super.read(index, out);
    }
  }
}
