package legend.game.statistics;

import discord.DiscordRichPresence;
import legend.game.combat.bent.BattleEntity27c;
import legend.game.combat.bent.PlayerBattleEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

public final class Statistics {

  private static final Logger LOGGER = LogManager.getFormatterLogger(DiscordRichPresence.class);

  public static HashMap<Integer, Float> statistics;

  //x1 Dart
  //x2 Lavitz
  //x3 Shana
  //x4 Rose
  //x5 Haschel
  //x6 Albert
  //x7 Meru
  //x8 Kongol
  //x9 Miranda
  public enum Stats {
    TOTAL_DAMAGE(0, "Damage Dealt"),
    TOTAL_TAKEN(0, "Damage Taken"),
    TOTAL_ATTACK(0, "Attack"),

    TOTAL_PHYSICAL_DAMAGE(-10, "Physical Damage Dealt"),
    TOTAL_MAGICAL_DAMAGE(-20, "Magical Damage Dealt"),
    TOTAL_PHYSICAL_TAKEN(-30, "Physical Damage Taken"),
    TOTAL_MAGICAL_TAKEN(-40, "Magical Damage Taken"),
    TOTAL_HP_RECOVER(-50, "HP Recovered"),
    TOTAL_MP_RECOVER(-60, "MP Recovered"),
    TOTAL_SP_RECOVER(-70, "SP Recovered"),
    TOTAL_EVADE(-80, "Evade"),
    TOTAL_PHYSICAL_ATTACK(-90, "Physical Attack"),
    TOTAL_MAGICAL_ATTACK(-100, "Magical Attack"),
    TOTAL_DRAGOON_PHYSICAL_ATTACK(-110, "D-Physical Attack"),
    TOTAL_DRAGOON_MAGICAL_ATTACK(-120, "D-Magical Attack"),

    DART_PHYSICAL_DAMAGE(11),
    LAVITZ_PHYSICAL_DAMAGE(12),
    SHANA_PHYSICAL_DAMAGE(13),
    ROSE_PHYSICAL_DAMAGE(14),
    HASCHEL_PHYSICAL_DAMAGE(15),
    ALBERT_PHYSICAL_DAMAGE(16),
    MERU_PHYSICAL_DAMAGE(17),
    KONGOL_PHYSICAL_DAMAGE(18),
    MIRANDA_PHYSICAL_DAMAGE(19),

    DART_MAGICAL_DAMAGE(21),
    LAVITZ_MAGICAL_DAMAGE(22),
    SHANA_MAGICAL_DAMAGE(23),
    ROSE_MAGICAL_DAMAGE(24),
    HASCHEL_MAGICAL_DAMAGE(25),
    ALBERT_MAGICAL_DAMAGE(26),
    MERU_MAGICAL_DAMAGE(27),
    KONGOL_MAGICAL_DAMAGE(28),
    MIRANDA_MAGICAL_DAMAGE(29),

    DART_PHYSICAL_TAKEN(31),
    LAVITZ_PHYSICAL_TAKEN(32),
    SHANA_PHYSICAL_TAKEN(33),
    ROSE_PHYSICAL_TAKEN(34),
    HASCHEL_PHYSICAL_TAKEN(35),
    ALBERT_PHYSICAL_TAKEN(36),
    MERU_PHYSICAL_TAKEN(37),
    KONGOL_PHYSICAL_TAKEN(38),
    MIRANDA_PHYSICAL_TAKEN(39),

    DART_MAGICAL_TAKEN(41),
    LAVITZ_MAGICAL_TAKEN(42),
    SHANA_MAGICAL_TAKEN(43),
    ROSE_MAGICAL_TAKEN(44),
    HASCHEL_MAGICAL_TAKEN(45),
    ALBERT_MAGICAL_TAKEN(46),
    MERU_MAGICAL_TAKEN(47),
    KONGOL_MAGICAL_TAKEN(48),
    MIRANDA_MAGICAL_TAKEN(49),

    DART_HP_RECOVER(51),
    LAVITZ_HP_RECOVER(52),
    SHANA_HP_RECOVER(53),
    ROSE_HP_RECOVER(54),
    HASCHEL_HP_RECOVER(55),
    ALBERT_HP_RECOVER(56),
    MERU_HP_RECOVER(57),
    KONGOL_HP_RECOVER(58),
    MIRANDA_HP_RECOVER(59),

    DART_MP_RECOVER(61),
    LAVITZ_MP_RECOVER(62),
    SHANA_MP_RECOVER(63),
    ROSE_MP_RECOVER(64),
    HASCHEL_MP_RECOVER(65),
    ALBERT_MP_RECOVER(66),
    MERU_MP_RECOVER(67),
    KONGOL_MP_RECOVER(68),
    MIRANDA_MP_RECOVER(69),

