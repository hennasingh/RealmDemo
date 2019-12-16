package com.pluralsight.realmdemo;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmAplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("myFirstRealm.realm") // By default the name of db is"default.realm"
                .build();

        Realm.setDefaultConfiguration(configuration);


    }
}
