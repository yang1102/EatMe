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
    protected Resource apiResource;

    public APIFetch(Callback callback, InputStream apiKey) {
        this.callback = callback;
        this.apiResource = Resource.getInstance(apiKey);
    }

    public void searchRestaurants(String rating, String type, String distance) {
        new AsyncSearch().execute(rating, type, distance);
    }

    public void findRestaurantByID(String restaurantID) {
        new AsyncSearch().execute(restaurantID);
    }

    public class AsyncSearch extends AsyncTask<String, Integer, ArrayList<Business>> {

        private ArrayList<Business> searchForRestaurantByID(String restaurantID){
            ArrayList<Business> result = new ArrayList<Business>();
            try{
                Business businessResult = apiResource.findBusiness(restaurantID);
                if(businessResult != null){
                    result.add(businessResult);
                    //add reviewer picture and star picture to bitmap
                    Bitmap userBitmap = null;
                    Bitmap ratingBitmap = null;
                    //get restaurant image
                    try {
                        userBitmap = BitmapFactory.decodeStream(new URL(businessResult.reviews().get(0).user().imageUrl()).openConnection().getInputStream());
                        ratingBitmap = BitmapFactory.decodeStream(new URL(businessResult.reviews().get(0).ratingImageLargeUrl()).openConnection().getInputStream());
                    }
                    catch (IOException e) {
                        userBitmap = BitmapCache.errorImageBitmap;
                        ratingBitmap = BitmapCache.errorImageBitmap;
                        e.printStackTrace();
                    }
                    BitmapCache.getInstance().setBitmap(businessResult.reviews().get(0).user().imageUrl(), userBitmap);
                    BitmapCache.getInstance().setBitmap(businessResult.reviews().get(0).ratingImageLargeUrl(), ratingBitmap);
                }
                return result;

            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        private ArrayList<Business> searchForRestaurantsByFilter(String ratingParam, String typeParam, String distanceParam){
            ArrayList<Business> result = new ArrayList<Business>();
            try{
                result = apiResource.search(ratingParam,typeParam,distanceParam);

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
        protected ArrayList<Business> doInBackground(String... strings) {
            //hacky way to reuse AsyncTask code to search for specific business or for nearby businesses
            if(strings.length == 1){
                return searchForRestaurantByID(strings[0]);
            }
            else{
                return searchForRestaurantsByFilter(strings[0], strings[1], strings[2]);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Business> result) {
            super.onPostExecute(result);
            callback.fetchComplete(result);
        }

    }
}
