package net.mcplayhd.lootrunstatistics.gui;

import net.mcplayhd.lootrunstatistics.LootrunStatistics;
import net.mcplayhd.lootrunstatistics.gui.drawables.DrawableLine;
import net.mcplayhd.lootrunstatistics.gui.drawables.DrawableLineItemTextTextAreaButton;
import net.mcplayhd.lootrunstatistics.gui.drawables.DrawableLineTextCenterSubtitle;
import net.mcplayhd.lootrunstatistics.gui.drawables.DrawableLineTextLeftButtonRight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomGui extends GuiScreen {
    public static final int lineSpace = 5;
    public static final int lineHeight = 20;
    public static final int columnSpace = 8;
    public static final int spaceTop = 60;
    public static final int spaceBottom = 40;
    // idea to only open the gui one tick later from https://github.com/albarv340/chestcountmod
    public static CustomGui shouldBeDrawn = null;
    private final GuiScreen parentScreen;

    private final List<DrawableLine> lines = new ArrayList<>();

    private int scrollPosition = 0;

    public CustomGui(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        lines.clear();
        Keyboard.enableRepeatEvents(true);

        onInitGui();

        addButtonsAndSetLinePositions();
    }

    public void onInitGui() {
    }

    public void addLine(DrawableLine line) {
        lines.add(line);
    }

    private int getMaxLines() {
        return (height - spaceTop - spaceBottom) / (lineSpace * 2 + lineHeight);
    }

    private void addButtonsAndSetLinePositions() {
        buttonList = new ArrayList<>();
        int y = spaceTop;
        for (DrawableLine line : lines) {
            if (line.getId() < scrollPosition || line.getId() >= scrollPosition + getMaxLines()) continue;
            y += lineSpace;
            line.setY(y);
            if (line.getRightButton() != null) {
                buttonList.add(line.getRightButton());
            }
            y += lineHeight + lineSpace;
        }

        // done button
        int doneWidth = Math.max(fontRenderer.getStringWidth("Done") + 20, 100);
        this.buttonList.add(new GuiButtonExt(2000, width / 2 - doneWidth / 2, height - spaceBottom / 2 - lineHeight / 2, doneWidth, lineHeight, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 2000) {
            this.mc.displayGuiScreen(parentScreen);
        }
        for (DrawableLine line : lines) {
            if (line.getRightButton() != null) {
                if (line.getRightButton().id == button.id) {
                    line.rightButtonPressed();
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(fontRenderer, LootrunStatistics.NAME + " v" + LootrunStatistics.VERSION, width / 2, spaceTop / 3 - (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
        this.drawCenteredString(fontRenderer, "by McPlayHD", width / 2, spaceTop * 2 / 3 - (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());

        int maxLeftTextWidth = 0;
        for (DrawableLine line : lines) {
            if (line.getLeftText() != null) {
                int textWidth = fontRenderer.getStringWidth(line.getLeftText());
                maxLeftTextWidth = Math.max(maxLeftTextWidth, textWidth);
            }
        }

        int maxLines = getMaxLines();
        for (DrawableLine line : lines) {
            if (line.getId() < scrollPosition || line.getId() >= scrollPosition + maxLines) {
                if (line.getCenterTextField() != null) {
                    // put away the TextFields
                    GuiTextField centerTextField = line.getCenterTextField();
                    centerTextField.x = -1000;
                    centerTextField.y = -1000;
                }
                continue;
            }
            int y = line.getY();
            if (line instanceof DrawableLineTextLeftButtonRight) {
                if (line.getLeftText() != null) {
                    this.drawString(fontRenderer, line.getLeftText(), width / 2 - maxLeftTextWidth - 10, y + (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
                }
            }
            if (line instanceof DrawableLineTextCenterSubtitle) {
                if (line.getCenterText() != null) {
                    this.drawCenteredString(fontRenderer, line.getCenterText(), width / 2, y + (lineHeight - 8) / 2, new Color(255, 216, 61).getRGB());
                }
            }
            if (line instanceof DrawableLineItemTextTextAreaButton) {
                int leftWidth = 120;
                int centerWidth = 100;
                if (line.getItemLeftThirdLeft() != null) {
                    this.drawItemStack(line.getItemLeftThirdLeft(), width / 2 - centerWidth / 2 - columnSpace - leftWidth, y + 1, false, "", false);
                    this.drawString(fontRenderer, line.getLeftText(), width / 2 - centerWidth / 2 - columnSpace - leftWidth + 25, y + (lineHeight - 8) / 2, new Color(137, 50, 183).getRGB());
                }
                if (line.getCenterTextField() != null) {
                    GuiTextField centerTextField = line.getCenterTextField();
                    centerTextField.x = width / 2 - centerWidth / 2;
                    centerTextField.y = y;
                    centerTextField.width = centerWidth;
                    centerTextField.height = lineHeight;
                    centerTextField.drawTextBox();
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        for (DrawableLine line : lines) {
            if (line instanceof DrawableLineItemTextTextAreaButton) {
                if (line.getCenterTextField() != null) {
                    line.getCenterTextField().updateCursorCounter();
                }
            }
        }
        super.updateScreen();
    }

    // copied from com.wynntils.core.framework.rendering.ScreenRenderer
    private void drawItemStack(ItemStack is, int x, int y, boolean count, String text, boolean effects) {
        RenderHelper.enableGUIStandardItemLighting();
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = is.getItem().getFontRenderer(is);
        if (font == null) font = fontRenderer;
        if (effects)
            itemRenderer.renderItemAndEffectIntoGUI(is, x, y);
        else
            itemRenderer.renderItemIntoGUI(is, x, y);
        itemRenderer.renderItemOverlayIntoGUI(font, is, x, y, text.isEmpty() ? count ? Integer.toString(is.getCount()) : null : text);
        itemRenderer.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(parentScreen);
            return;
        }
        for (DrawableLine line : lines) {
            if (line instanceof DrawableLineItemTextTextAreaButton) {
                if (line.getCenterTextField() != null) {
                    if (line.getCenterTextField().textboxKeyTyped(typedChar, keyCode)) {
                        line.onCenterTextFieldChanged(line.getCenterTextField().getText());
                        if (line.getId() < scrollPosition) {
                            scrollPosition = line.getId();
                            addButtonsAndSetLinePositions();
                        } else if (line.getId() >= scrollPosition + getMaxLines()) {
                            scrollPosition = line.getId() - getMaxLines() + 1;
                            addButtonsAndSetLinePositions();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int mouseButton = Mouse.getEventButton();
        if (mouseButton != -1 && Mouse.getEventButtonState()) {
            for (DrawableLine line : lines) {
                if (line instanceof DrawableLineItemTextTextAreaButton) {
                    if (line.getCenterTextField() != null) {
                        line.getCenterTextField().mouseClicked(x, y, mouseButton);
                    }
                }
            }
        }
        int scrollAmount = Mouse.getDWheel() / 120;
        if (scrollAmount != 0) {
            int maxLines = getMaxLines();
            if (scrollAmount < 0 && scrollPosition < lines.size() - maxLines) {
                scrollPosition++;
                addButtonsAndSetLinePositions();
            } else if (scrollAmount > 0 && scrollPosition > 0) {
                scrollPosition--;
                addButtonsAndSetLinePositions();
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
