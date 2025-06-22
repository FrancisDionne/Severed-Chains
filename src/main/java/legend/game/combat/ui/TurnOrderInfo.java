package legend.game.combat.ui;

import legend.game.combat.bent.BattleEntity27c;

public class TurnOrderInfo {
  public BattleEntity27c bent;
  public boolean forcedTurn;

  public TurnOrderInfo(final BattleEntity27c bent, final boolean forcedTurn) {
    this.bent = bent;
    this.forcedTurn = forcedTurn;
  }
}
