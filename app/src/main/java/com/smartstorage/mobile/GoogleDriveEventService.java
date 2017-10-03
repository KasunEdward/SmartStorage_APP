package com.smartstorage.mobile;

import android.app.Service;
import android.util.Log;

import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;

/**
 * Created by kasun on 10/2/17.
 */

public class GoogleDriveEventService extends DriveEventService {

    @Override
    public void onCompletion(CompletionEvent event){
        Log.d("drive success.....:", "Action completed with status: " + event.getStatus());

        // handle completion event here.

        event.dismiss();
    }
}
