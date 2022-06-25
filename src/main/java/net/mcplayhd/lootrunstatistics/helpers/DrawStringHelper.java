package net.mcplayhd.lootrunstatistics.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;

public class DrawStringHelper {

    public static void drawStringLeft(String text, int x, int y, Color color) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString(text, x, y, color.getRGB());
    }

    public static void drawCenteredString(String text, int x, int y, Color color) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color.getRGB());
    }

    public static void drawStringRight(String text, int x, int y, Color color) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text), y, color.getRGB());
    }
}
