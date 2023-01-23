package legend.game.types;

public class SpuStruct124 {
  /** ubyte Upper nibble is message, lower nibble is channel */
  public int command_000;
  public int previousCommand_001;
  /** ubyte */
  public int param0_002;
  /** ubyte */
  public int param1_003;

  /** ubyte */
  public int param2_005;

  public int sssqOffset_00c;
  /** Can either be a full SSSQ file, or just a sequence */
  public long sssqPtr_010;

  public int _018;

  /** ubyte */
  public int _01e;

  /** ushort */
  public int playableSoundIndex_020;
  /** ushort */
  public int sequenceIndex_022;
  /** ushort */
  public int patchIndex_024;
  /** ubyte */
  public int _026;
  /** ubyte */
  public int _027;
  /** ubyte */
  public int _028;
  /** ubyte */
  public int _029;
  /** ubyte */
  public int _02a;

  public int sssqOffset_02c;

  /** ubyte */
  public int _035;

  /** If set, {@link #command_000} will get set to {@link #_039} at the end of this sound's tick */
  public boolean overrideCommand_037;

  /** ubyte */
  public int _039;
  /** ubyte */
  public int _03a;

  /** ubyte */
  public int _03c;

  public final int[][] _03e = new int[10][16];
  /** Was two ushorts */
  public int keyOn_0de;
  /** Was two ushorts */
  public int keyOff_0e2;
  /** ubyte */
  public int _0e6;
  /** ubyte */
  public int _0e7;
  /** ubyte */
  public int _0e8;
  /** ushort */
  public int pitchShifted_0e9;
  /** ushort */
  public int reverbEnabled_0ea;
  /** 12-bit fixed-point short - 0x1000 is normal pitch */
  public int pitch_0ec;
  /** 12-bit fixed-point short */
  public int pitchShiftVolLeft_0ee;
  /** 12-bit fixed-point short */
  public int pitchShiftVolRight_0f0;

  /** ubyte */
  public int _104;
  /** ubyte */
  public int _105;

  /** ushort */
  public int tempo_108;
  /** ushort */
  public int deltaTime_10a;
  /** ubyte */
  public int _10c;

  public int _110;
  public int _114;
  public int _118;
  /** ubyte */
  public int _11c;
  /** ubyte */
  public int _11d;
  /** ubyte */
  public int _11e;
  /** ubyte */
  public int _11f;
  /** ubyte */
  public int _120;

  /** ushort */
  public int _122;
}
