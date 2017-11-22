package com.smartstorage.mobile;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.smartstorage.mobile.db.DatabaseHandler;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by kasun on 9/20/17.
 */

public class CopyFileToDropboxActivity  extends BroadcastReceiver{
    private Context context;
    private String fileUrl;
    ArrayList<String> copyingFileList;

    private static int fileCount=0;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        copyingFileList=intent.getStringArrayListExtra("copyingListToDB");
        Log.d("Dropbox_TAG:",String.valueOf(MainActivity.getDropboxAPI()));
        for(int i=0;i<copyingFileList.size();i++){
            Log.d("DropBox copying....:",copyingFileList.get(i));
            new Upload(copyingFileList.get(i)).execute();
        }


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
//                File file = new File(fileUrl);
                File file = new File(fileUrl);
                FileInputStream inputStream = new FileInputStream(file);


                // put the file to dropbox
                response = MainActivity.getDropboxAPI().putFile(fileUrl, inputStream,
                        file.length(), null, null);
//TODO: check below updating part
                DatabaseHandler db = DatabaseHandler.getDbInstance(context);
                db.updateFileLink(fileUrl, response.rev,"DropBox");

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
                fileCount++;
                if(fileCount==copyingFileList.size()){
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                    mBuilder.setSmallIcon(R.drawable.ic_folder);
                    String str=String.valueOf(fileCount)+" files were copied to Drop Box";
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
                    fileCount=0;
                }
            }
        }
    }
}
