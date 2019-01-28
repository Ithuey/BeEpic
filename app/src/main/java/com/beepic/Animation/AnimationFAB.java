package com.beepic.Animation;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.beepic.ProfileActivity;
import com.beepic.R;

public class AnimationFAB extends AppCompatActivity {



    FloatingActionButton fabMain, fabProfile, fabSetting, fabPost, fabInterest;
    Animation fabOpen, fabClose, rotationForward, rotationBackward;
    boolean isOpen= false;


//    public  void animateFAB() {
//        fabMain = findViewById(R.id.fab_main);
//        fabProfile = findViewById(R.id.fab_profile);
//        fabSetting = findViewById(R.id.fab_setting);
//        fabPost = findViewById(R.id.fab_post);
//        fabInterest = findViewById(R.id.fab_interest);
//
//        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
//        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
//
//        rotationForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
//        rotationBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
//
//
//        fabMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                animateFab();
//            }
//        });
//
//        fabProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                animateFab();
//
//            }
//        });

//    }

    public final void animateFab()
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

}
