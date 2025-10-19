package com.example.webscraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ESPNNews {

    public static NewsObj[] getNews(String listUrl) throws Exception {
        ArrayList<NewsObj> results = new ArrayList<>();

        System.out.println("Fetching ESPN listing: " + listUrl);

        Connection.Response listResp = Jsoup.connect(listUrl)
                .userAgent(UA())
                .referrer("https://www.google.com")
                .timeout(25000)
                .followRedirects(true)
                .method(Connection.Method.GET)
                .execute();

        Document doc = listResp.parse();

        Elements headlineLinks = doc.select(
                "li[data-story-id] a[data-mptype=headline][href]," +
                "li[data-id] a[data-mptype=headline][href]"
        );
        if (headlineLinks.isEmpty()) {
            headlineLinks = doc.select("a[data-mptype=headline][href], li[data-story-id] a[href]");
        }

        System.out.println("DEBUG: ESPN headlines found = " + headlineLinks.size());

        int numOfObj = 0;
        for (Element a : headlineLinks) {
            if(numOfObj > 3){
                break;
            }
            String title = textOrNull(a);
            if (isBlank(title)) continue;

            String href = absHref(a);
            if (isBlank(href)) continue;
            if (href.startsWith("/")) href = "https://www.espn.com" + href;

            String desc = fetchArticleBody(href); // full article text (your test won’t print it)
            results.add(new NewsObj(title, desc, href, "ESPN"));
            numOfObj++;
            Thread.sleep(350);
        }

        System.out.println("DEBUG: Built " + results.size() + " NewsObj items from ESPN");
        return results.toArray(new NewsObj[0]);
    }

    private static String fetchArticleBody(String articleUrl) {
        try {
            Document art = Jsoup.connect(articleUrl)
                    .userAgent(UA())
                    .referrer("https://www.google.com")
                    .timeout(25000)
                    .followRedirects(true)
                    .get();

            Elements paragraphs = art.select(
                    "article p," +
                    "section[data-name=article-body] p," +
                    "section.article-body p," +
                    "div.article-body p," +
                    "div.Article__Content p," +
                    "main article p"
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
            System.err.println("Failed to fetch ESPN article text for: " + articleUrl + " — " + e.getMessage());
            return "";
        }
    }

    // ---------- helpers ----------
    private static String UA() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    }

    private static boolean looksLikeBoilerplate(String t) {
        String s = t.toLowerCase();
        return s.equals("advertisement") ||
               s.startsWith("subscribe") ||
               s.startsWith("sign up") ||
               s.startsWith("read more") ||
               s.startsWith("credit:") ||
               s.startsWith("photo:");
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
