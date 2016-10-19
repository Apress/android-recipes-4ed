package com.examples.mylocation;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SupportErrorDialogFragment;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends ActionBarActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private static final String TAG = "AndroidRecipes";

    private static final int UPDATE_INTERVAL = 15 * 1000;
    private static final int FASTEST_UPDATE_INTERVAL = 2 * 1000;

    /* Client interface to Play Services */
    private LocationClient mLocationClient;
    /* Metadata about updates we want to receive */
    private LocationRequest mLocationRequest;
    /* Last-known device location */
    private Location mCurrentLocation;
    
    private TextView mLocationView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationView = new TextView(this);
        setContentView(mLocationView);

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

        //Add location updates monitoring
        mLocationClient = new LocationClient(this, this, this);

        mLocationRequest = LocationRequest.create()
                //Set the required accuracy level
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                //Set the desired (inexact) frequency of location updates
                .setInterval(UPDATE_INTERVAL)
                //Throttle the max rate of update requests
                .setFastestInterval(FASTEST_UPDATE_INTERVAL);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        //When we move into the foreground, attach to Play Services
        mLocationClient.connect();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        //Disable updates when we are not in the foreground
        if (mLocationClient.isConnected()) {
            mLocationClient.removeLocationUpdates(this);
        }
        //Detach from Play Services
        mLocationClient.disconnect();
    }
    
    private void updateDisplay() {
        if(mCurrentLocation == null) {
            mLocationView.setText("Determining Your Location...");
        } else {
            mLocationView.setText(String.format("Your Location:\n%.2f, %.2f",
                    mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude()));
        }
    }

    /** Play Services Location */

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
                    "Location Updates");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to Play Services");
        //Get last known location immediately
        mCurrentLocation = mLocationClient.getLastLocation();
        //Register for updates
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    @Override
    public void onDisconnected() { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    /** LocationListener Callbacks */

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Received location update");
        mCurrentLocation = location;
        updateDisplay();
    }
}