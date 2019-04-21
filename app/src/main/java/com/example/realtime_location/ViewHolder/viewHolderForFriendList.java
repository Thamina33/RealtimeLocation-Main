package com.example.realtime_location.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.realtime_location.R;

public class viewHolderForFriendList  extends RecyclerView.ViewHolder{



    View  mView ;
    public viewHolderForFriendList(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });
        //item long click
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
        });

    }

    //set details to recycler view row
    public void setDetails(Context ctx, String title, String description){
        //Views
        TextView mTitleTv = mView.findViewById(R.id.txt_user_email);
        TextView mDetailTv = mView.findViewById(R.id.uid);
        //set data to views
        mTitleTv.setText(title);
        mDetailTv.setText(description);



    }








private viewHolderForFriendList.ClickListener mClickListener;

//interface to send callbacks
public interface ClickListener{
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
}

    public void setOnClickListener(viewHolderForFriendList.ClickListener clickListener){
        mClickListener = clickListener;
    }

        }