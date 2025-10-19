package com.example;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.core.SdkBytes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummarizeArticle {

    private static final String MODEL_ID = "anthropic.claude-3-5-sonnet-20240620-v1:0";

    /**
     * Calls Anthropic Claude 3.5 Sonnet via Amazon Bedrock
     * to summarize a news article into 3 factual bullet points.
     *
     * @param title  The title of the article
     * @param desc   The full text or description of the article
     * @param link   The article's URL
     * @param source The source or publication name
     */
    public static void summarize(String title, String desc, String link, String source) {
        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build()) {

            // Build the full text prompt dynamically
            String prompt = String.join("\n",
                    "You are a news summarization assistant.",
                    "Your task is to condense news content into three short, factual bullet points. Each bullet point should be no longer than 256 characters.",
                    "Each bullet should highlight the key facts, context, or implications of the article.",
                    "Avoid speculation, personal opinion, or repetition.",
                    "Write in neutral, journalistic tone.",
                    "Output exactly three bullet points — no intro text, no summary paragraph, and no emojis.",
                    "",
                    "Here is the title: " + title,
                    "Here is the article: " + desc,
                    "Here is the link to the article: " + link,
                    "Here is the source of the article: " + source,
                    "",
                    "Please return the bullets strictly in the following format. Do not return anything other than the format:",
                    "[{title: _____, line1: _______, line2: _______, line3: _______, link: ________, source: ________}]"
            );

            // Construct proper Claude 3.5 API request
            Map<String, Object> input = new HashMap<>();
            input.put("anthropic_version", "bedrock-2023-05-31");
            input.put("max_tokens", 512);
            input.put("messages", List.of(
                    Map.of(
                            "role", "user",
                            "content", List.of(
                                    Map.of("type", "text", "text", prompt)
                            )
                    )
            ));

            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(input);

            // Send the request to Bedrock
            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(MODEL_ID)
                    .body(SdkBytes.fromUtf8String(body))
                    .build();

            InvokeModelResponse response = client.invokeModel(request);

            // Parse and display the text output from Claude
            String responseBody = response.body().asUtf8String();
            JsonNode root = mapper.readTree(responseBody);

            // Extract the assistant text output
            String outputText = "";
            if (root.has("content") && root.get("content").isArray() && root.get("content").size() > 0) {
                JsonNode first = root.get("content").get(0);
                if (first.has("text")) {
                    outputText = first.get("text").asText();
                }
            }

            System.out.println("Model response:\n" + outputText);

        } catch (Exception e) {
            System.err.println("Error invoking model: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
