package com.smartstorage.mobile.util;

import com.smartstorage.mobile.db.FileDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Irfad Hussain on 11/19/2017.
 */

public class EventJSON {

    private String accessedPath;
    private FileDetails[] successerList;

    public String getAccessedPath() {
        return accessedPath;
    }

    public void setAccessedPath(String accessedPath) {
        this.accessedPath = accessedPath;
    }

    public FileDetails[] getSuccesserList() {
        return successerList;
    }

    public void setSuccesserList(FileDetails[] successerList) {
        this.successerList = successerList;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current_file",accessedPath);
        jsonObject.put("event_type","OPEN");

        JSONArray succesorListJSON = new JSONArray();
        for (FileDetails successor : successerList){
            JSONObject successorJSON = new JSONObject();
            successorJSON.put("name", successor.getFile_name());
            String availability, prefetched;
            if (successor.getDeleted().equals("True")){
                availability = "No";
                prefetched = "Yes";
            }else{
                availability = "Yes";
                prefetched = "No";
            }
            successorJSON.put("availability", availability);
            successorJSON.put("prefetched",prefetched);
            succesorListJSON.put(successorJSON);
        }
        jsonObject.put("successor_list",succesorListJSON);
        return jsonObject;
    }
}
