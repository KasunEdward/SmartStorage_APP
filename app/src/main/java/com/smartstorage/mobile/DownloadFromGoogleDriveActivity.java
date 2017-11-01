package com.smartstorage.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.smartstorage.mobile.db.DatabaseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kasun on 9/19/17.
 */

public class DownloadFromGoogleDriveActivity extends BroadcastReceiver{

    String fileUrl="/storage/emulated/0/Samsung/Music/Over the Horizon.mp3";
    String GOOGLE_DRIVE_TAG="Google Drive..:";
    String driveId_str;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        DatabaseHandler db = DatabaseHandler.getDbInstance(context);
        fileUrl = intent.getStringExtra("fileUrl");
        String driveId_str = db.getFileLink(fileUrl);
//        driveId_str="DriveId:CAASABjkDiCO9tni-lQoAA==";
        DriveFile file = Drive.DriveApi.getFile(MainActivity.getGoogleApiClient(), DriveId.decodeFromString(driveId_str));
        file.open(MainActivity.getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).setResultCallback(contentsOpenedCallback);

    }
    ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("Error:", "No such file");
                        return;
                    }
                    DriveContents contents = result.getDriveContents();
                    InputStream inputStream = contents.getInputStream();
                    try {
                        DatabaseHandler db = DatabaseHandler.getDbInstance(context);
                        String fileName = db.getFileDetails(driveId_str);
                        Log.i(GOOGLE_DRIVE_TAG, fileName);
                        File targetFile = new File("/storage/emulated/0/Download/abcdefg.mp3");
                        OutputStream outputStream = new FileOutputStream(targetFile);
                        byte[] buffer = new byte[8 * 1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        inputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
}
