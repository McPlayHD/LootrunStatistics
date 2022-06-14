package net.mcplayhd.lootrunstatistics.gui.drawables;

import net.mcplayhd.lootrunstatistics.gui.handlers.TextChangeHandler;
import net.mcplayhd.lootrunstatistics.gui.handlers.TextHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class DrawableLineItemTextTextAreaButton extends DrawableLine {
    private final ItemStack itemLeftThirdFarLeft;
    private final String textLeft;
    private final GuiTextField textFieldCenter;
    private final TextChangeHandler onTextFieldCenterChanged;
    private final GuiButton buttonRight;
    private final TextHandler buttonTextHandler;
    private final Runnable onButtonPressed;

    public DrawableLineItemTextTextAreaButton(int id, ItemStack itemLeftThirdFarLeft, String textLeft, String textFieldContent, TextChangeHandler onTextFieldCenterChanged, int buttonX, int buttonWidth, int buttonHeight, TextHandler buttonTextHandler, Runnable onButtonPressed) {
        super(id);
        this.itemLeftThirdFarLeft = itemLeftThirdFarLeft;
        this.textLeft = textLeft;
        textFieldCenter = new GuiTextField(id * 10000, Minecraft.getMinecraft().fontRenderer, -1000, -1000, 100, 20);
        textFieldCenter.setText(textFieldContent);
        textFieldCenter.setMaxStringLength(16);
        textFieldCenter.setFocused(false);
        textFieldCenter.setCanLoseFocus(true);
        this.onTextFieldCenterChanged = onTextFieldCenterChanged;
        buttonRight = new GuiButtonExt(id, buttonX, 0, buttonWidth, buttonHeight, buttonTextHandler.getText());
        this.buttonTextHandler = buttonTextHandler;
        this.onButtonPressed = onButtonPressed;
    }

    @Override
    public void setY(int y) {
        buttonRight.y = y;
        super.setY(y);
    }

    @Override
    public ItemStack getItemLeftThirdLeft() {
        return itemLeftThirdFarLeft;
    }

    @Override
    public String getLeftText() {
        return textLeft;
    }

    @Override
    public GuiTextField getCenterTextField() {
        return textFieldCenter;
    }

    @Override
    public void onCenterTextFieldChanged(String text) {
        if (onTextFieldCenterChanged == null) return;
        onTextFieldCenterChanged.textChanged(text);
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
