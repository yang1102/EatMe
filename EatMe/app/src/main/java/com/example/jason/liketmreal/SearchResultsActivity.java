package com.example.jason.liketmreal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchResultsActivity extends AppCompatActivity {
    private ArrayList<Business> restaurants = new ArrayList<Business>();
    protected DynamicAdapter restaurantsAdapter = null;
    Boolean aToz = true;
    Boolean highToLow = true;
    Boolean nearToFar = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //get search results from intent
        restaurants = (ArrayList<Business>) getIntent().getSerializableExtra("searchResults");//check null first??

        // Lookup the recyclerview in activity layout
        final RecyclerView searchResultsRecycler = (RecyclerView) findViewById(R.id.searchResults);

        restaurantsAdapter = new DynamicAdapter(restaurants);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        searchResultsRecycler.setLayoutManager(mLayoutManager);
        //searchResultsRecycler.setItemAnimator(new DefaultItemAnimator());
        searchResultsRecycler.setAdapter(restaurantsAdapter);

        //setup buttons
        Button alphaOrder = (Button) findViewById(R.id.alphaOrder);
        alphaOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //order list by alphabetical order
                if(aToz){
                    //order from A to Z
                    Collections.sort(restaurantsAdapter.restaurants, new Comparator<Business>() {
                        @Override
                        public int compare(Business lhs, Business rhs) {
                            return lhs.name().compareTo(rhs.name());
                        }
                    });
                    aToz = false;
                }
                else{
                    //order from Z to A
                    Collections.sort(restaurantsAdapter.restaurants, new Comparator<Business>() {
                        @Override
                        public int compare(Business lhs, Business rhs) {
                            return rhs.name().compareTo(lhs.name());
                        }
                    });
                    aToz = true;
                }
                restaurantsAdapter.notifyDataSetChanged();
            }
        });

        Button ratingOrder = (Button) findViewById(R.id.ratingOrder);
        ratingOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //order list by alphabetical order
                if(highToLow){
                    //order from low ratings to high ratings
                    Collections.sort(restaurantsAdapter.restaurants, new Comparator<Business>() {
                        @Override
                        public int compare(Business lhs, Business rhs) {
                            return rhs.rating().compareTo(lhs.rating());
                        }
                    });
                    highToLow = false;
                }
                else{
                    //order from high ratings to low ratings
                    Collections.sort(restaurantsAdapter.restaurants, new Comparator<Business>() {
                        @Override
                        public int compare(Business lhs, Business rhs) {
                            return lhs.rating().compareTo(rhs.rating());
                        }
                    });
                    highToLow = true;
                }
                restaurantsAdapter.notifyDataSetChanged();
            }
        });

        Button distanceOrder = (Button) findViewById(R.id.distanceOrder);
        distanceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //order list by alphabetical order
                if(nearToFar){
                    //order from furthest restaurant to nearest
                    Collections.sort(restaurantsAdapter.restaurants, new Comparator<Business>() {
                        @Override
                        public int compare(Business lhs, Business rhs) {
                            return rhs.distance().compareTo(lhs.distance());
                        }
                    });
                    nearToFar = false;
                }
                else{
                    //order from nearest restaurant to furthest
                    Collections.sort(restaurantsAdapter.restaurants, new Comparator<Business>() {
                        @Override
                        public int compare(Business lhs, Business rhs) {
                            return lhs.distance().compareTo(rhs.distance());
                        }
                    });
                    nearToFar = true;
                }
                restaurantsAdapter.notifyDataSetChanged();
            }
        });
    }
}
