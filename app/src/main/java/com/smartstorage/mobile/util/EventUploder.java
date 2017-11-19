package com.smartstorage.mobile.util;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Irfad Hussain on 11/19/2017.
 */

public class EventUploder extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = "SS_EventUploader";
    private static final String BACK_END_IP = "192.168.43.65";

    private EventJSON[] events;

    public EventUploder(EventJSON[] events) {
        this.events = events;
    }

    @Override
    protected Void doInBackground(Void... params) {
        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://" + BACK_END_IP + "/FYPDemo/demo.php");
            JSONArray jsonArray = new JSONArray();
            for (EventJSON event : events) {
                jsonArray.put(event.toJSONObject());
            }
            String message = jsonArray.toString();

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(message.getBytes().length);

            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //open
            conn.connect();

            //setup send
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(message.getBytes());
            //clean up
            os.flush();

            //do somehting with response
            String str = conn.getResponseMessage();
            Log.e("httprequest:", str);
            is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result = bis.read();
            while (result != -1) {
                buf.write((byte) result);
                result = bis.read();
            }
            Log.e("response.....", buf.toString("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                os.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            conn.disconnect();
        }
        return null;
    }
}
