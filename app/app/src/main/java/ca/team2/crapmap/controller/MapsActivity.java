package ca.team2.crapmap.controller;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;

import ca.team2.crapmap.R;
import ca.team2.crapmap.adapter.BathroomInfoAdapter;
import ca.team2.crapmap.model.Bathroom;
import ca.team2.crapmap.service.BathroomService;
import ca.team2.crapmap.service.RequestHandler;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener
{

    private LocationRequest pollingLocationRequest;
    private GoogleApiClient googleApiClient;
    private SensorManager sensorManager;

    private LatLng currentLocation;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ArrayList<Marker> bathroomMarkers;
    private long previousShakeTimestamp;

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private static final int NEW_BATHROOM_CREATED = 101;
    private static final int NEW_COMMENT_CREATED = 102;

    private static final double SHAKE_THRESHOLD = 2.75;
    private static final int SHAKE_BUFFER = 500;

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

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
                );
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
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
        mMap.setMyLocationEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
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

        mMap.setInfoWindowAdapter(new BathroomInfoAdapter(this));

        mMap.setOnInfoWindowClickListener(buildInfoWindowClickListener());
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
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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
        //TODO: need to compare locations, if too far apart, refresh bathrooms
        currentLocation = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        getBathrooms();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }

    private void getAccelerometer(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        //gForce will be ~1 when phone is still
        double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD) {
            final long now = System.currentTimeMillis();

            // Ignore shake events that are too close together
            if (previousShakeTimestamp + SHAKE_BUFFER > now) {
                return;
            }

            Toast.makeText(this, "Loading Recommendation", Toast.LENGTH_SHORT)
                    .show();
            Marker closest = getClosestBathroomMarker();
            if (closest != null) {
                loadBathroomPreview(closest);
            }
        }
    }

    private void getBathrooms() {
        Log.i("getBathrooms", "executing");
        final Activity activity = this;
        BathroomService.getBathrooms(activity, "Finding bathrooms near you...", currentLocation, 3000, new RequestHandler<ArrayList<Bathroom>>() {
            @Override
            public void callback(ArrayList<Bathroom> result) {
                if (result == null) {
                    Toast.makeText(activity, "Cannot retrieve data, please try again later", Toast.LENGTH_LONG).show();
                    return;
                }
                ArrayList<Bathroom> bathroomList = result;
                for (Bathroom curr : bathroomList) {
                    MarkerOptions options = new MarkerOptions();
                    options.position(curr.getLocation());
                    options.title(curr.getName());
                    if (!curr.hasAnyHours() || (curr.hasAnyHours() && curr.isOpen())) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                    Marker newMarker = mMap.addMarker(options);
                    newMarker.setTag(curr);
                    bathroomMarkers.add(newMarker);
                }
            }
        });
    }

    private Marker getClosestBathroomMarker() {
        if (currentLocation != null) {
            float dist = Float.MAX_VALUE;
            Marker closest = null;
            for (Marker marker : bathroomMarkers) {
                float[] results = new float[3];
                Location.distanceBetween(
                        currentLocation.latitude,
                        currentLocation.longitude,
                        marker.getPosition().latitude,
                        marker.getPosition().longitude,
                        results);
                Log.i("result dist", marker.getTitle() + ": " + results[0]);
                if (results[0] <= dist) {
                    dist = results[0];
                    closest = marker;
                }
            }
            Log.i("closest", closest.getTitle());
            return closest;
        } else {
            return null;
        }
    }

    private void clearBathroomMarkers() {
        for (Marker marker : bathroomMarkers) {
            marker.remove();
        }
        bathroomMarkers.clear();
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
                    getBathrooms();
                    Bathroom bathroom = (Bathroom)data.getSerializableExtra("response");
                    Intent intent = new Intent(MapsActivity.this, PreviewBathroomActivity.class);
                    Double userLat = currentLocation.latitude;
                    Double userLng = currentLocation.longitude;
                    if (bathroom != null) {
                        intent.putExtra("bathroom", bathroom);
                        intent.putExtra("bathroomLat", userLat);
                        intent.putExtra("bathroomLng", userLng);
                        intent.putExtra("userLat", userLat);
                        intent.putExtra("userLng", userLng);
                        startActivity(intent);
                    }
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

    private GoogleMap.OnInfoWindowClickListener buildInfoWindowClickListener() {
        return new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                loadBathroomPreview(marker);
            }
        };
    }

    private void loadBathroomPreview(Marker marker) {
        Intent intent = new Intent(MapsActivity.this, PreviewBathroomActivity.class);
        Double userLat = currentLocation.latitude;
        Double userLng = currentLocation.longitude;
        Bathroom bathroom = (Bathroom) marker.getTag();
        if (bathroom != null) {
            intent.putExtra("bathroom", bathroom);
            intent.putExtra("bathroomLat", bathroom.getLocation().latitude);
            intent.putExtra("bathroomLng", bathroom.getLocation().longitude);
            intent.putExtra("userLat", userLat);
            intent.putExtra("userLng", userLng);
            startActivity(intent);
        }
    }
}
