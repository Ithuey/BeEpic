package com.beepic.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beepic.ProfileActivity;
import com.beepic.R;
import com.beepic.Share.GalleryActivity;
import com.beepic.Utils.FirebaseMethods;
import com.beepic.Utils.UniversalImageLoader;
import com.beepic.models.User;
import com.beepic.models.UserAccountSettings;
import com.beepic.models.UserSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "UserSettingActivity";

    private Context mContext;

    //vars
    private UserSettings mUserSettings;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //widgets
    private EditText mDisplayName, mUsername, mEmail;
    private CircleImageView mProfilePic;
    private TextView mChangeProfilePic;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile_activity);

        //set widget to id
        mProfilePic = findViewById(R.id.imgEditProfilePic);
        mDisplayName = findViewById(R.id.txtEditDisplayName);
        mUsername = findViewById(R.id.txtEditUserName);
        mEmail = findViewById(R.id.txtEditEmail);
        mChangeProfilePic = findViewById(R.id.txtChangePic);
        mFirebaseMethods = new FirebaseMethods(getBaseContext());
        mChangeProfilePic = findViewById(R.id.txtChangePic);
        setupFirebaseAuth();
        getIncomingIntent();

        Button save = findViewById(R.id.btnEditProfileSave);
                save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
            }
        });



    }

    private void getIncomingIntent(){

        Intent intent = getIntent();
        //if there is an imageUrl atached as an extra, then is was chosen from the gallery
        if(intent.hasExtra(getString(R.string.selected_image))) {
            Log.d(TAG, "getIncomingIntent: new incoming imgURL" + intent.hasExtra(getString(R.string.selected_image)));

            if(EditProfile.class.equals(EditProfile.class)){

                //set the new profile picture
                FirebaseMethods firebaseMethods = new FirebaseMethods(EditProfile.this);
                firebaseMethods.uploadNewPost(getString(R.string.new_profile_photo), null, null, 0,  intent.getStringExtra(getString(R.string.selected_image)));
            }


        }


    }

    //set profile widgets
    private void setProfileWidgets (UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());

        mUserSettings = userSettings;
        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePic, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mEmail.setText(userSettings.getUser().getEmail());

        mChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");

                Intent intent = new Intent(EditProfile.this, GalleryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                EditProfile.this.startActivity(intent);
                //EditProfile.this.finish();
            }
        });



    }

    /**
     *
     *Retrieves the data contained in the widget and submits it to the database
     * before doing so it checks to make sure the username chosen is unique
     * */

     private void saveProfileSettings() {

         final String displayName = mDisplayName.getText().toString();
         final String username = mUsername.getText().toString();
         final String email = mEmail.getText().toString();

         mRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 //save for later date.
                 if(!mUserSettings.getSettings().getDisplay_name().equals(displayName)){
                     mFirebaseMethods.updateUserAccountSettings(displayName);
                 }

                 //case1: the user did not change their username
                if(!mUserSettings.getUser().getUsername().equals(username)) {

                    checkIfUserNameExists(username);
                }
                 //case2: the user changed their username therefore we need to check for uniqueness
                else {

                }


             }



             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });


}


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

                if(!dataSnapshot.exists()) {
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(EditProfile.this, "Saved username." ,Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
                    startActivity(intent);

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()) {
                        Log.d(TAG, "checkIfUserNameExists: FOUND A MATCH " + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(EditProfile.this, "That username already exist." ,Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

     /*
     `````````````````````````````````````firebase```````````````````````````````````````````````
    */

    //setup firebase auth object
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting firebase auth");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

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

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
                //retrieve user post

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
