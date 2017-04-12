package com.taxiapp.group28.taxiapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        getLoaderManager().initLoader(0, null, this);
    }
    public Loader<Cursor> onCreateLoader(int id,Bundle bundle){
        Log.d("CURSOR","created");
        String[] args = {"1"}; // users id
        return new CursorLoader(this,DBContract.Booking_Table.CONTENT_URI,null,DBContract.Booking_Table.COLUMN_USER_ID+"= ?",args,null);

    }
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        Log.d("CURSOR","finished");
        ArrayList<Booking> bookings = new ArrayList<>();
        while(data.moveToNext()){
            bookings.add(new Booking(data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_DATE)),
                        data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_PICK_UP_NAME)),
                        data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_DEST_NAME)),
                        data.getString(data.getColumnIndex(DBContract.Booking_Table.COLUMN_PRICE))));
        }
        BookingAdapter bookingAdapter = new BookingAdapter(this,bookings);
        ListView bookingsListView = (ListView)this.findViewById(R.id.previou_bookings_listview);
        bookingsListView.setAdapter(bookingAdapter);

    }
    public void onLoaderReset(Loader<Cursor> loader){

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
        Booking(String date,String pickUpName,String destName,String price){
            this.date = date;
            this.pickUpName = pickUpName;
            this.destName = destName;
            this.price = price;
        }
        public String getDate(){return date;}
        public String getPickUpName(){return pickUpName;}
        public String getDestName(){return destName;}
        public String getPrice(){return price;}
    }
}
