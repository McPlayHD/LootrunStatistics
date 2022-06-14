package net.mcplayhd.lootrunstatistics.gui.configuration;

import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.gui.CustomGui;
import net.mcplayhd.lootrunstatistics.gui.drawables.DrawableLineButtonRight;
import net.mcplayhd.lootrunstatistics.gui.drawables.DrawableLineTextLeftButtonRight;
import net.mcplayhd.lootrunstatistics.helpers.DesktopHelper;
import net.mcplayhd.lootrunstatistics.helpers.VersionHelper;
import net.mcplayhd.lootrunstatistics.utils.Mythic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.*;

public class ConfigurationGuiMain extends CustomGui {
    public ConfigurationGuiMain(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void onInitGui() {
        int id = -1; // -1 so the first will get 0 because of ++id
        // version checker
        try {
            String newVersion = VersionHelper.getNewVersion();
            if (newVersion != null) {
                String url = "https://github.com/McPlayHD/LootrunStatistics/releases/tag/" + newVersion;
                int newVersionId = ++id;
                addLine(new DrawableLineTextLeftButtonRight(
                        newVersionId,
                        "New version available!",
                        width / 2 + 10,
                        150,
                        lineHeight,
                        () -> "Download " + newVersion,
                        true,
                        () -> {
                            DesktopHelper.openURL(url);
                            DesktopHelper.openFile(new File("mods"));
                        }
                ));
            }
        } catch (Exception ex) {
            getLogger().error("Couldn't check for new version", ex);
        }
        addLine(new DrawableLineTextLeftButtonRight(
                ++id,
                "Display total chest count in chests",
                width / 2 + 10,
                150,
                lineHeight,
                () -> getConfiguration().displayTotalChestCountInChest() ? "Enabled" : "Disabled",
                true,
                () -> getConfiguration().toggleTotalChestCountInChest()
        ));
        addLine(new DrawableLineTextLeftButtonRight(
                ++id,
                "Display dry count in chests",
                width / 2 + 10,
                150,
                lineHeight,
                () -> getConfiguration().displayDryCountInChest() ? "Enabled" : "Disabled",
                true,
                () -> getConfiguration().toggleDryCountInChest()
        ));
        addLine(new DrawableLineTextLeftButtonRight(
                ++id,
                "Toggle number grouping separator",
                width / 2 + 10,
                150,
                lineHeight,
                () -> getConfiguration().getGroupingSeparator().getDescription(),
                true,
                () -> getConfiguration().toggleGroupingSeparator()
        ));
        addLine(new DrawableLineTextLeftButtonRight(
                ++id,
                "Show level range above chests",
                width / 2 + 10,
                150,
                lineHeight,
                () -> {
                    if (!isWynntilsInstalled())
                        return "Requires Wynntils";
                    return getConfiguration().displayLevelRangeAboveChests() ? "Enabled" : "Disabled";
                },
                isWynntilsInstalled(),
                () -> getConfiguration().toggleLevelRangeAboveChests()
        ));
        addLine(new DrawableLineTextLeftButtonRight(
                ++id,
                "Toggle mythic display above chests",
                width / 2 + 10,
                150,
                lineHeight,
                () -> {
                    if (!isWynntilsInstalled())
                        return "Requires Wynntils";
                    return getConfiguration().getMythicsAboveChests().getDescription();
                },
                isWynntilsInstalled(),
                () -> getConfiguration().toggleMythicsAboveChests()
        ));
        addLine(new DrawableLineButtonRight(
                ++id,
                width / 2 + 10,
                150,
                lineHeight,
                () -> "Mythic Configurations",
                () -> Minecraft.getMinecraft().displayGuiScreen(new ConfigurationGuiMythics(this))
        ));
        addLine(new DrawableLineTextLeftButtonRight(
                ++id,
                "Reload Wynncraft item database",
                width / 2 + 10,
                150,
                lineHeight,
                () -> {
                    Date date = WynncraftAPI.getItemDBDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    return "Date: " + dateFormat.format(date);
                },
                true,
                () -> {
                    if (WynncraftAPI.clearItemCache()) {
                        WynncraftAPI.loadItems();
                        Mythic.loadMythicSettings();
                        getChests().updateAllNotes();
                    }
                }
        ));
    }
}
