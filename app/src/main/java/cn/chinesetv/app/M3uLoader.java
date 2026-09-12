package cn.chinesetv.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class M3uLoader {
    private static final int TIMEOUT_MS = 15000;
    private static final Pattern TVG_ID_PATTERN = Pattern.compile("tvg-id=\"([^\"]+)\"");

    private M3uLoader() {
    }

    static List<Channel> load(String playlistUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(playlistUrl).openConnection();
        connection.setConnectTimeout(TIMEOUT_MS);
        connection.setReadTimeout(TIMEOUT_MS);
        connection.setRequestProperty("User-Agent", "ChineseTV/1.0");
        connection.setInstanceFollowRedirects(true);

        InputStream input = null;
        try {
            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                throw new IOException("HTTP " + responseCode);
            }
            input = connection.getInputStream();
            return parse(input);
        } finally {
            if (input != null) {
                input.close();
            }
            connection.disconnect();
        }
    }

    static List<Channel> fallbackChannels() {
        List<Channel> channels = new ArrayList<Channel>();
        channels.add(new Channel("CCTV-1 综合", "http://74.91.26.218:82/live/cctv1hd.m3u8"));
        channels.add(new Channel("CCTV-13 新闻", "http://74.91.26.218:82/live/cctv13hd.m3u8"));
        channels.add(new Channel("浙江卫视", "http://ali-m-l.cztv.com/channels/lantian/channel001/1080p.m3u8"));
        channels.add(new Channel("湖南卫视", "http://74.91.26.218:82/live/hnwshd.m3u8"));
        return channels;
    }

    private static List<Channel> parse(InputStream input) throws IOException {
        List<Channel> channels = new ArrayList<Channel>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        String pendingName = null;
        String pendingId = null;
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#EXTINF:")) {
                int comma = line.lastIndexOf(',');
                pendingName = comma >= 0 ? line.substring(comma + 1).trim() : "未命名频道";
                Matcher idMatcher = TVG_ID_PATTERN.matcher(line);
                pendingId = idMatcher.find() ? idMatcher.group(1) : null;
            } else if (pendingName != null && (line.startsWith("http://") || line.startsWith("https://"))) {
                channels.add(new Channel(ChannelNameTranslator.translate(pendingId, pendingName), line));
                pendingName = null;
                pendingId = null;
            }
        }
        if (channels.isEmpty()) {
            throw new IOException("No channels in playlist");
        }
        return channels;
    }
}
