package com.example.scraper;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class runScraper {
    public static ArrayList<NewsObj> run() {
        // --- Configure sections/feeds for each source ---
        String[] nytSections = {
            "https://www.nytimes.com/section/us",
            "https://www.nytimes.com/section/world",
            "https://www.nytimes.com/section/business"
        };

        String[] apSections = {
            "https://apnews.com/hub/world-news",
            "https://apnews.com/hub/us-news",
            "https://apnews.com/hub/business",
            "https://apnews.com/hub/science"
        };

        String[] espnSections = {
            "https://www.espn.com/",
            "https://www.espn.com/nfl/",
            "https://www.espn.com/nba/",
            "https://www.espn.com/mlb/"
        };

        String[] wsjFeeds = {
            "https://feeds.a.dj.com/rss/RSSWorldNews.xml",
            "https://feeds.a.dj.com/rss/RSSUSNews.xml",
            "https://feeds.a.dj.com/rss/RSSMarketsMain.xml"
        };

        // Big bag for everything
        ArrayList<NewsObj> allNews = new ArrayList<>();

        // ---- NYT ----
        try {
            System.out.println("\n=== NYT ===");
            for (String sectionUrl : nytSections) {
                System.out.println("NYT section: " + sectionUrl);
                NewsObj[] items = NYT.getNews(sectionUrl);
                addAll(allNews, items);
                System.out.println("Added: " + items.length + " (running total: " + allNews.size() + ")");
            }
        } catch (Exception e) {
            System.err.println("NYT scrape failed: " + e.getMessage());
        }

        // ---- AP ----
        try {
            System.out.println("\n=== AP ===");
            for (String listUrl : apSections) {
                System.out.println("AP section: " + listUrl);
                NewsObj[] items = APNews.getNews(listUrl);
                addAll(allNews, items);
                System.out.println("Added: " + items.length + " (running total: " + allNews.size() + ")");
            }
        } catch (Exception e) {
            System.err.println("AP scrape failed: " + e.getMessage());
        }

        // ---- ESPN ----
        try {
            System.out.println("\n=== ESPN ===");
            for (String listUrl : espnSections) {
                System.out.println("ESPN section: " + listUrl);
                NewsObj[] items = ESPNNews.getNews(listUrl);
                addAll(allNews, items);
                System.out.println("Added: " + items.length + " (running total: " + allNews.size() + ")");
            }
        } catch (Exception e) {
            System.err.println("ESPN scrape failed: " + e.getMessage());
        }

        // ---- WSJ (RSS) ----
        try {
            System.out.println("\n=== WSJ ===");
            for (String feedUrl : wsjFeeds) {
                System.out.println("WSJ feed: " + feedUrl);
                NewsObj[] items = WSJ.getNewsFromFeed(feedUrl);
                addAll(allNews, items);
                System.out.println("Added: " + items.length + " (running total: " + allNews.size() + ")");
            }
        } catch (Exception e) {
            System.err.println("WSJ scrape failed: " + e.getMessage());
        }

        // Summary before cleanup
        System.out.println("\nCollected (raw): " + allNews.size());

        // --- Filter: remove any objects that have *any* null field (title/desc/link) ---
        ArrayList<NewsObj> cleaned = new ArrayList<>();
        for (NewsObj n : allNews) {
            if (n == null) continue;
            if (n.getTitle() == null) continue;
            if (n.getDesc()  == null) continue;
            if (n.getLink()  == null) continue;
            cleaned.add(n);
        }

        System.out.println("After null-check cleanup: " + cleaned.size());

        // --- Write only the titles (one per line) to a file ---
        Path outPath = Paths.get("all_titles.txt");
        try (BufferedWriter w = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8)) {
            for (NewsObj n : cleaned) {
                w.write(n.getTitle());
                w.newLine();
            }
        } catch (Exception e) {
            System.err.println("Failed writing titles file: " + e.getMessage());
        }

        System.out.println("\nDone. Wrote titles to: " + outPath.toAbsolutePath());
        return allNews;
    }

    private static void addAll(ArrayList<NewsObj> dest, NewsObj[] src) {
        if (src == null) return;
        for (NewsObj n : src) dest.add(n);
    }
}
