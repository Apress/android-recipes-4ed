
package com.androidrecipes.regionmonitor;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;

import java.util.ArrayList;

public class MainActivity extends Activity implements
        OnSeekBarChangeListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationClient.OnAddGeofencesResultListener,
        LocationClient.OnRemoveGeofencesResultListener {
    private static final String TAG = "RegionMonitorActivity";
    //Unique identifier for our single geofence
    private static final String FENCE_ID = "com.androidrecipes.FENCE";
    
    private LocationClient mLocationClient;
    private SeekBar mRadiusSlider;
    private TextView mStatusText, mRadiusText;
    
    private Geofence mCurrentFence;
    private Intent mServiceIntent;
    private PendingIntent mCallbackIntent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Wire up the UI connections
        mStatusText = (TextView) findViewById(R.id.status);
        mRadiusText = (TextView) findViewById(R.id.radius_text);
        mRadiusSlider = (SeekBar) findViewById(R.id.radius);
        mRadiusSlider.setOnSeekBarChangeListener(this);
        updateRadiusDisplay();
        
        //Check if Google Play Services is up to date.
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
            case ConnectionResult.SUCCESS:
                //Do nothing, move on
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(this,
                        "Geofencing service requires an update, please open Google Play.",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            default:
                Toast.makeText(this,
                        "Geofencing service is not available on this device.",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
        }
        //Create a client for Google Services
        mLocationClient = new LocationClient(this, this, this);
        //Create an Intent to trigger our service
        mServiceIntent = new Intent(this, RegionMonitorService.class);
        //Create a PendingIntent for Google Services to use with callbacks
        mCallbackIntent = PendingIntent.getService(this, 0, mServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Connect to all services
        if (!mLocationClient.isConnected()
                && !mLocationClient.isConnecting()) {
            mLocationClient.connect();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        //Disconnect when not in the foreground
        mLocationClient.disconnect();
    }
    
    public void onSetGeofenceClick(View v) {
        //Obtain the last location from services and radius
        // from the UI
        Location current = mLocationClient.getLastLocation();
        int radius = mRadiusSlider.getProgress();
        
        //Create a new Geofence using the Builder
        Geofence.Builder builder = new Geofence.Builder();
        mCurrentFence = builder
            //Unique to this geofence
            .setRequestId(FENCE_ID)
            //Size and location
            .setCircularRegion(
                current.getLatitude(),
                current.getLongitude(),
                radius)
            //Events both in and out of the fence
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                    | Geofence.GEOFENCE_TRANSITION_EXIT)
            //Keep alive
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build();
        
        mStatusText.setText(String.format("Geofence set at %.3f, %.3f",
                current.getLatitude(), current.getLongitude()) );
    }
    
    public void onStartMonitorClick(View v) {
        if (mCurrentFence == null) {
            Toast.makeText(this, "Geofence Not Yet Set",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        
        //Add the fence to start tracking, the PendingIntent will
        // be triggered with new updates
        ArrayList<Geofence> geofences = new ArrayList<Geofence>();
        geofences.add(mCurrentFence);
        mLocationClient.addGeofences(geofences, mCallbackIntent, this);
    }
    
    public void onStopMonitorClick(View v) {
        //Remove to stop tracking
        mLocationClient.removeGeofences(mCallbackIntent, this);
    }
    
    /** SeekBar Callbacks */
    
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        updateRadiusDisplay();
    }
    
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }
    
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }
    
    private void updateRadiusDisplay() {
        mRadiusText.setText(mRadiusSlider.getProgress() + " meters");
    }
    
    /** Google Services Connection Callbacks */
    
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v(TAG, "Google Services Connected");
    }
    
    @Override
    public void onDisconnected() {
        Log.w(TAG, "Google Services Disconnected");
    }
    
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.w(TAG, "Google Services Connection Failure");
    }
    
    /** LocationClient Callbacks */
    
    /*
     * Called when the asynchronous geofence add is complete.
     * When this happens we start our monitoring service.
     */
    @Override
    public void onAddGeofencesResult(int statusCode,
            String[] geofenceRequestIds) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            Toast.makeText(this, "Geofence Added Successfully", Toast.LENGTH_SHORT).show();
        }
        
        Intent startIntent = new Intent(mServiceIntent);
        startIntent.setAction(RegionMonitorService.ACTION_INIT);
        startService(mServiceIntent);
    }

    /*
     * Called when the asynchronous geofence remove is complete.
     * The version called depends on whether you requested the
     * removal via PendingIntent or request Id.
     * When this happens we stop our monitoring service.
     */
    @Override
    public void onRemoveGeofencesByPendingIntentResult(
            int statusCode, PendingIntent pendingIntent) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            Toast.makeText(this, "Geofence Removed Successfully",
                    Toast.LENGTH_SHORT).show();
        }
        
        stopService(mServiceIntent);
    }
    
    @Override
    public void onRemoveGeofencesByRequestIdsResult(
            int statusCode, String[] geofenceRequestIds) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            Toast.makeText(this, "Geofence Removed Successfully",
                    Toast.LENGTH_SHORT).show();
        }
        
        stopService(mServiceIntent);
    }
}
