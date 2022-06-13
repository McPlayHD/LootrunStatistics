package net.mcplayhd.lootrunstatistics.chests.utils;

import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.instances.LootRunNote;
import net.mcplayhd.lootrunstatistics.configuration.Configuration;
import net.mcplayhd.lootrunstatistics.utils.Loc;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getConfiguration;

public class NoteDrawer {

    private final ChestInfo chestInfo;
    private LootRunNote note;

    public NoteDrawer(ChestInfo chestInfo) {
        this.chestInfo = chestInfo;
    }

    public void updateNote() {
        MinMax minMax = chestInfo.getMinMax().clone();
        StringBuilder note = new StringBuilder();
        int confirmedRange = minMax.getDifference();
        boolean unlevelled = confirmedRange > 8;
        boolean highLevelUnconfirmedButEnoughItems
                = confirmedRange < 8
                && minMax.getMax() >= 100
                && chestInfo.getLevelsSeen().values().stream().mapToInt(i -> i).sum() > 69;
        if (getConfiguration().displayLevelRangeAboveChests()) {
            if (minMax.isEmpty()) {
                note.append("§b").append("Lv. ? - ?");
            } else {
                if (unlevelled) {
                    // Unlevelled chest
                    note.append("§b").append("Unlevelled");
                } else if (highLevelUnconfirmedButEnoughItems) {
                    // Sadly, in Wynncraft there are no "Normal" items above level 100. We have to improvise.
                    // In 80 "Normal" items we didn't find the edge case item level once.
                    // We just assume the chest level to be from `min` to `(min+8)`
                    note.append("§b").append("Lv. §3").append(minMax.getMin()).append(" §b- §6").append(minMax.getMin() + 8);
                } else if (confirmedRange == 8) {
                    note.append("§b").append("Lv. §3").append(minMax.getMin()).append(" §b- §3").append(minMax.getMax());
                } else {
                    note.append("§b").append("Lv. §b").append(minMax.getMin()).append(" §b- §b").append(minMax.getMax());
                }
            }
        }
        Configuration.MythicsAboveChests mythicsAboveChests = getConfiguration().getMythicsAboveChests();
        if (mythicsAboveChests != Configuration.MythicsAboveChests.NONE) {
            if (note.length() > 0) {
                note.append("\\n");
            }
            // TODO: 12/06/2022  display mythics
        }
        if (note.length() == 0) {
            this.note = null;
            return;
        }
        Loc loc = chestInfo.getLoc();
        this.note = new LootRunNote(new Location(loc.getX() + 0.5, loc.getY() - 0.4, loc.getZ() + 0.5), note.toString());
    }

    public void drawNote() {
        if (note == null) return;
        note.drawNote(MinecraftChatColors.WHITE);
    }

}
