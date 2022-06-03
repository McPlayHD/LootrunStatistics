package net.mcplayhd.lootrunstatistics.listeners;

import net.mcplayhd.lootrunstatistics.LootrunStatistics;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderListener {

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent e) {
        LootrunStatistics.getChests().drawNotes();
    }

}
