public class NewsObj {
    private final String title;
    private final String desc;
    private final String link;

    public NewsObj(String title, String desc, String link) {
        this.title = title;
        this.desc = desc;
        this.link = link;
    }

    public String getTitle() { return title; }
    public String getDesc()  { return desc; }
    public String getLink()  { return link; }
}
