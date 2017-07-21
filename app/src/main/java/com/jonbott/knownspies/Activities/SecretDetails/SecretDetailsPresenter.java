package com.jonbott.knownspies.Activities.SecretDetails;

import android.util.Log;
import android.view.View;

import com.jonbott.knownspies.Helpers.Threading;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;

import io.reactivex.functions.Consumer;
import io.realm.Realm;

/**
 * Created by jaeyoung on 7/21/17.
 */

class SecretDetailsPresenter {
    private static final String TAG = "SecretDetailsPresenter";
    private Realm realm = Realm.getDefaultInstance();

    private Spy spy;
    public String password;

    public SecretDetailsPresenter(int spyId) {
        Log.d(TAG, "SecretDetailsPresenter() called with: spyId = [" + spyId + "]");
        spy = getSpy(spyId);

        password = spy.password;
    }

    public void crackPassword(Consumer<String> finished) {
        Log.d(TAG, "crackPassword: ");
        Threading.async(()-> {
            //fake processing work
            Thread.sleep(2000);
            return true;
        }, success -> {
            finished.accept(password);
        });
    }

    //region Data loading

    public Spy getSpy(int id) {
        Log.d(TAG, "getSpy() called with: id = [" + id + "]");
        Spy tempSpy = realm.where(Spy.class).equalTo("id", id).findFirst();
        return realm.copyFromRealm(tempSpy);
    }

    //endregion

}
