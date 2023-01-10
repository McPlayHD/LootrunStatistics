package net.mcplayhd.lootrunstatistics.chests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcplayhd.lootrunstatistics.LootrunStatistics;
import net.mcplayhd.lootrunstatistics.chests.utils.ChestInfo;
import net.mcplayhd.lootrunstatistics.chests.utils.MinMax;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.PotionType;
import net.mcplayhd.lootrunstatistics.enums.PowderType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;
import net.mcplayhd.lootrunstatistics.utils.Loc;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.MODID;

public class Chests {
    private static final File file = new File(MODID + "/chests.json");
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .setPrettyPrinting()
            .create();

    protected HashMap<String, ChestInfo> chestInfos = new HashMap<>();

    public static Chests load() {
        return load(file);
    }

    public static Chests load(File file) {
        try {
            return gson.fromJson(FileHelper.readFile(file), Chests.class);
        } catch (Exception ignored) {
            return new Chests();
        }
    }

    public ChestInfo getChestInfo(Loc loc) {
        return chestInfos.computeIfAbsent(loc.toString(), ci -> new ChestInfo(loc));
    }

    public Collection<ChestInfo> getAllChests() {
        return chestInfos.values();
    }

    public void save() {
        try {
            FileHelper.writeFile(file, gson.toJson(this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void backup() {
        File backupFile = new File(file.getPath() + ".backup");
        if (backupFile.exists()) {
            backupFile.delete();
        }
        file.renameTo(backupFile);
    }

    public boolean restoreFromBackup() {
        File backupFile = new File(file.getPath() + ".backup");
        if (!backupFile.exists()) {
            return false;
        }
        Chests backup = load(backupFile);
        chestInfos = backup.chestInfos;
        return true;
    }

    public void reset() {
        chestInfos.clear();
    }

    public void registerOpened(Loc loc) {
        getChestInfo(loc).registerOpened();
    }

    public void setTier(Loc loc, int tier) {
        getChestInfo(loc).setTier(tier);
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

    public void addPowder(Loc loc, PowderType powderType, int tier) {
        getChestInfo(loc).addPowder(powderType, tier);
    }

    public void addIngredient(Loc loc, String name, int tier, int level) {
        getChestInfo(loc).addIngredient(name, tier, level);
    }

    public void addPouch(Loc loc, int tier) {
        getChestInfo(loc).addPouch(tier);
    }

    public void addEmeralds(Loc loc, int emeralds) {
        getChestInfo(loc).addEmeralds(emeralds);
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
