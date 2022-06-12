package net.mcplayhd.lootrunstatistics.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;

import java.io.File;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.MODID;
import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getPlayerUUID;

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

    public void setLevelRangeAboveChests(boolean levelRangeAboveChests) {
        this.levelRangeAboveChests = levelRangeAboveChests;
        save();
    }

    public MythicsAboveChests getMythicsAboveChests() {
        return mythicsAboveChests;
    }

    public void setMythicsAboveChests(MythicsAboveChests mythicsAboveChests) {
        this.mythicsAboveChests = mythicsAboveChests;
        save();
    }

    public boolean displayDryCountInChest() {
        return dryCountInChest;
    }

    public void setDryCountInChest(boolean dryCountInChest) {
        this.dryCountInChest = dryCountInChest;
        save();
    }

    public boolean displayTotalChestCountInChest() {
        return totalChestCountInChest;
    }

    public void setTotalChestCountInChest(boolean totalChestCountInChest) {
        this.totalChestCountInChest = totalChestCountInChest;
        save();
    }

    public GroupingSeparator getGroupingSeparator() {
        return groupingSeparator;
    }

    public void setGroupingSeparator(GroupingSeparator groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
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
        NONE("<None>", '\0'),
        SPACE("<Space>", ' '),
        APOSTROPHE("'", '\''),
        COMMA(",", ','),
        ;
        final String description;
        final char separator;

        GroupingSeparator(String description, char separator) {
            this.description = description;
            this.separator = separator;
        }

        public char getSeparator() {
            return separator;
        }
    }

}
