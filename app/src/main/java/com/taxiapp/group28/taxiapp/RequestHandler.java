package com.taxiapp.group28.taxiapp;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom on 26/03/2017.
 *  Version 1
 *  returns data as String in JSON format.
 *  Note error code -1 means request was successful for a post.
 *  Place the following code in an activity class.
 *  Example add a booking to database:
 *          new Thread(new Runnable() {
                    public void run() {
                         RequestHandler requestHandler = new RequestHandler();
                         HashMap<String,String> dataMap = new HashMap<>();

                         dataMap.put("pick_up_name","1");
                         dataMap.put("pick_up_latitude","1");
                         dataMap.put("pick_up_longitude","1");
                         dataMap.put("dest_name","1");
                         dataMap.put("dest_longitude","1");
                         dataMap.put("dest_latitude","1");
                         dataMap.put("user_id","1");
                         dataMap.put("assigned_driver_id","1");
                         dataMap.put("price","1");
                         dataMap.put("est_arrival_time","1");
                         dataMap.put("est_dest_time","1");
                         dataMap.put("confirmed_arrival_time","1");
                         dataMap.put("confirmed_dest_time","1");
                         dataMap.put("booking_complete","1");
                         requestHandler.sendPostRequest("addBooking",dataMap);
                    }
            }).start();
    Example get a users bookings from the database
 *          new Thread(new Runnable() {
                 public void run() {
                     RequestHandler requestHandler = new RequestHandler();
                     HashMap<String,String> dataMap = new HashMap<>();

                     dataMap.put("user_id","1");
                     requestHandler.sendGETRequest("getBooking",dataMap);
                 }
            }).start();
 */
public class RequestHandler {
    private static final String AUTH_TOKEN = "taxiAppTeam28";
    private static final String API_URL = "http://group10.sci-project.lboro.ac.uk/taxiAppAPI.php?auth_token="+AUTH_TOKEN;


    public RequestHandler(){

    }
    public String  sendGetRequest (String type, HashMap<String,String> dataParams) {
        return sendRequest("GET",type,dataParams);
    }
    public String  sendPostRequest (String type, HashMap<String,String> dataParams) {
        return sendRequest("POST",type,dataParams);
    }
    private String sendRequest(String method,String type, HashMap<String,String> dataParams) {
        URL url;
        try{
            url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod(method);
            conn.setDoOutput(true);

            OutputStream outputStream = conn.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(bindParams(type,dataParams));

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            int httpResponseCode = conn.getResponseCode();

            if(httpResponseCode == HttpURLConnection.HTTP_OK){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String responseData;
                StringBuilder responseBuilder = new StringBuilder();
                while((responseData = bufferedReader.readLine()) != null){
                    responseBuilder.append(responseData);
                }
                Log.d("Response",responseBuilder.toString());
                return responseBuilder.toString();
            }
        }catch(MalformedURLException e){
            Log.d("Error",""+e.getMessage());
        }
        catch(IOException e1){
            Log.d("Error",""+e1.getMessage());
        }catch(RuntimeException e2){
            Log.d("Error",""+e2.getMessage());
        }



        return "";
    }


    private String bindParams(String type,HashMap<String,String> dataParams){
        StringBuilder params = new StringBuilder();

        try {
            //bind initial parameters of the request type
            params.append(URLEncoder.encode(type, "UTF-8"));
            params.append("=");
            params.append(URLEncoder.encode("1", "UTF-8"));
            // bind the remaining data parameters
            for (Map.Entry<String, String> param : dataParams.entrySet()) {
                params.append("&");
                params.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                params.append("=");
                params.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            }
            return params.toString();
        }catch(UnsupportedEncodingException e){
            return "";
        }
    }
}
