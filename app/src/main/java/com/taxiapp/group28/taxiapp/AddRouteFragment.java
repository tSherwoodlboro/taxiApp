package com.taxiapp.group28.taxiapp;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Tom on 25/04/2017.
 */

public class AddRouteFragment extends Fragment {
    private View view;
    private TextView nameTextView = null; // name of route
    private TextView noteTextView = null; // note for route
    private Route route;
    public AddRouteFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view!=null){
            return view;
        }
        view = inflater.inflate(R.layout.fragment_add_route, container, false);
        noteTextView = (TextView)view.findViewById(R.id.route_edit_note);
        nameTextView = (TextView)view.findViewById(R.id.route_edit_name);
        Button addRouteBtn = (Button)view.findViewById(R.id.route_add_button);
        addRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getRouteName() != null && !getRouteName().isEmpty()){
                    getRouteNote();
                    route.setParams();

                    final TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase();
                    Log.d("RESULT",route.getParams().toString());
                    conn.addRoute(route.getParams());
                    conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
                        @Override
                        public void onGetResult() {
                            String message = "Add Route Failed!";
                            if(conn.getResult() != null){
                                if(conn.getResultMessage().equals(Integer.valueOf(TaxiAppOnlineDatabase.SUCCESS).toString())) {
                                    route.setId(Integer.valueOf(conn.getInsertId()));
                                    route.setContentValues();
                                    message = "Added Route";
                                    AddRouteFragment.this.getActivity().getContentResolver().insert(DBContract.Route_Table.CONTENT_URI,route.getContentValues());
                                    AddRouteFragment.this.getActivity().getFragmentManager().popBackStack();
                                }
                            }
                            Toast toast = Toast.makeText(AddRouteFragment.this.getActivity(),message,Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }
        });
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("ADD_ROUTE","START");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("ADD_ROUTE","STOP");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("ADD_ROUTE","RESUME");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("ADD_ROUTE","PAUSE");
    }
    public void setRoute(Route route){
        if(route== null){
            return;
        }
        this.route = route;
    }
    public void setRouteName(String name){
        if (nameTextView == null) {

            return;
        }
        route.setName(name);
        nameTextView.setText(name);
    }
    public void setRouteNote(String note){
        if (noteTextView == null) {
            return;
        }
        route.setNote(note);
        noteTextView.setText(note);
    }
    public String getRouteName(){
        if (nameTextView == null) {
            return null;
        }
        route.setName(nameTextView.getText().toString());
        return nameTextView.getText().toString();
    }
    public String getRouteNote(){
        if (noteTextView == null) {
            return null;
        }
        route.setNote(noteTextView.getText().toString());
        return noteTextView.getText().toString();
    }
    public Route getRoute(){
        return route;
    }


}
