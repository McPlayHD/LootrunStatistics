package net.mcplayhd.lootrunstatistics.api;

import com.google.gson.Gson;
import net.mcplayhd.lootrunstatistics.api.utils.ChestReport;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.MODID;
import static net.mcplayhd.lootrunstatistics.helpers.WebsiteHelper.getHttps;
import static net.mcplayhd.lootrunstatistics.helpers.WebsiteHelper.postHttps;

public class WynncraftChestsAPI {
    private static final ExecutorService reporter = Executors.newSingleThreadExecutor();
    private static String apiToken;

    public static String getEndpoint(String endpoint) {
        return getHttps("https://backup.mcplayhd.net/wynncraftchests" + endpoint, getApiToken());
    }

    public static String postEndpoint(String endpoint, String json) {
        return postHttps("https://backup.mcplayhd.net/wynncraftchests" + endpoint, json, getApiToken());
    }

    public static void reportChest(ChestReport chestReport) {
        if (getApiToken() == null) {
            return;
        }
        reporter.submit(() -> {
            try {
                String json = postEndpoint("/chests/report", new Gson().toJson(chestReport));
                System.out.println(json); // TODO: 17/01/2023 whatever
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private static String getApiToken() {
        if (apiToken == null) {
            File file = new File(MODID + "/wynncraftchestsapi.token");
            if (file.exists()) {
                try {
                    apiToken = FileHelper.readFile(file).replaceAll("[^A-Za-z0-9\\-]", "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return apiToken;
    }
}
