package ca.team2.crapmap;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private LocationRequest pollingLocationRequest;
    private GoogleApiClient googleApiClient;

    private LatLng currentLocation;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Marker currentLocationMarker;

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private static final int NEW_BATHROOM_CREATED = 101;
    private static final int NEW_COMMENT_CREATED = 102;

    private static final String BASE_API_URL = "https://crap-map-server.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewBathroomActivity();
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, PreviewBathroomActivity.class);
                //will pass database ID or something
                //could also pass full serialized content to reduce requests to server
                //intent.putExtra("bathroom", marker.getSomething());
                startActivity(intent);
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    /*
     * Executed when googleApiClient successfully connects to the API
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("googleApiClient","onConnected executing");

        Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastKnownLocation != null) {
            currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }

        pollingLocationRequest = new LocationRequest();
//        pollingLocationRequest.setInterval(1000000);
//        pollingLocationRequest.setFastestInterval(100000);
        pollingLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,  pollingLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //do something?
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //show an error probably
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        mMap.clear();
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        //need to compare locations, if too far apart, refresh bathrooms
        currentLocation = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        getBathrooms();
    }

    private void getBathrooms() {
        Log.i("getBathrooms", "executing");
        RetrieveBathrooms getTask = new RetrieveBathrooms(BASE_API_URL + "bathroom/?lat=" + currentLocation.latitude + "&long=" + currentLocation.longitude + "&radius=3000", RequestType.GET, this);
        getTask.execute();
    }

//    private void clearBathroomMarkers() {
//        for (Marker marker : bathroomMarkers) {
//            marker.remove();
//        }
//        bathroomMarkers.clear();
//    }

    public void bathroomCallback(Object result) {
        ArrayList<Bathroom> bathroomList = (ArrayList<Bathroom>)result;
        for (Bathroom curr : bathroomList) {
            MarkerOptions options = new MarkerOptions();
            options.position(curr.getLocation());
            options.title(curr.getName());
            if (curr.getReviews().size() != 0) {
                double avgReview = 0;
                for (Review review : curr.getReviews()) {
                    avgReview += review.getStars();
                }
                avgReview /= curr.getReviews().size();
                String snippet = "";
                for (int i = 0; i < (int)avgReview; i++) {
                    snippet += "★ ";
                }
                for (int i = 0; i < (5 - (int)avgReview); i++) {
                    snippet += "☆ ";
                }
                //options.snippet("Avg Rating: " + (int)avgReview + " stars");
                options.snippet(snippet);
            } else {
                options.snippet("No Reviews");
            }
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            //need to somehow get these markers into a list
            mMap.addMarker(options);
        }
    }

    private void openNewBathroomActivity() {
        Intent intent = new Intent(this, NewBathroomActivity.class);
        intent.putExtra("latitude", currentLocation.latitude);
        intent.putExtra("longitude", currentLocation.longitude);
        startActivityForResult(intent, NEW_BATHROOM_CREATED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case(NEW_BATHROOM_CREATED): {
                if (resultCode == Activity.RESULT_OK) {
                    mMap.clear();
                    //TODO: get location again here too, or just make a new marker
                    String name = data.getStringExtra("name");
                    LatLng location = new LatLng(data.getDoubleExtra("latitude", 0),
                            data.getDoubleExtra("longitude", 0));
                    //TODO: create new bathroom with return data
                    getBathrooms();
                } else {
                    //do nothing
                }
                break;
            }
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.permission_title_location))
                        .setMessage(getString(R.string.permission_explanation_location))
                        .setPositiveButton(R.string.permission_confirmation, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_map)
                        .show();

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);


            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i("permission","GRANTED");
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }

                } else {
                    // permission denied, boo!
                    Log.i("permission","DENIED");
                    // throw new SecurityException("Don't deny my permissions, bro!!");
                }
                return;
            }
        }
    }
}
