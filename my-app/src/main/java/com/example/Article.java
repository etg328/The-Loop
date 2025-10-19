package com.example;

public class Article {
    private String source;
    private String title;
    private String text;
    private String link;

    public Article(String source, String title, String text, String link) {
        this.source = source;
        this.title = title;
        this.text = text;
        this.link = link;
    }
    public String getSource() {
        return source;
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
    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
