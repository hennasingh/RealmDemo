package com.pluralsight.realmdemo;

import com.pluralsight.realmdemo.model.Company;
import com.pluralsight.realmdemo.model.SocialAccount;
import com.pluralsight.realmdemo.model.User;

import io.realm.annotations.RealmModule;

@RealmModule(classes = {User.class, SocialAccount.class, Company.class})
public class MyCustomModule {
}
