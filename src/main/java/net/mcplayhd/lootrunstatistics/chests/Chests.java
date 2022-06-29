package net.mcplayhd.lootrunstatistics.chests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcplayhd.lootrunstatistics.LootrunStatistics;
import net.mcplayhd.lootrunstatistics.chests.utils.ChestInfo;
import net.mcplayhd.lootrunstatistics.chests.utils.MinMax;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.PotionType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;
import net.mcplayhd.lootrunstatistics.utils.Loc;

import java.io.File;
import java.util.HashMap;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.MODID;

public class Chests {
    private static final File file = new File(MODID + "/chests.json");
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    protected HashMap<String, ChestInfo> chestInfos = new HashMap<>();

    public static Chests load() {
        try {
            return gson.fromJson(FileHelper.readFile(file), Chests.class);
        } catch (Exception ignored) {
            return new Chests();
        }
    }

    public ChestInfo getChestInfo(Loc loc) {
        return chestInfos.computeIfAbsent(loc.toString(), ci -> new ChestInfo(loc));
    }

    public void save() {
        try {
            FileHelper.writeFile(file, gson.toJson(this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean setTier(Loc loc, int tier) {
        return getChestInfo(loc).setTier(tier);
    }

    public void addNormalItem(Loc loc, ItemType type, int lvl) {
        getChestInfo(loc).addNormalItem(type, lvl);
    }

    public void addBox(Loc loc, ItemType type, Tier tier, MinMax minMax) {
        getChestInfo(loc).addBox(type, tier, minMax);
    }

    public void addPotion(Loc loc, PotionType potionType, int level) {
        getChestInfo(loc).addPotion(potionType, level);
    }

    public void updateChestInfo(Loc loc) {
        if (!LootrunStatistics.isWynntilsInstalled()) return;
        ChestInfo chestInfo = getChestInfo(loc);
        chestInfo.updateChestLevel();
        chestInfo.updateNote();
    }

    public void updateAllNotes() {
        if (!LootrunStatistics.isWynntilsInstalled()) return;
        for (ChestInfo chestInfo : chestInfos.values()) {
            chestInfo.updateNote();
        }
    }

    public void drawNotes() {
        if (!LootrunStatistics.isWynntilsInstalled()) return;
        for (ChestInfo chestInfo : chestInfos.values()) {
            chestInfo.drawNote();
        }
    }
}
