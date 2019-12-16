package com.pluralsight.realmdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;
import io.realm.RealmAsyncTask;

/**
 * Example from Android Realm Fundamentals- Pluralsight
 */
public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private EditText etPersonName, etAge, etSocialAccountName, etStatus;

	private Realm myRealm;
	private RealmAsyncTask realmAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etPersonName = findViewById(R.id.etPersonName);
		etAge = findViewById(R.id.etAge);
		etSocialAccountName = findViewById(R.id.etSocialAccount);
		etStatus = findViewById(R.id.etStatus);

		myRealm = Realm.getDefaultInstance();

	}

	// Add data to Realm using Main UI Thread. Be Careful: As it may BLOCK the UI.
	public void addUserToRealm_Synchronously(View view) {

	}

	// Add Data to Realm in the Background Thread.
	public void addUserToRealm_ASynchronously(View view) {

	}

	public void displayAllUsers(View view) {

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
