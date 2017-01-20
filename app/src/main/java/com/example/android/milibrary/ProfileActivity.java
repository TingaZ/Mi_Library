package com.example.android.milibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

public class ProfileActivity extends AppCompatActivity {

    Button btnTest;

    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Firebase.setAndroidContext(this);

        mRef = new Firebase("https://mi-library-6953b.firebaseio.com/");

        btnTest = (Button) findViewById(R.id.btn_test);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Firebase mrefChild = mRef.child("Name");
                mrefChild.setValue("Zack");


            }
        });


    }
}
