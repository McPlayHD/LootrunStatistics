package net.mcplayhd.lootrunstatistics.utils;

public enum Region {
    RODOROC("rodo"),
    SKY_ISLANDS("sky"),
    VOID("v"),
    MOON("moon"),
    SILENT_EXPANSE("se"),
    CORKUS("cork"),
    CANYON_OF_THE_LOST("cotl"),
    DARK_FORST("df"),
    KANDER_FOREST("kf"),
    LIGHT_FOREST("lf"),
    REALM_OF_LIGHT("rol"),
    TROMS("t"),
    DERNEL_JUNGLE("d");

    private final String abbreviation;

    Region(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public static Region fromString(String name) {
        for (Region region : values()) {
            if (region.name().equalsIgnoreCase(name) || region.getAbbreviation().equalsIgnoreCase(name)) {
                return region;
            }
        }
        return null;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
