package net.mcplayhd.lootrunstatistics.listeners;

import net.mcplayhd.lootrunstatistics.LootrunStatistics;
import net.mcplayhd.lootrunstatistics.gui.CustomGui;
import net.mcplayhd.lootrunstatistics.helpers.VersionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.*;

public class RenderListener {

    private int lastPlayerLevel = 0;
    private boolean seenPlayerOverLevel0 = false;

    // idea to only open the gui one tick later from https://github.com/albarv340/chestcountmod
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (CustomGui.shouldBeDrawn != null) {
            Minecraft.getMinecraft().displayGuiScreen(CustomGui.shouldBeDrawn);
            CustomGui.shouldBeDrawn = null;
        }
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        int playerLevel = LootrunStatistics.getPlayerLevel();
        if (!seenPlayerOverLevel0 && playerLevel > 0) {
            seenPlayerOverLevel0 = true;
            try {
                String newVersion = VersionHelper.getNewVersion();
                if (newVersion != null) {
                    String link = "https://github.com/McPlayHD/LootrunStatistics/releases/tag/" + newVersion;
                    ITextComponent text = new TextComponentString("§7\n")
                            .appendText("§eVersion §3" + newVersion + " §eis available for §b" + LootrunStatistics.NAME + "§e.\n")
                            .appendText("§7Click ")
                            .appendSibling(new TextComponentString("§9[§bhere§9]").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lootrunstatistics update"))))
                            .appendText(" §7to open the download page and mod folder automatically or click the following url if you prefer to do it on your own: ")
                            .appendSibling(new TextComponentString("§e§o" + link).setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link))));
                    getPlayer().sendMessage(text);
                }
            } catch (Exception ex) {
                getLogger().error("Couldn't check for new version", ex);
            }
        }
        if (lastPlayerLevel != playerLevel) {
            lastPlayerLevel = playerLevel;
            getChests().updateAllNotes();
        }
        getChests().drawNotes();
    }

}
