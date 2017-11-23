package com.smartstorage.mobile;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.smartstorage.mobile.db.DatabaseHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kasun on 9/29/17.
 */

public class DeleteFilesActivity extends BroadcastReceiver {

    public static  boolean isDeleting=false;

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHandler db=DatabaseHandler.getDbInstance(context);
        ArrayList<String> arl=db.getListOfFilesToBeDeleted();
        Log.d("delete file list:..",String.valueOf(arl.size()));
//        int listSize=arl.size();
        DeleteFilesActivity.isDeleting=true;
        int listSize=3;
        File file = new File("/storage/emulated/0/Prefetch/Pic1.jpg");

        boolean d=file.delete();

        File f = new File("/storage/emulated/0/Demo/Profile.jpg");
        f.delete();
          Log.i("Deleting elements",String.valueOf(d)+context.getFilesDir());
        Intent scanIntent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(scanIntent);
//        for(int i=0;i<listSize;i++){
//            Log.i("Deleted File names...:",arl.get(i));
//            db.updateDeletedState(arl.get(i));
//
//
//        }
        File newFile = new File("/storage/emulated/0/Prefetch/Pic1.jpg");
        db.updateDeletedState("/storage/emulated/0/Prefetch/Pic1.jpg");



        File newFile1 = new File("/storage/emulated/0/Demo/Profile.jpg");
        if(!newFile1.exists()){
            try{
                newFile1.createNewFile();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_folder);
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
        new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };
        try{
            newFile.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }
        DeleteFilesActivity.isDeleting=false;
    }
}
