package com.example.appweb.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;


public class MapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SearchView search;
    //auto défini si les coordonnées GPS doivent être utilisées
    boolean auto = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Geocoder coder = new Geocoder(this);
        //Création du client Google
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        //Test d'activation du GPS du téléphone
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            alerte("Le GPS est désactivé");
            //La désactivation du GPS n'entraine pas d'erreur
        }

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Création de la Map dans le layout
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Affichage et paramétrage du bouton de localisation
        mapFragment.getMap().setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                mPager.setAdapter(mPagerAdapter);
                auto = true;
                search.setIconified(true);
                hideSoftKeyboard();
                return false;
            }
        });

        //region Recherche
        search = (SearchView) findViewById(R.id.searchView);
        //Au moment de la recherche
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //SET Latitude et Longitude manuellement à partir d'un nom de ville
                List<Address> address;

                try {
                    address = coder.getFromLocationName(query,5);
                    if(address.size()!=0){
                        auto = false;
                        Address location=address.get(0);
                        latitude=location.getLatitude();
                        longitude=location.getLongitude();
                        LatLng movelocation = new LatLng(latitude, longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(movelocation));
                    }
                    else{
                        auto = true;
                        alerte("Aucune ville trouvée");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                hideSoftKeyboard();

                //Rechargement des pages avec les nouvelles coordonnées
                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                mPager.setAdapter(mPagerAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //A la sortie de la barre de recherche
        search.setOnCloseListener(new SearchView.OnCloseListener(){
            @Override
            public boolean onClose() {
                auto = true;
                //Rechargement des pages avec les nouvelles coordonnées
                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                mPager.setAdapter(mPagerAdapter);
                //SET Lat and Long back to GPS
                return false;
            }
        });
        //endregion

    }

    @Override
    protected void onResume(){
        super.onResume();
        //Rechargement des pages au retour
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    //region GoogleMap
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double latitude = 6.91667;
    private double longitude = 79.83333;
    private LocationRequest mLocationRequest = new LocationRequest();
    private Location mylocation;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude=mLastLocation.getLatitude();
            longitude=mLastLocation.getLongitude();
        }
        //Demande de localisation du client Google
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        //Rechargement des pages avec les nouvelles coordonnées
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Suivi de changement de position
    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (auto == true) {
            latitude=mylocation.getLatitude();
            longitude= mylocation.getLongitude();
            LatLng movelocation = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(movelocation));
        }
    }
    //endregion

    //region ViewPager

    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private static final int NUM_PAGES =3;

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }
        //Création des fragments du PageViewer avec les numéros de pages et les coordonnées
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return ScreenSlidePageFragment.newInstance(0,latitude,longitude);
                case 1: return ScreenSlidePageFragment.newInstance(1,latitude,longitude);
                case 2: return ScreenSlidePageFragment.newInstance(2,latitude,longitude);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    //endregion

    //Permet de masquer le clavier
    public void hideSoftKeyboard(){
        if(getCurrentFocus()!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    //Permet d'afficher une alerte avec le texte alerttext
    private void alerte (String alerttext){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alerte");
        alertDialog.setMessage(alerttext);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }



}

