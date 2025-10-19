package com.example;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

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

        // Correct prompt structure
        String prompt = """
        System: You are a helpful assistant that summarizes articles in exactly three bullet points.
        "Human: Summarize the following article:

        Article:
        %s
        "
        """.formatted(article);

        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(region)
                .build()) {

            JsonObject payload = new JsonObject();
            payload.addProperty("prompt", prompt);
            payload.addProperty("max_tokens_to_sample", 300);
            payload.addProperty("temperature", 0.3);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .body(SdkBytes.fromUtf8String(payload.toString()))
                    .build();

            InvokeModelResponse response = client.invokeModel(request);
            String responseBody = response.body().asUtf8String();

            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            String summary = json.get("completion").getAsString();

            System.out.println("Summary:");
            System.out.println(summary);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
