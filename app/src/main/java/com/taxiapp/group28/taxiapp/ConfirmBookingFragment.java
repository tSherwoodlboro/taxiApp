package com.taxiapp.group28.taxiapp;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by Tom on 04/04/2017.
 */

public class ConfirmBookingFragment extends Fragment {
    private Address pickUpAddress;
    private Address destAddress;
    private MapView mapView;
    private Calendar pickUpTime;
    private String price;
    private String duration;
    private static final Double FAIR_PRICE = 5.0;
    private static final Double PRICE_PER_MILE=1.2;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.confirm_tab, container, false);
        mapView = (MapView)view.findViewById(R.id.confirm_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        return view;
    }
    public void setPickUpTime(Calendar val){
        pickUpTime = val;
    }
    private void loadMap(){
        Log.d("LOAD_MAP","Loaded map");
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("LOAD_MAP","Map Ready");
                addMarker(googleMap,new LatLng(pickUpAddress.getLatitude(),pickUpAddress.getLongitude()), "Pick Up Point");
                addMarker(googleMap,new LatLng(destAddress.getLatitude(),pickUpAddress.getLongitude()), "Destination Point");
                LatLng latLng = new LatLng(pickUpAddress.getLatitude(),pickUpAddress.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14)); // move the camera to point
            }
        });

    }
    private void addMarker(GoogleMap map,LatLng location,String markerText) {
        try {
            map.addMarker(new MarkerOptions()
                    .position(location)
                    .title(markerText));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
            Geocoder geoCoder = new Geocoder(getActivity());
        } catch (Exception e) {
            return;
        }
    }
    public void setPickUpAddress(Address address){
        pickUpAddress = address;
    }
    public void setDestAddress(Address address){
        destAddress = address;
        loadMap();
        getRouteInfo();
    }
    private void  getRouteInfo(){

        class GetRouteInfo extends AsyncTask<Void,Void,String> {

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

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                TextView priceLabel = (TextView)view.findViewById(R.id.confirm_price_label);
                TextView estTimeLabel = (TextView)view.findViewById(R.id.est_arrival_time_label);
                TextView estPickUpTimeLabel = (TextView)view.findViewById(R.id.estimated_pick_up_time);
                priceLabel.setText("Price: £"+price);
                estTimeLabel.setText("Estimated Travel Time: "+duration);
                SimpleDateFormat dateFormat = new SimpleDateFormat("E HH:mm");
                estPickUpTimeLabel.setText("Estimated Pick Up Time: "+dateFormat.format(pickUpTime.getTime()).toString());
            }

            @Override
            protected String doInBackground(Void... parameters) {
                String googleURL = url+pickUpAddress.getLatitude()+","+pickUpAddress.getLongitude()+"&destinations="+destAddress.getLatitude()+","+destAddress.getLongitude()+"&key="+TaxiConstants.GOOGLE_API_KEY;
                try {
                    URL url = new URL(googleURL);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setRequestMethod("GET");
                    int httpResponseCode = conn.getResponseCode();
                    conn.disconnect();
                    if(httpResponseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String responseData;
                        StringBuilder responseBuilder = new StringBuilder();
                        while ((responseData = bufferedReader.readLine()) != null) {
                            responseBuilder.append(responseData);
                        }
                        String result = responseBuilder.toString();
                        JSONObject jsonResult = new JSONObject(result);
                        JSONObject jsonRowElements = jsonResult.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                        String tripDistance = (String)jsonRowElements.getJSONObject("distance").get("text");
                        String tripDuration = (String)jsonRowElements.getJSONObject("duration").get("text");
                        Log.d("TRIP INFORMATION",tripDistance+","+tripDuration);
                        price = (new Double((new Double(tripDistance.split(" ")[0])*PRICE_PER_MILE)+FAIR_PRICE)).toString();
                        duration = tripDuration;
                        return tripDistance+","+tripDuration;
                    }
                }catch(Exception e){
                    Log.d("ERROR",e.getMessage());
                }
                return "NULL";
            }
        }
        GetRouteInfo routeInfo = new GetRouteInfo();
        routeInfo.execute();

    }
}
