package com.example.jason.liketmreal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RestaurantViewActivity extends AppCompatActivity {
    Restaurant selectedRestaurant;
    TextView restaurantNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view);
        selectedRestaurant = (Restaurant) getIntent().getSerializableExtra("selectedRestaurant");
        restaurantNameView = (TextView) findViewById(R.id.selectedRestaurantName);
        restaurantNameView.setText(selectedRestaurant.getName());
    }
}
