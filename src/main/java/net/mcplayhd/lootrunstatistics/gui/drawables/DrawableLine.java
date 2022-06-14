package net.mcplayhd.lootrunstatistics.gui.drawables;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;

public class DrawableLine {
    private final int id;
    private int y;

    public DrawableLine(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /*
    DrawableLineTextLeftButtonRight
     */
    public String getLeftText() {
        return null;
    }

    public GuiButton getRightButton() {
        return null;
    }

    public void rightButtonPressed() {
    }

    /*
    DrawableLineButtonRight
     */
    // nothing new

    /*
    DrawableLineTextCenterSubtitle
     */
    public String getCenterText() {
        return null;
    }

    /*
    DrawableLineItemTextTextFieldButton
     */
    public ItemStack getItemLeftThirdLeft() {
        return null;
    }

    public GuiTextField getCenterTextField() {
        return null;
    }

    public void onCenterTextFieldChanged(String text) {
    }
}
