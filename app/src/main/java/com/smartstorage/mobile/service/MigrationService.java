package com.smartstorage.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.smartstorage.mobile.util.CommonUtils;
import com.smartstorage.mobile.util.SSFileObserver;

import java.io.File;
import java.util.ArrayList;

public class MigrationService extends Service {

    public static final String LOG_TAG = "SS_MigrationService";
    public static boolean running = false;
    public static ArrayList<SSFileObserver> fileObserverList = new ArrayList<>();

    public MigrationService() {
    }

    public static void addFileObserver(SSFileObserver fileObserver) {
        fileObserverList.add(fileObserver);
    }

    public static void removeFileObserver(SSFileObserver fileObserver) {
        fileObserverList.remove(fileObserver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        running = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (fileObserverList.isEmpty()) {
            File externalStorage = Environment.getExternalStorageDirectory();
            if (externalStorage.exists()) {
                Log.i(LOG_TAG, "Adding file observers for external storage." + externalStorage.getAbsolutePath());
                generateObservers(externalStorage);
            }
            String secondaryStoragePath = System.getenv("SECONDARY_STORAGE");
            if (secondaryStoragePath != null) {
                File secondaryStorage = new File(secondaryStoragePath);
                if (secondaryStorage.exists()) {
                    Log.i(LOG_TAG, "Adding file observers for secondary storage storage " + secondaryStorage.getAbsolutePath());
                    generateObservers(secondaryStorage);
                }
            }
        }
        for (SSFileObserver observer : fileObserverList){
            observer.startWatching();
        }
        Log.i(LOG_TAG, "Starting migration service");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        running = false;

        for (SSFileObserver observer : fileObserverList){
            observer.stopWatching();
        }
    }

    private void generateObservers(File file) {
        if (!CommonUtils.isTempOrCacheFile(file.getAbsolutePath()) && file.isDirectory()) {
            addFileObserver(new SSFileObserver(file));
            File[] childFiles = file.listFiles();
            if (childFiles != null) {
                for (File child : childFiles) {
                    generateObservers(child);
                }
            }
        }
    }
}
