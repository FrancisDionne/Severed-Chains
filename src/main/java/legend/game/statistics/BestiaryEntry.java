package legend.game.statistics;

import legend.game.inventory.screens.BestiaryScreen;
import legend.game.modding.events.battle.MonsterStatsEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static legend.game.combat.Monsters.monsterNames_80112068;
import static legend.game.statistics.Bestiary.RANK_2;
import static legend.game.statistics.Bestiary.RANK_3;
import static legend.game.statistics.Bestiary.bestiaryEntries;
import static legend.game.statistics.Bestiary.devMode;
import static legend.game.statistics.Bestiary.getElementBackgroundRGB;

public final class BestiaryEntry {
  public int entryNumber;
  public int charId;
  public String location;
  public MonsterStatsEvent stats;
  public String lore;
  public int kill;
  public int rank;
  public String name;
  public String listName;
  public int maxKill;
  public float[] elementRGB;
  public List<BestiaryEntry> subEntries;
  public boolean isSubEntry;

  public BestiaryEntry(final int charId, final int subEntryParentId, final int maxKill, @Nullable final String altName, @Nullable final String listName, final String map, final String region, final String lore) {
    this.charId = charId;
    this.stats = new MonsterStatsEvent(charId);
    this.name = altName == null ? monsterNames_80112068[this.charId] : altName;
    this.lore = devMode ? lore : "";
    this.listName = listName;
    this.maxKill = maxKill;

    if(subEntryParentId > -1) {
      this.isSubEntry = true;
      final BestiaryEntry parentEntry = Bestiary.getEntryByCharId(subEntryParentId);
      if(parentEntry != null) {
        if(parentEntry.subEntries == null) {
          parentEntry.subEntries = new ArrayList<>();
        }
        parentEntry.subEntries.add(this);
        this.entryNumber = parentEntry.entryNumber;
        this.location = parentEntry.location;
      }
    } else {
      this.entryNumber = bestiaryEntries.size() + 1;
      this.location = map + (region.isEmpty() ? "" : " - " + region);
    }

    this.reloadStatus();
  }

  public void reloadStatus() {
    this.kill = Statistics.getMonsterKill(this.charId);

    final int[] elementRGB = getElementBackgroundRGB(this.stats.elementFlag.flag);
    this.elementRGB = new float[] { elementRGB[0] / 255f, elementRGB[1] / 255f, elementRGB[2] / 255f, elementRGB[3] / 100f * 0.8f, elementRGB[4] / 255f, elementRGB[5] / 255f, elementRGB[6] / 255f, elementRGB[7] / 100f * 1f};

    if(this.kill >= RANK_3 || (this.maxKill > -1 && this.kill >= this.maxKill) || (this.maxKill == -2 && this.kill == -1) || this.isSubEntry) {
      this.rank = 3;
    } else if(this.kill >= RANK_2) {
      this.rank = 2;
    } else if(this.kill >= 1) {
      this.rank = 1;
    } else if (this.kill == -1) {
      this.rank = -1;
    } else {
      this.rank = 0;
    }

    if(devMode) {
      this.rank = 3;
    }
  }

  public boolean isComplete() {
    return this.rank >= 3;
  }
}