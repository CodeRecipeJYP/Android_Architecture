package com.jonbott.knownspies;

import android.app.Application;
import android.util.Log;
import com.jonbott.knownspies.ModelLayer.Network.MockWebServer;
import java.io.IOException;
import io.realm.Realm;

/**
 * Created by j on 4/24/17.
 */

public class KnownSpiesApplication extends Application {

    private static final String TAG = "KnownSpiesApplication";

    MockWebServer server;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        try {
            server = new MockWebServer();
            Log.d(TAG, "Web Server Initialized");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to create Web Server");
        }
    }
}
