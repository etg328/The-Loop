package com.example;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException;
import software.amazon.awssdk.services.bedrockruntime.model.ThrottlingException;

import com.example.scraper.NewsObj;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummarizeArticle {

    private static final String MODEL_ID = "anthropic.claude-3-5-sonnet-20240620-v1:0";
    private static final int MAX_RETRIES = 3;

    public static ArrayList<String> summarize(ArrayList<NewsObj> allNews) {
        
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> responseSet = new ArrayList<String>();
        
        //Response number
        int responseNum = 0;
        
        
        // if (responseNum == 1) {
        //     break;
        // }
        
        for (int i = 0; i < allNews.size() - 5; i+= 5){    
            System.out.println("Starting at the top of the loop");
            
            // Article 1
            String title1 = (allNews.get(i)).getTitle();
            String desc1 = (allNews.get(i)).getDesc();
            String link1 = (allNews.get(i)).getLink();
            String source1 = (allNews.get(i)).getSource();

            // Article 2
            String title2 = (allNews.get(i+1)).getTitle();
            String desc2 = (allNews.get(i+1)).getDesc();
            String link2 = (allNews.get(i+1)).getLink();
            String source2 = (allNews.get(i+1)).getSource();

            // Article 3
            String title3 = (allNews.get(i+2)).getTitle();
            String desc3 = (allNews.get(i+2)).getDesc();
            String link3 = (allNews.get(i+2)).getLink();
            String source3 = (allNews.get(i+2)).getSource();

            // Article 4
            String title4 = (allNews.get(i+3)).getTitle();
            String desc4 = (allNews.get(i+3)).getDesc();
            String link4 = (allNews.get(i+3)).getLink();
            String source4 = (allNews.get(i+3)).getSource();

            // Article 5
            String title5 = (allNews.get(i+4)).getTitle();
            String desc5 = (allNews.get(i+4)).getDesc();
            String link5 = (allNews.get(i+4)).getLink();
            String source5 = (allNews.get(i+4)).getSource();
            
            //Wait so that AWS does not throttle
            System.out.println("Sleeping");
            try{
                Thread.sleep(56000);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            } 

            System.out.println("Continuing");

            // Build the full text prompt dynamically
            String prompt = String.join("\n",
                "You are a news summarization assistant.",
                "Your task is to condense news content into three short, factual bullet points. Each bullet point should be no longer than 256 characters.",
                "I am going to pass you the following variables for 5 articles. Input each like the format provided below, seperated by an _",
                "Each bullet should highlight the key facts, context, or implications of the article.",
                "Avoid speculation, personal opinion, or repetition.",
                "Write in neutral, journalistic tone.",
                "Output exactly three bullet points — no intro text, no summary paragraph, and no emojis.",
                "First Article",
                "Here is the title: " + title1,
                "Here is the article: " + desc1,
                "Here is the link to the article: " + link1,
                "Here is the source of the article: " + source1,
                "",
                "Second Article",
                "Here is the title: " + title2,
                "Here is the article: " + desc2,
                "Here is the link to the article: " + link2,
                "Here is the source of the article: " + source2,
                "",
                "Third Article",
                "Here is the title: " + title3,
                "Here is the article: " + desc3,
                "Here is the link to the article: " + link3,
                "Here is the source of the article: " + source3,
                "",
                "Fourth Article",
                "Here is the title: " + title4,
                "Here is the article: " + desc4,
                "Here is the link to the article: " + link4,
                "Here is the source of the article: " + source4,
                "",
                "Fifth Article",
                "Here is the title: " + title5,
                "Here is the article: " + desc5,
                "Here is the link to the article: " + link5,
                "Here is the source of the article: " + source5,
                "",
                "Please return the bullets strictly in the following format. Do not return anything other than the format:",
                "[{title: \"_____\", line1: \"_____\", line2: \"_____\", line3: \"_____\", link: \"_____\", source: \"_____\"},",
                "{title: \"_____\", line1: \"_____\", line2: \"_____\", line3: \"_____\", link: \"_____\", source: \"_____\"},",
                "{title: \"_____\", line1: \"_____\", line2: \"_____\", line3: \"_____\", link: \"_____\", source: \"_____\"},",
                "{title: \"_____\", line1: \"_____\", line2: \"_____\", line3: \"_____\", link: \"_____\", source: \"_____\"},",
                "{title: \"_____\", line1: \"_____\", line2: \"_____\", line3: \"_____\", link: \"_____\", source: \"_____\"}]"
            );

        // Build request body
        Map<String, Object> input = new HashMap<>();
        input.put("anthropic_version", "bedrock-2023-05-31");
        input.put("max_tokens", 200000);
        input.put("messages", List.of(
                Map.of(
                        "role", "user",
                        "content", List.of(Map.of("type", "text", "text", prompt))
                )
        ));
        
        System.out.println("Building Prompt");
        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build()) {

            String body = mapper.writeValueAsString(input);

            int attempt = 0;
            while (attempt < MAX_RETRIES) {
                System.out.println("Running while loop");
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
                    System.out.println(outputText);
                    responseSet.add(outputText);
                    responseNum++;
                    break;

                } catch (ThrottlingException e) {
                    attempt++;
                    int delay = (int) Math.pow(2, attempt) * 1000; // exponential backoff
                    System.err.println("Throttled by Bedrock (attempt " + attempt + "), retrying in " + delay + "ms...");
                    Thread.sleep(delay);
                } catch (BedrockRuntimeException e) {
                    System.err.println("Bedrock error: " + e.getMessage());
                    e.printStackTrace();
                    responseSet.add("bedrock runtime error");
                }
            }

            if (attempt == MAX_RETRIES) {
                System.err.println("Failed after " + MAX_RETRIES + " attempts due to throttling.");
            }

        } catch (Exception e) {
            System.err.println("Error invoking model: " + e.getMessage());
            e.printStackTrace();
            responseSet.add("Modle invoke error error");
        }
        
        }
        return responseSet;
    }
}
