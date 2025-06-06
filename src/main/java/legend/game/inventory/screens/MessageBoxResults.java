package legend.game.inventory.screens;

import legend.game.types.MessageBoxResult;

public class MessageBoxResults {
  public MessageBoxResult messageBoxResult;
  public int quantity;

  MessageBoxResults(final MessageBoxResult result) {
    this.messageBoxResult = result;
  }

  MessageBoxResults(final MessageBoxResult result, final int quantity) {
    this.messageBoxResult = result;
    this.quantity = quantity;
  }
}