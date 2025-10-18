package com.example.webscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NYT {
    //https://www.nytimes.com/section/us
    //https://www.nytimes.com/

    private static final String BASE_URL = "https://www.nytimes.com/section/us";

    public static NewsObj[] getNews() throws Exception {
        List<NewsObj> results = new ArrayList<>();

        Document doc = Jsoup.connect(BASE_URL)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                         + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .referrer("https://www.google.com")
                .timeout(20000)
                .get();

        // Pass 1: anchors that wrap an h3/h2 (most robust)
        Elements anchors = doc.select(
                "#site-content a[href]:has(h3), #site-content a[href]:has(h2)"
        );
        System.out.println("DEBUG: anchors (has h3/h2) = " + anchors.size());

        // Fallback: headline tags with a child link
        if (anchors.isEmpty()) {
            anchors = doc.select("#site-content h3 a[href], #site-content h2 a[href]");
            System.out.println("DEBUG: fallback anchors (h3 a / h2 a) = " + anchors.size());
        }

        // Last-resort: any headline-looking link in main
        if (anchors.isEmpty()) {
            anchors = doc.select("main a[href]:has(h3), main a[href]:has(h2), main h3 a[href], main h2 a[href]");
            System.out.println("DEBUG: last-resort anchors (main) = " + anchors.size());
        }

        // Deduplicate by absolute URL while preserving order
        Set<String> seen = new LinkedHashSet<>();

        for (Element a : anchors) {
            // Title from nearest h3/h2 text, else the link text
            String title = textOrNull(a.selectFirst("h3, h2"));
            if (isBlank(title)) title = textOrNull(a);

            String href = absHref(a);
            if (isBlank(title) || isBlank(href)) continue;

            // Normalize relative
            if (href.startsWith("/")) href = "https://www.nytimes.com" + href;

            // Basic filtering: skip nav/utility links
            if (href.contains("#") || href.endsWith(".jpg") || href.endsWith(".png")) continue;

            // Only keep each URL once
            if (!seen.add(href)) continue;

            // Pull article body as description (can be long)
            String desc = fetchArticleText(href);

            results.add(new NewsObj(title, desc, href));

            // Be polite
            Thread.sleep(400);
        }

        System.out.println("DEBUG: Built " + results.size() + " NewsObj items");
        return results.toArray(new NewsObj[0]);
    }

    private static String fetchArticleText(String url) {
    try {
        Document article = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .referrer("https://www.google.com")
                .timeout(25000)
                .get();

        // 1) Your described structure: companion columns → container → paragraphs
        //    Example: [data-testid="companionColumn-0"] div.css-53u6y8 p.css-ac37hb.evys1bk0
        Elements paragraphs = article.select(
                "div[data-testid^=companionColumn-] div.css-53u6y8 p.css-ac37hb.evys1bk0"
        );

        // 2) Fallbacks for other common NYT article bodies
        if (paragraphs.isEmpty()) {
            paragraphs = article.select(
                // older/newer articleBody names
                "[data-testid=article-body] p, " +
                "section[name=articleBody] p, " +
                "div[name=articleBody] p, " +
                // generic article/main paragraph fallbacks
                "article p, main p"
            );
        }

        // 3) Final sanity fallback: any <p> within #site-content that looks like body text
        if (paragraphs.isEmpty()) {
            paragraphs = article.select("#site-content p");
        }

        StringBuilder sb = new StringBuilder();
        for (Element p : paragraphs) {
            String text = p.text().trim();
            if (text.isEmpty()) continue;
            if (looksLikeBoilerplate(text)) continue;   // skip subscribe promos, “Advertisement”, etc.
            sb.append(text).append("\n\n");
        }

        return sb.toString().trim();
    } catch (Exception e) {
        System.err.println("Failed to fetch article text for: " + url + " — " + e.getMessage());
        return "";
    }
}

// Skip obvious non-body lines
private static boolean looksLikeBoilerplate(String t) {
    String s = t.toLowerCase();
    return s.equals("advertisement")
        || s.startsWith("subscribe")
        || s.startsWith("sign up")
        || s.startsWith("read more")
        || s.startsWith("listen")
        || s.startsWith("supported by")
        || s.startsWith("credit:");
}
    private static String absHref(Element a) {
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
