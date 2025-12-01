package legend.game.statistics;

import legend.game.combat.ui.TrackerHud;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static legend.game.Audio.playMenuSound;

public final class Bestiary {
  public static final int RANK_2 = 3;
  public static final int RANK_3 = 5;

  public static final boolean devMode = false;
  public static List<BestiaryEntry> bestiaryEntries;

  private Bestiary() {
  }

  private static void addEntry(final int charId, final int subEntryParentId, final int killCount, @Nullable final String altName, @Nullable final String listName, final String map, final String region, final String lore) {
    final BestiaryEntry newEntry = new BestiaryEntry(charId, subEntryParentId, killCount, altName, listName, map, region, lore);
    if(!newEntry.isSubEntry) {
      bestiaryEntries.add(newEntry);
    }
  }

  public static int[] getElementBackgroundRGB(final int elementFlag) {
    return switch(elementFlag) {
      case 1 -> new int[] { 0, 196, 255, 35, 0, 144, 255, 80 };      //Water
      case 2 -> new int[] { 81, 55, 0, 35, 159, 108, 0, 80 };          //Earth
      case 4 -> new int[] { 0, 30, 255, 40, 40, 30, 227, 80 };        //Dark
      case 8 -> new int[] { 200, 200, 200, 40, 230, 230, 230, 80 };  //Divine
      case 16 -> new int[] { 97, 0, 196, 35, 129, 45, 255, 80 };       //Thunder
      case 32 -> new int[] { 255, 255, 53, 40, 255, 246, 0, 80 };   //Light
      case 64 -> new int[] { 0, 236, 94, 35, 0, 236, 94, 80 };       //Wind
      case 128 -> new int[] { 255, 15, 0, 25, 225, 6, 0, 80 };      //Fire
      default -> new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    };
  }

  public static BestiaryEntry getEntryByCharId(final int charId) {
    for(final BestiaryEntry entry : bestiaryEntries) {
      if(entry.charId == charId) {
        return entry;
      }
    }
    return null;
  }

  public static void loadTrackers() {
    final String stat = Statistics.getStatRaw(Statistics.Stats.TRACKER_BESTIARY);
    if(!stat.isEmpty()) {
      final String[] charIds = stat.split(",");
      for(final String id : charIds) {
        final BestiaryEntry entry = getEntryByCharId(Integer.parseInt(id));
        if(entry != null) {
          addTracker("bestiary:" + id, entry, true);
        }
      }
    }
  }

  public static void toggleTracker(final BestiaryEntry entry) {
    if(entry.rank > 0 && entry.rank < 3 || entry.rank == -1) {
      final String trackerID = "bestiary:" + entry.charId;
      if(TrackerHud.exists(trackerID)) {
        playMenuSound(3);
        removeTracker(trackerID, entry);
      } else {
        if(addTracker(trackerID, entry, false)) {
          playMenuSound(2);
        } else {
          playMenuSound(40);
        }
      }
    }
  }

  private static boolean addTracker(final String trackerID, final BestiaryEntry entry, final boolean isLoad) {
    if(TrackerHud.getTrackers("bestiary").size() < 10) {
      TrackerHud.add(trackerID, entry.name, Math.max(0, entry.kill), entry.maxKill < 0 ? RANK_3 : entry.maxKill);
      if(!isLoad) {
        String stat = Statistics.getStatRaw(Statistics.Stats.TRACKER_BESTIARY);
        stat += (stat.isEmpty() ? "" : ",") + entry.charId;
        Statistics.setStat(Statistics.Stats.TRACKER_BESTIARY, stat);
      }
      return true;
    }
    return false;
  }

  private static void removeTracker(final String trackerID, final BestiaryEntry entry) {
    TrackerHud.remove(trackerID);
    final String[] charIds = Statistics.getStatRaw(Statistics.Stats.TRACKER_BESTIARY).split(",");
    final StringBuilder stat = new StringBuilder();
    for(final String id : charIds) {
      if(!id.equals(String.valueOf(entry.charId))) {
        stat.append((stat.isEmpty()) ? "" : ",").append(id);
      }
    }
    Statistics.setStat(Statistics.Stats.TRACKER_BESTIARY, stat.toString());
  }

