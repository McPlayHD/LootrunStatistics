package net.mcplayhd.lootrunstatistics.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcplayhd.lootrunstatistics.data.legacy.StatsImporter;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;

import java.io.File;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.MODID;
import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getPlayerUUID;

public class ChestCountData {
    private static final File file = new File(MODID + "/" + getPlayerUUID() + "/chestCount.json");
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    protected int totalChests;

    public ChestCountData(int totalChests) {
        this.totalChests = totalChests;
    }

    public static ChestCountData load() {
        try {
            return gson.fromJson(FileHelper.readFile(file), ChestCountData.class);
        } catch (Exception ignored) {
            // attempting to import from stats stored in chestcountmod and wynntils...
            return StatsImporter.importChestCountData();
        }
    }

    public void addChest() {
        totalChests++;
        save();
    }

    public int getTotalChests() {
        return totalChests;
    }

    public void save() {
        try {
            FileHelper.writeFile(file, gson.toJson(this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
