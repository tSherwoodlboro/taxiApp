package com.taxiapp.group28.taxiapp;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Tom on 28/03/2017.
 *
 */

public class TaxiAppOnlineDatabase {
    public static final String TYPE_GET_DRIVERS_INFORMATION = "getDriversInfo";
    public static final String[] GET_DRIVERS_INFORMATION_PARAMS = {};

    public static final String TYPE_GET_DRIVER_INFORMATION = "getDriverInfo";
    public static final String TYPE_POST_UPDATE_DRIVER_LOCATION = "updateDriverLocation";

    public static final String[] GET_DRIVER_INFORMATION_PARAMS = {"driver_id"};
    public static final String[] UPDATE_DRIVER_LOCATION_PARAMS = {"driver_id","longitude","latitude"};

    public static final String TYPE_GET_BOOKINGS = "getBooking";
    public static final String TYPE_POST_ADD_BOOKING = "addBooking";
    public static final String TYPE_POST_UPDATE_BOOKING_COMPLETE = "editBookingComplete";
    public static final String TYPE_POST_UPDATE_BOOKING = "editBooking";
    public static final String TYPE_POST_DELETE_BOOKING = "deleteBooking";

    public static final String[] ADD_BOOKING_PARAMS = {"user_id","assigned_driver_id","note","pick_up_name","pick_up_latitude","pick_up_longitude","dest_name","dest_latitude","dest_longitude","price","est_arrival_time","est_dest_time","confirmed_arrival_time","confirmed_dest_time","booking_complete"};
    public static final String[] UPDATE_BOOKING_PARAMS =  {"id","user_id","note","assigned_driver_id","pick_up_name","pick_up_latitude","pick_up_longitude","dest_name","dest_latitude","dest_longitude","price","est_arrival_time","est_dest_time","confirmed_arrival_time","confirmed_dest_time","booking_complete"};
    public static final String[] UPDATE_BOOKINGS_COMPLETE_PARAMS = {"id"};
    public static final String[] DELETE_BOOKING_PARAMS = {"id"};
    public static final String[] GET_BOOKINGS_PARAMS = {"user_id"};

    public static final String TYPE_GET_ROUTES = "getRoute";
    public static final String TYPE_POST_ADD_ROUTE = "addRoute";
    public static final String TYPE_POST_UPDATE_ROUTE = "editRoute";
    public static final String TYPE_POST_DELETE_ROUTE= "deleteRoute";

    public static final String[] ADD_ROUTE_PARAMS = {"user_id","note","pick_up_name","pick_up_latitude","pick_up_longitude","dest_name","dest_latitude","dest_longitude","name"};
    public static final String[] UPDATE_ROUTE_PARAMS = {"id","times_used","user_id","note","pick_up_name","pick_up_latitude","pick_up_longitude","dest_name","dest_latitude","dest_longitude","name"};
    public static final String[] DELETE_ROUTE_PARAMS = {"id"};
    public static final String[] GET_ROUTE_PARAMS = {"user_id"};

    public static final String TYPE_GET_USER = "getUser";
    public static final String TYPE_POST_ADD_USER = "addUser";
    public static final String TYPE_POST_UPDATE_USER = "editUser";
    public static final String TYPE_POST_DELETE_USER = "deleteUser";

    public static final String[] ADD_USER_PARAMS = {"tel_no","user_name","verification_code","verified"};
    public static final String[] UPDATE_USER_PARAMS =  {"user_name","tel_no","preferred_driver_id","verified","verification_code"};
    public static final String[] DELETE_USER_PARAMS = {"id"};
    public static final String[] GET_USER_PARAMS = {"tel_no"};

    public static final int AUTH_ERROR = 0; // authentication failed
    public static final int GET_DATA_ERROR = 1; // not all the data was received
    public static final int DB_ERROR = 2; // database sql error
    public static final int ERROR_UNKOWWN_ERRPOR= 3; // error unknown
    public static final int NO_FUNCTION_ERROR = 4; // request unknown no action to take
    public static final int DB_UNIQUE_ERROR = 5; // duplicate entry error
    public static final int SUCCESS = -1; // request successful

    private JSONArray PARAMS_INVALID = null;
    private JSONArray PARAMS_VALID=null;
    private JSONArray result =null;

    private static final String ERROR_FIELD = "error";
    private static final String INSERT_ID_FIELD = "insert_id";
    private Context context;
    private onGetResultListener getResultListener;
    public interface onGetResultListener{
         void onGetResult();
    }
    public void setOnGetResultListener(onGetResultListener listener){
        getResultListener = listener;

    }
    public TaxiAppOnlineDatabase(Context context){
        this.context = context;
        try{
            PARAMS_INVALID = new JSONArray("[{error: 'Params Invalid'}]");
            PARAMS_VALID  = new JSONArray("[{success: 'Params Valid'}]");
        }catch(JSONException e){
            try {
                this.finalize();
            }catch(Throwable e1){
                Log.d("ERROR","Failed to convert JSON");
            }
        }
    }
    private void setResult(String result){
        try{
            this.result = new JSONArray(result);
        }catch(JSONException e){
            Log.d("JSON ERROR1",e.getMessage());
            this.result =null;
        }
    }

    public JSONArray getResult(){
        return result;
    }

