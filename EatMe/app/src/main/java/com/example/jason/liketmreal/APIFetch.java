package com.example.jason.liketmreal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.yelp.clientlib.entities.Business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Jason on 11/9/16.
 */

public class APIFetch  {

    public interface Callback {
        void fetchStart();
        void fetchComplete(ArrayList<Business> result);
        void fetchCancel(String keyward);
    }

    protected Callback callback = null;
    protected String searchKey;
    protected Resource apiResource;

    public APIFetch(Callback callback, String searchKey,InputStream apiKey) {
        this.callback = callback;
        this.searchKey = searchKey;
        this.apiResource = Resource.getInstance(apiKey);
        startDownload();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return searchKey.equals(((APIFetch)obj).searchKey);
    }

    public void startDownload() {
        new AsyncDownloader().execute(searchKey);
    }



    public class AsyncDownloader extends AsyncTask<String, Integer, ArrayList<Business>> {

        @Override
        protected ArrayList<Business> doInBackground(String... strings) {
            ArrayList<Business> result =null;
            String keyword= strings[0];

            try{
                result = apiResource.search(keyword);

            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Business> result) {
            super.onPostExecute(result);
            callback.fetchComplete(result);
        }

    }
}
