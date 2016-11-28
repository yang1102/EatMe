package com.example.jason.liketmreal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Review;
import com.yelp.clientlib.entities.User;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RestaurantViewActivity extends AppCompatActivity implements APIFetch.Callback{
    Business selectedRestaurant;
    TextView restaurantNameView;
    TextView restaurantAddressView;
    ImageView restaurantImageView;
    ImageView ratingImageView;
    protected ReviewAdapter reviewAdapter = null;
    protected ArrayList<Review> reviews = new ArrayList<Review>();


    //reviews download finished
    @Override
    public void fetchComplete(ArrayList<Business> result) {
        if(result.isEmpty()){
            Toast.makeText(getApplicationContext(), "Couldn't Load Restaurant", Toast.LENGTH_SHORT).show();
            return;
        }
        //update reviews list view
        Business asyncBusiness = result.get(0);
        ArrayList<Review> reviewsFound = asyncBusiness.reviews();
        reviews.add(0, reviewsFound.get(0));
        reviewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view);
        selectedRestaurant = (Business) getIntent().getSerializableExtra("selectedRestaurant");

        //first thing, async download the reivew list
        ArrayList<String> searchParams = new ArrayList<String>();
        searchParams.add(selectedRestaurant.id());
        APIFetch apiAccess = new APIFetch(this,searchParams,getResources().openRawResource(R.raw.yelpkey));
        apiAccess.startDownload();

        restaurantImageView = (ImageView) findViewById(R.id.RestaurantImage);
        restaurantImageView.setImageBitmap(BitmapCache.getInstance().getBitmap(selectedRestaurant.imageUrl()));

        ratingImageView = (ImageView) findViewById(R.id.selectedRating);
        ratingImageView.setImageBitmap(BitmapCache.getInstance().getBitmap(selectedRestaurant.ratingImgUrlLarge()));

        restaurantNameView = (TextView) findViewById(R.id.selectedRestaurantName);
        restaurantNameView.setText(selectedRestaurant.name());

        restaurantAddressView = (TextView) findViewById(R.id.selectedRestaurantAddress);
        restaurantAddressView.setText(selectedRestaurant.location().displayAddress().toString().replace("[","").replace("]",""));

        TextView website = (TextView) findViewById(R.id.selectedWebsite);
        website.setText(Html.fromHtml("<a href=\"" + selectedRestaurant.url() + "\">website</a>"));
        website.setMovementMethod(LinkMovementMethod.getInstance()); //set onclick for web browser intent

        TextView phoneView = (TextView) findViewById(R.id.selectedPhone);
        final String phoneNumber = selectedRestaurant.phone();
        phoneView.setText(phoneNumber);
        phoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                v.getContext().startActivity(intent);
            }
        });

        //create toy user data
        final User user1 = new User() {
            @Override
            public String id() {
                return "user1";
            }

            @Override
            public String imageUrl() {
                return "defaultUserImage";
            }

            @Override
            public String name() {
                return "Matt Union";
            }
        };

        //create toy Review Data
        Review review1 = new Review() {
            @Override
            public String id() {
                return "reivew1";
            }

            @Override
            public String excerpt() {
                return "This is toy data to show that we could display reviews if they were returned by the api";
            }

            @Override
            public Double rating() {
                return 4.0;
            }

            @Override
            public String ratingImageUrl() {
                return null;
            }

            @Override
            public String ratingImageLargeUrl() {
                return "defaultRatingImage";
            }

            @Override
            public String ratingImageSmallUrl() {
                return null;
            }

            @Override
            public Long timeCreated() {
                return null;
            }

            @Override
            public User user() {
                return user1;
            }
        };

        //setting defualt rating image
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.stars_large_5);
        BitmapCache.getInstance().setBitmap("defaultRatingImage", bm);


        for(int i = 0; i<8; i++){
            reviews.add(0, review1);
        }

        //setup list view
        // Lookup the recyclerview in activity layout
        final RecyclerView searchResultsRecycler = (RecyclerView) findViewById(R.id.searchResults);
        reviewAdapter = new ReviewAdapter(reviews);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        searchResultsRecycler.setLayoutManager(mLayoutManager);
        //searchResultsRecycler.setItemAnimator(new DefaultItemAnimator());
        searchResultsRecycler.setAdapter(reviewAdapter);

    }
}
