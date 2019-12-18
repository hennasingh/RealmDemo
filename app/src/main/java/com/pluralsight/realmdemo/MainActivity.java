package com.pluralsight.realmdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pluralsight.realmdemo.model.SocialAccount;
import com.pluralsight.realmdemo.model.User;

import java.util.UUID;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Example from Android Realm Fundamentals- Pluralsight
 */
public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private EditText etPersonName, etAge, etSocialAccountName, etStatus;

	private Realm myRealm;
	private RealmAsyncTask realmAsyncTask;
    RealmChangeListener<RealmResults<User>> userListListener = new RealmChangeListener<RealmResults<User>>() {

        @Override
        public void onChange(RealmResults<User> users) {
            displayQueriedUsers(users);
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etPersonName = findViewById(R.id.etPersonName);
		etAge = findViewById(R.id.etAge);
		etSocialAccountName = findViewById(R.id.etSocialAccount);
		etStatus = findViewById(R.id.etStatus);

		myRealm = Realm.getDefaultInstance();

        Realm myAnotherRealm = RealmAplication.getAnotherRealm();

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

    private RealmResults<User> userList;

	public void displayAllUsers(View view) {
        userList = myRealm.where(User.class).findAll();
        displayQueriedUsers(userList);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        myRealm.close(); // important to close else memory leaks

	}

    public void sampleQueryExample(View view) {

        RealmQuery<User> realmQuery = myRealm.where(User.class);

        //can filter results on realmQuery
        realmQuery.greaterThan("age", 15); //Condition 1
        realmQuery.contains("name", "John"); //Condition 2

        RealmResults<User> userList = realmQuery.findAll();
        displayQueriedUsers(userList);

        //Alternatively, lets use Fluid Interface
        RealmResults<User> userList2 = myRealm.where(User.class)
                .greaterThan("age", 15)
                .contains("name", "john", Case.INSENSITIVE)
                .findAll();
        displayQueriedUsers(userList2);

    }

    public void sampleComplexQueries(View view) {

        RealmResults<User> userList1 = myRealm.where(User.class)
                .between("age", 20, 40)
                .findAll();
        displayQueriedUsers(userList1);

        //Chaining Queries
        RealmResults<User> userList2 = myRealm.where(User.class)
                .between("age", 20, 40)
                .beginGroup()
                .endsWith("name", "n")
                .or() //explicitly define OR
                .contains("name", "Pe")
                .endGroup()
                .findAll();
        displayQueriedUsers(userList2);

        //accessing child fields
        RealmResults<User> userList3 = myRealm.where(User.class)
                .findAll()
                .sort("socialAccount.name", Sort.DESCENDING); //ascending by default
        displayQueriedUsers(userList3);


    }

    public void updateFirstRecord(View view) {

        myRealm.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).findFirst();
                user.setName("New Name");
                user.setAge(47);

                SocialAccount socialAccount = user.getSocialAccount();
                if (socialAccount != null) {
                    socialAccount.setName("Snapchat");
                    socialAccount.setStatus("Going for a Stroll");
                }
            }
        });
    }

    public void deleteFromRealm(View view) {

        myRealm.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).findFirst();
                user.deleteFromRealm(); //Delete a specific Entry

//                RealmResults<User> userList = realm.where(User.class).findAll();
//                userList.deleteFirstFromRealm();
//                userList.deleteLastFromRealm();
//                userList.deleteFromRealm(3);
//                userList.deleteAllFromRealm();
            }
        });
    }

    /**
     * Realm transaction needs to be cancelled if a call comes or activity goes in the background
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (userList != null)
            userList.removeChangeListener(userListListener);
        // Or userList.removeAllChangeListeners();

        if (realmAsyncTask != null && !realmAsyncTask.isCancelled()) {
            realmAsyncTask.cancel();
        }

    }

    public void exploreMiscConcepts(View view) {

        userList = myRealm.where(User.class).findAllAsync();

        userList.addChangeListener(userListListener);

    }

    private void displayQueriedUsers(RealmResults<User> userList) {

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

    public void openDisplayActivity(View view) {

        Intent intent = new Intent(this, DisplayActivity.class);
        startActivity(intent);
    }
}
