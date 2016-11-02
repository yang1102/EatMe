package com.example.jason.liketmreal;

import android.content.Context;
import android.view.View;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jason on 9/16/16.
 */
public class Resource {
    private String  consumerKey;
    private String consumerSecret;
    private String token;
    private String tokenSecret;
    private YelpAPIFactory apiFactory;
    private YelpAPI yelpAPI ;
    private static Resource rs = null;

    public static Resource getInstance(InputStream file) {
        if(rs == null) {
            rs = new Resource(file);
        }
        return rs;
    }

    private static ArrayList<String> getKeyFromFile(InputStream file ){

        BufferedReader r = new BufferedReader(new InputStreamReader(file));
        String line;
        ArrayList<String> myDict = new ArrayList<String>();
        try {
            while ((line=r.readLine()) != null) {
                myDict.add(line.split("\\s+")[1]);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myDict;
    }

    private Resource(InputStream file){
        ArrayList<String> keys = getKeyFromFile(file);
        consumerKey = keys.get(0);
        consumerSecret  = keys.get(1);
        token = keys.get(2);
        tokenSecret = keys.get(3);
        apiFactory = new YelpAPIFactory(consumerKey, consumerSecret, token, tokenSecret);
        yelpAPI = apiFactory.createAPI();
    }


    public YelpAPI getAPI(){
        return this.yelpAPI;
    }

    public String search() throws IOException {

        Map<String, String> params = new HashMap<>();

// general params
        params.put("term", "food");
        params.put("limit", "3");

// locale params
        params.put("lang", "fr");


        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);
        SearchResponse searchResponse = call.execute().body();
        ArrayList<Business> businesses = searchResponse.businesses();
        String businessName = businesses.get(0).name();  // "JapaCurry Truck"
        Double rating = businesses.get(0).rating();  // 4.0
        System.out.println(businessName+rating);

        return businessName+rating;
    }

}