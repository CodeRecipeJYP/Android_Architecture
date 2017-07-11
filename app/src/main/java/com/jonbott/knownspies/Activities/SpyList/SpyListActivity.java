package com.jonbott.knownspies.Activities.SpyList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jonbott.knownspies.Activities.Details.SpyDetailsActivity;
import com.jonbott.knownspies.Helpers.Constants;
import com.jonbott.knownspies.Helpers.Threading;
import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;
import com.jonbott.knownspies.ModelLayer.Enums.Source;
import com.jonbott.knownspies.ModelLayer.Translation.SpyTranslator;
import com.jonbott.knownspies.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpyListActivity extends AppCompatActivity {

    private static final String TAG = "SpyListActivity";

    private List<Spy> spies = new ArrayList<>();
    private RecyclerView recyclerView;

    private SpyTranslator spyTranslator = new SpyTranslator();
    private Realm realm = Realm.getDefaultInstance();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spy_list);

        setupUI();
        setupData();
    }

    //region Helper Methods
    private void setupUI() {
        LinearLayoutManager manager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.spy_recycler_view);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
    }

    private void setupData() {
        try {
            initializeListView();
            initializeData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Data Process specific to SpyListActivity
    private void initializeData() throws Exception {

        loadSpiesFromLocal();
        notifyDataReceived(Source.local);

        loadJson(json -> {
            notifyDataReceived(Source.network);
            persistJson(json, () -> loadSpiesFromLocal());
        });
    }

    //endregion

    //region User Interaction

    private void rowTapped(int position) {
        Spy spy = spies.get(position);
        gotoSpyDetails(spy.id);
    }

    private void notifyDataReceived(Source source) {
        String message = String.format("Data from %s", source.name());
        Toast.makeText(SpyListActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    //endregion

    //region Database Methods

    private void loadSpiesFromLocal() throws Exception {
        Log.d(TAG, "Loading spies from DB");
        loadSpiesFromRealm(spyList -> {

            spies = spyList;

            SpyViewAdapter adapter = (SpyViewAdapter) recyclerView.getAdapter();

            adapter.spies = spyList;
            adapter.notifyDataSetChanged();
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
            result = run("http://localhost:8080/");

            //fake server delay
            Thread.sleep(2000);

        } catch (Exception e) {
            Log.d(TAG, "makeWebCall: Failed!");
            e.printStackTrace();
        }

        return result;
    }

    private String run(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    //endregion

    //region List View Adapter

    private void initializeListView() {
        SpyViewAdapter adapter = new SpyViewAdapter(spies, (v, position) -> rowTapped(position));
        recyclerView.setAdapter(adapter);
    }

    //endregion

    //region Navigation

    private void gotoSpyDetails(int spyId) {

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.spyIdKey, spyId);

        Intent intent = new Intent(SpyListActivity.this, SpyDetailsActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    //endregion

}
