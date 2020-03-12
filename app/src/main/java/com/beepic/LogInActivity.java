package com.beepic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beepic.Utils.LocationMethods;
import com.beepic.Utils.Permissions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.support.constraint.Constraints.TAG;


public class LogInActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mLogInBtn;
    private Context mContext;
    private Button mSignUp;

    private static final String TAG = "LoginActivity";

    LocationMethods mLocationMethods;

    private static final int VERIFY_PERMISSIONS_REQUEST = 1;



    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity2);




        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.txtEmail);
        mPassword = findViewById(R.id.txtPassword);
        mLogInBtn = findViewById(R.id.btn_LogIn);
        mContext = LogInActivity.this;
        mSignUp = findViewById(R.id.btn_CrtAccount);
        Log.d(TAG, "onCreate: started");


/*        if (checkPermissionArray(Permissions.PERMISSIONS)) {

        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }*/

        setupFirebaseAuth();
        init();




        Button button = findViewById(R.id.btn_CrtAccount);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }


    private boolean isStringNull(String string) {
        Log.d(TAG, "isStringNull: check string is null");

        if (string.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /*
    -------------------------------------Firebase----------------------------------------------------
     */

    private void init() {
        Button button = findViewById(R.id.btn_LogIn);
        mLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (isStringNull(email) && isStringNull(password)) {
                    Toast.makeText(mContext, "You must fill out all fields.", Toast.LENGTH_SHORT).show();
                } else {

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    FirebaseUser user = mAuth.getCurrentUser();


                                    if (!task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: failed", task.getException());

                                        Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            if (user.isEmailVerified()) {
                                                Log.d(TAG, "onComplete: success. Email is verified");



                                                Intent intent = new Intent(LogInActivity.this, InterestActivity.class);



                                                startActivity(intent);





                                            }else {
                                                Toast.makeText(mContext, "Email is not verified.", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                            }
                                        }catch(NullPointerException e) {
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                        }
                                    }
                                }
                            });


                }
            }
        });
    }

    //setup firebase auth object
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting firebase auth");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();



                if (user != null){
                    //user signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in " + user.getUid());
                }else {
                    //user signed out
                    Log.d(TAG, "onAuthStateChanged: singed_out");
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }





    }

