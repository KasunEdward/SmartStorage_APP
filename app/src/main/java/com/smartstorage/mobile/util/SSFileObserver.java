package com.smartstorage.mobile.util;

import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;

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

    public SSFileObserver(File file, Context appContext) {
        super(file.getAbsolutePath(), ALL_EVENTS);
        this.initPath = file.getAbsolutePath();
        this.appContext = appContext;
    }

    @Override
    public void onEvent(int event, String path) {
        if (path == null) {
            path = "";
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
                eventType = EVENT_CLOSE_NOWRITE_STR;
                break;
            case CLOSE_WRITE:
                eventType = EVENT_CLOSE_WRITE_STR;
                break;
            case CREATE:
                Log.i("Settings","Deleting Files");
                Intent intent=new Intent();
                intent.setAction("com.smartStorage.deleteFile");
                ArrayList<String> strAL=new ArrayList<>();
                strAL.add("dddddddddd/sssss");
                strAL.add("df/gh/sssss");
                strAL.add("as/fg/hj/sssss");
                intent.putStringArrayListExtra("deletingList",strAL);
                appContext.sendBroadcast(intent);
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
            Log.d(LOG_TAG, "Event:" + eventType + " time:" + timeStamp + " " +
                "path" + initPath + "_" + path + "size: " + new File(initPath, path).length());
        }
    }

}
