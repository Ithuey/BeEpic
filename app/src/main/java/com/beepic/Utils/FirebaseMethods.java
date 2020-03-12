package com.beepic.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.beepic.MainActivity;
import com.beepic.R;
import com.beepic.models.NewPost;
import com.beepic.models.User;
import com.beepic.models.UserAccountSettings;
import com.beepic.models.UserSettings;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String user_ID;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private StorageReference mStorageReference;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;




    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;
        initImageLoader();

        if (mAuth.getCurrentUser() != null) {
            user_ID = mAuth.getCurrentUser().getUid();
        }
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    /**
     * Update "user_account_settings' node for current user
     * @param displayName
     */
    public void updateUserAccountSettings(String displayName) {
        Log.d(TAG, "updateUserAccountSettings: updating user account settings");


        //use if statement if you want to add settings to modify down the road.
        if(displayName != null) {
            mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(user_ID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
        }


    }


    //update username
    public void updateUsername (String username){
        Log.d(TAG, "updateUsername: updating username to: " + username);

        mRef.child(mContext.getString(R.string.dbname_user))
                .child(user_ID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
        mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(user_ID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }






/*    public boolean checkIfUsernameExist(String username, DataSnapshot datasnapshot) {
        Log.d(TAG, "checkIfUsernameExist: checikng if" + username + "already exists");

        User user = new User();

        for (DataSnapshot ds : datasnapshot.child(user_ID).getChildren()) {
            Log.d(TAG, "checkIfUsernameExist: datasnapshot: " + ds);

            user.setUsername(ds.getValue(User.class).getUsername());
            Log.d(TAG, "checkIfUsernameExist: username " + user.getUsername());

            if (StringManipulation.expandUsername(user.getUsername()).equals(username)) {
                Log.d(TAG, "checkIfUsernameExist: Found a match" + user.getUsername());
                return true;
            }
        }
        return false;
    }*/

    /**
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {
                            // send verification email
                            sendVerificationEmail();
                            user_ID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed" + user_ID);
                        }
                    }
                });
    }





    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }




    /**
     * Add info to user node
     * Add info to user_account_settings node
     */
    public void addNewUser(String email, String username, String profile_photo) {

        Log.d(TAG, "addNewUser: new user added to database");

        User user = new User(user_ID, email, StringManipulation.condenseUsername(username));

        mRef.child(mContext.getString(R.string.dbname_user))
                .child(user_ID)
                .setValue(user);

        //Must match UserAccountSettings
        UserAccountSettings settings = new UserAccountSettings(
                username,
                0,
                0,
                profile_photo,
                StringManipulation.condenseUsername(username)
        );

        mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(user_ID)
                .setValue(settings);

    }



/*    public void loadInterestSpinner(DataSnapshot dataSnapshot) {

        for(DataSnapshot ds: dataSnapshot.getChildren()){
            if(ds.getKey().equals("user_interest_post")){
                Log.d(TAG, "loadInterestSpinner: datatsnaptshot " + ds );

                mRef.child(mContext.getString(R.string.dbname_user_interest_post))
                        .child(user_ID)
                        .

*//*                mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                        .child(user_ID)
                        .child(mContext.getString(R.string.field_username))
                        .setValue(username);*//*
            }
        }
    }*/

    /*
     * Retrieves the account settings for the user currently logged in
     * Database: user_account_Settings node
     */

    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase.");

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "fetUserAccountSettings: datasnapshot: " + ds);


                try {

                    settings.setDisplay_name(
                            ds.child(user_ID)
                            .getValue(UserAccountSettings.class)
                            .getDisplay_name());

                    settings.setFollowers(ds.child(user_ID)
                            .getValue(UserAccountSettings.class)
                            .getFollowers());
                    settings.setProfile_photo(ds.child(user_ID)
                            .getValue(UserAccountSettings.class)
                            .getProfile_photo());
                    settings.setInterest(ds.child(user_ID)
                            .getValue(UserAccountSettings.class)
                            .getInterest());
                    settings.setUsername(ds.child(user_ID)
                            .getValue(UserAccountSettings.class)
                            .getUsername());

                    Log.d(TAG, "fetUserAccountSettings: retrieved user_Account_Settings information: " + settings.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
                }


            }
            //user node
            Log.d(TAG, "getUserSettings: snapshot key: " + ds.getKey());
            if(ds.getKey().equals(mContext.getString(R.string.dbname_user))){

                user.setUsername(
                        ds.child(user_ID)
                        .getValue(User.class)
                        .getUsername()
                );
                user.setEmail(
                        ds.child(user_ID)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setUser_id(
                        ds.child(user_ID)
                                .getValue(User.class)
                                .getUser_id()
                );
            }


        }
        return new UserSettings(user, settings);


    }

    // counts all the photos tha user has
    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_profile_post))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
    }



    public void uploadNewPostNoPic (final String caption, final String interest, final String image){
        Log.d(TAG, "uploadNewPostNoPic: attempting to upload new photo.");

            Log.d(TAG, "uploadNewPost: uploading new post with no pic");


            addPostToDatabaseNoPic(caption, interest,image );

            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);

            //UploadTask uploadTask = null;

