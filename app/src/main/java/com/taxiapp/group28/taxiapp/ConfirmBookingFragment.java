package com.taxiapp.group28.taxiapp;

import android.app.NotificationManager;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by Tom on 04/04/2017.
 */

public class ConfirmBookingFragment extends Fragment {
    //constants for calculating fair price
    private static final Double FAIR_PRICE = 5.0;
    private static final Double PRICE_PER_MILE=1.2;

    private Address pickUpAddress=null;
    private Address destAddress=null;
    private MapView mapView=null;
    private static final String POINTS = "PointCoords";
    private static final String PICK_UP_POINT_TEXT = "Pick Up Point";
    private static final String DESTINATION_POINT_TEXT = "Destination Point";
    private GoogleMap confirmMap=null; // map for confirm page
    private Calendar pickUpTime=null; // pick up time
    private Calendar estDestTime=null;
    private String pickUpName=null;
    private String destName=null;
    private String pickUpNote=null;
    private final static int BOOKING_COMPLETE=-1;
    private String pickUpTimeString=null; // string value of pick up time
    private String price=null; // price of booking
    private String duration=null; // time to get to dest from pick up point
    private View view=null; // view associated with layout
    private boolean gotBookingDetails = false;
    public static final String BOOKING_COMPLETE_MESSAGE = "Booking Complete";
    public static final String BOOKING_ERROR_MESSAGE = "Booking Error! Please try again later.";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // check if view set if not set up class
        if(view == null) {
            view = inflater.inflate(R.layout.confirm_tab, container, false); // assign view
            initialiseMapView(); // initialise viewmap
            Button bookButton = (Button) view.findViewById(R.id.add_booking_button); // add on click listener to booking button
            bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add booking to database
                    if(!gotBookingDetails){
                        return;
                    }
                    final TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase();
                    HashMap<String,String> data = new HashMap<>(); // hash map for parameters
                    // -1 values represent empty
                    data.put("user_id","1"); // add the parameter key and value
                    data.put("assigned_driver_id","1");
                    data.put("pick_up_name",pickUpName);
                    data.put("dest_name",destName);
                    data.put("pick_up_latitude",new Double(pickUpAddress.getLatitude()).toString());
                    data.put("pick_up_longitude",new Double(pickUpAddress.getLongitude()).toString());
                    data.put("dest_name",pickUpName);
                    data.put("dest_latitude",new Double(destAddress.getLatitude()).toString());
                    data.put("dest_longitude",new Double(destAddress.getLongitude()).toString());
                    data.put("price",price);
                    data.put("est_arrival_time",getTimestamp(pickUpTime));
                    data.put("est_dest_time",getTimestamp(estDestTime));
                    data.put("confirmed_arrival_time","-1");
                    data.put("confirmed_dest_time","-1");
                    data.put("booking_complete",new Integer(BOOKING_COMPLETE).toString());
                    data.put("note",pickUpNote);
                    conn.addBooking(data); // call the method
                    // set an onclick listener for result
                    conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
                        @Override
                        public void onGetResult() {
                            // check result isn't null
                            if(conn.getResult() !=null){
                                String message;
                                // create toast of either booking complete or booking error
                                Log.d("RESULT",conn.getResultMessage()+"   "+new Integer(TaxiAppOnlineDatabase.SUCCESS).toString());
                                if(conn.getResultMessage().equals(new Integer(TaxiAppOnlineDatabase.SUCCESS).toString())){
                                    message = BOOKING_COMPLETE_MESSAGE;
                                    createConfirmNotification();

                                }else{
                                    message = BOOKING_ERROR_MESSAGE;
                                }
                                Toast toast = Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
                }
            });
        }
        Log.d("FRAGMENT_STATE_CONFIRM","Initialise");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState ){
        super.onActivityCreated(savedInstanceState);
        mapView.onCreate(savedInstanceState);
        Log.d("FRAGMENT_STATE_CONFIRM","Created");
    }
    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
        if(isMapNeeded()){
            loadMap(); // reload the map fragment with points if fragment reloaded
        }
        Log.d("FRAGMENT_STATE_CONFIRM","Resume");
    }
    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
        Log.d("FRAGEMENT_STATE_CONFIRM","Pause");
    }
    @Override
    public void onStart(){
        super.onStart();
        mapView.onStart();
        Log.d("FRAGMENT_STATE_CONFIRM","Start");
    }
    @Override
    public void onStop(){
        super.onStop();
        mapView.onStop();
        Log.d("FRAGMENT_STATE_CONFIRM","Stop");
    }
    @Override public void onDestroy(){
        mapView.onDestroy();
        super.onDestroy();
        Log.d("FRAGMENT_STATE_CONFIRM","Destroy");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mapView.onSaveInstanceState(savedInstanceState);
        //Bundle bundle = new Bundle();
    }
    private void initialiseMapView(){
        mapView = (MapView)view.findViewById(R.id.confirm_map_view); // initialise the mapView variable
    }

    private void loadMap(){
        Log.d("LOAD_MAP","Loaded map");
        // get map async
        mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    // map ready
                    confirmMap = googleMap; // store map as class variable
                    confirmMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            setUpMap(); // when map loaded set the points
                        }
                    });
                }
            });
    }
    private void createConfirmNotification(){
        NotificationManager mNotifyMgr = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle("Taxi App Booking")
                        .setContentText("Booking Confirmed");
        mNotifyMgr.notify(0,mBuilder.build());
    }
    private void setUpMap(){
        // set the points for the map if needed
        if(!isMapNeeded()){
            return;
        }
        Log.d("LOAD_MAP","set up");
        // get coords for both pick up and dest
        LatLng pickUpLatLng = new LatLng(pickUpAddress.getLatitude(),pickUpAddress.getLongitude());
        LatLng destLatLng = new LatLng(destAddress.getLatitude(),destAddress.getLongitude());
        confirmMap.clear();
        addMarker(confirmMap,pickUpLatLng, PICK_UP_POINT_TEXT);
        addMarker(confirmMap,destLatLng, DESTINATION_POINT_TEXT);
        // create a lat/lng boundary based on the 2 points
        LatLngBounds.Builder cameraBounds = new LatLngBounds.Builder();
        cameraBounds.include(pickUpLatLng);
        cameraBounds.include(destLatLng);
        //googleMap.setLatLngBoundsForCameraTarget(cameraBounds.build());
        confirmMap.moveCamera(CameraUpdateFactory.newLatLngBounds(cameraBounds.build(), 0)); // move camera to appropriate position
        confirmMap.animateCamera(CameraUpdateFactory.zoomOut()); // zoom out for better viewing
    }
    private void addMarker(GoogleMap map,LatLng location,String markerText) {
        // adds markers/points to the map
        try {

            map.addMarker(new MarkerOptions()
                    .position(location)
                    .title(markerText));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
        } catch (Exception e) {
            return;
        }
    }
    public boolean isMapNeeded(){
        // map only needed if both coords are present
        return(pickUpAddress != null && destAddress !=null);
    }
    public void setPickUpName(String text){
        pickUpName = text;
    }
    public void setDestName(String text){
        destName = text;
    }
    public void setPickUpNote(String note){
        pickUpNote = note;
        if(pickUpNote.isEmpty()){
            pickUpNote="-1";
        }
    }
    public void setPickUpAddress(Address address){
        pickUpAddress = address;
    }
    public void setDestAddress(Address address){
        destAddress = address;
        loadMap(); // final coords set load map
        getRouteInfo();
    }

    private void setPrice(String _price){
        price = _price;
    }
    private void setDuration(String _duration){
        duration = _duration;
        if(duration != null){
            setEstDestTime();
        }
    }
    private void setEstDestTime(){
        estDestTime = pickUpTime;
        estDestTime.set(Calendar.MINUTE,estDestTime.get(Calendar.MINUTE)+new Integer(duration.split(" ")[0]));
    }
    public void setPickUpTime(Calendar val){
        pickUpTime = val;
    }
    private void setTextUI(String _price,String _duration,String estTime){
        // sets the UI text
        TextView priceLabel = (TextView)view.findViewById(R.id.confirm_price_label);
        TextView estTimeLabel = (TextView)view.findViewById(R.id.est_arrival_time_label);
        TextView estPickUpTimeLabel = (TextView)view.findViewById(R.id.estimated_pick_up_time);
        priceLabel.setText(_price);
        estTimeLabel.setText(_duration);
        estPickUpTimeLabel.setText(estTime);
    }

    private void  getRouteInfo(){
        // gets the price, the duration and distance of the route
        class GetRouteInfo extends AsyncTask<Void,Void,String> {
            // local class variables
            private String tripDistance;
            private String tripDuration;
            private String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=";

            public String getTripDistance(){
                return tripDistance;
            }
            public String getTripDuration(){
                return tripDuration;
            }
            public GetRouteInfo() {
                //constructor
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // update the UI with the info
                SimpleDateFormat dateFormat = new SimpleDateFormat("E HH:mm");
                pickUpTimeString = dateFormat.format(pickUpTime.getTime()).toString();
                setTextUI("Price: £"+price,"Estimated Travel Time: "+duration,"Estimated Pick Up Time: "+pickUpTimeString);
                gotBookingDetails=true;
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
                        setPrice((new Double((new Double(tripDistance.split(" ")[0])*PRICE_PER_MILE)+FAIR_PRICE)).toString().substring(0,4)); // calculate price
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
    public static String getTimestamp(Calendar calendar){
        SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:MM:SS");
        Log.d("TIMESTAMP",df.format(calendar.getTime()).toString());
        return df.format(calendar.getTime()).toString();
    }
}
