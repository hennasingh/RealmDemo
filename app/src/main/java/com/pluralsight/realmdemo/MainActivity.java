package com.pluralsight.realmdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pluralsight.realmdemo.model.SocialAccount;
import com.pluralsight.realmdemo.model.User;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

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

        final String id = UUID.randomUUID().toString();
        final String name = etPersonName.getText().toString();
        final int age = Integer.valueOf(etAge.getText().toString());
        final String socialAccountName = etSocialAccountName.getText().toString();
        final String status = etStatus.getText().toString();

//        try {
//            myRealm.beginTransaction();
//            myRealm.commitTransaction();
//        } catch (Exception e) {
//            myRealm.cancelTransaction();
//        }

        /**
         * No success or error methods associated with this call,
         * cannot be sure if data is added successfully or not even if the Toast msg says so.
         */
        myRealm.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                SocialAccount socialAccount = myRealm.createObject(SocialAccount.class);
                socialAccount.setName(socialAccountName);
                socialAccount.setStatus(status);

                User user = myRealm.createObject(User.class, id);
                user.setName(name);
                user.setAge(age);
                user.setSocialAccount(socialAccount);

                Toast.makeText(MainActivity.this, "Added Successfully", Toast.LENGTH_LONG).show();
            }
        });

	}

	// Add Data to Realm in the Background Thread.
	public void addUserToRealm_ASynchronously(View view) {

        final String id = UUID.randomUUID().toString();
        final String name = etPersonName.getText().toString();
        final int age = Integer.valueOf(etAge.getText().toString());
        final String socialAccountName = etSocialAccountName.getText().toString();
        final String status = etStatus.getText().toString();

        /**
         * \There are 3 variants of this method
         */
        realmAsyncTask = myRealm.executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {

                SocialAccount socialAccount = realm.createObject(SocialAccount.class);
                socialAccount.setName(socialAccountName);
                socialAccount.setStatus(status);

                User user = realm.createObject(User.class, id);
                user.setName(name);
                user.setAge(age);
                user.setSocialAccount(socialAccount);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Added Successfully", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }, new Realm.Transaction.OnSuccess() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Added Successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, new Realm.Transaction.OnError() {

            @Override
            public void onError(final Throwable error) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error on adding" + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                Log.e("MainActivity", "Error is " + error.getMessage());
            }
        });
	}

	public void displayAllUsers(View view) {
        RealmResults<User> userList = myRealm.where(User.class).findAll();

        StringBuilder builder = new StringBuilder();

        for (User user : userList) {
            builder.append("ID: ").append(user.getId());
            builder.append("\nName: ").append(user.getName());
            builder.append(", Age: ").append(user.getAge());

            SocialAccount socialAccount = user.getSocialAccount();
            builder.append("\nS'Account: ").append(socialAccount.getName());
            builder.append(", Status: ").append(socialAccount.getStatus()).append(" .\n\n");
        }

        Log.d(TAG + " Lists", builder.toString());
	}

    /**
     * Realm transaction needs to be cancelled if a call comes or activity goes in the background
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (realmAsyncTask != null && !realmAsyncTask.isCancelled()) {
            realmAsyncTask.cancel();
        }

    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
        myRealm.close(); // important to close else memory leaks

	}
}
