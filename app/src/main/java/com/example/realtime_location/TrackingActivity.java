package com.example.realtime_location;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.realtime_location.Model.MyLocation;
import com.example.realtime_location.Utils.Common;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    DatabaseReference trackingUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerEventRealtime();
    }

    private void registerEventRealtime() {

        trackingUserLocation = FirebaseDatabase.getInstance()
                .getReference(Common.Public_Location)
                .child(Common.trackingUser.getUid());
        trackingUserLocation.addValueEventListener(this);

    }

    @Override
    protected void onStop() {
        trackingUserLocation.removeEventListener(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackingUserLocation.addValueEventListener(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Enable xoom ui
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //set skin for map
        MapStyleOptions mapStyleOptions;
        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this , R.raw.my_uber_style));


    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() !=null){

            MyLocation location = dataSnapshot.getValue(MyLocation.class);

            //Add Marker

            LatLng userMarker = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userMarker)
            .title(Common.trackingUser.getEmail())
            .snippet(Common.getDateFormatted(Common.convertTimeStampToDate(location.getTime()))));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker,16f));
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
