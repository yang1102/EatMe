package com.example.jason.liketmreal;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Location;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

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

    public ArrayList<Business> search(String ratingParam,String foodParam, String distanceParam,String currentLocation) throws IOException {
        ArrayList<Business> businesses;
        Map<String, String> params = new HashMap<>();
        //term param should always be "food"
        params.put("term", "food");
        params.put("limit","20");
        if(foodParam!="")
            params.put("category_filter", foodParam);
        if(distanceParam!="")
            params.put("radius_filter", distanceParam);

        double latitude = Double.parseDouble(currentLocation.split(",")[0]);
        double longitude =  Double.parseDouble(currentLocation.split(",")[1]);

        CoordinateOptions cord = CoordinateOptions.builder()
                                .latitude(latitude)
                                .longitude(longitude).build();
        Call<SearchResponse> call = yelpAPI.search(cord, params);                       //eventually need to make "Austin" change dynamically depending on user location as reported by google maps api
        SearchResponse searchResponse = call.execute().body();
        businesses = searchResponse.businesses();

        ArrayList<Business> filteredBusinesses = new ArrayList<Business>();
        //go through businesses and filter those who don't meet ratingParam requirement
        for(Business business: businesses){
            if(ratingParam==""||business.rating() >= Double.parseDouble(ratingParam)){
                filteredBusinesses.add(business);
            }
        }

        return filteredBusinesses;
    }

    public Business findBusiness(String businessID) throws IOException {
        Call<Business> call = yelpAPI.getBusiness(businessID);
        Business searchResult = call.execute().body();
        return searchResult;
    }
}
