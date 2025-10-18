package com.example.webscraper;

public class TestWSJ {
    public static void main(String[] args) {
        try {
            // Option A: default “Latest” feed
            NewsObj[] latest = WSJ.getNews();
            System.out.println("Latest feed count: " + latest.length);

            // Option B: pick a specific section feed
            String worldFeed = "https://feeds.a.dj.com/rss/RSSWorldNews.xml";
            NewsObj[] world = WSJ.getNewsFromFeed(worldFeed);
            System.out.println("World feed count: " + world.length);

            // Print a few
            int show = Math.min(latest.length, 5);
            for (int i = 0; i < show; i++) {
                var n = latest[i];
                System.out.println("\n[" + (i+1) + "] " + n.getTitle());
                System.out.println("Desc: " + n.getDesc());
                System.out.println("Link: " + n.getLink());
            }
        } catch (Exception e) {
            System.err.println("WSJ RSS scrape failed: " + e.getMessage());
        }
    }
}
