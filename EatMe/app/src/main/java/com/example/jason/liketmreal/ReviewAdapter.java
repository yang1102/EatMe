package com.example.jason.liketmreal;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Review;

import java.util.ArrayList;

/**
 * Created by mattunion on 11/27/16.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.DynamicViewHolder>{
    public ArrayList<Review> reviews = null;//copy constructor or reference to arraylist in main_activity?

    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        protected TextView authorNameView;
        protected TextView bodyView;
        protected ImageView authorImageView;
        protected ImageView ratingImageView;
        protected View v;

        public DynamicViewHolder(View view) {
            super(view);
            authorNameView = (TextView) view.findViewById(R.id.authorName);
            bodyView = (TextView) view.findViewById(R.id.reviewBody);
            ratingImageView = (ImageView) view.findViewById(R.id.reviewRatingImage);
            authorImageView = (ImageView) view.findViewById(R.id.authorImage);
            v = view;
        }
    }
    public ReviewAdapter(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public Review getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public ReviewAdapter.DynamicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_cell, parent, false);
        return new ReviewAdapter.DynamicViewHolder(itemView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ReviewAdapter.DynamicViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Review review = reviews.get(position);

        // Set item views based on your views and data model
        viewHolder.authorNameView.setText(review.user().name());
        viewHolder.bodyView.setText(review.excerpt());
        viewHolder.authorImageView.setImageBitmap(BitmapCache.getInstance().getBitmap(review.user().imageUrl()));
        viewHolder.ratingImageView.setImageBitmap(BitmapCache.getInstance().getBitmap(review.ratingImageLargeUrl()));


        viewHolder.v.setOnClickListener(new View.OnClickListener() {
            //how to add url to bundle
            @Override
            public void onClick(View view) {
                //add intenet to run Restaurant homepage view activity
                //Intent intent = new Intent(view.getContext(), RestaurantViewActivity.class);
                //intent.putExtra("selectedReview", review);
                //view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
