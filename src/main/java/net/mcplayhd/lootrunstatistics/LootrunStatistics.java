package net.mcplayhd.lootrunstatistics;

import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.chests.Chests;
import net.mcplayhd.lootrunstatistics.commands.DryCommand;
import net.mcplayhd.lootrunstatistics.commands.LastMythicCommand;
import net.mcplayhd.lootrunstatistics.data.ChestCountData;
import net.mcplayhd.lootrunstatistics.data.DryData;
import net.mcplayhd.lootrunstatistics.data.MythicFindsData;
import net.mcplayhd.lootrunstatistics.listeners.ChestOpenListener;
import net.mcplayhd.lootrunstatistics.listeners.RenderListener;
import net.mcplayhd.lootrunstatistics.utils.Mythic;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.UUID;

@Mod(modid = LootrunStatistics.MODID, name = LootrunStatistics.NAME, version = LootrunStatistics.VERSION)
public class LootrunStatistics {
    public static final String MODID = "lootrunstatistics";
    public static final String NAME = "Lootrun Statistics";
    public static final String VERSION = "0.5";

    private static Logger logger;

    private static Boolean wynntilsInstalled;

    private static final ChestCountData chestCountData = ChestCountData.load();
    private static final DryData dryData = DryData.load();
    private static final MythicFindsData mythicFindsData = MythicFindsData.load();
    private static final Chests chests = Chests.load();

    public static ChestCountData getChestCountData() {
        return chestCountData;
    }

    public static DryData getDryData() {
        return dryData;
    }

    public static MythicFindsData getMythicFindsData() {
        return mythicFindsData;
    }

    public static Chests getChests() {
        return chests;
    }

    public static UUID getPlayerUUID() {
        return Minecraft.getMinecraft().getSession().getProfile().getId();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static boolean isWynntilsInstalled() {
        if (wynntilsInstalled != null)
            return wynntilsInstalled;
        getLogger().info("Initiating Wynntils check.");
        String version = Minecraft.getMinecraft().getVersion();
        version = version.split("-")[0];
        wynntilsInstalled = isWynntilsInDirectory(new File("mods"))
                || isWynntilsInDirectory(new File("mods/" + version));
        if (!wynntilsInstalled) {
            getLogger().warn("Wynntils not found. You won't see chest level ranges.");
        }
        return wynntilsInstalled;
    }

    public static boolean isWynntilsInDirectory(File dir) {
        getLogger().info("Looking for mods in " + dir.getAbsolutePath());
        if (!dir.exists()) return false;
        File[] files = dir.listFiles();
        if (files == null)
            return false;
        for (File file : files) {
            getLogger().info("Found `" + file.getName() + "`...");
            if (file.isFile() && file.getName().toLowerCase().contains("wynntils") && file.getName().endsWith(".jar")) {
                getLogger().info("This is Wynntils!");
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        WynncraftAPI.loadItems();
        Mythic.loadMythicSettings();
        chests.updateAllNotes();
    }

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ChestOpenListener());
        MinecraftForge.EVENT_BUS.register(new RenderListener());
        ClientCommandHandler.instance.registerCommand(new LastMythicCommand());
        ClientCommandHandler.instance.registerCommand(new DryCommand());
    }

}
