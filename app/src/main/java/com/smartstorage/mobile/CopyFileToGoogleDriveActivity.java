package com.smartstorage.mobile;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.smartstorage.mobile.db.DatabaseHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by kasun on 9/17/17.
 */

public class CopyFileToGoogleDriveActivity extends BroadcastReceiver{
    private static final String TAG = "Copying to Google Drive";

    private String fileUrl;
    Context context;

    private String GOOGLE_DRIVE_TAG="Google Drive..:";

    private DriveId driveId;
    String driveId_str;

//arraylist of files
    ArrayList<String> coyingFilesList;
//    when this value equals to the arraylist size, Notification will be invoked
    private static int fileCount;
    private static int fileCountInsideCallback=0;
    private static GoogleApiClient mGoogleApiClient;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;

        //    get the Arraylist of files
        coyingFilesList=intent.getStringArrayListExtra("copyingListToGD");
//        mGoogleApiClient=intent.getParcelableExtra("aa");
//        driveId=intent.getParcelableExtra("bb");
        Log.i(GOOGLE_DRIVE_TAG,"copying files");

        for(int i=0;i<coyingFilesList.size();i++){
            fileUrl=coyingFilesList.get(i);
            Drive.DriveApi.newDriveContents(GoogleClientHandler.googleApiClient)
                    .setResultCallback(driveContentsCallback);
        }


//        Drive.DriveApi.newDriveContents(MainActivity.getGoogleApiClient())
//                .setResultCallback(driveContentsCallback);
    }
    ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e(TAG,"Error while trying to create new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                    @Override
                    public void run() {
                        // write content to DriveContents
                        OutputStream outputStream = driveContents.getOutputStream();
                        String url=coyingFilesList.get(fileCountInsideCallback++);
                        Uri resourceUri = Uri.fromFile(new File(url));

                        try {
                            InputStream inputStream = context.getContentResolver().openInputStream(resourceUri);
                            if (inputStream != null) {
                                byte[] data = new byte[1024];
                                while (inputStream.read(data) != -1) {
                                    outputStream.write(data);
                                }
                                inputStream.close();
                            }

                            outputStream.close();
                        } catch (IOException e) {
                            Log.e(GOOGLE_DRIVE_TAG, e.getMessage());
                        }
                        String extension = url.substring(url.indexOf(".") + 1);
                        String fileType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        Log.e(GOOGLE_DRIVE_TAG, fileType);
                        String[] arr = url.split("/");
                        String fileName = arr[arr.length - 1].substring(0, arr[arr.length - 1].indexOf("."));

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(fileName)
                                .setMimeType(fileType)
                                .setStarred(true).build();

                        // create a file in root folder
                        DriveFolder folder = GoogleClientHandler.driveId.asDriveFolder();
                        folder.createFile(GoogleClientHandler.googleApiClient, changeSet, driveContents)
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                    @Override
                                    public void onResult(DriveFolder.DriveFileResult result) {
                                        if (result.getStatus().isSuccess()) {
                                            DriveId Did = result.getDriveFile().getDriveId();
                                            driveId_str = Did.encodeToString();

                                            DatabaseHandler db = DatabaseHandler.getDbInstance(context);
                                            db.updateFileLink(fileUrl, driveId_str,"GoogleDrive");
                                            Log.e("Android exxx:", driveId_str);
                                            fileCount++;
                                            if(fileCount==coyingFilesList.size()-1){
                                                fileCountInsideCallback=0;
                                            }
                                            if(fileCount==coyingFilesList.size()){
                                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                                                mBuilder.setSmallIcon(R.drawable.ic_cast_dark);
                                                String str=String.valueOf(fileCount)+" files were copied to Google Drive";
                                                String subStr="Total 75 MB";
                                                mBuilder.setContentTitle(str);
                                                mBuilder.setContentText(subStr);

                                                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                                                Notification notification = mBuilder.build();

                                                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                                notification.defaults |= Notification.DEFAULT_SOUND;
                                                notification.defaults |= Notification.DEFAULT_VIBRATE;

//                                                 notificationID allows you to update the notification later on.
                                                mNotificationManager.notify(0, notification);
                                            }
                                        }

                                        return;
                                    }
                                });
                    }
                }.start();
                }
            };


}
