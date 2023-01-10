package net.mcplayhd.lootrunstatistics.chests.utils;

import net.mcplayhd.lootrunstatistics.enums.Tier;

import java.util.HashMap;
import java.util.Map;

public class LevelMap {
    protected Map<Tier, Map<String, Integer>> levelMap = new HashMap<>();

    public Map<String, Integer> getLevelMap(Tier tier) {
        return levelMap.computeIfAbsent(tier, m -> new HashMap<>());
    }

    public void triggerNormalItemFind(int level) {
        getLevelMap(Tier.NORMAL).merge(level + "", 1, Integer::sum);
    }

    public void triggerBoxFind(Tier tier, MinMax minMax) {
        getLevelMap(tier).merge(minMax.getMin() + "," + minMax.getMax(), 1, Integer::sum);
    }
}
