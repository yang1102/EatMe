package com.example.jason.liketmreal;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

/**
 * Created by mattunion on 10/16/16.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.DynamicViewHolder> {
    public ArrayList<Business> restaurants = null;//copy constructor or reference to arraylist in main_activity?

    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView address;
        public ImageView pic;
        public TextView distance;
        public View v;
        protected ImageView ratingImage;

        public DynamicViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.restaurant_name);
            address = (TextView) view.findViewById(R.id.restaurant_address);
            pic = (ImageView) view.findViewById(R.id.restaurant_pic);
            distance = (TextView) view.findViewById(R.id.distance);
            ratingImage = (ImageView) view.findViewById(R.id.ratingImage);
            v = view;
        }
    }
    public RestaurantAdapter(ArrayList<Business> restaurants) {
        this.restaurants = restaurants;
    }

    public Business getItem(int position) {
        return restaurants.get(position);
    }

    @Override
    public RestaurantAdapter.DynamicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_list_cell, parent, false);

        return new DynamicViewHolder(itemView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RestaurantAdapter.DynamicViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Business restaurant = restaurants.get(position);

        // Set item views based on your views and data model
        viewHolder.name.setText(restaurant.name());
        viewHolder.address.setText(restaurant.location().displayAddress().toString().replace("[","").replace("]",""));
        if(restaurant.distance() == null){
            viewHolder.distance.setText(String.valueOf("distance unknown"));
        }
        else{
            viewHolder.distance.setText(String.valueOf(restaurant.distance()) + " meters away");
        }
        if(restaurant.imageUrl()!=null)
            viewHolder.pic.setImageBitmap(BitmapCache.getInstance().getBitmap(restaurant.imageUrl()));
        else
            viewHolder.pic.setImageBitmap(BitmapCache.errorImageBitmap);
        viewHolder.ratingImage.setImageBitmap(BitmapCache.getInstance().getBitmap(restaurant.ratingImgUrlLarge()));


        viewHolder.v.setOnClickListener(new View.OnClickListener() {
            //how to add url to bundle
            @Override
            public void onClick(View view) {
                //add intenet to run Restaurant homepage view activity
                Intent intent = new Intent(view.getContext(), RestaurantViewActivity.class);
                intent.putExtra("selectedRestaurant", restaurant);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}