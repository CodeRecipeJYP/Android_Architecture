package com.jonbott.knownspies.ModelLayer.Database.Realm;

import io.realm.RealmObject;

/**
 * Created by j on 4/24/17.
 */

public class Spy extends RealmObject {
    public int id;
    public int age;
    public String name;
    public String gender;
    public String password;
    public String imageName;
    public boolean isIncognito;
}
