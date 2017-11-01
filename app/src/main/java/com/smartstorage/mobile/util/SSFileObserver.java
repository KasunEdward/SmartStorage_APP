package com.smartstorage.mobile.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.FileObserver;
import android.util.Log;
import android.widget.Toast;

import com.smartstorage.mobile.MainActivity;
import com.smartstorage.mobile.R;
import com.smartstorage.mobile.db.DatabaseHandler;
import com.smartstorage.mobile.db.FileDetails;
import com.smartstorage.mobile.storage.StorageChecker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Irfad Hussain on 3/17/2017.
 */

public class SSFileObserver extends FileObserver {

    private static final String LOG_TAG = "SS_SSFileObserver";

    public static String moveToLocation;

    private static final String EVENT_ACCESS_STR = "ACCESS";
    private static final String EVENT_ATTRIB_STR = "ATTRIB";
    private static final String EVENT_CLOSE_NOWRITE_STR = "CLOSE_NOWRITE";
    private static final String EVENT_CLOSE_WRITE_STR = "CLOSE_WRITE";
    private static final String EVENT_CREATE_STR = "CREATE";
    private static final String EVENT_DELETE_STR = "DELETE";
    private static final String EVENT_DELETE_SELF_STR = "DELETE_SELF";
    private static final String EVENT_MODIFY_STR = "MODIFY";
    private static final String EVENT_MOVED_FROM_STR = "MOVED_FROM";
    private static final String EVENT_MOVED_TO_STR = "MOVED_TO";
    private static final String EVENT_MOVE_SELF_STR = "MOVE_SELF";
    private static final String EVENT_OPEN_STR = "OPEN";

    private String initPath;
    private Context appContext;
    private HashMap<String, FileDetails> children;

    public SSFileObserver(File file, Context appContext) {
        super(file.getAbsolutePath(), ALL_EVENTS);
        this.initPath = file.getAbsolutePath();
        this.appContext = appContext;
        children = new HashMap<>();
        String[] childList = file.list();
        if (childList != null && file.list().length > 0) {
            ArrayList<FileDetails> fileDetailses = DatabaseHandler.getDbInstance(appContext).getFileDetails(initPath, childList);
            for (FileDetails fileDetails : fileDetailses) {
                children.put(fileDetails.getFile_name(), fileDetails);
            }
        }
    }

