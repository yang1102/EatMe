package com.example.jason.liketmreal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yelp.clientlib.entities.Business;

import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;


public class SearchMenu extends AppCompatActivity implements APIFetch.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Button searchNearbyButton;
    private Button suggestionButton;
    private ImageButton ratingLock;
    private ImageButton typeLock;
    private ImageButton distanceLock;

    private Boolean ratingLocked = false;
    private Boolean typeLocked = false;
    private Boolean distanceLocked = false;
    private Boolean suggestionPressed;

    final String[] arrayString = new String[]{"American", "Barbeque", "Brazilian", "Cafes", "Chinese", "French", "Greek", "Indian", "Italian", "Japanese", "Mexican", "Middle Eastern", "Thai"};
    final String[] arrayRating = new String[]{"3 stars", "3.5 stars", "4 stars", "4.5 stars", "5 stars"};
    final String[] arrayDistance = new String[]{"1 Mile", "5 Miles", "10 Miles", "15 Miles", "20 Miles", "25 Miles"};

    final String[] typeCodes = new String[]{"newamerican", "bbq", "brazilian", "cafes", "chinese", "french", "greek", "indpak", "italian", "japanese", "mexican", "mideastern", "thai",};
    final String[] milesToMeters = new String[]{"1609", "8046", "16093", "24140", "32186", "40000"};//approximate miles to meters conversion
    final String[] stringToDouble = new String[]{"3.0", "3.5", "4.0", "4.5", "5.0"};


    private NumberPicker typePicker;
    private NumberPicker ratingPicker;
    private NumberPicker distancePicker;
    private SearchMenu searchMenu;
    private MediaPlayer mediaPlayer;

    //map
    private SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private MapHolder mapHolder;
    private boolean mRequestingLocationUpdates = false;

    public static final String TAG = SearchMenu.class.getSimpleName();

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 8000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/4th of the available memory for this memory cache.
        BitmapCache.cacheSize = maxMemory / 4;
        // Get the size of the display so we properly size bitmaps
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        BitmapCache.maxW = size.x;
        BitmapCache.maxH = size.y;


        setContentView(R.layout.activity_search_menue);
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        searchNearbyButton = (Button) findViewById(R.id.nearbyButton);

        //searchView = (EditText) findViewById(R.id.searchText);;
        suggestionButton = (Button) findViewById(R.id.suggestButton);

        //setup pickers
        typePicker = (NumberPicker) findViewById(R.id.typePicker);
        ratingPicker = (NumberPicker) findViewById(R.id.ratingPicker);
        distancePicker = (NumberPicker) findViewById(R.id.distancePicker);

        //setup picker locks
        typeLock = (ImageButton) findViewById(R.id.typeLock);
        ratingLock = (ImageButton) findViewById(R.id.ratingLock);
        distanceLock = (ImageButton) findViewById(R.id.distanceLock);

        searchMenu = this;

        typePicker.setMinValue(0);
        typePicker.setMaxValue(arrayString.length - 1);
        typePicker.setValue(typePicker.getMinValue());
        typePicker.setFormatter(new NumberPicker.Formatter() {

            @Override
            public String format(int value) {
                return arrayString[value];
            }
        });
        changeValueByOne(typePicker);

        ratingPicker.setMinValue(0);
        ratingPicker.setMaxValue(arrayRating.length - 1);
        ratingPicker.setValue(ratingPicker.getMinValue());
        ratingPicker.setFormatter(new NumberPicker.Formatter() {

            @Override
            public String format(int value) {
                return arrayRating[value];
            }
        });
        changeValueByOne(ratingPicker);

        distancePicker.setMinValue(0);
        distancePicker.setMaxValue(arrayDistance.length - 1);
        distancePicker.setValue(distancePicker.getMinValue());
        distancePicker.setFormatter(new NumberPicker.Formatter() {

            @Override
            public String format(int value) {
                return arrayDistance[value];
            }
        });
        changeValueByOne(distancePicker);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        getResources().openRawResource(R.raw.yelpkey);

        //setup map services
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Create the LocationRequest object
        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        }

//        mapHolder = new MapHolder(this);
//        mapFragment = new SupportMapFragment();
//        mapFragment.getMapAsync(mapHolder);

        /* Notice the handy method chaining idiom for fragment transactions */
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.main_fragment, mapFragment)
//                .hide(mapFragment)
//                .commit();


        //setup onclick listener for nearbyButton
        searchNearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
