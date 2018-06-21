package com.fithub.codekienmee.fithub;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private final static int REQUEST_CODE = 1;
    private final static float DEFAULT_ZOOM = 25;

    private MapView mapView; // View that displays the map
    private GoogleMap gMap; // Google Maps
    private boolean userPermission;
    private FusedLocationProviderClient locationProviderClient; // Client that gets locations

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userPermission = false;
        this.requestUserPermission();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_maps_service, container, false);

        this.mapView = (MapView) view.findViewById(R.id.map_view);
        this.mapView.onCreate(savedInstanceState);

        this.mapView.getMapAsync(this);
        this.mapView.onResume();

        return view;
    }

    /**
     * Method that obtains the device's current location.
     */
    private void getCurrLocation() {
        this.locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Gets current device location, and if successful center the view on current location.
        try {
            if (this.userPermission) {
                this.locationProviderClient.getLastLocation().addOnCompleteListener
                        (new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location currLocation = (Location) task.getResult();
                            centerLocation(new LatLng(currLocation.getLatitude(),
                                    currLocation.getLongitude()), DEFAULT_ZOOM);
                        }
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Locations permissions not enabled!", Toast.LENGTH_SHORT);
            }
        } catch (SecurityException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG);
        }

    }

    /**
     * Method that centers the map at desired location.
     * @param latLng coordinates to center location at.
     * @param zoom zoom level of location.
     */
    private void centerLocation(LatLng latLng, float zoom) {
        this.gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Method that requests user's permission for application to use location services.
     */
    private void requestUserPermission() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if ((ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, MapsFragment.REQUEST_CODE);
        } else {
            this.userPermission = true;
        }
    }

    /**
     * Method that checks if permission result matches the correct request code,
     * and checks if appropriate permissions are applied.
     * @param requestCode Request Code of Application.
     * @param permissions Array of permissions to allow
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MapsFragment.REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (Integer i : grantResults)
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            this.userPermission = false;
                            return;
                        }
                    this.userPermission = true;
                }
            }
        }
    }

    /**
     * Method that configures Google Map settings on start.
     * @param googleMap Maps to configure
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        if (this.userPermission) {
            this.getCurrLocation();

            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            this.gMap.setMyLocationEnabled(true);
            this.gMap.getUiSettings().setAllGesturesEnabled(true);
//            this.gMap.getUiSettings().setMyLocationButtonEnabled(false); // TODO: Disable after implementing custom center Location Button.
        }
    }
}