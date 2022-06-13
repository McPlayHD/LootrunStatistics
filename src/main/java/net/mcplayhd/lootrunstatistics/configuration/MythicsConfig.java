package net.mcplayhd.lootrunstatistics.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;
import net.mcplayhd.lootrunstatistics.utils.Mythic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.MODID;
import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getPlayerUUID;

public class MythicsConfig {

    private static final File file = new File(MODID + "/" + getPlayerUUID() + "/configs/mythicsConfig.json");
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    protected Map<String, MythicsConfigEntry> mythics = new HashMap<>(); // [hash, settings]

    public void load(Mythic mythic) {
        MythicsConfigEntry entry = mythics.get(mythic.getIdentifier());
        if (entry == null) return;
        mythic.setEnabled(entry.enabled);
        mythic.setDisplayName(entry.displayName);
    }

    public void save(Mythic mythic) {
        mythics.put(mythic.getIdentifier(), new MythicsConfigEntry(mythic));
        save();
    }

    public static MythicsConfig load() {
        try {
            return gson.fromJson(FileHelper.readFile(file), MythicsConfig.class);
        } catch (Exception ignored) {
            return new MythicsConfig();
        }
    }

    public void save() {
        try {
            FileHelper.writeFile(file, gson.toJson(this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static class MythicsConfigEntry {
        boolean enabled;
        String displayName;

        public MythicsConfigEntry(Mythic mythic) {
            this.enabled = mythic.isEnabled();
            this.displayName = mythic.getDisplayName();
        }
    }

}
