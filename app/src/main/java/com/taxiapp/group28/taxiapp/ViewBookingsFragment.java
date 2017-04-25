package com.taxiapp.group28.taxiapp;

import android.content.ContentValues;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public  class ViewBookingsFragment extends Fragment {
    private String[] args = new String[1];
    private View view;
    private boolean checkedBookings = false; // only check booking if completed at start.

    public ViewBookingsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null){
            return view;
        }
        view = inflater.inflate(R.layout.fragment_bookings, container, false);
        loadBookings();
        Log.d("LOAD_VIEW_BOOKINGS","true");
        if(savedInstanceState != null && savedInstanceState.containsKey("checkedBookings")){
            checkedBookings = savedInstanceState.getBoolean("checkedBookings");
        }
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        loadBookings();
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("FRAGMENT","VIEW RESUME");
        loadBookings();
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("FRAGMENT","VIEW PAUSE");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("FRAGMENT","VIEW START");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("FRAGMENT","VIEW STOP");
    }
    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putBoolean("checkedBookings",checkedBookings);
    }
    private void loadBookings(){
        if(view == null){
            return;
        }
        LiveBookingLoader liveBookingLoader = new LiveBookingLoader();
        PreviousBookingLoader previousBookingLoader = new PreviousBookingLoader();
        if(args[0] ==null) {
            args[0] = SharedPreferencesManager.getUserPreferences(ViewBookingsFragment.this.getActivity()).getString(getString(R.string.user_preferred_user_id_pref_key), null); // users id
        }
        // start loader if not started otherwise restart loader
        if(this.getActivity().getSupportLoaderManager().getLoader(0) == null) {
            this.getActivity().getSupportLoaderManager().initLoader(0, null, liveBookingLoader);
            this.getActivity().getSupportLoaderManager().initLoader(1, null, previousBookingLoader);
        }else{
            this.getActivity().getSupportLoaderManager().restartLoader(0, null, liveBookingLoader);
            this.getActivity().getSupportLoaderManager().restartLoader(1, null, previousBookingLoader);
        }
    }
    private class LiveBookingLoader implements LoaderManager.LoaderCallbacks<Cursor>{
        @Override
        public Loader<Cursor> onCreateLoader(int id,Bundle bundle){
            Log.d("CURSOR","created");
            return new CursorLoader(ViewBookingsFragment.this.getActivity(),DBContract.Booking_Table.CONTENT_URI,null,DBContract.Booking_Table.COLUMN_USER_ID+"= ? AND "+DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE+"= -1",args,null);
        }
        public void onLoadFinished(Loader<Cursor> loader, Cursor data){
            Log.d("CURSOR","finished");
            BookingAdapterCurrent bookingAdapter = new BookingAdapterCurrent(ViewBookingsFragment.this.getActivity(),addBookings(data,0));
            ListView bookingsListView = (ListView)view.findViewById(R.id.live_bookings_listview);
            bookingsListView.setAdapter(bookingAdapter);
            // on click listener to update booking for current booking
            bookingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Booking selectedBooking = (Booking) parent.getAdapter().getItem(position);
                    AddBookingFragment addBookingFragment = new AddBookingFragment();
                    addBookingFragment.setArguments(selectedBooking.getUpdateBookingBundle());
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    String key = Integer.toString(MainMenuActivity.UPDATE_BOOKINGS_FRAGMENT_POSITION);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame,addBookingFragment,key)
                            .addToBackStack(key)
                            .commit();
                }
            });
        }
        public void onLoaderReset(Loader<Cursor> loader){

        }
    }
    private class PreviousBookingLoader implements LoaderManager.LoaderCallbacks<Cursor>{
        public Loader<Cursor> onCreateLoader(int id,Bundle bundle){
            Log.d("CURSOR","created");

            String[] args ={ SharedPreferencesManager.getUserPreferences(ViewBookingsFragment.this.getActivity()).getString(getString(R.string.user_preferred_user_id_pref_key),null)}; // users id
            if(args[0] == null){
                return null;
            }
            return new CursorLoader(ViewBookingsFragment.this.getActivity(),DBContract.Booking_Table.CONTENT_URI,null,DBContract.Booking_Table.COLUMN_USER_ID+"= ? AND "+DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE+"= 1",args,null);

        }
        public void onLoadFinished(Loader<Cursor> loader, Cursor data){
            Log.d("CURSOR","finished");
            BookingAdapterPrevious bookingAdapter = new BookingAdapterPrevious(ViewBookingsFragment.this.getActivity(),addBookings(data,1));
            ListView bookingsListView = (ListView)view.findViewById(R.id.previous_bookings_listview);
            bookingsListView.setAdapter(bookingAdapter);
        }
        public void onLoaderReset(Loader<Cursor> loader){

        }
    }
    private ArrayList<Booking> addBookings(Cursor data,int state){
        ArrayList<Booking> bookings = new ArrayList<>();
        if(data.getCount()==0){
            // no previous bookings
        }
        boolean previousBooking;
        while(data.moveToNext()){
            previousBooking=false;
            // create a booking object
            Booking currentBooking = new Booking(getActivity(),
                    data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_DATE)),
                    data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_PICK_UP_NAME)),
                    data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_DEST_NAME)),
                    data.getDouble(data.getColumnIndex(DBContract.Booking_Table.COLUMN_PICK_UP_LATITUDE)),
                    data.getDouble(data.getColumnIndex(DBContract.Booking_Table.COLUMN_PICK_UP_LONGITUDE)),
                    data.getDouble(data.getColumnIndex(DBContract.Booking_Table.COLUMN_DEST_LATITUDE)),
                    data.getDouble(data.getColumnIndex(DBContract.Booking_Table.COLUMN_DEST_LONGITUDE)),
                    data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_PRICE)),
                    data.getInt(data.getColumnIndex(DBContract.Booking_Table._ID)));
            if(state ==0 && !checkedBookings) {
                currentBooking.setEstArrivalTime(data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_EST_ARRIVAL_TIME)));
                currentBooking.setEstDestTime(data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_EST_DEST_TIME)));
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.UK);
                    simpleDateFormat.parse(currentBooking.getEstDestTime());
                    if (simpleDateFormat.getCalendar().getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                        currentBooking.setBookingComplete();
                        previousBooking = true;
                    }
                } catch (Exception e) {
                    Log.d("TIME", e.getMessage() + " " + currentBooking.getEstDestTime());
                }
            }
            if(state== 1 || (state == 0 && previousBooking== false)){
                bookings.add(currentBooking);
            }
        }
        checkedBookings = true;
        return bookings;
    }
    // adapter for current bookings
    class BookingAdapterCurrent extends BaseAdapter{
        private Context context;
        private ArrayList<Booking> data;
        private  LayoutInflater inflater = null;
        public BookingAdapterCurrent(Context _context, ArrayList<Booking> _data){
            context = _context;
            data = _data;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount(){
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub

            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            if (vi == null)
                vi = inflater.inflate(R.layout.booking_item_row, null);
            TextView dateTextView = (TextView)vi.findViewById(R.id.row_booking_date);
            dateTextView.setText("Date: "+data.get(position).getDate());
            TextView pickUpTextView = (TextView)vi.findViewById(R.id.row_booking_pick_up_name);
            pickUpTextView.setText("Pick Up Point: "+data.get(position).getPickUpName());
            TextView destTextView = (TextView)vi.findViewById(R.id.row_booking_dest_name);
            destTextView.setText("Dest Point: "+data.get(position).getDestName());
            TextView priceTextView = (TextView)vi.findViewById(R.id.row_booking_price);
            priceTextView.setText("Price: £"+data.get(position).getPrice());

            return vi;
        }
    }
    // adapter for previous bookings
    class BookingAdapterPrevious extends BaseAdapter{
        private Context context;
        private ArrayList<Booking> data;
        private  LayoutInflater inflater = null;
        public BookingAdapterPrevious(Context _context, ArrayList<Booking> _data){
            context = _context;
            data = _data;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount(){
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub

            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            if (vi == null)
                vi = inflater.inflate(R.layout.booking_item_row_previous, null);
            TextView dateTextView = (TextView)vi.findViewById(R.id.row_booking_date_previous);
            dateTextView.setText("Date: "+data.get(position).getDate());
            TextView pickUpTextView = (TextView)vi.findViewById(R.id.row_booking_pick_up_name_previous);
            pickUpTextView.setText("Pick Up Point: "+data.get(position).getPickUpName());
            TextView destTextView = (TextView)vi.findViewById(R.id.row_booking_dest_name_previous);
            destTextView.setText("Dest Point: "+data.get(position).getDestName());
            TextView priceTextView = (TextView)vi.findViewById(R.id.row_booking_price_previous);
            priceTextView.setText("Price: £"+data.get(position).getPrice());
            Button addRouteBtn = (Button)vi.findViewById(R.id.add_route_button);
            addRouteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // to be implemented
                    Booking routeBooking = data.get(position);
                    Route newRoute = new Route(ViewBookingsFragment.this.getActivity(),null,routeBooking.getPickUpName(),routeBooking.getPickUpLatitude(),routeBooking.getPickUpLongitude(),
                            routeBooking.getDestName(),routeBooking.getDestLatitude(),routeBooking.getPickUpLongitude(),-1,null);

                    AddRouteFragment addRouteFragment = new AddRouteFragment();
                    addRouteFragment.setRoute(newRoute);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    String key = Integer.toString(MainMenuActivity.ADD_ROUTES_FRAGMENT_POSITION);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame,addRouteFragment,key)
                            .addToBackStack(key)
                            .commit();
                }
            });
            return vi;
        }
    }

}
