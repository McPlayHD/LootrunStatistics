package net.mcplayhd.lootrunstatistics.utils;

import net.mcplayhd.lootrunstatistics.chests.utils.MinMax;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.ItemStackHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MythicFind {
    private final String mythic;
    private final boolean approximately;
    private final int chestCount;
    private final int dry;
    private final int x, y, z;
    private final Date time;
    private final Map<Tier, Integer> itemsDry;
    private final int emeraldsDry;
    private MythicFindItem mythicFindItem;

    public MythicFind(String mythic, int chestCount, int dry, int x, int y, int z, Date time, Map<Tier, Integer> itemsDry, int emeraldsDry) {
        this.mythic = mythic;
        approximately = false;
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

    public ItemType getType() {
        String mythic = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(this.mythic));
        for (ItemType itemType : ItemType.values())
            if (mythic.contains(itemType.getName()))
                return itemType;
        return null;
    }

    public MinMax getBoxMinMax() {
        String mythic = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(this.mythic));
        String[] boxRangeSplit = mythic.replace("Lv. Range: ", "\n").split("\n")[1].split("-");
        int boxMin = Integer.parseInt(boxRangeSplit[0]) + 1; // apparently the lower bound can't be in boxes
        int boxMax = Integer.parseInt(boxRangeSplit[1]);
        return new MinMax(boxMin, boxMax);
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
        if (mythicFindItem == null) {
            MinMax minMax = getBoxMinMax();
            return "§5" + getType().getName() + " §a- §7Lv. §f" + (minMax.getMin() - 1) + "-" + minMax.getMax();
        } else {
            return mythicFindItem.getDisplayName();
        }
    }

    public boolean isApproximately() {
        return approximately;
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
