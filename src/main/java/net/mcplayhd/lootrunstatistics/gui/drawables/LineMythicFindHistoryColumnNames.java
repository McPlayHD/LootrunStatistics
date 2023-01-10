package net.mcplayhd.lootrunstatistics.gui.drawables;

public class LineMythicFindHistoryColumnNames extends DrawableLine {
    private final String textLeftCenterRight;
    private final String textCenterLeft;
    private final String textCenterRight;

    public LineMythicFindHistoryColumnNames(int id, String textLeftCenterRight, String textCenterLeft, String textCenterRight) {
        super(id);
        this.textLeftCenterRight = textLeftCenterRight;
        this.textCenterLeft = textCenterLeft;
        this.textCenterRight = textCenterRight;
    }

    public String getTextLeftCenterRight() {
        return textLeftCenterRight;
    }

    public String getTextCenterLeft() {
        return textCenterLeft;
    }

    public String getTextCenterRight() {
        return textCenterRight;
    }
}
