package com.beepic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.beepic.Animation.AnimationFAB;
import com.beepic.MainFeed.MainFeed;
import com.beepic.MainFeed.MainRecycleViewAdapter;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    //For Animation of main button
    FloatingActionButton fabMain, fabProfile, fabSetting, fabPost, fabInterest, fabHome;
    Animation fabOpen, fabClose, rotationForward, rotationBackward;
    boolean isOpen= false;

    //for profile feed
    RecyclerView recyclerView;
    ArrayList<MainFeed> mainFeedArrayList = new ArrayList<>();
    MainRecycleViewAdapter mainFeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile_activity);



        recyclerView = findViewById(R.id.profile_recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mainFeed = new MainRecycleViewAdapter(this, mainFeedArrayList);
        recyclerView.setAdapter(mainFeed);

        populateMainRecyclerView();

//        AnimationFAB animateMainButton = (AnimationFAB)Context;
//        animateMainButton.animateFAB();




        fabMain =  findViewById(R.id.fab_main);

        fabSetting =  findViewById(R.id.fab_setting);
        fabPost =  findViewById(R.id.fab_post);
        fabInterest = findViewById(R.id.fab_interest);
        fabHome = findViewById(R.id.fab_home);

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
    }

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
}
