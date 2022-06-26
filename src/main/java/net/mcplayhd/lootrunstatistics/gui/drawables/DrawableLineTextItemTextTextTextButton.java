package net.mcplayhd.lootrunstatistics.gui.drawables;

import net.mcplayhd.lootrunstatistics.gui.handlers.ItemStackHandler;
import net.mcplayhd.lootrunstatistics.gui.handlers.TextHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class DrawableLineTextItemTextTextTextButton extends DrawableLine {
    private final String textLeftLeft;
    private final ItemStackHandler itemHandlerLeftCenter;
    private final String textLeftRight;
    private final String textCenterLeft;
    private final String textCenterRight;
    private final GuiButton buttonRight;
    private final TextHandler buttonTextHandler;
    private final Runnable onButtonPressed;

    public DrawableLineTextItemTextTextTextButton(int id,
                                                  String textLeftLeft,
                                                  ItemStackHandler itemHandlerLeftCenter,
                                                  String textLeftRight,
                                                  String textCenterLeft,
                                                  String textCenterRight,
                                                  int buttonX,
                                                  int buttonWidth,
                                                  int buttonHeight,
                                                  TextHandler buttonTextHandler,
                                                  Runnable onButtonPressed) {
        super(id);
        this.textLeftLeft = textLeftLeft;
        this.itemHandlerLeftCenter = itemHandlerLeftCenter;
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

    public ItemStack getItemLeftCenter() {
        return itemHandlerLeftCenter.getItemStack();
    }

    public String getTextLeftRight() {
        return textLeftRight;
    }

    public String getTextCenterLeft() {
        return textCenterLeft;
    }

    public String getTextCenterRight() {
        return textCenterRight;
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
        buttonRight.displayString = buttonTextHandler.getText();
    }
}
