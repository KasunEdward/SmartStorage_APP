package com.smartstorage.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MigrationService extends Service {
    public MigrationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
