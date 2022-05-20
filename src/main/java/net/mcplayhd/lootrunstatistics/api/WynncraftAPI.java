package net.mcplayhd.lootrunstatistics.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.mcplayhd.lootrunstatistics.api.exceptions.APIOfflineException;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;
import net.mcplayhd.lootrunstatistics.helpers.WebsiteHelper;
import net.mcplayhd.lootrunstatistics.utils.Item;
import net.mcplayhd.lootrunstatistics.utils.Mythic;

import java.io.File;
import java.util.*;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.*;

public class WynncraftAPI {

    // items
    private static final Map<ItemType, Map<Tier, Set<Item>>> itemDatabase = new HashMap<>();
    private static final Set<Mythic> mythics = new HashSet<>();
    // chest count
    private static ChestCount chestCount;

    public static int getTotalChestCount() {
        return chestCount.totalChests;
    }

    public static void increaseTotalChestCountAndSave() {
        chestCount.totalChests++;
        saveChestCount();
    }

    public static Set<Mythic> getMythics() {
        return mythics;
    }

    public static void loadChestCount() {
        try {
            getLogger().info("Attempting to load player chest count.");
            UUID uuid = getPlayerUUID();
            File file = new File(MODID + "/" + uuid + "/chestCount.json");
            if (file.exists()) {
                chestCount = new Gson().fromJson(FileHelper.readFile(file), ChestCount.class);
                return;
            }
            chestCount = new ChestCount();
            chestCount.totalChests = 0;
            chestCount.lastChestOpened = -1;
            saveChestCount();
            getLogger().info("Created new chestCount file.");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void saveChestCount() {
        try {
            UUID uuid = getPlayerUUID();
            File file = new File(MODID + "/" + uuid + "/chestCount.json");
            String json = new Gson().toJson(chestCount);
            FileHelper.writeFile(file, json);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void loadItems() {
        try {
            getLogger().info("Attempting to load items.");
            File file = new File(MODID + "/itemDB.json");
            String json;
            if (!file.exists()) {
                getLogger().info("Loading items from Wynncraft API.");
                json = WebsiteHelper.getHttps("https://api.wynncraft.com/public_api.php?action=itemDB&category=all");
                if (json == null)
                    throw new APIOfflineException("Wynncraft Legacy");
                FileHelper.writeFile(file, json);
            } else {
                getLogger().info("Loading items from cached file.");
                json = FileHelper.readFile(file);
            }
            JsonArray itemsArray = new JsonParser().parse(json).getAsJsonObject().getAsJsonArray("items");
            for (JsonElement element : itemsArray) {
                String dropType = element.getAsJsonObject().get("dropType").getAsString();
                if (!(dropType.equals("NORMAL") || dropType.equals("lootchest")))
                    continue;
                String name = element.getAsJsonObject().get("name").getAsString();
                Tier tier;
                ItemType type;
                try {
                    tier = Tier.valueOf(element.getAsJsonObject().get("tier").getAsString().toUpperCase());
                    type = ItemType.valueOf(element.getAsJsonObject().get("type").getAsString().toUpperCase());
                } catch (Exception ignored) {
                    continue;
                }
                int level = element.getAsJsonObject().get("level").getAsInt();
                Item item;
                if (tier == Tier.MYTHIC) {
                    Mythic m = new Mythic(name, type, tier, level);
                    mythics.add(m);
                    item = m;
                } else {
                    item = new Item(name, type, tier, level);
                }
                itemDatabase.computeIfAbsent(type, a -> new HashMap<>())
                        .computeIfAbsent(tier, b -> new HashSet<>())
                        .add(item);
            }
            getLogger().info("Loaded items successfully.");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * This function should be called whenever Wynncraft makes changes to items... I don't know how to trigger that yet
     * todo implement trigger for this function to call
     *
     * @return true if the cache file existed and was successfully deleted
     */
    public static boolean clearItemCache() {
        try {
            File file = new File(MODID + "/itemDB.json");
            boolean success = file.exists() && file.delete();
            if (success) {
                getLogger().info("Successfully deleted item cache.");
            } else {
                getLogger().warn("Couldn't delete itemDB.json... Probably it didn't exist yet.");
            }
            return success;
        } catch (Throwable t) {
            return false;
        }
    }

    static class ChestCount {
        int totalChests;
        long lastChestOpened;
    }

}