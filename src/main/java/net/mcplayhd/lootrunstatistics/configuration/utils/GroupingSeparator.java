package net.mcplayhd.lootrunstatistics.configuration.utils;

public enum GroupingSeparator {
    NONE("<None>", null),
    SPACE("<Space>", ' '),
    APOSTROPHE("'", '\''),
    COMMA(",", ','),
    ;
    final String description;
    final Character separator;

    GroupingSeparator(String description, Character separator) {
        this.description = description;
        this.separator = separator;
    }

    public String getDescription() {
        return description;
    }

    public Character getSeparator() {
        return separator;
    }
}