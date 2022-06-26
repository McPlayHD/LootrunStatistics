package net.mcplayhd.lootrunstatistics.helpers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackHelper {

    public static ItemStack generateWynncraftItem(int id, int damage) {
        ItemStack itemStack = new ItemStack(Item.getItemById(id), 1, damage);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("Unbreakable", true);
        itemStack.setTagCompound(tag);
        return itemStack;
    }
}
