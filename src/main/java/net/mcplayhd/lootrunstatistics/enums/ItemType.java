package net.mcplayhd.lootrunstatistics.enums;

public enum ItemType {

    HELMET("Helmet"),
    CHESTPLATE("Chestplate"),
    LEGGINGS("Leggings"),
    BOOTS("Boots"),
    BOW("Bow"),
    WAND("Wand"),
    DAGGER("Dagger"),
    SPEAR("Spear"),
    RELIK("Relik"),
    NECKLACE("Necklace"),
    RING("Ring"),
    BRACELET("Bracelet");

    private final String name;

    ItemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
