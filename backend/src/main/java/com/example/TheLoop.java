package com.example;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

public class TheLoop {

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";

            // Enable CORS
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.allowHost("http://localhost:8080"); // frontend origin
                });
            });
        }).start("0.0.0.0", 8081);

        // GET route: send list of articles
        app.get("/api/articles", TheLoop::getArticles);

        System.out.println("The Loop backend running at http://localhost:8080");
        System.out.println("The Loop articles are at http://localhost:8080/api/articles");
    }

    private static void getArticles(Context ctx) {
        try {
            // Read query parameter 'sources' from URL (e.g., ?sources=CNN,BBC)
            String sourcesParam = ctx.queryParam("sources");
            String[] sources = sourcesParam.isEmpty() ? new String[0] : sourcesParam.split(",");

            // Call your database function
            ArrayList<Article> articles = Database.get(sources);

            // Return JSON
            ctx.json(articles);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}
