package net.mcplayhd.lootrunstatistics.utils;

import net.mcplayhd.lootrunstatistics.chests.utils.ChestInfo;
import net.mcplayhd.lootrunstatistics.chests.utils.MinMax;
import net.mcplayhd.lootrunstatistics.enums.ArmorType;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.ItemStackHelper;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class Item {

    private final String name;
    private final ItemType type;
    private final Tier tier;
    private final int level;

    private final String material; // formatted as <id>:<damage>
    private final ArmorType armorType;

    public Item(String name, ItemType type, Tier tier, int level, String material, ArmorType armorType) {
        this.name = name;
        this.type = type;
        this.tier = tier;
        this.level = level;
        this.material = material;
        this.armorType = armorType;
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

    public String getIdentifier() {
        return name + "_" + type + "_" + level;
    }

    public ItemStack getItemStack() {
        if (material != null) {
            String[] split = material.split(":");
            int id = Integer.parseInt(split[0]);
            int damage = Integer.parseInt(split[1]);
            return ItemStackHelper.generateWynncraftItem(id, damage);
        }
        if (armorType != null)
            return new ItemStack(armorType.getItem(type));
        return null;
    }

    public boolean canBeInChest(ChestInfo chestInfo, MinMax minMax) {
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
