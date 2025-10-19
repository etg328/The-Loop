package com.example;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SummarizeArticle {
    public static void main(String[] args) {

        String article = """
            The Federal Reserve announced a surprise rate cut today,
            lowering the benchmark interest rate by 0.5%. The decision
            comes amid slowing job growth and cooling inflation data.
            Markets reacted positively, with the S&P 500 rising nearly 2%
            after the announcement.
        """;

        Region region = Region.US_EAST_1;
        String modelId = "anthropic.claude-3-sonnet-20240229-v1:0";

        // Top-level system prompt
        JsonObject payload = new JsonObject();

        // Top-level system prompt
        payload.addProperty("system", "You are a helpful assistant that summarizes news articles in exactly three bullet points.");

        // Anthropic version
        payload.addProperty("anthropic_version", "2023-10-01");

        // Messages array (user messages only)
        JsonArray messages = new JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", "Summarize the following article:\n\n" + article);
        messages.add(userMessage);

        payload.add("messages", messages);
        payload.addProperty("max_tokens", 300);
        payload.addProperty("temperature", 0.3);

        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(region)
                .build()) {

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .body(SdkBytes.fromUtf8String(payload.toString()))
                    .build();

            InvokeModelResponse response = client.invokeModel(request);
            String responseBody = response.body().asUtf8String();

            // The response structure may differ depending on the model
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray outputMessages = json.getAsJsonArray("messages");
            if (outputMessages != null && outputMessages.size() > 0) {
                String summary = outputMessages.get(0).getAsJsonObject().get("content").getAsString();
                System.out.println("Summary:");
                System.out.println(summary);
            } else {
                System.out.println("No output returned from model.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
