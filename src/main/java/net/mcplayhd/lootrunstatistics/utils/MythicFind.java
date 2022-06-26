package net.mcplayhd.lootrunstatistics.utils;

import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.ItemStackHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.io.IOException;
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
    private MythicFindItem mythicFindItem;

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

    public void setMythicFindItem(ItemStack itemStack) {
        int id = net.minecraft.item.Item.getIdFromItem(itemStack.getItem());
        int damage = itemStack.getItemDamage();
        try {
            mythicFindItem = new MythicFindItem(id + ":" + damage, itemStack.getDisplayName(), itemStack.getTagCompound());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemStack getItem() {
        if (mythicFindItem == null) { // return a box
            // TODO: 25/06/2022 add lore
            return ItemStackHelper.generateWynncraftItem(Items.STONE_SHOVEL, 6);
        } else {
            try {
                return mythicFindItem.getItemStack();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
