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
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

import static legend.game.combat.Monsters.monsterNames_80112068;

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
    TOTAL_GUARD(-130, "Guard"),
    TOTAL_DEATH(-140, "Death"),
    TOTAL_ADDITION(-150, "Addition Attempt"),
    TOTAL_ADDITION_COMPLETE(-160, "Addition Complete"),
    TOTAL_ADDITION_FLAWLESS(-170, "Addition Flawless"),
    TOTAL_ADDITION_HIT(-180, "Addition Hits"),
    TOTAL_ADDITION_COUNTER(-190, "Counter"),
    TOTAL_ADDITION_COUNTER_BLOCK(-200, "Counter Block"),
    TOTAL_DRAGOON_ADDITION(-210, "D-Addition Attempt"),
    TOTAL_DRAGOON_ADDITION_COMPLETED(-220, "D-Addition Complete"),
    TOTAL_DRAGOON_ADDITION_HIT(-230, "D-Addition Hits"),
    TOTAL_REVIVE(-240, "Revive"),
    TOTAL_REVIVED(-250, "Revived"),
    TOTAL_EXP(-260, "Exp. Gained"),
    TOTAL_ENCOUNTER(-270, "Encounters"),
    TOTAL_ESCAPE(-280, "Escape"),
    TOTAL_KILL(-290, "Enemies Defeated"),

    GOLD(10000, "Gold Earned"),
    CHEST(10001, "Chests Opened"),
    DISTANCE(10003, "Distance Traveled", "%.2f"),

    DART_UNLOCKED(1, true),
    LAVITZ_UNLOCKED(2, true),
    SHANA_UNLOCKED(3, true),
    ROSE_UNLOCKED(4, true),
    HASCHEL_UNLOCKED(5, true),
    ALBERT_UNLOCKED(6, true),
    MERU_UNLOCKED(7, true),
    KONGOL_UNLOCKED(8, true),
    MIRANDA_UNLOCKED(9, true),

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

    DART_GUARD(131),
    LAVITZ_GUARD(132),
    SHANA_GUARD(133),
    ROSE_GUARD(134),
    HASCHEL_GUARD(135),
    ALBERT_GUARD(136),
    MERU_GUARD(137),
    KONGOL_GUARD(138),
    MIRANDA_GUARD(139),

    DART_DEATH(141),
    LAVITZ_DEATH(142),
    SHANA_DEATH(143),
    ROSE_DEATH(144),
    HASCHEL_DEATH(145),
    ALBERT_DEATH(146),
    MERU_DEATH(147),
    KONGOL_DEATH(148),
    MIRANDA_DEATH(149),

    DART_ADDITION(151),
    LAVITZ_ADDITION(152),
    SHANA_ADDITION(153),
    ROSE_ADDITION(154),
    HASCHEL_ADDITION(155),
    ALBERT_ADDITION(156),
    MERU_ADDITION(157),
    KONGOL_ADDITION(158),
    MIRANDA_ADDITION(159),

    DART_ADDITION_COMPLETE(161),
    LAVITZ_ADDITION_COMPLETE(162),
    SHANA_ADDITION_COMPLETE(163),
    ROSE_ADDITION_COMPLETE(164),
    HASCHEL_ADDITION_COMPLETE(165),
    ALBERT_ADDITION_COMPLETE(166),
    MERU_ADDITION_COMPLETE(167),
    KONGOL_ADDITION_COMPLETE(168),
    MIRANDA_ADDITION_COMPLETE(169),

    DART_ADDITION_FLAWLESS(171),
    LAVITZ_ADDITION_FLAWLESS(172),
    SHANA_ADDITION_FLAWLESS(173),
    ROSE_ADDITION_FLAWLESS(174),
    HASCHEL_ADDITION_FLAWLESS(175),
    ALBERT_ADDITION_FLAWLESS(176),
    MERU_ADDITION_FLAWLESS(177),
    KONGOL_ADDITION_FLAWLESS(178),
    MIRANDA_ADDITION_FLAWLESS(179),

    DART_ADDITION_HIT(181),
    LAVITZ_ADDITION_HIT(182),
    SHANA_ADDITION_HIT(183),
    ROSE_ADDITION_HIT(184),
    HASCHEL_ADDITION_HIT(185),
    ALBERT_ADDITION_HIT(186),
    MERU_ADDITION_HIT(187),
    KONGOL_ADDITION_HIT(188),
    MIRANDA_ADDITION_HIT(189),

    DART_ADDITION_COUNTER(191),
    LAVITZ_ADDITION_COUNTER(192),
    SHANA_ADDITION_COUNTER(193),
    ROSE_ADDITION_COUNTER(194),
    HASCHEL_ADDITION_COUNTER(195),
    ALBERT_ADDITION_COUNTER(196),
    MERU_ADDITION_COUNTER(197),
    KONGOL_ADDITION_COUNTER(198),
    MIRANDA_ADDITION_COUNTER(199),

    DART_ADDITION_COUNTER_BLOCK(201),
    LAVITZ_ADDITION_COUNTER_BLOCK(202),
    SHANA_ADDITION_COUNTER_BLOCK(203),
    ROSE_ADDITION_COUNTER_BLOCK(204),
    HASCHEL_ADDITION_COUNTER_BLOCK(205),
    ALBERT_ADDITION_COUNTER_BLOCK(206),
    MERU_ADDITION_COUNTER_BLOCK(207),
    KONGOL_ADDITION_COUNTER_BLOCK(208),
    MIRANDA_ADDITION_COUNTER_BLOCK(209),

    DART_DRAGOON_ADDITION(211),
    LAVITZ_DRAGOON_ADDITION(212),
    SHANA_DRAGOON_ADDITION(213),
    ROSE_DRAGOON_ADDITION(214),
    HASCHEL_DRAGOON_ADDITION(215),
    ALBERT_DRAGOON_ADDITION(216),
    MERU_DRAGOON_ADDITION(217),
    KONGOL_DRAGOON_ADDITION(218),
    MIRANDA_DRAGOON_ADDITION(219),

    DART_DRAGOON_ADDITION_COMPLETE(221),
    LAVITZ_DRAGOON_ADDITION_COMPLETE(222),
    SHANA_DRAGOON_ADDITION_COMPLETE(223),
    ROSE_DRAGOON_ADDITION_COMPLETE(224),
    HASCHEL_DRAGOON_ADDITION_COMPLETE(225),
    ALBERT_DRAGOON_ADDITION_COMPLETE(226),
    MERU_DRAGOON_ADDITION_COMPLETE(227),
    KONGOL_DRAGOON_ADDITION_COMPLETE(228),
    MIRANDA_DRAGOON_ADDITION_COMPLETE(229),

    DART_DRAGOON_ADDITION_HIT(231),
    LAVITZ_DRAGOON_ADDITION_HIT(232),
    SHANA_DRAGOON_ADDITION_HIT(233),
    ROSE_DRAGOON_ADDITION_HIT(234),
    HASCHEL_DRAGOON_ADDITION_HIT(235),
    ALBERT_DRAGOON_ADDITION_HIT(236),
    MERU_DRAGOON_ADDITION_HIT(237),
    KONGOL_DRAGOON_ADDITION_HIT(238),
    MIRANDA_DRAGOON_ADDITION_HIT(239),

    DART_REVIVE(241),
    LAVITZ_REVIVE(242),
    SHANA_REVIVE(243),
    ROSE_REVIVE(244),
    HASCHEL_REVIVE(245),
    ALBERT_REVIVE(246),
    MERU_REVIVE(247),
    KONGOL_REVIVE(248),
    MIRANDA_REVIVE(249),

    DART_REVIVED(251),
    LAVITZ_REVIVED(252),
    SHANA_REVIVED(253),
    ROSE_REVIVED(254),
    HASCHEL_REVIVED(255),
    ALBERT_REVIVED(256),
    MERU_REVIVED(257),
    KONGOL_REVIVED(258),
    MIRANDA_REVIVED(259),

    DART_EXP(261),
    LAVITZ_EXP(262),
    SHANA_EXP(263),
    ROSE_EXP(264),
    HASCHEL_EXP(265),
    ALBERT_EXP(266),
    MERU_EXP(267),
    KONGOL_EXP(268),
    MIRANDA_EXP(269),

    DART_ENCOUNTER(271),
    LAVITZ_ENCOUNTER(272),
    SHANA_ENCOUNTER(273),
    ROSE_ENCOUNTER(274),
    HASCHEL_ENCOUNTER(275),
    ALBERT_ENCOUNTER(276),
    MERU_ENCOUNTER(277),
    KONGOL_ENCOUNTER(278),
    MIRANDA_ENCOUNTER(279),

    DART_ESCAPE(281),
    LAVITZ_ESCAPE(282),
    SHANA_ESCAPE(283),
    ROSE_ESCAPE(284),
    HASCHEL_ESCAPE(285),
    ALBERT_ESCAPE(286),
    MERU_ESCAPE(287),
    KONGOL_ESCAPE(288),
    MIRANDA_ESCAPE(289),

    DART_KILL(291),
    LAVITZ_KILL(292),
    SHANA_KILL(293),
    ROSE_KILL(294),
    HASCHEL_KILL(295),
    ALBERT_KILL(296),
    MERU_KILL(297),
    KONGOL_KILL(298),
    MIRANDA_KILL(299),
    ;

    private final int stat;
    private final String name;
    private final String format;

    Stats(final int stat) {
      this(stat, null, null);
    }

    Stats(final int stat, final boolean isBool) {
      this(stat, null, isBool ? "b" : null);
    }

    Stats(final int stat, final String name) {
      this(stat, name, null);
    }

    Stats(final int stat, @Nullable final String name, @Nullable final String format) {
      this.stat = stat;
      this.name = name;
      this.format = format;
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
            if(i > 0) {
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

  public static void copy(final Path path, final String oldSaveName, final String newSaveName) {
    try {
      Files.copy(path.resolve(oldSaveName + ".stats"), path.resolve(newSaveName + ".stats"), StandardCopyOption.REPLACE_EXISTING);
    } catch(final IOException ex) {
      LOGGER.error(ex);
    }
  }

  private static String getStatsString() {
    final StringBuilder text = new StringBuilder();
    for(final Stats stat : Stats.values()) {
      final int i = stat.asInt();
      if(i > 0) {
        text.append(i).append('=').append(statistics.getOrDefault(i, 0f)).append('\n');
      }
    }
    for(int i = 0; i < monsterNames_80112068.length; i++) {
      final int enemyStatId = i + 100000;
      if(statistics.containsKey(enemyStatId)) {
        text.append(enemyStatId).append('=').append(statistics.get(enemyStatId)).append('\n');
      }
    }
    return text.toString();
  }

  public static void appendStat(final Stats stat, final float value) {
    final int i = stat.asInt();
    if(!statistics.containsKey(i)) {
      statistics.put(i, 0f);
    }
    float newValue = statistics.get(i) + value;
    if(stat.format != null && stat.format.equals("b")) {
      newValue = value > 0 ? 1 : 0;
    }
    statistics.put(i, newValue);
  }

  public static void appendStat(final int statIndex, final float value) {
    if(!statistics.containsKey(statIndex)) {
      statistics.put(statIndex, 0f);
    }
    statistics.put(statIndex, statistics.get(statIndex) + value);
  }

  public static void appendStat(final BattleEntity27c bent, final Stats stat, final float value) {
    if(bent instanceof final PlayerBattleEntity player) {
      appendStat(player.charId_272, stat, value);
    }
  }

  public static void appendStat(final int charId, final Stats stat, final float value) {
    if(value != 0) {
      final int statIndex = Math.abs(stat.asInt()) + charId + 1;
      appendStat(Stats.asStat(statIndex), value);
    }
  }

  public static void appendStat(final Stats stat, final float value, final int statOffset) {
    appendStat(Stats.asStat(stat.asInt() + statOffset), value);
  }

  public static void appendRecoverStat(final BattleEntity27c bent, final int amount, final int colour) {
    final Stats stat = switch(colour) {
      case 3, 7 -> Stats.TOTAL_HP_RECOVER;
      case 10, 12 -> Stats.TOTAL_MP_RECOVER;
      case 11, 13 -> Stats.TOTAL_SP_RECOVER;
      default -> null;
    };
    if(stat != null) {
      appendStat(bent, stat, amount);
    }
  }

  public static void incrementMonsterKill(final int monsterId, final int value) {
    appendStat(100000 + monsterId, value);
  }

  public static int getMonsterKill(final int monsterId) {
    final int statId = monsterId + 100000;
    if(statistics.containsKey(statId)) {
      return Math.round(statistics.get(statId));
    }
    return 0;
  }

  public static float getStat(final int statIndex) {
    return getStat(Stats.asStat(statIndex), 0);
  }

  public static float getStat(final Stats stat) {
    return getStat(stat, 0);
  }

  public static float getStat(final Stats stat, final int offset) {
    float value = 0f;
    switch(stat) {
      case Stats.TOTAL_DAMAGE:
        value += getOffsetStat(Stats.TOTAL_PHYSICAL_DAMAGE, offset);
        value += getOffsetStat(Stats.TOTAL_MAGICAL_DAMAGE, offset);
        break;
      case Stats.TOTAL_TAKEN:
        value += getOffsetStat(Stats.TOTAL_PHYSICAL_TAKEN, offset);
        value += getOffsetStat(Stats.TOTAL_MAGICAL_TAKEN, offset);
        break;
      case Stats.TOTAL_ATTACK:
        value += getOffsetStat(Stats.TOTAL_PHYSICAL_ATTACK, offset);
        value += getOffsetStat(Stats.TOTAL_MAGICAL_ATTACK, offset);
        value += getOffsetStat(Stats.TOTAL_DRAGOON_PHYSICAL_ATTACK, offset);
        value += getOffsetStat(Stats.TOTAL_DRAGOON_MAGICAL_ATTACK, offset);
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

  private static float getOffsetStat(final Stats stat, final int offset) {
    return getStat(stat, Math.abs(stat.asInt()) * 2 + offset);
  }

  public static float[] getStats(final int statIndex) {
    return getStats(Stats.asStat(statIndex));
  }

  public static float[] getStats(final Stats stat) {
    final float[] stats;
    final int statIndex = Math.abs(stat.asInt());
    if(statIndex > 0 && statIndex < 9999) {
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

  private static float getPercentTotalValue(final Stats stat, final int charIndex, final boolean isTotal, final int displayMode) {
    if(displayMode == 2) {
        return getArraySum(getStats(stat));
    }
    if(displayMode == 1) {
      final Stats totalStat = switch(stat) {
        case Stats.TOTAL_PHYSICAL_DAMAGE, Stats.TOTAL_MAGICAL_DAMAGE -> Stats.TOTAL_DAMAGE;
        case Stats.TOTAL_PHYSICAL_TAKEN, Stats.TOTAL_MAGICAL_TAKEN -> Stats.TOTAL_TAKEN;
        case Stats.TOTAL_PHYSICAL_ATTACK, Stats.TOTAL_MAGICAL_ATTACK, Stats.TOTAL_DRAGOON_PHYSICAL_ATTACK, Stats.TOTAL_DRAGOON_MAGICAL_ATTACK -> Stats.TOTAL_ATTACK;
        case Stats.TOTAL_ADDITION_COMPLETE, Stats.TOTAL_ADDITION_FLAWLESS -> Stats.TOTAL_ADDITION;
        case Stats.TOTAL_DRAGOON_ADDITION_COMPLETED -> Stats.TOTAL_DRAGOON_ADDITION;
        default -> null;
      };
      if(totalStat != null) {
        if(totalStat.asInt() != 0) {
          return isTotal ? getArraySum(getStats(totalStat.asInt())) : getStat(Math.abs(totalStat.asInt()) + charIndex);
        }
        return isTotal ? getArraySum(getStats(totalStat)) : getStat(totalStat, charIndex);
      }
    }
    return 0;
  }

  public static String getDisplayValue(final float value, final Stats stat, final int charIndex, final boolean isTotal, final int displayMode) {
    if(displayMode == 1 || displayMode == 2) {
      float total = getPercentTotalValue(stat, charIndex + 1, isTotal, displayMode);
      if(total == 0) {
        total = value;
      }
      return total > 0 ? String.valueOf(Math.round(value / total * 100)) + '%' : "0%";
    }
    return stat.format != null && !stat.format.equals("b") ? String.format(stat.format, value) : String.valueOf((int)value);
  }

  private static float getArraySum(final float[] array) {
    float total = 0f;
    for(final float value : array) {
      total += value;
    }
    return total;
  }
}