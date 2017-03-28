package com.taxiapp.group28.taxiapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Tom on 28/03/2017.
 */

public class TaxiAppOnlineDatabase extends AppCompatActivity{
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

    private JSONObject PARAMS_INVALID = null;
    private JSONObject PARAMS_VALID=null;
    private JSONObject result =null;
    public TaxiAppOnlineDatabase(){
        try{
            PARAMS_INVALID = new JSONObject("[{error: 'Params Invalid'}]");
            PARAMS_VALID  = new JSONObject("[{success: 'Params Valid'}]");
        }catch(JSONException e){
            this.finish();
        }
    }
    private void setResult(String result){
        try{
            this.result = new JSONObject(result);
        }catch(JSONException e){
            this.result =null;
        }
    }
    public JSONObject getResult(){
        return result;
    }
    public JSONObject getDriverInformation(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_DRIVER_INFORMATION_PARAMS)) {
             requestData("GET",TYPE_GET_DRIVER_INFORMATION, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONObject updateDriverLocation(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_DRIVER_LOCATION_PARAMS)) {
             requestData("POST",TYPE_POST_UPDATE_DRIVER_LOCATION, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONObject addBooking(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,ADD_BOOKING_PARAMS)) {
             requestData("POST",TYPE_POST_ADD_BOOKING, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONObject updateBooking(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_BOOKING_PARAMS)) {
             requestData("POST",TYPE_POST_UPDATE_BOOKING, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONObject deleteBooking(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,DELETE_BOOKING_PARAMS)) {
             requestData("POST",TYPE_POST_DELETE_BOOKING, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONObject getBookings(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_BOOKINGS_PARAMS)) {
             requestData("GET",TYPE_GET_BOOKINGS, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONObject addRoute(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,ADD_ROUTE_PARAMS)) {
             requestData("POST",TYPE_POST_ADD_ROUTE, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONObject updateRoute(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_ROUTE_PARAMS)) {
             requestData("POST",TYPE_POST_UPDATE_ROUTE, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONObject deleteRoute(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,DELETE_ROUTE_PARAMS)) {
             requestData("POST",TYPE_POST_DELETE_ROUTE, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONObject getRoutes(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_ROUTE_PARAMS)){
             requestData("GET",TYPE_GET_ROUTES, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    private void requestData(String method,String type,HashMap<String,String> params){

        class GetJSONData extends AsyncTask<Void,Void,String> {
            private String method;
            private String type;
            private HashMap<String,String> params;
            private String result;
            private static final String ERROR = "[{error: 'true'}]";
            public GetJSONData(String method,String type,HashMap<String,String> params){
                this.method = method;
                this.type = type;
                this.params = params;
            }
            public JSONObject getResult(){
                try{
                    return new JSONObject(result);
                }catch (JSONException e){
                    return null;
                }
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                setResult(result);
            }
            @Override
            protected String doInBackground(Void... parameters) {
                TaxiAppRequestHandler requestHandler = new TaxiAppRequestHandler();
                switch(method)
                {
                    case "POST":
                        result = requestHandler.sendPostRequest(type, params);
                        break;
                    case "GET":
                        result = requestHandler.sendGetRequest(type, params);
                        break;
                    default:
                        return ERROR;
                }
                return result;
            }
        }
        GetJSONData getJSON = new GetJSONData(method,type,params);
        getJSON.execute();

    }
    private boolean isValidParams(HashMap<String,String> params, String[] keys){
        boolean  valid = true;

        for (String key  : keys) {
            if(!params.containsKey(key)){
                valid = false;
            }
        }
        return valid;
    }
}
