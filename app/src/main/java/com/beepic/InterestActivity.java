package com.beepic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

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

        lstInterest.add(new Interest("Camping", R.drawable.camping_tn));
        lstInterest.add(new Interest("Kayaking", R.drawable.kayaking_tn));
        lstInterest.add(new Interest("Camping", R.drawable.camping_tn));
        lstInterest.add(new Interest("Kayaking", R.drawable.kayaking_tn));
        lstInterest.add(new Interest("Camping", R.drawable.camping_tn));
        lstInterest.add(new Interest("Kayaking", R.drawable.kayaking_tn));
        lstInterest.add(new Interest("Camping", R.drawable.camping_tn));
        lstInterest.add(new Interest("Kayaking", R.drawable.kayaking_tn));
        lstInterest.add(new Interest("Camping", R.drawable.camping_tn));
        lstInterest.add(new Interest("Kayaking", R.drawable.kayaking_tn));

        RecyclerView interestRv = (RecyclerView) findViewById(R.id.interest_recyclerview_id);
        RecyclerViewAdapter interestAdapter = new RecyclerViewAdapter(this, lstInterest);
        interestRv.setLayoutManager(new GridLayoutManager(this, 2));
        interestRv.setAdapter(interestAdapter);
    }

}
