package net.mcplayhd.lootrunstatistics.helpers;

import net.mcplayhd.lootrunstatistics.LootrunStatistics;
import net.mcplayhd.lootrunstatistics.api.exceptions.APIOfflineException;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.getLogger;

public class VersionHelper {
    private static String newVersion = "unchecked";

    public static String getOnlineVersion() throws APIOfflineException {
        String version = WebsiteHelper.getHttps("https://raw.githubusercontent.com/McPlayHD/LootrunStatistics/master/resources/version.txt");
        if (version == null)
            throw new APIOfflineException("GitHub");
        version = version.replace(System.lineSeparator(), "");
        return version;
    }

    private static int[] getIntegersFillUpBackZeros(String[] strings, int size) {
        int[] numbers = new int[size];
        for (int index = 0; index < strings.length; index++) {
            numbers[index] = Integer.parseInt(strings[index]);
        }
        return numbers;
    }

    public static String getNewVersion() throws APIOfflineException {
        if (!"unchecked".equals(newVersion))
            return newVersion;
        getLogger().info("Checking for new version...");
        String currentVersion = LootrunStatistics.VERSION;
        String onlineVersion = getOnlineVersion();
        String[] currentVersionSplit = currentVersion.replaceAll("[^\\d.]", "").split("\\.");
        String[] onlineVersionSplit = onlineVersion.replaceAll("[^\\d.]", "").split("\\.");
        int maxLength = Math.max(currentVersionSplit.length, onlineVersionSplit.length);
        int[] currentVersionNumbers = getIntegersFillUpBackZeros(currentVersionSplit, maxLength);
        int[] onlineVersionNumbers = getIntegersFillUpBackZeros(onlineVersionSplit, maxLength);
        for (int index = 0; index < maxLength; index++) {
            int current = currentVersionNumbers[index];
            int online = onlineVersionNumbers[index];
            if (online > current) {
                getLogger().info("Found " + onlineVersion + ".");
                newVersion = onlineVersion;
                return onlineVersion;
            }
            if (online < current) {
                getLogger().info("We are ahead! =D");
                newVersion = null;
                return null;
            }
        }
        getLogger().info("No new version found.");
        newVersion = null;
        return null;
    }
}
