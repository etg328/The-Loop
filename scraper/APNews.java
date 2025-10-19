package com.example.webscraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class APNews {

    /**
     * Scrapes an AP News section page (like world-news, us-news, business, science).
     * For each article card, grabs the headline and link, then fetches
     * the full article page to use as the description.
     *
     * @param listUrl The URL of the AP News section page.
     */
    public static NewsObj[] getNews(String listUrl) throws Exception {
        ArrayList<NewsObj> results = new ArrayList<>();

        System.out.println("Fetching section: " + listUrl);

        // Fetch the section listing page
        Connection.Response listResp = Jsoup.connect(listUrl)
                .userAgent(UA())
                .referrer("https://www.google.com")
                .timeout(25000)
                .followRedirects(true)
                .method(Connection.Method.GET)
                .execute();

        Document doc = listResp.parse();

        // Each <li class="PageList-items-item"> contains an article
        Elements cards = doc.select("li.PageList-items-item h3.PagePromo-title a[href]");
        if (cards.isEmpty()) {
            // fallback selector
            cards = doc.select("li.PageList-items-item a.Link[href]");
        }

        System.out.println("DEBUG: Found " + cards.size() + " article cards in section.");

        for (Element a : cards) {
            String title = textOrNull(a.selectFirst(".PagePromoContentIcons-text"));
            if (isBlank(title)) title = textOrNull(a);
            if (isBlank(title)) continue;

            String href = absHref(a);
            if (isBlank(href)) continue;

            // Fetch the full article text
            String desc = fetchArticleBody(href);

            results.add(new NewsObj(title, desc, href,"AP"));
            Thread.sleep(400); // small delay for politeness
        }

        System.out.println("DEBUG: Built " + results.size() + " NewsObj items from " + listUrl);
        return results.toArray(new NewsObj[0]);
    }

    /** Fetches the full text of a single AP article. */
    private static String fetchArticleBody(String articleUrl) {
        try {
            Document art = Jsoup.connect(articleUrl)
                    .userAgent(UA())
                    .referrer("https://www.google.com")
                    .timeout(25000)
                    .followRedirects(true)
                    .get();

            Elements paragraphs = art.select(
                    "div.RichTextStoryBody p, " +
                    "div[data-test-id=ArticleBody] p, " +
                    "article p, main p"
            );

            StringBuilder sb = new StringBuilder();
            for (Element p : paragraphs) {
                String t = p.text().trim();
                if (t.isEmpty()) continue;
                if (looksLikeBoilerplate(t)) continue;
                sb.append(t).append("\n\n");
            }
            return sb.toString().trim();
        } catch (Exception e) {
            System.err.println("Failed to fetch article text for: " + articleUrl + " — " + e.getMessage());
            return "";
        }
    }

    // ---------- helpers ----------
    private static String UA() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
             + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    }

    private static boolean looksLikeBoilerplate(String t) {
        String s = t.toLowerCase();
        return s.equals("advertisement")
                || s.startsWith("subscribe")
                || s.startsWith("sign up")
                || s.startsWith("read more")
                || s.startsWith("photo:")
                || s.startsWith("credit:");
    }

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
}
