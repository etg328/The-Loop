package com.example.webscraper;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        String[] nytSections = {
            "https://www.nytimes.com/section/us",
            "https://www.nytimes.com/section/world",
            "https://www.nytimes.com/section/business"  // fixed spelling
        };

        ArrayList<NewsObj> allNews = new ArrayList<>();
        int totalPages = 0;

        try {
            for (String sectionUrl : nytSections) {
                System.out.println("\n=== SECTION: " + sectionUrl + " ===");
                NewsObj[] news = NYT.getNews(sectionUrl);

                // count pages from the log is fine, but we’ll show per-section article count here
                System.out.println("Section articles: " + news.length);

                // accumulate
                for (NewsObj obj : news) allNews.add(obj);

                // crude pages counter (NYT.getNews prints exact count; here we estimate by non-empty result)
                // If you want exact pages, you could return a small wrapper object instead of just NewsObj[]
                totalPages += 1; // counts the section as processed; or remove this if you prefer the NYT logs only
            }

            System.out.println("\n=== SCRAPE COMPLETE ===");
            System.out.println("Sections processed: " + nytSections.length);
            System.out.println("Total articles collected: " + allNews.size());

            // Print a few items as a sanity check
            int show = Math.min(allNews.size(), 10);
            for (int i = 0; i < show; i++) {
                NewsObj n = allNews.get(i);
                System.out.println("\n[" + (i + 1) + "] " + n.getTitle());
                System.out.println("Desc: " + n.getDesc());
                System.out.println("Link: " + n.getLink());
            }

        } catch (Exception e) {
            System.err.println("Scraping failed: " + e.getMessage());
        }
    }
}
