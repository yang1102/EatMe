package com.example.jason.liketmreal;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import android.app.SearchManager;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.yelp.clientlib.entities.Business;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SearchMenu extends AppCompatActivity implements APIFetch.Callback {

    private Button searchNearbyButton;
    private Button suggestionButton;

    private NumberPicker typePicker;
    private NumberPicker costPicker;
    private NumberPicker distancePicker;
    private EditText searchView;

    private SearchMenu searchMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/4th of the available memory for this memory cache.
        BitmapCache.cacheSize = maxMemory / 4;
        // Get the size of the display so we properly size bitmaps
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        BitmapCache.maxW = size.x;
        BitmapCache.maxH = size.y;


        setContentView(R.layout.activity_search_menue);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        searchNearbyButton = (Button) findViewById(R.id.nearbyButton);

        searchView = (EditText) findViewById(R.id.searchText);;
        suggestionButton = (Button) findViewById(R.id.suggestButton);

        //setup pickers
        typePicker = (NumberPicker) findViewById(R.id.typePicker);
        costPicker = (NumberPicker) findViewById(R.id.costPicker);
        distancePicker = (NumberPicker) findViewById(R.id.distancePicker);
        searchMenu = this;

        final String[] arrayString= new String[]{"American","Chinese","Mexican","Italian","Indian"};//for testing purposes
        typePicker.setMinValue(0);
        typePicker.setMaxValue(arrayString.length-1);
        typePicker.setFormatter(new NumberPicker.Formatter() {

            @Override
            public String format(int value) {
                return arrayString[value];
            }
        });

        final String[] arrayCost= new String[]{"$","$$","$$$","$$$$","$$$$$"};//for testing purposes
        costPicker.setMinValue(0);
        costPicker.setMaxValue(arrayCost.length-1);
        costPicker.setFormatter(new NumberPicker.Formatter() {

            @Override
            public String format(int value) {
                return arrayCost[value];
            }
        });

        final String[] arrayDistance= new String[]{"1 Mile","5 Miles","10 Miles","15 Miles","20 Miles"};//for testing purposes
        distancePicker.setMinValue(0);
        distancePicker.setMaxValue(arrayDistance.length-1);
        distancePicker.setFormatter(new NumberPicker.Formatter() {

            @Override
            public String format(int value) {
                return arrayDistance[value];
            }
        });


        getResources().openRawResource(R.raw.yelpkey);

        //setup onclick listener for nearbyButton
        searchNearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKeyword = searchView.getText().toString();
                //put async task to query yelp api here.
                //that async task will call startSearchResultsActivity onCallback
                //manually creating searchResults list to send to listView Activity
                String foodType = arrayString[typePicker.getValue()].toLowerCase();
                String foodCoast= Integer.toString(typePicker.getValue());

                ArrayList<String> searchParam=new ArrayList<String>();

                searchParam.add(searchKeyword);
                searchParam.add(foodType);
                searchParam.add(foodCoast);

                if(searchKeyword!=null)
                    new APIFetch(searchMenu,searchParam,getResources().openRawResource(R.raw.yelpkey));
            }
        });

        //setup onclick listener for suggestButton
        suggestionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //animate number picker change
                changeValueByOne(costPicker, true);
            }
        });

    }

    //animate number picker
    private void changeValueByOne(final NumberPicker higherPicker, final boolean increment) {

        Method method;
        try {
            // refelction call for
            // higherPicker.changeValueByOne(true);
            method = higherPicker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(higherPicker, increment);

        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void startSearchResultsActivity(ArrayList<Restaurant> searchResults){
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("searchResults", searchResults);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;

    }



    @Override
    public void fetchStart() {

    }

    @Override
    public void fetchComplete(ArrayList<Business> result) {
        displayList(result);
    }

    @Override
    public void fetchCancel(String url) {

    }

    public void displayList(ArrayList<Business> result){
        ArrayList<Restaurant> searchResutls = new ArrayList<Restaurant>();

        for(Business bs:result){
//                Restaurant restaurant= new Restaurant(bs.name());

            URL url = null;
            try {
                url  = new URL(bs.url());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Restaurant rs = new Restaurant(bs.name(),bs.location().displayAddress().toString().replace("[","").replace("]",""),url,bs.phone(),bs.rating().intValue(),bs.imageUrl());
            searchResutls.add(rs);
        }
        startSearchResultsActivity(searchResutls);
    }
}