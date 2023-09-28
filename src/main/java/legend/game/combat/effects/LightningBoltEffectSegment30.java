package legend.game.combat.effects;

import legend.core.gte.USCOLOUR;
import org.joml.Vector3f;

public class LightningBoltEffectSegment30 {
  public final int index;

  public final Vector3f origin_00 = new Vector3f();
  /** Narrower gradient of bolt effect, renders below outer */
  public final USCOLOUR innerColour_10 = new USCOLOUR();
  /** Wider gradient of bolt effect, renders above inner */
  public final USCOLOUR outerColour_16 = new USCOLOUR();
  public final USCOLOUR innerColourFadeStep_1c = new USCOLOUR();
  public final USCOLOUR outerColourFadeStep_22 = new USCOLOUR();
  /** ubyte */
  public float scaleMultiplier_28;

  /** short; Incremented while rendering, but never used for anything. */
  public float unused_2a;
  /** short */
  public float originTranslationMagnitude_2c;
  /** Was 8-bit fixed-point short */
  public float baseVertexTranslationScale_2e;

  public LightningBoltEffectSegment30(final int index) {
    this.index = index;
  }
}
