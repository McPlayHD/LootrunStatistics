package net.mcplayhd.lootrunstatistics.gui.drawables;

public class DrawableLineTextCenterSubtitle extends DrawableLine {
    private final String textCenter;

    public DrawableLineTextCenterSubtitle(int id, String textCenter) {
        super(id);
        this.textCenter = textCenter;
    }

    @Override
    public String getCenterText() {
        return textCenter;
    }
}
