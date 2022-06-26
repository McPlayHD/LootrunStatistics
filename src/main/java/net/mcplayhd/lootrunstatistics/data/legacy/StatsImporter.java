package net.mcplayhd.lootrunstatistics.data.legacy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcplayhd.lootrunstatistics.data.ChestCountData;
import net.mcplayhd.lootrunstatistics.data.MythicFindsData;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;
import net.mcplayhd.lootrunstatistics.utils.MythicFind;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getPlayerUUID;

public class StatsImporter {

    public static ChestCountData importChestCountData() {
        int lastMythicChestCount = 0;
        try {
            MythicFindsData data = importMythicFinds();
            lastMythicChestCount = data.getLastMythic().getChestCount();
        } catch (Exception ignored) {
            // chestcountmod had no mythics.
        }
        int dryCount = getDryCountFromWynntils();
        int total = lastMythicChestCount + dryCount;
        if (total > 0) {
            ChestCountData data = new ChestCountData(lastMythicChestCount + dryCount);
            data.save();
            return data;
        } else {
            return new ChestCountData(0);
        }
    }

    private static int getDryCountFromWynntils() {
        try {
            File oldFile = new File(Minecraft.getMinecraft().mcDataDir, "wynntils/configs/" + getPlayerUUID().toString().replace("-", "") + "/utilities-main.config");
            String json = FileHelper.readFile(oldFile);
            WynntilsUtilitiesMain main = new GsonBuilder().setPrettyPrinting().create().fromJson(json, WynntilsUtilitiesMain.class);
            return main.dryStreakCount;
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static MythicFindsData importMythicFinds() {
        try {
            return getMythicFindsFromChestcountmod();
        } catch (Exception ignored) {
            return new MythicFindsData(new ArrayList<>());
        }
    }

    private static MythicFindsData getMythicFindsFromChestcountmod() throws IOException {
        File oldFile = new File(Minecraft.getMinecraft().mcDataDir, "chestcountmod/mythicData.json");
        ChestCountModMythics oldData = new Gson().fromJson("{\"mythicFinds\":" + FileHelper.readFile(oldFile) + "}", ChestCountModMythics.class);
        return new MythicFindsData(oldData.mythicFinds.get(getPlayerUUID()));
    }

    static class ChestCountModMythics {
        protected Map<UUID, List<MythicFind>> mythicFinds;
    }

    static class WynntilsUtilitiesMain {
        int dryStreakCount;
    }
}
