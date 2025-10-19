package com.example.scraper;
public class NewsObj {
    private final String title;
    private final String desc;
    private final String link;
    private final String source;

    public NewsObj(String title, String desc, String link, String source) {
        this.title = title;
        this.desc = desc;
        this.link = link;
        this.source = source;
    }

    public String getTitle() { return title; }
    public String getDesc()  { return desc; }
    public String getLink()  { return link; }
    public String getSource() {return source; }
}
