package legend.game.modding.coremod.config;

import legend.game.combat.BattleDifficulty;
import legend.game.combat.Monsters;
import legend.game.combat.bent.BattleEntity27c;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.types.MonsterStats1c;
import legend.game.modding.coremod.CoreMod;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;
import legend.game.scripting.ScriptState;

import javax.annotation.Nullable;

import static legend.core.GameEngine.CONFIG;

public class BattleDifficultyConfigEntry extends EnumConfigEntry<BattleDifficulty> {
  public BattleDifficultyConfigEntry() {
    super(BattleDifficulty.class, BattleDifficulty.NORMAL, ConfigStorageLocation.CAMPAIGN, ConfigCategory.CHALLENGES, 0, BattleDifficultyConfigEntry::reloadMonsters);
  }

  public static void reloadMonsters() {
    Monsters.loadMonsters();
  }
  
  public static void adjustMonsterStats(final MonsterStats1c monster) {
    switch(CONFIG.getConfig(CoreMod.BATTLE_DIFFICULTY.get())) {
      case BattleDifficulty.EASY:
        monster.hp_00 = Math.round(monster.hp_00 * 0.8f);
        monster.attack_04 = Math.round(monster.attack_04 * 0.9f);
        monster.magicAttack_06 = Math.round(monster.magicAttack_06 * 0.85f);
        monster.defence_09 = Math.round(monster.defence_09 * 0.9f);
        monster.magicDefence_0a = Math.round(monster.magicDefence_0a * 0.9f);
        monster.attackAvoid_0b = Math.round(monster.attackAvoid_0b * 0.9f);
        monster.magicAvoid_0c = Math.round(monster.magicAvoid_0c * 0.9f);
        monster.speed_08 = Math.round(monster.speed_08 * 0.95f);
        break;
      case BattleDifficulty.HARD:
        monster.hp_00 = Math.round(monster.hp_00 * 1.5f);
        monster.attack_04 = Math.round(monster.attack_04 * 1.12f);
        monster.magicAttack_06 = Math.round(monster.magicAttack_06 * 1.06f);
        monster.defence_09 = Math.round(monster.defence_09 * 1.05f);
        monster.magicDefence_0a = Math.round(monster.magicDefence_0a * 1.05f);
        monster.attackAvoid_0b = Math.round(monster.attackAvoid_0b * 1.1f);
        monster.magicAvoid_0c = Math.round(monster.magicAvoid_0c * 1.1f);
        monster.speed_08 = Math.round(monster.speed_08 * 1.1f);
        break;
      case BattleDifficulty.EXTREME:
        monster.hp_00 = Math.round(monster.hp_00 * 2f);
        monster.attack_04 = Math.round(monster.attack_04 * 1.20f);
        monster.magicAttack_06 = Math.round(monster.magicAttack_06 * 1.14f);
        monster.defence_09 = Math.round(monster.defence_09 * 1.1f);
        monster.magicDefence_0a = Math.round(monster.magicDefence_0a * 1.1f);
        monster.attackAvoid_0b = Math.round(monster.attackAvoid_0b * 1.15f);
        monster.magicAvoid_0c = Math.round(monster.magicAvoid_0c * 1.15f);
        monster.speed_08 = Math.round(monster.speed_08 * 1.15f);
        break;
    }
  }

  public static int adjustCounterDamage(int damage, @Nullable final ScriptState<? extends BattleEntity27c> currentBent, final BattleEntity27c defender) {
    if(currentBent != null && currentBent.innerStruct_00 instanceof final PlayerBattleEntity player && defender.charId_272 == player.charId_272) {
      switch(CONFIG.getConfig(CoreMod.BATTLE_DIFFICULTY.get())) {
        case BattleDifficulty.EASY:
          damage = Math.round(damage * 0.80f);
          break;
        case BattleDifficulty.HARD:
          damage = Math.round(damage * 2.5f);
          break;
        case BattleDifficulty.EXTREME:
          damage = Math.round(damage * 5.0f);
          break;
      }
    }
    return damage;
  }
}
