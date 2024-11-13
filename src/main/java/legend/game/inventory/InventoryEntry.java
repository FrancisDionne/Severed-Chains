package legend.game.inventory;

public interface InventoryEntry {
  int getIcon();
  String getNameTranslationKey();
  String getDescriptionTranslationKey();
  int getPrice();
  int getQuantity();
  void setQuantity(final int quantity);
}
