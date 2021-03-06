package com.example.realtime_location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realtime_location.Interface.IFirebaseLoadDone;
import com.example.realtime_location.Interface.IRecyclerItemClickListener;
import com.example.realtime_location.Model.User;
import com.example.realtime_location.Model.notificationModel;
import com.example.realtime_location.Service.MyLocationReceiver;
import com.example.realtime_location.Utils.Common;
import com.example.realtime_location.ViewHolder.UserViewHOlder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, UserViewHOlder> adapter, searchAdapter;
    RecyclerView recycle_Friend_List;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();
    FirebaseAuth mauth ;
    String NotificationId ;

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationOpenedHandler(new notificationOpenHandler())
                .setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
                    @Override
                    public void notificationReceived(OSNotification notification) {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(intent);


                    }
                })
                .autoPromptLocation(true)
                .init();

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        NotificationId = status.getSubscriptionStatus().getUserId();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TriggerNottification() ;

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View haderView = navigationView.getHeaderView(0);
        TextView txt_user_logged = (TextView) haderView.findViewById(R.id.txt_logged_email);
        txt_user_logged.setText(Common.loggedUser.getEmail());

        //view

        //init view
        searchBar = (MaterialSearchBar) findViewById(R.id.material_search_bar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        insertOneDataIntoFirebasae();

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    if (adapter != null) {
                        //if close search , restore default
                        recycle_Friend_List.setAdapter(adapter);
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

        recycle_Friend_List = (RecyclerView) findViewById(R.id.recycle_Friend_List);
        recycle_Friend_List.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycle_Friend_List.setLayoutManager(layoutManager);
        recycle_Friend_List.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));

        //update Location
       updateLocation();
        firebaseLoadDone=this;

        loadFriendList();
        loadSearchData();
    }

    private void TriggerNottification() {


        DatabaseReference mref ;

        mauth = FirebaseAuth.getInstance();
        String uid = mauth.getCurrentUser().getUid();


        mref = FirebaseDatabase.getInstance().getReference("UserInformation").child(uid).child("emergentcyContact");

        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                notificationModel  model = dataSnapshot.getValue(notificationModel.class);
                String nuid  = model.getNuid();
                if(!TextUtils.isEmpty(nuid))
                         {


                            try{
                               // Toast.makeText(getApplicationContext() , nuid , Toast.LENGTH_LONG)
                                       // .show();
                                    String email = mauth.getCurrentUser().getEmail();


                                JSONObject notificationContent = new JSONObject("{'contents': {'en': '"+email+" is in  Danger !!!'}," +
                                        "'include_player_ids': ['" + nuid + "'], " +
                                        "'headings': {'en':'Alert!!!'}, " +
                                        "'big_picture': ''}");
                                OneSignal.postNotification(notificationContent, null);


                                Toast.makeText(getApplicationContext() , "Notification Sent" ,Toast.LENGTH_LONG).show();


                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext() , e.getMessage() ,Toast.LENGTH_LONG).show();
                            }




                }
                else {


                    Toast.makeText(getApplicationContext() , "You Didnot Add Any Friend" , Toast.LENGTH_LONG)
                        .show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadSearchData() {

        final List<String>lstUseremail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);

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

    private void loadFriendList() {

        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<User, UserViewHOlder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHOlder holder, int position, @NonNull final User model) {
                holder.txt_user_email.setText(new StringBuilder(model.getEmail()));

               holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                   @Override
                   public void onItemClickListener(View view, int position) {
                       //show tracking
                       Common.trackingUser = model;
                       startActivity(new Intent(HomeActivity.this,TrackingActivity.class));

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
        recycle_Friend_List.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        if (adapter !=null)
            adapter.stopListening();
        if (searchAdapter!=null)
            searchAdapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (adapter !=null)
            adapter.startListening();
        if (searchAdapter!=null)
            searchAdapter.startListening();
    }

    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(HomeActivity.this,MyLocationReceiver.class);
        intent.setAction(MyLocationReceiver.ACTION);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setFastestInterval(3000);
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void insertOneDataIntoFirebasae(){

        FirebaseAuth mauth = FirebaseAuth.getInstance();
        String uid = mauth.getCurrentUser().getUid();

        DatabaseReference mref  = FirebaseDatabase.getInstance().getReference("UserInformation");

        HashMap  map = new HashMap();
        map.put("nuid", NotificationId);



        mref.child(uid).child("notificationUid").setValue(map) ;






    }

    private void startSearch(String search_value) {

        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST)
                .orderByChild("name")
                .startAt(search_value);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        searchAdapter = new FirebaseRecyclerAdapter<User, UserViewHOlder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHOlder holder, int position, @NonNull final User model) {
                holder.txt_user_email.setText(new StringBuilder(model.getEmail()));

                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        Common.trackingUser = model;
                        startActivity(new Intent(HomeActivity.this,TrackingActivity.class));

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
        recycle_Friend_List.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_find_people) {
           startActivity(new Intent(HomeActivity.this,AllPeopleActivity.class     ));
        } else if (id == R.id.nav_add_people) {
            startActivity(new Intent(HomeActivity.this,FriendRequestActivity.class     ));
        } else if (id == R.id.nav_sign_out) {

            String action;
            Intent  o = new Intent(getApplicationContext() , MainActivity.class);
            startActivity(o);
            finish();

        }
         else if (id == R.id.nav_settings_people){

            Intent  o = new Intent(getApplicationContext() , SettingsActivity.class);
            startActivity(o);

        }
        else if (id == R.id.nav_period_calcu){

            Intent  i = new Intent(getApplicationContext() , PeriodCalculator.class);
            startActivity(i);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }
    public class notificationOpenHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            //   String title = result.notification.payload.title;
            String desc = result.notification.payload.body;
            //  String f = result.notification.payload.groupKey

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            /*
           if(desc.equals("Voca_activity")){
               Intent intent = new Intent(getApplicationContext(), Voca_activity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
           }
           else {
               Intent intent = new Intent(getApplicationContext(), NottificationPage.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
           }
*/

        }

    }



}
