package com.taxiapp.group28.taxiapp;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Tom on 27/04/2017.
 */

public class SyncDbTest {
    @Test
    public void syncDb() throws Exception {
        // Context of the app under test.
        SyncDatabases syncDatabases = new SyncDatabases(InstrumentationRegistry.getTargetContext(),86);
        syncDatabases.syncDataBase();
        syncDatabases.setOnSyncCompleteListener(new SyncDatabases.onSyncCompleteListener() {
            @Override
            public void onSyncComplete() {
                assertEquals(true,true);
            }
        });

    }
}
