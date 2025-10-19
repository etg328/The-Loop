package com.example;

import com.example.scraper.runScraper;
import java.util.ArrayList;
import java.lang.Thread;

import com.example.scraper.NewsObj;

public class App {
    public static void main(String[] args) {
        ArrayList<NewsObj> allNews = new ArrayList<>();
        
        allNews = runScraper.run();
        System.out.println("Calling Claude 3.5 Sonnet model via Amazon Bedrock...");
        for (int i = 0; i < allNews.size(); i++){


            String title = (allNews.get(i)).getTitle();
            String desc = (allNews.get(i)).getDesc();
            String link = (allNews.get(i)).getLink();
            String source = (allNews.get(i)).getSource();
            
            SummarizeArticle.summarize(title, desc, link, source);
            try{
                Thread.sleep(20000);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}