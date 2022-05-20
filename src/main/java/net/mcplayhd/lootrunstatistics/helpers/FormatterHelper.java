package net.mcplayhd.lootrunstatistics.helpers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatterHelper {

    // TODO: 20/05/2022 make configurable
    public static DecimalFormat NUMBER_FORMATTER;

    static {
        NUMBER_FORMATTER = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = NUMBER_FORMATTER.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('\'');
        NUMBER_FORMATTER.setDecimalFormatSymbols(symbols);
    }

    public static String getFormatted(int number) {
        return NUMBER_FORMATTER.format(number);
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

}
