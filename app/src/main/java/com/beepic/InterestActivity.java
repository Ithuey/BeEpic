package com.beepic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.beepic.Interest.Interest;
import com.beepic.Interest.RecyclerViewAdapter;
import com.beepic.Utils.FirebaseMethods;
import com.beepic.Utils.Permissions;
import com.beepic.models.UserLocation;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InterestActivity extends AppCompatActivity {

    private static final String TAG = "InterestActivity";
    SharedPreferences prefs;
    SharedPreferences.Editor mEditor;
    String key = "Key";

    //const
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    //location
    private FusedLocationProviderClient fusedLocationClient;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    private Context mContext;


    List<Interest> lstInterest;
    StringBuffer interestList = null;
    //RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mContext = InterestActivity.this;

        if (checkPermissionArray(Permissions.PERMISSIONS)) {

        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
        //Firebase
        setupFirebaseAuth();


        //adapter = new RecyclerViewAdapter(this, lstInterest);

            lstInterest = new ArrayList<>();

            lstInterest.add(new Interest("BMX", R.drawable.bmx_tn, false));
            lstInterest.add(new Interest("Bowling", R.drawable.bowling_tn, false));
            lstInterest.add(new Interest("Camping", R.drawable.camping_tn, false));
            lstInterest.add(new Interest("Dance", R.drawable.dance_tn, false));
            lstInterest.add(new Interest("Fishing", R.drawable.fishing_tn, false));
            lstInterest.add(new Interest("Frisbee Golf", R.drawable.frisbeegolf_tn, false));
            lstInterest.add(new Interest("Golfing", R.drawable.golfing_tn, false));
            lstInterest.add(new Interest("Hunting", R.drawable.hunting_tn, false));
            lstInterest.add(new Interest("Kayaking", R.drawable.kayaking_tn, false));
            lstInterest.add(new Interest("Mountain Biking", R.drawable.mountainbiking_tn, false));
            lstInterest.add(new Interest("Programming", R.drawable.programming_tn, false));
            lstInterest.add(new Interest("Rock Climbing", R.drawable.rockclimbing_tn, false));
            lstInterest.add(new Interest("Skateboarding", R.drawable.skateboarding_tn, false));
            lstInterest.add(new Interest("Slack Line", R.drawable.slackline_tn, false));
            lstInterest.add(new Interest("Video Games", R.drawable.videogames_tn, false));
            lstInterest.add(new Interest("Yoga", R.drawable.yoga_tn, false));

           //loadData();

        RecyclerView interestRv = (RecyclerView) findViewById(R.id.interest_recyclerview_id);
        final RecyclerViewAdapter interestAdapter = new RecyclerViewAdapter(this, lstInterest);

        interestRv.setLayoutManager(new GridLayoutManager(this, 2));
        interestRv.setAdapter(interestAdapter);


        Button btnButton = (Button) findViewById(R.id.btnInterestSave);
        btnButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {


                interestList = new StringBuffer();

                for (Interest i : interestAdapter.checkedInterest) {


                    interestList.append(i.getTitle());
                    interestList.append(",");
                    saveData();

                    Log.d(TAG, "onClick: interest adapter " + interestAdapter.checkedInterest);

                }
                if (interestAdapter.checkedInterest.size() > 0) {
                    Toast.makeText(InterestActivity.this, interestList.toString(), Toast.LENGTH_SHORT).show();

                    addUserInterest(interestList.toString());

                }

                Intent intent = new Intent(InterestActivity.this, MainActivity.class);
                startActivity(intent);
                //getUserLocation();


            }
        });


    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preference", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lstInterest);
        editor.putString("task list", json);
        editor.apply();
    }

    private void loadData() {


        SharedPreferences sharedPreferences = getSharedPreferences("shared preference", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<List<Interest>>() {
        }.getType();
        lstInterest = gson.fromJson(json, type);


    }


         /*
     `````````````````````````````````````firebase```````````````````````````````````````````````
    */

    //Send data to firebase interest DB.
    private void addUserInterest(String interest) {

        Log.d(TAG, "addUserInterest: use to save interest to database user_interest_post");


        mRef.child(mContext.getString(R.string.dbname_user_interest_post))
                .child(userID)
                .child(mContext.getString(R.string.string_interest))
                .setValue(interest);

        mRef.child("user_profile_post")
                .child(userID)
                .child("interest")
                .setValue(interest);


    }

    //setup firebase auth object
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting firebase auth");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    //user signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in " + user.getUid());
                } else {
                    //user signed out
                    Log.d(TAG, "onAuthStateChanged: singed_out");
                }
            }
        };

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                //setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }








    private int radius = 40;
    private Boolean userFound = false;
    private String userLocationID;
    private ArrayList<String> mUserIDLocation;


    public void getUserLocation() {

        mUserIDLocation = new ArrayList<String>();

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) mContext, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {

                if(location != null){
                    //Toast.makeText(this, "Location " + location.toString(), Toast.LENGTH_SHORT ).show();
                   final Location userLocation = location;
                    Log.d(TAG, "onSuccess: Location" + location);
                    Log.d(TAG, "onSuccess: Location Latitude " + location.getLatitude());

                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("user_location");

                    GeoFire geoFire = new GeoFire(mRef);
                    geoFire.setLocation(user_id, new GeoLocation(location.getLatitude(), location.getLongitude()));


                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()), radius);

                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
