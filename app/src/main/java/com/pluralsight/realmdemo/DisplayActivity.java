package com.pluralsight.realmdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.pluralsight.realmdemo.model.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class DisplayActivity extends AppCompatActivity {

    private Realm myRealm;
    private UsersAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        myRealm = Realm.getDefaultInstance();

        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void setupRecyclerView() {

        RealmResults<User> userList = myRealm.where(User.class).findAll();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        adapter = new UsersAdapter(this, myRealm, userList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }
}
