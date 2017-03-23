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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private LocationRequest pollingLocationRequest;
    private GoogleApiClient googleApiClient;

    private LatLng currentLocation;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Marker currentLocationMarker;
    private ArrayList<Marker> bathroomMarkers;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        bathroomMarkers = new ArrayList<>();
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

        mMap.setInfoWindowAdapter(buildInfoWindowAdapter());

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getId() != currentLocationMarker.getId()) {
                    Intent intent = new Intent(MapsActivity.this, PreviewBathroomActivity.class);
                    Bathroom bathroom = (Bathroom)marker.getTag();
                    if (bathroom != null) {
                        intent.putExtra("bathroom", bathroom);
                        startActivity(intent);
                    }
                }
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
            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
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
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        getBathrooms();
    }

    private void getBathrooms() {
        Log.i("getBathrooms", "executing");
        RetrieveBathrooms getTask = new RetrieveBathrooms(BASE_API_URL + "bathroom/?lat=" + currentLocation.latitude + "&long=" + currentLocation.longitude + "&radius=3000", RequestType.GET, this);
        getTask.execute();
    }

    private void clearBathroomMarkers() {
        for (Marker marker : bathroomMarkers) {
            marker.remove();
        }
        bathroomMarkers.clear();
    }

    public void bathroomCallback(Object result) {
        ArrayList<Bathroom> bathroomList = (ArrayList<Bathroom>)result;
        for (Bathroom curr : bathroomList) {
            MarkerOptions options = new MarkerOptions();
            options.position(curr.getLocation());
            options.title(curr.getName());
            if (curr.getReviews().size() != 0) {
                double avgCleanliness = 0;
                double avgAccessibility = 0;
                double avgAvailability = 0;
                for (Review review : curr.getReviews()) {
                    avgCleanliness += review.getCleanliness();
                    avgAccessibility += review.getAccessibility();
                    avgAvailability += review.getAccessibility();
                }
                avgCleanliness /= curr.getReviews().size();
                avgAccessibility /= curr.getReviews().size();
                avgAvailability /= curr.getReviews().size();
                String snippet = "Cleanliness: ";
                for (int i = 0; i < (int)avgCleanliness; i++) {
                    snippet += "\uD83D\uDEBD ";
                }
                snippet += "\nAccessibility: ";
                for (int i = 0; i < (int)avgAccessibility; i++) {
                    snippet += "\uD83D\uDEBD ";
                }
                snippet += "\nAvailability: ";
                for (int i = 0; i < (int)avgAvailability; i++) {
                    snippet += "\uD83D\uDEBD ";
                }
                Log.i("snippet", snippet);
                options.snippet(snippet);
            } else {
                options.snippet("No Reviews");
            }
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            Marker newMarker = mMap.addMarker(options);
            newMarker.setTag(curr);
            bathroomMarkers.add(newMarker);
        }
    }

    private void openNewBathroomActivity() {
        Intent intent = new Intent(this, NewBathroomActivity.class);
        intent.putExtra("latitude", currentLocation.latitude);
        intent.putExtra("longitude", currentLocation.longitude);
        intent.putExtra("baseApiUrl", BASE_API_URL);
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

    private GoogleMap.InfoWindowAdapter buildInfoWindowAdapter() {
        return new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window_bathroom, null);
                Bathroom curr = (Bathroom)marker.getTag();
                if (curr == null) {
                    return null;
                }
                TextView name_preview = (TextView)v.findViewById(R.id.bathroom_name_preview);
                TextView ratings_preview = (TextView)v.findViewById(R.id.ratings_preview);
                TextView hours_preview = (TextView)v.findViewById(R.id.open_status_preview);
                TextView req_purchase = (TextView)v.findViewById(R.id.requires_purchase_preview);
                name_preview.setText(marker.getTitle());
                ratings_preview.setText(marker.getSnippet());
                if (curr.getRequiresPurchase()) {
                    req_purchase.setText("Requires Purchase");
                } else {
                    req_purchase.setHeight(0);
                }

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                Hours currHours = curr.getHours()[day];
                if (currHours != null) {
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int min = calendar.get(Calendar.MINUTE);
                    double combo = hour + (min/100);
                    if (currHours.getOpen() <= combo && currHours.getClose() >= combo) {
                        hours_preview.setText("Open Now");
                        hours_preview.setTextColor(getResources().getColor(R.color.colorGreen));
                    } else {
                        hours_preview.setText("Closed Now");
                        hours_preview.setTextColor(getResources().getColor(R.color.colorRed));
                    }
                } else {
                    hours_preview.setHeight(0);
                }

                return v;
            }
        };
    }
}
