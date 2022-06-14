package net.mcplayhd.lootrunstatistics.listeners;

import net.mcplayhd.lootrunstatistics.gui.CustomGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getChests;

public class RenderListener {

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
        getChests().drawNotes();
    }

}
