package com.smartstorage.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.smartstorage.mobile.db.DatabaseHandler;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by kasun on 9/20/17.
 */

public class CopyFileToDropboxActivity  extends BroadcastReceiver{
    private Context context;
    private String fileUrl;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        new Upload(fileUrl).execute();

    }

    class Upload extends AsyncTask<String, Void, String> {
        String fileUrl;

        Upload(String url) {
            this.fileUrl = url;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {

            DropboxAPI.Entry response = null;

            try {

                // Define path of file to be upload
                File file = new File(fileUrl);
                FileInputStream inputStream = new FileInputStream(file);


                // put the file to dropbox
                response = MainActivity.getDropboxAPI().putFile(fileUrl, inputStream,
                        file.length(), null, null);
//TODO: check below updating part
                DatabaseHandler db = DatabaseHandler.getDbInstance(context);
                db.updateFileLink(fileUrl, response.rev);

                Log.e("DbExampleLog", "The uploaded file's rev is:" + response.rev);

            } catch (Exception e) {

                e.printStackTrace();
            }

            return response.rev;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.isEmpty() == false) {

//                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                Log.e("DbExampleLog", "The uploaded file's rev is: " + result);
            }
        }
    }
}
