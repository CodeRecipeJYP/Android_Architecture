package com.jonbott.knownspies.ModelLayer.Network;

import android.util.Log;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by j on 4/26/17.
 */

public class MockWebServer extends NanoHTTPD {

    private static final String TAG = "MockWebServer";

    public MockWebServer() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Log.d(TAG, "Running! Point your browsers to http://localhost:8080/");
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.d(TAG, "serve: uri:" + session.getUri());

        String json = LocalJsonLoader.shared.loadSpies();

        Response response = newFixedLengthResponse(Response.Status.OK, "application/json", json);

        return response;
    }
}
