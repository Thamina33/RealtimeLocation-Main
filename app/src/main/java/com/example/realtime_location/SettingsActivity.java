package com.example.realtime_location;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.realtime_location.Model.notificationModel;
import com.example.realtime_location.Model.reUser;
import com.example.realtime_location.ViewHolder.viewHolderForFriendList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    Button setContact ;
    TextView mtexview ;
    DatabaseReference mref  ;
    FirebaseAuth mauth ;

    private ProgressBar job_progressBar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setContact = findViewById(R.id.emergencyContact);
        mtexview = findViewById(R.id.textViewaa);
        mauth = FirebaseAuth.getInstance();
        String uid = mauth.getUid();


        mref = FirebaseDatabase.getInstance().getReference("UserInformation").child(uid).child("emergentcyContact");


        loadDatafromFireBAse();


        setContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent o  =new Intent(getApplicationContext() , loadAllYourFriendHere.class);
                startActivity(o);


            }
        });

    }

    private void loadDatafromFireBAse() {



                mref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String email = "";
                       notificationModel model = dataSnapshot.getValue(notificationModel.class);
                       try{
                            email = model.getEmail() ;
                       }
                       catch (NullPointerException e ){

                           mtexview.setText("You Dont Have Any Friend");
                        }



                       if(!TextUtils.isEmpty(email))
                       {

                           mtexview.setText(email);

                       }
                      else{




                       }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
}
