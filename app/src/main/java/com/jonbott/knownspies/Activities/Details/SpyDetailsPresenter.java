package com.jonbott.knownspies.Activities.Details;

import android.content.Context;
import android.util.Log;

import com.jonbott.knownspies.Helpers.Helper;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;

import io.realm.Realm;

/**
 * Created by jaeyoung on 7/18/17.
 */

class SpyDetailsPresenter {
    private static final String TAG = "SpyDetailsPresenter";
    private Realm realm = Realm.getDefaultInstance();
    public int spyId;

    public String age;
    public String name;
    public String gender;
    public String imageName;
    public int imageId;

    private Context context;

    public SpyDetailsPresenter(int spyId) {
        Log.d(TAG, "SpyDetailsPresenter() called with: spyId = [" + spyId + "]");
        this.spyId = spyId;

        Spy spy = getSpy(spyId);

        age = String.valueOf(spy.age);
        name = spy.name;
        gender = spy.gender;
        imageName = spy.imageName;
    }

    public void configureWithContext(Context context) {
        Log.d(TAG, "configureWithContext() called with: context = [" + context + "]");
        this.context = context;

        imageId = Helper.resourceIdWith(context, imageName);
    }

    //region Data loading

    private Spy getSpy(int id) {
        Log.d(TAG, "getSpy() called with: id = [" + id + "]");
        Spy tempSpy = realm.where(Spy.class).equalTo("id", id).findFirst();
        return realm.copyFromRealm(tempSpy);
    }

    //endregion
}
