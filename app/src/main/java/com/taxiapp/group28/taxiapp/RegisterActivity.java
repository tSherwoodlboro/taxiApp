package com.taxiapp.group28.taxiapp;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    private static final String REGEX_MOBILE_NUMBER = "^(\\+44\\s?7\\d{3}|\\(?07\\d{3}\\)?)\\s?\\d{3}\\s?\\d{3}$";  // source http://regexlib.com/UserPatterns.aspx?authorid=d95177b0-6014-4e73-a959-73f1663ae814&AspxAutoDetectCookieSupport=1
    private static final int  MY_PERMISSIONS_REQUEST_SEND_SMS =124; // permission constnt for sms permission
    private static final String MOBILE_VERIFIED_VAL = "-1"; // default  not verified value
    private static final String MOBILE_VERIFIED = "1"; // user verified value
    private static final String DEFAULT_USER_NAME = "User";
    private static final int REQUEST_CODE = 0; // constant
    private String verificationCode =null; // verification code sent to user
    private boolean smsPermission= false; // boolean to check if the app has been given sms permission
    private boolean oldAccountVerified=true; // if an existing user enters their details
    private String mobileNumber = null; // value of users mobile number from UI
    // UI components
    private EditText mobileNum=null;
    private Button registerBtn =null;
    private Button verifyButton = null;
    private EditText editVerificationCode = null;
    // user details from db
    private String userUserName=null;
    private String userTelNo=null;
    private String userPreferredDriverID = "1";
    private int userId=-1;
    private boolean verificationCodeReceived = false;
    private int requestCode=12345;
    private boolean tNCAccepted=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if(savedInstanceState == null) {
            isUserLoggedIn(); // checks if user logged in before
            setVerificationCode(); // create a random verification code
            Log.d("REGISTER","created null");
        }else{
            Log.d("REGISTER","Bundle: "+savedInstanceState.toString());
            // restore class variables
            oldAccountVerified = savedInstanceState.getBoolean("oldAccountVerified", true);
            verificationCode = savedInstanceState.getString("verificationCode", null);
            userPreferredDriverID = savedInstanceState.getString("userPreferredDriverID", "1");
            userUserName = savedInstanceState.getString("userUserName", null);
            userTelNo = savedInstanceState.getString("userTelNo", null);
            userId = savedInstanceState.getInt("userId", -1);
            mobileNumber = savedInstanceState.getString("mobileNumber",null);
            verificationCodeReceived = savedInstanceState.getBoolean("verificationCodeReady", false);
        }
        // st up UI objects
        if(!TaxiAppOnlineDatabase.isNetworkEnabled(RegisterActivity.this)){
            Toast toast = Toast.makeText(this,"Warning Network Disabled!",Toast.LENGTH_LONG);
            toast.show();
        }
        mobileNum = (EditText)this.findViewById(R.id.edit_mobile_number);
        editVerificationCode = (EditText)this.findViewById(R.id.edit_verification_code);
        registerBtn = (Button)this.findViewById(R.id.register_button);
        // add listeners for buttons
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             registerBtnPress();
            }
        });
        verifyButton = (Button)this.findViewById(R.id.verify_button);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               verifyUser(); // verify user
            }
        });
        if(verificationCodeReceived){
            enableRegister(false);
            enableVerify(true);
        }else{
            enableRegister(true);
            enableVerify(false);
        }
        Log.d("REGISTER","Created");
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        // save the class variables
        savedInstanceState.putBoolean("oldAccountVerified",oldAccountVerified);
        savedInstanceState.putString("verificationCode",verificationCode);
        savedInstanceState.putString("userPreferredDriverID",userPreferredDriverID);
        savedInstanceState.putString("userUserName",userUserName);
        savedInstanceState.putString("userTelNo",userTelNo);
        savedInstanceState.putInt("userId",userId);
        savedInstanceState.putString("mobileNumber",mobileNumber);
        savedInstanceState.putBoolean("verificationCodeReady",verificationCodeReceived);
    }
    @Override
    public void onRestoreInstanceState(Bundle bundle){
        super.onRestoreInstanceState(bundle);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        // getting permission for sending sms
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty. and set boolean "smsPermission"
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    smsPermission=true;
                    registerBtnPress();
                } else {
                    smsPermission = false;
                }
                return;
            }
        }
    }
    private void registerBtnPress(){
        if(!TaxiAppOnlineDatabase.isNetworkEnabled(RegisterActivity.this)){
            return;
        }
        String message = "Mobile Number Invalid"; // default message
        String mobileText = mobileNum.getText().toString(); // get mobile number
        if (!mobileText.isEmpty()) {

            if(mobileText.compareTo("12345")==0){
                //cheat code for tablet users. Used for testing, review and demo only.
                mobileNumber="12345";
                verificationCode="12345";
                verifyUser();
            }
            // if mobile number not empty check if it's valid
            if (mobileText.matches(REGEX_MOBILE_NUMBER)) {
                message = "Mobile Number Valid.";
                if(smsPermission || isPermissions()){
                    // if sms permission granted add user
                    mobileNumber = mobileText;
                    addUser();
                }else{
                    message="SMS Permission required";
                }
            }
        }
        makeToast(message);// make toast
    }
    private boolean isPermissions(){
        // check if the user has given permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            smsPermission= false;
            return smsPermission;
        }
        smsPermission= true;
        return smsPermission;
    }
    private void setVerificationCode(){
        // create a verification code
        Random randomNumber = new Random();
        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        StringBuilder stringBuilder = new StringBuilder(); // string builder to build code
        // create 2 random digits both 0-9
        int thirdDigit = randomNumber.nextInt(10);
        int fourthDigit = randomNumber.nextInt(10);
        stringBuilder.append(alphabet[randomNumber.nextInt(26)]); // pick a random Character from the alphabet
        stringBuilder.append(alphabet[randomNumber.nextInt(26)]);
        stringBuilder.append(thirdDigit);
        stringBuilder.append(fourthDigit);
        verificationCode = stringBuilder.toString(); // build and set verification code
        Log.d("VERIFICATION CODE", "Verification code "+ verificationCode);

    }
    private void sendVerificationSms(){
        // send an sms to the mobile number
        final String SENT = "SMS_SEND";
        // create pending intents for sending and receiving texts
        PendingIntent sentIntent = PendingIntent.getBroadcast(RegisterActivity.this,REQUEST_CODE,new Intent(SENT),0);
        try {

            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // when the user receives sms change the UI to allow verification code input
                    if (getResultCode() == Activity.RESULT_OK) {
                        //verify user
                        enableRegister(false);
                        enableVerify(true);
                        verificationCodeReceived=true;
                    } else {
                        makeToast("Sending Verification SMS Failed.");
                        enableRegister(true);
                        enableVerify(false);
                    }
                }
            }, new IntentFilter(SENT));
        }catch(Exception e){
            // receiver already registered
        }
        //create sms manager and send sms
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mobileNumber,null,"Verification Code: "+ verificationCode,sentIntent,null);
    }
    private void enableRegister(boolean val){
        // enable the register UI
        mobileNum.setEnabled(val);
        registerBtn.setEnabled(val);
    }
    private void enableVerify(boolean val){
        // enable the verify UI
        int visible;
        if(val){
            visible = View.VISIBLE;
        }else{
            visible = View.INVISIBLE;
        }
        verifyButton.setVisibility(visible);
        editVerificationCode.setVisibility(visible);
        verifyButton.setEnabled(val);
        editVerificationCode.setEnabled(val);
    }
    private void addUser(){
        // add a user to the database
        final TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase(this); // create a db instance
        HashMap<String,String> dataParams = new HashMap<>(); // hashmap for data
        Log.d("Mobile Number","Mobile Number "+mobileNumber);
        dataParams.put(DBContract.User_Table.COLUMN_TEL_NO,mobileNumber);
        dataParams.put(DBContract.User_Table.COLUMN_USER_NAME,DEFAULT_USER_NAME);
        dataParams.put(DBContract.User_Table.COLUMN_VERIFICATION_CODE, verificationCode);
        dataParams.put(DBContract.User_Table.COLUMN_VERIFIED,MOBILE_VERIFIED_VAL);
        conn.addUser(dataParams); // add the user
        // set a listener to get the response from the server
        conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
            @Override
            public void onGetResult() {

                if(conn != null && (conn.getResultMessage().equals(Integer.valueOf(TaxiAppOnlineDatabase.SUCCESS).toString()))) {
                    // if the result  is successful send a verification sms
                    sendVerificationSms();
                }else if(conn != null && conn.getResultMessage().equals(Integer.valueOf(TaxiAppOnlineDatabase.DB_UNIQUE_ERROR).toString())){
                    // The user already has an account. Therefore check that they are verified
                    oldAccountVerified = false;  // assume old account isn't verified
                    verifyUser();
                }else{
                    makeToast("An Error Occurred. Please try again later");// Error occurred
                }
                Log.d("RESULT", "ADD USER RESULT: "+conn.getResult());
            }
        });
    }
    private void verifyUser(){
        // verify the user
        final TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase(this); // create db instance
        HashMap<String,String> dataParams = new HashMap<>(); // hashmap for data
        dataParams.put("tel_no",mobileNumber); // mobile number needed as it uniquely identifies the user
        conn.getUser(dataParams); //  get the users info
        // get listener for  result
        conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
            @Override
            public void onGetResult() {
                if(conn != null){
                    JSONArray result = conn.getResult(); // get the result (The user information)
                    try {
                        // store user info in local variables
                        String userVerified = Integer.valueOf((int)result.getJSONObject(0).get(DBContract.User_Table.COLUMN_VERIFIED)).toString(); // if the user is verified
                        String userVerificationCode = editVerificationCode.getText().toString(); // the verification code the user entered
                        verificationCode = (String)result.getJSONObject(0).get(DBContract.User_Table.COLUMN_VERIFICATION_CODE);
                        userId = (int)result.getJSONObject(0).get("id");
                        userPreferredDriverID = String.valueOf(result.getJSONObject(0).get(DBContract.User_Table.COLUMN_PREFERRED_DRIVER_ID)); // default 1
                        userUserName = (String)result.getJSONObject(0).get(DBContract.User_Table.COLUMN_USER_NAME);
                        userTelNo = (String)result.getJSONObject(0).get(DBContract.User_Table.COLUMN_TEL_NO);
                        //userPreferredDriverID = "-1"; // proffered driver id NULL (-1) this value is set later in settings

                       if(verificationCode.compareTo("12345")!= 0 && !oldAccountVerified){
                           // if it's an existing account that wasn't verified send an sms and return
                           sendVerificationSms();
                           oldAccountVerified=true;
                           return;
                       }
                       // cheat code
                       if(verificationCode.compareTo("12345")==0){
                           userVerificationCode = "12345";
                       }
                       if(userVerificationCode.equals(verificationCode)){
                           // if the verification code is valid update the users details. Set verified to true
                           HashMap<String,String> dataParams = new HashMap<>(); // hashmap for data
                           dataParams.put(DBContract.User_Table.COLUMN_USER_NAME,userUserName);
                           dataParams.put(DBContract.User_Table.COLUMN_TEL_NO,userTelNo);
                           dataParams.put(DBContract.User_Table.COLUMN_VERIFIED, MOBILE_VERIFIED);
                           dataParams.put(DBContract.User_Table.COLUMN_PREFERRED_DRIVER_ID,userPreferredDriverID);
                           dataParams.put(DBContract.User_Table.COLUMN_VERIFICATION_CODE,userVerificationCode);
                           conn.setOnGetResultListener(null); // remove listener to prevent recursion
                           conn.updateUser(dataParams); // update user to verified
                           makeToast("Account Verified");
                           addUserToLocalStorage();
                       }else{
                           makeToast("Verification Code Invalid.");
                       }

                    }catch(JSONException e){
                        // error handling
                        Log.d("JSON ERROR",e.getMessage());
                        Log.d("Error","Account doesn't exist");
                        return;
                    }
                    conn.close(); // destroy db conn instance
                }
            }
        });
    }
    private void addUserToLocalStorage(){

        ContentValues data = new ContentValues();
        data.put(DBContract.User_Table._ID,userId);
        data.put(DBContract.User_Table.COLUMN_USER_NAME,userUserName);
        data.put(DBContract.User_Table.COLUMN_TEL_NO,userTelNo);
        if(userPreferredDriverID == "-1"){
            userPreferredDriverID = null;
        }
        data.put(DBContract.User_Table.COLUMN_PREFERRED_DRIVER_ID,userPreferredDriverID);
        getContentResolver().insert(DBContract.User_Table.CONTENT_URI,data);
        addUserPreferences();
        // sync database with online database (Loads users bookings if they were a previous user).
        SyncDatabases syncDatabases = new SyncDatabases(this,userId);
        syncDatabases.syncDataBase();
        Log.d("REGISTER","User id: "+userId);
        syncDatabases.setOnSyncCompleteListener(new SyncDatabases.onSyncCompleteListener() {
            @Override
            public void onSyncComplete() {
                loadBookingActivity(); // load booking activity
            }
        });

    }
    private void addUserPreferences(){
        // adds user preferences to local storage
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(TaxiConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(getString(R.string.user_name_pref_key), userUserName);
        editor.putString(getString(R.string.user_tel_no_pref_key), userTelNo);
        editor.putString(getString(R.string.user_preferred_driver_id_pref_key), userPreferredDriverID);
        editor.putString(getString(R.string.user_email_pref_key), null);
        editor.putString(getString(R.string.user_preferred_user_id_pref_key),new Integer(userId).toString());
        editor.apply();
    }
    private void loadBookingActivity(){
        // load Booking activity via intent
        Intent bookingIntent = new Intent(this,MainMenuActivity.class);
        startActivity(bookingIntent);
        this.finish();
    }
    private void  makeToast(String message){
        // make short toast
        Toast toast = Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }
    private boolean isUserLoggedIn(){
        // if the user has their details saved then skip login and load booking activity
        SharedPreferences sharedPref = getSharedPreferences(TaxiConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        final String defaultValue = "null";
        if(!sharedPref.getString(getString(R.string.user_tel_no_pref_key), defaultValue).equals(defaultValue)){
            loadBookingActivity();
        }else{
            Intent intent = new Intent(this,TNCActivity.class);
            startActivity(intent);
        }
        return false;
    }
}
