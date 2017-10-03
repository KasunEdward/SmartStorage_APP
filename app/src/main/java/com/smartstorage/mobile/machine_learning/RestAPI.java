package com.smartstorage.mobile.machine_learning;

/**
 * Created by anuradha on 10/2/17.
 */

import java.io.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *This class calls the external end point where pre-trained ML model is loaded ( Random Forest Classifier )
 */
public class RestAPI {

    public String predict(int[] arr) {

        HttpClient httpClient = new DefaultHttpClient();
//
//        int[] arr = {
//                6,3,2074,4096,2073,2072,2073,2074,2075
//        };

        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0 ; i<arr.length; ++i){
            stringBuilder.append(arr[i]);
            if(i != arr.length -1){
                stringBuilder.append(',');
            }
        }
        System.out.println(stringBuilder);
        try {
            HttpPost httpPostRequest = new HttpPost("http://127.0.0.1:8081/predict");

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
                        System.out.println(chunk);
                        return chunk;
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
       return "";
    }
}