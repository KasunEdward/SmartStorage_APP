package com.smartstorage.mobile;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kasun on 9/29/17.
 */

public class DeleteFilesActivity extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<String> arl=intent.getStringArrayListExtra("deletingList");
        int listSize=arl.size();

        File file = new File("/storage/emulated/0/DCIM/Camera/test.jpg");
        Log.i("Deleting elements",String.valueOf(arl.size()));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_cast_dark);
        String str=String.valueOf(listSize)+" files were deleted";
        String subStr="100 MB cleared";
        mBuilder.setContentTitle(str);
        mBuilder.setContentText(subStr);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

// notificationID allows you to update the notification later on.
        mNotificationManager.notify(0, notification);
        if (!file.exists()) {
            try {
//                file.createNewFile();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
