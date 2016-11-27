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

import com.yelp.clientlib.entities.Business;

import java.net.MalformedURLException;
import java.net.URL;

public class RestaurantViewActivity extends AppCompatActivity {
    Business selectedRestaurant;
    TextView restaurantNameView;
    ImageView restaurantImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view);
        selectedRestaurant = (Business) getIntent().getSerializableExtra("selectedRestaurant");

        restaurantImageView = (ImageView) findViewById(R.id.RestaurantImage);
        restaurantImageView.setImageBitmap(BitmapCache.getInstance().getBitmap(selectedRestaurant.imageUrl()));

        restaurantNameView = (TextView) findViewById(R.id.selectedRestaurantName);
        restaurantNameView.setText(selectedRestaurant.name());

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
