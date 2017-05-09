package com.taxiapp.group28.taxiapp;

import android.content.ContentValues;
import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Tom on 25/04/2017.
 */

public class Booking {
    //properties of a booking
    private static final Double FAIR_PRICE = 5.0;
    private static final Double PRICE_PER_MILE=1.2;
    public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private String date = null;
    private String pickUpName = null;
    private String destName = null;
    private String price =null;
    private Double pickUpLatitude;
    private Double pickUpLongitude;
    private Double destLatitude;
    private Double destLongitude;
    private int id =-1;
    private String estArrivalTime = null;
    private String estDestTime = null;
    private String confirmedArrivalTime = "-1";
    private String confirmedDestTime= "-1";
    private int bookingComplete = -1;
    private String note = null;
    private String userId = null;
    private HashMap<String,String> params = null;
    private ContentValues contentValues = null;
    private int assignedDriverId=1;
    private Context context = null;
    private Address pickUpAddress=null;
    private Address destAddress = null;
    private String duration=null;
    private Calendar estArrivalTimeCalendar=null; // pick up time
    private Calendar estDestTimeCalendar=null;
    private Booking.onGetResultListener getResultListener;

    public interface onGetResultListener{
        void onGetResult();
    }
    public void setOnGetResultListener(Booking.onGetResultListener listener){
        getResultListener = listener; // called when the route info has been fetched

    }
    public Booking(Context context){
        // Constructor always called to get user id.
        this.context = context;
        userId = SharedPreferencesManager.getUserPreferences(this.context).getString(context.getString(R.string.user_preferred_user_id_pref_key),"null");
        assignedDriverId =  Integer.valueOf(SharedPreferencesManager.getUserPreferences(this.context).getString(context.getString(R.string.user_preferred_driver_id_pref_key),"1"));
    }
    public Booking(Context context, String date, String pickUpName, String destName, Double pickUpLatitude, Double pickUpLongitude, Double destLatitude, Double destLongitude,String price, int id){
        this(context);
        // constructor used for updating booking.Called in ViewBookingsFragment
        this.date = date;
        this.pickUpName = pickUpName;
        this.pickUpLatitude = pickUpLatitude;
        this.pickUpLongitude = pickUpLongitude;
        this.destName = destName;
        this.destLatitude = destLatitude;
        this.destLongitude = destLongitude;
        this.price = price;
        this.id= id;

    }
    public Booking(Context context, String pickUpName, String destName, Address pickUpAddress, Address destAddress,String note,Calendar estArrivalTimeCalendar){
        this(context);
        // constructor used for creating a new booking.Called in the confirmBookingFragment.
        this.pickUpAddress = pickUpAddress;
        this.destAddress = destAddress;
        this.pickUpName = pickUpName;
        this.pickUpLatitude = pickUpAddress.getLatitude();
        this.pickUpLongitude =pickUpAddress.getLongitude();
        this.destName = destName;
        this.destLatitude = destAddress.getLatitude();
        this.destLongitude = destAddress.getLongitude();
        this.estArrivalTime = getTimestamp(estArrivalTimeCalendar);
        this.estArrivalTimeCalendar = estArrivalTimeCalendar;
        this.note = note;
        setNote(note);
        getRouteInfo();
    }

