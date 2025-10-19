package com.example;

public class App {
    public static void main(String[] args) {
        System.out.println("Calling Claude 3.5 Sonnet model via Amazon Bedrock...");
        SummarizeArticle.summarize();
    }
}