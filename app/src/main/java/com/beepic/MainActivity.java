package com.beepic;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;


import com.beepic.Interest.Interest;
import com.beepic.MainFeed.MainFeed;
import com.beepic.MainFeed.MainRecycleViewAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabMain, fabProfile, fabSetting, fabPost, fabInterest;
    Animation fabOpen, fabClose, rotationForward, rotationBackward;
    boolean isOpen= false;

    //Gets main feed
    RecyclerView recyclerView;
    ArrayList<MainFeed> mainFeedArrayList = new ArrayList<>();
    MainRecycleViewAdapter mainFeed;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recyclerViewMainFeed);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mainFeed = new MainRecycleViewAdapter(this, mainFeedArrayList);
        recyclerView.setAdapter(mainFeed);

        populateMainRecyclerView();

        //Controls for Main FAB
        fabMain =  findViewById(R.id.fab_main);
        fabProfile =  findViewById(R.id.fab_profile);
        fabSetting =  findViewById(R.id.fab_setting);
        fabPost =  findViewById(R.id.fab_post);
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






    }

    //Feed
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




    }

    //animation for fab
    private void animateFab()
    {
        if(isOpen)
        {
            fabMain.startAnimation(rotationForward);
            fabPost.startAnimation(fabClose);
            fabProfile.startAnimation(fabClose);
            fabSetting.startAnimation(fabClose);
            fabInterest.startAnimation(fabClose);
            fabPost.setClickable(false);
            fabProfile.setClickable(false);
            fabSetting.setClickable(false);
            fabInterest.setClickable(false);

            isOpen=false;
        }
        else
        {
            fabMain.startAnimation(rotationBackward);
            fabPost.startAnimation(fabOpen);
            fabProfile.startAnimation(fabOpen);
            fabSetting.startAnimation(fabOpen);
            fabInterest.startAnimation(fabOpen);
            fabPost.setClickable(true);
            fabProfile.setClickable(true);
            fabSetting.setClickable(true);
            fabInterest.setClickable(true);
            isOpen=true;

        }
    }

        /*
        `````````````````````````````````````firebase```````````````````````````````````````````````
        */


    }



