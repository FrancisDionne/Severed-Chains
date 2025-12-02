package legend.game.combat;

import legend.core.GameEngine;
import legend.game.additions.Addition;
import legend.game.additions.AdditionHitProperties10;
import legend.game.additions.AdditionHits80;
import legend.game.additions.AdditionSound;
import legend.game.additions.LevelLockedAddition;
import legend.game.additions.MasterAddition;
import legend.game.additions.SimpleAddition;
import legend.game.combat.ui.AdditionTimingOffset;
import legend.game.modding.coremod.CoreMod;
import org.legendofdragoon.modloader.registries.RegistryId;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.REGISTRIES;

public final class AdditionConfigs {
  public static final String[] additionNames_800fa8d4 = {
    "Double Slash", "Volcano", "Burning Rush", "Crush Dance", "Madness Hero", "Moon Strike", "Blazing Dynamo", "", // Dart
    "Harpoon", "Spinning Cane", "Rod Typhoon", "Gust of Wind Dance", "Flower Storm", "", // Lavitz
    "Whip Smack", "More & More", "Hard Blade", "Demon's Dance", "", // Rose
    "Pursuit", "Inferno", "Bone Crush", "", // Kongol
    "Double Smack", "Hammer Spin", "Cool Boogie", "Cat's Cradle", "Perky Step", "", // Meru
    "Double Punch", "Ferry of Styx", "Summon 4 Gods", "5-Ring Shattering", "Hex Hammer", "Omni-Sweep", // Haschel
    "Harpoon", "Spinning Cane", "Rod Typhoon", "Gust of Wind Dance", "Flower Storm" // Albert
  };

  private static boolean fc; //Frame Correction (shorthanded to keep the ternaries in the createAdditionHits calls lean). Main use is to make the No HUD option on Additions feel way more intuitive

  private AdditionConfigs() {
  }

  private static void debugCompareScript() {
    System.out.println("============TEST=============");
    for(final RegistryId a : GameEngine.REGISTRIES.additions) {
      final Addition addition = GameEngine.REGISTRIES.additions.getEntry(a).get();
      System.out.println(a.entryId());
      for(int i = 0; i < addition.getHitCount(null, null); i++) {
        final AdditionHitProperties10 hit = addition.getHit(null, null, i);
        System.out.println(hit.toString());
        for(final AdditionSound sound : hit.sounds) {
          System.out.println(sound.soundIndex + "," + sound.initialDelay);
        }
      }
    }
    System.out.println("=============================");
  }

  public static void load() {
    fc = CONFIG.getConfig(CoreMod.ADDITION_TIMING_MODE_CONFIG.get()) == AdditionTimingMode.ADJUSTED;
    //REGISTRIES.reloadAdditions();
  }

