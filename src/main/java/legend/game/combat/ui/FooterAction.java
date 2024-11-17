package legend.game.combat.ui;

import legend.game.input.InputAction;

public class FooterAction {
  public FooterActions action;
  public InputAction input;
  public FooterAction(final FooterActions action, final InputAction input) {
    this.action = action;
    this.input = input;
  }
}
