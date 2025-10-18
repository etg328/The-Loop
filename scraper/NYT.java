package com.example.webscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * NYT scraper – fetches the NYTimes.com homepage and returns an array of newsObj.
 * Each newsObj contains the title, the full text scraped from the linked article, and the article URL.
 */
public class NYT {
    private static final String BASE_URL = "https://www.nytimes.com/";

    // ====== SELECTORS ======
    // You can narrow this later if you want specific sections
    private static final String ARTICLE_CARD_SEL = "article"; 

    private static final String TITLE_SEL = "#site-content > div > div > div > div:nth-child(1) > div:nth-child(1) > div.css-1p2nb0z.e1ppw5w20 > div:nth-child(1) > div:nth-child(2) > div.story-wrapper.css-a4nlbf > div > div:nth-child(2) > div.css-1wzkfo3 > div.css-cfnhvx > a > div > p";
    private static final String LINK_SEL  = "#site-content > div > div > div > div:nth-child(1) > div:nth-child(1) > div.css-1p2nb0z.e1ppw5w20 > div:nth-child(1) > div:nth-child(2) > div.story-wrapper.css-a4nlbf > div > div:nth-child(2) > div.css-1wzkfo3 > div.css-cfnhvx > a";
    // ========================

    /**
     * Fetches the NYT homepage and returns an array of newsObj.
     * Each object's description is scraped from the linked article page.
     */
    public static NewsObj[] getNews() throws Exception {
        List<NewsObj> results = new ArrayList<>();

        Document doc = Jsoup.connect(BASE_URL)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        Elements cards = doc.select(ARTICLE_CARD_SEL);

        for (Element card : cards) {
            String title = textOrNull(card.selectFirst(TITLE_SEL));
            String href  = linkOrNull(card.selectFirst(LINK_SEL));

            // Skip if missing critical info
            if (isBlank(title) || isBlank(href)) continue;

            // Fetch article page and extract full text
            String desc = fetchArticleText(href);

            results.add(new NewsObj(title, desc, href));

            // polite delay between requests to avoid overwhelming NYT
            Thread.sleep(1000);
        }

        return results.toArray(new NewsObj[0]);
    }

    /**
     * Connects to an individual NYT article and returns the combined text content.
     * You can later adjust the selector for paragraphs or body sections.
     */
    private static String fetchArticleText(String url) {
        try {
            Document article = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            // Basic paragraph selector (NYT uses <p> heavily for article content)
            Elements paragraphs = article.select("section[name='articleBody'] p, article p");
            StringBuilder sb = new StringBuilder();

            for (Element p : paragraphs) {
                String text = p.text().trim();
                if (!text.isEmpty()) sb.append(text).append(" ");
            }

            return sb.toString().trim();
        } catch (Exception e) {
            System.err.println("Failed to fetch article text for: " + url + " — " + e.getMessage());
            return "";
        }
    }

    // ---------- helpers ----------
    private static String textOrNull(Element el) {
        return el == null ? null : trimOrNull(el.text());
    }

    private static String linkOrNull(Element linkEl) {
        if (linkEl == null) return null;

        String url = trimOrNull(linkEl.absUrl("href"));
        if (!isBlank(url)) return url;

        String raw = trimOrNull(linkEl.attr("href"));
        if (isBlank(raw)) return null;

        if (raw.startsWith("/")) return "https://www.nytimes.com" + raw;
        return raw;
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
