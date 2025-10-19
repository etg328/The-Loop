package com.example;

import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ArrayList<Article> list = Database.get("New York Times");
        for (Article article : list) {
            System.out.println(article);
        }
    }
}
