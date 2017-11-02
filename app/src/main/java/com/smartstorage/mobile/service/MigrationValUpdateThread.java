package com.smartstorage.mobile.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.smartstorage.mobile.AppParams;
import com.smartstorage.mobile.db.DatabaseHandler;
import com.smartstorage.mobile.db.FileDetails;
import com.smartstorage.mobile.util.SSFileObserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Irfad Hussain on 11/1/2017.
 */

public class MigrationValUpdateThread extends Thread {

    public static boolean running = false;

    private Context context;

    public MigrationValUpdateThread(Context context){
        this.context = context;
    }

    @Override
    public void run() {
        while(true) {
            running = true;
            try {
                Thread.sleep(60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("MIGRATION_VALUE_UPDATE", "Updating migration values");
            DatabaseHandler db = DatabaseHandler.getDbInstance(context.getApplicationContext());
            SharedPreferences sharedPreferences = context.getSharedPreferences(AppParams.PreferenceStr.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
            long lastUpdate = sharedPreferences.getLong(AppParams.PreferenceStr.LAST_MIGRATION_VAL_UPDATE, 0);
            sharedPreferences.edit().putLong(AppParams.PreferenceStr.LAST_MIGRATION_VAL_UPDATE, System.currentTimeMillis()).commit();
            if (lastUpdate == 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Log.i("MIGRATION_VALUE_UPDATE", "firstUpdate " + calendar.getTime().toString());
                lastUpdate = calendar.getTimeInMillis();
            }
            ArrayList<SSFileObserver> fileObserverList = MigrationService.fileObserverList;
            for (SSFileObserver fileObserver : fileObserverList) {
                HashMap<String, FileDetails> children = fileObserver.getChildren();
                for (String path : children.keySet()) {
                    FileDetails fileDetails = children.get(path);
                    if (fileDetails.getSize() > 0) {
                        double currentMigrationVal = fileDetails.getMigration_value();
                        double migrationUpdate = (AppParams.MIGRATION_X / fileDetails.getSize()) * AppParams.MIGRATION_FACTOR;
                        if (fileDetails.getLast_accessed() > lastUpdate) {  // file has accessed, increase migration value
                            fileDetails.setMigration_value(currentMigrationVal + migrationUpdate);
                        } else {
                            fileDetails.setMigration_value(currentMigrationVal - migrationUpdate);
                        }
                        db.updateMigrationValue(fileDetails);
                    }

                }
            }
        }
    }

    protected void finalize() throws Throwable {
        running = false;
        super.finalize();
     }

}
