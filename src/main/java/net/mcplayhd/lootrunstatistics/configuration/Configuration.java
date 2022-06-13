package net.mcplayhd.lootrunstatistics.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;

import java.io.File;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.*;

public class Configuration {

    private static final File file = new File(MODID + "/" + getPlayerUUID() + "/configs/configuration.json");
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private boolean levelRangeAboveChests = true;
    private MythicsAboveChests mythicsAboveChests = MythicsAboveChests.MYTHICS_ALL;
    private boolean dryCountInChest = true;
    private boolean totalChestCountInChest = true;
    private GroupingSeparator groupingSeparator = GroupingSeparator.APOSTROPHE;

    public static Configuration load() {
        try {
            return gson.fromJson(FileHelper.readFile(file), Configuration.class);
        } catch (Exception ignored) {
            return new Configuration();
        }
    }

    public boolean displayLevelRangeAboveChests() {
        return levelRangeAboveChests;
    }

    public void toggleLevelRangeAboveChests() {
        this.levelRangeAboveChests = !this.levelRangeAboveChests;
        save();
        getChests().updateAllNotes();
    }

    public MythicsAboveChests getMythicsAboveChests() {
        return mythicsAboveChests;
    }

    public void toggleMythicsAboveChests() {
        int nextId = (this.mythicsAboveChests.ordinal() + 1) % MythicsAboveChests.values().length;
        this.mythicsAboveChests = MythicsAboveChests.values()[nextId];
        save();
        getChests().updateAllNotes();
    }

    public boolean displayDryCountInChest() {
        return dryCountInChest;
    }

    public void toggleDryCountInChest() {
        this.dryCountInChest = !this.dryCountInChest;
        save();
    }

    public boolean displayTotalChestCountInChest() {
        return totalChestCountInChest;
    }

    public void toggleTotalChestCountInChest() {
        this.totalChestCountInChest = !this.totalChestCountInChest;
        save();
    }

    public GroupingSeparator getGroupingSeparator() {
        return groupingSeparator;
    }

    public void toggleGroupingSeparator() {
        int nextId = (this.groupingSeparator.ordinal() + 1) % GroupingSeparator.values().length;
        this.groupingSeparator = GroupingSeparator.values()[nextId];
        save();
    }

    public void save() {
        try {
            FileHelper.writeFile(file, gson.toJson(this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public enum MythicsAboveChests {
        NONE("<None>"),
        MYTHICS_ALL("All Possible Mythics"),
        MYTHICS_FOUND("Mythics found in chest"),
        ;
        final String description;

        MythicsAboveChests(String description) {
            this.description = description;
        }
    }

    public enum GroupingSeparator {
        NONE("<None>", null),
        SPACE("<Space>", ' '),
        APOSTROPHE("'", '\''),
        COMMA(",", ','),
        ;
        final String description;
        final Character separator;

        GroupingSeparator(String description, Character separator) {
            this.description = description;
            this.separator = separator;
        }

        public String getDescription() {
            return description;
        }

        public Character getSeparator() {
            return separator;
        }
    }

}
