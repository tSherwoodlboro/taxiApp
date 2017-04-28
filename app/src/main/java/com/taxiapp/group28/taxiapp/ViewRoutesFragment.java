package com.taxiapp.group28.taxiapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tom on 26/04/2017.
 */

public class ViewRoutesFragment extends Fragment {

    private String[] args = new String[1];
    private View view;
    private   LoaderManager loaderManager=null;
    public ViewRoutesFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null){
            return view;
        }
        view = inflater.inflate(R.layout.fragment_routes, container, false);
       // loadRoutes();
        Log.d("LOAD_ROUTES","true");
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        loaderManager = this.getLoaderManager();
        if(this.loaderManager == null){
            Log.d("FRAGMENT_ROUTE","Loader manager null");
        }

        Log.d("FRAGMENT_ROUTE","Activity created");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("FRAGMENT_ROUTE","VIEW RESUME");
        loadRoutes();
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("FRAGMENT_ROUTE","VIEW PAUSE");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("FRAGMENT_ROUTE","VIEW START");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("FRAGMENT_ROUTE","VIEW STOP");
    }
    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
    }
    private void addBooking(Route route){
        route.setContentValues();
        Booking selectedBooking = new Booking(this.getActivity(),route.getRouteBundle());
        AddBookingFragment addBookingFragment = new AddBookingFragment();
        Bundle argsBundle = selectedBooking.getBookingBundle();
        argsBundle.putString(BookingPagerAdapter.USING_ROUTE,"true");
        addBookingFragment.setArguments(argsBundle);
        MainMenuActivity mainMenuActivity = (MainMenuActivity)this.getActivity();
        mainMenuActivity.loadFragment(addBookingFragment,MainMenuActivity.ADD_BOOKING_FRAGMENT_POSITION,true);

    }
    private void removeRoute(Route route, final int position){
        final TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase(getActivity());
        HashMap<String,String> params = new HashMap<>();
        params.put("id",String.valueOf(route.getId()));
        final String[] args = {params.get("id")};
        conn.deleteRoute(params);
        conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
            @Override
            public void onGetResult() {
                if (conn.getResultMessage().equals(Integer.valueOf(TaxiAppOnlineDatabase.SUCCESS).toString())) {
                    ViewRoutesFragment.this.getActivity().getContentResolver().delete(DBContract.Route_Table.CONTENT_URI,DBContract.Route_Table._ID+" = ?",args);
                    ListView listView = (ListView)view.findViewById(R.id.routes_listview);
                    RouteAdapter routeAdapter = (RouteAdapter)listView.getAdapter();
                    routeAdapter.getData().remove(position);
                    routeAdapter.notifyDataSetChanged();
                }else{
                    Log.d("ERROR", "An error occurred");
                }
            }
        });
    }

    private void loadRoutes(){
        if(view == null){
            return;
        }
        Log.d("FRAGMENT_ROUTE","LOAD_ROUTES");

        RouteLoader routeLoader = new RouteLoader();
        if(args[0] ==null) {
            args[0] = SharedPreferencesManager.getUserPreferences(ViewRoutesFragment.this.getActivity()).getString(getString(R.string.user_preferred_user_id_pref_key), null); // users id
        }
        // start loader if not started otherwise restart loader
        if(loaderManager == null){
            loaderManager = this.getLoaderManager();
        }
        if(loaderManager.getLoader(3) == null) {
            Log.d("CURSOR","Initiated");
            loaderManager.initLoader(3, null, routeLoader);

        }else {
            Log.d("CURSOR","Restarted");
            loaderManager.restartLoader(3, null, routeLoader);
        }

    }
    class RouteLoader implements LoaderManager.LoaderCallbacks<Cursor>{
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle){
            Log.d("CURSOR","created");
            return new CursorLoader(ViewRoutesFragment.this.getActivity(),DBContract.Route_Table.CONTENT_URI,null,null,null,DBContract.Route_Table._ID+" DESC");
        }
        public void onLoadFinished(Loader<Cursor> loader, Cursor data){
            Log.d("CURSOR","finished");
            ListView routesListView = (ListView)view.findViewById(R.id.routes_listview);
            if(data.getCount() > 0){
                RouteAdapter routeAdapter = new RouteAdapter(ViewRoutesFragment.this.getActivity(),addRoutes(data));
                routesListView.setAdapter(routeAdapter);
            }else{
                routesListView.setAdapter(MainMenuActivity.getNoResultAdapter(ViewRoutesFragment.this.getActivity()));
            }
        }
        public void onLoaderReset(Loader<Cursor> loader){

        }
    }
    private ArrayList<Route> addRoutes(Cursor data) {
        ArrayList<Route> routeList = new ArrayList<>();
        while (data.moveToNext()) {
            // create a route object
            Route currentRoute = new Route(getActivity(),
                    data.getInt(data.getColumnIndex(DBContract.Route_Table._ID)),
                    data.getString(data.getColumnIndex(DBContract.Route_Table.COLUMN_NAME)),
                    data.getString(data.getColumnIndex(DBContract.Route_Table.COLUMN_PICK_UP_NAME)),
                    data.getDouble(data.getColumnIndex(DBContract.Route_Table.COLUMN_PICK_UP_LATITUDE)),
                    data.getDouble(data.getColumnIndex(DBContract.Route_Table.COLUMN_PICK_UP_LONGITUDE)),
                    data.getString(data.getColumnIndex(DBContract.Route_Table.COLUMN_DEST_NAME)),
                    data.getDouble(data.getColumnIndex(DBContract.Route_Table.COLUMN_DEST_LATITUDE)),
                    data.getDouble(data.getColumnIndex(DBContract.Route_Table.COLUMN_DEST_LONGITUDE)),
                    data.getInt(data.getColumnIndex(DBContract.Route_Table.COLUMN_TIMES_USED)),
                    data.getString(data.getColumnIndex(DBContract.Route_Table.COLUMN_NOTE)));
            routeList.add(currentRoute);
        }
        return routeList;
    }

    class RouteAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Route> data;
        private  LayoutInflater inflater = null;
        public RouteAdapter(Context _context, ArrayList<Route> _data){
            context = _context;
            data = _data;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount(){
            return data.size();
        }
        public ArrayList<Route> getData(){return data;}
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
                vi = inflater.inflate(R.layout.route_item_row, null);
            TextView routeNameTextView = (TextView)vi.findViewById(R.id.item_route_name);
            routeNameTextView.setText("Name: "+data.get(position).getName());
            TextView pickUpTextView = (TextView)vi.findViewById(R.id.item_route_pick_up);
            pickUpTextView.setText("Pick Up: "+data.get(position).getPickUpName());
            TextView destTextView = (TextView)vi.findViewById(R.id.item_route_dest);
            destTextView.setText("Destination: "+data.get(position).getDestName());
            ImageButton optionsButton = (ImageButton)vi.findViewById(R.id.item_route_option_button);
            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(ViewRoutesFragment.this.getActivity(),v);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.option_route_book:
                                    addBooking(data.get(position));
                                    return true;
                                case R.id.option_route_remove:
                                    removeRoute(data.get(position),position);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    menuInflater.inflate(R.menu.menu_route_options,popupMenu.getMenu());
                    popupMenu.show();
                }

            });
            return vi;
        }
    }
}
