package net.mcplayhd.lootrunstatistics.gui.guis.history;

import net.mcplayhd.lootrunstatistics.gui.CustomGui;
import net.mcplayhd.lootrunstatistics.gui.drawables.DrawableLineTextItemTextTextTextButton;
import net.mcplayhd.lootrunstatistics.utils.MythicFind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getMythicFindsData;

public class HistoryGuiMythics extends CustomGui {

    public HistoryGuiMythics(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void onInitGui() {
        int id = -1; // -1 so the first will get 0 because of ++id
        int mythicNumber = 1;
        for (MythicFind mythicFind : getMythicFindsData().getMythicFinds()) {
            addLine(new DrawableLineTextItemTextTextTextButton(
                    ++id,
                    "#" + mythicNumber++,
                    mythicFind::getItem,
                    mythicFind.getMythic(),
                    "#" + mythicFind.getChestCount(),
                    mythicFind.getDry() + " dry",
                    width / 2 + 100 / 2 + 8, // center size 100, spacing column 8
                    120,
                    lineHeight,
                    () -> "Copy",
                    // TODO: 25/06/2022 add different way to set mythic if you don't have the item anymore
                    () -> {
                        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
                        // TODO: 25/06/2022 check if even possible
                        mythicFind.setMythicFindItem(itemStack);
                        getMythicFindsData().save();
                    }
            ));
        }
    }
}
