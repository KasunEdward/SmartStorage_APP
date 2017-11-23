package com.smartstorage.mobile.machine_learning;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Created by kasun on 11/24/17.
 */

public class RestAPI4 extends AsyncTask<Void,Void,Void>{
    HttpClient httpClient = new DefaultHttpClient();

    @Override
    protected Void doInBackground(Void... param) {
        int[] arr = {
                6,3,2074,4096,2073,2072,2073,2074,2345,6789,5677
        };

        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0 ; i<arr.length; ++i){
            stringBuilder.append(arr[i]);
            if(i != arr.length -1){
                stringBuilder.append(',');
            }
        }
        System.out.println(stringBuilder);
        try {
            HttpPost httpPostRequest = new HttpPost("http://192.168.43.65:5000/predict4");

            StringEntity params =new StringEntity(stringBuilder.toString());
            httpPostRequest.setEntity(params);
            HttpResponse httpResponse = httpClient.execute(httpPostRequest);
            System.out.println("----------------------------------------");
            System.out.println(httpResponse.getStatusLine());
            System.out.println("----------------------------------------");

            HttpEntity entity = httpResponse.getEntity();
            String chunk = "";
            byte[] buffer = new byte[1024];
            if (entity != null) {
                InputStream inputStream = entity.getContent();
                try {
                    int bytesRead = 0;
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        chunk = new String(buffer, 0, bytesRead);
                        Log.i("Prefetching result..:",chunk);
                        //return chunk;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try { inputStream.close(); } catch (Exception ignore) {}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return null;
    }
}
