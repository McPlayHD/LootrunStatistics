package net.mcplayhd.lootrunstatistics.helpers;

import java.io.File;
import java.net.URI;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getLogger;

public class DesktopHelper {

    // taken from net.minecraft.client.gui.GuiMainMenu
    public static void openURL(String url) {
        try {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object object = clazz.getMethod("getDesktop").invoke(null);
            clazz.getMethod("browse", URI.class).invoke(object, new URI(url));
        } catch (Throwable throwable) {
            getLogger().error("Couldn't open url", throwable);
        }
    }

    public static void openFile(File file) {
        try {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object object = clazz.getMethod("getDesktop").invoke(null);
            clazz.getMethod("open", File.class).invoke(object, file);
        } catch (Throwable throwable) {
            getLogger().error("Couldn't open file", throwable);
        }
    }
}
