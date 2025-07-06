package legend.game.modding.coremod.config;

import legend.game.combat.BattleDifficulty;
import legend.game.combat.Monsters;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;

import static legend.core.GameEngine.CONFIG;

public class BattleDifficultyConfigEntry extends EnumConfigEntry<BattleDifficulty> {
  public BattleDifficultyConfigEntry() {
    super(BattleDifficulty.class, BattleDifficulty.NORMAL, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, 3001);
  }

  public static void adjustMonsterStats(final MonsterStatsEvent monster) {
    switch(CONFIG.getConfig(CoreMod.BATTLE_DIFFICULTY.get())) {
      case BattleDifficulty.EASY:
        monster.hp = Math.round(monster.hp * 0.8f);
        monster.attack = Math.round(monster.attack * 0.8f);
        monster.magicAttack = Math.round(monster.magicAttack * 0.8f);
        monster.defence = Math.round(monster.defence * 0.9f);
        monster.magicDefence = Math.round(monster.magicDefence * 0.9f);
        monster.attackAvoid = Math.round(monster.attackAvoid * 0.9f);
        monster.magicAvoid = Math.round(monster.magicAvoid * 0.9f);
        monster.speed = Math.round(monster.speed * 0.9f);
        break;
      case BattleDifficulty.HARD:
        monster.hp = Math.round(monster.hp * 1.5f);
        monster.attack = Math.round(monster.attack * 1.2f);
        monster.magicAttack = Math.round(monster.magicAttack * 1.2f);
        monster.defence = Math.round(monster.defence * 1.1f);
        monster.magicDefence = Math.round(monster.magicDefence * 1.1f);
        monster.attackAvoid = Math.round(monster.attackAvoid * 1.1f);
        monster.magicAvoid = Math.round(monster.magicAvoid * 1.1f);
        monster.speed = Math.round(monster.speed * 1.1f);
        break;
      case BattleDifficulty.HARDER:
        monster.hp = Math.round(monster.hp * 1.75f);
        monster.attack = Math.round(monster.attack * 1.35f);
        monster.magicAttack = Math.round(monster.magicAttack * 1.35f);
        monster.defence = Math.round(monster.defence * 1.2f);
        monster.magicDefence = Math.round(monster.magicDefence * 1.2f);
        monster.attackAvoid = Math.round(monster.attackAvoid * 1.25f);
        monster.magicAvoid = Math.round(monster.magicAvoid * 1.25f);
        monster.speed = Math.round(monster.speed * 1.2f);
        break;
    }
  }
}
