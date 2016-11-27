package com.example.jason.liketmreal;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Review;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RestaurantViewActivity extends AppCompatActivity implements APIFetch.Callback{
    Business selectedRestaurant;
    TextView restaurantNameView;
    TextView restaurantAddressView;
    ImageView restaurantImageView;
    ImageView ratingImageView;

    //reviews download finished
    @Override
    public void fetchComplete(ArrayList<Business> result) {
        if(result.isEmpty()){
            Toast.makeText(getApplicationContext(), "Couldn't Load Restaurant", Toast.LENGTH_SHORT).show();
            return;
        }
        //update reviews list view
        Business asyncBusiness = result.get(0);
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

    }
}
