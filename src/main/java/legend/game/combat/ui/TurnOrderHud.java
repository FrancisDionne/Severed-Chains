package legend.game.combat.ui;

import legend.core.gpu.Bpp;
import legend.core.opengl.Obj;
import legend.core.opengl.QuadBuilder;
import legend.game.combat.bent.BattleEntity27c;
import legend.game.combat.bent.MonsterBattleEntity;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.TextColour;
import legend.game.scripting.ScriptState;
import org.joml.Matrix4f;

import java.util.List;

import static legend.core.GameEngine.RENDERER;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;

public class TurnOrderHud {
  private final BattleHud battleHud;
  private final FontOptions font = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.GREY);
  private final Matrix4f m = new Matrix4f();
  private final Obj quad = new QuadBuilder("Turn Order HUD")
    .size(1.0f, 1.0f)
    .uv(0.0f, 0.0f)
    .uvSize(1.0f, 1.0f)
    .bpp(Bpp.BITS_24)
    .build();

  public TurnOrderHud(final BattleHud battleHud) {
    this.battleHud = battleHud;
  }

  public void render() {
    final List<ScriptState<? extends BattleEntity27c>> bents = battleState_8006e398.getNextTurnBents(7, false);
    final int xOffset = (int)RENDERER.getWidescreenOrthoOffsetX();
    for(int i = 0; i < bents.size(); i++) {
      final BattleEntity27c bent = bents.get(i).innerStruct_00;

      String bentName = null;
      if(bent instanceof final PlayerBattleEntity player) {
        bentName = BattleHud.playerNames_800fb378[player.charId_272];
      } else if(bent instanceof final MonsterBattleEntity monster) {
        bentName = this.battleHud.getTargetEnemyName(monster, this.battleHud.battle.currentEnemyNames_800c69d0[monster.combatantIndex_26c]);
      }

      if(bentName != null) {
        renderText((i + 1) + ": " + bentName, -xOffset + 10, 5 + (i * 15), this.font);
      }

      //RENDERER
      //  .queueOrthoModel(quad, m, QueuedModelStandard.class)
      //  .texture(footAction.secondaryTexture);
    }

  }
}

