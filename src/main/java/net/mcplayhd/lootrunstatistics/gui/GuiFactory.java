package net.mcplayhd.lootrunstatistics.gui;

import net.mcplayhd.lootrunstatistics.gui.configuration.ConfigurationGuiMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class GuiFactory implements IModGuiFactory {

    // TODO: 14/06/2022 link actual main menu once it exists
    public static CustomGui getMainGui(GuiScreen parentScreen) {
        return new ConfigurationGuiMain(parentScreen);
    }

    @Override
    public void initialize(Minecraft minecraftInstance) {
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new ConfigurationGuiMain(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

}
