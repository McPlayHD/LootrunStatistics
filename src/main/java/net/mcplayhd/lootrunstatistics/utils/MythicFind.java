package net.mcplayhd.lootrunstatistics.utils;

import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.chests.utils.MinMax;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.ItemStackHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.*;

public class MythicFind {
    private final String mythic;
    private final boolean approximately;
    private final int chestCount;
    private final int dry;
    private final int x, y, z;
    private final Date time;
    private final Map<Tier, Integer> itemsDry;
    private final int emeraldsDry;
    private String mythicFindMythic;
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

    public void toggleMythicFindMythic() {
        Mythic current = getMythic();
        List<Mythic> possible = getPossibleMythics();
        int currentIndex = current == null ? -1 : possible.indexOf(current);
        currentIndex++;
        if (currentIndex == possible.size()) {
            mythicFindMythic = null;
        } else {
            mythicFindMythic = possible.get(currentIndex).getName();
        }
    }

    public List<Mythic> getPossibleMythics() {
        List<Mythic> possible = new ArrayList<>();
        MinMax boxMinMax = getBoxMinMax();
        for (Item item : WynncraftAPI.getItems(getType(), Tier.MYTHIC)) {
            if (boxMinMax.isInRange(item.getLevel())) {
                possible.add((Mythic) item);
            }
        }
        Collections.sort(possible);
        return possible;
    }

    public Mythic getMythic() {
        if (mythicFindItem != null)
            return Mythic.getMythicByName(mythicFindItem.getDisplayName());
        if (mythicFindMythic != null)
            return Mythic.getMythicByName(mythicFindMythic);
        return null;
    }

    public String getBoxName() {
        return mythic;
    }

    public MythicFindItem getMythicFindItem() {
        return mythicFindItem;
    }

    public void setMythicFindItem(ItemStack itemStack) {
        if (itemStack == null) {
            mythicFindItem = null;
            return;
        }
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
            return ItemStackHelper.generateWynncraftItem(Items.STONE_SHOVEL, 6);
        } else {
            try {
                return mythicFindItem.getItemStack();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getMythicFindTitle() {
        Mythic mythic = getMythic();
        if (mythic != null)
            return "§5" + mythic.getName();
        return getBoxTitle();
    }

    public String getBoxTitle() {
        MinMax minMax = getBoxMinMax();
        return "§5" + getType().getName() + " §a- §7Lv. §f" + (minMax.getMin() - 1) + "-" + minMax.getMax();
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
