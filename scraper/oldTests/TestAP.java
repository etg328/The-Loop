package com.example.webscraper;

public class TestAP {
    public static void main(String[] args) {
        // AP section listing pages (feel free to add/remove)
        String[] sections = new String[] {
            "https://apnews.com/hub/world-news",
            "https://apnews.com/hub/us-news",
            "https://apnews.com/hub/business",
            "https://apnews.com/hub/science"
        };

        try {
            for (String listUrl : sections) {
                System.out.println("\n=== AP SECTION: " + listUrl + " ===");

                NewsObj[] items = APNews.getNews(listUrl);
                System.out.println("Total articles collected: " + items.length);

                for (int i = 0; i < items.length; i++) {
                    System.out.println((i + 1) + ". " + items[i].getTitle());
                }
            }
        } catch (Exception e) {
            System.err.println("AP scrape failed: " + e.getMessage());
        }
    }
}
