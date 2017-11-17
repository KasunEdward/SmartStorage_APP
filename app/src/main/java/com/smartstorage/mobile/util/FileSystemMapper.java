package com.smartstorage.mobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.smartstorage.mobile.AppParams;
import com.smartstorage.mobile.db.DatabaseHandler;
import com.smartstorage.mobile.db.FileDetails;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Irfad Hussain on 3/20/2017.
 */

public class FileSystemMapper extends AsyncTask<Void, Void, Boolean> {

    private static final String LOG_TAG = "SS_FileSystemMapper";

    private ArrayList<FileDetails> fileDetailsList;
    private Context context;

    public FileSystemMapper(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        this.fileDetailsList = new ArrayList<>();
        try {
            File externalStorage = Environment.getExternalStorageDirectory();
            if (externalStorage != null && externalStorage.exists()) {
                Log.i(LOG_TAG, "Mapping external storage :" + externalStorage.getAbsolutePath());
                traverseChildren(externalStorage);
            }

            String secondaryStoragePath = System.getenv("SECONDARY_STORAGE");
            if (secondaryStoragePath != null) {
                File secondaryStorage = new File(secondaryStoragePath);
                if (secondaryStorage != null && secondaryStorage.exists()) {
                    Log.i(LOG_TAG, "Mapping secondary storage :" + secondaryStorage.getAbsolutePath());
                    traverseChildren(secondaryStorage);
                }
            }

            if (fileDetailsList.size() > 0) {
                DatabaseHandler dbHandler = DatabaseHandler.getDbInstance(this.context);
                dbHandler.addMultipleFileDetails(fileDetailsList);
            }
            Log.i(LOG_TAG, "File system mapped successfully");
            return true;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error during mapping file", ex);
            return false;
        }
    }

    private void traverseChildren(File parent) {
        File[] children = parent.listFiles();
        if (children != null) {
            for (File child : children) {
                if (!CommonUtils.isTempOrCacheFile(child.getAbsolutePath())) {
                    if (child.isDirectory()) {
                        traverseChildren(child);
                    } else {
                        FileDetails fileDetails = new FileDetails(child.getAbsolutePath(), AppParams.DRIVE_NO_LINK, "", child.length());
                        fileDetails.setLast_accessed(child.lastModified());
                        fileDetailsList.add(fileDetails);

                    }
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            SharedPreferences sharedPreferences = this.context.getSharedPreferences(AppParams.PreferenceStr.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(AppParams.PreferenceStr.FILE_SYSTEM_MAPPED, "mapped").commit();
        }
    }
}
