package net.mcplayhd.lootrunstatistics.gui.drawables;

import net.mcplayhd.lootrunstatistics.gui.handlers.ItemStackHandler;
import net.mcplayhd.lootrunstatistics.gui.handlers.TextHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class DrawableLineTextItemTextTextTextButton extends DrawableLine {
    private final String textLeftLeft;
    private final ItemStackHandler itemHandlerLeftCenterRight;
    private final TextHandler textLeftRight;
    private final String textCenterLeft;
    private final String textCenterRight;
    private final GuiButton buttonRight;
    private final TextHandler buttonTextHandler;
    private final Runnable onButtonPressed;

    public DrawableLineTextItemTextTextTextButton(int id,
                                                  String textLeftLeft,
                                                  ItemStackHandler itemHandlerLeftCenterRight,
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
