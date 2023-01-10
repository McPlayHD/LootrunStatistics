package net.mcplayhd.lootrunstatistics.gui;

import net.mcplayhd.lootrunstatistics.gui.drawables.*;
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
    public static final int columnSpace = 6;
    private final int spaceTop;
    private final String title;
    private final String subtitle;
    public static final int spaceBottom = 40;
    // idea to only open the gui one tick later from https://github.com/albarv340/chestcountmod
    public static CustomGui shouldBeDrawn = null;
    private final GuiScreen parentScreen;

    private final List<DrawableLine> lines = new ArrayList<>();

    private int scrollPosition = 0;

    public CustomGui(GuiScreen parentScreen, int spaceTop, String title, String subtitle) {
        this.parentScreen = parentScreen;
        this.spaceTop = spaceTop;
        this.title = title;
        this.subtitle = subtitle;
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

    public void scrollToBottom() {
        scrollPosition = lines.size() - getMaxLines();
    }

    private void addButtonsAndSetLinePositions() {
        buttonList = new ArrayList<>();
        int y = spaceTop;
        for (DrawableLine line : lines) {
            if (line.getId() < scrollPosition || line.getId() >= scrollPosition + getMaxLines())
                continue;
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
        this.drawCenteredString(fontRenderer, title, width / 2, spaceTop / (subtitle == null ? 2 : 3) - (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
        if (subtitle != null) {
            this.drawCenteredString(fontRenderer, subtitle, width / 2, spaceTop * 2 / 3 - (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
        }

        int maxLeftTextWidth = 0;
        for (DrawableLine line : lines) {
            if (line.getLeftText() != null) {
                int textWidth = fontRenderer.getStringWidth(line.getLeftText());
                maxLeftTextWidth = Math.max(maxLeftTextWidth, textWidth);
            }
        }

        List<String> hoveringText = new ArrayList<>();
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
            if (line instanceof LineMythicFindHistoryColumnNames) {
                LineMythicFindHistoryColumnNames line1 = (LineMythicFindHistoryColumnNames) line;
                int totalWidth = 420; // everyone that players with a smaller width is weird.
                int leftWidth = 170;
                this.drawString(fontRenderer, line1.getTextLeftCenterRight(), width / 2 - totalWidth / 2 + 18 + columnSpace, y + (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
                this.drawString(fontRenderer, line1.getTextCenterLeft(), width / 2 - totalWidth / 2 + leftWidth, y + (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
                this.drawString(fontRenderer, line1.getTextCenterRight(), width / 2 - totalWidth / 2 + leftWidth + 80 + columnSpace / 2, y + (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
            }
            if (line instanceof LineMythicFindHistoryEntry) {
                int totalWidth = 420; // everyone that players with a smaller width is weird.
                int leftWidth = 170;
                int centerWidth = 150;
                int rightWidth = 100;
                LineMythicFindHistoryEntry line1 = (LineMythicFindHistoryEntry) line;
                this.drawString(fontRenderer, line1.getTextLeftLeft(), width / 2 - totalWidth / 2, y + (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
                int textWidth1 = fontRenderer.getStringWidth(line1.getTextLeftLeft());
                if (isHovered(mouseX, mouseY, width / 2 - totalWidth / 2, y + (lineHeight - 8) / 2, textWidth1, 10)) {
                    hoveringText.addAll(line1.getMythicFindInfoLore());
                }
                this.drawItemStack(line1.getItemLeftCenterRight(), width / 2 - totalWidth / 2 + 18 + columnSpace, y + 1, false, "", false);
                if (isHovered(mouseX, mouseY, width / 2 - totalWidth / 2 + 18 + columnSpace, y + 1, 16, 16)) {
                    hoveringText.addAll(line1.getItemStackLore());
                }
                this.drawString(fontRenderer, line1.getTextLeftRight(), width / 2 - totalWidth / 2 + 18 + 16 + 2 * columnSpace, y + (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
                this.drawString(fontRenderer, line1.getTextCenterLeft(), width / 2 - totalWidth / 2 + leftWidth, y + (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
                this.drawString(fontRenderer, line1.getTextCenterRight(), width / 2 - totalWidth / 2 + leftWidth + 80 + columnSpace / 2, y + (lineHeight - 8) / 2, new Color(255, 255, 255).getRGB());
                int textWidth2 = fontRenderer.getStringWidth(line1.getTextCenterRight());
                if (isHovered(mouseX, mouseY, width / 2 - totalWidth / 2 + leftWidth + 80 + columnSpace / 2, y + (lineHeight - 8) / 2, textWidth2, 10)) {
                    hoveringText.addAll(line1.getDryInfoLore());
                }
                line.getRightButton().displayString = line1.getRightButtonText();
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!hoveringText.isEmpty()) {
            drawHoveringText(hoveringText, mouseX, mouseY);
        }
    }

    private boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
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
        int clickX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int clickY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int mouseButton = Mouse.getEventButton();
        if (mouseButton != -1 && Mouse.getEventButtonState()) {
            for (DrawableLine line : lines) {
                if (line.getId() < scrollPosition || line.getId() >= scrollPosition + getMaxLines())
                    continue;
                if (line instanceof DrawableLineItemTextTextAreaButton) {
                    if (line.getCenterTextField() != null) {
                        line.getCenterTextField().mouseClicked(clickX, clickY, mouseButton);
                    }
                }
                if (line instanceof LineMythicFindHistoryEntry) {
                    int y = line.getY();
                    int totalWidth = 420; // everyone that players with a smaller width is weird.
                    if (isHovered(clickX, clickY, width / 2 - totalWidth / 2 + 18 + columnSpace, y + 1, 16, 16)) {
                        ((LineMythicFindHistoryEntry) line).onItemStackClicked();
                    }
                }
            }
        }
        int scrollAmount = Mouse.getDWheel() / 120;
        int scrollSpeed = isShiftKeyDown() ? getMaxLines() : 1;
        if (scrollAmount != 0) {
            int maxLines = getMaxLines();
            if (scrollAmount < 0 && scrollPosition < lines.size() - maxLines) {
                scrollPosition += scrollSpeed;
                scrollPosition = Math.min(scrollPosition, lines.size() - maxLines);
                addButtonsAndSetLinePositions();
            } else if (scrollAmount > 0 && scrollPosition > 0) {
                scrollPosition -= scrollSpeed;
                scrollPosition = Math.max(scrollPosition, 0);
                addButtonsAndSetLinePositions();
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
