package com.example.cik.mymapss;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private FloatingActionButton Fbtn;

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    public ArrayList<String> strPoluch;
    private GoogleMap mMap;


    protected Button onOfloc;
    protected Button ofloc;

    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected Location mCurrentLocation;
    protected Boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;
    protected static final String TAG = "MainActivity";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";


   ///foursquare
   ArrayList<FoursquareVenue> venuesList;

    // the foursquare client_id and the client_secret

    // ============== YOU SHOULD MAKE NEW KEYS ====================//
    final String CLIENT_ID = "W0WYDFUGB4Y03OEBGNZMRCZ3JDAH1KNUB4OLPN3GKHTBX5J5";
    final String CLIENT_SECRET = "NDO5XXIBEBMPCO1YJGLAY5RUUYVD2KOT14Y1GHOTYUZJVRIP";

    // we will need to take the latitude and the logntitude from a certain point
    // this is the center of New York
    public String latitude = "1.985678";
    public String longtitude = "1.233631";
    public String query="куку";

    ArrayAdapter<String> myAdapter;

boolean forsqmark=false;

////foursq

     public String qqqqq="macdonalds";
   public String qqqqq1="дом";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fbtn = (FloatingActionButton) findViewById(R.id.fab);
       // onOfloc = (Button) findViewById(R.id.loconof);
      //  ofloc = (Button) findViewById(R.id.of);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
       /* map.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Toast toast = Toast.makeText(this, "Hello Android 7",Toast.LENGTH_LONG);
                toast.show();

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
            }
        });
*/

        updateValuesFromBundle(savedInstanceState);

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

    }

    public void onClick12(View view) {
        CheckVoiceRecognition();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

public void onClickFors(View view){
    query=qqqqq1;
    new fourquare().execute();
    updateLocationUI();

}

    public void CheckVoiceRecognition() {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            Fbtn.setEnabled(false);
            Toast.makeText(this, "Voice recognizer not present", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ArrayList<String> textMatchlist = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (!textMatchlist.isEmpty()) {
                if (textMatchlist.get(0).contains("search")) {
                    String searchQuery = textMatchlist.get(0).replace("search", " ");
                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    search.putExtra(SearchManager.QUERY, searchQuery);
                    startActivity(search);
                } else {
                    strPoluch = textMatchlist;
                    Toast.makeText(this, strPoluch.get(0), Toast.LENGTH_LONG).show();
                   // qqqqq1=strPoluch.get(0);
                    query=strPoluch.get(0);
                  //  Toast.makeText(this, query+" проверка записи", Toast.LENGTH_LONG).show();
                    new fourquare().execute();
                    updateLocationUI();
                  /*  if(strPoluch.get(0)!=null|strPoluch.get(0)!=""){
                    qqqqq1=strPoluch.get(0);
                    Toast.makeText(this, qqqqq1+"qqqqq", Toast.LENGTH_LONG).show();}
*/

                }
            }
        } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
            showToastMessage("Audio Error");

        } else if ((resultCode == RecognizerIntent.RESULT_CLIENT_ERROR)) {
            showToastMessage("Client Error");

        } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
            showToastMessage("Network Error");
        } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
            showToastMessage("No Match");
        } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
            showToastMessage("Server Error");
        }



        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateUI();
                        break;
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
       // LatLng sydney = new LatLng(10, 10);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("You are here"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateLocationUI();


    }


    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks( this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void updateLocationUI() {
        if (mCurrentLocation != null) {

            Toast.makeText(this,(Double.toString(mCurrentLocation.getLatitude()) + ":" +
                    Double.toString(mCurrentLocation.getLongitude())+" ::"+mLastUpdateTime), Toast.LENGTH_LONG).show();

            latitude=Double.toString(mCurrentLocation.getLatitude());
            longtitude=Double.toString(mCurrentLocation.getLongitude());
            LatLng sydney = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
          //  mMap.addMarker(new MarkerOptions().position(sydney).title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12));
            mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("You are here"));

       //   new fourquare().execute();
/*
            LatLng sydney12 = new LatLng(Double.parseDouble(venuesList.get(0).getLatitude()),
                    Double.parseDouble(venuesList.get(0).getLongtitude()));
            mMap.addMarker(new MarkerOptions().position(sydney1).title(venuesList.get(0).getName()));*/
               // LatLng sydney12 = new LatLng(Double.parseDouble(venuesList.get(0).getLatitude()),
                    //    Double.parseDouble(venuesList.get(0).getLongtitude()));
               // mMap.addMarker(new MarkerOptions().position(sydney12).title(venuesList.get(0).getName()));

            // LatLng sydney12 = new LatLng(12,
                 //   12);
               //  mMap.addMarker(new MarkerOptions().position(sydney12).title(venuesList.get(0).getName()));

        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    public void onClickonof(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            setButtonsEnabledState();
            startLocationUpdates();
        }
    }
    public void onClickof(View view) {
        stopLocationUpdates();
    }
    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            onOfloc.setEnabled(false);
            ofloc.setEnabled(true);
        } else {
            onOfloc.setEnabled(true);
            ofloc.setEnabled(false);
        }
    }

    protected void startLocationUpdates() {
        LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");

/*
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, MainActivity.this);
*/
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Log.e(TAG, errorMessage);
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = false;
                }
                updateUI();
            }
        });

    }

