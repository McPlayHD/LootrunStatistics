package net.mcplayhd.lootrunstatistics.gui.guis.history;

import net.mcplayhd.lootrunstatistics.gui.CustomGui;
import net.mcplayhd.lootrunstatistics.gui.drawables.DrawableLineTextItemTextTextTextButton;
import net.mcplayhd.lootrunstatistics.gui.utils.ButtonText;
import net.mcplayhd.lootrunstatistics.helpers.FormatterHelper;
import net.mcplayhd.lootrunstatistics.utils.Mythic;
import net.mcplayhd.lootrunstatistics.utils.MythicFind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

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
        for (MythicFind mythicFind : getMythicFindsData().getMythicFinds()) {
            ButtonText buttonText = new ButtonText("Copy from Hand");
            // TODO: 27/06/2022 add reset
            addLine(new DrawableLineTextItemTextTextTextButton(
                    ++id,
                    "#" + mythicNumber++,
                    mythicFind::getItem,
                    mythicFind::getMythic,
                    "§8#§3" + FormatterHelper.getFormatted(mythicFind.getChestCount()),
                    FormatterHelper.getFormattedDry(mythicFind.getDry()) + " §e" + "dry",
                    width / 2 - totalWidth / 2 + leftWidth + centerWidth + 6 / 2, // spacing 6
                    100, // right width
                    lineHeight,
                    buttonText::getText,
                    // TODO: 25/06/2022 add different way to set mythic if you don't have the item anymore
                    () -> {
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
                        buttonText.changeText("Success!");
                    }
            ));
        }
        scrollToBottom();
    }
}
