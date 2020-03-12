package com.beepic.Interest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beepic.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    SharedPreferences prefs;
    SharedPreferences.Editor mEditor;

    private Context mContext;
    private List<Interest> mData;
    public ArrayList<Interest> checkedInterest = new ArrayList<>();
    private SparseBooleanArray itemStateArray= new SparseBooleanArray();

    private boolean checked = false;




    public RecyclerViewAdapter(Context mContext, List<Interest> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mContext = mContext;


        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = prefs.edit();

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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.tv_interest_title.setText(mData.get(position).getTitle());
        holder.img_interest_thumbnail.setImageResource(mData.get(position).getThumbnail());
        //holder.chk.setOnCheckedChangeListener(null);
        //holder.chk.setChecked(numbers.get(position).is);
        holder.chk.setChecked(mData.get(position).isSelected());
        holder.chk.setTag(position);

        //Shared Pref Starts Here


        //causing method in interest activity to not run the button click

/*        holder.chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){




            }
        });*/





        holder.setItemClickListener(new ItemClickListener(){
            @Override
            public void onItemClick(View v, int pos){

                CheckBox chk = (CheckBox) v;

/*
                chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (buttonView.isChecked()){
                            Log.d(TAG, "button is checked!");
                            checked = true; //Change boolean value 'True" because it's checked!
                        }else {
                            Log.d(TAG, "button is not checked!");
                            checked = false; //Change boolean value 'False' because it's unchecked!
                        }
                    }
                });
*/



                if (chk.isChecked()) {
                    checkedInterest.add(mData.get(pos));

                    Log.d(TAG, "onItemClick: checked state 1 " + checkedInterest);
                }else if(!chk.isChecked()){
                    checkedInterest.remove(mData.get(pos));
                    Log.d(TAG, "onItemClick: checked state 2 " + checkedInterest);
                }


                //Integer pos =  (int) holder.chk.getTag();
                //Toast.makeText(mContext, mData.get(position).getTitle() + " Clicked!", Toast.LENGTH_SHORT).show();

                if (mData.get(pos).isSelected()) {
                    mData.get(pos).setSelected(false);
                } else {
                    mData.get(pos).setSelected(true);

                }
            }
        } );


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_interest_title;
        ImageView img_interest_thumbnail;
        CheckBox chk;
        ItemClickListener itemClickListener;

        //public SharedPreferences prefs;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_interest_title =  itemView.findViewById(R.id.interest_title_id);
            img_interest_thumbnail =  itemView.findViewById(R.id.interest_img_id);
            chk = itemView.findViewById(R.id.cbInterest);








            chk.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener ic){

            this.itemClickListener = ic;
        }

       @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());




        }
    }

}
