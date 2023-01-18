package net.mcplayhd.lootrunstatistics.helpers;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebsiteHelper {

    public static String getHttps(String url) {
        return getHttps(url, null);
    }

    public static String getHttps(String url, String bearer) {
        HttpsURLConnection conn;
        InputStream stream = null;
        try {
            conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            if (bearer != null) {
                conn.addRequestProperty("Authorization", "Bearer " + bearer);
            }
            // https://stackoverflow.com/a/8329674
            conn.getResponseCode();
            stream = conn.getErrorStream();
            if (stream == null)
                stream = conn.getInputStream();
            return readInputStream(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String postHttps(String url, String content, String bearer) {
        HttpsURLConnection conn;
        InputStream stream = null;
        try {
            conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            if (bearer != null) {
                conn.addRequestProperty("Authorization", "Bearer " + bearer);
            }
            conn.addRequestProperty("Content-type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
            try (OutputStream output = conn.getOutputStream()) {
                byte[] input = content.getBytes(StandardCharsets.UTF_8);
                output.write(input, 0, input.length);
            }
            // https://stackoverflow.com/a/8329674
            conn.getResponseCode();
            stream = conn.getErrorStream();
            if (stream == null)
                stream = conn.getInputStream();
            return readInputStream(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readInputStream(InputStream is) {
        try (InputStreamReader isr = new InputStreamReader(is); BufferedReader br = new BufferedReader(isr)) {
            StringBuilder sb = new StringBuilder();
            for (String inputLine; (inputLine = br.readLine()) != null; )
                sb.append(inputLine).append(System.lineSeparator());
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
