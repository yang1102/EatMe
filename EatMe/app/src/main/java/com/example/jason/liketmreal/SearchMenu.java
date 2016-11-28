package com.example.jason.liketmreal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

import com.yelp.clientlib.entities.Business;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SearchMenu extends AppCompatActivity implements APIFetch.Callback {

    private Button searchNearbyButton;
    private Button suggestionButton;
    private ImageButton ratingLock ;
    private ImageButton typeLock;
    private ImageButton distanceLock;

    private Boolean ratingLocked = false;
    private Boolean typeLocked = false;
    private Boolean distanceLocked = false;

    final String[] arrayString= new String[]{"American", "Barbeque", "Brazilian", "Cafes", "Chinese", "French", "Greek", "Indian","Italian", "Japanese", "Mexican", "Middle Eastern", "Thai"};
    final String[] arrayRating= new String[]{"3 stars", "3.5 stars","4 stars","4.5 stars","5 stars"};
    final String[] arrayDistance= new String[]{"1 Mile","5 Miles","10 Miles","15 Miles","20 Miles", "25 Miles"};

    final String[] typeCodes = new String[]{"newamerican", "bbq", "brazilian", "cafes", "chinese", "french", "greek", "indpak", "italian", "japanese", "mexican", "mideastern", "thai",};
    final String[] milesToMeters = new String[]{"1609", "8046", "16093", "24140", "32186", "40000"};//approximate miles to meters conversion
    final String[] stringToDouble = new String[]{"3.0", "3.5", "4.0", "4.5", "5.0"};


    private NumberPicker typePicker;
    private NumberPicker ratingPicker;
    private NumberPicker distancePicker;
    private EditText searchView;
    private SearchMenu searchMenu;
    private MediaPlayer mediaPlayer;

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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
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
        typePicker.setMaxValue(arrayString.length-1);
        typePicker.setValue(typePicker.getMinValue());
        typePicker.setFormatter(new NumberPicker.Formatter() {

            @Override
            public String format(int value) {
                return arrayString[value];
            }
        });
        changeValueByOne(typePicker);

        ratingPicker.setMinValue(0);
        ratingPicker.setMaxValue(arrayRating.length-1);
        ratingPicker.setValue(ratingPicker.getMinValue());
        ratingPicker.setFormatter(new NumberPicker.Formatter() {

            @Override
            public String format(int value) {
                return arrayRating[value];
            }
        });
        changeValueByOne(ratingPicker);

        distancePicker.setMinValue(0);
        distancePicker.setMaxValue(arrayDistance.length-1);
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

        //setup onclick listener for nearbyButton
        searchNearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //put async task to query yelp api here.
                //that async task will call startSearchResultsActivity onCallback
                //manually creating searchResults list to send to listView Activity
                String foodType = typeCodes[typePicker.getValue()];
                String distance = milesToMeters[distancePicker.getValue()];
                String rating = stringToDouble[ratingPicker.getValue()];
                APIFetch apiAccess = new APIFetch(searchMenu, getResources().openRawResource(R.raw.yelpkey));
                apiAccess.searchRestaurants(rating, foodType, distance);
            }
        });

        //setup onclick listener for suggestButton
        suggestionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!ratingPicker.isEnabled() && !typePicker.isEnabled() && !distancePicker.isEnabled()){
                    return;
                }

                //animate number pickers
                animateSpinner(ratingPicker, 50, 2000L, false);
                animateSpinner(typePicker, 50, 2500L, false);
                animateSpinner(distancePicker, 50, 3000L, true);

                //start slot machine sound effect
                int resID = R.raw.slots_sound_effect;
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
        });

        //setup onclick listeners for lock buttons
        ratingLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingLocked){
                    ratingLocked = false;
                    ratingLock.setImageResource(R.drawable.unlocked);
                    ratingPicker.setEnabled(true);
                }
                else{
                    ratingLocked = true;
                    ratingLock.setImageResource(R.drawable.locked);
                    ratingPicker.setEnabled(false);
                }
            }
        });

        typeLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeLocked){
                    typeLocked = false;
                    typeLock.setImageResource(R.drawable.unlocked);
                    typePicker.setEnabled(true);
                }
                else{
                    typeLocked = true;
                    typeLock.setImageResource(R.drawable.locked);
                    typePicker.setEnabled(false);
                }
            }
        });

        distanceLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(distanceLocked){
                    distanceLocked = false;
                    distanceLock.setImageResource(R.drawable.unlocked);
                    distancePicker.setEnabled(true);
                }
                else{
                    distanceLocked = true;
                    distanceLock.setImageResource(R.drawable.locked);
                    distancePicker.setEnabled(false);
                }
            }
        });
    }

    protected void animateSpinner(final NumberPicker picker, int spinDistance, Long timeDuration, boolean onEndListener){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, spinDistance);

        if(!picker.isEnabled()){
            return;
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                picker.scrollBy(0, value);
            }
        });

        //if onEndListener is true, then add onAnimationEnd listener to this animation object
        if(onEndListener){
            valueAnimator.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    //start the restaurant home page activity
                }
            });
        }

        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(timeDuration);
        valueAnimator.start();
    }

    public void startSearchResultsActivity(ArrayList<Business> searchResults){
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("searchResults", searchResults);
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
        if(result.isEmpty()){
            Toast.makeText(searchMenu, "No reviews found using current filters. Try relaxing constraints.", Toast.LENGTH_SHORT).show();
            return;
        }
        startSearchResultsActivity(result);
    }

    //fix to number picker being blank on initialization bug
    //incrementing the picker by one forces the formatter to work
    private void changeValueByOne(NumberPicker picker){
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
}