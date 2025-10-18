package com.example;

public class Article {
    private String title;
    private String text;
    private String link;

    public Article(String title, String text, String link) {
        this.title = title;
        this.text = text;
        this.link = link;
    }
    public String getTitle() {
        return title;
    }
    public String getText() {
        return text;
    }
    public String getLink() {
        return link;
    }
}
