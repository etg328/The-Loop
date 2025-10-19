package com.example;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import java.util.HashMap;
import com.google.gson.*;
import java.util.Map;

import java.util.*;


public class UploadToDynamo {

    // Simple POJO matching your JSON structure
    static class NewsItem {
        String title;
        String line1;
        String line2;
        String line3;
        String link;
        String source;
    }
    
    public static void upload(String response) {
        
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
            .region(region)
            .build();
        // Parse a single object instead of an array
        Gson gson = new Gson();
        NewsItem[] itemObjs = gson.fromJson(response, NewsItem[].class);
        
        for (NewsItem itemObj : itemObjs) {
            
            String tableName = "news";
            String key = "title";
            String keyVal = itemObj.title;
            String source = "source";
            String sourceVal = itemObj.source;
            String line1 = "line1";
            String line1Val = itemObj.line1;
            String line2 = "line2";
            String line2Val = itemObj.line2;
            String line3 = "line3";
            String line3Val = itemObj.line3;
            String link = "link";
            String linkVal = itemObj.link;
            
            
            putItemInTable(ddb, tableName, key, keyVal, line1, line1Val, line2, line2Val, line3, line3Val, link, linkVal, source, sourceVal);
        }
        ddb.close();
            System.out.println("Done!");
        }
        
        public static void putItemInTable(DynamoDbClient ddb,
        String tableName,
        String key,
        String keyVal,
        String line1,
        String line1Val,
        String line2,
        String line2Val,
        String line3,
        String line3Val,
        String link,
        String linkVal,
        String source,
            String sourceVal) {

        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put(key, AttributeValue.builder().s(keyVal).build());
        itemValues.put(line1, AttributeValue.builder().s(line1Val).build());
        itemValues.put(line2, AttributeValue.builder().s(line2Val).build());
        itemValues.put(line3, AttributeValue.builder().s(line3Val).build());
        itemValues.put(link, AttributeValue.builder().s(linkVal).build());
        itemValues.put(source, AttributeValue.builder().s(sourceVal).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            PutItemResponse response = ddb.putItem(request);
            System.out.println(tableName + " was successfully updated. The request id is "
                    + response.responseMetadata().requestId());

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}