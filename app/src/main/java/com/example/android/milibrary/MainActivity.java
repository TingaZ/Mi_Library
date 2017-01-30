package com.example.android.milibrary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        //Checks if the user has logged on or not.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };

        checkIfUserExist();

    }


    private void checkIfUserExist() {

        if (mAuth.getCurrentUser() != null) {

            final String user_uid = mAuth.getCurrentUser().getUid();
            //Check if the value a user entered exists on the database or not
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                //@param dataSnapshot returns the results
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Chech if the result has a child on the database
                    if (!dataSnapshot.hasChild(user_uid)) {

                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        mAuth.addAuthStateListener(mAuthListener);

    }

    public void gotoProfile(View v) {

        startActivity(new Intent(this, ProfileActivity.class));

    }

    public void gotoBooks(View view) {
        startActivity(new Intent(this, BooksActivity.class));
    }

//    public void gotoLibraries(View view){
//        startActivity(new Intent(this, LibraryActivity.class));
//    }
//
//    public void gotoLocateLibrary(View view){
//        startActivity(new Intent(this, LocateLibrary.class));
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_logout) {

            logout();

        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}
