package net.mcplayhd.lootrunstatistics.gui.guis.history;

import net.mcplayhd.lootrunstatistics.gui.CustomGui;
import net.mcplayhd.lootrunstatistics.gui.drawables.LineMythicFindHistoryEntry;
import net.mcplayhd.lootrunstatistics.gui.utils.ButtonText;
import net.mcplayhd.lootrunstatistics.helpers.FormatterHelper;
import net.mcplayhd.lootrunstatistics.helpers.ItemStackHelper;
import net.mcplayhd.lootrunstatistics.utils.Mythic;
import net.mcplayhd.lootrunstatistics.utils.MythicFind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getMythicFindsData;

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
        // TODO: 27/06/2022 show detailed dry info when hovering over dry
        // TODO: 27/06/2022 show time and place of mythic found when hovering over number
        for (MythicFind mythicFind : getMythicFindsData().getMythicFinds()) {
            ButtonText buttonText = new ButtonText(mythicFind.getMythicFindItem() == null ? "Copy from hand" : "Reset");
            String chestCountText = "§8#§3" + FormatterHelper.getFormatted(mythicFind.getChestCount());
            int dryCount = mythicBefore != null && mythicBefore.isApproximately()
                    ? mythicFind.getChestCount() - mythicBefore.getChestCount()
                    : mythicFind.getDry();
            String dryCountText = FormatterHelper.getFormattedDry(dryCount) + " §e" + "dry";
            if ((mythicBefore != null && mythicBefore.isApproximately()) || mythicFind.isApproximately()) {
                chestCountText = "§8~" + chestCountText;
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
