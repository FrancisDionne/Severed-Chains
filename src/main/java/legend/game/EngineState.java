package legend.game;

import legend.game.scripting.FlowControl;
import legend.game.scripting.RunningScript;
import legend.game.models.CContainer;
import legend.game.models.Model124;

import java.util.function.Function;

public abstract class EngineState {
  private final Function<RunningScript, FlowControl>[] functions = new Function[1024];

  /** Runs before scripts are ticked */
  public abstract void tick();

  /** Runs after scripts are ticked */
  public void postScriptTick() {

  }

  /** Runs after everything else is rendered */
  public void overlayTick() {

  }

  /** The amount we've multiplied this engine state's frame rate by (e.g. world map was 20FPS, we multiplied it by 3 to bring it to 60FPS) */
  public int tickMultiplier() {
    return 2;
  }

  public void restoreMusicAfterMenu() {

  }

  public Function<RunningScript, FlowControl>[] getScriptFunctions() {
    return this.functions;
  }

  public void modelLoaded(final Model124 model, final CContainer cContainer) {

  }

  public void menuClosed() {

  }

  public boolean renderTextOnTopOfAllBoxes() {
    return true;
  }

  public boolean allowsWidescreen() {
    return true;
  }

  public boolean allowsHighQualityProjection() {
    return true;
  }
}
