package net.mcplayhd.lootrunstatistics.chests.utils;

import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.utils.Loc;

import java.util.HashMap;
import java.util.Map;

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
            minMax.consider(level);
        }
        this.minMax = minMax;
    }

    public void updateNote() {
        getNoteDrawer().updateNote();
    }

    public void drawNote() {
        getNoteDrawer().drawNote();
    }

}
