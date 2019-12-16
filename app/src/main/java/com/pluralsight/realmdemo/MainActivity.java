package com.pluralsight.realmdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.pluralsight.realmdemo.model.SocialAccount;
import com.pluralsight.realmdemo.model.User;

import java.util.UUID;

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

        String id = UUID.randomUUID().toString();
        String name = etPersonName.getText().toString();
        int age = Integer.valueOf(etAge.getText().toString());
        String socialAccountName = etSocialAccountName.getText().toString();
        String status = etStatus.getText().toString();

        try {
            myRealm.beginTransaction();
            SocialAccount socialAccount = myRealm.createObject(SocialAccount.class);
            socialAccount.setName(socialAccountName);
            socialAccount.setStatus(status);

            User user = myRealm.createObject(User.class, id);
            user.setName(name);
            user.setAge(age);
            user.setSocialAccount(socialAccount);
            myRealm.commitTransaction();
        } catch (Exception e) {
            myRealm.cancelTransaction();
        }

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
