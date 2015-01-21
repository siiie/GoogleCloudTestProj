package com.google_cloud_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.UUID;


public class LocationView extends ActionBarActivity {

    static public final String INTERVALINMINUTES = "intervalInMinutes";
    static public final String CURRENTKYTRACKING = "currentlyTracking";
    static public final String APPPREF = "com.google_cloud_app.pref";
    static public final String APPRES = "com.google_cloud_app.res";
    static public final String APPLOCATIONS = "com.google_cloud_app.location";
    private boolean currentlyTracking;
    private static final String TAG = "GpsTrackerActivity";

    private AlarmManager alarmManager;
    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;
    private int intervalInMinutes = 1;
    private GcmBroadcastReceiver receiver;


    TextView tvLocation ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);

        tvLocation = (TextView) findViewById(R.id.tvLocation);

        SharedPreferences sharedPreferences = this.getSharedPreferences(APPPREF, Context.MODE_PRIVATE);
        currentlyTracking = sharedPreferences.getBoolean(CURRENTKYTRACKING, false);

        boolean firstTimeLoadindApp = sharedPreferences.getBoolean("firstTimeLoadindApp", true);
        if (firstTimeLoadindApp) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTimeLoadindApp", false);
            editor.putString("appID",  UUID.randomUUID().toString());
            editor.apply();
        }
        setIntervalInMinutes(sharedPreferences);

        receiver = new GcmBroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                String theLocation = intent.getStringExtra("locationString");
                tvLocation.append("/n");
                tvLocation.append(theLocation);
            }
        };
    }

    public void setIntervalInMinutes(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("intervalInMinutes", 1);
        editor.apply();
    }

    private void startAlarmManager() {
        Log.d(TAG, "startAlarmManager");

        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(context, GPSTrackerAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        SharedPreferences sharedPreferences = this.getSharedPreferences(APPPREF, Context.MODE_PRIVATE);
        intervalInMinutes = sharedPreferences.getInt(INTERVALINMINUTES, 1);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                intervalInMinutes * 10000, // 10000 = 10 seconds
                pendingIntent);
    }
    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, GPSTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void stopButton_OnClick(View v) {
        if(currentlyTracking) {
            SharedPreferences sharedPreferences = this.getSharedPreferences(APPPREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            cancelAlarmManager();
            currentlyTracking = false;
            editor.putBoolean(CURRENTKYTRACKING, false);
            editor.apply();
        }
    }

    public void startButton_OnClick(View v) {
        if(!currentlyTracking) {
            SharedPreferences sharedPreferences = this.getSharedPreferences(APPPREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            startAlarmManager();
            currentlyTracking = true;
            editor.putBoolean(CURRENTKYTRACKING, true);
            editor.apply();
        }
    }

    public void getLocationsButton_OnClick(View v){
        SharedPreferences sharedpreferences = getSharedPreferences(LocationView.APPPREF, Context.MODE_PRIVATE);
        String lastLocation = sharedpreferences.getString(LocationView.APPLOCATIONS,"");
        tvLocation.append(lastLocation);
        Log.d("getLocationsButton_OnClick",lastLocation );
    }

}
