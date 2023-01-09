package net.mcplayhd.lootrunstatistics.chests.utils;

import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.PotionType;
import net.mcplayhd.lootrunstatistics.enums.PowderType;
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
    protected Map<PotionType, Map<Integer, Integer>> potions = new HashMap<>();
    protected Map<PowderType, Map<Integer, Integer>> powders = new HashMap<>();
    protected Map<String, Integer> ingredients = new HashMap<>(); // formatted as 'name:tier:level'
    protected Map<Integer, Integer> pouches = new HashMap<>();
    protected Date lastOpened;
    protected long openCount;
    protected long emeralds;

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

    public Map<ItemType, LevelMap> getItemInfos() {
        if (itemInfos == null) {
            itemInfos = new HashMap<>();
        }
        return itemInfos;
    }

    public Map<Integer, Integer> getLevelsSeen() {
        if (levelsSeen == null) {
            levelsSeen = new HashMap<>();
        }
        return levelsSeen;
    }

    public Map<PotionType, Map<Integer, Integer>> getPotions() {
        if (potions == null) {
            potions = new HashMap<>();
        }
        return potions;
    }

    public Map<PowderType, Map<Integer, Integer>> getPowders() {
        if (powders == null) {
            powders = new HashMap<>();
        }
        return powders;
    }

    public Map<String, Integer> getIngredients() {
        if (ingredients == null) {
            ingredients = new HashMap<>();
        }
        return ingredients;
    }

    public Map<Integer, Integer> getPouches() {
        if (pouches == null) {
            pouches = new HashMap<>();
        }
        return pouches;
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

    public void registerOpened() {
        lastOpened = new Date();
        openCount++;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public void addNormalItem(ItemType type, int lvl) {
        LevelMap map = getItemInfos().computeIfAbsent(type, m -> new LevelMap());
        map.triggerNormalItemFind(lvl);
        getLevelsSeen().merge(lvl, 1, Integer::sum);
    }

    public void addBox(ItemType type, Tier tier, MinMax minMax) {
        LevelMap map = getItemInfos().computeIfAbsent(type, m -> new LevelMap());
        map.triggerBoxFind(tier, minMax);
    }

    public void addPotion(PotionType potionType, int level) {
        Map<Integer, Integer> levelMap = getPotions().computeIfAbsent(potionType, m -> new HashMap<>());
        levelMap.merge(level, 1, Integer::sum);
    }

    public void addPowder(PowderType powderType, int tier) {
        Map<Integer, Integer> levelMap = getPowders().computeIfAbsent(powderType, m -> new HashMap<>());
        levelMap.merge(tier, 1, Integer::sum);
    }

    public void addIngredient(String name, int tier, int level) {
        String key = name + ":" + tier + ":" + level;
        getIngredients().merge(key, 1, Integer::sum);
    }

    public void addPouch(int tier) {
        getPouches().merge(tier, 1, Integer::sum);
    }

    public void addEmeralds(int emeralds) {
        this.emeralds += emeralds;
    }

    // TODO: 29/06/2022 check if we can somehow also use potions for level decision making
    public void updateChestLevel() {
        MinMax minMax = new MinMax();
        for (int level : getLevelsSeen().keySet()) {
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
        for (Map.Entry<ItemType, LevelMap> itemTypeLevelMapEntry : getItemInfos().entrySet()) {
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
