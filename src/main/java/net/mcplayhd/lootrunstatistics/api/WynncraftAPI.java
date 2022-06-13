package net.mcplayhd.lootrunstatistics.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mcplayhd.lootrunstatistics.api.exceptions.APIOfflineException;
import net.mcplayhd.lootrunstatistics.enums.ArmorType;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;
import net.mcplayhd.lootrunstatistics.helpers.WebsiteHelper;
import net.mcplayhd.lootrunstatistics.utils.Item;
import net.mcplayhd.lootrunstatistics.utils.Mythic;

import java.io.File;
import java.util.*;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.MODID;
import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getLogger;

public class WynncraftAPI {
    // items
    private static final File itemDBFile = new File(MODID + "/itemDB.json");
    private static Date itemDBDate;
    private static final Map<ItemType, Map<Tier, Set<Item>>> itemDatabase = new HashMap<>();
    private static final Set<Mythic> mythics = new HashSet<>();

    public static Date getItemDBDate() {
        return itemDBDate;
    }

    public static Set<Mythic> getMythics() {
        return mythics;
    }

    public static Set<Item> getItems(ItemType type, Tier tier) {
        return itemDatabase.getOrDefault(type, new HashMap<>()).getOrDefault(tier, new HashSet<>());
    }

    private static String getItemDBJson() throws APIOfflineException {
        String json = WebsiteHelper.getHttps("https://api.wynncraft.com/public_api.php?action=itemDB&category=all");
        if (json == null)
            throw new APIOfflineException("Wynncraft Legacy");
        return json;
    }

    private static boolean checkItemDBVersion(float version) throws APIOfflineException {
        String json = WebsiteHelper.getHttps("https://api.wynncraft.com/public_api.php?action=itemDB&category=null");
        if (json == null)
            throw new APIOfflineException("Wynncraft Legacy");
        float onlineVersion = new JsonParser().parse(json).getAsJsonObject().getAsJsonObject("request").get("version").getAsFloat();
        return onlineVersion == version;
    }

    public static void loadItems() {
        try {
            itemDatabase.clear();
            mythics.clear();
            getLogger().info("Attempting to load items.");
            JsonObject jsonObject = null;
            boolean isUpToDate = false;
            if (itemDBFile.exists()) {
                getLogger().info("Loading items from cached file.");
                jsonObject = new JsonParser().parse(FileHelper.readFile(itemDBFile)).getAsJsonObject();
                float version = jsonObject.getAsJsonObject("request").get("version").getAsFloat();
                getLogger().info("Checking version of itemDB...");
                isUpToDate = checkItemDBVersion(version);
                getLogger().info("Needs update: " + !isUpToDate);
            }
            if (!isUpToDate) {
                // loading the database or reloading it if a new one exists.
                getLogger().info("Loading items from Wynncraft API.");
                String json = getItemDBJson();
                jsonObject = new JsonParser().parse(json).getAsJsonObject();
                FileHelper.writeFile(itemDBFile, json);
            }
            itemDBDate = new Date(itemDBFile.lastModified());
            JsonArray itemsArray = jsonObject.getAsJsonArray("items");
            for (JsonElement element : itemsArray) {
                JsonObject itemObject = element.getAsJsonObject();
                String dropType = itemObject.get("dropType").getAsString();
                if (!(dropType.equals("NORMAL") || dropType.equals("lootchest")))
                    continue;
                if (!(itemObject.has("type") || itemObject.has("accessoryType")))
                    continue;
                String name;
                if (itemObject.has("displayName")) {
                    name = itemObject.get("displayName").getAsString();
                } else {
                    name = itemObject.get("name").getAsString();
                }
                Tier tier;
                ItemType type;
                try {
                    tier = Tier.valueOf(itemObject.get("tier").getAsString().toUpperCase());
                    if (itemObject.has("type")) {
                        type = ItemType.valueOf(itemObject.get("type").getAsString().toUpperCase());
                    } else {
                        type = ItemType.valueOf(itemObject.get("accessoryType").getAsString().toUpperCase());
                    }
                } catch (Exception ignored) {
                    continue;
                }
                int level = itemObject.get("level").getAsInt();
                String material = null;
                if (itemObject.has("material") && !itemObject.get("material").isJsonNull()) {
                    material = itemObject.get("material").getAsString();
                }
                ArmorType armorType = null;
                try {
                    armorType = ArmorType.valueOf(itemObject.get("armorType").getAsString().toUpperCase());
                } catch (Exception ignored) {
                }
                Item item;
                if (tier == Tier.MYTHIC) {
                    Mythic m = new Mythic(name, type, tier, level, material, armorType);
                    mythics.add(m);
                    item = m;
                } else {
                    item = new Item(name, type, tier, level, material, armorType);
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
     *
     * @return true if the cache file existed and was successfully deleted
     */
    public static boolean clearItemCache() {
        try {
            boolean success = itemDBFile.exists() && itemDBFile.delete();
            if (success) {
                getLogger().info("Successfully deleted item cache.");
            } else {
                getLogger().warn("Couldn't delete '" + itemDBFile.getAbsolutePath() + "'... Probably it didn't exist yet.");
            }
            return success;
        } catch (Throwable t) {
            return false;
        }
    }

}
