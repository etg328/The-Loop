package com.example.webscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class NYT {
    private static final int MAX_PAGES = 2; // scrape up to 10 pages

    public static NewsObj[] getNews(String sectionUrl) throws Exception {
        var results = new ArrayList<NewsObj>();
        int pagesRead = 0;

        for (int page = 1; page <= MAX_PAGES; page++) {
            // First page has no query; subsequent pages use ?page=N (or &page=N if there's already a query)
            String url = (page == 1)
                    ? sectionUrl
                    : sectionUrl + (sectionUrl.contains("?") ? "&page=" + page : "?page=" + page);

            System.out.println("Fetching page: " + url);

            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .referrer("https://www.google.com")
                        .timeout(20000)
                        .get();

                // Typical card container: #stream-panel > div > ol > li
                Elements lis = doc.select("#stream-panel > div > ol > li");
                if (lis.isEmpty()) {
                    System.out.println("No articles found on page " + page + " — stopping this section.");
                    break;
                }

                for (Element li : lis) {
                    Element article = li.selectFirst("article");
                    if (article == null) continue;

                    // Title: headline element within the article
                    Element h = article.selectFirst("a[href] > h3, h3, h2, [role=heading]");
                    String title = textOrNull(h);
                    if (isBlank(title)) continue;

                    // Link: first anchor in the article
                    Element a = article.selectFirst("a[href]");
                    String href = absHref(a);
                    if (isBlank(href)) continue;
                    if (href.startsWith("/")) href = "https://www.nytimes.com" + href;

                    // Blurb/description: the short paragraph next to the headline
                    Element descEl = article.selectFirst("p.css-1pga48a.e15t083i1, p.css-1pga48a");
                    String desc = textOrNull(descEl);

                    results.add(new NewsObj(title, nullToEmpty(desc), href));
                }

                pagesRead++;
                System.out.println("Page " + page + " done. Articles so far (this section): " + results.size());
                Thread.sleep(800); // polite delay
            } catch (Exception e) {
                System.err.println("Error fetching page " + page + ": " + e.getMessage());
                break;
            }
        }

        System.out.println("DEBUG: Read " + pagesRead + " pages for section " + sectionUrl
                + ". Total section articles: " + results.size());
        return results.toArray(new NewsObj[0]);
    }

    // ---------- helpers ----------
    private static String absHref(Element a) {
        if (a == null) return "";
        String abs = trimOrNull(a.absUrl("href"));
        if (!isBlank(abs)) return abs;
        String raw = trimOrNull(a.attr("href"));
        return raw == null ? "" : raw;
    }

    private static String textOrNull(Element el) {
        return el == null ? null : trimOrNull(el.text());
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
