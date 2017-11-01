package com.smartstorage.mobile.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.smartstorage.mobile.AppParams;
import com.smartstorage.mobile.db.DatabaseHandler;
import com.smartstorage.mobile.db.FileDetails;
import com.smartstorage.mobile.util.SSFileObserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MigrationValueUpdateAlarm extends BroadcastReceiver {

    public static final int ALARM_ID = 130209;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("MIGRATION_VALUE_UPDATE", "Updating migration values");
        DatabaseHandler db = DatabaseHandler.getDbInstance(context.getApplicationContext());
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppParams.PreferenceStr.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        long lastUpdate = sharedPreferences.getLong(AppParams.PreferenceStr.LAST_MIGRATION_VAL_UPDATE, 0);
        sharedPreferences.edit().putLong(AppParams.PreferenceStr.LAST_MIGRATION_VAL_UPDATE, System.currentTimeMillis()).commit();
        if (lastUpdate == 0){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Log.i("MIGRATION_VALUE_UPDATE", "firstUpdate " + calendar.getTime().toString());
            lastUpdate = calendar.getTimeInMillis();
        }
        ArrayList<SSFileObserver> fileObserverList = MigrationService.fileObserverList;
        for (SSFileObserver fileObserver : fileObserverList){
            HashMap<String, FileDetails> children = fileObserver.getChildren();
            for (String path : children.keySet()){
                FileDetails fileDetails = children.get(path);
                if (fileDetails.getSize() > 0) {
                    double currentMigrationVal = fileDetails.getMigration_value();
                    double migrationUpdate = (AppParams.MIGRATION_X / fileDetails.getSize()) * AppParams.MIGRATION_FACTOR;
                    if (fileDetails.getLast_accessed() > lastUpdate) {  // file has accessed, increase migration value
                        fileDetails.setMigration_value(currentMigrationVal + migrationUpdate);
                    }else{
                        fileDetails.setMigration_value(currentMigrationVal - migrationUpdate);
                    }
                    db.updateMigrationValue(fileDetails);
                }

            }
        }
    }

    public static boolean isAlarmSet(Context context) {
        return PendingIntent.getBroadcast(context, ALARM_ID,
                new Intent(AppParams.MIGRATION_VALUE_UPDATE_INTENT), PendingIntent.FLAG_NO_CREATE) != null;
    }

    public static void createAlarm(Context context) {
        Log.i("ALARM_CREATE", "Setting alarm to update migration values");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AppParams.MIGRATION_VALUE_UPDATE_INTENT);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // fire alarm everyday the midnight to update variables, upload data
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES/5, alarmIntent);
    }

}
