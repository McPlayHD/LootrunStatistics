package net.mcplayhd.lootrunstatistics.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.*;

public class Mythic extends Item {

    private static final File file = new File(MODID + "/" + getPlayerUUID() + "/settings/mythicSettings.json");
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private boolean enabled = true;

    public Mythic(String name, ItemType type, Tier tier, int level) {
        super(name, type, tier, level);
    }

    public static void loadMythicSettings() {
        try {
            if (!file.exists()) return;
            String json = FileHelper.readFile(file);
            Map<String, Boolean> settings = gson.fromJson(json, new TypeToken<Map<String, Boolean>>() {
            }.getType());
            for (Mythic m : WynncraftAPI.getMythics()) {
                m.setEnabled(settings.getOrDefault(m.getType() + "-" + m.getName(), true));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void saveMythicSettings() {
        try {
            Map<String, Boolean> settings = new HashMap<>();
            for (Mythic m : WynncraftAPI.getMythics()) {
                settings.put(m.getType() + "-" + m.getName(), m.isEnabled());
            }
            String json = gson.toJson(settings);
            FileHelper.writeFile(file, json);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        getChests().updateAllChestInfos();
    }

}
