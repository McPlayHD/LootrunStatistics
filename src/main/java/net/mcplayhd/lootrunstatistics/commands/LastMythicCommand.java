package net.mcplayhd.lootrunstatistics.commands;

import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.utils.Mythic;
import net.mcplayhd.lootrunstatistics.utils.MythicFind;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getMythicFindsData;
import static net.mcplayhd.lootrunstatistics.helpers.FormatterHelper.*;

public class LastMythicCommand extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    @Nonnull
    public String getName() {
        return "lastmythic";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return Collections.singletonList("lm");
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/lastmythic";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        MythicFind lastMythic = getMythicFindsData().getLastMythic();
        if (lastMythic == null) {
            sender.sendMessage(formatString("§cNo §5Mythics §cfound."));
            return;
        }
        Mythic mythic = lastMythic.getMythic();
        String name;
        if (mythic == null) {
            name = lastMythic.getBoxName();
        } else {
            name = mythic.getDisplayName();
        }
        sender.sendMessage(formatString("§eLast §5Mythic§7: §5" + name));
        sender.sendMessage(formatString("§eIn chest §8#§3" + getFormatted(lastMythic.getChestCount()) + " §7(" + getFormattedDry(lastMythic.getDry()) + " §edry§7)"));
        Map<Tier, Integer> tiers = lastMythic.getItemsDry();
        int sum = tiers.values().stream().mapToInt(i -> i).sum();
        sender.sendMessage(formatString("§eAfter items§7: §e" + getFormatted(sum)));
        for (Map.Entry<Tier, Integer> tierDry : tiers.entrySet()) {
            Tier tier = tierDry.getKey();
            if (tier == Tier.MYTHIC)
                continue; // will never be seen there
            int dry = tierDry.getValue();
            sender.sendMessage(formatString("§7  " + tier.getDisplayName() + "§7: §e" + getFormatted(dry)));
        }
    }
}
