package com.example;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.core.SdkBytes;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummarizeArticle {

    private static final String MODEL_ID = "anthropic.claude-3-5-sonnet-20240620-v1:0";

    public static void summarize() {
        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build()) {

            ObjectMapper mapper = new ObjectMapper();

            // Proper Claude 3.x Bedrock input structure
            Map<String, Object> input = new HashMap<>();
            input.put("anthropic_version", "bedrock-2023-05-31");
            input.put("max_tokens", 200);
            input.put("messages", List.of(
                    Map.of(
                            "role", "user",
                            "content", List.of(
                                    Map.of("type", "text",
                                            "text", "Summarize the following article in 3 sentences: 'Amazon Bedrock lets developers access foundation models via API.'")
                            )
                    )
            ));

            String body = mapper.writeValueAsString(input);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(MODEL_ID)
                    .body(SdkBytes.fromUtf8String(body))
                    .build();

            InvokeModelResponse response = client.invokeModel(request);

            String responseBody = response.body().asUtf8String();
            System.out.println("Model response:\n" + responseBody);

        } catch (Exception e) {
            System.err.println("Error invoking model: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