    public Booking(Context context,Bundle bundle){
        this(context);
        // constructor for add or update booking in bundle form
        this.pickUpName = String.valueOf(bundle.get(DBContract.Booking_Table.COLUMN_PICK_UP_NAME));
        this.pickUpLatitude = Double.valueOf((String)bundle.get(DBContract.Booking_Table.COLUMN_PICK_UP_LATITUDE));
        this.pickUpLongitude = Double.valueOf((String)bundle.get(DBContract.Booking_Table.COLUMN_PICK_UP_LONGITUDE));
        this.destName = String.valueOf(bundle.get(DBContract.Booking_Table.COLUMN_DEST_NAME));
        this.destLatitude = Double.valueOf((String)bundle.get(DBContract.Booking_Table.COLUMN_DEST_LATITUDE));
        this.destLongitude = Double.valueOf((String)bundle.get(DBContract.Booking_Table.COLUMN_DEST_LONGITUDE));
        this.price = (String)bundle.get(DBContract.Booking_Table.COLUMN_PRICE);
        this.estArrivalTime = (String)bundle.get(DBContract.Booking_Table.COLUMN_EST_ARRIVAL_TIME);
        this.estDestTime = (String)bundle.get(DBContract.Booking_Table.COLUMN_EST_DEST_TIME);
        this.confirmedArrivalTime = (String)bundle.get(DBContract.Booking_Table.COLUMN_CONFIRMED_ARRIVAL_TIME);
        this.confirmedDestTime = (String)bundle.get(DBContract.Booking_Table.COLUMN_CONFIRMED_DEST_TIME);
        if(bundle.get(DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE) != null){
            this.bookingComplete = Integer.valueOf((String)bundle.get(DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE));
        }
        this.note = (String)bundle.get(DBContract.Booking_Table.COLUMN_NOTE);
        if(bundle.get(DBContract.Booking_Table.COLUMN_ASSIGNED_DRIVER_ID) != null){
            this.assignedDriverId = Integer.valueOf((String)bundle.get(DBContract.Booking_Table.COLUMN_ASSIGNED_DRIVER_ID));
        }
    }

