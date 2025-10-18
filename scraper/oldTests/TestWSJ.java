package com.example.webscraper;

import java.util.ArrayList;

public class TestWSJ {
    public static void main(String[] args) {
        // WSJ section RSS feeds
        String[] feeds = {
            "https://feeds.a.dj.com/rss/RSSWorldNews.xml",   // World
            "https://feeds.a.dj.com/rss/RSSUSNews.xml",      // US
            "https://feeds.a.dj.com/rss/RSSMarketsMain.xml"  // Markets / Business
        };

        ArrayList<NewsObj> allNews = new ArrayList<>();

        try {
            for (String feedUrl : feeds) {
                System.out.println("\n=== WSJ FEED: " + feedUrl + " ===");

                // Scrape feed
                NewsObj[] items = WSJ.getNewsFromFeed(feedUrl);
                System.out.println("Articles in this feed: " + items.length);

                // Add to overall list
                for (NewsObj n : items) allNews.add(n);

                // Show top 3 from this feed
                int show = Math.min(items.length, 3);
                for (int i = 0; i < show; i++) {
                    NewsObj n = items[i];
                    System.out.println("\n[" + (i + 1) + "] " + n.getTitle());
                    System.out.println("Desc: " + n.getDesc());
                    System.out.println("Link: " + n.getLink());
                }
            }

            System.out.println("\n=== SUMMARY ===");
            System.out.println("Total WSJ sections scraped: " + feeds.length);
            System.out.println("Total articles collected: " + allNews.size());

        } catch (Exception e) {
            System.err.println("WSJ RSS scrape failed: " + e.getMessage());
        }
    }
}
