package com.jonbott.knownspies.Activities.SpyList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.jonbott.knownspies.Activities.Details.SpyDetailsActivity;
import com.jonbott.knownspies.Helpers.Constants;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;
import com.jonbott.knownspies.ModelLayer.Enums.Source;
import com.jonbott.knownspies.R;

import java.util.ArrayList;
import java.util.List;

public class SpyListActivity extends AppCompatActivity {

    private static final String TAG = "SpyListActivity";

    private SpyListPresenter spyListPresenter = new SpyListPresenter();
    private List<Spy> spies = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spy_list);

        attachUI();
        loadData();
    }

    private void loadData() {
        spyListPresenter.loadData(spies -> {
            this.spies = spies;

            SpyViewAdapter adapter = (SpyViewAdapter) recyclerView.getAdapter();
            adapter.spies = this.spies;
            adapter.notifyDataSetChanged();
        }, this::notifyDataReceived);
    }

    //region Helper Methods
    private void attachUI() {
        LinearLayoutManager manager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.spy_recycler_view);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        initializeListView();
    }

    //endregion

    //region Data Process specific to SpyListActivity

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
