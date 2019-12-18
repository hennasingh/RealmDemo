package com.pluralsight.realmdemo;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmAplication extends Application {

    public static Realm getAnotherRealm() {
        //configuration for different database

        RealmConfiguration myOtherConfig = new RealmConfiguration.Builder()
                .name("anotherRealm.realm")
                .build();

        Realm myAnotherRealm = Realm.getInstance(myOtherConfig);

        return myAnotherRealm;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("myFirstRealm.realm") // By default the name of db is"default.realm"
                //.modules(Realm.getDefaultModule()) //all model classes extending RealmObject
                .modules(new MyCustomModule())
                .build();

        Realm.setDefaultConfiguration(configuration);


    }
}
