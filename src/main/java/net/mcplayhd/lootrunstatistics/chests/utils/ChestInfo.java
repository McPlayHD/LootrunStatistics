package net.mcplayhd.lootrunstatistics.chests.utils;

import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.utils.Item;
import net.mcplayhd.lootrunstatistics.utils.Loc;

import java.util.*;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getLogger;

public class ChestInfo {

    protected Loc loc;
    protected int tier;
    protected Map<ItemType, LevelMap> itemInfos = new HashMap<>();
    protected Map<Integer, Integer> levelsSeen = new HashMap<>();

    private transient MinMax minMax;
    private transient NoteDrawer noteDrawer;

    public ChestInfo(Loc loc) {
        this.loc = loc;
    }

    public Loc getLoc() {
        return loc;
    }

    public int getTier() {
        return tier;
    }

    public Map<Integer, Integer> getLevelsSeen() {
        return levelsSeen;
    }

    public MinMax getMinMax() {
        if (minMax == null) {
            updateChestLevel();
        }
        return minMax;
    }

    private NoteDrawer getNoteDrawer() {
        if (noteDrawer == null) {
            noteDrawer = new NoteDrawer(this);
        }
        return noteDrawer;
    }

    public boolean setTier(int tier) {
        if (this.tier == tier)
            return false;
        this.tier = tier;
        return true;
    }

    public void addNormalItem(ItemType type, int lvl) {
        LevelMap map = itemInfos.computeIfAbsent(type, m -> new LevelMap());
        map.triggerNormalItemFind(lvl);
        levelsSeen.merge(lvl, 1, Integer::sum);
    }

    public void addBox(ItemType type, Tier tier, MinMax minMax) {
        LevelMap map = itemInfos.computeIfAbsent(type, m -> new LevelMap());
        map.triggerBoxFind(tier, minMax);
    }

    public void updateChestLevel() {
        MinMax minMax = new MinMax();
        for (int level : levelsSeen.keySet()) {
            minMax.updateMin(level);
            minMax.updateMax(level);
        }
        List<MinMax> allBoxRanges = getAllBoxRanges();
        if (minMax.isEmpty()) {
            // no normal item was found in this chest
            for (MinMax a : allBoxRanges) {
                for (MinMax b : allBoxRanges) {
                    int gap = b.getMin() - a.getMax();
                    // if the two boxes are overlapping by more than just one level the gap will be smaller than 0
                    if (gap < 0)
                        continue;
                    // this range can now be considered as confirmed because the two boxes don't overlap (or only one level)
                    minMax.updateMin(a.getMax());
                    minMax.updateMax(b.getMin());
                }
            }
        } else {
            // at least one normal item was found in this chest
            for (MinMax minMaxInBox : allBoxRanges) {
                // the respecting box min could be the max of the chest and the max of the box the min of the chest.
                minMax.updateMin(minMaxInBox.getMax());
                minMax.updateMax(minMaxInBox.getMin());
            }
        }
        this.minMax = minMax;
    }

    private List<MinMax> getAllBoxRanges() {
        List<MinMax> allPossibleBoxRanges = new ArrayList<>();
        for (Map.Entry<ItemType, LevelMap> itemTypeLevelMapEntry : itemInfos.entrySet()) {
            ItemType itemType = itemTypeLevelMapEntry.getKey();
            LevelMap levelMap = itemTypeLevelMapEntry.getValue();
            for (Map.Entry<Tier, Map<String, Integer>> entry : levelMap.levelMap.entrySet()) {
                Tier tier = entry.getKey();
                if (tier == Tier.NORMAL)
                    continue;
                Set<Item> items = WynncraftAPI.getItems(itemType, tier);
                Collection<String> boxRanges = entry.getValue().keySet();
                for (String boxRange : boxRanges) {
                    MinMax minMaxInBox = getMinMaxInBox(items, boxRange);
                    if (minMaxInBox.isEmpty()) {
                        // should never happen because we actually found this box, but we never know...
                        getLogger().warn("Found level range " + boxRange + " of type " + itemType + " but there are no know items in that range.");
                        continue;
                    }
                    allPossibleBoxRanges.add(minMaxInBox);
                }
            }
        }
        return allPossibleBoxRanges;
    }

    private MinMax getMinMaxInBox(Set<Item> items, String boxRange) {
        String[] boxRangeSplit = boxRange.split(",");
        int boxMin = Integer.parseInt(boxRangeSplit[0]) + 1; // apparently the lower bound can't be in boxes
        int boxMax = Integer.parseInt(boxRangeSplit[1]);

        MinMax minMaxInBox = new MinMax();
        for (Item item : items) {
            if (item.getLevel() > boxMax || item.getLevel() < boxMin) continue;
            minMaxInBox.updateMin(item.getLevel());
            minMaxInBox.updateMax(item.getLevel());
        }
        return minMaxInBox;
    }

    public void updateNote() {
        getNoteDrawer().updateNote();
    }

    public void drawNote() {
        getNoteDrawer().drawNote();
    }

}
