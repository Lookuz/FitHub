package com.fithub.codekienmee.fithub;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private final static int REQUEST_CODE = 1;

    private MapView mapView;
    private GoogleMap gMap;
    private boolean userPermission;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userPermission = false;
        this.requestUserPermission();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater
                .inflate(R.layout.fragment_maps_service, container, false);

        this.mapView = (MapView) view.findViewById(R.id.map_view);
        this.mapView.onCreate(savedInstanceState);

        this.mapView.getMapAsync(this);
        this.mapView.onResume();

        return view;
    }

    private void requestUserPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if ((ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, MapsFragment.REQUEST_CODE);
            } else {
            this.userPermission = true;
        }
    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;
        this.gMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.gMap.getUiSettings().setZoomGesturesEnabled(true);
        this.gMap.getUiSettings().setCompassEnabled(true);
    }
}
