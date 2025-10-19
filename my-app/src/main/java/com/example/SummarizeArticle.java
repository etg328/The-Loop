package com.example;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException;
import software.amazon.awssdk.services.bedrockruntime.model.ThrottlingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummarizeArticle {

    private static final String MODEL_ID = "anthropic.claude-3-5-sonnet-20240620-v1:0";
    private static final int MAX_RETRIES = 3;

    public static String summarize(String title, String desc, String link, String source) {
        ObjectMapper mapper = new ObjectMapper();

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

        // Build request body
        Map<String, Object> input = new HashMap<>();
        input.put("anthropic_version", "bedrock-2023-05-31");
        input.put("max_tokens", 512);
        input.put("messages", List.of(
                Map.of(
                        "role", "user",
                        "content", List.of(Map.of("type", "text", "text", prompt))
                )
        ));

        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build()) {

            String body = mapper.writeValueAsString(input);

            int attempt = 0;
            while (attempt < MAX_RETRIES) {
                try {
                    InvokeModelRequest request = InvokeModelRequest.builder()
                            .modelId(MODEL_ID)
                            .body(SdkBytes.fromUtf8String(body))
                            .build();

                    InvokeModelResponse response = client.invokeModel(request);
                    String responseBody = response.body().asUtf8String();

                    // Parse model response
                    JsonNode root = mapper.readTree(responseBody);
                    String outputText = "";
                    if (root.has("content") && root.get("content").isArray() && root.get("content").size() > 0) {
                        JsonNode first = root.get("content").get(0);
                        if (first.has("text")) {
                            outputText = first.get("text").asText();
                        }
                    }

                    return(outputText);

                } catch (ThrottlingException e) {
                    attempt++;
                    int delay = (int) Math.pow(2, attempt) * 1000; // exponential backoff
                    System.err.println("Throttled by Bedrock (attempt " + attempt + "), retrying in " + delay + "ms...");
                    Thread.sleep(delay);
                } catch (BedrockRuntimeException e) {
                    System.err.println("Bedrock error: " + e.getMessage());
                    e.printStackTrace();
                    return ("error");
                }
            }

            if (attempt == MAX_RETRIES) {
                System.err.println("Failed after " + MAX_RETRIES + " attempts due to throttling.");
            }

        } catch (Exception e) {
            System.err.println("Error invoking model: " + e.getMessage());
            e.printStackTrace();
            return("error");
        }
        return ("error");
    }
}
