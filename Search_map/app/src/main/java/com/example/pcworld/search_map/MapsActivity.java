  package com.example.pcworld.search_map;


  import android.content.pm.PackageManager;
  import android.graphics.Color;
  import android.location.Location;


  import android.os.Looper;
  import android.support.annotation.NonNull;
  import android.support.annotation.Nullable;
  import android.support.v4.app.ActivityCompat;
  import android.support.v4.app.FragmentActivity;
  import android.os.Bundle;
  import android.support.v4.content.ContextCompat;

  import android.util.Log;
  import android.widget.Toast;

  import com.google.android.gms.common.ConnectionResult;
  import com.google.android.gms.location.FusedLocationProviderApi;
  import com.google.android.gms.location.FusedLocationProviderClient;
  import com.google.android.gms.location.LocationCallback;
  import com.google.android.gms.location.LocationListener;
  import com.google.android.gms.common.api.GoogleApiClient;
  import com.google.android.gms.location.LocationRequest;
  import com.google.android.gms.location.LocationResult;
  import com.google.android.gms.location.LocationServices;
  import com.google.android.gms.maps.CameraUpdateFactory;
  import com.google.android.gms.maps.GoogleMap;
  import com.google.android.gms.maps.OnMapReadyCallback;
  import com.google.android.gms.maps.SupportMapFragment;
  import com.google.android.gms.maps.model.BitmapDescriptor;
  import com.google.android.gms.maps.model.BitmapDescriptorFactory;
  import com.google.android.gms.maps.model.LatLng;
  import com.google.android.gms.maps.model.Marker;
  import com.google.android.gms.maps.model.MarkerOptions;
  import com.google.android.gms.maps.model.PolygonOptions;
  import com.google.android.gms.maps.model.Polyline;
  import com.google.android.gms.maps.model.PolylineOptions;

  import java.util.ArrayList;

  import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
  import static android.Manifest.permission.ACCESS_FINE_LOCATION;
  import static android.Manifest.permission.ACCESS_NETWORK_STATE;
  import static android.Manifest.permission.INTERNET;
  import static android.Manifest.permission.READ_PHONE_STATE;


  public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

      private GoogleMap mMap;
      // private GoogleApiClient client;
      private LocationRequest locationRequest;
      Location LastLocation;
      private FusedLocationProviderClient mFusedLocationClient;
     // private Location LastLoction;
      private Marker currentLocationMarker;
      public double x;
      public double y;
      // public static final int REQUEST_LOCATION_CODE=99;
      public static final int RequestPermissionCode = 99;

      // private ArrayList<LatLng> points; //added
      ArrayList<LatLng> MarkerPoints1;

      Polyline line; //added

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          //points = new ArrayList<LatLng>();
          MarkerPoints1 = new ArrayList<>();
          setContentView(R.layout.activity_maps);
          mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
          // Obtain the SupportMapFragment and get notified when the map is ready to be used.
          SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
          mapFragment.getMapAsync(this);

//          if (checkLocationPermission()) {
//              Toast.makeText(MapsActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
//
//          } else {
//              requestPermission();
//
//          }

          Log.i(MapsActivity.class.getSimpleName(), "m 4 ");
      }
