package com.jonbott.knownspies.Activities.SpyList;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jonbott.knownspies.Helpers.Threading;
import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;
import com.jonbott.knownspies.ModelLayer.Enums.Source;
import com.jonbott.knownspies.ModelLayer.Translation.SpyTranslator;

import java.io.IOException;
import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jaeyoung on 7/11/17.
 */

public class SpyListPresenter {
    private static final String TAG = "SpyListPresenter";
    private SpyTranslator spyTranslator = new SpyTranslator();
    private Realm realm = Realm.getDefaultInstance();
    private Gson gson = new Gson();

    //region Presenter Methods

    public void loadData(Consumer<List<Spy>> onNewResults, Consumer<Source> notifyDataReceived) {
        try {
            loadSpiesFromLocal(onNewResults);
            notifyDataReceived.accept(Source.local);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadJson(json -> {
            notifyDataReceived.accept(Source.network);
            persistJson(json, ()->loadSpiesFromLocal(onNewResults));
        });
    }

    //region Database Methods

    private void loadSpiesFromLocal(Consumer<List<Spy>> onNewResults) throws Exception {
        Log.d(TAG, "Loading spies from DB");
        loadSpiesFromRealm(spyList -> {
            onNewResults.accept(spyList);
        });
    }

    private void persistJson(String json, Action finished) {
        Threading.async(() -> {

            clearSpies(() -> {
                List<SpyDTO> dtos = convertJson(json);
                dtos.forEach(dto -> dto.initialize());
                persistDTOs(dtos);

                Threading.dispatchMain(() -> finished.run());
            });

            return true;
        });
    }

    private void loadSpiesFromRealm(Consumer<List<Spy>> finished) throws Exception {
        RealmResults<Spy> spyResults = realm.where(Spy.class).findAll();

        List<Spy> spies = realm.copyFromRealm(spyResults);
        finished.accept(spies);
    }

    private void clearSpies(Action finished) throws Exception {
        Log.d(TAG, "clearing DB");

        Realm backgroundRealm = Realm.getInstance(realm.getConfiguration());
        backgroundRealm.executeTransaction(r -> r.delete(Spy.class));

        finished.run();
    }

    private void persistDTOs(List<SpyDTO> dtos) {
        Log.d(TAG, "persisting dtos to DB");

        Realm backgroundRealm = Realm.getInstance(realm.getConfiguration());
        backgroundRealm.executeTransaction(r -> r.delete(Spy.class));

        //ignore result and just save in realm
        dtos.forEach(dto -> spyTranslator.translate(dto, backgroundRealm));
    }

    //endregion

    //region Network Methods

    private void loadJson(Consumer<String> finished) {
        Log.d(TAG, "loading json from web");

        Threading.async(() -> makeRequest(), finished, null);
    }

    @Nullable
    private List<SpyDTO> convertJson(String json) {
        Log.d(TAG, "converting json to dtos");

        TypeToken<List<SpyDTO>> token = new TypeToken<List<SpyDTO>>(){};

        return gson.fromJson(json, token.getType());
    }

    private String makeRequest() {
        String result = "";
        try {
            result = request("http://localhost:8080/");

            //fake server delay
            Thread.sleep(2000);

        } catch (Exception e) {
            Log.d(TAG, "makeWebCall: Failed!");
            e.printStackTrace();
        }

        return result;
    }

    private String request(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    //endregion

}
