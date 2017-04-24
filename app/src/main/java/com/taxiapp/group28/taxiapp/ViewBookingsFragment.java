package com.taxiapp.group28.taxiapp;

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

import java.util.ArrayList;

public  class ViewBookingsFragment extends Fragment {
    private String[] args = new String[1];
    private View view;

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
    private void loadBookings(){
        if(view == null){
            return;
        }
        LiveBookingLoader liveBookingLoader = new LiveBookingLoader();
        PreviousBookingLoader previousBookingLoader = new PreviousBookingLoader();
        if(args[0] ==null) {
            args[0] = SharedPreferencesManager.getUserPreferences(ViewBookingsFragment.this.getActivity()).getString(getString(R.string.user_preferred_user_id_pref_key), null); // users id
        }
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
            BookingAdapter bookingAdapter = new BookingAdapter(ViewBookingsFragment.this.getActivity(),addBookings(data));
            ListView bookingsListView = (ListView)view.findViewById(R.id.live_bookings_listview);
            bookingsListView.setAdapter(bookingAdapter);
            bookingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("LOAD BOOKING","LOAD BOOKING");
                    Booking selectedBooking = (Booking) parent.getAdapter().getItem(position);
                    Bundle argsBundle = new Bundle();
                    argsBundle.putString(BookingPagerAdapter.UPDATE_BOOKING,"true");
                    argsBundle.putInt(BookingPagerAdapter.UPDATE_BOOKING_ID,selectedBooking.getId());

                    argsBundle.putString(BookingPagerAdapter.UPDATE_BOOKING_PICK_UP_LOCATION_NAME,selectedBooking.getPickUpName());
                    argsBundle.putDouble(BookingPagerAdapter.UPDATE_BOOKING_PICK_UP_LATITUDE,selectedBooking.getPickUpLatitude());
                    argsBundle.putDouble(BookingPagerAdapter.UPDATE_BOOKING_PICK_UP_LONGITUDE,selectedBooking.getPickUpLongitude());

                    argsBundle.putString(BookingPagerAdapter.UPDATE_BOOKING_DEST_LOCATION_NAME,selectedBooking.getDestName());
                    argsBundle.putDouble(BookingPagerAdapter.UPDATE_BOOKING_DEST_LATITUDE,selectedBooking.getDestLatitude());
                    argsBundle.putDouble(BookingPagerAdapter.UPDATE_BOOKING_DEST_LONGITUDE,selectedBooking.getDestLongitude());

                    AddBookingFragment addBookingFragment = new AddBookingFragment();
                    addBookingFragment.setArguments(argsBundle);
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
            BookingAdapter bookingAdapter = new BookingAdapter(ViewBookingsFragment.this.getActivity(),addBookings(data));
            ListView bookingsListView = (ListView)view.findViewById(R.id.previous_bookings_listview);
            bookingsListView.setAdapter(bookingAdapter);
        }
        public void onLoaderReset(Loader<Cursor> loader){

        }
    }
    private ArrayList<Booking> addBookings(Cursor data){
        ArrayList<Booking> bookings = new ArrayList<>();
        if(data.getCount()==0){
            // no previous bookings
        }
        while(data.moveToNext()){
            bookings.add(new Booking(data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_DATE)),
                    data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_PICK_UP_NAME)),
                    data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_DEST_NAME)),
                    data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_PRICE)),
                    data.getDouble(data.getColumnIndex(DBContract.Booking_Table.COLUMN_PICK_UP_LATITUDE)),
                    data.getDouble(data.getColumnIndex(DBContract.Booking_Table.COLUMN_PICK_UP_LONGITUDE)),
                    data.getDouble(data.getColumnIndex(DBContract.Booking_Table.COLUMN_DEST_LATITUDE)),
                    data.getDouble(data.getColumnIndex(DBContract.Booking_Table.COLUMN_DEST_LONGITUDE)),
                    data.getInt(data.getColumnIndex(DBContract.Booking_Table._ID))));
        }
        return bookings;
    }

    class BookingAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<Booking> data;
        private  LayoutInflater inflater = null;
        public BookingAdapter(Context _context, ArrayList<Booking> _data){
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
        public View getView(int position, View convertView, ViewGroup parent) {
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
            priceTextView.setText("Price: "+data.get(position).getPrice());
            Button addRoute = (Button)vi.findViewById(R.id.add_route_button);
            addRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // to be implemented
                }
            });
            return vi;
        }
    }

    class Booking {
        private String date = null;
        private String pickUpName = null;
        private String destName = null;
        private String price =null;
        private Double pickUpLatitude;
        private Double pickUpLongitude;
        private Double destLatitude;
        private Double destLongitude;
        private int id =-1;
        Booking(String date,String pickUpName,String destName,String price,Double pickUpLatitude, Double pickUpLongitude, Double destLatitude, Double destLongitude, int id){
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
        private String getDate(){return date;}
        private String getPickUpName(){return pickUpName;}
        private String getDestName(){return destName;}
        private String getPrice(){return price;}
        private Double getPickUpLatitude(){return pickUpLatitude;}
        private Double getPickUpLongitude(){return pickUpLongitude;}
        private Double getDestLatitude(){return destLatitude;}
        private Double getDestLongitude(){return destLongitude;}
        private int getId(){return id;}
    }
}
