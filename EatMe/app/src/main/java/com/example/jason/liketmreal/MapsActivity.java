package com.example.jason.liketmreal;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Category;

import java.util.ArrayList;
import java.util.Arrays;

import autovalue.shaded.com.google.common.common.collect.MapMaker;

import static android.R.attr.name;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private ArrayList<Business> restaurants = new ArrayList<Business>();

    private LatLng my;
    Marker me;

    final ArrayList<String> typeCodes = new ArrayList<String>(Arrays.asList("newamerican", "bbq", "brazilian", "cafes", "chinese", "french", "greek", "indpak", "italian", "japanese", "mexican", "mideastern", "thai"));

    /*
     * Define a request code to send to Google Play services
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 8000;

    //Define tag for creating logs
    public static final String TAG = MapsActivity.class.getSimpleName();


    //location service
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    // Keys for storing activity state.
    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.



        //setup map services
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */,
                            this /* OnConnectionFailedListener */)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            if (mLocationRequest == null) {
                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(1000); // 1 second, in milliseconds
            }
        }
        mGoogleApiClient.connect();


        // Create the LocationRequest object


        restaurants = (ArrayList<Business>) getIntent().getSerializableExtra("restaurants");//check null first??
//        mLastLocation = getIntent().getParcelableExtra("currentLocation");
//        restaurant = getIntent().getParcelableExtra("restaurant");
//        if (restaurant != null && restaurants == null)
//            onRoute = true;
//        else if (restaurant == null && restaurants != null)
//            onRoute = false;
//        else
//            Toast.makeText(this, "You should never get here", Toast.LENGTH_SHORT).show();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mLastLocation!=null) {
            my = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            updateLocationUI();
        }
        else{
            Toast.makeText(getApplicationContext(), "cant access previous location!",
                    Toast.LENGTH_SHORT).show();
        }

            if(me==null)
                me = mMap.addMarker(new MarkerOptions()
                    .position(my));

            for (Business business : restaurants) {
                addMarker(business);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(my));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

            mMap.setOnMarkerClickListener(this);
            mMap.setOnInfoWindowClickListener(this);
            mMap.setOnMapClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(my));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Business markerBusiness = (Business) marker.getTag();
        if(markerBusiness!=null){
            Intent intent = new Intent(this, RestaurantViewActivity.class);
            intent.putExtra("selectedRestaurant", markerBusiness);
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"invalid marker",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //if not click on current location
        if(!marker.equals(me)){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(marker.getPosition())      // Sets the center of the map to Mountain View
                    .zoom(17)                   // Sets the zoom
                    .bearing(180)                // Sets the orientation of the camera
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition), 2000, null);
            marker.showInfoWindow();
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        moveToLocation(my);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
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

        //Build the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        updateLocationUI();

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
        if(location!=null) {
            mLastLocation = location;
            my = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            moveToLocation(my);
        }
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
        if(mLastLocation == null){
            Toast.makeText(getApplicationContext(), "cant access previous location!",
                    Toast.LENGTH_SHORT).show();
            startLocationUpdates();
        }
    }

    public void moveToLocation(LatLng loc){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(loc)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition),2000,null);

        if(me!=null)
            me.setPosition(loc);
    }

    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void addMarker(Business business){
        LatLng cur = new LatLng(business.location().coordinate().latitude(), business.location().coordinate().longitude());
        Log.d(TAG,business.categories().toString());
        String category = business.categories().get(0).alias();
        String category_name = business.categories().get(0).name();

        //set restaurant marker icon
        for(Category cat:business.categories()){
            if(typeCodes.contains(cat.alias())){
                category = cat.alias();
                category_name = cat.name();
                break;
            }
        }

        final int resourceId = getResources().getIdentifier(category, "drawable", getPackageName());
        Marker m;
        if (resourceId != 0) {
            m = mMap.addMarker(new MarkerOptions()
                    .position(cur)
                    .title(business.name())
                    .snippet(category_name)
                    .icon(BitmapDescriptorFactory.fromResource(resourceId)));
            m.setTag(business);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        } else {
            m = mMap.addMarker(new MarkerOptions()
                    .position(cur)
                    .title(business.name())
                    .snippet(category_name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant)));
            m.setTag(business);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
    }


}
