package com.fithub.codekienmee.fithub;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private final static int REQUEST_CODE = 1;
    private final static float DEFAULT_ZOOM = 15;
    private final static Locale DEFAULT_LOCALE = Locale.ENGLISH;
    // Location bounds to apply search results.
    // TODO: Restrict results to Singapore.
    private final static LatLngBounds  LOCATION_BOUNDS= new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private final static FitLocation MOCK_LOCATION =
            new FitLocation(DEFAULT_LOCALE, "BestGym", "592775", "84845566",
                    1.333501, 103.788373, "925 Bukit Timah Road");

    private MapView mapView; // View that displays the map
    private GoogleMap gMap; // Google Maps
    private GeoDataClient geoDataClient;
    private AutoCompleteTextView searchBar;
    private ImageButton centerUserLocation; // Button to center back on user's location.
    private ExecutorService executorService;
    private boolean userPermission;
    private FusedLocationProviderClient locationProviderClient; // Client that gets locations
    // Adapter that sets autocomplete features and filters for Google Locations.
    private PlaceAutocompleteAdapter autocompleteAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userPermission = false;
        this.executorService = Executors.newFixedThreadPool(5);
        this.executorService.submit(new Runnable() {
            @Override
            public void run() { // Initialize GoogleApiClient asynchronously.
                geoDataClient = Places.getGeoDataClient(getContext());
            }
        });
        this.requestUserPermission();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_maps_service, container, false);

        this.mapView = (MapView) view.findViewById(R.id.map_view);
        this.mapView.onCreate(savedInstanceState);
        this.mapView.getMapAsync(this); // Prepare map functions.
        this.initWidgets(view); // Initialize widgets on map.
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
            }
        } catch (SecurityException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    /**
     * Method that displays markers of all allowed fitness centre locations.
     */
    private void displayLocations() {
        this.gMap.addMarker(new MarkerOptions()
                .position(MapsFragment.MOCK_LOCATION.getLocationCoordinates())
                .title(MOCK_LOCATION.getLocationName())
                .icon(this.bitmapDescriptorFromVector(getContext(),
                        R.drawable.ic_fithub_location_icon_monochrome))); // TODO: Finetune icon for custom marker.
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
     * Method to initialize widgets for MapsFragment.
     * Widgets used: AutoCompleteTextView search bar, ImageButton centerUserLocation.
     */
    private void initWidgets(View view) {
        this.searchBar = (AutoCompleteTextView) view.findViewById(R.id.map_search_bar);
        this.autocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), geoDataClient,
                LOCATION_BOUNDS, null);
        this.searchBar.setAdapter(this.autocompleteAdapter);
        this.searchBar.setHint("Can't find what you're looking for?");
        this.centerUserLocation = (ImageButton) view.findViewById(R.id.map_center_button);
        this.centerUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrLocation(); // TODO: Animate centering back on device location.
            }
        });
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
            this.gMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.displayLocations(); // Display available locations on map.
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        Bitmap bitmap = Bitmap.createBitmap(168,
                168, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}