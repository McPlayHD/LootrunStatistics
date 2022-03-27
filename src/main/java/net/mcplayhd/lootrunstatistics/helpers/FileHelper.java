package net.mcplayhd.lootrunstatistics.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class FileHelper {

    public static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            stream.forEach(s -> sb.append(s).append("\n"));
        }
        return sb.toString();
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

}