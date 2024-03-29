package net.mcplayhd.lootrunstatistics.commands;

import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Map;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getChestCountData;
import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getDryData;
import static net.mcplayhd.lootrunstatistics.helpers.FormatterHelper.*;

public class DryCommand extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    @Nonnull
    public String getName() {
        return "dry";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/dry";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        sender.sendMessage(formatString("§aEmeralds §edry§7: §a" + getFormatted(getDryData().getEmeraldsDry())));
        Map<Tier, Integer> tiers = getDryData().getItemsDry();
        int sum = tiers.values().stream().mapToInt(i -> i).sum();
        sender.sendMessage(formatString("§eItems dry§7: §e" + getFormatted(sum)));
        DecimalFormat decimalFormat = new DecimalFormat("#0.0");
        for (Map.Entry<Tier, Integer> tierDry : tiers.entrySet()) {
            Tier tier = tierDry.getKey();
            if (tier == Tier.MYTHIC)
                continue; // will never be seen there
            int dry = tierDry.getValue();
            double percentage = sum == 0 ? 0 : dry / (double) sum * 100;
            sender.sendMessage(formatString("§7  " + tier.getDisplayName() + "§7: §e" + getFormatted(dry) + " §7(§e" + decimalFormat.format(percentage) + "%§7)"));
        }
        int dry = getDryData().getChestsDry();
        int total = getChestCountData().getTotalChests();
        sender.sendMessage(formatString("§eChests dry§7: " + getFormattedDry(dry) + " §etotal§7: §3" + getFormatted(total)));
    }
}
