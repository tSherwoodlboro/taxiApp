package com.taxiapp.group28.taxiapp;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom on 26/03/2017.
 *  Version 1
 */
public class TaxiAppRequestHandler {
    private static final String AUTH_TOKEN = "taxiAppTeam28"; // auth code
    private static final String API_URL = "http://group10.sci-project.lboro.ac.uk/taxiAppAPI.php?auth_token="+AUTH_TOKEN; // url for api
    private static final String ERROR = "[{error: '3'}]";

    public TaxiAppRequestHandler(){

    }

    public String  sendGetRequest (String type, HashMap<String,String> dataParams) {
        // send get request
        return sendRequest("GET",type,dataParams);
    }
    public String  sendPostRequest (String type, HashMap<String,String> dataParams) {
        // send post request
        return sendRequest("POST",type,dataParams);
    }
    private String sendRequest(String method,String type, HashMap<String,String> dataParams) {
        URL url;
        HttpURLConnection conn=null;
        try{
            url = new URL(API_URL); // create new url
            conn = (HttpURLConnection)url.openConnection(); // create and open connections http
            // set up connection properties
            conn.setDoInput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod(method);
            conn.setDoOutput(true);

            OutputStream outputStream = conn.getOutputStream(); // create an output stream for url parameters
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(bindParams(type,dataParams)); // add the parameters to stream send to api
            // close
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            int httpResponseCode = conn.getResponseCode(); // get response code
            if(httpResponseCode == HttpURLConnection.HTTP_OK){
                // if response ok get results using buffer and string builder to append results
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String responseData;
                StringBuilder responseBuilder = new StringBuilder();
                while((responseData = bufferedReader.readLine()) != null){
                    responseBuilder.append(responseData);
                }
                bufferedReader.close();
                conn.disconnect();
                return responseBuilder.toString();
            }
        }catch(MalformedURLException e){
            Log.d("Error1",""+e.getMessage());
        }
        catch(IOException e1) {
            Log.d("Error2", "" + e1.getMessage());
        }catch(RuntimeException e2){
           Log.d("Error3",""+e2.getMessage());
        }finally {
            if(conn != null) {
                try {
                    conn.disconnect(); // close connection
                }catch(Exception e){
                    Log.d("ERROR4",e.getMessage());
                }
            }

        }
        return ERROR;
    }


    private String bindParams(String type,HashMap<String,String> dataParams){
        // bind the parameters into the correct format for the url
        StringBuilder params = new StringBuilder();

        try {
            //bind initial parameters of the request type
            params.append(URLEncoder.encode(type, "UTF-8"));
            params.append("=");
            params.append(URLEncoder.encode("1", "UTF-8"));
            // bind the remaining data parameters
            for (Map.Entry<String, String> param : dataParams.entrySet()) {
                params.append("&");
                params.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                params.append("=");
                params.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            }
            Log.d("PARAMS",params.toString());
            return params.toString();
        }catch(UnsupportedEncodingException e){
            return "";
        }
    }
}
