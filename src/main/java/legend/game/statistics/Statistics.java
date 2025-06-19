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
    TOTAL_DAMAGE(0, "Damage"),
    TOTAL_PHYSICAL_DAMAGE(-10, "Physical Damage"),
    TOTAL_MAGICAL_DAMAGE(-20, "Magical Damage"),

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
    MIRANDA_MAGICAL_DAMAGE(29);

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
    for (final Stats stat : Stats.values()) {
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
    if (bent instanceof final PlayerBattleEntity player) {
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
    for (int i = statIndex + minRange; i <= statIndex + maxRange; i++) {
      value += getStat(Stats.asStat(i));
    }
    return value;
  }

  public static float[] getStats(final Stats stat) {
    final float[] stats;
    final int statIndex = Math.abs(stat.asInt());
    if (statIndex > 0) {
      stats = new float[9];
      for(int i = 0; i < stats.length; i++) {
        stats[i] = getStat(Stats.asStat(statIndex + i + 1));
      }
    } else if(statIndex == 0) {
      stats = new float[9];
      for(int i = 0; i < stats.length; i++) {
        stats[i] = getStat(Stats.asStat(statIndex), i + 1);
      }
    } else {
      stats = new float[1];
      stats[0] = getStat(stat);
    }
    return stats;
  }
}