  public static void checkTrackers() {
    if(bestiaryEntries == null) {
      loadEntries();
    }

    for(final Map.Entry<String, TrackerHud.Tracker> t : TrackerHud.getTrackers("bestiary").entrySet()) {
      final int charID = Integer.parseInt(t.getKey().replace("bestiary:", ""));
      final BestiaryEntry entry = getEntryByCharId(charID);
      if(entry != null) {
        entry.reloadStatus();
        if(entry.isComplete()) {
          TrackerHud.remove(t.getKey());
        } else {
          final TrackerHud.Tracker tracker = t.getValue();
          tracker.number = Math.max(0, entry.kill);
        }
      }
    }
  }

  public static void loadEntries() {
    bestiaryEntries = new ArrayList<>(); //https://docs.google.com/spreadsheets/d/111ZSCvhnfoxKxtmLeLXeHuviHhM3ZNt2vBxFww9pO_g/
    addEntry(257, -1, 4, null, null, "Seles", "Southern Serdio", "A rank-and-file knight in the service of Sandora.\nUses a combination of ranged knives and sword attacks.");
    addEntry(256, -1, 1, null, null, "Seles", "Southern Serdio", "Commander lingering near Seles after it was raided by Sandora.\nHas a basic attack, combo slash, and items at their disposal.");
    addEntry(21, -1, -1, null, null, "Forest", "Southern Serdio", "A cocky bird. Will slash with talons and crow at its enemies.");
    addEntry(24, -1, -1, null, null, "Forest", "Southern Serdio", "A crazed rodent in the Forest. Will often bite.\nAlso has a strange \"Chisel\" attack.");
    addEntry(8, -1, -1, null, null, "Forest", "Southern Serdio", "Strong fighter in the Prairie.\nSeems weak, but does very high damage\nwhen at low health.");
    addEntry(38, -1, -1, null, null, "Forest", "Southern Serdio", "The most durable creature in the Forest. High physical defense.\nWill snap its branches to attack, as well as Pellet.");
    addEntry(134, -1, -1, null, null, "Hellena Prison", "Southern Serdio", "The standard-issue guards of Hellena Prison. They use a long mace in\ncombat, and also carry magical attack items. Their job is to patrol\nthe prison and keep it secure, ensuring no-one escapes.");
    addEntry(261, -1, 1, null, null, "Hellena Prison", "Southern Serdio", "Age: 48\nHeight: 190 cm / 6'3\"\n\nHated by his own country, Fruegel is relegated to lording\nover Hellena Prison. A vain pleasure-seeker adorned with decorations. \nHis pets Guftas and Rodriguez are his only friends.\n\nCombat: club bash, body slam, boulder throw, call allies.");
    addEntry(259, 261, -1, null, null, "Hellena Prison", "Southern Serdio", "The standard-issue guards of Hellena Prison. They use a long mace in\ncombat, and also carry magical attack items. Their job is to patrol\nthe prison and keep it secure, ensuring no-one escapes.");
    addEntry(260, 261, -1, null, null, "Hellena Prison", "Southern Serdio", "The Senior Warden is stronger and more deadly. They wield\ndouble-edged spears and carry magic attack items. \n\nThe Senior Warden also has access to Power Up. This makes them\nharder to defeat, but it can also make their magical attack deadly.");
    addEntry(59, -1, -1, null, null, "Prairie", "Southern Serdio", "bzzzzzt!");
    addEntry(0, -1, -1, null, null, "Prairie", "Southern Serdio", "A strong creature,featuring high physical stats\nand Power Up. Weaker to magic.");
    addEntry(45, -1, -1, null, null, "Prairie", "Southern Serdio", "");
    addEntry(27, -1, -1, null, null, "Prairie", "Southern Serdio", "Notable for its long proboscis and large ears. it's mostly harmless.\nHowever, when its health is low it will suck blood to restore itself.");
    addEntry(13, -1, -1, null, null, "Limestone Cave", "Northern Serdio", "");
    addEntry(94, -1, -1, null, null, "Limestone Cave", "Northern Serdio", "");
    addEntry(46, -1, -1, null, null, "Limestone Cave", "Northern Serdio", "A common bat residing in the Limestone Cave. Although very weak,\nits special ability can confuse people into attacking themselves\nor their friends.");
    addEntry(104, -1, -1, null, null, "Limestone Cave", "Northern Serdio", "A living ooze, the Slime's standard attack can render enemies unable to\nattack in return. Its special ability deals increased damage.");
    //addEntry(148, -1, -1, null, null, "Dragon's Nest", "Southern Serdio", "Unknown variant - pending investigation");
    addEntry(64, -1, -1, null, null, "Limestone Cave", "Northern Serdio", "The Ugly Balloon is a creature of the Limestone Cave. It can fly at high\nspeeds. Though it has low defense and attack, it can poison its enemies\nfor high damage over time.");
    addEntry(332, -1, 1, null, null, "Limestone Cave", "Northern Serdio", "A large snake-like creature, Urobolus is the guardian of Limestone Cave.\nIt has strong physical attacks and can inflict poison frequently. \nPeriodically it will retreat to a high place, avoiding melee attacks.");
    addEntry(138, -1, 1, null, null, "Endiness", "Limestone Cave <-> Bale", "A rare bird seen only along the road between Limestone Cave and\nBale. \n\nLike all Rare creatures, it has a high chance of running away, but\ndrops great rewards if defeated.");
    addEntry(130, -1, 1, null, null, "Hoax", "Northern Serdio", "");
    addEntry(258, -1, 1, null, null, "Hoax", "Northern Serdio", "The first known human who can use innate magic. However, the Elite\nwas not born with it. Researchers at the black castle figured out a way\nto infuse a human with magical powers - possibly with the help\nof Emperor Diaz.\n\nThe Elite has a few physical attacks, but its signature moves are magical.\nThe first is a green flame that deals moderate AoE damage. \nThe second summons apparitions which take no damage.");
    addEntry(273, 258, -1, "Sandora Elite (Clone)", null, "Black Castle", "Southern Serdio", "");
    addEntry(265, -1, 1, null, null, "Hoax", "Northern Serdio", "Age: \nHeight: 250 cm / \n\nThe last of his species, Kongol is a towering giant with unmatched\nphysical prowess. He wields a large axe, but can fight without it.\n\nAs a child his hometown was raided by bandits. At the last moment he\nwas saved by Doel, who then raised him.");
    addEntry(68, -1, -1, null, null, "Marshland", "Northern Serdio", "");
    addEntry(22, -1, -1, null, null, "Marshland", "Northern Serdio", "An aquatic creature found in the Marshland. Although the basic attack\ndeals little damage, Mermen also have Water magic that is lethal to\nenemies with low defense or a Fire attribute.");
    addEntry(28, -1, -1, null, null, "Marshland", "Northern Serdio", "");
    addEntry(4, -1, -1, null, null, "Marshland", "Northern Serdio", "");
    addEntry(131, -1, -1, null, null, "Marshland", "Northern Serdio", "");
    addEntry(132, -1, -1, null, null, "Marshland", "Northern Serdio", "");
    addEntry(135, -1, 1, null, null, "Marshland", "Northern Serdio", "");
    addEntry(33, -1, -1, null, null, "Villude Volcano", "Southern Serdio", "");
    //addEntry(150, -1, -1, null, null, "Villude Volcano", "Southern Serdio", "");
    addEntry(74, -1, -1, null, null, "Villude Volcano", "Southern Serdio", "");
    //addEntry(157, -1, -1, null, null, "Villude Volcano", "Southern Serdio", "");
    addEntry(100, -1, -1, null, null, "Villude Volcano", "Southern Serdio", "");
    //addEntry(146, -1, -1, null, null, "Villude Volcano", "Southern Serdio", "");
    addEntry(92, -1, -1, null, null, "Villude Volcano", "Southern Serdio", "A large creature with its back constantly ablaze. The Salamander's\nbasic attack can stun enemies, which becomes dangerous when it\nattacks the same target multiple times.");
    addEntry(308, -1, 1, "Wounded Virage (Head)", "Wounded Virage", "Villude Volcano", "Southern Serdio", "A relic of the Dragon Campaign, this Virage was seemingly petrified\nwithin the Volcano for millenia. It activates to attack Dart's party.\n\nThis Virage is not at full strength. It is clearly damaged:\nmissing one arm, both legs, and more. Despite this, it is still deadly.\nThis Virage can inflict an assortment of status ailments, and uses a\nstaggeringly powerful beam laser.");
    addEntry(309, 308, -1, "Wounded Virage (Body)", null, "Villude Volcano", "Southern Serdio", "");
    addEntry(310, 308, -1, "Wounded Virage (Arm)", null, "Villude Volcano", "Southern Serdio", "");
    addEntry(333, -1, 1, null, null, "Villude Volcano", "Southern Serdio", "An elemental creature that patrols the volcano. Fire Bird may pursue\npassersby who attempt to traverse the molten crags. \n\nMost of its abilities have an area of effect, and are of the Fire element.\n\nAlso known as Piton.");
    addEntry(334, 333, -1, null, null, "Villude Volcano", "Southern Serdio", "An elemental creature that patrols the volcano. Fire Bird may pursue\npassersby who attempt to traverse the molten crags. \n\nMost of its abilities have an area of effect, and are of the Fire element.\n\nAlso known as Piton.");
    addEntry(19, -1, -1, null, null, "Dragon's Nest", "Southern Serdio", "");
    addEntry(88, -1, -1, "Man-Eating Bud", null, "Dragon's Nest", "Southern Serdio", "");
    addEntry(90, -1, -1, null, null, "Dragon's Nest", "Southern Serdio", "");
    addEntry(35, -1, -1, null, null, "Dragon's Nest", "Southern Serdio", "");
    addEntry(115, -1, -1, null, null, "Dragon's Nest", "Southern Serdio", "");
    addEntry(287, -1, 1, null, null, "Dragon's Nest", "Southern Serdio", "");
    addEntry(275, -1, 1, null, null, "Dragon's Nest", "Southern Serdio", "");
    addEntry(87, -1, 1, null, null, "Endiness", "Dragon's Nest <-> Lohan", "");
    addEntry(302, -1, 1, null, null, "Lohan", "Southern Serdio", "");
    addEntry(303, -1, 1, null, null, "Lohan", "Southern Serdio", "");
    addEntry(304, -1, 1, null, null, "Lohan", "Southern Serdio", "");
    addEntry(305, -1, 1, null, null, "Lohan", "Southern Serdio", "");
    addEntry(269, -1, -2, null, null, "Lohan", "Southern Serdio", "");
    addEntry(66, -1, -1, null, null, "Shirley's Shrine", "Southern Serdio", "");
    addEntry(58, -1, -1, null, null, "Shirley's Shrine", "Southern Serdio", "");
    addEntry(55, -1, -1, null, null, "Shirley's Shrine", "Southern Serdio", "");
    addEntry(107, -1, -1, null, null, "Shirley's Shrine", "Southern Serdio", "");
    addEntry(15, -1, -1, null, null, "Shirley's Shrine", "Southern Serdio", "");
    addEntry(325, -1, 1, null, null, "Shirley's Shrine", "Southern Serdio", "");
    addEntry(326, 325, -1, null, null, "Shirley's Shrine", "Southern Serdio", "");
    addEntry(327, 325, -1, null, null, "Shirley's Shrine", "Southern Serdio", "");
    addEntry(288, -1, 1, null, null, "Shirley's Shrine", "Southern Serdio", "");
    addEntry(105, -1, 4, null, null, "Hellena Prison", "Southern Serdio", "");
    addEntry(129, -1, -1, null, null, "Hellena Prison", "Southern Serdio", "");
    addEntry(133, -1, -1, null, null, "Hellena Prison", "Southern Serdio", "");
    addEntry(329, -1, 1, null, null, "Hellena Prison", "Southern Serdio", "");
    addEntry(262, -1, 1, "Fruegel II", null, "Hellena Prison", "Southern Serdio", "");
    addEntry(264, -1, 1, null, null, "Hellena Prison", "Southern Serdio", "");
    addEntry(263, -1, 1, null, null, "Hellena Prison", "Southern Serdio", "");
    addEntry(42, -1, 1, null, null, "Endiness / Moon", "Road Near Kazas", "");
    addEntry(17, -1, -1, null, null, "Black Castle", "Southern Serdio", "");
    addEntry(103, -1, -1, null, null, "Black Castle", "Southern Serdio", "");
    addEntry(102, -1, 1, null, null, "Black Castle", "Southern Serdio", "");
    addEntry(266, -1, 1, "Kongol (Armored)", null, "Black Castle", "Southern Serdio", "");
    addEntry(267, -1, 1, null, null, "Black Castle", "Southern Serdio", "");
    addEntry(268, 267, -1, "Emperor Doel (Dragoon)", null, "Black Castle", "Southern Serdio", "");
    addEntry(99, -1, -1, null, null, "Barrens", "Tiberoa", "");
    addEntry(26, -1, -1, null, null, "Barrens", "Tiberoa", "");
    addEntry(69, -1, -1, null, null, "Barrens", "Tiberoa", "");
    addEntry(1, -1, -1, null, null, "Barrens", "Tiberoa", "Decent physical attacker with good defense.\nWeaker to magic.");
    addEntry(108, -1, -1, null, null, "Barrens", "Tiberoa", "");
    addEntry(299, -1, 1, null, null, "Barrens", "Tiberoa", "");
    addEntry(274, 299, -1, null, null, "Barrens", "Tiberoa", "");
    addEntry(136, -1, 1, null, null, "Endiness", "Barrens <-> Home of Gigantos", "");
    addEntry(16, -1, -1, null, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    addEntry(52, -1, -1, null, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    addEntry(106, -1, -1, null, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    addEntry(29, -1, -1, null, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    addEntry(14, -1, -1, null, null, "Valley of Corrupted Gravity", "Tiberoa", "");
    addEntry(311, -1, 1, "Virage (Head)", "Virage", "Valley of Corrupted Gravity", "Tiberoa", "A relic of the Dragon Campaign, this Virage laid dormant in the Valley\nfollowing a magic-intensive battle between Winglies and Humans.\n\nUnlike the one found in Serdio, this Virage is at full strength. It has all\nlimbs intact, and has many more abilities at its disposal. It can stomp\nwith its legs, conjure an ethereal energy wave, and can kill someone\nby smashing them into the central green energy cluster on its head.");
    addEntry(313, 311, -1, "Virage (Body)", null, "Valley of Corrupted Gravity", "Tiberoa", "");
    addEntry(312, 311, -1, "Virage (Arm)", null, "Valley of Corrupted Gravity", "Tiberoa", "");
    addEntry(79, -1, -1, null, null, "Giganto Home", "Tiberoa", "");
    addEntry(83, -1, -1, null, null, "Giganto Home", "Tiberoa", "");
    addEntry(82, -1, -1, null, null, "Giganto Home", "Tiberoa", "");
    addEntry(109, -1, -1, null, null, "Giganto Home", "Tiberoa", "");
    addEntry(301, -1, 1, null, null, "Giganto Home", "Tiberoa", "");
    addEntry(300, -1, 1, null, null, "Giganto Home", "Tiberoa", "");
    addEntry(293, -1, 1, null, null, "Fletz", "Tiberoa", "");
    addEntry(18, -1, -1, null, null, "Phantom Ship", "Illisa Bay", "");
    addEntry(85, -1, -1, null, null, "Phantom Ship", "Illisa Bay", "");
    addEntry(86, -1, -1, null, null, "Phantom Ship", "Illisa Bay", "");
    //addEntry(154, -1, -1, null, null, "Phantom Ship", "Illisa Bay", "");
    //addEntry(155, -1, -1, null, null, "Phantom Ship", "Illisa Bay", "");
    addEntry(70, -1, 4, null, null, "Phantom Ship", "Illisa Bay", "");
    addEntry(340, -1, 1, null, null, "Phantom Ship", "Illisa Bay", "");
    addEntry(341, -1, 1, null, null, "Phantom Ship", "Illisa Bay", "");
    addEntry(12, -1, -1, null, null, "Undersea Cavern", "Illisa Bay", "");
    addEntry(23, -1, -1, null, null, "Undersea Cavern", "Illisa Bay", "");
    addEntry(91, -1, -1, null, null, "Undersea Cavern", "Illisa Bay", "");
    addEntry(76, -1, -1, null, null, "Undersea Cavern", "Illisa Bay", "");
    addEntry(110, -1, -1, null, null, "Undersea Cavern", "Illisa Bay", "");
    addEntry(294, -1, 1, "Lenus (Dragoon)", null, "Undersea Cavern", "Illisa Bay", "");
    addEntry(279, -1, 1, null, null, "Undersea Cavern", "Illisa Bay", "");
    addEntry(124, -1, 1, null, null, "Endiness", "Undersea Cavern <-> Fueno", "");
    addEntry(137, -1, 1, null, null, "Endiness", "Furni <-> Deningrad", "");
    addEntry(95, -1, -1, null, null, "Evergreen Forest", "Mille Seseau", "");
    addEntry(53, -1, -1, null, null, "Evergreen Forest", "Mille Seseau", "");
    addEntry(73, -1, -1, null, null, "Evergreen Forest", "Mille Seseau", "");
    addEntry(117, -1, -1, null, null, "Evergreen Forest", "Mille Seseau", "");
    addEntry(51, -1, -1, null, null, "Evergreen Forest", "Mille Seseau", "");
    addEntry(343, -1, 1, null, null, "Evergreen Forest", "Mille Seseau", "");
    addEntry(43, -1, -1, null, null, "Kadessa", "Mille Seseau", "");
    addEntry(30, -1, -1, null, null, "Kadessa", "Mille Seseau", "");
    addEntry(44, -1, -1, null, null, "Kadessa", "Mille Seseau", "");
    addEntry(111, -1, -1, null, null, "Kadessa", "Mille Seseau", "");
    addEntry(2, -1, -1, null, null, "Kadessa", "Mille Seseau", "");
    addEntry(316, -1, 1, "Wounded Super Virage (Head)", "Wounded Super Virage", "Kadessa", "Mille Seseau", "A relic of the Dragon Campaign, this is a more powerful type of Virage.\nIt was nearly defeated by Kanzas during the final battle of the Dragon\nCampaign, at the Wingly capital Kadessa.\n\nThis Virage barely survived, but is still formiddable. It has a variety of\nattacks, and will self-destruct if not defeated quickly.");
    addEntry(317, 316, -1, "Wounded Super Virage (Body)", null, "Kadessa", "Mille Seseau", "");
    addEntry(318, 316, -1, "Wounded Super Virage (Arm)", null, "Kadessa", "Mille Seseau", "");
    addEntry(335, -1, 1, null, null, "Kadessa", "Mille Seseau", "");
    addEntry(96, -1, -1, null, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    addEntry(6, -1, -1, null, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    addEntry(113, -1, -1, null, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    addEntry(112, -1, -1, null, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    addEntry(37, -1, -1, null, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    addEntry(283, -1, 1, null, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    addEntry(284, 283, -1, null, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    addEntry(285, 283, -1, null, null, "Mortal Dragon Mountain", "Mille Seseau", "");
    addEntry(32, -1, -1, null, null, "Kashua Glacier", "Mille Seseau", "");
    addEntry(114, -1, -1, null, null, "Kashua Glacier", "Mille Seseau", "");
    addEntry(5, -1, -1, null, null, "Kashua Glacier", "Mille Seseau", "");
    addEntry(48, -1, -1, null, null, "Kashua Glacier", "Mille Seseau", "");
    addEntry(67, -1, -1, null, null, "Kashua Glacier", "Mille Seseau", "");
    addEntry(346, -1, 1, null, null, "Kashua Glacier", "Mille Seseau", "");
    addEntry(348, 346, -1, "Windigo (Heart)", null, "Kashua Glacier", "Mille Seseau", "");
    addEntry(347, 346, -1, null, null, "Kashua Glacier", "Mille Seseau", "");
    addEntry(270, -1, 1, "Lloyd (Wingly Armor)", null, "Flanvel Tower", "Mille Seseau", "");
    addEntry(11, -1, -1, null, null, "Flanvel Tower", "Mille Seseau", "");
    addEntry(25, -1, -1, null, null, "Flanvel Tower", "Mille Seseau", "");
    addEntry(56, -1, -1, null, null, "Flanvel Tower", "Mille Seseau", "");
    //addEntry(144, -1, -1, null, null, "Flanvel Tower", "Mille Seseau", "");
    addEntry(49, -1, -1, null, null, "Flanvel Tower", "Mille Seseau", "");
    addEntry(344, -1, 1, null, null, "Flanvel Tower", "Mille Seseau", "");
    addEntry(31, -1, -1, null, null, "Snowfield", "Gloriano", "");
    addEntry(116, -1, -1, null, null, "Snowfield", "Gloriano", "");
    addEntry(20, -1, -1, null, null, "Snowfield", "Gloriano", "");
    addEntry(57, -1, -1, null, null, "Snowfield", "Gloriano", "");
    addEntry(50, -1, -1, null, null, "Snowfield", "Gloriano", "");
    addEntry(350, -1, 1, null, null, "Snowfield", "Gloriano", "");
    addEntry(349, 350, -1, null, null, "Snowfield", "Gloriano", "");
    addEntry(351, 350, -1, null, null, "Snowfield", "Gloriano", "");
    addEntry(65, -1, -1, null, null, "Vellweb", "Gloriano", "");
    addEntry(47, -1, -1, null, null, "Vellweb", "Gloriano", "");
    addEntry(39, -1, -1, null, null, "Vellweb", "Gloriano", "");
    addEntry(41, -1, -1, null, null, "Vellweb", "Gloriano", "");
    addEntry(63, -1, -1, null, null, "Vellweb", "Gloriano", "");
    addEntry(89, -1, -1, null, null, "Death Frontier", "Death Frontier", "");
    addEntry(36, -1, -1, null, null, "Death Frontier", "Death Frontier", "");
    addEntry(3, -1, -1, null, null, "Death Frontier", "Death Frontier", "Physical attacker often accompanied by other\ncreatures. Has a special sand attack.");
    addEntry(119, -1, -1, null, null, "Death Frontier", "Death Frontier", "");
    addEntry(61, -1, -1, null, null, "Death Frontier", "Death Frontier", "");
    addEntry(139, -1, 1, null, null, "Endiness", "Death Frontier <-> Ulara", "");
    addEntry(54, -1, 1, null, null, "Endiness", "Fletz <-> Rouge", "");
    addEntry(121, -1, -1, null, null, "Aglis", "Broken Islands", "");
    addEntry(60, -1, -1, null, null, "Aglis", "Broken Islands", "");
    addEntry(9, -1, -1, null, null, "Aglis", "Broken Islands", "");
    addEntry(75, -1, -1, null, null, "Aglis", "Broken Islands", "");
    addEntry(72, -1, -1, null, null, "Aglis", "Broken Islands", "");
    addEntry(365, -1, 1, null, null, "Aglis", "Broken Islands", "");
    addEntry(366, 365, -1, null, null, "Aglis", "Broken Islands", "");
    addEntry(118, -1, -1, null, null, "Zenebatos", "Gloriano", "");
    addEntry(40, -1, -1, null, null, "Zenebatos", "Gloriano", "");
    addEntry(97, -1, -1, null, null, "Zenebatos", "Gloriano", "");
    addEntry(80, -1, -1, null, null, "Zenebatos", "Gloriano", "");
    addEntry(122, -1, -1, null, null, "Zenebatos", "Gloriano", "");
    addEntry(360, -1, 1, null, null, "Zenebatos", "Gloriano", "");
    addEntry(362, -1, 1, null, null, "Zenebatos", "Gloriano", "");
    addEntry(361, -1, 1, null, null, "Zenebatos", "Gloriano", "");
    addEntry(297, -1, 1, null, null, "Vellweb", "Gloriano", "");
    addEntry(295, -1, 1, null, null, "Vellweb", "Gloriano", "");
    addEntry(298, -1, 1, null, null, "Vellweb", "Gloriano", "");
    addEntry(296, -1, 1, null, null, "Vellweb", "Gloriano", "");
    //addEntry(101, -1, -1, null, null, "Mayfil", "Gloriano", "");
    addEntry(152, -1, -1, null, null, "Mayfil", "Gloriano", "");
    addEntry(125, -1, -1, null, null, "Mayfil", "Gloriano", "");
    addEntry(10, -1, -1, null, null, "Mayfil", "Gloriano", "");
    addEntry(78, -1, -1, null, null, "Mayfil", "Gloriano", "");
    addEntry(84, -1, -1, null, null, "Mayfil", "Gloriano", "");
    addEntry(354, -1, 1, "Feyrbrand's Spirit", null, "Mayfil", "Gloriano", "");
    addEntry(353, -1, 1, "Regole's Spirit", null, "Mayfil", "Gloriano", "");
    addEntry(352, -1, 1, "Divine Dragon's Spirit", null, "Mayfil", "Gloriano", "");
    addEntry(363, -1, 1, null, null, "Mayfil", "Gloriano", "");
    addEntry(364, 363, -1, null, null, "Mayfil", "Gloriano", "");
    addEntry(98, -1, -1, null, null, "Divine Tree", "Gloriano", "");
    addEntry(7, -1, -1, null, null, "Divine Tree", "Gloriano", "");
    addEntry(126, -1, -1, null, null, "Divine Tree", "Gloriano", "");
    addEntry(77, -1, -1, null, null, "Divine Tree", "Gloriano", "");
    addEntry(62, -1, -1, null, null, "Divine Tree", "Gloriano", "");
    addEntry(370, -1, 1, null, null, "Divine Tree", "Gloriano", "");
    addEntry(368, 370, -1, null, null, "Divine Tree", "Gloriano", "");
    addEntry(369, 370, -1, null, null, "Divine Tree", "Gloriano", "");
    addEntry(123, -1, -1, null, null, "Moon", "Gloriano", "");
    addEntry(34, -1, -1, null, null, "Moon", "Gloriano", "");
    addEntry(81, -1, -1, null, null, "Moon", "Gloriano", "");
    addEntry(127, -1, -1, null, null, "Moon", "Gloriano", "");
    addEntry(128, -1, -1, null, null, "Moon", "Gloriano", "");
    addEntry(120, -1, -1, null, null, "Moon", "Gloriano", "");
    addEntry(71, -1, -1, null, null, "Moon", "Gloriano", "");
    addEntry(93, -1, -1, null, null, "Moon", "Gloriano", "");
    addEntry(371, -1, 1, null, null, "Moon", "Gloriano", "");
    addEntry(373, -1, 1, null, null, "Moon", "Gloriano", "");
    addEntry(375, -1, 1, null, null, "Moon", "Gloriano", "");
    addEntry(378, -1, 1, null, null, "Moon", "Gloriano", "");
    addEntry(380, 378, -1, "Michael (Core)", null, "Moon", "Gloriano", "");
    addEntry(381, -1, 1, null, null, "Moon", "Gloriano", "");
    addEntry(376, 381, -1, null, null, "Moon", "Gloriano", "");
    addEntry(377, 381, -1, null, null, "Moon", "Gloriano", "");
    addEntry(382, -1, 1, null, null, "Moon", "Gloriano", "");
    addEntry(320, -1, 1, "Super Virage (Head)", "Super Virage", "Moon", "Gloriano", "A relic of the Dragon Campaign, this Super Virage is the last natural line\nof defense for the primary Virage Embryo. Unlike the one found in the\nruins of Kadessa, this one won't self-detonate, and must be defeated.");
    addEntry(322, 320, -1, "Super Virage (Body)", null, "Moon", "Gloriano", "");
    addEntry(321, 320, -1, "Super Virage (Arm)", null, "Moon", "Gloriano", "");
    addEntry(387, -1, 1, null, null, "Moon", "Gloriano", "Age: 28\nHeight: 181 cm / 5'9\"\n\nZieg is a legendary warrior from the time of the Dragon Campaign.\nHe normally would have perished long ago, but in the final battle he\nwas petrified by Melbu Frahma for over 11,000 years. \n\nWhen the spell wore off, he tried to start a new life. However, as his\nhometown was attacked, he tried to save it. When he activated\nhis Dragoon Spirit, Melbu's spirit came out instead, possessing him. \n\nZieg was not himself for 18 years. Melbu would use his body to create\na new scheme to destroy the world, and remake it in his image.\n\nOn the Moon, Melbu (as ZIeg) swipes Dart's Dargoon Spirit and transforms.\nHe has his own style of Dart's Dragoon abilities, proving very powerful.\n\nAfter being defeated, Zieg is finally himself again, and gets to speak\nwith his fiancé Rose one last time.");
    //addEntry(388, -1, 1, null, null, "Moon", "Gloriano", "A Wingly dictator, Melbu Frahma was obsessed with power. He sees\nhimself as superior, both during the Dragon Campaign and now. \nHis magic was so strong that he needed a restraining device to keep\nit under control. Under his rule, many species were subjugated.\n\nAlthough defeated in the final battle of the Dragon Campaign, he would\npreserve himself within Zieg's Dragoon Spirit, biding his time.\n\nOnce ZIeg unwittingly released him, Melbu began a new plot to\nremake the world in his image. Eventually, Melbu would reach the Moon\nand stop the");
    addEntry(389, 388, -1, null, null, "Moon", "Gloriano", "");
    addEntry(390, 388, -1, null, null, "Moon", "Gloriano", "");
    addEntry(391, 388, -1, null, null, "Moon", "Gloriano", "");
    addEntry(392, 388, -1, null, null, "Moon", "Gloriano", "");
    addEntry(393, 388, -1, null, null, "Moon", "Gloriano", "");
    addEntry(395, 388, -1, null, null, "Moon", "Gloriano", "");
    addEntry(396, 388, -1, null, null, "Moon", "Gloriano", "");
    addEntry(397, 388, -1, null, null, "Moon", "Gloriano", "");
  }
}
