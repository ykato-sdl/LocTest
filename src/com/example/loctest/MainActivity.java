package com.example.loctest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static String TAG = "MainActivity";

    private LocationClient locationClient;
    private TextView latitudeView, longitudeView, accuracyView;
    private Button startStopButton;
    
    private boolean isUpdating;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        latitudeView = (TextView) findViewById(R.id.latitude_view);
        longitudeView = (TextView) findViewById(R.id.longitude_view);
        accuracyView = (TextView) findViewById(R.id.accuracy_view);
        startStopButton = (Button) findViewById(R.id.start_stop_button);
        
        isUpdating = false;
        
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000); // 5 sec.
        locationRequest.setFastestInterval(1 * 1000); // 1 sec.

        locationClient = new LocationClient(this, this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        locationClient.connect();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        if (locationClient.isConnected())
            stopPeriodicUpdate();
        locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Log.i(TAG, "onConnectionFailed");
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.i(TAG, "onConnected");
        startStopButton.setEnabled(true);
    }

    @Override
    public void onDisconnected() {
        Log.i(TAG, "onDisconnected");
        startStopButton.setEnabled(false);
    }

    @Override
    public void onLocationChanged(Location arg0) {
        showLocation();
    }
    
    public void onClickStartStopButton(View view) {
        if (!isUpdating)
            startPeriodicUpdate();
        else
            stopPeriodicUpdate();
    }
    
    private void startPeriodicUpdate() {
        isUpdating = true;
        startStopButton.setText(getString(R.string.start_stop_button_stop_label));
        locationClient.requestLocationUpdates(locationRequest, this);
    }
    
    private void stopPeriodicUpdate() {
        isUpdating = false;
        startStopButton.setText(getString(R.string.start_stop_button_start_label));
        locationClient.removeLocationUpdates(this);
    }
    
    private void showLocation() {
        Location loc = locationClient.getLastLocation();
        if (loc != null) {
            latitudeView.setText(Double.toString(loc.getLatitude()));
            longitudeView.setText(Double.toString(loc.getLongitude()));
            accuracyView.setText(Float.toString(loc.getAccuracy()));
        }
    }

}
