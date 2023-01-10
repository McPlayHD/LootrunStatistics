package net.mcplayhd.lootrunstatistics.chests.utils;

import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.instances.LootRunNote;
import net.mcplayhd.lootrunstatistics.api.WynncraftAPI;
import net.mcplayhd.lootrunstatistics.configuration.Configuration;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.utils.Loc;
import net.mcplayhd.lootrunstatistics.utils.Mythic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getConfiguration;
import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getPlayerLevel;

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
        if (highLevelUnconfirmedButEnoughItems) {
            minMax.updateMax(minMax.getMin() + 8);
        }
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
                    note.append("§b").append("Lv. §3").append(minMax.getMin()).append(" §b- §6").append(minMax.getMax());
                } else if (confirmedRange == 8) {
                    note.append("§b").append("Lv. §3").append(minMax.getMin()).append(" §b- §3").append(minMax.getMax());
                } else {
                    note.append("§b").append("Lv. §b").append(minMax.getMin()).append(" §b- §b").append(minMax.getMax());
                }
            }
        }
        Configuration.MythicsAboveChests mythicsAboveChests = getConfiguration().getMythicsAboveChests();
        if (mythicsAboveChests != Configuration.MythicsAboveChests.NONE) {
            if (mythicsAboveChests == Configuration.MythicsAboveChests.MYTHICS_ALL) {
                if (!minMax.isEmpty()) {
                    if (unlevelled) {
                        // let's only display mythics in the players level range.
                        int playerLevel = getPlayerLevel();
                        if (playerLevel > 100) {
                            // unlevelled chests seem to be capped at level 100.
                            playerLevel = 100;
                        }
                        minMax = new MinMax(playerLevel - 4, playerLevel + 4);
                    }
                    Set<Mythic> possibleMythics = new HashSet<>();
                    Set<Mythic> ignoredMythics = new HashSet<>();
                    Map<ItemType, Set<Mythic>> possiblePerType = new HashMap<>();
                    for (Mythic mythic : WynncraftAPI.getMythics()) {
                        if (!mythic.canBeInChest(chestInfo, minMax)) continue;
                        if (!mythic.isEnabled()) {
                            ignoredMythics.add(mythic);
                            continue;
                        }
                        possibleMythics.add(mythic);
                        possiblePerType.computeIfAbsent(mythic.getType(), s -> new HashSet<>()).add(mythic);
                    }
                    if (!(possibleMythics.isEmpty() && ignoredMythics.isEmpty())) {
                        if (note.length() > 0) {
                            note.append("\\n");
                        }
                        boolean first = true;
                        for (Map.Entry<ItemType, Set<Mythic>> entry : possiblePerType.entrySet()) {
                            for (Mythic mythic : entry.getValue()) {
                                String displayName = "§5" + mythic.getDisplayName();
                                displayName = displayName.replace(" ", " &5");
                                note.append(first ? "" : "&7, ").append(displayName);
                                first = false;
                            }
                        }
                        if (!ignoredMythics.isEmpty()) {
                            note.append("\\n&7&oIgnored: &d").append(ignoredMythics.size());
                        }
                    }
                }
            } else {
                // TODO: 12/06/2022 mythics found in this chest
            }
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
