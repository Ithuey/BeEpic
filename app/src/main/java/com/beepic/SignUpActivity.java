package com.beepic;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beepic.Utils.FirebaseMethods;
import com.beepic.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity{

    RelativeLayout relativeLayout1, relativeLayout2, relativeLayout3;

    private static final String TAG = "SignUpActivity";

    private Context mContext;
    private String email, username, password;
    private EditText mEmail, mPassword, mUsername;
    private Button btnRegister;
    

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private String append = "";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.signup_activity);
            mContext = SignUpActivity.this;
            firebaseMethods = new FirebaseMethods(mContext);
            Log.d(TAG, "onCreate: started.");

            initWidgets();
            setupFirebaseAuth();
            init();

//            Button btnButton = (Button) findViewById(R.id.btn_signUp);
//            btnButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(SignUpActivity.this, InterestActivity.class);
//                    startActivity(intent);
//
//                }
//            });

        }


        //Todo compare that both passwords match.



        // Todo don't allow user to move forward unless terms box is checked.

        private void init() {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email = mEmail.getText().toString();
                    username = mUsername.getText().toString();
                    password = mPassword.getText().toString();

                    if (checkInputs(email,username, password)){

                        firebaseMethods.registerNewEmail(email, password, username);
                    }
                }
            });
        }

        // checking field inputs
        private boolean checkInputs (String email, String username, String password){
            Log.d(TAG, "checkInputs: checking inputs for null values");
            if(email.equals("") || username.equals("") || password.equals("")){
                Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        // initialize the activity widgets
        private void initWidgets(){
            mEmail = findViewById(R.id.txt_email);
            mPassword = findViewById(R.id.txt_password);
            mUsername = findViewById(R.id.txt_username);
            btnRegister = findViewById(R.id.btn_signUp);
            mContext = SignUpActivity.this;

        }


        private boolean isStringNull(String string){
            Log.d(TAG, "isStringNull: checking string if null");

            if (string.equals("")){
                return true;
            }else {
                return false;
            }
        }

        /*
        `````````````````````````````````````firebase```````````````````````````````````````````````
        */

    /**
     * check is @parm username already exist in the database.
     * @param username
     */
    private void checkIfUserNameExists(final String username) {
        Log.d(TAG, "checkIfUserNameExists: checking " + username + " already exist.");

        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()) {
                        Log.d(TAG, "checkIfUserNameExists: FOUND A MATCH " + singleSnapshot.getValue(User.class).getUsername());
                        append = mRef.push().getKey().substring(3,10);
                        Log.d(TAG, "onDataChange: username already exist. Appending random string to name: " + append);
                    }
                }

                String mUsername = "";
                mUsername = username + append;


                //add new user to the database
                firebaseMethods.addNewUser(email, mUsername, "");

                Toast.makeText(mContext, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();

                mAuth.signOut();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

        private void setupFirebaseAuth(){
            mAuth = FirebaseAuth.getInstance();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mRef = mFirebaseDatabase.getReference();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null){
                        //user is signed in
                        Log.d(TAG, "onAuthStateChanged: signed_in " + user.getUid());

                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange( DataSnapshot dataSnapshot) {
                                checkIfUserNameExists(username);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        finish();

                    }else {
                        //user is signed out
                        Log.d(TAG,"onAuthStateChanged:Signed_out");
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
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