    public boolean setParams(){
        // HashMap format needed when the booking is updated or created in the online db.
        params = new HashMap<>();
        params.put("id",String.valueOf(id));
        params.put(DBContract.Booking_Table.COLUMN_USER_ID,userId);
        params.put(DBContract.Booking_Table.COLUMN_ASSIGNED_DRIVER_ID,Integer.valueOf(assignedDriverId).toString());
        params.put(DBContract.Booking_Table.COLUMN_PICK_UP_NAME,pickUpName);
        params.put(DBContract.Booking_Table.COLUMN_PICK_UP_LATITUDE,Double.valueOf(pickUpLatitude).toString());
        params.put(DBContract.Booking_Table.COLUMN_PICK_UP_LONGITUDE,Double.valueOf(pickUpLongitude).toString());
        params.put(DBContract.Booking_Table.COLUMN_DEST_NAME,destName);
        params.put(DBContract.Booking_Table.COLUMN_DEST_LATITUDE,Double.valueOf(destLatitude).toString());
        params.put(DBContract.Booking_Table.COLUMN_DEST_LONGITUDE,Double.valueOf(destLongitude).toString());
        params.put(DBContract.Booking_Table.COLUMN_PRICE,Double.valueOf(price).toString());
        params.put(DBContract.Booking_Table.COLUMN_EST_ARRIVAL_TIME,estArrivalTime);
        params.put(DBContract.Booking_Table.COLUMN_EST_DEST_TIME,estDestTime);
        params.put(DBContract.Booking_Table.COLUMN_CONFIRMED_ARRIVAL_TIME,confirmedArrivalTime);
        params.put(DBContract.Booking_Table.COLUMN_CONFIRMED_DEST_TIME,confirmedDestTime);
        params.put(DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE,Integer.valueOf(bookingComplete).toString());
        params.put(DBContract.Booking_Table.COLUMN_NOTE,note);
        return true;
    }
    public boolean setContentValues(){
        // sets the contentValues when the booking is being updated or created in the local db.
        if(date == null && id!=-1){
            return false;
        }
        contentValues = new ContentValues();
        contentValues.put(DBContract.Booking_Table._ID,id);
        contentValues.put(DBContract.Booking_Table.COLUMN_DATE,date);
        contentValues.put(DBContract.Booking_Table.COLUMN_USER_ID,userId);
        contentValues.put(DBContract.Booking_Table.COLUMN_ASSIGNED_DRIVER_ID,Integer.valueOf(assignedDriverId).toString());
        contentValues.put(DBContract.Booking_Table.COLUMN_PICK_UP_NAME,pickUpName);
        contentValues.put(DBContract.Booking_Table.COLUMN_PICK_UP_LATITUDE,Double.valueOf(pickUpLatitude).toString());
        contentValues.put(DBContract.Booking_Table.COLUMN_PICK_UP_LONGITUDE,Double.valueOf(pickUpLongitude).toString());
        contentValues.put(DBContract.Booking_Table.COLUMN_DEST_NAME,destName);
        contentValues.put(DBContract.Booking_Table.COLUMN_DEST_LATITUDE,Double.valueOf(destLatitude).toString());
        contentValues.put(DBContract.Booking_Table.COLUMN_DEST_LONGITUDE,Double.valueOf(destLongitude).toString());
        contentValues.put(DBContract.Booking_Table.COLUMN_PRICE,Double.valueOf(price).toString());
        contentValues.put(DBContract.Booking_Table.COLUMN_EST_ARRIVAL_TIME,estArrivalTime);
        contentValues.put(DBContract.Booking_Table.COLUMN_EST_DEST_TIME,estDestTime);
        contentValues.put(DBContract.Booking_Table.COLUMN_CONFIRMED_ARRIVAL_TIME,confirmedArrivalTime);
        contentValues.put(DBContract.Booking_Table.COLUMN_CONFIRMED_DEST_TIME,confirmedDestTime);
        contentValues.put(DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE,Integer.valueOf(bookingComplete).toString());
        contentValues.put(DBContract.Booking_Table.COLUMN_NOTE,note);
        return true;
    }
    private void setDuration(String _duration){
        // set the duration of the booking. pick up to destination time
        duration = _duration;
        if(duration != null){
            setEstDestTime();
        }
    }
    private void setEstDestTime(){
        // set the estimated destination time
        String[] durationInfo = duration.split(" ");
        Log.d("DURATION",duration+" "+estArrivalTimeCalendar.get(Calendar.HOUR));
        int addHour = 0;
        int addMin;
        if(durationInfo.length >2) {
            addHour = Integer.valueOf(durationInfo[0]); // add journey hour
            addMin = Integer.valueOf(durationInfo[2]); // add journey minutes
        }else{
            addMin = Integer.valueOf(durationInfo[0]);
        }
        // create a new calendar instance and set the new properties
        estDestTimeCalendar =  Calendar.getInstance();
        estDestTimeCalendar.set(Calendar.HOUR,estArrivalTimeCalendar.get(Calendar.HOUR)+addHour);
        estDestTimeCalendar.set(Calendar.MINUTE,estArrivalTimeCalendar.get(Calendar.MINUTE)+addMin);
        estDestTimeCalendar.set(Calendar.SECOND,estArrivalTimeCalendar.get(Calendar.SECOND));
        // if day change
        if(estDestTimeCalendar.getTimeInMillis()< estArrivalTimeCalendar.getTimeInMillis()){
            estDestTimeCalendar.set(Calendar.DAY_OF_MONTH,estDestTimeCalendar.get(Calendar.DAY_OF_MONTH)+1);
            estDestTimeCalendar.set(Calendar.DAY_OF_MONTH,estDestTimeCalendar.get(Calendar.HOUR)-12);
        }

        estDestTime = getTimestamp(estDestTimeCalendar);
    }
    public void setAssignedDriverId(int val){assignedDriverId= val;}
    public void setDate(String val){this.date = val;}
    public void setId(int val){this.id = val;}
    public void setPickUpName(String val ){ pickUpName = val;}
    public void setDestName(String val){ destName=val;}
    public void setPrice(String val){ price=val;}
    public void setPickUpLatitude(Double val){ pickUpLatitude=val;}
    public void setPickUpLongitude(Double val){ pickUpLongitude=val;}
    public void setDestLatitude(Double val){ destLatitude=val;}
    public void setDestLongitude(Double val){ destLongitude=val;}
    public void setEstArrivalTime(String val){ estArrivalTime=val;}
    public void setEstDestTime(String val){  estDestTime=val;}
    public void setConfirmedArrivalTime(String val){  confirmedArrivalTime=val;}
    public void setConfirmedDestTime(String val){  confirmedDestTime=val;}
    public void setBookingComplete(int val) { bookingComplete=val;}
    public void setNote(String val){
        note=val;
        if(note.isEmpty() || note.equals("")){
            note="-1";
    }}
    public void setPickUpAddress(Address address){
        pickUpAddress = address;
        pickUpLatitude = address.getLatitude();
        pickUpLongitude = address.getLongitude();
    }
    public void setDestAddress(Address address){
        destAddress = address;
        destLatitude = address.getLatitude();
        destLongitude = address.getLongitude();
    }
    public void setestArrivalTimeCalendar(Calendar val){ estArrivalTimeCalendar = val;}
    public void setEstDestTimeCalendar(Calendar val){ estDestTimeCalendar = val;}

