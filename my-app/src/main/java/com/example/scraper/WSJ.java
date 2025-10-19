package com.example.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class WSJ {

    /**
     * Fetches articles from a WSJ RSS feed and returns them as an array of NewsObj.
     * Works for any section feed (World, US, Markets, etc.).
     */
    public static NewsObj[] getNewsFromFeed(String feedUrl) throws Exception {
        var results = new ArrayList<NewsObj>();

        System.out.println("Fetching WSJ feed: " + feedUrl);

        // Fetch the RSS XML and parse it
        Document feed = Jsoup.connect(feedUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .timeout(20000)
                .ignoreContentType(true)
                .parser(Parser.xmlParser()) // XML mode for RSS
                .get();

        // Each <item> is one article
        Elements items = feed.select("rss > channel > item");
        System.out.println("DEBUG: Found " + items.size() + " items in feed");

        for (Element item : items) {
            String title = textOrNull(item.selectFirst("title"));
            String link  = textOrNull(item.selectFirst("link"));
            String desc  = textOrNull(item.selectFirst("description")); // short summary

            if (isBlank(title) || isBlank(link)) continue;

            results.add(new NewsObj(title, nullToEmpty(desc), link, "WSJ"));
        }

        System.out.println("DEBUG: Built " + results.size() + " NewsObj items from " + feedUrl);
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