    DART_SP_RECOVER(71),
    LAVITZ_SP_RECOVER(72),
    SHANA_SP_RECOVER(73),
    ROSE_SP_RECOVER(74),
    HASCHEL_SP_RECOVER(75),
    ALBERT_SP_RECOVER(76),
    MERU_SP_RECOVER(77),
    KONGOL_SP_RECOVER(78),
    MIRANDA_SP_RECOVER(79),

    DART_EVADE(81),
    LAVITZ_EVADE(82),
    SHANA_EVADE(83),
    ROSE_EVADE(84),
    HASCHEL_EVADE(85),
    ALBERT_EVADE(86),
    MERU_EVADE(87),
    KONGOL_EVADE(88),
    MIRANDA_EVADE(89),

    DART_PHYSICAL_ATTACK(91),
    LAVITZ_PHYSICAL_ATTACK(92),
    SHANA_PHYSICAL_ATTACK(93),
    ROSE_PHYSICAL_ATTACK(94),
    HASCHEL_PHYSICAL_ATTACK(95),
    ALBERT_PHYSICAL_ATTACK(96),
    MERU_PHYSICAL_ATTACK(97),
    KONGOL_PHYSICAL_ATTACK(98),
    MIRANDA_PHYSICAL_ATTACK(99),

    DART_MAGICAL_ATTACK(101),
    LAVITZ_MAGICAL_ATTACK(102),
    SHANA_MAGICAL_ATTACK(103),
    ROSE_MAGICAL_ATTACK(104),
    HASCHEL_MAGICAL_ATTACK(105),
    ALBERT_MAGICAL_ATTACK(106),
    MERU_MAGICAL_ATTACK(107),
    KONGOL_MAGICAL_ATTACK(108),
    MIRANDA_MAGICAL_ATTACK(109),

    DART_DRAGOON_PHYSICAL_ATTACK(111),
    LAVITZ_DRAGOON_PHYSICAL_ATTACK(112),
    SHANA_DRAGOON_PHYSICAL_ATTACK(113),
    ROSE_DRAGOON_PHYSICAL_ATTACK(114),
    HASCHEL_DRAGOON_PHYSICAL_ATTACK(115),
    ALBERT_DRAGOON_PHYSICAL_ATTACK(116),
    MERU_DRAGOON_PHYSICAL_ATTACK(117),
    KONGOL_DRAGOON_PHYSICAL_ATTACK(118),
    MIRANDA_DRAGOON_PHYSICAL_ATTACK(119),

    DART_DRAGOON_MAGICAL_ATTACK(121),
    LAVITZ_DRAGOON_MAGICAL_ATTACK(122),
    SHANA_DRAGOON_MAGICAL_ATTACK(123),
    ROSE_DRAGOON_MAGICAL_ATTACK(124),
    HASCHEL_DRAGOON_MAGICAL_ATTACK(125),
    ALBERT_DRAGOON_MAGICAL_ATTACK(126),
    MERU_DRAGOON_MAGICAL_ATTACK(127),
    KONGOL_DRAGOON_MAGICAL_ATTACK(128),
    MIRANDA_DRAGOON_MAGICAL_ATTACK(129),

    ;

    private final int stat;
    private final String name;

    Stats(final int stat, final String name) {
      this.stat = stat;
      this.name = name;
    }

    Stats(final int stat) {
      this.stat = stat;
      this.name = null;
    }

    public String getName() {
      return this.name;
    }

    public int asInt() {
      return this.stat;
    }

    public static Stats asStat(final int value) {
      return Arrays.stream(values()).filter(x -> x.asInt() == value).findFirst().orElse(null);
    }

    public static Stats getMax() {
      final Optional<Stats> max = Arrays.stream(Stats.values()).max(Comparator.comparingInt(Stats::asInt));
      return max.orElse(null);
    }
  }

  private Statistics() {
  }

  public static void save(final Path path, final String saveName) throws IOException {
    try {
      final String fileName = String.valueOf(path.resolve(saveName + ".stats"));
      final File myObj = new File(fileName);
      myObj.createNewFile();
      final FileWriter myWriter = new FileWriter(fileName);
      myWriter.write(getStatsString());
      myWriter.close();
    } catch(final IOException ex) {
      LOGGER.error(ex);
    }
  }

  public static void load(@Nullable final Path path, @Nullable final String saveName) {
    statistics = new HashMap<>();
    if(path != null && saveName != null) {
      try(final BufferedReader br = new BufferedReader(new FileReader(String.valueOf(path.resolve(saveName + ".stats"))))) {
        for(String line; (line = br.readLine()) != null; ) {
          line = line.trim();
          if(!line.isEmpty()) {
            final int i = Integer.parseInt(line.substring(0, line.indexOf('=')));
            if(i > -1) {
              statistics.put(i, Float.parseFloat(line.replace(i + "=", "")));
            }
          }
        }
      } catch(final IOException ex) {
        LOGGER.error(ex);
      }
    }
  }

