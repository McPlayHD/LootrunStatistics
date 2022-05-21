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

    public ChestInfo(Loc loc) {
        this.loc = loc;
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

}
