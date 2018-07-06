package com.example.pcworld.search_map;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.directions.route.AbstractRouting;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_PHONE_STATE;

public class Maps1Activity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener,RoutingListener,GoogleMap.OnMarkerClickListener {

    private LocationManager locationManager = null;
    private Button drive,walk1;
    private TextView txtDistance, txtTime;
    private LocationListener listener;

    //Global UI Map markers
    private Marker currentMarker = null;
    private Marker destMarker = null;
    private LatLng currentLatLng = null;
    private Polyline line = null;
    public static final int RequestPermissionCode = 99;

    private boolean firstRefresh = true;
    private static boolean flag=true;
    private GoogleMap mMap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps1);
        Constants.POINT_DEST = new LatLng(31.220071, 29.931529);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        txtDistance = (TextView) findViewById(R.id.txt_distance);
        txtTime = (TextView) findViewById(R.id.txt_time);
        drive = findViewById(R.id.feb);
        walk1 = findViewById(R.id.walk);

        if (checkPermission()) {
            Toast.makeText(Maps1Activity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();

        } else {
            requestPermission();

        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Maps1Activity.this.getRoutingPathdrive();
                Toast.makeText(Maps1Activity.this, "start", Toast.LENGTH_SHORT).show();
                flag=false;

            }
        });
        walk1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Maps1Activity.this.getRoutingPathwalk();
                Toast.makeText(Maps1Activity.this, "start", Toast.LENGTH_SHORT).show();
            }
        });


    /**
     * @desc LocationListener Interface Methods implemented.
     */
    listener =new LocationListener() {
        @Override
        public void onLocationChanged (Location location)
        {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            currentLatLng = new LatLng(lat, lng);
            if (firstRefresh) {
                //Add Start Marker.
                currentMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Position"));//.icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                firstRefresh = false;
                destMarker = mMap.addMarker(new MarkerOptions().position(Constants.POINT_DEST).title("Destination"));//.icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(Constants.POINT_DEST));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                if(flag){
                    getRoutingPathdrive();
                }else {
                    getRoutingPathwalk();
                }
                //getRoutingPath();
            } else {
              //  LatLng test=new LatLng(31.207441, 29.919142);
                currentMarker.setPosition(currentLatLng);
            }
        }
        @Override
        public void onStatusChanged (String s,int i, Bundle bundle){

        }

        @Override
        public void onProviderEnabled (String s){

        }

        @Override
        public void onProviderDisabled (String s){

            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
    };

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)

    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                    , 10);
        }
        return;
    }



        locationManager.requestLocationUpdates("gps",50000,0,listener);
}
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }
    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }}


    private void requestPermission() {

        ActivityCompat.requestPermissions(Maps1Activity.this, new String[]
                {
                        ACCESS_FINE_LOCATION,
                        //READ_PHONE_STATE,
                        ACCESS_COARSE_LOCATION,
                        INTERNET,
                        ACCESS_NETWORK_STATE
                }, RequestPermissionCode);

    }

    /**
     * @desc MapMarker Interface Methods Implemented.
     */

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        if(marker.getTitle().contains("nearest point"))
        {
            //Do some task on dest pin click
        }
        else if(marker.getTitle().contains("Current location"))
        {
            //Do some task on current pin click
        }
        return false;
    }

    /**
     *@desc Routing Listener interface methods implemented.
     **/
    @Override
    public void onRoutingFailure(RouteException e)
    {
        Toast.makeText(Maps1Activity.this, "Routing Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() { }

    @Override
    public void onRoutingSuccess(ArrayList<Route> list, int i)
    {
        try
        {
            //Get all points and plot the polyLine route.
            List<LatLng> listPoints = list.get(0).getPoints();
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            Iterator<LatLng> iterator = listPoints.iterator();
            while(iterator.hasNext())
            {
                LatLng data = iterator.next();
                options.add(data);
            }

            //If line not null then remove old polyline routing.
            if(line != null)
            {
                line.remove();
            }
            line = mMap.addPolyline(options);

            //Show distance and duration.
            txtDistance.setText("Distance: "+ ""+list.get(0).getDistanceText());
            txtTime.setText("Duration: " + ""+list.get(0).getDurationText());

            //Focus on map bounds
            mMap.moveCamera(CameraUpdateFactory.newLatLng(list.get(0).getLatLgnBounds().getCenter()));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(currentLatLng);
            builder.include(Constants.POINT_DEST);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 18));
        }
        catch (Exception e)
        {
            Toast.makeText(Maps1Activity.this, "EXCEPTION: Cannot parse routing response", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled()
    {
        Toast.makeText(Maps1Activity.this, "Routing Cancelled", Toast.LENGTH_SHORT).show();
    }

    /**
     * @method getRoutingPath
     * @desc Method to draw the google routed path.
     */
    private void getRoutingPathdrive()
    {//if(flage)
        try
        {
            Toast.makeText(Maps1Activity.this, "drive Route", Toast.LENGTH_SHORT).show();
            //Do Routing
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)///can change so i will put button to switch
                    //..travelMode(Routing.TravelMode.WALKING)

                    .withListener(this)
                    .waypoints(currentLatLng, Constants.POINT_DEST)
                    .build();
            routing.execute();
        }
        catch (Exception e)
        {
            Toast.makeText(Maps1Activity.this, "Unable to Route", Toast.LENGTH_SHORT).show();
        }
    }
    /////////////
    private void getRoutingPathwalk()
    {//if(flage)
        try
        {
            Toast.makeText(Maps1Activity.this, "walk Route", Toast.LENGTH_SHORT).show();
            //Do Routing
            Routing routing = new Routing.Builder()
                    //.travelMode(Routing.TravelMode.DRIVING)///can change so i will put button to switch
                    .travelMode(Routing.TravelMode.WALKING)

                    .withListener(this)
                    .waypoints(currentLatLng, Constants.POINT_DEST)
                    .build();
            routing.execute();
        }
        catch (Exception e)
        {
            Toast.makeText(Maps1Activity.this, "Unable to Route", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        //int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
             //   SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED; //&&
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setOnMarkerClickListener( this);
    }
}