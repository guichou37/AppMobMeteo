package com.example.appweb.myapplication;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double latitude = 0;
    private double longitude = 0;
    private LocationRequest mLocationRequest = new LocationRequest();
    private Location mylocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // création du client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //création de la map
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);


        /* Add a marker in mylocation and move the camera
        LatLng mylocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(mylocation).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation)); */
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude=mLastLocation.getLatitude();
            longitude=mLastLocation.getLongitude();
            TextView latitudetest= (TextView) findViewById(R.id.mLatitudeText);
            latitudetest.setText(String.valueOf(latitude));
            TextView longitudetest= (TextView)findViewById(R.id.mLongitudeText);
            longitudetest.setText(String.valueOf(longitude));
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        latitude=mylocation.getLatitude();
        longitude=mylocation.getLongitude();
        TextView latitudetest= (TextView) findViewById(R.id.mLatitudeText);
        latitudetest.setText(String.valueOf(latitude));
        TextView longitudetest= (TextView)findViewById(R.id.mLongitudeText);
        longitudetest.setText(String.valueOf(longitude));

        LatLng movelocation = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(movelocation));
    }
}
