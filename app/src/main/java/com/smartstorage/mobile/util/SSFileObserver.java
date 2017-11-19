package com.smartstorage.mobile.util;

import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.util.Log;

import com.smartstorage.mobile.DeleteFilesActivity;
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
    private static final int EVENT_BUFFER_SIZE = 1;
    public static String moveToLocation;

    private static EventJSON[] eventBuffer = new EventJSON[EVENT_BUFFER_SIZE];
    private static int bufferPointer = 0;

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

    private static long last_event_time = 0;

    public SSFileObserver(File file, Context appContext) {
        super(file.getAbsolutePath(), ALL_EVENTS);
        this.initPath = file.getAbsolutePath();
        this.appContext = appContext;
        this.children = new HashMap<>();
        String[] childList = file.list();
        if (childList != null && file.list().length > 0 && file.list().length < 50) {
            ArrayList<FileDetails> fileDetailses = DatabaseHandler.getDbInstance(appContext).getFileDetails(initPath, childList);
            for (FileDetails fileDetails : fileDetailses) {
                children.put(fileDetails.getFile_name(), fileDetails);
            }
            //Log.i("SSFILEOBSERVER", "children length "+ children.size());
        }
    }

    @Override
    public void onEvent(int event, String path) {
        String eventFilePath;
        FileDetails file;
        long timeStamp = System.currentTimeMillis();
        if (path == null) {
            path = "";
            eventFilePath = initPath;
        } else {
            eventFilePath = initPath + File.separator + path;
        }
        event &= ALL_EVENTS;

        if (!DeleteFilesActivity.isDeleting && event == OPEN && timeStamp - last_event_time > 50) {
            last_event_time = timeStamp;
            DatabaseHandler handler = DatabaseHandler.getDbInstance(appContext);
            String filePath = initPath + "/" + path;
            FileDetails[] predictedFileNames = handler.getPredictedFileNames(initPath, filePath);

//            AlertDialog alertDialog = new AlertDialog.Builder(appContext).create();
//            alertDialog.setTitle("Suggested prefetching list");
//            alertDialog.setMessage(predictedFileNames[0]+"\n"+
//                    predictedFileNames[1]+"\n"+
//                    predictedFileNames[2]+"\n"+
//                    predictedFileNames[3]);
//            alertDialog.setIcon(R.drawable.cast_ic_notification_0);

            Log.d(LOG_TAG, predictedFileNames[0].getFile_name() + "\n" +
                    predictedFileNames[1].getFile_name() + "\n" +
                    predictedFileNames[2].getFile_name() + "\n" +
                    predictedFileNames[3].getFile_name());

//            if (bufferPointer == 0) {
            eventBuffer[bufferPointer] = new EventJSON();
            eventBuffer[bufferPointer].setAccessedPath(filePath);
            eventBuffer[bufferPointer].setSuccesserList(predictedFileNames);
//                bufferPointer++;
            new EventUploder(eventBuffer).execute();

//            } else if (!eventBuffer[bufferPointer - 1].getAccessedPath().equals(filePath)) {
//                eventBuffer[bufferPointer] = new EventJSON();
//                eventBuffer[bufferPointer].setAccessedPath(filePath);
//                eventBuffer[bufferPointer].setSuccesserList(predictedFileNames);
//                bufferPointer++;
//                if (bufferPointer == EVENT_BUFFER_SIZE){
//                    bufferPointer = 0;
//                    new EventUploder(eventBuffer).execute();
//                    eventBuffer = new EventJSON[EVENT_BUFFER_SIZE];
//                }
//            }
//            Toast.makeText(appContext, predictedFileNames[0]+"\n"+
//                    predictedFileNames[1]+"\n"+
//                    predictedFileNames[2]+"\n"+
//                    predictedFileNames[3], Toast.LENGTH_SHORT).show();

            /*alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(appContext, "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });*/

//            alertDialog.show();

            for (int i = 0; i < predictedFileNames.length; i++) {
                if (predictedFileNames[i] != null && predictedFileNames[i].getDeleted().equals("True")) {
                    //add here notification to show downloading file
                    //download the file from google drive. File name = predictedFileNames[i]
                    Log.e(LOG_TAG, "Sending broadcast fetch: " + predictedFileNames[i].getFile_name());
                    Intent intent = new Intent("com.smartStorage.downloadFromGD");
                    intent.putExtra("fileUrl", predictedFileNames[i].getFile_name());
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
                if (StorageChecker.returnUsedPercentage() >= 89) {
                    Log.i("Settings", "Deleting Files");
                    Intent intent = new Intent();
                    intent.setAction("com.smartStorage.deleteFile");
                    ArrayList<String> strAL = new ArrayList<>();
                    DatabaseHandler db = DatabaseHandler.getDbInstance(appContext);
                    strAL = db.getListOfFilesToBeDeleted();
                    intent.putStringArrayListExtra("deletingList", strAL);
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
//                if(StorageChecker.returnUsedPercentage()>89){
//                    Log.i("Settings","Deleting Files");
//                    Intent intent=new Intent();
//                    intent.setAction("com.smartStorage.deleteFile");
//                    ArrayList<String> strAL=new ArrayList<>();
//                    DatabaseHandler db=DatabaseHandler.getDbInstance(appContext);
//                    strAL=db.getListOfFilesToBeDeleted();
//                    intent.putStringArrayListExtra("deletingList",strAL);
//                    appContext.sendBroadcast(intent);
//                }
                break;
            case MOVE_SELF:
                eventType = EVENT_MOVE_SELF_STR;
                break;
            case OPEN:
                eventType = EVENT_OPEN_STR;
                Log.i("Inside FileObserver..:", "Open");
                break;
            default:
                Log.d(LOG_TAG, "No matching event");
                return;
        }

        if (eventType == null) {
            Log.d(LOG_TAG, event + " not met. path=" + initPath + "_" + path);
            return;
        } else {
//            Log.d(LOG_TAG, "Event:" + eventType + " time:" + timeStamp + " " +
//                "path" + initPath + "_" + path + "size: " + new File(initPath, path).length());
        }
    }

    public HashMap<String, FileDetails> getChildren() {
        return children;
    }
}
