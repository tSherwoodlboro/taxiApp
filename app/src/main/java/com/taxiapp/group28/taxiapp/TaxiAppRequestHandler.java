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
                         TaxiAppRequestHandler requestHandler = new TaxiAppRequestHandler();
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
                         requestHandler.addBooking(dataMap);
                    }
            }).start();
    Example get a users bookings from the database
 *          new Thread(new Runnable() {
                 public void run() {
                     TaxiAppRequestHandler requestHandler = new TaxiAppRequestHandler();
                     HashMap<String,String> dataMap = new HashMap<>();

                     dataMap.put("user_id","1");
                     requestHandler.getBookings("getBooking",dataMap);
                 }
            }).start();
 */
public class TaxiAppRequestHandler {
    private static final String AUTH_TOKEN = "taxiAppTeam28";
    private static final String API_URL = "http://group10.sci-project.lboro.ac.uk/taxiAppAPI.php?auth_token="+AUTH_TOKEN;

    private static final String TYPE_GET_DRIVER_INFORMATION = "getDriverInfo";
    private static final String TYPE_POST_UPDATE_DRIVER_LOCATION = "updateDriverLocation";

    private static final String[] GET_DRIVER_INFORMATION_PARAMS = {"driver_id"};
    private static final String[] UPDATE_DRIVER_LOCATION_PARAMS = {"driver_id","longitude","latitude"};

    private static final String TYPE_GET_BOOKINGS = "getBooking";
    private static final String TYPE_POST_ADD_BOOKING = "addBooking";
    private static final String TYPE_POST_UPDATE_BOOKING = "editBooking";
    private static final String TYPE_POST_DELETE_BOOKING = "deleteBooking";

    private static final String[] ADD_BOOKING_PARAMS = {"user_id","assigned_driver_id","pick_up_name","pick_up_latitude","pick_up_longitude","dest_name","dest_latitude","dest_longitude","price","est_arrival_time","est_dest_time","confirmed_arrival_time","confirmed_dest_time","booking_complete"};
    private static final String[] UPDATE_BOOKING_PARAMS =  {"id","user_id","assigned_driver_id","pick_up_name","pick_up_latitude","pick_up_longitude","dest_name","dest_latitude","dest_longitude","price","est_arrival_time","est_dest_time","confirmed_arrival_time","confirmed_dest_time","booking_complete"};
    private static final String[] DELETE_BOOKING_PARAMS = {"id"};
    private static final String[] GET_BOOKINGS_PARAMS = {"user_id"};

    private static final String TYPE_GET_ROUTES = "gerRoute";
    private static final String TYPE_POST_ADD_ROUTE = "addRoute";
    private static final String TYPE_POST_UPDATE_ROUTE = "editRoute";
    private static final String TYPE_POST_DELETE_ROUTE= "deleteRoute";

    private static final String[] ADD_ROUTE_PARAMS = {"user_id","assigned_driver_id","pick_up_name","pick_up_latitude","pick_up_longitude","dest_name","dest_latitude","dest_longitude","name"};
    private static final String[] UPDATE_ROUTE_PARAMS = {"id","times_used","user_id","assigned_driver_id","pick_up_name","pick_up_latitude","pick_up_longitude","dest_name","dest_latitude","dest_longitude","name"};
    private static final String[] DELETE_ROUTE_PARAMS = {"id"};
    private static final String[] GET_ROUTE_PARAMS = {"user_id"};

    private static final String PARAMS_INVALID = "[{error: 'Params Invalid'}]";
    private static final String ERROR = "[{error: 'true'}]";

    private String lastResult =null; // last JSON result from server

    public TaxiAppRequestHandler(){

    }
    public String getDriverInformation(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_DRIVER_INFORMATION_PARAMS)) {
            return sendGetRequest(TYPE_GET_DRIVER_INFORMATION, dataParams);
        }else{
            return PARAMS_INVALID;
        }
    }
    public String updateDriverLocation(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_DRIVER_LOCATION_PARAMS)) {
            return sendPostRequest(TYPE_POST_UPDATE_DRIVER_LOCATION, dataParams);
        }else{
            return PARAMS_INVALID;
        }
    }
    public String addBooking(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,ADD_BOOKING_PARAMS)) {
            return sendPostRequest(TYPE_POST_ADD_BOOKING, dataParams);
        }else{
            return PARAMS_INVALID;
        }
    }
    public String updateBooking(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_BOOKING_PARAMS)) {
            return sendPostRequest(TYPE_POST_UPDATE_BOOKING, dataParams);
        }else{
            return PARAMS_INVALID;
        }
    }
    public String deleteBooking(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,DELETE_BOOKING_PARAMS)) {
            return sendPostRequest(TYPE_POST_DELETE_BOOKING, dataParams);
        }else{
            return PARAMS_INVALID;
        }
    }
    public String getBookings(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_BOOKINGS_PARAMS)) {
            return sendGetRequest(TYPE_GET_BOOKINGS, dataParams);
        }else{
            return PARAMS_INVALID;
        }
    }
    public String addRoute(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,ADD_ROUTE_PARAMS)) {
            return sendPostRequest(TYPE_POST_ADD_ROUTE, dataParams);
        }else{
            return PARAMS_INVALID;
        }
    }
    public String updateRoute(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_ROUTE_PARAMS)) {
            return sendPostRequest(TYPE_POST_UPDATE_ROUTE, dataParams);
        }else{
            return PARAMS_INVALID;
        }
    }
    public String deleteRoute(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,DELETE_ROUTE_PARAMS)) {
            return sendPostRequest(TYPE_POST_DELETE_ROUTE, dataParams);
        }else{
            return PARAMS_INVALID;
        }
    }
    public String getRoutes(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_ROUTE_PARAMS)){
            return sendGetRequest(TYPE_GET_ROUTES, dataParams);
        }else{
            return PARAMS_INVALID;
        }
    }

    private String  sendGetRequest (String type, HashMap<String,String> dataParams) {
        return sendRequest("GET",type,dataParams);
    }
    private String  sendPostRequest (String type, HashMap<String,String> dataParams) {
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
            conn.disconnect();
            if(httpResponseCode == HttpURLConnection.HTTP_OK){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String responseData;
                StringBuilder responseBuilder = new StringBuilder();
                while((responseData = bufferedReader.readLine()) != null){
                    responseBuilder.append(responseData);
                }
                lastResult = responseBuilder.toString();
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


        return ERROR;
    }

    public String getLastResult(){ return lastResult; }

    private boolean isValidParams(HashMap<String,String> params, String[] keys){
        boolean  valid = true;

        for (String key  : keys) {
            if(!params.containsKey(key)){
                valid = false;
            }
        }
        return valid;
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