    public String getDate(){return date;}
    public String getPickUpName(){return pickUpName;}
    public String getDestName(){return destName;}
    public String getPrice(){
        return String.format(Locale.UK,"%.2f",Double.valueOf(price));
    }
    public Double getPickUpLatitude(){return pickUpLatitude;}
    public Double getPickUpLongitude(){return pickUpLongitude;}
    public Double getDestLatitude(){return destLatitude;}
    public Double getDestLongitude(){return destLongitude;}
    public String getEstArrivalTime(){return estArrivalTime;}
    public String getEstDestTime(){ return estDestTime;}
    public String getConfirmedArrivalTime(){ return confirmedArrivalTime;}
    public String getConfirmedDestTime(){ return confirmedDestTime;}
    public int getBookingComplete() {return bookingComplete;}
    public String getNote(){return note;}
    public int getId(){return id;}
    public HashMap<String,String> getParams() { return params;}
    public ContentValues getContentValues() { return contentValues;}
    public Address getPickUpAddress(){return pickUpAddress;}
    public Address getDestAddress(){return destAddress;}
    public Calendar getEstArrivalTimeCalendar(){ return estArrivalTimeCalendar;}
    public Calendar getEstDestTimeCalendar(){return estDestTimeCalendar;}
    public String getDuration(){return duration;}

    public Bundle getUpdateBookingBundle(){
        // return a Bundle containing  all the information needed for updating a booking
        Bundle argsBundle = new Bundle();
        argsBundle.putString(BookingPagerAdapter.UPDATE_BOOKING,"true");
        argsBundle.putInt(BookingPagerAdapter.UPDATE_BOOKING_ID,id);

        argsBundle.putString(BookingPagerAdapter.UPDATE_BOOKING_PICK_UP_LOCATION_NAME,pickUpName);
        argsBundle.putDouble(BookingPagerAdapter.UPDATE_BOOKING_PICK_UP_LATITUDE,pickUpLatitude);
        argsBundle.putDouble(BookingPagerAdapter.UPDATE_BOOKING_PICK_UP_LONGITUDE,destLatitude);

        argsBundle.putString(BookingPagerAdapter.UPDATE_BOOKING_DEST_LOCATION_NAME,destName);
        argsBundle.putDouble(BookingPagerAdapter.UPDATE_BOOKING_DEST_LATITUDE,destLatitude);
        argsBundle.putDouble(BookingPagerAdapter.UPDATE_BOOKING_DEST_LONGITUDE,destLongitude);
        argsBundle.putString(BookingPagerAdapter.UPDATE_BOOKING_NOTE,note);
        return argsBundle;
    }
    public Bundle getBookingBundle(){
        // return a bundle containing all the properties of a booking
        Bundle argsBundle = new Bundle();
        argsBundle.putString(DBContract.Booking_Table.COLUMN_PICK_UP_NAME,pickUpName);
        argsBundle.putDouble(DBContract.Booking_Table.COLUMN_PICK_UP_LATITUDE,pickUpLatitude);
        argsBundle.putDouble(DBContract.Booking_Table.COLUMN_PICK_UP_LONGITUDE,destLatitude);
        argsBundle.putString(DBContract.Booking_Table.COLUMN_DEST_NAME,destName);
        argsBundle.putDouble(DBContract.Booking_Table.COLUMN_DEST_LATITUDE,destLatitude);
        argsBundle.putDouble(DBContract.Booking_Table.COLUMN_DEST_LONGITUDE,destLongitude);
        argsBundle.putString(DBContract.Booking_Table.COLUMN_PRICE,price);
        argsBundle.putString(DBContract.Booking_Table.COLUMN_EST_ARRIVAL_TIME,estArrivalTime);
        argsBundle.putString(DBContract.Booking_Table.COLUMN_EST_DEST_TIME,estDestTime);
        argsBundle.putString(DBContract.Booking_Table.COLUMN_CONFIRMED_ARRIVAL_TIME,confirmedArrivalTime);
        argsBundle.putString(DBContract.Booking_Table.COLUMN_CONFIRMED_DEST_TIME,confirmedDestTime);
        argsBundle.putInt(DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE,bookingComplete);
        argsBundle.putString(DBContract.Booking_Table.COLUMN_NOTE,note);

        return argsBundle;
    }

