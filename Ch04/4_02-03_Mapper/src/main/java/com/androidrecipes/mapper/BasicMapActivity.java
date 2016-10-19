package com.androidrecipes.mapper;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.RadioGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SupportErrorDialogFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class BasicMapActivity extends FragmentActivity implements
        RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "AndroidRecipes";

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Verify play services is active and up to date
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        switch (resultCode) {
            case ConnectionResult.SUCCESS:
                Log.d(TAG, "Google Play Services is ready to go!");
                break;
            default:
                showPlayServicesError(resultCode);
                return;
        }
        
        mMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = mMapFragment.getMap();
        
        //Quickly see if our last known user location is valid, and center
        // the map around that point.  If not, use a default location.
        LocationManager manager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location =
                manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        LatLng mapCenter;
        if(location != null) {
            mapCenter = new LatLng(location.getLatitude(),
                    location.getLongitude());
        } else {
            //Use a default location
            mapCenter = new LatLng(37.4218,  -122.0840);
        }
        
        //Center and zoom the map simultaneously
        CameraUpdate newCamera = CameraUpdateFactory.newLatLngZoom(mapCenter, 13);
        mMap.moveCamera(newCamera);
        
        // Wire up the map type selector UI
        RadioGroup typeSelect = (RadioGroup) findViewById(R.id.group_maptype);
        typeSelect.setOnCheckedChangeListener(this);
        typeSelect.check(R.id.type_normal);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null) {
            //Enable user location display on the map
            mMap.setMyLocationEnabled(true);
        }
    }
    
    @Override
    public void onPause() {
        super.onResume();
        if (mMap != null) {
            //Disable user location when not visible
            mMap.setMyLocationEnabled(false);
        }
    }

    /*
     * When Play Services is missing or at the wrong version, the client
     * library will assist with a dialog to help the user update.
     */
    private void showPlayServicesError(int errorCode) {
        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                errorCode,
                this,
                1000 /* RequestCode */);
        // If Google Play services can provide an error dialog
        if (errorDialog != null) {
            // Create a new DialogFragment for the error dialog
            SupportErrorDialogFragment errorFragment = SupportErrorDialogFragment.newInstance(errorDialog);
            // Show the error dialog in the DialogFragment
            errorFragment.show(
                    getSupportFragmentManager(),
                    "Google Maps");
        }
    }

    /** OnCheckedChangeListener Methods */
    
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.type_satellite:
                //Show the satellite map view
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.type_normal:
            default:
                //Show the normal map view
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
    }
}