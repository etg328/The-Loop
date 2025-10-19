package com.example;

public class Article {
    private String source;
    private String title;
    private String line1;
    private String line2;
    private String line3;
    private String link;

    public Article(String source, String title, String line1, String line2, String line3, String link) {
        this.source = source;
        this.title = title;
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.link = link;
    }
    public String getSource() {
        return source;
    }
    public String getTitle() {
        return title;
    }
    public String getline1() {
        return line1;
    }
    public String getline2() {
        return line2;
    }
    public String getline3() {
        return line3;
    }
    public String getLink() {
        return link;
    }
    @Override
    public String toString() {
        return "Article{" +
                "source='" + source + '\'' +
                ", title='" + title + '\'' +
                ", line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", line3='" + line3 + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
