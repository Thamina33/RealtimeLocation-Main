package com.example.realtime_location.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.realtime_location.R;

public class UserViewHOlder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView txt_user_email;
    public UserViewHOlder(@NonNull View itemView) {
        super(itemView);
        txt_user_email = (TextView) itemView.findViewById(R.id.txt_user_email);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}
