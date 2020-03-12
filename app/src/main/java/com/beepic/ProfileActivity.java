package com.beepic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beepic.MainFeed.MainFeed;
import com.beepic.MainFeed.MainRecycleViewAdapter;
import com.beepic.Utils.FirebaseMethods;
import com.beepic.Utils.MainfeedListAdapter;
import com.beepic.Utils.ProfilefeedAdapter;
import com.beepic.Utils.UniversalImageLoader;
import com.beepic.models.NewPost;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";


    private Context mContext;
    //For profile navigation
    private TextView mInterest, mFollowers, mUsername, mDisplayName;
    private ImageView mProfilePic;

    //For Animation of main button
    FloatingActionButton fabMain, fabProfile, fabSetting, fabPost, fabInterest, fabHome;
    Animation fabOpen, fabClose, rotationForward, rotationBackward;
    boolean isOpen= false;

    //for profile feed
    private RecyclerView recyclerView;
    ArrayList<MainFeed> mainFeedArrayList = new ArrayList<>();
    MainRecycleViewAdapter mainFeed;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;

    private ArrayList<NewPost> mNewPost;
    private ArrayList<String> mFollowing;
    private ListView mListView;
    private ProfilefeedAdapter mAdapter;
    private String mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile_activity);


        mListView = findViewById(R.id.lv_profile);
        mNewPost = new ArrayList<>();
        mFollowing = new ArrayList<>();
        mCurrentUser = "";

        //initImageLoader();
        setupFirebaseAuth();
        getUsersPost();

        //Load profile feed
        //populateMainRecyclerView();

        //Initiate recycle viewer
/*        recyclerView = findViewById(R.id.profile_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mainFeed = new MainRecycleViewAdapter(this, mainFeedArrayList);
        recyclerView.setAdapter(mainFeed);*/



//        AnimationFAB animateMainButton = (AnimationFAB)Context;
//        animateMainButton.animateFAB();


        mFirebaseMethods = new FirebaseMethods(getBaseContext());
        //widget initialized for profile navigation
        mInterest = findViewById(R.id.txtInterest);
        mFollowers = findViewById(R.id.txtFollowers);
        mUsername = findViewById(R.id.txtUserName);
        mProfilePic = findViewById(R.id.profile_image);
        mDisplayName = findViewById(R.id.txtDisplayName);

        //widget initialized for FAB
        fabMain =  findViewById(R.id.fab_main);
        fabSetting =  findViewById(R.id.fab_setting);
        fabPost =  findViewById(R.id.fab_post);
        fabInterest = findViewById(R.id.fab_interest);
        fabHome = findViewById(R.id.fab_home);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotationForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotationBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        //Navigate the FAB
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });
        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
        fabInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, InterestActivity.class);
                startActivity(intent);
            }
        });
        fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });
    }


    //initialize image loader
/*    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getBaseContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }*/

    //Fake data
/*
    public void populateMainRecyclerView(){

        MainFeed mainFeed = new MainFeed(2, 15, 10, R.drawable.beachchic_profilepic, 0,
                "Sarah Flanders", "Slack Line", "48 min", "Going to eden park to do some slack lining if anyone is up for it.");
        mainFeedArrayList.add(mainFeed);

        mainFeed = new MainFeed(2, 18, 12, R.drawable.beachchic_profilepic, 0,
                "Sarah Flanders", "Climbing", "12 hr", "I must say CLimb Time has been the best local climbing gym to learn bouldering.");
        mainFeedArrayList.add(mainFeed);

        mainFeed = new MainFeed(2, 22, 15, R.drawable.beachchic_profilepic, 0,
                "Sarah Flanders", "Yoga", "2 days", "Washington Park has Yoga classes every Saturday at 9am. The class is very welcoming for noobs.");
        mainFeedArrayList.add(mainFeed);

        mainFeed = new MainFeed(2, 60, 20, R.drawable.beachchic_profilepic, 0,
                "Sarah Flanders", "Camping", "5 days", "Would you all say the trip to Hocking Hills is worth the drive? I've never been but I constantly hear good things about this place.");
        mainFeedArrayList.add(mainFeed);


    }
*/

    //set profile widgets
    private void setProfileWidgets (UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());

        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePic, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mInterest.setText(String.valueOf(settings.getInterest()));

    }




    // Animate the FAB
    private void animateFab()
    {
        if(isOpen)
        {
            fabMain.startAnimation(rotationForward);
            fabPost.startAnimation(fabClose);

            fabSetting.startAnimation(fabClose);
            fabInterest.startAnimation(fabClose);
            fabHome.startAnimation(fabClose);
            fabPost.setClickable(false);

            fabSetting.setClickable(false);
            fabInterest.setClickable(false);
            fabHome.setClickable(false);

            isOpen=false;
        }
        else
        {
            fabMain.startAnimation(rotationBackward);
            fabPost.startAnimation(fabOpen);

            fabSetting.startAnimation(fabOpen);
            fabInterest.startAnimation(fabOpen);
            fabHome.startAnimation(fabOpen);
            fabPost.setClickable(true);

            fabSetting.setClickable(true);
            fabInterest.setClickable(true);
            fabHome.setClickable(true);
            isOpen=true;

        }
    }

    //get following count.
    private void getUsersPost(){
        Log.d(TAG, "getUsersPost: getting all the post for profile feed");

       // swipe.setRefreshing(false);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_interest_post));
                //.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //.equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());


        Log.d(TAG, "getUsersPost: getUserPost query: " + query);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user profile getUserPost(): " + singleSnapshot.getKey());

                    if(singleSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        mFollowing.add(singleSnapshot.getKey());
                    }

                    Log.d(TAG, "onDataChange: mFollowing getUserPost profile  " + mFollowing);

                }



                //get the post
                getPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    private void getPost(){
        Log.d(TAG, "getPost: getting post profile");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for( int i = 0; i < mFollowing.size(); i++){

            Log.d(TAG, "getPost: getting size profile " + mFollowing.size());
            final int count = i;
            Query query = reference
                    .child(getString(R.string.dbname_user_interest_post))
                    //.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(mFollowing.get(i))


                   .orderByChild("user_id")
                   .equalTo(mFollowing.get(i));

            Log.d(TAG, "getPost: getPost query profile: " + query);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        Log.d(TAG, "onDataChange: profile feed getChildren " + dataSnapshot.getChildren());
                        Log.d(TAG, "onDataChange: found user profile getPost: " + singleSnapshot);

                        NewPost post = new NewPost();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        post.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        post.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        post.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        post.setInterest(objectMap.get(getString(R.string.string_interest)).toString());
                        post.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        post.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        Log.d(TAG, "onDataChange: getPost caption profile: " + post);

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

            mAdapter = new ProfilefeedAdapter(ProfileActivity.this, R.layout.main_feed, mNewPost);
            mListView.setAdapter(mAdapter);
        }
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
