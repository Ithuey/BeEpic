package com.beepic.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beepic.ProfileActivity;
import com.beepic.R;
import com.beepic.models.NewPost;
import com.beepic.models.User;
import com.beepic.models.UserAccountSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilefeedAdapter extends ArrayAdapter<NewPost> {

    private static final String TAG = "MainfeedListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername = "";

    public ProfilefeedAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<NewPost> objects) {
        super(context, resource, objects);


        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;
        mReference = FirebaseDatabase.getInstance().getReference();

        initImageLoader();
    }


    static class ViewHolder {
        CircleImageView mProfileImage;
        String likeString; //might not need
        TextView username, timeDelta, caption, interest, likes, comments;
        ImageView image;
        ImageView starFullWhite, startOutlineWhite, comment;

        UserAccountSettings setting = new UserAccountSettings();
        User user = new User();
        //StringBuilder users;
        String mLikeString;
        boolean likeByCurrentUser;
        //Star star;
        //GestureDetector detector;
        NewPost post;
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ProfilefeedAdapter.ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ProfilefeedAdapter.ViewHolder();

            holder.username = convertView.findViewById(R.id.tv_name);
            holder.image = convertView.findViewById(R.id.imgView_postPic);
            //holder.starFullWhite = convertView.FindViewById(R.id.starFullWhite);
            //holder.startOutlineWhite = convertView.FindViewById(R.id.startOutlineWhite);
            //holder.comments = convertView.findViewById(R.id.tv_comment);
            holder.caption = convertView.findViewById(R.id.tv_status);
            holder.timeDelta = convertView.findViewById(R.id.tv_time); //this is correct
            holder.mProfileImage = convertView.findViewById(R.id.imgView_proPic);//this is correct
            holder.interest = convertView.findViewById(R.id.interest_feed);
            //holder.star = new Star(holder.starFullWhite, holder.startOutlineWhite);
            holder.post = getItem(position);
            //holder.detector = new GestureListener(holder); this deals with likes
            //holder.users = new StringBuilder();

            convertView.setTag(holder);

        }else{
            holder = (ProfilefeedAdapter.ViewHolder) convertView.getTag();
        }
        //get current users username (need for checking likes string)
        getCurrentUsername();

        //set the time it was posted
        String timestampDifference = getTimeDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timeDelta.setText(timestampDifference + " days ago");

        }else {
            holder.timeDelta.setText("Today");
        }

        //getting interest and caption and imagepath
        holder.interest.setText(getItem(position).getInterest());
        holder.caption.setText(getItem(position).getCaption());




        //set the profile image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImage_path(), holder.mProfileImage);
       // imageLoader.displayImage(getItem(position).getImage_path(), holder.image);


        if (getItem(position).getImage_path().contains("null")){
            Log.d(TAG, "getView: ImagePath " + getItem(position).getImage_path());
            imageLoader.displayImage(getItem(position).getImage_path(), holder.image);
            holder.image.setVisibility(View.GONE);
        }else {
            holder.image.setVisibility(View.VISIBLE);
            Log.d(TAG, "getView: ImagePath " + getItem(position).getImage_path());
            imageLoader.displayImage(getItem(position).getImage_path(), holder.image);
        }

        //get the profile image and username
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByKey()
                //.orderByChild(mContext.getString(R.string.field_username))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    //currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();

                    Log.d(TAG, "onDataChange: found user that: "
                            + singleSnapshot.getValue(UserAccountSettings.class).getUsername());

                    holder.username.setText(singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigate to the user profile page: " + holder.user.getUsername());

                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity),
                                    mContext.getString(R.string.home_activity));

                        }
                    });



                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
                            holder.mProfileImage);
                    holder.mProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigate to the user profile page: " + holder.user.getUsername());

                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity),
                                    mContext.getString(R.string.home_activity));

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //get the user objects
        Query userQuery = mReference
                .child(mContext.getString(R.string.dbname_user))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    Log.d(TAG, "onDataChange: found user this:"  + singleSnapshot.getValue(User.class).getUsername());

                    holder.user = singleSnapshot.getValue(User.class);
                    //holder.interest = singleSnapshot.getValue(get);
                    //holder.username.setText(singleSnapshot.getValue(User.class).getUsername());
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        return convertView;
    }


    //get time stamp from post and converts it
    private String getTimeDifference(NewPost post) {
        Log.d(TAG, "getTimeDifference: getting timestap difference");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;

        final String postTimestamp = post.getDate_created();
        try{
            timestamp = sdf.parse(postTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60/ 60 / 24)));
        }catch (ParseException e){
            Log.d(TAG, "getTimeDifference: ParseException: " + e.getMessage());
            difference = "0";
        }
        return difference;
    }




    //this does nothing at the moment
    private void getCurrentUsername(){
        Log.d(TAG, "getCurrentUsername: retrieving user account settings");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
