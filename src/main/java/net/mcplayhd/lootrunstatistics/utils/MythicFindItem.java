package net.mcplayhd.lootrunstatistics.utils;

import net.mcplayhd.lootrunstatistics.helpers.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class MythicFindItem {
    private final String material;
    private final String displayName;
    private final String nbtTagCompound;

    public MythicFindItem(String material, String displayName, NBTTagCompound nbtTagCompound) throws IOException {
        this.material = material;
        this.displayName = displayName;
        this.nbtTagCompound = ItemStackHelper.serializeNBTTagCompound(nbtTagCompound);
    }

    public ItemStack getItemStack() throws IOException {
        String[] split = material.split(":");
        int id = Integer.parseInt(split[0]);
        int damage = Integer.parseInt(split[1]);
        ItemStack itemStack = ItemStackHelper.generateWynncraftItem(id, damage);
        itemStack.setStackDisplayName(displayName);
        itemStack.setTagCompound(ItemStackHelper.deserializeNBTTagCompound(nbtTagCompound));
        return itemStack;
    }
}
