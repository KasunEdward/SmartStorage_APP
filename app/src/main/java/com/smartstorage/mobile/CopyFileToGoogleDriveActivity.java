package com.smartstorage.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

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

/**
 * Created by kasun on 9/17/17.
 */

public class CopyFileToGoogleDriveActivity extends BroadcastReceiver{
    private static final String TAG = "CreateFileActivity";
//    TODO: This should be replaced with an arraylist of files
    private String fileUrl="/storage/emulated/0/Samsung/Music/Over the Horizon.mp3";
    Context context;

    private String GOOGLE_DRIVE_TAG="Google Drive..:";

    private DriveId driveId=MainActivity.getDriveId();
    String driveId_str;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        Log.i(GOOGLE_DRIVE_TAG,"copying files");
        Drive.DriveApi.newDriveContents(MainActivity.getGoogleApiClient())
                .setResultCallback(driveContentsCallback);
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
                        Uri resourceUri = Uri.fromFile(new File(fileUrl));

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
                        String extension = fileUrl.substring(fileUrl.indexOf(".") + 1);
                        String fileType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        Log.e(GOOGLE_DRIVE_TAG, fileType);
                        String[] arr = fileUrl.split("/");
                        String fileName = arr[arr.length - 1].substring(0, arr[arr.length - 1].indexOf("."));

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(fileName)
                                .setMimeType(fileType)
                                .setStarred(true).build();

                        // create a file in root folder
                        DriveFolder folder = driveId.asDriveFolder();
                        folder.createFile(MainActivity.getGoogleApiClient(), changeSet, driveContents)
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                    @Override
                                    public void onResult(DriveFolder.DriveFileResult result) {
                                        if (result.getStatus().isSuccess()) {
                                            DriveId Did = result.getDriveFile().getDriveId();
                                            driveId_str = Did.encodeToString();

                                            DatabaseHandler db = DatabaseHandler.getDbInstance(context);
                                            db.updateFileLink(fileUrl, driveId_str);
                                            Log.e("Android exxx:", driveId_str);
                                        }

                                        return;
                                    }
                                });
                    }
                }.start();
                }
            };
}
