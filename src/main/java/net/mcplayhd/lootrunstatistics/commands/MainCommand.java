package net.mcplayhd.lootrunstatistics.commands;

import com.google.common.collect.ImmutableList;
import net.mcplayhd.lootrunstatistics.gui.CustomGui;
import net.mcplayhd.lootrunstatistics.gui.GuiFactory;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

import javax.annotation.Nonnull;
import java.util.List;

public class MainCommand extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    @Nonnull
    public String getName() {
        return "lootrunstatistics";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return ImmutableList.of("lrs", "ls");
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/lrs";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        // I can't open the gui immediately because the chat close event will also update the currentScreen.
        // `null` because I don't want the chat box to open again when closing the inventory.
        CustomGui.shouldBeDrawn = GuiFactory.getMainGui(null);
    }

}
