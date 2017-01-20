package com.example.android.milibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

    }

    public void gotoProfile(View v){

        startActivity(new Intent(this, ProfileActivity.class));

    }

    public void gotoBooks(View view){
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

        if (item.getItemId() == R.id.action_logout){

            logout();

        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}
