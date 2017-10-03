package com.smartstorage.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by kasun on 9/20/17.
 */

public class DownloadFromDropbox extends BroadcastReceiver {
    String fileUrl;
    @Override
    public void onReceive(Context context, Intent intent) {
        new Download(fileUrl).execute();
    }
    class Download extends AsyncTask<String, Void, String> {
        String fileUrl;

        Download(String url) {
            this.fileUrl = url;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {

            File file = new File("/storage/emulated/0/Download/abcdefg.jpg");
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            DropboxAPI.DropboxFileInfo info = null;
            try {
                info = MainActivity.getDropboxAPI().getFile(fileUrl, null, outputStream, null);
            } catch (DropboxException e) {
                e.printStackTrace();
            }
            return info.getMetadata().rev;

        }

        @Override
        protected void onPostExecute(String result) {

            if (result.isEmpty() == false) {

//                Toast.makeText(getApplicationContext(), "File Downloaded ", Toast.LENGTH_LONG).show();

                Log.e("DbExampleLog", "The Downloaded file's rev is: " + result);
            }
        }
    }
}
