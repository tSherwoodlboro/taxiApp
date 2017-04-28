package com.taxiapp.group28.taxiapp;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tom on 27/04/2017.
 */

public class GuideFragment extends Fragment {
    private View view=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_guide, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("GUIDE", "START");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("GUIDE", "STOP");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("GUIDE", "RESUME");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("GUIDE", "PAUSE");
    }
}