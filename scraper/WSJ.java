package com.example.webscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class WSJ {
    // Use a WSJ RSS feed. Swap this with any WSJ feed you prefer.
    // Examples you can try:
    //  - Latest:   https://feeds.a.dj.com/rss/RSSWSJLatestNews.xml
    //  - World:    https://feeds.a.dj.com/rss/RSSWorldNews.xml
    //  - US:       https://feeds.a.dj.com/rss/RSSUSNews.xml
    //  - Business: https://feeds.a.dj.com/rss/RSSMarketsMain.xml
    private static final String DEFAULT_RSS =
            "https://feeds.a.dj.com/rss/RSSWorldNews.xml";

    /** Scrape via RSS (recommended). */
    public static NewsObj[] getNews() throws Exception {
        return getNewsFromFeed(DEFAULT_RSS);
    }

    /** Scrape from a specific WSJ RSS feed URL. */
    public static NewsObj[] getNewsFromFeed(String feedUrl) throws Exception {
        var results = new ArrayList<NewsObj>();

        // Fetch as XML with Jsoup’s XML parser
        Document feed = Jsoup.connect(feedUrl)
                .userAgent("Mozilla/5.0")
                .timeout(20000)
                .ignoreContentType(true)
                .parser(Parser.xmlParser())
                .get();

        Elements items = feed.select("rss > channel > item");
        for (Element item : items) {
            String title = textOrNull(item.selectFirst("title"));
            String link  = textOrNull(item.selectFirst("link"));
            String desc  = textOrNull(item.selectFirst("description")); // brief summary

            if (isBlank(title) || isBlank(link)) continue;
            results.add(new NewsObj(title, nullToEmpty(desc), link));
        }

        System.out.println("DEBUG: WSJ RSS items collected = " + results.size());
        return results.toArray(new NewsObj[0]);
    }

    // ---------- helpers ----------
    private static String textOrNull(Element el) {
        if (el == null) return null;
        String t = el.text();
        return (t == null || t.trim().isEmpty()) ? null : t.trim();
    }
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    private static String nullToEmpty(String s) {
        return s == null ? "" : s.trim();
    }
}
