package com.oliviabecht.obechtwalkingtours;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.Circle;
import com.oliviabecht.obechtwalkingtours.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static GoogleMap mMap;
    private static FenceMgr fenceMgr;
    private static final int LOCATION_REQUEST = 111;
    private static final int BACKGROUND_LOCATION_REQUEST = 222;
    private static final List<PatternItem> pattern = Collections.singletonList(new Dot());
    private final ArrayList<LatLng> latLonHistory = new ArrayList<>();
    public static ArrayList<FenceData> mapsFenceList = new ArrayList<>();
    private Polyline llHistoryPolyline;
    private Marker carMarker;

    //ADD HERE
    private String userCurrentAddress;
    private ActivityMapsBinding binding;
    private Boolean showAdd = false;
    private Boolean showGeo = false;
    private Boolean showTravel = false;
    private Boolean showTour = false;

    //

    private static final float zoomLevel = 15f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       binding.activityMapsTxtShowTravel.setChecked(true);
       binding.activityMapsTxtShowGeo.setChecked(true);
        binding.activityMapsTxtShowAdd.setChecked(true);
        binding.activityMapsTxtShowTour.setChecked(true);

        binding.activityMapsTxtShowAdd.setOnClickListener(v -> {
            if (binding.activityMapsTxtShowAdd.isChecked() == true) {
                showAdd = true;
            } else {
                showAdd = false;
            }
            updateShowAddresses();
        });
        binding.activityMapsTxtShowGeo.setOnClickListener(v -> {
            if (binding.activityMapsTxtShowGeo.isChecked() == true) {
                showGeo = true;
            } else {
                showGeo = false;
            }
            updateShowGeo();
        });
        binding.activityMapsTxtShowTravel.setOnClickListener(v -> {
            if (binding.activityMapsTxtShowTravel.isChecked() == true) {
                showTravel = true;
            } else {
                showTravel = false;
            }
            updateShowTravel();
        });
        binding.activityMapsTxtShowTour.setOnClickListener(v -> {
            if (binding.activityMapsTxtShowTour.isChecked() == true) {
                showTour = true;
            } else {
                showTour = false;
            }
            updateShowTour();
        });

        initMap();
    }
//Outside of OnCreate____________________________________________________________________________________________________________________________

    //MENU STUFF________________________________________________________________________________________________________________________________
    private void updateShowTour() {
        if (showTour == true) {
//Not sure what tour means here? especially vs travel
        } else {

        }
    }

    private void updateShowTravel() {
        if (showTravel == true) {
            llHistoryPolyline.setVisible(true);
        } else {
            llHistoryPolyline.setVisible(false);
        }
    }

    private void updateShowGeo() {
        if (showGeo == true) {
//the circles
        } else {

        }
    }

    private void updateShowAddresses() {
        if (showAdd == true) {
//would this be current address txt line?
            binding.activityMapsTxtCurrAddress.setVisibility(View.VISIBLE);
        } else {
            binding.activityMapsTxtCurrAddress.setVisibility(View.INVISIBLE);
        }
    }
    //END OF MENU STUFF_______________________________________________________________________________________________________________________