    public void  getRouteInfo(){
        // gets the price, the duration and distance of the route
        class GetRouteInfo extends AsyncTask<Void,Void,String> {
            // local class variables
            private String tripDistance;
            private String tripDuration;
            private String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="; // using google maps api

            public String getTripDistance(){
                return tripDistance;
            }
            public String getTripDuration(){
                return tripDuration;
            }
            private GetRouteInfo() {
                //constructor
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(getResultListener != null) {
                    getResultListener.onGetResult();
                }
            }

            @Override
            protected String doInBackground(Void... parameters) {
                // http requests done on separate thread
                String googleURL = url+pickUpAddress.getLatitude()+","+pickUpAddress.getLongitude()+"&destinations="+destAddress.getLatitude()+","+destAddress.getLongitude()+"&key="+TaxiConstants.GOOGLE_API_KEY;
                try {
                    URL url = new URL(googleURL);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setRequestMethod("GET");
                    int httpResponseCode = conn.getResponseCode(); // get response from google maps api
                    conn.disconnect();
                    if(httpResponseCode == HttpURLConnection.HTTP_OK) {
                        // if response ok get results and
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String responseData;
                        StringBuilder responseBuilder = new StringBuilder();
                        while ((responseData = bufferedReader.readLine()) != null) {
                            responseBuilder.append(responseData);
                        }
                        String result = responseBuilder.toString(); // return result as string
                        JSONObject jsonResult = new JSONObject(result); // convert to JSON object
                        // Go through JSON object to get the info needed
                        JSONObject jsonRowElements = jsonResult.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                        tripDistance = (String)jsonRowElements.getJSONObject("distance").get("text");
                        tripDuration = (String)jsonRowElements.getJSONObject("duration").get("text");
                        Log.d("TRIP INFORMATION",tripDistance+","+tripDuration);
                        String tempPrice = (Double.valueOf((Double.valueOf(tripDistance.split(" ")[0])*PRICE_PER_MILE)+FAIR_PRICE)).toString();
                        setPrice(tempPrice.substring(0,tempPrice.length()-1)); // calculate price
                        setDuration(tripDuration);
                        return tripDistance+","+tripDuration; // return info not really needed
                    }
                }catch(Exception e){
                    Log.d("ERROR",e.getMessage());
                }
                return "NULL";
            }
        }
        // create instance of GetRouteInfo and execute
        GetRouteInfo routeInfo = new GetRouteInfo();
        routeInfo.execute();
    }
    public void setBookingComplete(){
        // called when the booking is completed. When the user gets in the taxi
        if(id==-1){return;}
        TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase(context);
        setBookingComplete(1);
        setParams();
        HashMap<String,String> params = new HashMap<>();
        params.put("id",String.valueOf(id));
        conn.updateBookingComplete(params); // update online database
        setContentValues();
        context.getContentResolver().insert(DBContract.Booking_Table.CONTENT_URI,getContentValues()); // update local database
    }
    public static String getTimestamp(Calendar calendar){
        SimpleDateFormat df = new SimpleDateFormat(TIME_STAMP_FORMAT,Locale.UK);
        return df.format(calendar.getTime());
    }
}
