package net.mcplayhd.lootrunstatistics.utils;

import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.enums.ArmorType;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getChests;
import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getMythicsConfig;

public class Mythic extends Item {

    private boolean enabled = true;
    private String displayName;

    public Mythic(String name, ItemType type, Tier tier, int level, String material, ArmorType armorType) {
        super(name, type, tier, level, material, armorType);
        displayName = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static void loadMythicSettings() {
        for (Mythic mythic : WynncraftAPI.getMythics()) {
            getMythicsConfig().load(mythic);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        getMythicsConfig().save(this);
        getChests().updateAllNotes();
    }

    public void toggleEnabled() {
        setEnabled(!isEnabled());
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        getMythicsConfig().save(this);
        getChests().updateAllNotes();
    }

}
