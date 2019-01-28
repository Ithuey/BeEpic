package com.beepic.MainFeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;



import com.beepic.R;

import java.util.ArrayList;

public class MainRecycleViewAdapter extends RecyclerView.Adapter<MainRecycleViewAdapter.MyViewHolder> {

    private Context mContext;
    ArrayList<MainFeed> mainFeedArrayList = new ArrayList<>();
    RequestManager glide;

    public MainRecycleViewAdapter (Context mContext, ArrayList<MainFeed> mainFeedArrayList){

        this.mContext = mContext;
        this.mainFeedArrayList = mainFeedArrayList;
        glide= Glide.with(mContext);
    }

    @NonNull
    @Override
    public MainRecycleViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.main_feed, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainRecycleViewAdapter.MyViewHolder myViewHolder, int position) {
        final MainFeed mainFeed = mainFeedArrayList.get(position);

        myViewHolder.tv_name.setText(mainFeed.getName());
        myViewHolder.tv_interest.setText(mainFeed.getInterest());
        myViewHolder.tv_like.setText(String.valueOf(mainFeed.getLikes()));
        myViewHolder.tv_comment.setText(mainFeed.getComments()+ " comments");
        myViewHolder.tv_status.setText(mainFeed.getStatus());
        myViewHolder.tv_time.setText(mainFeed.getTime());

        glide.load(mainFeed.getPropic()).into(myViewHolder.imgView_proPic);

        if (mainFeed.getPostpic() == 0) {
            myViewHolder.imgView_postPic.setVisibility(View.GONE);
        } else {
            myViewHolder.imgView_postPic.setVisibility(View.VISIBLE);
            glide.load(mainFeed.getPostpic()).into(myViewHolder.imgView_postPic);
        }
    }

    @Override
    public int getItemCount() {
        return mainFeedArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name, tv_time, tv_interest, tv_status, tv_like, tv_comment;
        ImageView imgView_proPic, imgView_postPic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            imgView_proPic = itemView.findViewById(R.id.imgView_proPic);
            imgView_postPic = itemView.findViewById(R.id.imgView_postPic);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_interest = itemView.findViewById(R.id.interest_feed);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_like = itemView.findViewById(R.id.tv_like);
            tv_comment = itemView.findViewById(R.id.tv_comment);






        }
    }
}