//                toMapFragment();
                //tell class to start SearchResultsActivity on async return
                suggestionPressed = false;
                //put async task to query yelp api here.
                //that async task will call startSearchResultsActivity onCallback
                //manually creating searchResults list to send to listView Activity
                String foodType = typeCodes[typePicker.getValue()];
                String distance = milesToMeters[distancePicker.getValue()];
                String rating = stringToDouble[ratingPicker.getValue()];
                String currentLocation = mLastLocation.getLatitude()+","+mLastLocation.getLongitude();

                APIFetch apiAccess = new APIFetch(searchMenu, getResources().openRawResource(R.raw.yelpkey));
                apiAccess.searchRestaurants(rating, foodType, distance,currentLocation);
            }
        });

        //setup onclick listener for suggestButton
        suggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ratingPicker.isEnabled() && !typePicker.isEnabled() && !distancePicker.isEnabled()) {
                    return;
                }
                //tell class to start restaurantViewIntent on async return
                suggestionPressed = true;

                //animate number pickers
                animateSpinner(ratingPicker, 50, 2000L, false);
                animateSpinner(typePicker, 50, 2500L, false);
                animateSpinner(distancePicker, 50, 3000L, true);

                //start slot machine sound effect
                int resID = R.raw.slots_sound_effect;
                playSoundEffect(resID);
            }
        });

        //setup onclick listeners for lock buttons
        ratingLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingLocked) {
                    ratingLocked = false;
                    ratingLock.setImageResource(R.drawable.unlocked);
                    ratingPicker.setEnabled(true);
                } else {
                    ratingLocked = true;
                    ratingLock.setImageResource(R.drawable.locked);
                    ratingPicker.setEnabled(false);
                }
            }
        });

        typeLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeLocked) {
                    typeLocked = false;
                    typeLock.setImageResource(R.drawable.unlocked);
                    typePicker.setEnabled(true);
                } else {
                    typeLocked = true;
                    typeLock.setImageResource(R.drawable.locked);
                    typePicker.setEnabled(false);
                }
            }
        });

        distanceLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distanceLocked) {
                    distanceLocked = false;
                    distanceLock.setImageResource(R.drawable.unlocked);
                    distancePicker.setEnabled(true);
                } else {
                    distanceLocked = true;
                    distanceLock.setImageResource(R.drawable.locked);
                    distancePicker.setEnabled(false);
                }
            }
        });
    }

    protected void animateSpinner(final NumberPicker picker, int spinDistance, Long timeDuration, boolean onEndListener) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, spinDistance);

        if (picker.isEnabled()) {
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    picker.scrollBy(0, value);
                }
            });
        }

        //if onEndListener is true, then add onAnimationEnd listener to this animation object
        if (onEndListener) {
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //start search with current filters
                    String foodType = typeCodes[typePicker.getValue()];
                    String distance = milesToMeters[distancePicker.getValue()];
                    String rating = stringToDouble[ratingPicker.getValue()];
                    APIFetch apiAccess = new APIFetch(searchMenu, getResources().openRawResource(R.raw.yelpkey));

                    String currentLocation = mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
                    apiAccess.searchRestaurants(rating, foodType, distance,currentLocation);
                }
            });
        }

        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(timeDuration);
        valueAnimator.start();
    }

    public void startSearchResultsActivity(ArrayList<Business> searchResults) {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("searchResults", searchResults);
        startActivity(intent);
    }

    public void startRestaurantViewActivity(ArrayList<Business> searchResults) {
        //pick one resturant at random
        Random randomizer = new Random();
        Business searchResult = searchResults.get(randomizer.nextInt(searchResults.size()));
        //start restaurant view activity
        Intent intent = new Intent(this, RestaurantViewActivity.class);
        intent.putExtra("selectedRestaurant", searchResult);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;

    }

    @Override
    public void fetchComplete(ArrayList<Business> result) {
        if (result.isEmpty()) {
            playSoundEffect(R.raw.sad_trombone);
            Toast.makeText(searchMenu, "No restaurants found using current filters. Try relaxing constraints.", Toast.LENGTH_SHORT).show();
            return;
        }
        //hacky way to start single restaurant view or list view depending on what button was pressed. Probably should've just made two separate async downloader classes.
        if (suggestionPressed) {
            playSoundEffect(R.raw.ta_da);
            startRestaurantViewActivity(result);
        } else {
            startSearchResultsActivity(result);
        }
    }

    //fix to number picker being blank on initialization bug
    //incrementing the picker by one forces the formatter to work
    private void changeValueByOne(NumberPicker picker) {
        try {
            Method method = picker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(picker, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void playSoundEffect(int resID) {
        AssetFileDescriptor afd = searchMenu.getResources().openRawResourceFd(resID);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
            displayLocation();
            startLocationUpdates();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                // PERMISSION_REQUEST_ACCESS_FINE_LOCATION can be any unique int
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }

            Log.i(TAG, "Location permission error");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location!=null)
             mLastLocation=location;
        Log.d(TAG,location.toString());
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                // PERMISSION_REQUEST_ACCESS_FINE_LOCATION can be any unique int
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }

            Log.i(TAG, "Location permission error");
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
        } else {
            Toast.makeText(getApplicationContext(), "service not ready !",
                    Toast.LENGTH_SHORT).show();
        }
    }

}