/*
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    addPostToDatabaseNoPic(caption, interest);
                }
            });
*/


/*            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


*//*                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String firebaseUrl = uri.toString();

                            addPostToDatabase(caption, interest);
                        }
                    });*//*


                    // navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);


                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "Poto upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });*/

        }


    public void uploadNewPost(String photoType, final String caption, final String interest, int count, String imgUrl){
        Log.d(TAG, "uploadNewPost: attempting to upload new photo.");

        final FilePaths filePaths = new FilePaths();
        //case1) new post
        if(photoType.equals(mContext.getString(R.string.new_post))){
            Log.d(TAG, "uploadNewPost: uploading new post");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference
                    .child(filePaths.FIRBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap

                Bitmap bm = ImageManager.getBitmap(imgUrl);


                byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

                UploadTask uploadTask = null;
                uploadTask = storageReference.putBytes(bytes);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String firebaseUrl = uri.toString();

                                addPostToDatabase(caption, interest, firebaseUrl);
                            }
                        });


                        // navigate to the main feed so the user can see their photo
                        Intent intent = new Intent(mContext, MainActivity.class);
                        mContext.startActivity(intent);


                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Photo upload failed.");
                        Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        if (progress - 15 > mPhotoUploadProgress) {
                            Toast.makeText(mContext, "Poto upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress = progress;
                        }
                        Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                    }
                });

        }
        //case2) change profile photo
        else if(photoType.equals(mContext.getString(R.string.new_profile_photo))){
            Log.d(TAG, "uploadNewPost: uploading new Profile photo");


            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference
                    .child(filePaths.FIRBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo" );

            //convert image url to bitmap
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String firebaseUrl = uri.toString();

                            //insert into 'user_account_settings node
                            setProfilePhoto(firebaseUrl);
                        }
                    });

                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if(progress - 15> mPhotoUploadProgress) {
                        Toast.makeText(mContext, "Poto upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }
    }

    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: setting new profile image " + url);

        mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.new_profile_photo))
                .setValue(url);
    }

    private String getTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        return sdf.format(new Date());
    }

    private void addPostToDatabase(String caption, String interest,  String url){

        Log.d(TAG, "addPostToDatabase:  adding photo to database");

        //String interest = "";
        String newPostKey = mRef.child(mContext.getString(R.string.dbname_user_profile_post)).push().getKey();
        NewPost newPost = new NewPost();
        newPost.setCaption(caption);
        newPost.setDate_created(getTimeStamp());
        newPost.setImage_path(url);
        newPost.setInterest(interest);
        newPost.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newPost.setPhoto_id(newPostKey);


        //insert into database.
        mRef.child(mContext.getString(R.string.dbname_user_profile_post)).child(newPostKey).setValue(newPost);

        mRef.child(mContext.getString(R.string.dbname_user_interest_post))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPostKey).setValue(newPost);

    }

    private void addPostToDatabaseNoPic(String caption, String interest, String image){

        Log.d(TAG, "addPostToDatabase:  adding photo to database");

        image = "null";

        //String interest = "";
        String newPostKey = mRef.child(mContext.getString(R.string.dbname_user_profile_post)).push().getKey();
        NewPost newPost = new NewPost();
        newPost.setCaption(caption);
        newPost.setDate_created(getTimeStamp());

        newPost.setInterest(interest);
        newPost.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newPost.setPhoto_id(newPostKey);
        newPost.setImage_path(image);

        //insert into database.
        mRef.child(mContext.getString(R.string.dbname_user_profile_post)).child(newPostKey).setValue(newPost);

        mRef.child(mContext.getString(R.string.dbname_user_interest_post))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPostKey).setValue(newPost);

    }

}
