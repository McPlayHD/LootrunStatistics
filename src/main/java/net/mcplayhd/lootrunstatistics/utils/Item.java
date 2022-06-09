package net.mcplayhd.lootrunstatistics.utils;

import net.mcplayhd.lootrunstatistics.chests.utils.ChestInfo;
import net.mcplayhd.lootrunstatistics.chests.utils.MinMax;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;

import java.util.Objects;

public class Item {

    private final String name;
    private final ItemType type;
    private final Tier tier;
    private final int level;

    public Item(String name, ItemType type, Tier tier, int level) {
        this.name = name;
        this.type = type;
        this.tier = tier;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public Tier getTier() {
        return tier;
    }

    public int getLevel() {
        return level;
    }

    public boolean canBeInChest(ChestInfo chestInfo) {
        MinMax minMax = chestInfo.getMinMax();
        if (!minMax.isInRange(level))
            return false;
        if (chestInfo.getTier() < 3) {
            // low tier chests can't have Discoverer
            if (tier == Tier.MYTHIC && name.equals("Discoverer"))
                return false;
            // low tier chests can't have Accessories
            return !type.isAccessory();
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return level == item.level && Objects.equals(name, item.name) && type == item.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, level);
    }

}
