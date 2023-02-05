package net.mcplayhd.lootrunstatistics.helpers;

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileHelper {

    public static String readFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    public static void writeFile(File file, String content) throws IOException {
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        if (!file.exists())
            file.createNewFile();
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }

    public static void backupFile(File file) throws IOException {
        File backupFile = new File(file.getParentFile().getPath() + "/." + file.getName() + ".backup");
        if (backupFile.exists()) {
            backupFile.delete();
        }
        FileUtils.copyFile(file, backupFile);
    }
}
