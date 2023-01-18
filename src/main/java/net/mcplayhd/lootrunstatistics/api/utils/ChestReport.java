package net.mcplayhd.lootrunstatistics.api.utils;

import net.mcplayhd.lootrunstatistics.utils.Loc;
import net.mcplayhd.lootrunstatistics.utils.Region;

public class ChestReport {
    protected Loc position;
    protected Region region;
    protected int tier = -1;
    protected int minLevel = -1;
    protected int maxLevel = -1;

    public ChestReport(Loc position, Region region) {
        this.position = position;
        this.region = region;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public void setMinLevel(int minLevel) {
        if (minLevel == Integer.MAX_VALUE) {
            return;
        }
        this.minLevel = minLevel;
    }

    public void setMaxLevel(int maxLevel) {
        if (maxLevel == Integer.MIN_VALUE) {
            return;
        }
        this.maxLevel = maxLevel;
    }
}