    public JSONArray getDriversInformation(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_DRIVERS_INFORMATION_PARAMS)) {
            requestData("GET",TYPE_GET_DRIVERS_INFORMATION, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray getDriverInformation(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_DRIVER_INFORMATION_PARAMS)) {
             requestData("GET",TYPE_GET_DRIVER_INFORMATION, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray updateDriverLocation(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_DRIVER_LOCATION_PARAMS)) {
             requestData("POST",TYPE_POST_UPDATE_DRIVER_LOCATION, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray addBooking(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,ADD_BOOKING_PARAMS)) {
             requestData("POST",TYPE_POST_ADD_BOOKING, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray updateBookingComplete(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_BOOKINGS_COMPLETE_PARAMS)) {
            requestData("POST",TYPE_POST_UPDATE_BOOKING_COMPLETE, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray updateBooking(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_BOOKING_PARAMS)) {
             requestData("POST",TYPE_POST_UPDATE_BOOKING, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray deleteBooking(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,DELETE_BOOKING_PARAMS)) {
             requestData("POST",TYPE_POST_DELETE_BOOKING, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray getBookings(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_BOOKINGS_PARAMS)) {
             requestData("GET",TYPE_GET_BOOKINGS, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray addUser(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,ADD_USER_PARAMS)) {
            requestData("POST",TYPE_POST_ADD_USER, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray updateUser(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_USER_PARAMS)) {
            requestData("POST",TYPE_POST_UPDATE_USER, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray deleteUser(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,DELETE_USER_PARAMS)) {
            requestData("POST",TYPE_POST_DELETE_USER, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray getUser(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_USER_PARAMS)) {
            requestData("GET",TYPE_GET_USER, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray addRoute(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,ADD_ROUTE_PARAMS)) {
             requestData("POST",TYPE_POST_ADD_ROUTE, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray updateRoute(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,UPDATE_ROUTE_PARAMS)) {
             requestData("POST",TYPE_POST_UPDATE_ROUTE, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray deleteRoute(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,DELETE_ROUTE_PARAMS)) {
             requestData("POST",TYPE_POST_DELETE_ROUTE, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public JSONArray getRoutes(HashMap<String,String> dataParams){
        if(isValidParams(dataParams,GET_ROUTE_PARAMS)){
             requestData("GET",TYPE_GET_ROUTES, dataParams);
        }else{
            return PARAMS_INVALID;
        }
        return PARAMS_VALID;
    }
    public boolean isResultSuccess(){
        if ( getResultMessage() != null && getResultMessage().equals(Integer.valueOf(TaxiAppOnlineDatabase.SUCCESS).toString())) {
            return true;
        }
        return false;
    }
    public boolean isResultError(){
        if(result != null){
            try{
                if(!result.getJSONObject(0).keys().next().equals("error")){
                    return false;
                }else{
                    return true;
                }

            }catch(Exception e){
                return true;
            }
        }
        return true;
    }
    public ArrayList<ContentValues> getContentValuesList(){
        ArrayList<ContentValues> contentValuesArrayList  = new ArrayList<>();
        if(result == null){
            return null;
        }
        for(int i=0;i<result.length();++i){
            ContentValues contentValues = new ContentValues();
            try {
                JSONObject rowObject = result.getJSONObject(i);
                Log.d("ROW_OBJECT",rowObject.toString());
                Iterator<?> keys  = rowObject.keys();
                while(keys.hasNext()){
                    String key = (String)keys.next();
                    if(key.equals("id")){
                        contentValues.put(BaseColumns._ID,(int)rowObject.get(key));
                        continue;
                    }
                    String type =  rowObject.get(key).getClass().getName();
                    if(type.equals(Integer.class.getName())){
                        contentValues.put(key,(int)rowObject.get(key));
                    }else if(type.equals(Double.class.getName())){
                        contentValues.put(key,(double)rowObject.get(key));
                    }else if(type.equals(String.class.getName())){
                        contentValues.put(key,(String)(rowObject.get(key)));
                    }
                }
            }catch(JSONException e){
                return null;
            }
            Log.d("CONTENT_VALUES",contentValues.toString());
            contentValuesArrayList.add(contentValues);
        }
        return contentValuesArrayList;
    }
    private void requestData(String method,String type,HashMap<String,String> params){
        if(!isNetworkEnabled(context)){
            return;
        }
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

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                setResult(result);
                if(getResultListener != null) {
                    getResultListener.onGetResult();
                }
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
    public String getResultMessage(){
        // get the result only valid for post requests
        if(result == null){
            return null;
        }
        try{
            return result.getJSONObject(0).get(ERROR_FIELD).toString();
        }catch(JSONException e){
            return null;
        }

    }
    public String getInsertId(){
        if(result == null){
            return null;
        }
        try{
            return result.getJSONObject(0).get(INSERT_ID_FIELD).toString();
        }catch(JSONException e){
            Log.d("JSON ERROR","ERROR "+e.getMessage());
            return null;
        }
    }
    public void close(){
        try {
            this.finalize();
        }catch(Throwable e){
            Log.d("Error",e.getMessage());
        }
    }
    private boolean isValidParams(HashMap<String,String> params, String[] keys){
        // check the parameters are valid for the request
        boolean  valid = true;

        for (String key  : keys) {
            if(!params.containsKey(key)){
                Log.d("NO_KEY",key);
                valid = false;
            }
        }
        return valid;
    }
    public static boolean isNetworkEnabled(Context _context) {
        try {
            ConnectivityManager connManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        } catch (Exception e) {
                Log.d("NETWORK_ERROR","Error " +e.getMessage());
        }
        return false;
    }
    public static boolean isNetworkEnabled(Context _context,int state) {
        if(!isNetworkEnabled(_context)){
            Toast toast = Toast.makeText(_context,"Network Connection Required!",Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }
}
