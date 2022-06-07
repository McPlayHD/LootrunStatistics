package net.mcplayhd.lootrunstatistics.chests.utils;

import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.instances.LootRunNote;
import net.mcplayhd.lootrunstatistics.utils.Loc;

public class NoteDrawer {

    private final ChestInfo chestInfo;
    private LootRunNote note;

    public NoteDrawer(ChestInfo chestInfo) {
        this.chestInfo = chestInfo;
    }

    public void updateNote() {
        // TODO: 03/06/2022 configurable
        // TODO: 03/06/2022 also display possible mythics
        MinMax minMax = chestInfo.getMinMax();
        StringBuilder note = new StringBuilder();
        if (minMax.isEmpty()) {
            note.append("§b").append("Lv. ? - ?");
        } else {
            int confirmedRange = minMax.getDifference();
            if (confirmedRange > 8) {
                // Unlevelled chest
                note.append("§b").append("Unlevelled");
            } else if (confirmedRange < 8 && minMax.getMax() >= 100 && chestInfo.getLevelsSeen().values().stream().mapToInt(i -> i).sum() > 90) {
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
        Loc loc = chestInfo.getLoc();
        this.note = new LootRunNote(new Location(loc.getX() + 0.5, loc.getY() - 0.4, loc.getZ() + 0.5), note.toString());
    }

    public void drawNote() {
        if (note != null) {
            note.drawNote(MinecraftChatColors.WHITE);
        }
    }

}
