package com.example.realtime_location;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
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
import com.example.realtime_location.Model.MyResponse;
import com.example.realtime_location.Model.Request;
import com.example.realtime_location.Model.User;
import com.example.realtime_location.Remote.lFCMService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;



public class AllPeopleActivity extends AppCompatActivity implements IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, UserViewHOlder> adapter,searchAdapter;
    RecyclerView recycler_all_user;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String>suggestList = new ArrayList<>();

    lFCMService lFCMService ;
    CompositeDisposable compositeDisposable =new CompositeDisposable();

    



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);

        //init api
        lFCMService=Common.getFCMSerice();

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
            protected void onBindViewHolder(@NonNull UserViewHOlder holder, int position, @NonNull final User model) {
               if (model.getEmail().contains(Common.loggedUser.getEmail())){
                   holder.txt_user_email.setText(model.getEmail());
                 //  holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);

               }
               else {
                   holder.txt_user_email.setText(model.getEmail());
               }
               //Event
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {

                        //
                        showDialogRequest(model);

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

    private void showDialogRequest(final User model) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.MyRequestDialog);
        alertDialog.setTitle("Request Friend");
        alertDialog.setMessage("Do you want to sent request friend to "+model.getEmail());
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add to Acpet list
                DatabaseReference acceptList = FirebaseDatabase.getInstance()
                        .getReference(Common.USER_INFORMATION)
                        .child(Common.loggedUser.getUid())
                        .child(Common.ACCEPT_LIST);
                acceptList.orderByKey().equalTo(model.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue()==null)//if not friend before
                                    sendFriendRequest(model);
                                else
                                    Toast.makeText(AllPeopleActivity.this, "You and "+model.getEmail()+"already are friends",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });

        alertDialog.show();//don't forget it
    }

    private void sendFriendRequest(final User model) {
        //get token to sent
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.TOKENS);
        tokens.orderByKey().equalTo(model.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()==null)
                            Toast.makeText(AllPeopleActivity.this, "Token Error",Toast.LENGTH_SHORT).show();

                        else {
                            //Create Request
                            Request request = new Request();
                        //create list
                            Map<String,String> dataSend=new HashMap<>();
                            dataSend.put(Common.FROM_UID,Common.loggedUser.getUid());
                            dataSend.put(Common.FROM_NAME,Common.loggedUser.getEmail());
                            dataSend.put(Common.To_UID,model.getUid());
                            dataSend.put(Common.To_NAME,model.getEmail());

                            request.setTo(dataSnapshot.child(model.getUid()).getValue(String.class));
                            request.setData(dataSend);

                            compositeDisposable.add(lFCMService.sendFriendRequestToUser(request)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<MyResponse>() {
                                        @Override
                                        public void accept(MyResponse myResponse) throws Exception {

                                            if(myResponse.success==1)
                                            {
                                                Toast.makeText(getApplicationContext() , "Request  Sent ", Toast.LENGTH_SHORT)
                                                        .show();
                                            }



                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {

                                            Toast.makeText(getApplicationContext() , throwable.getMessage(), Toast.LENGTH_SHORT)
                                                    .show();

                                        }
                                    }));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    protected void onStop() {
        if (adapter !=null)
            adapter.stopListening();
        if (searchAdapter !=null)
            searchAdapter.stopListening();
        compositeDisposable.clear();
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
                .orderByChild("email")
                .startAt(text_search);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        searchAdapter =new FirebaseRecyclerAdapter<User, UserViewHOlder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHOlder holder, int position, @NonNull final User model) {
                if (model.getEmail().contains(Common.loggedUser.getEmail())){
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                    holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);

                }
                else {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                }
                //Event
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        showDialogRequest(model);

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
