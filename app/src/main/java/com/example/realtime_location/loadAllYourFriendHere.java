package com.example.realtime_location;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.realtime_location.Model.notificationModel;
import com.example.realtime_location.Model.reUser;
import com.example.realtime_location.Utils.Common;
import com.example.realtime_location.ViewHolder.viewHolderForFriendList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class loadAllYourFriendHere extends AppCompatActivity {
    LinearLayoutManager mLayoutManager; //for sorting
    SharedPreferences mSharedPref; //for saving sort settings
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    Toolbar toolbar ;
    FirebaseRecyclerAdapter<reUser, viewHolderForFriendList> firebaseRecyclerAdapter ;
    FirebaseRecyclerOptions<reUser> options ;

    FirebaseAuth mauth ;
    String uid ;
    String email ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_all_your_friend_here);



        mauth = FirebaseAuth.getInstance();
        uid = mauth.getUid();
        email = mauth.getCurrentUser().getEmail();


        //RecyclerView
        mRecyclerView = findViewById(R.id.recycleForAllFriend);
        mRecyclerView.setHasFixedSize(true);

        //set layout as LinearLayout
        mLayoutManager = new LinearLayoutManager(this);
        //this will load the items from bottom means newest first
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        //send Query to FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();



        showData();

    }

    private  void showData(){

        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);


        options = new FirebaseRecyclerOptions.Builder<reUser>().setQuery(query , reUser.class)
                .build() ;

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<reUser, viewHolderForFriendList>(options) {
            @Override
            protected void onBindViewHolder(@NonNull viewHolderForFriendList holder, int position, @NonNull reUser model) {
                holder.setDetails(getApplicationContext(), model.getEmail(), model.getUid());

            }

            @NonNull
            @Override
            public viewHolderForFriendList onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                //INflate the row
                Context context;
                View itemVIew = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_user, viewGroup, false);

                viewHolderForFriendList viewHolder = new viewHolderForFriendList(itemVIew);

                //itemClicklistener
                viewHolder.setOnClickListener(new viewHolderForFriendList.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //Views
                        //   TextView mTitleTv = view.findViewById(R.id.rTitleTv);
                        //    TextView mDescTv = view.findViewById(R.id.rDescriptionTv);
                        //     ImageView mImageView = view.findViewById(R.id.rImageView);



                        //get data from views
                        String mTitle = getItem(position).getEmail() ;                         //mTitleTv.getText().toString();
                        String mDesc = getItem(position).getUid();
                        //mDescTv.getText().toString();



                        addFrindINEmergencyList(mDesc , mTitle) ;



                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });



                return viewHolder;
            }
        };

        mRecyclerView.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter.startListening();
        //setting adapter

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void addFrindINEmergencyList(final String mDesc , final  String mTitle) {
        mRef = mFirebaseDatabase.getReference("UserInformation");


        mRef.child(mDesc).child("notificationUid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                notificationModel  model = dataSnapshot.getValue(notificationModel.class) ;
                String nUid = model.getNuid();



                if(nUid!= null){


                    HashMap map = new HashMap();
                    map.put("nuid", nUid) ;
                    map.put("uid" , mDesc);
                    map.put("email",mTitle );


                    mRef.child(uid).child("emergentcyContact").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext() , "Friend Added To The Emergency List " , Toast.LENGTH_LONG)
                                    .show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });




                }
                else {


                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

}
