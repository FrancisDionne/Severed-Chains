package legend.game.combat.types;

import legend.game.combat.SEffe;

public class DragoonAdditionScriptData1c {
  public int unused_00;
  public int baseAngle_02;
  public int currentTick_04;
  public int unused_05;
  public int stepCountIndex_06;
  public int currentPressNumber_07;
  // public final int[] _08 = new int[5]; // Never used
  public int countEyeFlashTicks_0d;
  public int ticksUntilDeallocationAfterCompletion_0e;
  public int tickEffect_0f;
  public int meterSpinning_10;
  public int buttonPressGlowBrightnessFactor_11;
  public int ticksRemainingToBeginAddition_12;
  /** 0 = requires input, 1 = automatic, 2 = can't be started for some reason and will never be deallocated */
  public int inputMode_13;
  public int totalPressCount_14;
  public int charId_18;

  //Handles for Adjusted Dragoon Additions
  public float successWindowLowerBound;
  public float successWindowUpperBound;
  public float nextAngle;
  public float lastTickAngle;
  public boolean pressedThisCycle;

  public void setSuccessWindowBounds(final int[] successWindowArray) {
    final float threshold = 0f * 18f * SEffe.ONE_DEGREE;
    this.successWindowLowerBound = this.nextAngle - (SEffe.ONE_DEGREE * 18f);// - ((successWindowArray[this.stepCountIndex_06] - 1) * 2f * SEffe.ONE_DEGREE) - threshold;
    this.successWindowUpperBound = this.nextAngle + (SEffe.ONE_DEGREE * 18f);// + ((successWindowArray[this.stepCountIndex_06] - 1) * 2f * SEffe.ONE_DEGREE) + threshold;
  }
}