    @Override
    public void onEvent(int event, String path) {
        String eventFilePath;
        FileDetails file;
        if (path == null) {
            path = "";
            eventFilePath = initPath;
        }else{
            eventFilePath = initPath + File.separator + path;
        }
        Log.d("file path",path);
        if(initPath.equals("Prefetch")){
            String[] neighbourFileIds = new String[4];
            String[] predictedFileNames = new String[4];
            DatabaseHandler handler = DatabaseHandler.getDbInstance(appContext);
            String filePath = initPath+"/"+path;
            String fileId = handler.getFileId(filePath);
            switch (fileId){
                case "5962":
                    neighbourFileIds[0]="5963";
                    neighbourFileIds[1]="5964";
                    neighbourFileIds[2]="5965";
                    neighbourFileIds[3]="5966";
                    break;
                case "5963":
                    neighbourFileIds[0]="5962";
                    neighbourFileIds[1]="5964";
                    neighbourFileIds[2]="5966";
                    neighbourFileIds[3]="5965";
                    break;
                case "5964":
                    neighbourFileIds[0]="5965";
                    neighbourFileIds[1]="5967";
                    neighbourFileIds[2]="5966";
                    neighbourFileIds[3]="5968";
                    break;
                case "5965":
                    neighbourFileIds[0]="5966";
                    neighbourFileIds[1]="5964";
                    neighbourFileIds[2]="5967";
                    neighbourFileIds[3]="5968";
                    break;
                case "5966":
                    neighbourFileIds[0]="5967";
                    neighbourFileIds[1]="5968";
                    neighbourFileIds[2]="5964";
                    neighbourFileIds[3]="5963";
                    break;
                case "5967":
                    neighbourFileIds[0]="5968";
                    neighbourFileIds[1]="5965";
                    neighbourFileIds[2]="5966";
                    neighbourFileIds[3]="5964";
                    break;
                case "5968":
                    neighbourFileIds[0]="5967";
                    neighbourFileIds[1]="5965";
                    neighbourFileIds[2]="5966";
                    neighbourFileIds[3]="5962";
                    break;
            }

            predictedFileNames[0] = handler.getFileName(neighbourFileIds[0]);
            predictedFileNames[1] = handler.getFileName(neighbourFileIds[1]);
            predictedFileNames[2] = handler.getFileName(neighbourFileIds[2]);
            predictedFileNames[3] = handler.getFileName(neighbourFileIds[3]);

            AlertDialog alertDialog = new AlertDialog.Builder(appContext).create();
            alertDialog.setTitle("Suggested prefetching list");
            alertDialog.setMessage(predictedFileNames[0]+"\n"+
                    predictedFileNames[1]+"\n"+
                    predictedFileNames[2]+"\n"+
                    predictedFileNames[3]);
            alertDialog.setIcon(R.drawable.cast_ic_notification_0);

            /*alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(appContext, "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });*/

            alertDialog.show();

            for(int i= 0; i < neighbourFileIds.length;i++){
                if(handler.isDeleted(neighbourFileIds[i])){
                    //add here notification to show downloading file
                    //download the file from google drive. File name = predictedFileNames[i]
                    Toast.makeText(appContext, predictedFileNames[i]+" is not in the local storage and it is downloading.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent("com.smartStorage.downloadFromGD");
                    intent.putExtra("fileUrl", initPath+"/"+predictedFileNames[i]);
                    appContext.sendBroadcast(intent);
                }
            }

        }
//        EventAttribs attribs = this.fileEventAttribs.get(path);
//        if (attribs == null) {
//            Log.d(LOG_TAG, "attribs null for path" + path);
//            File newFile = new File(initPath, path);
//            attribs = new EventAttribs(path, newFile.isDirectory());
//            fileEventAttribs.put(path, attribs);
//        }
//        if (!path.isEmpty())
//            hashedPath = attribs.encryptedName;
        String eventType = null;
//        LogEntry logEntry = null;
        long timeStamp = System.currentTimeMillis();
        event &= ALL_EVENTS;
        switch (event) {
            case ACCESS:
                eventType = EVENT_ACCESS_STR;
                break;
            case ATTRIB:
                eventType = EVENT_ATTRIB_STR;
                break;
            case CLOSE_NOWRITE:
                if (children != null && (file = children.get(eventFilePath)) != null) {
                    file.setLast_accessed(timeStamp);
                    Log.i(LOG_TAG, "File " + eventFilePath + "closed and updated access time");
                }
                eventType = EVENT_CLOSE_NOWRITE_STR;
                break;
            case CLOSE_WRITE:
                if (children != null && (file = children.get(eventFilePath)) != null) {
                    file.setLast_accessed(timeStamp);
                    Log.i(LOG_TAG, "File " + eventFilePath + "closed and updated access time");
                }
                eventType = EVENT_CLOSE_WRITE_STR;
                break;
            case CREATE:
                if(StorageChecker.returnUsedPercentage()>=89){
                    Log.i("Settings","Deleting Files");
                    Intent intent=new Intent();
                    intent.setAction("com.smartStorage.deleteFile");
                    ArrayList<String> strAL=new ArrayList<>();
                    DatabaseHandler db=DatabaseHandler.getDbInstance(appContext);
                    strAL=db.getListOfFilesToBeDeleted();
                    intent.putStringArrayListExtra("deletingList",strAL);
                    appContext.sendBroadcast(intent);
                }
                eventType = EVENT_CREATE_STR;
                break;
            case DELETE:
                eventType = EVENT_DELETE_STR;
                break;
            case DELETE_SELF:
                eventType = EVENT_DELETE_SELF_STR;
                break;
            case MODIFY:
                eventType = EVENT_MODIFY_STR;
                break;
            case MOVED_FROM:
                eventType = EVENT_MOVED_FROM_STR;
                break;
            case MOVED_TO:
                eventType = EVENT_MOVED_TO_STR;
                if(StorageChecker.returnUsedPercentage()>89){
                    Log.i("Settings","Deleting Files");
                    Intent intent=new Intent();
                    intent.setAction("com.smartStorage.deleteFile");
                    ArrayList<String> strAL=new ArrayList<>();
                    DatabaseHandler db=DatabaseHandler.getDbInstance(appContext);
                    strAL=db.getListOfFilesToBeDeleted();
                    intent.putStringArrayListExtra("deletingList",strAL);
                    appContext.sendBroadcast(intent);
                }
                break;
            case MOVE_SELF:
                eventType = EVENT_MOVE_SELF_STR;
                break;
            case OPEN:
                eventType = EVENT_OPEN_STR;
                Log.i("Inside FileObserver..:","Open");
                break;
            default:
                Log.d(LOG_TAG, "No matching event");
                return;
        }

        if (eventType == null) {
            Log.d(LOG_TAG, event + " not met. path=" + initPath + "_" + path);
            return;
        }else{
//            Log.d(LOG_TAG, "Event:" + eventType + " time:" + timeStamp + " " +
//                "path" + initPath + "_" + path + "size: " + new File(initPath, path).length());
        }
    }

    public HashMap<String, FileDetails> getChildren() {
        return children;
    }
}
