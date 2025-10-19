package com.example;

import com.example.scraper.runScraper;
import java.util.ArrayList;

import com.example.scraper.NewsObj;

public class App {
    public static void main(String[] args) {
        ArrayList<NewsObj> allNews = new ArrayList<>();
        
        runScraper.run();
        System.out.println("Calling Claude 3.5 Sonnet model via Amazon Bedrock...");
        SummarizeArticle.summarize();
    }
}