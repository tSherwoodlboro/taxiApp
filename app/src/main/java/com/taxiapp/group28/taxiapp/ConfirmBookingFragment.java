package com.taxiapp.group28.taxiapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
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
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by Tom on 04/04/2017.
 */

public class ConfirmBookingFragment extends Fragment {
    //constants for calculating fair price


    private MapView mapView = null;
    private static final String POINTS = "PointCoords";
    private static final String PICK_UP_POINT_TEXT = "Pick Up Point";
    private static final String DESTINATION_POINT_TEXT = "Destination Point";
    private GoogleMap confirmMap = null; // map for confirm page

    private String insert_id = "-1";
    private View view = null; // view associated with layout
    private boolean gotBookingDetails = false;
    public static final String BOOKING_COMPLETE_MESSAGE = "Booking Complete";
    public static final String BOOKING_ERROR_MESSAGE = "Booking Error! Please try again later.";
    public static final String BOOKING_UPDATED_MESSAGE ="Booking Updated";
    private boolean updateBooking = false;
    private Booking booking = null;
    private int bookingId;
    private static PendingIntent pendingIntent = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // check if view set if not set up class
        if (view == null) {
            view = inflater.inflate(R.layout.confirm_tab, container, false); // assign view
            initialiseMapView(); // initialise viewmap
            final Button bookButton = (Button) view.findViewById(R.id.add_booking_button); // add on click listener to booking button
            bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add booking to database
                    if (!gotBookingDetails) {
                        return;
                    }
                    final TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase();
                    // -1 values represent empty
                    if (!updateBooking) {
                        booking.setParams();
                        conn.addBooking(booking.getParams()); // call the method
                    } else {
                        Bundle argBundle = ConfirmBookingFragment.this.getArguments();
                        if (argBundle.get(BookingPagerAdapter.UPDATE_BOOKING) != null) {
                            booking.setId((int) argBundle.get(BookingPagerAdapter.UPDATE_BOOKING_ID));
                            booking.setParams();
                            conn.updateBooking(booking.getParams());
                        }
                    }
                    setPickUpAlarm();
                    // set an onclick listener for result
                    conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
                        @Override
                        public void onGetResult() {
                            // check result isn't null
                            if (conn.getResult() != null) {
                                String message;
                                // create toast of either booking complete or booking error
                                if (conn.getResultMessage().equals(Integer.valueOf(TaxiAppOnlineDatabase.SUCCESS).toString())) {
                                    message = BOOKING_COMPLETE_MESSAGE;
                                    if (!updateBooking) {
                                        createConfirmNotification();
                                        insert_id = conn.getInsertId();
                                        booking.setId(Integer.valueOf(insert_id));
                                    } else {
                                        booking.setId(bookingId);
                                        message = BOOKING_UPDATED_MESSAGE;
                                    }
                                    addBookingLocal();
                                    MainMenuActivity mainMenuActivity = (MainMenuActivity)ConfirmBookingFragment.this.getActivity();
                                    mainMenuActivity.loadFragment(new ViewBookingsFragment(),MainMenuActivity.VIEW_BOOKINGS_FRAGMENT_POSITION,true);
                                } else {
                                    message = BOOKING_ERROR_MESSAGE;
                                }
                                Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                Log.d("Error", "An error occurred");
                            }
                        }
                    });
                }
            });
        }
        //Log.d("FRAGMENT_STATE_CONFIRM","Initialise");
        isUpdatingBooking();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView.onCreate(savedInstanceState);
        Log.d("FRAGMENT_STATE_CONFIRM", "Created");
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (isMapNeeded()) {
            loadMap(); // reload the map fragment with points if fragment reloaded
        }
        Log.d("FRAGMENT_STATE_CONFIRM", "Resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        Log.d("FRAGMENT_STATE_CONFIRM", "Pause");
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        Log.d("FRAGMENT_STATE_CONFIRM", "Start");
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        Log.d("FRAGMENT_STATE_CONFIRM", "Stop");
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
        // Log.d("FRAGMENT_STATE_CONFIRM","Destroy");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(mapView != null){
            mapView.onSaveInstanceState(savedInstanceState);
        }
    }

    private boolean isUpdatingBooking() {
        if (updateBooking) {
            return true;
        }
        Bundle argBundle = this.getArguments();
        if (argBundle != null && argBundle.get("updateBooking") != null) {
            bookingId = (int) argBundle.get("bookingId");
            updateBooking = true;
            return true;
        } else {
            updateBooking = false;
            return false;
        }
    }

    public boolean isMapNeeded() {
        // map only needed if both coords are present
        return (booking != null && booking.getPickUpAddress() != null && booking.getDestAddress() != null);
    }

    private void initialiseMapView() {
        mapView = (MapView) view.findViewById(R.id.confirm_map_view); // initialise the mapView variable
    }

    private void loadMap() {
        Log.d("LOAD_MAP", "Loaded map");
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

    private void setUpMap() {
        // set the points for the map if needed
        if (!isMapNeeded()) {
            return;
        }
        Log.d("LOAD_MAP", "set up");
        // get coords for both pick up and dest
        LatLng pickUpLatLng = new LatLng(booking.getPickUpLatitude(), booking.getPickUpLongitude());
        LatLng destLatLng = new LatLng(booking.getDestLatitude(), booking.getDestLongitude());
        confirmMap.clear();
        addMarker(confirmMap, pickUpLatLng, PICK_UP_POINT_TEXT);
        addMarker(confirmMap, destLatLng, DESTINATION_POINT_TEXT);
        // create a lat/lng boundary based on the 2 points
        LatLngBounds.Builder cameraBounds = new LatLngBounds.Builder();
        cameraBounds.include(pickUpLatLng);
        cameraBounds.include(destLatLng);
        //googleMap.setLatLngBoundsForCameraTarget(cameraBounds.build());
        confirmMap.moveCamera(CameraUpdateFactory.newLatLngBounds(cameraBounds.build(), 0)); // move camera to appropriate position
        confirmMap.animateCamera(CameraUpdateFactory.zoomOut()); // zoom out for better viewing
    }
    public void setBooking(Booking _booking) {
        this.booking = _booking;
        booking.getRouteInfo();

        booking.setOnGetResultListener(new Booking.onGetResultListener() {
            @Override
            public void onGetResult() {
                // update the UI with the info
                SimpleDateFormat dateFormat = new SimpleDateFormat("E HH:mm", Locale.UK);
                String pickUpTimeString = dateFormat.format(booking.getEstArrivalTimeCalendar().getTime());
                setTextUI("Price: £"+booking.getPrice(),"Estimated Travel Time: "+booking.getDuration(),"Estimated Pick Up Time: "+pickUpTimeString);
                gotBookingDetails=true;
                loadMap();
            }
        });
    }
    private void addMarker(GoogleMap map, LatLng location, String markerText) {
        // adds markers/points to the map
        try {

            map.addMarker(new MarkerOptions()
                    .position(location)
                    .title(markerText));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
        } catch (Exception e) {
            //
        }
    }

    private void createConfirmNotification() {
        NotificationManager mNotifyMgr = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle("Taxi App Booking")
                        .setContentText("Booking Confirmed");
        mNotifyMgr.notify(0, mBuilder.build());
    }

    private void addBookingLocal() {
        Calendar currentDate = Calendar.getInstance();
        booking.setDate(Booking.getTimestamp(currentDate));
        booking.setContentValues();
        getActivity().getContentResolver().insert(DBContract.Booking_Table.CONTENT_URI, booking.getContentValues());
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
    private void setPickUpAlarm(){
        if(pendingIntent == null) {
            Intent intent = new Intent(this.getActivity(), PickUpTimeReceiver.class);
            int requestCode = 123456;
            pendingIntent = PendingIntent.getBroadcast(this.getActivity(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar.getInstance().getTimeInMillis();
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, booking.getEstArrivalTimeCalendar().getTimeInMillis(), pendingIntent);
        }
    }
}
