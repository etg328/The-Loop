package com.example.webscraper;

public class TestESPN {
    public static void main(String[] args) {
        // Sections to scrape
        String[] sections = new String[] {
            "https://www.espn.com/",
            "https://www.espn.com/nfl/",
            "https://www.espn.com/nba/",
            "https://www.espn.com/mlb/"
        };

        try {
            for (String listUrl : sections) {
                System.out.println("\n=== ESPN SECTION: " + listUrl + " ===");

                NewsObj[] items = ESPNNews.getNews(listUrl);
                System.out.println("Total items: " + items.length);

                // Print titles only
                for (int i = 0; i < items.length; i++) {
                    System.out.println((i + 1) + ". " + items[i].getTitle());
                }
            }

            System.out.println("\n=== DONE ===");
        } catch (Exception e) {
            System.err.println("ESPN scrape failed: " + e.getMessage());
        }
    }
}
