package com.taxiapp.group28.taxiapp;

import android.content.ContentValues;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public  class ViewBookingsFragment extends Fragment {
    private String[] args = new String[1];
    private View view;
    private boolean checkedBookings = false; // only check booking if completed at start.
    private LoaderManager loaderManager;
    public ViewBookingsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null){
            return view;
        }
        Log.d("NETWORK_ENABLED", "Is enabled "+TaxiAppOnlineDatabase.isNetworkEnabled(getActivity()));
        // load layout depending on screen orientation
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                view = inflater.inflate(R.layout.fragment_bookings, container, false);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                view = inflater.inflate(R.layout.fragment_bookings_landscape, container, false);
                break;
        }
        final SwipeRefreshLayout currentBookingsRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_current_bookings);
        if(currentBookingsRefreshLayout != null) {
            currentBookingsRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    checkedBookings = false;
                    loadBookings();
                    currentBookingsRefreshLayout.setRefreshing(false);
                }
            });
        }
        Log.d("LOAD_VIEW_BOOKINGS","true");
        if(savedInstanceState != null && savedInstanceState.containsKey("checkedBookings")){
            checkedBookings = savedInstanceState.getBoolean("checkedBookings");
        }
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        loaderManager = this.getLoaderManager();
        //loadBookings();
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
    private void updateBooking(Booking selectedBooking){
        // update a booking
        AddBookingFragment addBookingFragment = new AddBookingFragment();
        addBookingFragment.setArguments(selectedBooking.getUpdateBookingBundle()); // set arguments. update booking properties
        MainMenuActivity mainMenuActivity = (MainMenuActivity)ViewBookingsFragment.this.getActivity();
        mainMenuActivity.loadFragment(addBookingFragment,MainMenuActivity.VIEW_BOOKINGS_FRAGMENT_POSITION,true); // load add booking activity
    }
    private void removeBooking(Booking selectedBooking,final int position){
        // removes booking from databases and list view
        final TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase(getActivity());
        HashMap<String,String> params = new HashMap<>();
        params.put("id",String.valueOf(selectedBooking.getId()));
        final String[] args = {params.get("id")};
        conn.deleteBooking(params);
        conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
            @Override
            public void onGetResult() {
                if (conn.getResultMessage().equals(Integer.valueOf(TaxiAppOnlineDatabase.SUCCESS).toString())) {
                    ViewBookingsFragment.this.getActivity().getContentResolver().delete(DBContract.Booking_Table.CONTENT_URI,DBContract.Route_Table._ID+" = ?",args);
                    ListView listView = (ListView)view.findViewById(R.id.live_bookings_listview);
                    ViewBookingsFragment.BookingAdapterCurrent  currentBookingAdapter = (ViewBookingsFragment.BookingAdapterCurrent)listView.getAdapter();
                    currentBookingAdapter.getData().remove(position);
                    currentBookingAdapter.notifyDataSetChanged();
                    if(currentBookingAdapter.getData().isEmpty()){
                        listView.setAdapter(MainMenuActivity.getNoResultAdapter(ViewBookingsFragment.this.getActivity()));
                    }
                }else{
                    Log.d("ERROR", "An error occurred");
                }
            }
        });

    }
    private void loadBookings(){
        // load bookings into listViews
        if(view == null){
            return;
        }
        LiveBookingLoader liveBookingLoader = new LiveBookingLoader(); // create a  loader for live bookings
        PreviousBookingLoader previousBookingLoader = new PreviousBookingLoader(); // create a loader for previous bookings
        if(args[0] ==null) {
            args[0] = SharedPreferencesManager.getUserPreferences(ViewBookingsFragment.this.getActivity()).getString(getString(R.string.user_preferred_user_id_pref_key), null); // users id
        }
        if(loaderManager == null){
            loaderManager = this.getLoaderManager();
        }
        // start loader if not started otherwise restart loader
        if(loaderManager.getLoader(0) == null) {
            loaderManager.initLoader(0, null, liveBookingLoader);
            loaderManager.initLoader(1, null, previousBookingLoader);
        }else{
            loaderManager.restartLoader(0, null, liveBookingLoader);
            loaderManager.restartLoader(1, null, previousBookingLoader);
        }
    }
    private class LiveBookingLoader implements LoaderManager.LoaderCallbacks<Cursor>{
        @Override
        public Loader<Cursor> onCreateLoader(int id,Bundle bundle){
            Log.d("CURSOR","created"); // get data from content provider using CursorLoader.
            return new CursorLoader(ViewBookingsFragment.this.getActivity(),DBContract.Booking_Table.CONTENT_URI,null,DBContract.Booking_Table.COLUMN_USER_ID+"= ? AND "+DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE+"= -1",args,"_id ASC");
        }
        public void onLoadFinished(Loader<Cursor> loader, Cursor data){
            Log.d("CURSOR","finished");
            ListView bookingsListView = (ListView) view.findViewById(R.id.live_bookings_listview);
            if(data.getCount()>0){
                BookingAdapterCurrent bookingAdapter = new BookingAdapterCurrent(ViewBookingsFragment.this.getActivity(), addBookings(data, 0)); // create booking adapter
                bookingsListView.setAdapter(bookingAdapter); // set adapter
            }
            if(bookingsListView.getAdapter() == null || bookingsListView.getAdapter() != null && bookingsListView.getAdapter().isEmpty()) {
                bookingsListView.setAdapter(MainMenuActivity.getNoResultAdapter(ViewBookingsFragment.this.getActivity())); // load no results item
            }
        }
        public void onLoaderReset(Loader<Cursor> loader){

        }
    }
    private class PreviousBookingLoader implements LoaderManager.LoaderCallbacks<Cursor>{
        public Loader<Cursor> onCreateLoader(int id,Bundle bundle){
            Log.d("CURSOR","created");// get data from content provider using CursorLoader.
            return new CursorLoader(ViewBookingsFragment.this.getActivity(),DBContract.Booking_Table.CONTENT_URI,null,DBContract.Booking_Table.COLUMN_USER_ID+"= ? AND "+DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE+"= 1",args,"_id DESC");
        }
        public void onLoadFinished(Loader<Cursor> loader, Cursor data){
            Log.d("CURSOR","finished");
            ListView bookingsListView = (ListView)view.findViewById(R.id.previous_bookings_listview);
            if(data.getCount() >0){
                BookingAdapterPrevious bookingAdapter = new BookingAdapterPrevious(ViewBookingsFragment.this.getActivity(),addBookings(data,1)); // create booking adapter
                bookingsListView.setAdapter(bookingAdapter); // set adapter
            }
            if(bookingsListView.getAdapter() == null || bookingsListView.getAdapter() != null && bookingsListView.getAdapter().isEmpty()) {
                bookingsListView.setAdapter(MainMenuActivity.getNoResultAdapter(ViewBookingsFragment.this.getActivity())); // load no results item
            }
        }
        public void onLoaderReset(Loader<Cursor> loader){

        }
    }
    private ArrayList<Booking> addBookings(Cursor data,int state){
        // add bookings to the array list for the listviews
        // state 0 = live bookings
        // state 1 = previous bookings
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
                // check if the live bookings are old i.e. completed if so set them as complete.
                Log.d("VIEW_BOOKINGS_FRAGMENT","Checking bookings");
                currentBooking.setEstArrivalTime(data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_EST_ARRIVAL_TIME)));
                currentBooking.setEstDestTime(data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_EST_DEST_TIME)));
                currentBooking.setCalendars();
                if (currentBooking.getEstDestTimeCalendar().getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                    // check if the if the dest time is less than the current time if so set booking as complete
                    currentBooking.setBookingComplete();
                    previousBooking = true;
                }

            }
            if(state== 1 || (state == 0 && previousBooking== false)){
                bookings.add(currentBooking); // add booking to array list
            }
        }
        checkedBookings = true; // the bookings have been checked i.e. all live bookings are actually live bookings.
        return bookings;
    }
    // adapter for current bookings
    class BookingAdapterCurrent extends BaseAdapter{
        protected Context context;
        protected ArrayList<Booking> data;
        protected  LayoutInflater inflater = null;
        public BookingAdapterCurrent(Context _context, ArrayList<Booking> _data){
            context = _context;
            data = _data;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public ArrayList<Booking> getData(){return data;}
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
            // set item properties
            TextView dateTextView = (TextView)vi.findViewById(R.id.row_booking_date);
            dateTextView.setText("Date: "+data.get(position).getDate());
            TextView pickUpTextView = (TextView)vi.findViewById(R.id.row_booking_pick_up_name);
            pickUpTextView.setText("Pick Up Point: "+data.get(position).getPickUpName());
            TextView destTextView = (TextView)vi.findViewById(R.id.row_booking_dest_name);
            destTextView.setText("Dest Point: "+data.get(position).getDestName());
            TextView priceTextView = (TextView)vi.findViewById(R.id.row_booking_price);
            priceTextView.setText("Price: £"+data.get(position).getPrice());
            ImageButton optionsButton = (ImageButton)vi.findViewById(R.id.option_booking_button); // options button "Three vertical dots in listView item"
            // set an on click listener
            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(ViewBookingsFragment.this.getActivity(),v); // create a new pop up menu
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            if(!TaxiAppOnlineDatabase.isNetworkEnabled(context,0)){
                                return false;
                            }
                           if(data.get(position).getEstDestTimeCalendar() != null && data.get(position).getEstDestTimeCalendar().getTimeInMillis() < Calendar.getInstance(Locale.UK).getTimeInMillis()){
                               checkedBookings = false;
                               loadBookings();
                               return false;
                           }
                            switch(item.getItemId()) {
                                case R.id.option_booking_update:
                                    updateBooking(data.get(position)); // update booking
                                    return true;
                                case R.id.option_booking_cancel:
                                    removeBooking(data.get(position), position); // remove booking
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    menuInflater.inflate(R.menu.menu_current_booking_options,popupMenu.getMenu()); // inflate
                    popupMenu.show(); // show menu
                }

            });

            return vi;
        }
    }
    // adapter for previous bookings
    class BookingAdapterPrevious extends BookingAdapterCurrent{

        public BookingAdapterPrevious(Context _context, ArrayList<Booking> _data){
            super(_context,_data);
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            if (vi == null)
                vi = inflater.inflate(R.layout.booking_item_row_previous, null);
            // set item properties
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
                    // add a route
                    if(!TaxiAppOnlineDatabase.isNetworkEnabled(context,0)){
                        return;
                    }
                    Booking routeBooking = data.get(position); // get the booking
                    Route newRoute = new Route(ViewBookingsFragment.this.getActivity(),null,routeBooking.getPickUpName(),routeBooking.getPickUpLatitude(),routeBooking.getPickUpLongitude(),
                            routeBooking.getDestName(),routeBooking.getDestLatitude(),routeBooking.getPickUpLongitude(),-1,null); // create a new route instance

                    AddRouteFragment addRouteFragment = new AddRouteFragment(); // create a route fragment
                    addRouteFragment.setRoute(newRoute); // set the route for the route fragment
                    MainMenuActivity mainMenuActivity = (MainMenuActivity)ViewBookingsFragment.this.getActivity();
                    mainMenuActivity.loadFragment(addRouteFragment,MainMenuActivity.VIEW_BOOKINGS_FRAGMENT_POSITION,true);// load the route fragment to the screen
                }
            });
            return vi;
        }
    }

}
