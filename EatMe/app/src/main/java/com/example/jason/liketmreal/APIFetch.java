package com.example.jason.liketmreal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.yelp.clientlib.entities.Business;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jason on 11/9/16.
 */

public class APIFetch  {

    public interface Callback {
        void fetchComplete(ArrayList<Business> result);
    }

    protected Callback callback = null;
    protected String searchParam;
    protected Resource apiResource;

    public APIFetch(Callback callback, ArrayList<String> searchParam,InputStream apiKey) {
        this.callback = callback;
        this.searchParam = searchParam.toString().replace("[","").replace("]","").replace("\\s+","");
        this.apiResource = Resource.getInstance(apiKey);
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
        new AsyncSearch().execute(searchParam);
    }



    public class AsyncSearch extends AsyncTask<String, Integer, ArrayList<Business>> {

        @Override
        protected ArrayList<Business> doInBackground(String... strings) {
            ArrayList<Business> result = null;
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

            if(result == null){
                result = new ArrayList<Business>();
                return result;
            }

            for(Business bs:result){
                Bitmap bitmap = null;
                //get restaurant image
                try {
                    bitmap = BitmapFactory.decodeStream(new URL(bs.imageUrl()).openConnection().getInputStream());
                }
                catch (IOException e) {
                    bitmap = BitmapCache.errorImageBitmap;
                    e.printStackTrace();
                }
                BitmapCache.getInstance().setBitmap(bs.imageUrl(), bitmap);
                //get rating image
                try {
                    bitmap = BitmapFactory.decodeStream(new URL(bs.ratingImgUrlLarge()).openConnection().getInputStream());
                }
                catch (IOException e) {
                    bitmap = BitmapCache.errorImageBitmap;
                    e.printStackTrace();
                }
                BitmapCache.getInstance().setBitmap(bs.ratingImgUrlLarge(), bitmap);
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
