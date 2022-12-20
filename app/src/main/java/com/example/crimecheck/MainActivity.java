package com.example.crimecheck;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import java.util.List;


public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double DESIRED_ACCURACY = 0;
    private static final long MINIMAL_TIME = 2000;
    private static final double MINIMAL_DISTANCE = 1;
    private static final boolean USE_IN_BACKGROUND = false;
    private static final int COMFORTABLE_ZOOM_LEVEL = 14;
    private final String MAPKIT_API_KEY = "28e6d937-fb53-497e-9441-4c3d487b4a06";
    public static MapView mapView = null;
    private UserLocationLayer userLocation;
    private CoordinatorLayout rootCoordinatorLayout;
    private LocationManager locationManager;
    private LocationListener myLocationListener;
    private Point myLocation;
    private String sLocation;
    private ImageButton plusCrime;
    private Intent intent;
    private ImageButton refreshMarkersBtn;
    public static int CurrentId;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        requestLocationPermission();

        refreshMarkersBtn = findViewById(R.id.UpdateMarkers);

        mapView = findViewById(R.id.Map);
        plusCrime = findViewById(R.id.PlusCrime);
        InitListners();
        MapKit mapkit = MapKitFactory.getInstance();
        locationManager = mapkit.createLocationManager();
        userLocation = mapkit.createUserLocationLayer(mapView.getMapWindow());
        userLocation.setVisible(true);
        userLocation.setObjectListener(this);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void InitListners() {

        plusCrime.setOnClickListener(v -> {
            intent = new Intent(MainActivity.this, AddCrime.class);
            intent.putExtra("location", sLocation);

            startActivity(intent);
        });
        refreshMarkersBtn.setOnClickListener(v ->{
            mapView.getMap().getMapObjects().clear();
            List<Profile> dataSql = null;
            ProfileList myData = new ProfileList();
            dataSql = myData.getList();

            for (int i = 0; i < dataSql.size(); i++) {
                String[] cutted = dataSql.get(i).location.split(",");
                Point point;
                PlacemarkMapObject marker;
                try {
                    int dayofmonth = java.time.LocalDateTime.now().getDayOfMonth();
                    int sqlday = dataSql.get(i).dateTime.getDate();
                    int differense = dayofmonth - sqlday;
                    if(differense <= 1){
                        point = new Point(Double.valueOf(cutted[0]), Double.valueOf(cutted[1]));
                        marker = mapView.getMap().getMapObjects().addPlacemark(point,
                                ImageProvider.fromResource(this,R.drawable.locationmarker));

                        List<Profile> finalDataSql = dataSql;
                        marker.addTapListener((MapObjectTapListener, point1)-> {
                            for (int j = 0; j < finalDataSql.size(); j++){
                                Point point2;
                                try {
                                    String[] loc = finalDataSql.get(j).location.toString().split(",");

                                    point2 = new Point(Double.valueOf(loc[0]), Double.valueOf(loc[1]));
                                }
                                catch (Exception ex){
                                    continue;
                                }
                                if(point2.getLongitude() - point1.getLongitude() <= 0.003 && point2.getLatitude() - point1.getLatitude() <= 0.003){
                                    CurrentId = finalDataSql.get(j).ID;
                                    break;
                                }
                            }
                            intent = new Intent(this, checkCrime.class);
                            startActivity(intent);
                            return true;
                        });
                    }
                    else{
                        dataSql.get(i).location = null;
                    }
                }
                catch (NumberFormatException ex){
                    continue;
                }


            }
        });
        myLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (myLocation == null) {
                    moveCamera(location.getPosition(),COMFORTABLE_ZOOM_LEVEL);
                }
                myLocation = location.getPosition(); //this user point
                sLocation = myLocation.getLatitude() + "," + myLocation.getLongitude();
                Log.w(TAG, "my location - " + myLocation.getLatitude() + "," + myLocation.getLongitude());
            }

            @Override
            public void onLocationStatusUpdated(LocationStatus locationStatus) {
                if (locationStatus == LocationStatus.NOT_AVAILABLE) {
                    Log.w("Error: ", "LOCATION NOT FOUND");
                }
                if (locationStatus == LocationStatus.AVAILABLE){
                    Log.w("OK: ", "LOCATION FOUND");
                }
            }
        };


    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[2];
            permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            permissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        subscribeToLocationUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        locationManager.unsubscribe(myLocationListener);
        mapView.onStop();
    }

    private void subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, myLocationListener);
        }
        else{
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, myLocationListener);
        }
    }

    private void moveCamera(Point point, float zoom) {
        mapView.getMap().move(
                new CameraPosition(point, zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 100),
                null);
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocation.setAnchor(
                new PointF((float) (mapView.width() * 0.5), (float) (mapView.height() * 0.5)),
                new PointF((float) (mapView.width() * 0.5), (float) (mapView.height() * 0.83))
        );
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }
}