//MAP STUFF________________________________________________________________________________________________________________________________________
    public void initMap() {

        fenceMgr = new FenceMgr(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));

        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);

        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        mMap.getUiSettings().setRotateGesturesEnabled(false);


        if (checkPermission()) {
            setupLocationListener();
            makeFences();
        }
    } //potentially breaks here

    private boolean checkPermission() {
        ArrayList<String> perms = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            perms.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                perms.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!perms.isEmpty()) {
            String[] array = perms.toArray(new String[0]);
            ActivityCompat.requestPermissions(this,
                    array, LOCATION_REQUEST);
            return false;
        }

        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setupLocationListener() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocListener(this);

        //minTime	    long: minimum time interval between location updates, in milliseconds
        //minDistance	float: minimum distance between location updates, in meters
        if (checkPermission() && locationManager != null)
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
    }

    private void makeFences() {
        fenceDownloader.downloadRoutes(this);
    }

    public static void acceptStops(ArrayList<FenceData> fenceList, MapsActivity mapsActivityIn) {

        mapsFenceList = fenceList;

        for (FenceData fence : fenceList) {
            addFence(fence, mapsActivityIn);
        }
    }

    private static void addFence(FenceData fd, Context context) {
        //String id = UUID.randomUUID().toString();
        //FenceData fd = new FenceData(id, latLng, radius);
        fenceMgr.addFence(fd);

        // Just to see the fence
        int line = Color.parseColor(fd.getBuildingFenceColor());
        int fill = ColorUtils.setAlphaComponent(line, 50);


        mMap.addCircle(new CircleOptions()
                .center(fd.getLatLng())
                .radius(fd.getRadius())
                .strokePattern(pattern)
                .strokeColor(line)
                .fillColor(fill));
    }


    public void updateLocation(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        latLonHistory.add(latLng); // Add the LL to our location history



        //userCurrentAddress = location.get
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            userCurrentAddress = addresses.get(0).getAddressLine(0);
            binding.activityMapsTxtCurrAddress.setText(userCurrentAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }



        if (llHistoryPolyline != null) {
            llHistoryPolyline.remove(); // Remove old polyline
        }

        if (latLonHistory.size() == 1) { // First update
            mMap.addMarker(new MarkerOptions().alpha(0.5f).position(latLng).title("My Origin"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            return;
        }



        if (latLonHistory.size() > 1) { // Second (or more) update
            PolylineOptions polylineOptions = new PolylineOptions();

            for (LatLng ll : latLonHistory) {
                polylineOptions.add(ll);
            }
            llHistoryPolyline = mMap.addPolyline(polylineOptions);
            llHistoryPolyline.setEndCap(new RoundCap());
            llHistoryPolyline.setWidth(8);
            llHistoryPolyline.setColor(Color.BLUE);

            float r = getRadius();
            if (r > 0) {
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.walker_left);
                Bitmap resized = Bitmap.createScaledBitmap(icon, (int) r, (int) r, false);

                BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromBitmap(resized);

                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.icon(iconBitmap);
                options.rotation(location.getBearing());
                options.anchor(0.5f,0.5f);

                if (carMarker != null) {
                    carMarker.remove();
                }

                carMarker = mMap.addMarker(options);
            }
        }
        Log.d(TAG, "updateLocation: " + mMap.getCameraPosition().zoom);

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        sumIt();
    }

    private void sumIt() {
        double sum = 0;
        LatLng last = latLonHistory.get(0);
        for (int i = 1; i < latLonHistory.size(); i++) {
            LatLng current = latLonHistory.get(i);
            sum += SphericalUtil.computeDistanceBetween(current, last);
            last = current;
        }
        Log.d(TAG, "sumIt: " + String.format("%.3f km", sum/1000.0));

    }
    private float getRadius() {
        float z = mMap.getCameraPosition().zoom;
        return 15f * z - 145f;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permNum = permissions.length + 1;
        int permCount = 0;

        if (requestCode == LOCATION_REQUEST) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
                permCount++;

            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getBackgroundLocPerm();
                permCount++;
            }

            if (permissions.length == 2) {
                if (permissions[1].equals(Manifest.permission.POST_NOTIFICATIONS) &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    permCount++;
                }
            }

            if (permCount == permNum) {
                setupLocationListener();
                makeFences();
            }

        }
        else if (requestCode == BACKGROUND_LOCATION_REQUEST) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setupLocationListener();
                makeFences();
            }
        }
    }


    private void getBackgroundLocPerm() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            return;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "NEED BASIC PERMS FIRST!", Toast.LENGTH_LONG).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    BACKGROUND_LOCATION_REQUEST);
        } else {
            Toast.makeText(this, "ALREADY HAS BACKGROUND LOC PERMS", Toast.LENGTH_LONG).show();
        }
    }
}