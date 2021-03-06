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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private EditText mLoginEmail, mLoginCardCode;
    private Button mSignUpBtn, mLoginBtn;
    private SignInButton mGoogleBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRefUsers;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgress, mDialog;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {



            }
        };

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRefUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRefUsers.keepSynced(true);

        mLoginEmail = (EditText) findViewById(R.id.txt_email);
        mLoginCardCode = (EditText) findViewById(R.id.txt_cardcode);
        mLoginBtn = (Button) findViewById(R.id.btn_signIn);
        mSignUpBtn = (Button) findViewById(R.id.btn_signUp);
        mGoogleBtn = (SignInButton) findViewById(R.id.google_btn);
        mProgress = new ProgressDialog(this);
        mDialog = new ProgressDialog(this);

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });


        //GOOGLE SIGN-IN METHOD
        //Configure Google Sign In
        // https://firebase.google.com/docs/auth/android/google-signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(LoginActivity.this, "Failed to Login... try again later.", Toast.LENGTH_SHORT).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();

            }
        });


    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            mProgress.setMessage("Starting sign in ...");
            mProgress.show();

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...

                mProgress.dismiss();

            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            // ...
                            mProgress.dismiss();
                            checkIfUserExists();

                        }

                    }
                });
    }


    private void checkLogin() {

        String email = mLoginEmail.getText().toString().trim();
        String cardcode = mLoginCardCode.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(cardcode)) {

            mDialog.setMessage("Verifying credentials ...");
            mDialog.show();

            mAuth.signInWithEmailAndPassword(email, cardcode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        mDialog.dismiss();
                        checkIfUserExists();

                    } else {

                        mDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }


    public void checkIfUserExists() {

        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseRefUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(user_id)) {

                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    } else {

                        Intent setupIntent = new Intent(LoginActivity.this, SetupActivity.class);
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
}
