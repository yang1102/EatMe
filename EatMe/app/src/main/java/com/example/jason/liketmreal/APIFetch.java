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
    protected String searchParam;
    protected Resource apiResource;

    public APIFetch(Callback callback, ArrayList<String> searchParam,InputStream apiKey) {
        this.callback = callback;
        this.searchParam = searchParam.toString().replace("[","").replace("]","").replace("\\s+","");
        this.apiResource = Resource.getInstance(apiKey);
        System.out.println(this.searchParam);
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
        return searchParam.equals(((APIFetch)obj).searchParam);
    }

    public void startDownload() {
        new AsyncDownloader().execute(searchParam);
    }



    public class AsyncDownloader extends AsyncTask<String, Integer, ArrayList<Business>> {

        @Override
        protected ArrayList<Business> doInBackground(String... strings) {
            ArrayList<Business> result =null;
            String[] keyParam = strings[0].split(",");
            String keyword = keyParam[0];
            String foodType = keyParam[1];
            String foodCost = keyParam[2];

            try{
                result = apiResource.search(keyword,foodType,foodCost);

            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e) {
                e.printStackTrace();
            }

            for(Business bs:result){
                Bitmap bitmap = null;
                try{
                    try {
                        bitmap = BitmapFactory.decodeStream(new URL(bs.imageUrl()).openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (OutOfMemoryError e) {
                    bitmap = BitmapCache.errorImageBitmap;
                }

                BitmapCache.getInstance().setBitmap(bs.imageUrl(), bitmap);
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
