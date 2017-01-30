package com.example.android.milibrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUsername, mEmail, mCardCode;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsername = (EditText) findViewById(R.id.editText_username);
        mEmail = (EditText) findViewById(R.id.editText_Email);
        mCardCode = (EditText) findViewById(R.id.editText_cardcode);
        btnRegister = (Button) findViewById(R.id.button_register);
        mDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startRegister();

            }
        });

    }

    private void startRegister() {

        final String username = mUsername.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String cardcode = mCardCode.toString().toString().trim();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(cardcode)) {

            mDialog.setMessage("Signing Up ...");
            mDialog.show();

            mAuth.createUserWithEmailAndPassword(email, cardcode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        String user_id = mAuth.getCurrentUser().getUid();

                        Log.v("TAG", "User UID: " + user_id);

                        DatabaseReference current_logged_user = mDatabase.child(user_id);

                        current_logged_user.child("name").setValue(username);
                        current_logged_user.child("image").setValue("default");

                        mDialog.dismiss();

                    }

                }
            });

        }

    }
}