  private static AdditionHitProperties10[] createAdditionHitPropertiesArray(final int hitCount) {
    final AdditionHitProperties10[] additionHitProperties = new AdditionHitProperties10[hitCount];
    for (int i = 0; i < hitCount; i++) {
      additionHitProperties[i] = new AdditionHitProperties10(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
    return additionHitProperties;
  }

  // totalFrames: length of the animation, when done, the next animation and overlay starts
  // overlayHitFrameOffset: the speed at which the overlay closes in (bigger = slower)
  // totalSuccessFrames: the total amount of frames that will be valid for a successful input
  // overlayStartingFrameOffset: frames before the overlay shows
  private static AdditionHitProperties10 createAdditionHitProperties(final int flags, int totalFrames, final int overlayHitFrameOffset, int totalSuccessFrames, final int damageMultiplier, final int spValue, final int audioFile, final int isFinalHit, final int _08, final int _09, final int _0a, final int hitDistanceFromTarget, final int framesToHitPosition, final int _0d, final int _0e, final int overlayStartingFrameOffset, final AdditionSound... sounds) {
    switch (CONFIG.getConfig(CoreMod.ADDITION_DIFFICULTY_CONFIG.get())) {
      case AdditionDifficulty.EASY: totalSuccessFrames += 1; break;
      case AdditionDifficulty.HARD:
        if (totalSuccessFrames > 2) {
          totalFrames = totalFrames - (totalSuccessFrames - 2);
          totalSuccessFrames = 2;
        }
        break;
    }
    totalFrames = Math.max(1, totalFrames);
    totalSuccessFrames = Math.max(1, totalSuccessFrames);
    return new AdditionHitProperties10(flags, totalFrames, overlayHitFrameOffset, totalSuccessFrames, damageMultiplier, spValue, audioFile, isFinalHit, _08, _09, _0a, hitDistanceFromTarget, framesToHitPosition, _0d, _0e, overlayStartingFrameOffset, sounds);
  }

  private static void adjustHitOverlayStartingOffset(final AdditionHitProperties10[] additionHitProperties) {
      int overlayStartingFrameOffset = additionHitProperties[0].overlayStartingFrameOffset_0f;
      switch (CONFIG.getConfig(CoreMod.ADDITION_TIMING_OFFSET_CONFIG.get())) {
        case AdditionTimingOffset.SLOW: overlayStartingFrameOffset += 1; break;
        case AdditionTimingOffset.SLOWER: overlayStartingFrameOffset += 2; break;
        case AdditionTimingOffset.FAST: overlayStartingFrameOffset -= 1; break;
        case AdditionTimingOffset.FASTER: overlayStartingFrameOffset -= 2; break;
        default: return;
      }
      additionHitProperties[0].overlayStartingFrameOffset_0f = Math.max(0, overlayStartingFrameOffset);
  }

  public static LevelLockedAddition createAdditionHits_Dart_DoubleSlash() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(2);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 100, 30, 0, 0, 0, 0, 8, 5, 12, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 19, 11, 2, 50, 5, 0, 4, 0, 8, 3, 3, 10, 32, 0, 0, new AdditionSound(5, 11), new AdditionSound(3, 9));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4031, 1, 150, 
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.05f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.10f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.35f, 1.00f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Dart_Volcano() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(4);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 50, 5, 1, 0, 0, 0, 8, 5, 12, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 33, 27, 3, 50, 5, 0, 0, 0, 25, 2, 1, 12, 32, 0, 0, new AdditionSound(5, 28), new AdditionSound(1, 26));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 30, fc ? 14 : 15, 3, 50, 5, 0, 0, 0, 0, 0, 0, 13, 32, 0, 0, new AdditionSound(9, 15), new AdditionSound(2, 13));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 23, 5, 2, 50, 5, 0, 4, -5, 0, 12, 5, 12, 32, 0, 0, new AdditionSound(10, 5), new AdditionSound(3, 3));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4032, 2, 200,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.05f, 1.20f),
        new SimpleAddition.LevelMultipliers(1.10f, 1.40f),
        new SimpleAddition.LevelMultipliers(1.15f, 1.60f),
        new SimpleAddition.LevelMultipliers(1.25f, 1.80f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Dart_BurningRush() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(3);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 50, 10, 2, 0, 0, 0, 8, 5, 12, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 27, 12, 3, 50, 10, 0, 0, 0, 0, 27, 8, 10, 32, 0, 0, new AdditionSound(13, 12), new AdditionSound(1, 10));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 22, 8, 2, 50, 10, 0, 4, -8, 5, 17, 8, 8, 32, 0, 0, new AdditionSound(9, 8), new AdditionSound(3, 6));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4033, 8, 150,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.00f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.00f, 2.00f),
        new SimpleAddition.LevelMultipliers(1.00f, 2.50f),
        new SimpleAddition.LevelMultipliers(1.00f, 3.40f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Dart_CrushDance() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(5);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 30, 10, 3, 0, 0, 0, 8, 5, 12, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 28, 22, 3, 30, 10, 0, 0, 0, 0, 12, 6, 11, 32, 0, 0, new AdditionSound(7, 23), new AdditionSound(1, 21));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 26, 14, 3, 30, 10, 0, 0, 0, 0, 0, 0, 12, 32, 0, 0, new AdditionSound(9, 14), new AdditionSound(3, 12));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 21, 14, 3, 30, 10, 0, 0, 0, 11, 4, 0, 15, 32, 0, 0, new AdditionSound(5, 14), new AdditionSound(2, 12));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 24, 8, 2, 30, 10, 0, 4, -5, 5, 15, 10, 11, 32, 0, 0, new AdditionSound(10, 8), new AdditionSound(3, 6));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4034, 15, 150,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.15f, 1.25f),
        new SimpleAddition.LevelMultipliers(1.30f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.45f, 1.75f),
        new SimpleAddition.LevelMultipliers(1.67f, 2.00f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Dart_MadnessHero() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 20, 10, 4, 0, 0, 0, 8, 5, 12, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 15, 9, 3, 20, 10, 0, 0, 0, 0, 0, 0, 11, 32, 0, 0, new AdditionSound(5, 10), new AdditionSound(2, 8));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 8, 2, 3, 20, 10, 0, 0, 0, 0, 0, 0, 10, 32, 0, fc ? 1 : 0, new AdditionSound(7, 3), new AdditionSound(3, 1));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 8, 2, 3, 20, 10, 0, 0, 0, 0, 0, 0, 7, 32, 0, fc ? 1 : 0, new AdditionSound(8, 3), new AdditionSound(3, 1));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 8, 2, 3, 10, 10, 0, 0, 0, 0, 0, 0, 12, 32, 0, 0, new AdditionSound(5, 3), new AdditionSound(3, 1));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 10, 2, 2, 10, 10, 0, 4, 0, 0, 0, 0, 11, 32, 0, 0, new AdditionSound(9, 2), new AdditionSound(3, 0));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4035, 22, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.00f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.00f, 2.00f),
        new SimpleAddition.LevelMultipliers(1.00f, 2.50f),
        new SimpleAddition.LevelMultipliers(1.00f, 3.40f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Dart_MoonStrike() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(7);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 30, 8, 5, 0, 0, 0, 8, 5, 12, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 25, 19, 3, 30, 2, 0, 0, 0, 16, 3, 7, 9, 32, 0, 0, new AdditionSound(7, 20), new AdditionSound(2, 18));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 14, 8, 3, 30, 2, 0, 0, 0, 0, 0, 0, 10, 32, 0, 0, new AdditionSound(8, 9), new AdditionSound(1, 7));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 15, 10, 2, 30, 2, 0, 0, 0, 0, 0, 0, 9, 32, 0, fc ? 1 : 0, new AdditionSound(7, 11), new AdditionSound(3, 9));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 12, 6, 3, 30, 2, 0, 0, 0, 4, 1, 1, 14, 32, 0, 0, new AdditionSound(4, 7), new AdditionSound(2, 5));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 12, 6, 3, 30, 2, 0, 0, 0, 0, 0, 0, 14, 32, 0, 0, new AdditionSound(5, 7), new AdditionSound(2, 5));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 59, 12, 2, 20, 2, 0, 4, -5, 0, 11, 3, 13, 32, 0, 0, new AdditionSound(10, 12), new AdditionSound(3, 10), new AdditionSound(12, 40));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4036, 29, 200,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.40f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.60f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.75f, 1.00f)
      }, additionHitProperties);
  }

  public static MasterAddition createAdditionHits_Dart_BlazingDynamo() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(8);
    additionHitProperties[0] = createAdditionHitProperties(0xe0, 14, 9, 3, 40, 20, 6, 0, 0, 0, 8, 5, 12, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 23, 17, 3, 30, 20, 0, 0, 0, 0, 0, 0, 9, 32, 0, fc ? 1 : 0, new AdditionSound(8, 18), new AdditionSound(1, 16));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 10, 5, 2, 30, 10, 0, 0, 0, 3, 2, 1, 7, 32, 0, fc ? 2 : 0, new AdditionSound(5, 6), new AdditionSound(2, 4));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 12, fc ? 5 : 6, 3, 30, 10, 0, 0, 0, 0, 0, 0, 9, 32, 0, 0, new AdditionSound(7, 7), new AdditionSound(1, 5));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 20, fc ? 13 : 14, 3, 30, 10, 0, 0, 0, 0, 13, 4, 13, 32, 0, 0, new AdditionSound(5, 15), new AdditionSound(3, 13));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 12, 7, 2, 30, 10, 0, 0, 0, 0, 0, 0, 11, 32, 24, 0, new AdditionSound(9, 8), new AdditionSound(1, 6));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 29, fc ? 8 : 9, 3, 30, 10, 0, 0, 3, 7, 11, 5, 10, 32, 0, 0, new AdditionSound(11, 9), new AdditionSound(2, 7));
    additionHitProperties[7] = createAdditionHitProperties(0xc0, 23, 17, 2, 30, 10, 0, 4, -8, 0, 16, 3, 13, 32, 0, 0, new AdditionSound(10, 17), new AdditionSound(3, 15));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new MasterAddition(4037, 250,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.10f),
        new SimpleAddition.LevelMultipliers(1.40f, 1.20f),
        new SimpleAddition.LevelMultipliers(1.60f, 1.30f),
        new SimpleAddition.LevelMultipliers(1.80f, 1.50f)
      }, additionHitProperties);
  }

  public static AdditionHits80 createAdditionHits_Unknown1() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 5, 0, 0, 100, 0, 7, 0, 0, 0, 4, 1, 8, 32, 0, 11, new AdditionSound(5, 11), new AdditionSound(0, 9));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 15, 4, 0, 10, 0, 0, 0, 0, 0, 14, 0, 7, 32, 0, 0, new AdditionSound(8, 13), new AdditionSound(3, 11));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 16, 10, 0, 20, 0, 0, 0, 0, 6, 8, 1, 10, 32, 0, 0, new AdditionSound(5, 11), new AdditionSound(0, 9));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 11, 5, 0, 30, 0, 0, 0, 0, 4, 6, 0, 9, 32, 0, 0, new AdditionSound(9, 16), new AdditionSound(1, 14));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 19, 5, 0, 40, 0, 0, 0, 0, 0, 18, 0, 9, 32, 0, 0, new AdditionSound(10, 20), new AdditionSound(3, 18), new AdditionSound(7, 4), new AdditionSound(7, 10), new AdditionSound(5, 11), new AdditionSound(0, 9));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 68, 39, 0, 0, 0, 0, 0, 0, 0, 67, 0, 11, 32, 0, 0, new AdditionSound(5, 11), new AdditionSound(0, 9));
    adjustHitOverlayStartingOffset(additionHitProperties);
    return new AdditionHits80(additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Lavitz_Harpoon() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(2);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 17, 11, 3, 75, 30, 8, 0, 0, 2, 5, 7, 16, 32, 0, fc ? 10 : 11, new AdditionSound(5, 11), new AdditionSound(0, 9));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 25, 13, 2, 25, 5, 0, 4, -8, 0, 8, 12, 14, 32, 0, 0, new AdditionSound(8, 13), new AdditionSound(3, 11));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4039, 1, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.10f, 1.10f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.20f),
        new SimpleAddition.LevelMultipliers(1.30f, 1.30f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.45f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Lavitz_SpinningCane() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(3);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 17, 11, 3, 50, 15, 9, 0, 2, 2, 5, 7, 16, 32, 0, fc ? 10 : 11, new AdditionSound(5, 11), new AdditionSound(0, 9));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 22, 16, 3, 25, 10, 0, 0, 0, 0, 17, 5, 16, 32, 0, 0, new AdditionSound(9, 16), new AdditionSound(1, 14));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 25, 20, 2, 25, 10, 0, 4, -4, 10, 8, 5, 17, 32, 0, 0, new AdditionSound(10, 20), new AdditionSound(3, 18), new AdditionSound(7, 4), new AdditionSound(7, 10), new AdditionSound(5, 11), new AdditionSound(0, 9));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4040, 5, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.25f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.75f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.00f, 1.00f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Lavitz_RodTyphoon() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(5);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 17, 11, 3, 30, 6, 10, 0, 2, 2, 5, 7, 16, 32, 0, fc ? 10 : 11, new AdditionSound(5, 11), new AdditionSound(0, 9));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 19, 13, 3, 30, 6, 0, 0, -3, 11, 7, 3, 15, 32, 0, fc ? 1 : 0, new AdditionSound(8, 14), new AdditionSound(1, 12));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 13, 7, 3, 30, 6, 0, 0, -2, 0, 9, 11, 12, 32, 0, 0, new AdditionSound(8, 7), new AdditionSound(2, 5));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 16, fc ? 9 : 10, 3, 30, 6, 0, 0, -2, 0, 12, 11, 14, 32, 0, 0, new AdditionSound(9, 11), new AdditionSound(2, 9));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 17, 12, 2, 30, 6, 0, 4, -9, 0, 17, 13, 18, 32, 0, 0, new AdditionSound(11, 13), new AdditionSound(3, 11));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4041, 7, 150,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.08f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.16f, 2.00f),
        new SimpleAddition.LevelMultipliers(1.24f, 2.50f),
        new SimpleAddition.LevelMultipliers(1.35f, 3.40f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Lavitz_GustOfWindDance() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(7);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 17, 11, 3, 30, 5, 11, 0, 0, 2, 5, 7, 16, 32, 0, fc ? 10 : 11, new AdditionSound(5, 11), new AdditionSound(0, 9));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 29, 14, 3, 30, 5, 0, 0, 0, 0, 29, 6, 15, 32, 0, 0, new AdditionSound(8, 14), new AdditionSound(1, 12));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 22, 15, 3, 30, 5, 0, 0, 0, 4, 11, 23, 9, 32, 0, 0, new AdditionSound(12, 15), new AdditionSound(3, 13));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 10, 5, 2, 30, 5, 0, 0, 0, 4, 1, 1, 18, 32, 0, fc ? 2 : 0, new AdditionSound(8, 6), new AdditionSound(0, 4));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 22, fc ? 14 : 16, 3, 30, 5, 0, 0, 0, 11, 5, 3, 22, 32, 0, 0, new AdditionSound(9, 17), new AdditionSound(1, 15), new AdditionSound(7, 7), new AdditionSound(7, 12), new AdditionSound(10, 8), new AdditionSound(2, 6));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 13, fc ? 5 : 7, 3, 30, 5, 0, 0, -4, 3, 4, 7, 18, 32, 0, 0, new AdditionSound(10, 8), new AdditionSound(2, 6));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 40, 19, 2, 20, 5, 0, 4, -8, 0, 40, 6, 24, 32, 0, 0, new AdditionSound(11, 19), new AdditionSound(3, 17), new AdditionSound(7, 12), new AdditionSound(7, 15), new AdditionSound(5, 11), new AdditionSound(0, 9));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4042, 11, 200,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.40f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.60f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.75f, 1.00f)
      }, additionHitProperties);
  }

  public static MasterAddition createAdditionHits_Lavitz_FlowerStorm() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(8);
    additionHitProperties[0] = createAdditionHitProperties(0xe0, 17, 11, 3, 30, 8, 12, 0, 0, 2, 5, 7, 16, 32, 0, fc ? 10 : 11, new AdditionSound(5, 11), new AdditionSound(0, 9));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 19, 13, 3, 30, 8, 0, 0, 0, 0, 0, 0, 18, 32, 24, 0, new AdditionSound(10, 13), new AdditionSound(1, 11));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 27, 20, 2, 30, 8, 0, 0, 0, 14, 12, 12, 12, 32, 0, fc ? 1 : 0, new AdditionSound(13, 20), new AdditionSound(2, 19));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 16, 10, 3, 40, 8, 0, 0, 0, 4, 5, 1, 22, 32, 0, 0, new AdditionSound(8, 11), new AdditionSound(0, 9));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 22, 16, 3, 40, 8, 0, 0, 0, 10, 5, 3, 18, 32, 0, 0, new AdditionSound(9, 17), new AdditionSound(2, 15));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 17, 12, 2, 40, 8, 0, 0, 0, 0, 14, 6, 22, 32, 0, fc ? 1 : 0, new AdditionSound(5, 13), new AdditionSound(2, 11));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 20, fc ? 13 : 14, 3, 40, 6, 0, 0, 0, 11, 7, 14, 20, 32, 25, 0, new AdditionSound(10, 15), new AdditionSound(1, 13));
    additionHitProperties[7] = createAdditionHitProperties(0xc0, 26, 7, 2, 50, 6, 0, 4, 0, 0, 26, 10, 10, 32, 0, 0, new AdditionSound(11, 7), new AdditionSound(3, 5));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new MasterAddition(4043, 300,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.08f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.16f, 2.00f),
        new SimpleAddition.LevelMultipliers(1.24f, 2.50f),
        new SimpleAddition.LevelMultipliers(1.35f, 3.40f)
      }, additionHitProperties);
  }

  public static AdditionHits80 createAdditionHits_Unknown2() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 2, 0, 0, 100, 0, 13, 0, 0, 0, 1, 7, 10, 32, 18, 11, new AdditionSound(5, 5), new AdditionSound(0, 3));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 14, 5, 0, 10, 0, 0, 0, 0, 0, 0, 0, 7, 32, 0, 0, new AdditionSound(4, 11), new AdditionSound(2, 9));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 32, 25, 0, 20, 0, 0, 0, 0, 4, 21, 2, 7, 32, 0, 0, new AdditionSound(5, 5), new AdditionSound(0, 3));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 10, 1, 0, 30, 0, 0, 0, 0, 1, 8, 29, 7, 32, 0, 0, new AdditionSound(7, 9), new AdditionSound(1, 7));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 11, 10, 0, 40, 0, 0, 0, 0, 0, 10, 29, 5, 32, 19, 0, new AdditionSound(10, 8), new AdditionSound(3, 6));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 61, 29, 0, 0, 0, 0, 0, 0, 0, 60, 5, 7, 32, 0, 0, new AdditionSound(5, 5), new AdditionSound(0, 3));
    adjustHitOverlayStartingOffset(additionHitProperties);
    return new AdditionHits80(additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Rose_WhipSmack() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(2);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 11, 5, 3, 75, 30, 14, 0, 3, 2, 2, 9, 12, 32, 0, fc ? 10 : 11, new AdditionSound(5, 5), new AdditionSound(0, 3));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 16, 11, 2, 25, 5, 0, 4, 0, 0, 0, 0, 8, 32, 0, 0, new AdditionSound(4, 11), new AdditionSound(2, 9));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4055, 1, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.25f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.75f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.00f, 1.00f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Rose_MoreAndMore() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(3);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 11, 5, 3, 50, 10, 15, 0, 3, 2, 2, 5, 12, 32, 0, fc ? 10 : 11, new AdditionSound(5, 5), new AdditionSound(0, 3));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 15, 9, 3, 50, 10, 0, 0, -4, 8, 2, 7, 8, 32, 0, 0, new AdditionSound(7, 9), new AdditionSound(1, 7));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 33, 8, 2, 50, 10, 0, 4, 0, 6, 2, 11, 6, 32, 0, 0, new AdditionSound(10, 8), new AdditionSound(3, 6));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4056, 14, 150,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.00f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.00f, 2.00f),
        new SimpleAddition.LevelMultipliers(1.00f, 2.50f),
        new SimpleAddition.LevelMultipliers(1.00f, 3.40f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Rose_HardBlade() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 11, 5, 3, 20, 10, 16, 0, 0, 2, 2, 9, 12, 32, 0, fc ? 10 : 11, new AdditionSound(5, 5), new AdditionSound(0, 3));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 17, 11, 3, 20, 5, 0, 0, 0, 10, 2, 8, 8, 32, 0, 0, new AdditionSound(4, 12), new AdditionSound(1, 10));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 22, 16, 2, 20, 5, 0, 0, 0, 0, 2, 0, 12, 32, 24, 0, new AdditionSound(8, 16), new AdditionSound(2, 14));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 11, 5, 3, 20, 5, 0, 0, 0, 0, 2, 0, 7, 32, 0, 0, new AdditionSound(5, 6), new AdditionSound(1, 4));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 22, fc ? 14 : 15, 3, 10, 5, 0, 0, 0, 13, 2, 13, 12, 32, 0, 0, new AdditionSound(9, 15), new AdditionSound(3, 13));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 17, 12, 2, 10, 5, 0, 4, -8, 10, 3, 8, 12, 32, 0, 0, new AdditionSound(10, 12), new AdditionSound(2, 10));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4057, 19, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.50f, 1.00f),
        new SimpleAddition.LevelMultipliers(3.00f, 1.00f)
      }, additionHitProperties);
  }

  public static MasterAddition createAdditionHits_Rose_DemonsDance() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(8);
    additionHitProperties[0] = createAdditionHitProperties(0xe0, 11, 5, 3, 30, 20, 17, 0, 0, 2, 2, 9, 12, 32, 0, fc ? 10 : 11, new AdditionSound(5, 5), new AdditionSound(0, 3));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 12, 6, 3, 30, 20, 0, 0, 0, 0, 0, 0, 10, 32, 0, 0, new AdditionSound(9, 7), new AdditionSound(1, 5));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 14, 9, 2, 30, 10, 0, 0, 0, 6, 3, 8, 10, 32, 0, fc ? 1 : 0, new AdditionSound(4, 9), new AdditionSound(3, 7));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 19, 13, 3, 30, 10, 0, 0, 0, 16, 2, 8, 10, 32, 0, 0, new AdditionSound(8, 14), new AdditionSound(1, 12));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 23, fc ? 12 : 14, 3, 20, 10, 0, 0, 3, 12, 3, 10, 10, 32, 0, 0, new AdditionSound(7, 14), new AdditionSound(0, 12));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 30, 10, 2, 20, 10, 0, 0, -4, 10, 11, 3, 7, 32, 0, 0, new AdditionSound(8, 10), new AdditionSound(2, 8));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 20, fc ? 10 : 11, 3, 20, 10, 0, 0, 5, 8, 4, 12, 9, 32, 0, 0, new AdditionSound(9, 11), new AdditionSound(1, 9));
    additionHitProperties[7] = createAdditionHitProperties(0xc0, 26, 4, 2, 20, 10, 0, 4, 12, 0, 0, 0, 7, 32, 0, 0, new AdditionSound(10, 4), new AdditionSound(2, 2));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new MasterAddition(4058, 200,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.40f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.80f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.20f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.50f, 1.00f)
      }, additionHitProperties);
  }

  public static AdditionHits80 createAdditionHits_Unknown3() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 0, 0, 0, 100, 0, 18, 0, 0, 0, 0, 0, 12, 0, 0, 0, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 12, 0, 0, 0, new AdditionSound(10, 3), new AdditionSound(1, 1));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0, 12, 0, 0, 0, new AdditionSound(10, 3), new AdditionSound(0, 1));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 0, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0, 9, 32, 0, 0, new AdditionSound(11, 42), new AdditionSound(3, 40));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, new AdditionSound(4, 9), new AdditionSound(0, 7));
    adjustHitOverlayStartingOffset(additionHitProperties);
    return new AdditionHits80(additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Kongol_Pursuit() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(2);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 8, fc ? 1 : 2, 3, 75, 30, 19, 0, 10, 0, 0, 0, 16, 32, 0, 11, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 30, 21, 2, 25, 5, 0, 4, -10, 0, 0, 0, 2, 32, 0, 0, new AdditionSound(10, 21), new AdditionSound(3, 19));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4087, 1, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.10f, 1.10f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.20f),
        new SimpleAddition.LevelMultipliers(1.30f, 1.30f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.45f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Kongol_Inferno() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(4);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 8, fc ? 1 : 2, 3, 40, 5, 20, 0, 15, 0, 0, 0, 20, 32, 0, 11, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 56, fc ? 45 : 46, 3, 20, 5, 0, 0, 15, 0, 0, 0, 15, 32, 0, 0, new AdditionSound(9, 46), new AdditionSound(1, 44), new AdditionSound(7, 26), new AdditionSound(7, 39), new AdditionSound(10, 3), new AdditionSound(2, 1));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 9, fc ? 2 : 3, 3, 20, 5, 0, 0, -8, 0, 1, 12, 18, 32, 0, 0, new AdditionSound(10, 3), new AdditionSound(2, 1));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 42, 13, 2, 20, 5, 0, 4, -5, 0, 11, 12, 18, 32, 0, 0, new AdditionSound(12, 13), new AdditionSound(3, 11));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4088, 23, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.25f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.75f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.00f, 1.00f)
      }, additionHitProperties);
  }

  public static MasterAddition createAdditionHits_Kongol_BoneCrush() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xe0, 8, fc ? 1 : 2, 3, 50, 20, 21, 0, 10, 0, 0, 0, 20, 32, 0, 11, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 19, fc ? 12 : 13, 3, 30, 20, 0, 0, 0, 11, 2, 7, 15, 32, 0, 0, new AdditionSound(4, 14), new AdditionSound(1, 12));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 10, 5, 2, 30, 20, 0, 0, -3, 3, 2, 3, 15, 32, 0, 0, new AdditionSound(11, 5), new AdditionSound(1, 3));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 16, fc ? 6 : 8, 3, 30, 20, 0, 0, -3, 5, 1, 3, 15, 32, 25, 0, new AdditionSound(9, 8), new AdditionSound(0, 6));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 10, fc ? 2 : 3, 3, 30, 10, 0, 0, -8, 0, 2, 7, 13, 32, 0, 0, new AdditionSound(13, 3), new AdditionSound(2, 1));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 21, 12, 2, 30, 10, 0, 4, -13, 0, 11, 20, 15, 32, 0, 0, new AdditionSound(14, 12), new AdditionSound(3, 10));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new MasterAddition(4089, 200,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.10f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.30f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.00f)
      }, additionHitProperties);
  }

  public static AdditionHits80 createAdditionHits_Unknown4() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 0, 0, 0, 100, 0, 22, 0, 0, 0, 0, 0, 7, 0, 0, 0, new AdditionSound(0, 0), new AdditionSound(0, 0), new AdditionSound(0, 0), new AdditionSound(0, 0), new AdditionSound(0, 0), new AdditionSound(0, 0), new AdditionSound(0, 0), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, new AdditionSound(0, 0), new AdditionSound(0, 0), new AdditionSound(0, 0), new AdditionSound(0, 0));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0);
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0);
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 0, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    adjustHitOverlayStartingOffset(additionHitProperties);
    return new AdditionHits80(additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Meru_DoubleSmack() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(2);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 7, 1, 3, 75, 10, 23, 0, 0, 0, 0, 0, 9, 32, 24, fc ? 12 : 13, new AdditionSound(4, 1), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 21, 6, 2, 25, 10, 0, 4, -6, 0, 6, 5, 10, 32, 0, 0, new AdditionSound(5, 6), new AdditionSound(3, 4));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4079, 1, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.10f, 1.20f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.40f),
        new SimpleAddition.LevelMultipliers(1.30f, 1.60f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.75f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Meru_HammerSpin() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(4);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 7, 1, 3, 50, 20, 24, 0, 0, 0, 0, 0, 9, 32, 24, fc ? 12 : 13, new AdditionSound(4, 1), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 26, fc ? 7 : 8, 3, 50, 5, 0, 0, 0, 6, 12, 8, 10, 32, 0, 0, new AdditionSound(8, 8), new AdditionSound(1, 6));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 11, fc ? 2 : 3, 3, 25, 5, 0, 0, 0, 0, 3, 3, 9, 32, 0, 0, new AdditionSound(15, 3), new AdditionSound(0, 1));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 71, 16, 3, 25, 5, 0, 4, -6, 0, 6, 10, 6, 32, 0, 0, new AdditionSound(14, 16), new AdditionSound(3, 14), new AdditionSound(7, 26));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4080, 21, 150,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.08f, 1.25f),
        new SimpleAddition.LevelMultipliers(1.16f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.24f, 1.75f),
        new SimpleAddition.LevelMultipliers(1.35f, 2.00f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Meru_CoolBoogie() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(5);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 7, 1, 3, 20, 12, 25, 0, 0, 0, 0, 0, 9, 32, 24, fc ? 12 : 13, new AdditionSound(4, 1), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 19, 8, 3, 20, 12, 0, 0, -7, 0, 9, 6, 9, 32, 0, 0, new AdditionSound(8, 8), new AdditionSound(1, 6));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 34, fc ? 16 : 17, 3, 20, 12, 0, 0, 3, 6, 11, 20, 10, 32, 0, 0, new AdditionSound(9, 17), new AdditionSound(2, 15), new AdditionSound(7, 30), new AdditionSound(7, 33), new AdditionSound(10, 2), new AdditionSound(0, 0));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 16, fc ? 1 : 2, 3, 20, 12, 0, 0, 0, 0, 0, 0, 15, 32, 0, 0, new AdditionSound(10, 2), new AdditionSound(0, 0));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 23, 11, 2, 20, 12, 0, 4, -2, 9, 4, 6, 9, 32, 0, 0, new AdditionSound(11, 11), new AdditionSound(3, 9));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4081, 26, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.00f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.00f, 2.00f),
        new SimpleAddition.LevelMultipliers(1.00f, 2.50f),
        new SimpleAddition.LevelMultipliers(1.00f, 3.40f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Meru_CatsCradle() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(7);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 7, 1, 3, 30, 3, 26, 0, 0, 0, 0, 0, 9, 32, 24, fc ? 12 : 13, new AdditionSound(4, 1), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 12, 6, 3, 20, 3, 0, 0, 0, 5, 3, 1, 12, 32, 0, fc ? 1 : 0, new AdditionSound(10, 7), new AdditionSound(1, 5));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 16, fc ? 8 : 9, 3, 20, 3, 0, 0, 0, 0, 11, 6, 15, 32, 0, 0, new AdditionSound(5, 9), new AdditionSound(0, 7));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 16, 8, 2, 20, 3, 0, 0, 0, 0, 12, 14, 14, 32, 0, 0, new AdditionSound(13, 8), new AdditionSound(2, 6));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 18, 1, 3, 20, 3, 0, 0, 10, 0, 2, 3, 12, 32, 0, 0, new AdditionSound(14, 1), new AdditionSound(0, 0));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 27, fc ? 17 : 18, 3, 20, 3, 0, 0, -5, 0, 22, 5, 15, 32, 25, 0, new AdditionSound(10, 18), new AdditionSound(2, 17));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 29, 1, 2, 20, 2, 0, 4, -9, 0, 3, 4, 15, 32, 0, 0, new AdditionSound(11, 1), new AdditionSound(3, 0), new AdditionSound(7, 12), new AdditionSound(7, 16), new AdditionSound(4, 1), new AdditionSound(0, 0));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4082, 30, 150,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.30f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.60f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.90f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.34f, 1.00f)
      }, additionHitProperties);
  }

  public static MasterAddition createAdditionHits_Meru_PerkyStep() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(8);
    additionHitProperties[0] = createAdditionHitProperties(0xe0, 7, 1, 3, 30, 20, 27, 0, 0, 0, 0, 0, 9, 32, 24, fc ? 12 : 13, new AdditionSound(4, 1), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 24, 16, 3, 30, 20, 0, 0, 0, 6, 14, 10, 14, 32, 0, 0, new AdditionSound(5, 16), new AdditionSound(1, 14));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 21, 1, 2, 30, 10, 0, 0, 0, 0, 4, 7, 14, 32, 25, fc ? 1 : 0, new AdditionSound(9, 1), new AdditionSound(1, 0));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 19, fc ? 4 : 5, 3, 30, 10, 0, 0, -12, 0, 6, 15, 12, 32, 0, 0, new AdditionSound(15, 5), new AdditionSound(3, 3));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 8, 1, 3, 20, 10, 0, 0, 15, 0, 6, 10, 22, 32, 0, 0, new AdditionSound(8, 1), new AdditionSound(1, 0));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 14, 4, 2, 20, 10, 0, 0, 0, 6, 3, 4, 16, 32, 0, 0, new AdditionSound(9, 4), new AdditionSound(2, 2));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 19, fc ? 6 : 7, 3, 20, 10, 0, 0, -3, 0, 6, 1, 6, 32, 0, 0, new AdditionSound(10, 7), new AdditionSound(0, 5));
    additionHitProperties[7] = createAdditionHitProperties(0xc0, 10, 1, 2, 20, 10, 0, 4, -2, 0, 2, 4, 18, 32, 0, 0, new AdditionSound(11, 1), new AdditionSound(3, 0));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new MasterAddition(4083, 200,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.50f, 1.00f),
        new SimpleAddition.LevelMultipliers(3.00f, 1.00f)
      }, additionHitProperties);
  }

  public static AdditionHits80 createAdditionHits_Unknown5() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 0, 0, 0, 100, 0, 28, 0, 0, 0, 0, 0, 6, 0, 0, 0, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, new AdditionSound(10, 21), new AdditionSound(3, 19));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, new AdditionSound(9, 46), new AdditionSound(1, 44), new AdditionSound(7, 26), new AdditionSound(7, 39), new AdditionSound(10, 3), new AdditionSound(2, 1));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 0, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, new AdditionSound(10, 3), new AdditionSound(2, 1));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, new AdditionSound(12, 13), new AdditionSound(3, 11));
    adjustHitOverlayStartingOffset(additionHitProperties);
    return new AdditionHits80(additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Haschel_DoublePunch() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(2);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 75, 30, 29, 0, 0, 0, 0, 0, 8, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 23, 3, 2, 25, 5, 0, 4, 0, 0, 0, 0, 8, 32, 0, 0, new AdditionSound(10, 3), new AdditionSound(1, 1));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4063, 1, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.10f, 1.10f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.20f),
        new SimpleAddition.LevelMultipliers(1.30f, 1.30f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.45f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Haschel_FlurryOfStyx() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(3);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 100, 10, 30, 0, 0, 0, 0, 0, 8, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 19, 3, 3, 25, 5, 0, 0, -8, 0, 0, 0, 10, 32, 0, 0, new AdditionSound(10, 3), new AdditionSound(0, 1));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 51, 42, 2, 25, 5, 0, 4, -8, 3, 3, 0, 13, 32, 0, 0, new AdditionSound(11, 42), new AdditionSound(3, 40));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4064, 14, 150,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.08f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.16f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.24f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.35f, 1.00f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Haschel_Summon4Gods() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(4);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 25, 20, 31, 0, 0, 0, 0, 0, 8, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 15, 9, 3, 25, 10, 0, 0, -3, 0, 0, 0, 5, 32, 25, fc ? 1 : 0, new AdditionSound(9, 10), new AdditionSound(1, 8));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 19, fc ? 9 : 11, 3, 25, 10, 0, 0, 0, 0, 9, 8, 5, 32, 0, 0, new AdditionSound(5, 11), new AdditionSound(2, 9));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 24, 10, 2, 25, 10, 0, 4, 5, 0, 0, 0, 8, 32, 0, 0, new AdditionSound(11, 10), new AdditionSound(3, 8));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4065, 18, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.00f, 1.25f),
        new SimpleAddition.LevelMultipliers(1.00f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.00f, 1.75f),
        new SimpleAddition.LevelMultipliers(1.00f, 2.02f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Haschel_5RingShattering() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(5);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 30, 7, 32, 0, 0, 0, 0, 0, 8, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 15, 9, 3, 30, 7, 0, 0, 0, 6, 3, 5, 4, 32, 0, fc ? 1 : 0, new AdditionSound(4, 10), new AdditionSound(1, 8));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 12, 6, 2, 30, 7, 0, 0, -3, 4, 2, 3, 5, 32, 0, 0, new AdditionSound(8, 6), new AdditionSound(1, 4));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 10, 4, 3, 30, 7, 0, 0, 0, 0, 10, 1, 6, 32, 25, fc ? 1 : 0, new AdditionSound(9, 6), new AdditionSound(2, 4));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 33, 7, 2, 30, 7, 0, 4, -5, 0, 0, 0, 10, 32, 0, 0, new AdditionSound(11, 7), new AdditionSound(3, 5));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4066, 22, 150,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.25f, 1.10f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.20f),
        new SimpleAddition.LevelMultipliers(1.75f, 1.30f),
        new SimpleAddition.LevelMultipliers(2.00f, 1.45f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Haschel_HexHammer() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(7);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 15, 9, 3, 30, 3, 33, 0, 0, 0, 0, 0, 8, 32, 0, fc ? 10 : 11, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 13, 7, 3, 30, 2, 0, 0, 0, 4, 2, 5, 8, 32, 0, fc ? 1 : 0, new AdditionSound(5, 8), new AdditionSound(1, 6));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 13, 4, 2, 30, 2, 0, 0, 0, 3, 1, 3, 8, 0, 0, fc ? 1 : 0, new AdditionSound(9, 4), new AdditionSound(2, 2));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 11, 5, 3, 30, 2, 0, 0, 0, 3, 2, 5, 6, 32, 0, 0, new AdditionSound(8, 6), new AdditionSound(0, 4));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 12, fc ? 4 : 5, 3, 30, 2, 0, 0, 0, 2, 2, 3, 5, 32, 0, 0, new AdditionSound(5, 5), new AdditionSound(1, 3));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 13, 8, 2, 30, 2, 0, 0, 0, 0, 9, 5, 10, 32, 0, fc ? 1 : 0, new AdditionSound(11, 9), new AdditionSound(2, 7));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 23, 10, 2, 20, 2, 0, 4, -10, 9, 1, 8, 11, 32, 0, 0, new AdditionSound(10, 10), new AdditionSound(3, 8));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4067, 26, 200,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.25f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.75f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.00f, 1.00f)
      }, additionHitProperties);
  }

  public static MasterAddition createAdditionHits_Haschel_OmniSweep() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(8);
    additionHitProperties[0] = createAdditionHitProperties(0xe0, 15, 9, 3, 30, 8, 34, 0, 0, 0, 0, 0, 8, 32, 0, fc ? 10 : 11, new AdditionSound(4, 8), new AdditionSound(0, 6));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 18, fc ? 7 : 8, 2, 30, 6, 0, 0, 0, 5, 3, 4, 5, 0, 0, fc ? 1 : 0, new AdditionSound(9, 8), new AdditionSound(1, 6));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 12, fc ? 2 : 3, 3, 30, 6, 0, 0, 0, 0, 2, 1, 10, 0, 0, 0, new AdditionSound(8, 2), new AdditionSound(2, 0));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 11, 6, 2, 40, 6, 0, 0, 0, 5, 2, 3, 10, 32, 0, fc ? 1 : 0, new AdditionSound(5, 7), new AdditionSound(0, 5));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 16, fc ? 3 : 6, 3, 40, 6, 0, 0, 0, 0, 0, 0, 8, 32, 0, 0, new AdditionSound(9, 5), new AdditionSound(2, 3));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 8, fc ? 2 : 3, 2, 40, 6, 0, 0, -3, 0, 3, 4, 6, 32, 0, 0, new AdditionSound(8, 3), new AdditionSound(1, 1));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 19, fc ? 9 : 11, 3, 40, 6, 0, 0, 0, 4, 6, 7, 12, 32, 0, 0, new AdditionSound(11, 10), new AdditionSound(2, 8));
    additionHitProperties[7] = createAdditionHitProperties(0xc0, 37, 3, 2, 50, 6, 0, 4, -7, 0, 2, 15, 10, 32, 0, 0, new AdditionSound(10, 3), new AdditionSound(3, 1));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new MasterAddition(4068, 300,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.15f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.30f, 2.00f),
        new SimpleAddition.LevelMultipliers(1.45f, 2.50f),
        new SimpleAddition.LevelMultipliers(1.67f, 3.00f)
      }, additionHitProperties);
  }

  public static AdditionHits80 createAdditionHits_Unknown6() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 0, 0, 0, 100, 0, 35, 0, 0, 0, 0, 0, 6, 0, 0, 0, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, new AdditionSound(8, 15), new AdditionSound(3, 13));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, new AdditionSound(9, 19), new AdditionSound(1, 17), new AdditionSound(7, 13), new AdditionSound(7, 15), new AdditionSound(10, 4), new AdditionSound(3, 2));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, new AdditionSound(10, 4), new AdditionSound(3, 2));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 0, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 54, 0, 0, 0, new AdditionSound(8, 22), new AdditionSound(1, 20));
    adjustHitOverlayStartingOffset(additionHitProperties);
    return new AdditionHits80(additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Albert_Harpoon() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(2);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 8, 2, 3, 75, 30, 36, 0, 5, 0, 1, 0, 19, 32, 0, fc ? 10 : 11, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 44, 15, 2, 25, 5, 0, 4, 0, 3, 10, 3, 10, 32, 0, 0, new AdditionSound(8, 15), new AdditionSound(3, 13));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4071, 1, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.10f, 1.10f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.20f),
        new SimpleAddition.LevelMultipliers(1.30f, 1.30f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.45f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Albert_SpinningCane() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(3);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 8, 2, 3, 50, 15, 37, 0, 5, 0, 1, 0, 19, 32, 0, fc ? 10 : 11, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 24, fc ? 17 : 19, 3, 25, 10, 0, 0, 4, 0, 4, 0, 8, 32, 0, 0, new AdditionSound(9, 19), new AdditionSound(1, 17), new AdditionSound(7, 13), new AdditionSound(7, 15), new AdditionSound(10, 4), new AdditionSound(3, 2));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 20, 4, 2, 25, 10, 0, 4, 0, 0, 0, 0, 6, 32, 0, 0, new AdditionSound(10, 4), new AdditionSound(3, 2));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4072, 5, 100,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.25f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.50f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.75f, 1.00f),
        new SimpleAddition.LevelMultipliers(2.00f, 1.00f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Albert_RodTyphoon() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(5);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 8, 2, 3, 30, 6, 38, 0, 0, 0, 1, 3, 19, 32, 0, fc ? 10 : 11, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 31, fc ? 21 : 22, 3, 30, 6, 0, 0, 5, 0, 20, 0, 16, 32, 0, 0, new AdditionSound(8, 22), new AdditionSound(1, 20));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 11, fc ? 3 : 4, 3, 30, 6, 0, 0, 0, 0, 0, 0, 16, 32, 0, 0, new AdditionSound(8, 4), new AdditionSound(2, 2));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 8, 2, 3, 30, 6, 0, 0, 0, 0, 1, 3, 19, 32, 0, 0, new AdditionSound(9, 3), new AdditionSound(2, 1));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 36, 3, 2, 30, 6, 0, 4, 0, 0, 0, 0, 18, 32, 0, 0, new AdditionSound(11, 3), new AdditionSound(3, 1));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4073, 7, 150,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.08f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.16f, 2.00f),
        new SimpleAddition.LevelMultipliers(1.24f, 2.50f),
        new SimpleAddition.LevelMultipliers(1.35f, 3.40f)
      }, additionHitProperties);
  }

  public static LevelLockedAddition createAdditionHits_Albert_GustOfWindDance() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(7);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 8, 2, 3, 30, 5, 39, 0, 5, 0, 1, 0, 19, 32, 0, fc ? 10 : 11, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 33, fc ? 18 : 19, 3, 30, 5, 0, 0, -3, 17, 2, 4, 11, 32, 0, 0, new AdditionSound(8, 19), new AdditionSound(2, 17));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 32, fc ? 14 : 15, 3, 30, 5, 0, 0, 3, 10, 3, 15, 17, 32, 0, 0, new AdditionSound(12, 15), new AdditionSound(3, 13), new AdditionSound(7, 3));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 6, 1, 2, 30, 5, 0, 0, 0, 0, 1, 0, 16, 32, 0, fc ? 1 : 0, new AdditionSound(8, 1), new AdditionSound(2, 0));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 11, 5, 3, 30, 5, 0, 0, 0, 0, 0, 0, 16, 32, 0, 0, new AdditionSound(9, 6), new AdditionSound(0, 4), new AdditionSound(7, 9), new AdditionSound(7, 14), new AdditionSound(10, 7), new AdditionSound(1, 5));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 12, 6, 3, 30, 5, 0, 0, 0, 4, 2, 3, 15, 32, 0, 0, new AdditionSound(10, 7), new AdditionSound(1, 5));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 34, 12, 2, 20, 5, 0, 4, 0, 4, 9, 13, 16, 32, 0, 0, new AdditionSound(11, 12), new AdditionSound(3, 10), new AdditionSound(7, 9));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new LevelLockedAddition(4074, 11, 200,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.20f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.40f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.60f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.75f, 1.00f)
      }, additionHitProperties);
  }

  public static MasterAddition createAdditionHits_Albert_FlowerStorm() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(8);
    additionHitProperties[0] = createAdditionHitProperties(0xe0, 8, 2, 3, 30, 8, 40, 0, 0, 0, 1, 0, 19, 32, 0, fc ? 10 : 11, new AdditionSound(5, 2), new AdditionSound(0, 0));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 26, 12, 3, 30, 8, 0, 0, -12, 10, 6, 8, 10, 32, 0, 0, new AdditionSound(10, 12), new AdditionSound(3, 10));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 17, 3, 2, 30, 8, 0, 0, 12, 0, 3, 22, 13, 32, 0, fc ? 1 : 0, new AdditionSound(13, 3), new AdditionSound(2, 1), new AdditionSound(7, 0));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 9, fc ? 2 : 3, 3, 40, 8, 0, 0, 0, 0, 3, 3, 10, 32, 0, 0, new AdditionSound(8, 4), new AdditionSound(0, 2));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 16, fc ? 9 : 10, 3, 40, 8, 0, 0, 6, 0, 9, 0, 0, 32, 0, 0, new AdditionSound(9, 11), new AdditionSound(1, 9));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 10, 2, 2, 40, 8, 0, 0, 0, 0, 3, 0, 0, 32, 0, 0, new AdditionSound(5, 2), new AdditionSound(2, 0));
    additionHitProperties[6] = createAdditionHitProperties(0xc0, 21, 14, 3, 40, 6, 0, 0, 0, 19, 2, 0, 0, 32, 25, 0, new AdditionSound(10, 14), new AdditionSound(0, 12));
    additionHitProperties[7] = createAdditionHitProperties(0xc0, 16, fc ? 5 : 7, 2, 50, 6, 0, 4, -5, 0, 8, 16, 18, 32, 0, 0, new AdditionSound(11, 7), new AdditionSound(3, 5), new AdditionSound(7, 0));
    adjustHitOverlayStartingOffset(additionHitProperties);

    return new MasterAddition(4075, 300,
      new SimpleAddition.LevelMultipliers[] {
        new SimpleAddition.LevelMultipliers(1.00f, 1.00f),
        new SimpleAddition.LevelMultipliers(1.08f, 1.50f),
        new SimpleAddition.LevelMultipliers(1.16f, 2.00f),
        new SimpleAddition.LevelMultipliers(1.24f, 2.50f),
        new SimpleAddition.LevelMultipliers(1.35f, 3.40f)
      }, additionHitProperties);
  }

  public static AdditionHits80 createAdditionHits_Unknown7() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 0, 0, 0, 100, 0, 41, 0, 0, 0, 0, 0, 12, 0, 18, 0);
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0);
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0);
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0);
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 0, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0);
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0);
    adjustHitOverlayStartingOffset(additionHitProperties);
    return new AdditionHits80(additionHitProperties);
  }

  public static AdditionHits80 createAdditionHits_Unknown8() {
    final AdditionHitProperties10[] additionHitProperties = createAdditionHitPropertiesArray(6);
    additionHitProperties[0] = createAdditionHitProperties(0xc0, 0, 0, 0, 200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[1] = createAdditionHitProperties(0xc0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, new AdditionSound(5, 11), new AdditionSound(3, 9));
    additionHitProperties[2] = createAdditionHitProperties(0xc0, 0, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, new AdditionSound(4, 9), new AdditionSound(0, 7));
    additionHitProperties[3] = createAdditionHitProperties(0xc0, 0, 0, 0, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, new AdditionSound(13, 12), new AdditionSound(1, 10));
    additionHitProperties[4] = createAdditionHitProperties(0xc0, 0, 0, 0, 80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, new AdditionSound(9, 8), new AdditionSound(3, 6));
    additionHitProperties[5] = createAdditionHitProperties(0xc0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, new AdditionSound(4, 9), new AdditionSound(0, 7));
    adjustHitOverlayStartingOffset(additionHitProperties);
    return new AdditionHits80(additionHitProperties);
  }
}
