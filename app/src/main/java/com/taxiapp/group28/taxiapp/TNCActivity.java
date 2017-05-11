package com.taxiapp.group28.taxiapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Tom on 11/05/2017.
 */

public class TNCActivity extends AppCompatActivity {
    private boolean accepted = false;
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        if(saveInstanceState != null){
            accepted = saveInstanceState.getBoolean("accepted",false);
            this.onBackPressed();
        }
        setContentView(R.layout.activity_terms_and_conditions);
        Button button = (Button)this.findViewById(R.id.tnc_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accepted=true;
                finish();
            }
        });
    }
    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putBoolean("accepted",accepted);
    }
    @Override
    public void onBackPressed(){
        // do nothing
    }
}
