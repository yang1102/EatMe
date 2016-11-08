package com.example.jason.liketmreal;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SearchMenu extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Resource apiResource;
    private Button searchNearbyButton;
    private TextView tv;
    private String[]  category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menue);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Spinner priceSpinner = (Spinner) findViewById(R.id.priceSpinner);
        setupSpinner(priceSpinner,R.array.price_type);

        Spinner foodSpinner = (Spinner) findViewById(R.id.foodSpinner);
        setupSpinner(foodSpinner,R.array.restaurant_type);

        searchNearbyButton = (Button) findViewById(R.id.nearbyButton);

        apiResource = Resource.getInstance(getResources().openRawResource(R.raw.yelpkey));

        category = getResources().getStringArray(R.array.restaurant_type);
        category[0] = "newamerican";
        category[1] = "tradamerican";

        //setup onclick listener for nearbyButton
        searchNearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //put async task to query yelp api here.
                //that async task will call startSearchResultsActivity onCallback
                //manually creating searchResults list to send to listView Activity
                ArrayList<Restaurant> searchResutls = new ArrayList<Restaurant>();
                searchResutls.add(new Restaurant("Torchy's Tacos", "301 Gaudalupe St.", "www.TorchysTacos.com","5126567432", 5, 3));
                searchResutls.add(new Restaurant("Fuzzy's Tacos"));
                searchResutls.add(new Restaurant("Taco's and Tequila"));
                searchResutls.add(new Restaurant("Taco Bell"));
                searchResutls.add(new Restaurant("Del Taco"));
                searchResutls.add(new Restaurant("Taco Shack"));
                searchResutls.add(new Restaurant("Taco Deli"));

                startSearchResultsActivity(searchResutls);
            }
        });

    }

    public void startSearchResultsActivity(ArrayList<Restaurant> searchResults){
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("searchResults", searchResults);
        startActivity(intent);
    }

    public void setupSpinner(Spinner s,int id ){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,id,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;

    }


    private class MyTask extends AsyncTask<Void, Void, Void> {

        String Result = "error";
        protected Void doInBackground(Void... params){
            try{
                Result = apiResource.search();

            }catch(MalformedURLException e){
                e.printStackTrace();
                Result = e.toString();

            }catch(IOException e) {
                e.printStackTrace();
                Result = "IO error";
            }
            return null;
        }

        protected void onPostExecute(Void result){
            tv.setText(Result);
            super.onPostExecute(result);
        }

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (pos != 0) {
            String list = category[pos];

            // XXX Spinner has chosen new restaurant list
            // Inform restaurantAdapter of the change
        }
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
