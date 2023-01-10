package net.mcplayhd.lootrunstatistics.gui.guis.history;

import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.gui.CustomGui;
import net.mcplayhd.lootrunstatistics.gui.drawables.LineMythicFindHistoryColumnNames;
import net.mcplayhd.lootrunstatistics.gui.drawables.LineMythicFindHistoryEntry;
import net.mcplayhd.lootrunstatistics.gui.utils.ButtonText;
import net.mcplayhd.lootrunstatistics.helpers.FormatterHelper;
import net.mcplayhd.lootrunstatistics.helpers.ItemStackHelper;
import net.mcplayhd.lootrunstatistics.utils.Mythic;
import net.mcplayhd.lootrunstatistics.utils.MythicFind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getMythicFindsData;
import static net.mcplayhd.lootrunstatistics.helpers.FormatterHelper.getFormatted;

public class HistoryGuiMythics extends CustomGui {

    public HistoryGuiMythics(GuiScreen parentScreen) {
        super(parentScreen, 40, "Mythic history", null);
    }

    @Override
    public void onInitGui() {
        int id = -1; // -1 so the first will get 0 because of ++id
        int mythicNumber = 1;
        int totalWidth = 420; // everyone that players with a smaller width is weird.
        int leftWidth = 162;
        int centerWidth = 158;
        int rightWidth = 100;
        MythicFind mythicBefore = null;
        addLine(new LineMythicFindHistoryColumnNames(++id,
                "Mythic",
                "Chest count",
                "Chests dry"
        ));
        for (MythicFind mythicFind : getMythicFindsData().getMythicFinds()) {
            ButtonText buttonText = new ButtonText(mythicFind.getMythicFindItem() == null ? "Copy from hand" : "Reset");
            String chestCountText = "§8#§3" + getFormatted(mythicFind.getChestCount());
            int dryCount = mythicBefore != null && mythicBefore.isApproximately()
                    ? mythicFind.getChestCount() - mythicBefore.getChestCount()
                    : mythicFind.getDry();
            String dryCountText = FormatterHelper.getFormattedDry(dryCount) + " §e" + "dry";
            if (mythicFind.isApproximately()) {
                chestCountText = "§8~" + chestCountText;
            }
            if ((mythicBefore != null && mythicBefore.isApproximately()) || mythicFind.isApproximately()) {
                dryCountText = "§8~" + dryCountText;
            }
            addLine(new LineMythicFindHistoryEntry(
                    ++id,
                    "#" + mythicNumber++,
                    mythicFind::getItem,
                    () -> {
                        if (mythicFind.getMythicFindItem() != null)
                            return;
                        mythicFind.toggleMythicFindMythic();
                        getMythicFindsData().save();
                    },
                    () -> {
                        List<String> lore = new ArrayList<>();
                        lore.add("§3Date§7: §e" + (mythicFind.getTime() == null ? "§cUnknown" : mythicFind.getTime().toString()));
                        lore.add("§3Location§7: §e" + mythicFind.getX() + "§7,§e" + mythicFind.getY() + "§7,§e" + mythicFind.getZ());
                        return lore;
                    },
                    () -> {
                        List<String> lore = new ArrayList<>();
                        if (mythicFind.getMythicFindItem() != null) {
                            ItemStack itemStack = mythicFind.getItem();
                            lore.add(itemStack.getDisplayName());
                            lore.addAll(ItemStackHelper.getLore(itemStack));
                        } else {
                            Mythic current = mythicFind.getMythic();
                            List<Mythic> possible = mythicFind.getPossibleMythics();
                            lore.add(mythicFind.getBoxTitle());
                            lore.add("§7 ");
                            for (Mythic mythic : possible) {
                                lore.add((mythic == current ? "⬠" : "§7• ") + " §5" + mythic.getName());
                            }
                            lore.add("§7 ");
                            lore.add("§eClick to toggle.");
                        }
                        return lore;
                    },
                    () -> {
                        int emeraldsDry = mythicFind.getEmeraldsDry();
                        Map<Tier, Integer> tiers = mythicFind.getItemsDry();
                        List<String> lore = new ArrayList<>();
                        lore.add("§aEmeralds §edry§7: §a" + getFormatted(emeraldsDry));
                        int sum = tiers.values().stream().mapToInt(i -> i).sum();
                        lore.add("§eItems dry§7: §e" + getFormatted(sum));
                        DecimalFormat decimalFormat = new DecimalFormat("#0.0");
                        for (Map.Entry<Tier, Integer> tierDry : tiers.entrySet()) {
                            Tier tier = tierDry.getKey();
                            if (tier == Tier.MYTHIC)
                                continue; // will never be seen there
                            int dry = tierDry.getValue();
                            double percentage = sum == 0 ? 0 : dry / (double) sum * 100;
                            lore.add("§7  " + tier.getDisplayName() + "§7: §e" + getFormatted(dry) + " §7(§e" + decimalFormat.format(percentage) + "%§7)");
                        }
                        return lore;
                    },
                    mythicFind::getMythicFindTitle,
                    chestCountText,
                    dryCountText,
                    width / 2 - totalWidth / 2 + leftWidth + centerWidth + 6 / 2, // spacing 6
                    100 - 6 / 2, // right width, spacing 6
                    lineHeight,
                    buttonText::getText,
                    () -> {
                        if (mythicFind.getMythicFindItem() != null) {
                            mythicFind.setMythicFindItem(null);
                            getMythicFindsData().save();
                            buttonText.changeText("Copy from hand");
                            return;
                        }
                        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
                        if (itemStack.isEmpty()) {
                            buttonText.changeText("No item", 2000);
                            return;
                        }
                        Mythic mythic = Mythic.getMythicByName(itemStack.getDisplayName());
                        if (mythic == null) {
                            buttonText.changeText("Not mythic", 2000);
                            return;
                        }
                        if (mythic.getType() != mythicFind.getType()) {
                            buttonText.changeText("Wrong type", 2000);
                            return;
                        }
                        if (!mythicFind.getBoxMinMax().isInRange(mythic.getLevel())) {
                            buttonText.changeText("Wrong level", 2000);
                            return;
                        }
                        mythicFind.setMythicFindItem(itemStack);
                        getMythicFindsData().save();
                        buttonText.changeText("Reset");
                        buttonText.changeText("Success!", 2000);
                    }
            ));
            mythicBefore = mythicFind;
        }
        scrollToBottom();
    }
}
