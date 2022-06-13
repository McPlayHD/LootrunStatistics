package net.mcplayhd.lootrunstatistics.enums;

import net.minecraft.item.Item;

public enum ArmorType {
    LEATHER("leather"),
    GOLDEN("golden"),
    CHAIN("chainmail"),
    IRON("iron"),
    DIAMOND("diamond"),
    ;

    private final String registryName;

    ArmorType(String registryName) {
        this.registryName = registryName;
    }

    public Item getItem(ItemType itemType) {
        return Item.getByNameOrId(registryName + "_" + itemType.name().toLowerCase());
    }
}
