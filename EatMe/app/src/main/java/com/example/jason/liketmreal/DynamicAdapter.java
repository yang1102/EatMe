package com.example.jason.liketmreal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jason.liketmreal.R;
import com.example.jason.liketmreal.Restaurant;

import java.net.URL;
import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by mattunion on 10/16/16.
 */

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder> {
    private ArrayList<Restaurant> resturants = null;//copy constructor or reference to arraylist in main_activity?

    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        //public TextView hiddenText;
        public View v;

        public DynamicViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.restaurantName);
            v = view;
        }
    }
    public DynamicAdapter(ArrayList<Restaurant> restaurants) {
        this.resturants = restaurants;
    }

    public Restaurant getItem(int position) {
        return resturants.get(position);
    }

    @Override
    public DynamicAdapter.DynamicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_list_cell, parent, false);

        return new DynamicViewHolder(itemView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(DynamicAdapter.DynamicViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Restaurant restaurant = resturants.get(position);

        // Set item views based on your views and data model
        TextView nameView = viewHolder.name;
        nameView.setText(restaurant.getName());

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
        return resturants.size();
    }
}