package com.beepic.Interest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beepic.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    private Context mContext;
    private List<Interest> mData;

    public RecyclerViewAdapter(Context mContext, List<Interest> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_interest, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_interest_title.setText(mData.get(position).getTitle());
        holder.img_interest_thumbnail.setImageResource(mData.get(position).getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_interest_title;
        ImageView img_interest_thumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_interest_title = (TextView) itemView.findViewById(R.id.interest_title_id);
            img_interest_thumbnail = (ImageView) itemView.findViewById(R.id.interest_img_id);
        }
    }

}
