package com.beepic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.widget.Toast;


import com.beepic.Interest.Interest;
import com.beepic.MainFeed.MainFeed;
import com.beepic.MainFeed.MainRecycleViewAdapter;
import com.beepic.Utils.LocationMethods;
import com.beepic.Utils.MainfeedListAdapter;
import com.beepic.Utils.Permissions;
import com.beepic.Utils.UniversalImageLoader;
import com.beepic.models.NewPost;
import com.beepic.models.UserAccountSettings;
import com.beepic.models.UserLocation;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabMain, fabProfile, fabSetting, fabPost, fabInterest;
    Animation fabOpen, fabClose, rotationForward, rotationBackward;
    boolean isOpen = false;

    //Gets main feed
    RecyclerView recyclerView;
    ArrayList<MainFeed> mainFeedArrayList = new ArrayList<>();
    MainRecycleViewAdapter mainFeed;


    private InterestActivity mInterst;

    //const
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    //location
    private FusedLocationProviderClient fusedLocationClient;

    private static final String TAG = "MainActivity";
    private Context mContext;



    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //vars
    private ArrayList<NewPost> mNewPost;
    private static ArrayList<String> mFollowing;
    private  ArrayList<String> mLocationPost;
    private ListView mListView;
    private MainfeedListAdapter mAdapter;

    SwipeRefreshLayout swipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.listView);
        mNewPost = new ArrayList<>();
        mFollowing = new ArrayList<>();
        mLocationPost = new ArrayList<>();

        //mLocationMethods = new LocationMethods();
        //mContext = MainActivity.this;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        //check permissions
        if (checkPermissionArray(Permissions.PERMISSIONS)) {

        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }


        //recyclerView = findViewById(R.id.recyclerViewMainFeed);

/*        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mainFeed = new MainRecycleViewAdapter(this, mainFeedArrayList);
        recyclerView.setAdapter(mainFeed);*/

        // populateMainRecyclerView();

        //Controls for Main FAB
        fabMain = findViewById(R.id.fab_main);
        fabProfile = findViewById(R.id.fab_profile);
        fabSetting = findViewById(R.id.fab_setting);
        fabPost = findViewById(R.id.fab_post);
        fabInterest = findViewById(R.id.fab_interest);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotationForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotationBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });

        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);


            }
        });
        fabInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, InterestActivity.class);
                startActivity(intent);

            }
        });
        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

            }
        });
        fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
                finish();

            }
        });

        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
                //getUsersPost();
            }
        });



        //Firebase
        getUserLocation();
        setupFirebaseAuth();
        //getUsersPost();





    }
