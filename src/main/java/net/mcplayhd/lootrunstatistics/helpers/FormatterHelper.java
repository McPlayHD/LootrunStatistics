package net.mcplayhd.lootrunstatistics.helpers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getConfiguration;

public class FormatterHelper {

    public static String getFormatted(int number) {
        Character groupingSeparator = getConfiguration().getGroupingSeparator().getSeparator();
        if (groupingSeparator == null)
            return "" + number;
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(groupingSeparator);
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(number);
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
