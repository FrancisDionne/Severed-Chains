package legend.game.types;

public enum EquipmentSlot {
  WEAPON,
  HELMET,
  ARMOUR,
  BOOTS,
  ACCESSORY,
  ;

  public static EquipmentSlot fromLegacy(final int slot) {
    return slot > -1 ? EquipmentSlot.values()[slot] : null;
  }
}
