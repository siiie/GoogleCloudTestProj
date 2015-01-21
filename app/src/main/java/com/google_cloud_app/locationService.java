package com.google_cloud_app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;

import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class locationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private boolean currentlyProcessingLocation = false;
    LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "LocationService";
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

//    public void sendResult(Location location){
//        Intent intent = new Intent(LocationView.APPRES);
//        if(location!=null){
//            intent.putExtra("locationString", "Lat: "+location.getLatitude() + "Longi "+ location.getLongitude());
//
//        }
//        broadcaster.sendBroadcast(intent);
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }

        return START_NOT_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }
    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        SharedPreferences sharedpreferences = getSharedPreferences(LocationView.APPPREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String lastLocation = sharedpreferences.getString(LocationView.APPLOCATIONS,"");
        lastLocation = lastLocation+ "LAT: "+location.getLatitude()+"LONGI: "+location.getLongitude()+"/n";
        editor.putString(LocationView.APPLOCATIONS,lastLocation);
        Log.d("onLocationChanged: ","LAT: "+location.getLatitude()+"LONGI: "+location.getLongitude());

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    private void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

}