/*    //Feed
    public void populateMainRecyclerView(){

        MainFeed mainFeed = new MainFeed(1, 5, 12, R.drawable.rocklimbing_profilepic, R.drawable.rockclimbing_post,
                "CrimpKing", "Rock Climbing", " 10 min", "Created this new rout down at the gorge today. I would give it a 5.8. Pretty sharp but not to crazy difficult.");
        mainFeedArrayList.add(mainFeed);

        mainFeed = new MainFeed(2, 15, 10, R.drawable.beachchic_profilepic, 0,
                "MissFlanders", "Slack Line", "12 min", "Going to eden park to do some slack lining if anyone is up for it.");
        mainFeedArrayList.add(mainFeed);


        mainFeed = new MainFeed(5, 2, 6, R.drawable.dog_profilepic, 0,
                "TryHardMcGee", "Video Games", "1 hr", "Does anyone still like to do LAN parties? If I held a LAN for Halo 3, who would you be interested?");
        mainFeedArrayList.add(mainFeed);

        mainFeed = new MainFeed(6, 18, 6, R.drawable.bowling_profile, R.drawable.bowling_post_pic,
                "EnglishMagic", "Bowling", "2 hr", "I need a new League team for Erlangers super bowl. I say I average a 155 on most days. Let me know if you are looking.");
        mainFeedArrayList.add(mainFeed);

        mainFeed = new MainFeed(7, 29, 5, R.drawable.golf_profile, 0,
                "FairwayGod", "Golfing", "3 hr", "Im just starting to get into golfing. What are some of the easier courses around?");
        mainFeedArrayList.add(mainFeed);

        mainFeed = new MainFeed(8, 32, 15, R.drawable.camping_profile, 0,
                "inTents", "Camping", "4 hr", "Everyone favorite camping spot at the gorge?");
        mainFeedArrayList.add(mainFeed);

        mainFeed = new MainFeed(9, 50, 8, R.drawable.mountainbiking_profile, 0,
                "LookMaNoHands", "Mountain Biking", "4 hr", "Whats everyone favorite trail at Idawhile in Boone County KY? I just discovered this place and it so awesome for being a local riding spot. .");
        mainFeedArrayList.add(mainFeed);

        mainFeed = new MainFeed(3, 14, 26, R.drawable.lookingup_profilepic, R.drawable.skateboarding_post,
                "Scabloobs", "Skateboarding", "5 hr", "Started messing around with this little set up off of Madisons in Covington. ");
        mainFeedArrayList.add(mainFeed);

        mainFeed = new MainFeed(4, 3, 6, R.drawable.yoga_profile, 0,
                "Yamyam", "Yoga", "5 hr", "This hot yoga class has me beat. 90 mins feels like 6 hours.");
        mainFeedArrayList.add(mainFeed);




    }*/

    //animation for fab
    private void animateFab() {
        if (isOpen) {
            fabMain.startAnimation(rotationForward);
            fabPost.startAnimation(fabClose);
            fabProfile.startAnimation(fabClose);
            fabSetting.startAnimation(fabClose);
            fabInterest.startAnimation(fabClose);
            fabPost.setClickable(false);
            fabProfile.setClickable(false);
            fabSetting.setClickable(false);
            fabInterest.setClickable(false);

            isOpen = false;
        } else {
            fabMain.startAnimation(rotationBackward);
            fabPost.startAnimation(fabOpen);
            fabProfile.startAnimation(fabOpen);
            fabSetting.startAnimation(fabOpen);
            fabInterest.startAnimation(fabOpen);
            fabPost.setClickable(true);
            fabProfile.setClickable(true);
            fabSetting.setClickable(true);
            fabInterest.setClickable(true);
            isOpen = true;

        }
    }


    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions");

        ActivityCompat.requestPermissions(MainActivity.this,
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

        int permissionsRequest = ActivityCompat.checkSelfPermission(MainActivity.this, permmission);

        if (permissionsRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: \n permission granted for " + permmission);
            return false;
        } else {
            Log.d(TAG, "checkPermission: \n Permission denied for " + permmission);
            return true;
        }
    }

    //get following count.
    private void getUsersPost() {
        Log.d(TAG, "getUsersPost: getting all the post");



        Log.d(TAG, "onKeyEntered: setUserLocation insidegetUserPost " +  userLocation.getUserLocations());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("user_location");
        //getString(R.string.dbname_user_interest_post)

        Log.d(TAG, "getUsersPost: getUserPost query main: " + query);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user main getUsersPost: " + singleSnapshot.getKey());

                    mFollowing.add(singleSnapshot.getKey());

                    Log.d(TAG, "onDataEntered: mFollowing1 " + mFollowing);
                    Log.d(TAG, "onDataEntered: mFollowing size1 " + mFollowing.size());
                }
                Log.d(TAG, "onDataEntered: mFollowing2 " + mFollowing);
                Log.d(TAG, "onDataEntered: mFollowing size2 " + mFollowing.size());

                //get the post
                getPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




    private void getPost(){
        Log.d(TAG, "getPost: getting post");


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "getPost: mfollowing size3 " + mFollowing.size());
        for( int i = 0; i < mFollowing.size(); i++){


            Log.d(TAG, "getPost: mFollowing " + mFollowing);
            Log.d(TAG, "getPost: getting size " + mFollowing.size());
            final int count = i;
            Query query = reference
                    .child(getString(R.string.dbname_user_interest_post))
                    .child(mFollowing.get(i))

                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));

            Log.d(TAG, "getPost: getPost query: " + query);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: main feed getChildren " + dataSnapshot.getChildren());
                        Log.d(TAG, "onDataChange: onDataChange main feed: " + singleSnapshot
                                .child(getString(R.string.field_user_id)));

                        NewPost post = new NewPost();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        post.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        post.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        post.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        post.setInterest(objectMap.get(getString(R.string.string_interest)).toString());
                        post.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());


                        post.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        Log.d(TAG, "onDataChange: getPost caption main " + post);

                        mNewPost.add(post);
                    }

                    if(count >= mFollowing.size() -1){

                        //display post
                        displayPost();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void displayPost(){
        if (mNewPost  != null){
            Collections.sort(mNewPost, new Comparator<NewPost>() {
                @Override
                public int compare(NewPost o1, NewPost o2) {
                    return o2.getDate_created().compareTo(o1.getDate_created());
                }
            });

            mAdapter = new MainfeedListAdapter(MainActivity.this, R.layout.main_feed, mNewPost);
            mListView.setAdapter(mAdapter);
        }
    }


        /*
        `````````````````````````````````````UserLocation```````````````````````````````````````````````
        */


    private int radius = 40;
    private Boolean userFound = false;
    private String userLocationID;
    private ArrayList<String> mUserIDLocation;
    final UserLocation userLocation = new UserLocation();
    public void getUserLocation() {
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final GeoFire geoFire = new GeoFire(mRef.child("user_location"));
        mUserIDLocation = new ArrayList<String>();

        swipe.setRefreshing(false);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) MainActivity.this, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    //Toast.makeText(this, "UserLocation " + location.toString(), Toast.LENGTH_SHORT ).show();
                    //final Location userLocation = location;
                    Log.d(TAG, "onSuccess: UserLocation" + location);
                    Log.d(TAG, "onSuccess: UserLocation Latitude " + location.getLatitude());

                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();




                    geoFire.setLocation(user_id, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });

                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), radius);


                    geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                        @Override
                        public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

                            Log.d(TAG, "onDataEntered: datasnapshot " + dataSnapshot);

                            mUserIDLocation.add(dataSnapshot.getKey());


                        }

                        @Override
                        public void onDataExited(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

                        }

                        @Override
                        public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {

                            mFollowing = mUserIDLocation;

                            Log.d(TAG, "onGeoQueryReady: mFollowing users " + mFollowing);

                            getPost();
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });
                }
            }
        });

    }



/*                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {

                            //mFollowing.add(key);



                            Log.d(TAG, "onKeyEntered: key " + key);
                            Log.d(TAG, "onKeyEntered: mFollowing add key " + mFollowing.add(key));
                            mFollowing.add(key);
                            Log.d(TAG, "onKeyEntered: mFollowing key size " + mFollowing.size());
                            //getUsersPost();
                            getPost();

                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {

                            if (userFound) {
                                Toast.makeText(MainActivity.this, "No users in your location", Toast.LENGTH_SHORT).show();
                                //getAllUsersInProximity();
                            }

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });*/


/*
                }
            }
        });



    }
*/



    public void userIDlocationMeth(ArrayList mUserIDLocation) {

        for (int i = 0; i <mUserIDLocation.size(); i++){


            Log.d(TAG, "userIDlocationMeth: " + mUserIDLocation);
        }
    }


        /*
        `````````````````````````````````````firebase```````````````````````````````````````````````
        */


    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checkinig if current user is logged in");

        if (user == null) {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        }
    }



    //setup firebase auth object
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting firebase auth");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if user is logged in
                checkCurrentUser(user);

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
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }




}



