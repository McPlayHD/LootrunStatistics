package net.mcplayhd.lootrunstatistics.gui.utils;

public class ButtonText {
    private String defaultText;
    private String text;
    private long resetAt = -1;

    public ButtonText(String text) {
        this.defaultText = text;
        this.text = text;
    }

    public String getText() {
        if (resetAt != -1 && resetAt < System.currentTimeMillis()) {
            text = defaultText;
            resetAt = -1;
        }
        return text;
    }

    public void changeText(String text) {
        changeText(text, -1);
    }

    public void changeText(String text, long resetAfter) {
        if (resetAfter == -1) {
            defaultText = text;
        }
        this.text = text;
        this.resetAt = resetAfter == -1 ? -1 : System.currentTimeMillis() + resetAfter;
    }
}
