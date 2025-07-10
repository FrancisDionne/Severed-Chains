package legend.game.inventory.screens.controls;

import legend.game.saves.Campaign;
import legend.game.saves.SavedGame;

import javax.annotation.Nullable;

public class SaveCardData {
  public Campaign campaign;
  public SavedGame saveGame;
  public SaveCardData(final Campaign campaign, @Nullable final SavedGame saveGame) {
    this.campaign = campaign;
    this.saveGame = saveGame;
  }
  public String campaignName() {
    return this.campaign.name;
  }
  public String saveName() {
    return this.saveGame.saveName;
  }
}
