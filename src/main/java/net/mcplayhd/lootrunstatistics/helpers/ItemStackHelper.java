package net.mcplayhd.lootrunstatistics.helpers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ItemStackHelper {

    public static ItemStack generateWynncraftItem(int id, int damage) {
        return generateWynncraftItem(Item.getItemById(id), damage);
    }

    public static ItemStack generateWynncraftItem(Item item, int damage) {
        ItemStack itemStack = new ItemStack(item, 1, damage);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("Unbreakable", true);
        itemStack.setTagCompound(tag);
        return itemStack;
    }

    public static String serializeNBTTagCompound(NBTTagCompound nbtTagCompound) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            CompressedStreamTools.writeCompressed(nbtTagCompound, outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
    }

    public static NBTTagCompound deserializeNBTTagCompound(String serialized) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serialized))) {
            return CompressedStreamTools.readCompressed(inputStream);
        }
    }

    /*
    From com.wynntils.core.utils.ItemUtils
     */

    /**
     * Get the lore NBT tag from an item
     */
    public static NBTTagList getLoreTag(ItemStack item) {
        if (item.isEmpty()) return null;
        NBTTagCompound display = item.getSubCompound("display");
        if (display == null || !display.hasKey("Lore")) return null;

        NBTBase loreBase = display.getTag("Lore");
        NBTTagList lore;
        if (loreBase.getId() != 9) return null;

        lore = (NBTTagList) loreBase;
        if (lore.getTagType() != 8) return null;

        return lore;
    }

    /*
    From com.wynntils.core.utils.ItemUtils
     */

    /**
     * Get the lore from an item
     *
     * @return an {@link List} containing all item lore
     */
    public static List<String> getLore(ItemStack item) {
        NBTTagList loreTag = getLoreTag(item);

        List<String> lore = new ArrayList<>();
        if (loreTag == null) return lore;

        for (int i = 0; i < loreTag.tagCount(); ++i) {
            lore.add(loreTag.getStringTagAt(i));
        }

        return lore;
    }
}