//      private void requestPermission() {
//          if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
//          if(ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, ACCESS_FINE_LOCATION))
//                {
////                  {
////                          ACCESS_FINE_LOCATION,
////                          READ_PHONE_STATE,
////                          ACCESS_COARSE_LOCATION,
////                        //  INTERNET,
////                        //  ACCESS_NETWORK_STATE
////                  }, REQUEST_LOCATION_CODE);
//          }
//      }
//      }

      @Override
      public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults);
          Log.i(MapsActivity.class.getSimpleName(), "m 1 ");
          switch (requestCode) {
              case RequestPermissionCode:
                  if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                      //prPERMISSION is GRANTED
                      if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                          Log.i(MapsActivity.class.getSimpleName(), "m 1 ");
                          mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                          mMap.setMyLocationEnabled(true);
                          Log.i(MapsActivity.class.getSimpleName(), "m 1 ");
                      }

                  } else //prPERMISSION is denied
                  {
                      Toast.makeText(this, "permation Denied!", Toast.LENGTH_LONG).show();
                  }
                  return;
          }
      }


      LocationCallback mLocationCallback = new LocationCallback() {
          @Override
          public void onLocationResult(LocationResult locationResult) {
              for (Location location : locationResult.getLocations()) {
                  LastLocation = location;

                  Log.i(MapsActivity.class.getSimpleName(), "m 3 ");
                  double latitude = LastLocation.getLatitude();
                  double longitude = LastLocation.getLongitude();
                  Toast.makeText(getApplicationContext(), "Your Location is -\nLat: " + latitude + "\nLong: "
                          + longitude , Toast.LENGTH_LONG).show();

                  // LatLng hcmus1 = new LatLng(31.211854, 29.923490);
                  LatLng latLng = new LatLng(latitude, longitude); //you already have this
                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                  MarkerPoints1.add(latLng);
                  MarkerOptions markerOptions = new MarkerOptions();
                  markerOptions.position(latLng);
                  markerOptions.title("current location");
                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                  //  for (int i = 0; i < 4; i++) {
                  int i = 0;
                  LatLng finish1 = new LatLng(MarkerPoints1.get(i).latitude + 0, MarkerPoints1.get(i).longitude + 0.00035); // +y
                  LatLng finish2 = new LatLng(MarkerPoints1.get(i).latitude - 0.00035, MarkerPoints1.get(i).longitude + 0); // -x
                  LatLng finish3 = new LatLng(MarkerPoints1.get(i).latitude + 0, MarkerPoints1.get(i).longitude - 0.00035); // -y
                  LatLng finish4 = new LatLng(MarkerPoints1.get(i).latitude + 0.00035, MarkerPoints1.get(i).longitude + 0); // +x
                  mMap.addPolygon(new PolygonOptions().add(finish1, finish2, finish3, finish4).strokeColor(Color.argb(0, 0, 255, 0)).fillColor(Color.argb(130, 0, 255, 0)));
                  //   }



                  // LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
//        MarkerOptions markerOptions=new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("current location");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//        currentLocationMarker =mMap.addMarker(markerOptions);
                  //    points.add(latLng); //added
                  //      redrawLine(); //added

                  //  mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                  //mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
              }
          }
      };



      @Override
      public void onMapReady(GoogleMap googleMap) {
          Log.i(MapsActivity.class.getSimpleName(), "m 2 ");
          mMap = googleMap;
          locationRequest = new LocationRequest();
          locationRequest.setInterval(1000);
          locationRequest.setFastestInterval(1000);
          locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
          Log.i(MapsActivity.class.getSimpleName(), "m 2 ");

              Log.i(MapsActivity.class.getSimpleName(), "m 22 ");

              if (  checkLocationPermission()) {
                  Log.i(MapsActivity.class.getSimpleName(), "m 9 ");
                  mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                  mMap.setMyLocationEnabled(true);
                  Log.i(MapsActivity.class.getSimpleName(), "m 8 ");
//              }
              } else {
                  requestPermission();
                  Log.i(MapsActivity.class.getSimpleName(), "m 10 ");
              }


              Log.i(MapsActivity.class.getSimpleName(), "m 2` ");

      }


      //
//   public void redrawLine(){
//         mMap.clear();  //clears all Markers and Polylines
//
//        PolylineOptions options = new PolylineOptions().width(10).color(Color.RED).geodesic(true);
//        for (int i = 0; i < points.size(); i++) {
//            LatLng point = points.get(i);
//            options.add(point);
//            line = mMap.addPolyline(options);
//        }
//         //addMarker(); //add Marker in current position
//        line = mMap.addPolyline(options); //add Polyline
//    }
      public boolean checkLocationPermission() {
          Log.i(MapsActivity.class.getSimpleName(), "m 6 ");
          int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
          // int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
          int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);

          return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                  //  SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                  ThirdPermissionResult == PackageManager.PERMISSION_GRANTED; //&&
      }



      private void requestPermission() {
          Log.i(MapsActivity.class.getSimpleName(), "m 66 ");
          ActivityCompat.requestPermissions(MapsActivity.this, new String[]
                  {
                          ACCESS_FINE_LOCATION,
                          READ_PHONE_STATE,
                          ACCESS_COARSE_LOCATION,
                          INTERNET,
                          ACCESS_NETWORK_STATE
                  }, RequestPermissionCode);

      }



  }
