package com.example.realtime_location;

import android.app.DownloadManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.realtime_location.Interface.IFirebaseLoadDone;
import com.example.realtime_location.Interface.IRecyclerItemClickListener;
import com.example.realtime_location.Model.User;
import com.example.realtime_location.Utils.Common;
import com.example.realtime_location.ViewHolder.UserViewHOlder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class AllPeopleActivity extends AppCompatActivity implements IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, UserViewHOlder> adapter,searchAdapter;
    RecyclerView recycler_all_user;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String>suggestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);

        //init view

        searchBar =(MaterialSearchBar)findViewById(R.id.material_search_bar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                List<String> suggest = new ArrayList<>();
                for (String search:suggestList){
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    if(adapter!=null){
                        //if close search , restore default
                        recycler_all_user.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

         recycler_all_user = (RecyclerView)findViewById(R.id.recycle_all_people);
         recycler_all_user.setHasFixedSize(true);
         RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
         recycler_all_user.setLayoutManager(layoutManager);
         recycler_all_user.addItemDecoration(new DividerItemDecoration(this,((LinearLayoutManager) layoutManager).getOrientation()));


         firebaseLoadDone = this;
         loadUserList();
         loadSearchData();

    }

    private void loadSearchData() {
        final List<String>lstUseremail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION);
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapShot:dataSnapshot.getChildren()){
                    User user = userSnapShot.getValue(User.class);
                    lstUseremail.add(user.getEmail());
                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lstUseremail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());

            }
        });
    }

    private void loadUserList() {
        Query query =FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        adapter =new FirebaseRecyclerAdapter<User, UserViewHOlder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHOlder holder, int position, @NonNull User model) {
               if (model.getEmail().equals(Common.loggedUser.getEmail())){
                   holder.txt_user_email.setText(new StringBuilder(model.getEmail()).append("me"));
                   holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);

               }
               else {
                   holder.txt_user_email.setText(new StringBuilder(model.getEmail()));
               }
               //Event
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {

                    }
                });
            }

            @NonNull
            @Override
            public UserViewHOlder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);

                return new UserViewHOlder(itemView);
            }
        };

        adapter.startListening();
        recycler_all_user.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        if (adapter !=null)
            adapter.stopListening();
        if (searchAdapter !=null)
            searchAdapter.stopListening();
        super.onStop();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        if (adapter !=null)
            adapter.startListening();
        if (searchAdapter!=null)
            searchAdapter.startListening();
    }

    private void startSearch(String text_search) {
        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .orderByChild("name")
                .startAt(text_search);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        searchAdapter =new FirebaseRecyclerAdapter<User, UserViewHOlder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHOlder holder, int position, @NonNull User model) {
                if (model.getEmail().equals(Common.loggedUser.getEmail())){
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()).append(" (me"));
                    holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);

                }
                else {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                }
                //Event
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {

                    }
                });
            }

            @NonNull
            @Override
            public UserViewHOlder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);

                return new UserViewHOlder(itemView);
            }
        };

        searchAdapter.startListening();
        recycler_all_user.setAdapter(searchAdapter);


    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
    searchBar.setLastSuggestions(lstEmail);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }
}
