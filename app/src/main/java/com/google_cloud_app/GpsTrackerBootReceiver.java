package com.google_cloud_app;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

public class GpsTrackerBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, GPSTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,gpsTrackerIntent,0);

        SharedPreferences sharedPreferences = context.getSharedPreferences(LocationView.APPPREF, Context.MODE_PRIVATE);
        int intervalInMinutes = sharedPreferences.getInt(LocationView.INTERVALINMINUTES,1 );
        Boolean currentlyTracking = sharedPreferences.getBoolean(LocationView.CURRENTKYTRACKING,false);

        if (currentlyTracking){
            alarmManager.setRepeating(alarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),intervalInMinutes*60000,pendingIntent);
        }else
            alarmManager.cancel(pendingIntent);

    }
}
