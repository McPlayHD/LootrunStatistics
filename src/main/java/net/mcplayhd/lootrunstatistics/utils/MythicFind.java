package net.mcplayhd.lootrunstatistics.utils;

import net.mcplayhd.lootrunstatistics.enums.Tier;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MythicFind {

    private final String mythic;
    private final int chestCount;
    private final int dry;
    private final int x, y, z;
    private final Date time;
    private final Map<Tier, Integer> itemsDry;
    private final int emeraldsDry;

    public MythicFind(String mythic, int chestCount, int dry, int x, int y, int z, Date time, Map<Tier, Integer> itemsDry, int emeraldsDry) {
        this.mythic = mythic;
        this.chestCount = chestCount;
        this.dry = dry;
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = time;
        this.itemsDry = itemsDry;
        this.emeraldsDry = emeraldsDry;
    }

    public String getMythic() {
        return mythic;
    }

    public int getChestCount() {
        return chestCount;
    }

    public int getDry() {
        return dry;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Date getTime() {
        return time;
    }

    public Map<Tier, Integer> getItemsDry() {
        return itemsDry == null ? new HashMap<>() : itemsDry;
    }

    public int getEmeraldsDry() {
        return emeraldsDry;
    }

}
