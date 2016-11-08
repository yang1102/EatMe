package com.example.jason.liketmreal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
    private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //get search results from intent
        restaurants = (ArrayList<Restaurant>) getIntent().getSerializableExtra("searchResults");//check null first??

        //test search results serialized okay
        for(Restaurant restaurant: restaurants){
            Toast.makeText(this, restaurant.getName(), Toast.LENGTH_SHORT).show();
        }
    }
}
