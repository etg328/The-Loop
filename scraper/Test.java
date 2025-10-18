package com.example.webscraper;

public class Test {
    public static void main(String[] args) {
        try {
            NewsObj[] items = NYT.getNews();
            System.out.println("Found " + items.length + " articles.");
            for (NewsObj n : items) {
                System.out.println("Title: " + n.getTitle());
                System.out.println("Desc : " + n.getDesc());
                System.out.println("Link : " + n.getLink());
                System.out.println("-----");
            }
        } catch (Exception e) {
            System.err.println("Error scraping NYT: " + e.getMessage());
        }
    }
}
