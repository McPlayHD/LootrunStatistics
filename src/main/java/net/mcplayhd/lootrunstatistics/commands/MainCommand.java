package net.mcplayhd.lootrunstatistics.commands;

import com.google.common.collect.ImmutableList;
import net.mcplayhd.lootrunstatistics.chests.utils.ChestInfo;
import net.mcplayhd.lootrunstatistics.chests.utils.LevelMap;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.gui.CustomGui;
import net.mcplayhd.lootrunstatistics.gui.GuiFactory;
import net.mcplayhd.lootrunstatistics.gui.guis.configuration.ConfigurationGuiMain;
import net.mcplayhd.lootrunstatistics.gui.guis.history.HistoryGuiMythics;
import net.mcplayhd.lootrunstatistics.helpers.DesktopHelper;
import net.mcplayhd.lootrunstatistics.helpers.VersionHelper;
import net.mcplayhd.lootrunstatistics.listeners.ChestOpenListener;
import net.mcplayhd.lootrunstatistics.utils.Region;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

import javax.annotation.Nonnull;
import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getChests;
import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getLogger;
import static net.mcplayhd.lootrunstatistics.helpers.FormatterHelper.formatString;
import static net.mcplayhd.lootrunstatistics.helpers.FormatterHelper.getFormatted;

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
        // TODO: 26/06/2022 reload (all .json files)
        if (args.length > 0 && args[0].equalsIgnoreCase("update")) {
            try {
                String version = VersionHelper.getOnlineVersion();
                String url = "https://github.com/McPlayHD/LootrunStatistics/releases/tag/" + version;
                DesktopHelper.openURL(url);
                DesktopHelper.openFile(new File("mods"));
            } catch (Exception ex) {
                getLogger().error("Couldn't fetch current version");
            }
            return;
        }
        if (args.length > 0 && (args[0].equalsIgnoreCase("settings") || args[0].equalsIgnoreCase("config"))) {
            CustomGui.shouldBeDrawn = new ConfigurationGuiMain(null);
            return;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("history")) {
            CustomGui.shouldBeDrawn = new HistoryGuiMythics(null);
            return;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("rarities")) {
            int total = 0;
            Map<Tier, Integer> allItems = new HashMap<>();
            for (ChestInfo chestInfo : getChests().getAllChests()) {
                for (LevelMap levelMap : chestInfo.getItemInfos().values()) {
                    for (Tier tier : Tier.values()) {
                        for (Integer amount : levelMap.getLevelMap(tier).values()) {
                            allItems.merge(tier, amount, Integer::sum);
                            total += amount;
                        }
                    }
                }
            }
            sender.sendMessage(formatString("§eTotal items found§7: §a" + getFormatted(total)));
            sender.sendMessage(formatString("§3Distribution§7:"));
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
            for (Tier tier : Tier.values()) {
                int amount = allItems.getOrDefault(tier, 0);
                double percentage = total == 0 ? 0 : amount / (double) total * 100;
                sender.sendMessage(formatString("§7  " + tier.getDisplayName() + "§7: §e" + getFormatted(amount) + " §7(§e" + decimalFormat.format(percentage) + "%§7)"));
            }
            return;
        }
        if (args.length > 1 && args[0].equalsIgnoreCase("region")) {
            Region region = Region.fromString(args[1]);
            ChestOpenListener.selectedRegion = region;
            if (region == null) {
                if (args[1].equalsIgnoreCase("reset")) {
                    sender.sendMessage(formatString("§eNew region§7: §c" + null));
                } else {
                    sender.sendMessage(formatString("§cUnknown region§7: §e" + args[1]));
                }
            } else {
                sender.sendMessage(formatString("§eNew region§7: §a" + region));
            }
            return;
        }
        // I can't open the gui immediately because the chat close event will also update the currentScreen.
        // `null` because I don't want the chat box to open again when closing the inventory.
        CustomGui.shouldBeDrawn = GuiFactory.getMainGui(null);
    }
}
