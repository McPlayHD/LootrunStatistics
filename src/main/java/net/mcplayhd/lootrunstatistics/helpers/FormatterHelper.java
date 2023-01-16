package net.mcplayhd.lootrunstatistics.helpers;

import net.mcplayhd.lootrunstatistics.configuration.utils.GroupingSeparator;
import net.minecraft.util.text.TextComponentString;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getConfiguration;

public class FormatterHelper {

    public static TextComponentString formatString(String string) {
        return new TextComponentString(string.replace("§", "\u00a7"));
    }

    public static String getFormatted(long number) {
        GroupingSeparator groupingSeparator = getConfiguration().getGroupingSeparator();
        if (groupingSeparator == null || groupingSeparator == GroupingSeparator.NONE) {
            return Long.toString(number);
        }
        char[] numberChars = Long.toString(number).toCharArray();
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < numberChars.length; index++) {
            if (index > 0 && index % 3 == 0) {
                result.insert(0, groupingSeparator.getSeparator());
            }
            result.insert(0, numberChars[numberChars.length - 1 - index]);
        }
        return result.toString();
    }

    public static String getFormattedDry(int dry) {
        String formatted = getFormatted(dry);
        if (dry < 750)
            return "§2" + formatted;
        if (dry < 1500)
            return "§a" + formatted;
        if (dry < 2250)
            return "§e" + formatted;
        if (dry < 3000)
            return "§c" + formatted;
        return "§4" + formatted;
    }

    public static int convertRomanToArabic(String roman) {
        switch (roman) {
            case "I":
                return 1;
            case "II":
                return 2;
            case "III":
                return 3;
            case "IV":
                return 4;
            case "V":
                return 5;
            case "VI":
                return 6;
            case "VII":
                return 7;
            case "VIII":
                return 8;
            case "IX":
                return 9;
            case "X":
                return 10;
        }
        throw new RuntimeException("Unsupported roman number.");
    }
}
