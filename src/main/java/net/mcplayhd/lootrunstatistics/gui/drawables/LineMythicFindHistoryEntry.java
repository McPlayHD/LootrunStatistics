package net.mcplayhd.lootrunstatistics.gui.drawables;

import net.mcplayhd.lootrunstatistics.gui.handlers.ItemStackHandler;
import net.mcplayhd.lootrunstatistics.gui.handlers.LoreHandler;
import net.mcplayhd.lootrunstatistics.gui.handlers.TextHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.List;

public class LineMythicFindHistoryEntry extends DrawableLine {
    private final String textLeftLeft;
    private final ItemStackHandler itemHandlerLeftCenterRight;
    private final Runnable onItemStackClicked;
    private final LoreHandler mythicFindInfoLoreHandler;
    private final LoreHandler itemStackLoreHandler;
    private final LoreHandler dryInfoLoreHandler;
    private final TextHandler textLeftRight;
    private final String textCenterLeft;
    private final String textCenterRight;
    private final GuiButton buttonRight;
    private final TextHandler buttonTextHandler;
    private final Runnable onButtonPressed;

    public LineMythicFindHistoryEntry(int id,
                                      String textLeftLeft,
                                      ItemStackHandler itemHandlerLeftCenterRight,
                                      Runnable onItemStackClicked,
                                      LoreHandler mythicFindInfoLoreHandler,
                                      LoreHandler itemStackLoreHandler,
                                      LoreHandler dryInfoLoreHandler,
                                      TextHandler textLeftRight,
                                      String textCenterLeft,
                                      String textCenterRight,
                                      int buttonX,
                                      int buttonWidth,
                                      int buttonHeight,
                                      TextHandler buttonTextHandler,
                                      Runnable onButtonPressed) {
        super(id);
        this.textLeftLeft = textLeftLeft;
        this.itemHandlerLeftCenterRight = itemHandlerLeftCenterRight;
        this.onItemStackClicked = onItemStackClicked;
        this.mythicFindInfoLoreHandler = mythicFindInfoLoreHandler;
        this.itemStackLoreHandler = itemStackLoreHandler;
        this.dryInfoLoreHandler = dryInfoLoreHandler;
        this.textLeftRight = textLeftRight;
        this.textCenterLeft = textCenterLeft;
        this.textCenterRight = textCenterRight;
        buttonRight = new GuiButtonExt(id, buttonX, 0, buttonWidth, buttonHeight, buttonTextHandler.getText());
        this.buttonTextHandler = buttonTextHandler;
        this.onButtonPressed = onButtonPressed;
    }

    public String getTextLeftLeft() {
        return textLeftLeft;
    }

    public ItemStack getItemLeftCenterRight() {
        return itemHandlerLeftCenterRight.getItemStack();
    }

    public void onItemStackClicked() {
        if (onItemStackClicked == null) return;
        onItemStackClicked.run();
    }

    public List<String> getMythicFindInfoLore() {
        return mythicFindInfoLoreHandler.getLore();
    }

    public List<String> getItemStackLore() {
        return itemStackLoreHandler.getLore();
    }

    public List<String> getDryInfoLore() {
        return dryInfoLoreHandler.getLore();
    }

    public String getTextLeftRight() {
        return textLeftRight.getText();
    }

    public String getTextCenterLeft() {
        return textCenterLeft;
    }

    public String getTextCenterRight() {
        return textCenterRight;
    }

    public String getRightButtonText() {
        return buttonTextHandler.getText();
    }

    @Override
    public void setY(int y) {
        buttonRight.y = y;
        super.setY(y);
    }

    @Override
    public GuiButton getRightButton() {
        return buttonRight;
    }

    @Override
    public void rightButtonPressed() {
        if (onButtonPressed == null) return;
        onButtonPressed.run();
        buttonRight.displayString = getRightButtonText();
    }
}
