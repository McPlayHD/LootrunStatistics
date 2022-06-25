package net.mcplayhd.lootrunstatistics.enums;

public enum Tier {
    NORMAL("§7Normal"),
    UNIQUE("§eUnique"),
    RARE("§dRare"),
    SET("§aSet"),
    LEGENDARY("§bLegendary"),
    FABLED("§cFabled"),
    MYTHIC("§5Mythic");

    final String displayName;

    Tier(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
