package net.mcplayhd.lootrunstatistics.gui.guis.configuration;

import net.mcplayhd.lootrunstatistics.LootrunStatistics;
import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.gui.CustomGui;
import net.mcplayhd.lootrunstatistics.gui.drawables.DrawableLineItemTextTextAreaButton;
import net.mcplayhd.lootrunstatistics.gui.drawables.DrawableLineTextCenterSubtitle;
import net.mcplayhd.lootrunstatistics.utils.Mythic;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationGuiMythics extends CustomGui {

    public ConfigurationGuiMythics(GuiScreen parentScreen) {
        super(parentScreen, 60, LootrunStatistics.NAME + " v" + LootrunStatistics.VERSION, "by McPlayHD");
    }

    @Override
    public void onInitGui() {
        Map<ItemType, ArrayList<Mythic>> mythicsByType = new HashMap<>();
        for (Mythic mythic : WynncraftAPI.getMythics()) {
            mythicsByType.computeIfAbsent(mythic.getType(), a -> new ArrayList<>()).add(mythic);
        }

        int id = -1; // -1 so the first will get 0 because of ++id
        for (ItemType type : ItemType.values()) {
            ArrayList<Mythic> mythics = mythicsByType.get(type);
            if (mythics == null) continue;
            addLine(new DrawableLineTextCenterSubtitle(++id, type.getName()));
            Collections.sort(mythics);
            for (Mythic mythic : mythics) {
                addLine(new DrawableLineItemTextTextAreaButton(
                        ++id,
                        mythic.getItemStack(),
                        mythic.getName(),
                        mythic.getDisplayName(),
                        mythic::setDisplayName,
                        width / 2 + 100 / 2 + 6, // center size 100, spacing column 6
                        120,
                        lineHeight,
                        () -> mythic.isEnabled() ? "Enabled" : "Ignored",
                        mythic::toggleEnabled
                ));
            }
        }
    }
}