  public static void delete(final Path path, final String saveName) throws IOException {
    try {
      Files.delete(path.resolve(saveName + ".stats"));
    } catch(final IOException ex) {
      LOGGER.error(ex);
    }
  }

  private static String getStatsString() {
    final StringBuilder text = new StringBuilder();
    for(final Stats stat : Stats.values()) {
      final int i = stat.asInt();
      if(i > -1) {
        text.append(i).append('=').append(statistics.getOrDefault(i, 0f)).append('\n');
      }
    }
    return text.toString();
  }

  public static void appendStat(final Stats stat, final float value) {
    final int i = stat.asInt();
    if(!statistics.containsKey(i)) {
      statistics.put(i, 0f);
    }
    statistics.put(i, statistics.get(i) + value);
  }

  public static void appendStat(final BattleEntity27c bent, final Stats stat, final float value) {
    if(value != 0 && bent instanceof final PlayerBattleEntity player) {
      final int statIndex = Math.abs(stat.asInt()) + player.charId_272 + 1;
      appendStat(Stats.asStat(statIndex), value);
    }
  }

  public static float getStat(final Stats stat) {
    return getStat(stat, 0);
  }

  public static float getStat(final Stats stat, final int offset) {
    float value = 0f;
    switch(stat) {
      case Stats.TOTAL_DAMAGE:
        value += getStat(Stats.TOTAL_PHYSICAL_DAMAGE, Math.abs(Stats.TOTAL_PHYSICAL_DAMAGE.asInt() * 2) + offset);
        value += getStat(Stats.TOTAL_MAGICAL_DAMAGE, Math.abs(Stats.TOTAL_MAGICAL_DAMAGE.asInt() * 2) + offset);
        break;
      case Stats.TOTAL_TAKEN:
        value += getStat(Stats.TOTAL_PHYSICAL_TAKEN, Math.abs(Stats.TOTAL_PHYSICAL_TAKEN.asInt() * 2) + offset);
        value += getStat(Stats.TOTAL_MAGICAL_TAKEN, Math.abs(Stats.TOTAL_MAGICAL_TAKEN.asInt() * 2) + offset);
        break;
      case Stats.TOTAL_ATTACK:
        value += getStat(Stats.TOTAL_PHYSICAL_ATTACK, Math.abs(Stats.TOTAL_PHYSICAL_ATTACK.asInt() * 2) + offset);
        value += getStat(Stats.TOTAL_MAGICAL_ATTACK, Math.abs(Stats.TOTAL_MAGICAL_ATTACK.asInt() * 2) + offset);
        value += getStat(Stats.TOTAL_DRAGOON_PHYSICAL_ATTACK, Math.abs(Stats.TOTAL_DRAGOON_PHYSICAL_ATTACK.asInt() * 2) + offset);
        value += getStat(Stats.TOTAL_DRAGOON_MAGICAL_ATTACK, Math.abs(Stats.TOTAL_DRAGOON_MAGICAL_ATTACK.asInt() * 2) + offset);
        break;
      default:
        final int i = stat.asInt() + offset;
        if(statistics.containsKey(i)) {
          value = statistics.get(i);
        }
        break;
    }
    return value;
  }

  private static float getSumForCharacterStats(final Stats stat) {
    return getStat(Math.abs(stat.asInt()), 1, 9);
  }

  private static float getStat(final int statIndex, final int minRange, final int maxRange) {
    float value = 0f;
    for(int i = statIndex + minRange; i <= statIndex + maxRange; i++) {
      value += getStat(Stats.asStat(i));
    }
    return value;
  }

  public static float[] getStats(final Stats stat) {
    final float[] stats;
    final int statIndex = Math.abs(stat.asInt());
    if(statIndex > 0) {
      stats = new float[9];
      for(int i = 0; i < stats.length; i++) {
        stats[i] = getStat(Stats.asStat(statIndex + i + 1));
      }
    } else if(statIndex == 0) {
      stats = new float[9];
      for(int i = 0; i < stats.length; i++) {
        stats[i] = getStat(stat, i + 1);
      }
    } else {
      stats = new float[1];
      stats[0] = getStat(stat);
    }
    return stats;
  }

  public static void appendRecoverStat(final BattleEntity27c bent, final int amount, final int colour) {
    final Stats stat = switch(colour) {
      case 3, 7 -> Statistics.Stats.TOTAL_HP_RECOVER;
      case 10, 12 -> Statistics.Stats.TOTAL_MP_RECOVER;
      case 11, 13 -> Statistics.Stats.TOTAL_SP_RECOVER;
      default -> null;
    };
    if (stat != null) {
      appendStat(bent, stat, amount);
    }
  }
}