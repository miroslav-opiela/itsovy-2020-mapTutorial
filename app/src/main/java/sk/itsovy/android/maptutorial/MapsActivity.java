package sk.itsovy.android.maptutorial;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location location;

    private static final int PERMISSION_CODE = 33;
    private static final String PERM = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // kontrolovat povolenie
                if (checkPermissions()) {
                    getLastLocation();
                } else {
                    requestPermissions();
                }

            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{PERM}, PERMISSION_CODE);
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, PERM) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // ci ide o povolenie na location
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ok mam povolenie
                getLastLocation();
            } else {
                // nedostal som povolenie

                Snackbar.make(findViewById(android.R.id.content), "permissions denied",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Go to settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();

            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    location = task.getResult();
                    updateMap();
                    Log.d("MAP", location.toString());
                } else {
                    Log.e("MAP", "could not load location ", task.getException());
                }
            }
        });
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
        map = googleMap;

        updateMap();
    }

    /**
     * na zaklade location nastavi mapu
     */
    private void updateMap() {
        if (map == null) {
            return;
        }
        if (location == null) {
            LatLng sydney = new LatLng(-34, 151);
            drawOnMap(sydney, "Default marker in Sydney", 5);
        } else {
            map.clear();
            drawOnMap(new LatLng(location.getLatitude(), location.getLongitude()),
                    "My location", 15);
        }
    }

    /**
     * vykresli marker a nastavi mapu
     */
    private void drawOnMap(LatLng position, String marker, int zoom) {
        map.addMarker(new MarkerOptions().position(position).title(marker));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
    }
}