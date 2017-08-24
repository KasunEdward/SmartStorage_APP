package com.smartstorage.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.smartstorage.mobile.util.SSFileObserver;

import java.util.ArrayList;

public class MigrationService extends Service {

    public static final String LOG_TAG = "SS_MigrationService";
    public static boolean running = false;
    public static ArrayList<SSFileObserver> fileObserverList = new ArrayList<>();

    public MigrationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        running = true;
    }
}