/*                            if(!userFound){
                                userFound = true;
                                userLocationID = key;
                                mUserIDLocation.add(userLocationID);

                                for (int i = 0; i < mUserIDLocation.size(); i++){

                                    Log.d(TAG, "onKeyEntered: mUserIdLocation " + mUserIDLocation);
                                }


                                Log.d(TAG, "onKeyEntered: userLocationID: " + userLocationID);
                            }*/

                            mUserIDLocation.add(key);


                            UserLocation userLocation = new UserLocation();
                            for (int i = 0; i < mUserIDLocation.size(); i++) {

                                Log.d(TAG, "onKeyEntered: mUserIDLocation " + mUserIDLocation);


                                userLocation.setUserLocations(mUserIDLocation);


                            }
                            Log.d(TAG, "onKeyEntered: setUserLocation " +  userLocation.getUserLocations());

                            Log.d(TAG, "onKeyEntered: key " + key);
                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {

                            if(userFound){
                                Toast.makeText(mContext, "No users in your location", Toast.LENGTH_SHORT).show();
                                getAllUsersInProximity();
                            }

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });


                }
            }
        });

    }








    private void getAllUsersInProximity(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("user_location");

        GeoFire geoFire = new GeoFire(mRef);


    }



        //GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lng), radius);




/*    private void getAllUsersInProximity(){

        DatabaseReference mAllUsersInRadius = FirebaseDatabase.getInstance().getReference().child("user_location");

        GeoFire geoFire = new GeoFire(mAllUsersInRadius);



        //GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation());

    }*/

    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions");

        ActivityCompat.requestPermissions(InterestActivity.this,
                permissions, VERIFY_PERMISSIONS_REQUEST);

    }


    /**
     * Check an array of permissions
     * @param permission
     * @return
     */
    public boolean checkPermissionArray(String[] permission) {
        Log.d(TAG, "checkPermissionArray: checking permission array");
        for (int i = 0; i < permission.length; i++) {
            String check = permission[i];
            if (!checkPermission(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checking for a single permission
     * @param permmission
     * @return
     */
    public boolean checkPermission(String permmission) {
        Log.d(TAG, "checkPermission: checking permmissions: " + permmission);

        int permissionsRequest = ActivityCompat.checkSelfPermission(mContext, permmission);

        if (permissionsRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: \n permission granted for " + permmission);
            return false;
        } else {
            Log.d(TAG, "checkPermission: \n Permission denied for " + permmission);
            return true;
        }
    }


}
