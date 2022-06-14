package net.mcplayhd.lootrunstatistics.gui.drawables;

import net.mcplayhd.lootrunstatistics.gui.handlers.TextHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class DrawableLineTextLeftButtonRight extends DrawableLine {
    private final String textLeft;
    private final TextHandler buttonTextHandler;
    private final GuiButton buttonRight;
    private final Runnable onButtonPressed;

    public DrawableLineTextLeftButtonRight(int id, String textLeft, int buttonX, int buttonWidth, int buttonHeight, TextHandler buttonTextHandler, boolean buttonEnabled, Runnable onButtonPressed) {
        super(id);
        this.textLeft = textLeft;
        this.buttonTextHandler = buttonTextHandler;
        buttonRight = new GuiButtonExt(id, buttonX, 0, buttonWidth, buttonHeight, buttonTextHandler.getText());
        buttonRight.enabled = buttonEnabled;
        this.onButtonPressed = onButtonPressed;
    }

    @Override
    public void setY(int y) {
        buttonRight.y = y;
        super.setY(y);
    }

    @Override
    public String getLeftText() {
        return textLeft;
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
