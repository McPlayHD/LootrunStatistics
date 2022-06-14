package net.mcplayhd.lootrunstatistics.gui.drawables;

import net.mcplayhd.lootrunstatistics.gui.handlers.TextHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class DrawableLineButtonRight extends DrawableLine {
    private final TextHandler buttonTextHandler;
    private final GuiButton buttonRight;
    private final Runnable onButtonPressed;

    public DrawableLineButtonRight(int id, int buttonX, int buttonWidth, int buttonHeight, TextHandler buttonTextHandler, Runnable onButtonPressed) {
        super(id);
        this.buttonTextHandler = buttonTextHandler;
        buttonRight = new GuiButtonExt(id, buttonX, 0, buttonWidth, buttonHeight, buttonTextHandler.getText());
        this.onButtonPressed = onButtonPressed;
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
