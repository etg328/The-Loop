package com.example;

import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        QueryResponse response = Database.get("New York Times");
        System.out.print(response.items());
    }
}
