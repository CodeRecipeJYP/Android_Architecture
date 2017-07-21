package com.jonbott.knownspies.Activities.SecretDetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jonbott.knownspies.Activities.SpyList.SpyListActivity;
import com.jonbott.knownspies.Helpers.Constants;
import com.jonbott.knownspies.Helpers.Threading;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;
import com.jonbott.knownspies.R;

import io.realm.Realm;

public class SecretDetailsActivity extends AppCompatActivity {
    private static final String TAG = "SecretDetailsActivity";

    private SecretDetailsPresenter presenter;

    ProgressBar progressBar;
    TextView crackingLabel;
    Button finishedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_details);

        Log.d(TAG, "onCreate: ");
        setupUI();
        parseBundle();
    }

    private void configure(SecretDetailsPresenter presenter) {
        Log.d(TAG, "configure() called with: presenter = [" + presenter + "]");
        this.presenter = presenter;
        presenter.crackPassword(password -> {
            Log.d(TAG, "crackPassword/Consumer called with: password= [" + password+ "]");
            progressBar.setVisibility(View.GONE);
            crackingLabel.setText(presenter.password);
        });
    }

    //region Helper Methods

    private void setupUI() {
        Log.d(TAG, "setupUI: ");
        progressBar    = (ProgressBar) findViewById(R.id.secret_progress_bar);
        crackingLabel  = (TextView)    findViewById(R.id.secret_cracking_label);
        finishedButton = (Button)      findViewById(R.id.secret_finished_button);

        finishedButton.setOnClickListener(v -> {
            Log.d(TAG, "setupUI: v=[" + v.toString() + "]");
            finishedClicked();
        } );

    }

    //region Dependency Method
    private void setupPresenterFor(int spyId) {
        Log.d(TAG, "setupPresenterFor() called with: spyId = [" + spyId + "]");
        configure(new SecretDetailsPresenter(spyId));
    }
    //endregion

    private void parseBundle() {
        Log.d(TAG, "parseBundle: ");
        Bundle b = getIntent().getExtras();

        if(b != null) {
            int spyId = b.getInt(Constants.spyIdKey);
            setupPresenterFor(spyId);
        }
    }

    //endregion

    //region User Interaction

    private void finishedClicked() {
        Intent intent = new Intent(this, SpyListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    //endregion

}
