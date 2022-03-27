package net.mcplayhd.lootrunstatistics;

import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.utils.Mythic;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Mod(modid = LootrunStatistics.MODID, name = LootrunStatistics.NAME, version = LootrunStatistics.VERSION)
public class LootrunStatistics {
    public static final String MODID = "lootrunstatistics";
    public static final String NAME = "Lootrun Statistics";
    public static final String VERSION = "0.1";

    public static UUID getPlayerUUID() {
        return Minecraft.getMinecraft().getSession().getProfile().getId();
    }

    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        WynncraftAPI.loadChestCount();
        WynncraftAPI.loadItems();
        Mythic.loadMythicSettings();
    }

}
