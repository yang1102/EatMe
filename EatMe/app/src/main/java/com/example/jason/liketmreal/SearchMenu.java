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
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SearchMenu extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Resource apiResource;
    Button bt;
    TextView tv;
    private String[]  category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menue);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.restaurant_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        apiResource = Resource.getInstance(getResources().openRawResource(R.raw.yelpkey));

        category = getResources().getStringArray(R.array.restaurant_type);
        category[0] = "newamerican";
        category[1] = "tradamerican";

//        tv = (TextView) findViewById(R.id.output);
//        bt = (Button) findViewById(R.id.button);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MyTask().execute();
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));

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
