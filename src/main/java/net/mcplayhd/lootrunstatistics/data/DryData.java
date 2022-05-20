package net.mcplayhd.lootrunstatistics.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.MODID;
import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getPlayerUUID;

public class DryData {

    private static final File file = new File(MODID + "/" + getPlayerUUID() + "/dry.json");
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    protected Map<Tier, Integer> itemsDry = new HashMap<>();
    protected int emeraldsDry = 0;
    protected int chestsDry = 0;

    public static DryData load() {
        try {
            return gson.fromJson(FileHelper.readFile(file), DryData.class);
        } catch (Exception ignored) {
            return new DryData();
        }
    }

    public void addItemDry(Tier tier) {
        itemsDry.merge(tier, 1, Integer::sum);
    }

    public void addEmeralds(int amount) {
        emeraldsDry += amount;
    }

    public void addChestDry() {
        chestsDry++;
    }

    public Map<Tier, Integer> getItemsDry() {
        Map<Tier, Integer> info = new TreeMap<>();
        for (Tier tier : Tier.values()) {
            info.put(tier, itemsDry.getOrDefault(tier, 0));
        }
        return info;
    }

    public int getEmeraldsDry() {
        return emeraldsDry;
    }

    public int getChestsDry() {
        return chestsDry;
    }

    public void reset() {
        itemsDry.clear();
        emeraldsDry = 0;
        chestsDry = 0;
        save();
    }

    public void save() {
        try {
            FileHelper.writeFile(file, gson.toJson(this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
