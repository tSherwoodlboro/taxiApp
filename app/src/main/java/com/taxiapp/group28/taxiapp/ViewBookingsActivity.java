package com.taxiapp.group28.taxiapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewBookingsActivity extends AppCompatActivity  {
    private String[] args = new String[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        LiveBookingLoader liveBookingLoader = new LiveBookingLoader();
        PreviousBookingLoader previousBookingLoader = new PreviousBookingLoader();
        args[0] =SharedPreferencesManager.getUserPreferences(ViewBookingsActivity.this).getString(getString(R.string.user_preferred_user_id_pref_key),null); // users id

        getLoaderManager().initLoader(0, null,liveBookingLoader);
        getLoaderManager().initLoader(1, null,previousBookingLoader);
    }
    class LiveBookingLoader implements LoaderManager.LoaderCallbacks<Cursor>{
        public Loader<Cursor> onCreateLoader(int id,Bundle bundle){
            Log.d("CURSOR","created");
            return new CursorLoader(ViewBookingsActivity.this,DBContract.Booking_Table.CONTENT_URI,null,DBContract.Booking_Table.COLUMN_USER_ID+"= ? AND "+DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE+"= -1",args,null);
        }
        public void onLoadFinished(Loader<Cursor> loader, Cursor data){
            Log.d("CURSOR","finished");
            BookingAdapter bookingAdapter = new BookingAdapter(ViewBookingsActivity.this,addBookings(data));
            ListView bookingsListView = (ListView)ViewBookingsActivity.this.findViewById(R.id.live_bookings_listview);
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
                    Intent updateBookingIntent = new Intent(ViewBookingsActivity.this,BookingActivity.class);
                    updateBookingIntent.putExtras(argsBundle);
                    startActivity(updateBookingIntent);
                }
            });
        }
        public void onLoaderReset(Loader<Cursor> loader){

        }
    }
    class PreviousBookingLoader implements LoaderManager.LoaderCallbacks<Cursor>{
        public Loader<Cursor> onCreateLoader(int id,Bundle bundle){
            Log.d("CURSOR","created");

            String[] args ={ SharedPreferencesManager.getUserPreferences(ViewBookingsActivity.this).getString(getString(R.string.user_preferred_user_id_pref_key),null)}; // users id
            if(args[0] == null){
                return null;
            }
            return new CursorLoader(ViewBookingsActivity.this,DBContract.Booking_Table.CONTENT_URI,null,DBContract.Booking_Table.COLUMN_USER_ID+"= ? AND "+DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE+"= 1",args,null);

        }
        public void onLoadFinished(Loader<Cursor> loader, Cursor data){
            Log.d("CURSOR","finished");
            BookingAdapter bookingAdapter = new BookingAdapter(ViewBookingsActivity.this,addBookings(data));
            final ListView bookingsListView = (ListView)ViewBookingsActivity.this.findViewById(R.id.previous_bookings_listview);
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
                vi = inflater.inflate(R.layout.booking_item_row, null);
            TextView dateTextView = (TextView)vi.findViewById(R.id.row_booking_date);
            dateTextView.setText("Date: "+data.get(position).getDate());
            TextView pickUpTextView = (TextView)vi.findViewById(R.id.row_booking_pick_up_name);
            pickUpTextView.setText("Pick Up Point: "+data.get(position).getPickUpName());
            TextView destTextView = (TextView)vi.findViewById(R.id.row_booking_dest_name);
            destTextView.setText("Dest Point: "+data.get(position).getDestName());
            TextView priceTextView = (TextView)vi.findViewById(R.id.row_booking_price);
            priceTextView.setText("Price: "+data.get(position).getPrice());
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
        public String getDate(){return date;}
        public String getPickUpName(){return pickUpName;}
        public String getDestName(){return destName;}
        public String getPrice(){return price;}
        public Double getPickUpLatitude(){return pickUpLatitude;}
        public Double getPickUpLongitude(){return pickUpLongitude;}
        public Double getDestLatitude(){return destLatitude;}
        public Double getDestLongitude(){return destLongitude;}
        public int getId(){return id;}
    }
}
