package com.example.jason.liketmreal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
    private ArrayList<Business> restaurants = new ArrayList<Business>();
    protected DynamicAdapter restaurantsAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //get search results from intent
        restaurants = (ArrayList<Business>) getIntent().getSerializableExtra("searchResults");//check null first??

        // Lookup the recyclerview in activity layout
        RecyclerView searchResultsRecycler = (RecyclerView) findViewById(R.id.searchResults);

        restaurantsAdapter = new DynamicAdapter(restaurants);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        searchResultsRecycler.setLayoutManager(mLayoutManager);
        //searchResultsRecycler.setItemAnimator(new DefaultItemAnimator());
        searchResultsRecycler.setAdapter(restaurantsAdapter);
    }
}
