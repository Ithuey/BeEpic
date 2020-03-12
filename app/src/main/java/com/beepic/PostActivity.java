package com.beepic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.beepic.Share.GalleryActivity;
import com.beepic.Utils.FirebaseMethods;
import com.beepic.Utils.Permissions;
import com.beepic.Utils.UniversalImageLoader;
import com.beepic.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";

    private Context mContext;


    //widgets
    private Spinner interestSpinner;
    private EditText mCaption;
    private ImageView mImageGallery;
    private ImageView mImageCamera;
    private ImageView mGalleryCameraSelected;
    private Button mPost;
    private ImageView mBackArrow;
    private String imgUrl;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Constants
    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 1;
    private static final int CAMERA_REQUEST_CODE = 5;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);
        mFirebaseMethods = new FirebaseMethods(PostActivity.this);
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        mContext = PostActivity.this;
        setupFirebaseAuth();
        initWidgets();
        
/*        mImageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: launching camera. ");



                //Might Create a bug
                if(((MainActivity)getBaseContext()).checkPermission(Permissions.CAMERA_PERMISSION[0])){

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

                }


            }*/
        /*});*/

        mImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment. ");

                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate back to main screen. ");

                //upload the image to firebase
                Toast.makeText(PostActivity.this, "Attempting to upload new post", Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();
                //Get interest from spinner
                String interest = interestSpinner.getSelectedItem().toString();


                //Trying to figure our how to allow post without image
               if(imgUrl == null) {

                   try {
                       mFirebaseMethods.uploadNewPostNoPic(caption, interest, imgUrl);
                   } catch (NullPointerException e) {
                       Log.d(TAG, "onClick: NullPointerException " + e.getMessage());
                       Toast.makeText(mContext, "Something went wrong.", Toast.LENGTH_SHORT).show();
                   }

               }
                if ( imgUrl != null){
                    mFirebaseMethods.uploadNewPost(getString(R.string.new_post), caption, interest, imageCount, imgUrl);
                }

            }
        });

        //initImageLoader();
        setImage();

    }





    /**
     * gets the image url from the incoming intent and displays the chosen image. This will need to be modified to display nothing if no images is
     * chosen
     */
    private void setImage() {


        Intent intent = getIntent();
        imgUrl = intent.getStringExtra(getString(R.string.selected_image));
        UniversalImageLoader.setImage(imgUrl, mGalleryCameraSelected,null, mAppend );
    }

/*    private void initImageLoader() {

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }*/


    private void initWidgets() {

        interestSpinner = findViewById(R.id.sp_Interest);
        mCaption = findViewById(R.id.et_Post);
        //mImageCamera = findViewById(R.id.imgPostCamera);
        mImageGallery = findViewById(R.id.imgPostGallery);
        mGalleryCameraSelected = findViewById(R.id.imageGalleryCameraDisplay);
        mPost = findViewById(R.id.btn_Post);
        mBackArrow = findViewById(R.id.backArrow);


    }



    /*
     `````````````````````````````````````firebase```````````````````````````````````````````````
    */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting firebase auth");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "onDataChange: image count: " + imageCount);

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

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);
                getUserInterest(dataSnapshot);
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

    private void getUserInterest(DataSnapshot dataSnapshot){

        Post post = new Post();

        for(DataSnapshot ds: dataSnapshot.getChildren()){
            Log.d(TAG, "getUserInterest: show dataSnapShot " + ds);

            if(ds.getKey().equals(mContext.getString(R.string.dbname_user_interest_post))) {

                post.setInterest(
                        ds.child(userID)
                                .getValue(Post.class)
                                .getInterest()); //set the interest post

                Log.d(TAG, "showData: interest" + post.getInterest());

                String separateInterest = post.getInterest();

                String [] items = separateInterest.split(",");
                List<String> container = Arrays.asList(items);

                Log.d(TAG, "getUserInterest: List Array" + container);


                // Sets dropdown list
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                        R.layout.post_spinner_text,

                        container);

                adapter.setDropDownViewResource( R.layout.post_spinner_text);
                interestSpinner.setAdapter(adapter);

                // Sorts array alphabetically
                Arrays.sort(items);
                adapter.notifyDataSetChanged();

                //Toast.makeText(mContext, post.getInterest(), Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: done taking a photo");
            Log.d(TAG, "onActivityResult: attempting to navigate to final share screen");
        }
        
    }
}