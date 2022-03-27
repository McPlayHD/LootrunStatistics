package net.mcplayhd.lootrunstatistics.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.chests.ChestInfoManager;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.MODID;
import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getPlayerUUID;

public class Mythic extends Item {

    private boolean enabled = true;

    public Mythic(String name, ItemType type, Tier tier, int level) {
        super(name, type, tier, level);
    }

    public static void loadMythicSettings() {
        try {
            File file = new File(MODID + "/" + getPlayerUUID() + "/settings/mythic_settings.json");
            if (!file.exists()) return;
            String json = FileHelper.readFile(file);
            Map<String, Boolean> settings = new Gson().fromJson(json, new TypeToken<Map<String, Boolean>>() {
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
            File file = new File(MODID + "/" + getPlayerUUID() + "/settings/mythic_settings.json");
            String json = new Gson().toJson(settings);
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
        ChestInfoManager.updateAllChestInfos();
    }

}