private void updateUI() {
    updateLocationUI();
}
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        updateUI();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;

            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateLocationUI();
        }
        if (mRequestingLocationUpdates) {
            Log.i(TAG, "in onConnected(), starting location updates");
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }

   ///////foursquare
   private class fourquare extends AsyncTask<View, Void, String> {

       String temp;

       @Override
       protected String doInBackground(View... urls) {
          // query=qqqqq1;
           // make Call to the url
           temp = makeCall("https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
                   "&v=20130815"+ "&query="+query+"&ll="+latitude+","+longtitude);
           return "";
       }

       @Override
       protected void onPreExecute() {
           // we can start a progress bar here
       }

       @Override
       protected void onPostExecute(String result) {
           if (temp == null) {
               // we have an error to the call
               // we can also stop the progress bar
           } else {
               // all things went right

               // parseFoursquare venues search result
               venuesList = (ArrayList<FoursquareVenue>) parseFoursquare(temp);

               List<String> listTitle = new ArrayList<String>();

               for (int i = 0; i < venuesList.size(); i++) {
                   // make a list of the venus that are loaded in the list.
                   // show the name, the category and the city
                   listTitle.add(i, venuesList.get(i).getName() + ", " + venuesList.get(i).getCategory() + ", "
                           + venuesList.get(i).getCity()+", "+venuesList.get(i).getLatitude()+", "+venuesList.get(i).getLongtitude());
               }
               Toast.makeText(MainActivity.this, venuesList.get(0).getLatitude(), Toast.LENGTH_LONG).show();
              for(int i=0;i<listTitle.size();i++) {
                  if (venuesList.get(i).getName() != null | venuesList.get(i).getName() != "") {
                      LatLng sydney12 = new LatLng(Double.parseDouble(venuesList.get(i).getLatitude()),
                              Double.parseDouble(venuesList.get(i).getLongtitude()));
/*
                      LatLng sydney12 = new LatLng(i+1.3,
                              i+1.3);
*/
                      mMap.addMarker(new MarkerOptions().position(sydney12).title(venuesList.get(i).getName()));
                  }
              }
              /* LatLng sydney12 = new LatLng(Double.parseDouble(venuesList.get(0).getLatitude()),
                       Double.parseDouble(venuesList.get(0).getLongtitude()));


               mMap.addMarker(new MarkerOptions().position(sydney12).title(venuesList.get(0).getName()));
*/



           }
       }
   }

    public static String makeCall(String url) {

        // string buffers the url
        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";

        // instanciate an HttpClient
        HttpClient httpclient = new DefaultHttpClient();
        // instanciate an HttpGet
        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {
            // get the responce of the httpclient execution of the url
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();

            // buffer input stream the result
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // the result as a string is ready for parsing
            replyString = new String(baf.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // trim the whitespaces
        return replyString.trim();
    }

    private ArrayList<FoursquareVenue> parseFoursquare(final String response) {

        ArrayList<FoursquareVenue> temp = new ArrayList<FoursquareVenue>();
        try {

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);

            // make an jsonObject in order to parse the response
            if (jsonObject.has("response")) {
                if (jsonObject.getJSONObject("response").has("venues")) {
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("venues");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        FoursquareVenue poi = new FoursquareVenue();

                        if (jsonArray.getJSONObject(i).has("name")) {
                            poi.setName(jsonArray.getJSONObject(i).getString("name"));

                            if (jsonArray.getJSONObject(i).has("location")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("location").has("address")) {
                                    if (jsonArray.getJSONObject(i).getJSONObject("location").has("city")) {
                                        poi.setCity(jsonArray.getJSONObject(i).getJSONObject("location").getString("city"));

                                    }
                                    if (jsonArray.getJSONObject(i).has("categories")) {
                                        if (jsonArray.getJSONObject(i).getJSONArray("categories").length() > 0) {
                                            if (jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).has("icon")) {
                                                poi.setCategory(jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getString("name"));
                                                poi.setLatitude(jsonArray.getJSONObject(i).getJSONObject("location").getString("lat"));
                                                poi.setLongtitude(jsonArray.getJSONObject(i).getJSONObject("location").getString("lng"));
                                            }
                                        }
                                    }
                                    temp.add(poi);

                                }
                            }
                        }

                    }


                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<FoursquareVenue>();
        }
        return temp;


    }

















 ////foursq






}

