package com.taxiapp.group28.taxiapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Tom on 27/03/2017.
 */

public class TaxiAppContentProvider extends ContentProvider {
    TaxiAppRequestHandler requestHandler;
    @Override
    public boolean onCreate() {
        requestHandler = new TaxiAppRequestHandler();
        return true;
    }
    @Override
    public String getType(Uri uri) {
        return "";
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}
