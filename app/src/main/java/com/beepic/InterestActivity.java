package com.beepic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.beepic.Interest.Interest;
import com.beepic.Interest.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class InterestActivity extends AppCompatActivity{

    List<Interest> lstInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest);


        lstInterest = new ArrayList<>();

        lstInterest.add(new Interest("BMX", R.drawable.bmx_tn));
        lstInterest.add(new Interest("Bowling", R.drawable.bowling_tn));
        lstInterest.add(new Interest("Camping", R.drawable.camping_tn));
        lstInterest.add(new Interest("Dance", R.drawable.dance_tn));
        lstInterest.add(new Interest("Fishing", R.drawable.fishing_tn));
        lstInterest.add(new Interest("Frisbee Golf", R.drawable.frisbeegolf_tn));
        lstInterest.add(new Interest("Golfing", R.drawable.golfing_tn));
        lstInterest.add(new Interest("Hunting", R.drawable.hunting_tn));
        lstInterest.add(new Interest("Kayaking", R.drawable.kayaking_tn));
        lstInterest.add(new Interest("Mountain Biking", R.drawable.mountainbiking_tn));
        lstInterest.add(new Interest("Programming", R.drawable.programming_tn));
        lstInterest.add(new Interest("Rock Climbing", R.drawable.rockclimbing_tn));
        lstInterest.add(new Interest("Skateboarding", R.drawable.skateboarding_tn));
        lstInterest.add(new Interest("Slack Line", R.drawable.slackline_tn));
        lstInterest.add(new Interest("Video Games", R.drawable.videogames_tn));
        lstInterest.add(new Interest("Yoga", R.drawable.yoga_tn));

        RecyclerView interestRv = (RecyclerView) findViewById(R.id.interest_recyclerview_id);
        RecyclerViewAdapter interestAdapter = new RecyclerViewAdapter(this, lstInterest);
        interestRv.setLayoutManager(new GridLayoutManager(this, 2));
        interestRv.setAdapter(interestAdapter);



        Button btnButton = (Button) findViewById(R.id.btnInterestSave);
        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InterestActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }

}
