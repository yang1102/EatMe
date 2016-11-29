package com.example.jason.liketmreal;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by thunt on 10/25/16.
 * Holds Maps...What else would it do?
 */

public class MapHolder implements OnMapReadyCallback {
    /* Some from RedFetch some from this example:
    http://theoryapp.com/parse-json-in-java/
     */

    static boolean invalid = false;

//    private static class NameToLatLngTask extends AsyncTask<String, Object, LatLng> {
//        public interface OnLatLngCallback {
//            public void onLatLng(LatLng a);
//        }
//
//        OnLatLngCallback cb;
//        Context context;
//
//        Toast toast;
//
//        URL geocoderURLBuilder(String address) {
//            URL result = null;
//            final String base = "https://maps.googleapis.com/maps/api/geocode/json?key=";
//            try {
//                result = new URL(base + context.getResources().getString(R.string.google_maps_key)
//                        + "&address=" +  URLEncoder.encode(address, "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                Log.e("Geocoder", "Encoding address: " + e.toString());
//            } catch (MalformedURLException e) {
//                Log.e("Geocoder", "Building URL: " + e.toString());
//            }
//            return result;
//        }
//
//        public NameToLatLngTask(Context ctx, String addr, OnLatLngCallback _cb) {
//            context = ctx;
//            execute(addr);
//            cb = _cb;
//        }
//
//        protected LatLng latLngFromJsonString(String json) throws JSONException {
//            JSONObject obj = new JSONObject(json);
//            LatLng result = null;
//            if (!obj.getString("status").equals("OK")) {
//                Log.e("URLfetch", "returned status" + obj.getString("status"));
//            } else {
//                JSONObject loc = obj.getJSONArray("results").getJSONObject(0)
//                        .getJSONObject("geometry")
//                        .getJSONObject("location");
//                double lat = loc.getDouble("lat");
//                double lng = loc.getDouble("lng");
//                result = new LatLng(lat, lng);
//                Log.d("Geocoder", "got lat: " + lat + ", lng: " + lng);
//            }
//            return result;
//        }
//
//        @Override
//        protected LatLng doInBackground(String... params) {
//            assert(params.length > 1);
//            String name = params[0];
//            URL url;
//            LatLng pos = null;
//
//            /* Try Geocoder first */
//            {
//                Geocoder geo = new Geocoder(context);
//
//                /* XXX write me
//                    Use the Geocoder object for fast(er) geocoding first
//                 */
//                try {
//                    List<Address> addressList = geo.getFromLocationName(name,1);
//                    if(addressList!=null &&addressList.size()!=0) {
//                        invalid = false;
//                        Address add = addressList.get(0);
//                        pos = new LatLng(add.getLatitude(), add.getLongitude());
//                        return pos;
//                    }
//                    else{
//                        invalid = true;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    invalid = true;
//                }
//
//
//            }
//
//            /* go remote as a last resort*/
//            url = geocoderURLBuilder(name);
//            if (url == null) {
//                cancel(true);
//                return null;
//            }
//
//            try {
//                String result = null;
//                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//                urlConn.connect();
//
//                if( urlConn.getContentType().startsWith("application/json") )
//                    result = fetchJson(urlConn);
//                else
//                    Log.e("URLfetch", "Result has bad type (not json)");
//
//                if (result != null)
//                    pos = latLngFromJsonString(result);
//            } catch (IOException e) {
//                Log.e("URLfetch", e.toString());
//                e.printStackTrace();
//            } catch (JSONException e) {
//                Log.e("JsonBuild", "JSON malformed");
//            }
//
//            if (pos == null) {
//                cancel(false);
//            }
//            return pos;
//        }
//
//        protected String readStreamToString(InputStream in) throws IOException{
//            int numRead;
//            final int bufferSize = 1024;
//            byte[] buffer = new byte[bufferSize];
//            ByteArrayOutputStream outString = new ByteArrayOutputStream();
//
//            while ((numRead = in.read(buffer)) != -1) {
//                outString.write(buffer, 0, numRead);
//                if (isCancelled()) {
//                    return null;
//                }
//            }
//            return new String(outString.toByteArray(), "UTF-8");
//        }
//
//        protected String fetchJson(HttpURLConnection conn) {
//            InputStream in = null;
//            String result = null;
//            try {
//                in = new BufferedInputStream(conn.getInputStream());
//                result = readStreamToString(in);
//                Log.d("fetchJson", "json " + result);
//            } catch( IOException e ) {
//                e.printStackTrace();
//            }
//
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(LatLng result) {
//            cb.onLatLng(result);
//        }
//
//        @Override
//        protected void onCancelled(LatLng result) {
//            Log.e("NameToLatLng", "cancelled");
//            cb.onLatLng(null);
//        }
//    }


    private GoogleMap gMap;
    private Context context;

    public MapHolder(Context ctx) {
        context = ctx;
    }

    public boolean warnIfNotReady() {
        if (gMap == null) {
            Toast.makeText(context, "No map yet.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }

    public void showAddress(LatLng address) {
        if (warnIfNotReady())
            return;
                //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
//        MarkerOptions options = new MarkerOptions()
//                .position(address)
//                .title("I am here!");
        gMap.addMarker(new MarkerOptions().position(address));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(address));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

}
