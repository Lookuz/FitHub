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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private final static int REQUEST_CODE = 1;
    private final static float DEFAULT_ZOOM = 15;
    private final static Locale DEFAULT_LOCALE = Locale.ENGLISH;
    // Location bounds to apply search results.
    private final static LatLngBounds  LOCATION_BOUNDS= new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private MapView mapView; // View that displays the map
    private GoogleMap gMap; // Google Maps
    private GeoDataClient geoDataClient;
    private AutoCompleteTextView searchBar;
    private ImageButton centerUserLocation; // Button to center back on user's location.
    private ExecutorService executorService;
    private boolean userPermission;
    private FusedLocationProviderClient locationProviderClient; // Client that gets locations
//     Adapter that sets autocomplete features and filters for Google Locations.
//    private PlaceAutocompleteAdapter autocompleteAdapter;
    // HashMap that maps each marker to it's respective FitLocation data.
    private HashMap<FitLocation, Marker> locationHashMap;
    // List that stores all the known available locations.
    private List<FitLocation> locationList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userPermission = false;
        this.locationHashMap = new HashMap<>();
        this.locationList = new ArrayList<>(); // TODO: Initialize list with locations from DB.
        this.initMockLocations();
        this.executorService = Executors.newFixedThreadPool(5);
        this.executorService.submit(new Runnable() {
            @Override
            public void run() { // Initialize GoogleApiClient asynchronously.
                geoDataClient = Places.getGeoDataClient(getContext());
            }
        });

        this.requestUserPermission();
    }

    // For Mocking Only.
    private void initMockLocations() {
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "HeartBeat@Bedok ActiveSG Gym", "469662", "6443 5511",
                1.326975, 103.932149, "11 Bedok North Street 1",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Bishan ActiveSG Gym", "579783", "6353 9238",
                1.355272, 103.850811, "5 Bishan Street 14",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Bukit Gombak ActiveSG Gym", "659081", "6896 2197",
                1.359667, 103.752228, "810 Bukit Batok West Ave 5",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Choa Chu Kang ActiveSG Gym", "689236", "6767 1735",
                1.390985, 103.748676, "1 Choa Chu Kang Street 53",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Clementi Sports Centre", "129907", "6776 2560",
                1.310962, 103.765033, "518 Clementi Avenue 3",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Delta ActiveSG Gym", "158790", "6471 9030",
                1.289297, 103.820672, "900 Tiong Bahru Rd",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Enabling Village ActiveSG Gym", "159053", "6265 1292",
                1.287243, 103.814824, "20, Lengkok Bahru, #01-05, Enabling Village",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Hougang ActiveSG Sports Centre", "538832", "6315 8671",
                1.370726, 103.888364, " 93 Hougang Ave 4",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Jurong East ActiveSG Sports Centre", "609517", "6896 3569",
                1.346744, 103.729447, "21 Jurong East Street 31 ",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Jurong West ActiveSG Sports Centre", "648965", "6515 5331",
                1.338574, 103.694113, "20 Jurong West Street 93",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, " Pasir Ris ActiveSG Gym", "519640", "6583 2696",
                1.374088, 103.951925, "120 Pasir Ris Central",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Sengkang Sports Centre", "544964", "6315 3576",
                1.396745, 103.886448, "57 Anchorvale Rd",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Tampines ActiveSG Gym", "528523", "6260 1160",
                1.353908, 103.940558, "1 Tampines Walk, #07-31",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Toa Payoh ActiveSG Gym", "319392", "6256 7153",
                1.330533, 103.850158, "301 Lorong 6 Toa Payoh",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Woodlands ActiveSG Sports Centre", "738620", "6362 9100",
                1.434149, 103.779881, "2 Woodlands Street 12",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Yishun ActiveSG Gym", "769130", "6851 8604",
                1.411751, 103.831227, "101 Yishun Ave 1",
                "https://www.myactivesg.com"));
        this.locationList.add(new FitLocation(DEFAULT_LOCALE, "Yio Chu Kang ActiveSG Gym", "569770", "6482 4980",
                1.377487, 103.849504, "200 Ang Mo Kio Avenue 9",
                "https://www.myactivesg.com"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_maps_service,
                container, false);

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
        for (FitLocation location : this.locationList) {
            // Define settings for adding markers.
            Marker marker = this.gMap.addMarker(new MarkerOptions()
                    .position(location.getLocationCoordinates())
                    .title(location.getLocationName())
                    .zIndex(5)
                    .icon(this.bitmapDescriptorFromVector(getContext(),
                            R.drawable.ic_fithub_location_icon_monochrome))); // TODO: Fine tune icon for custom marker.
            // Map a marker to it's respective FitLocation.
            marker.setTag(location);
            this.locationHashMap.put(location, marker);
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
     * Method to initialize widgets for MapsFragment.
     * Widgets used: AutoCompleteTextView search bar, ImageButton centerUserLocation.
     */
    private void initWidgets(View view) {
        final SuggestionsAdapter suggestionsAdapter = new SuggestionsAdapter(getContext(),
                this.locationList);

        this.searchBar = (AutoCompleteTextView) view.findViewById(R.id.map_search_bar);
//        this.autocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), geoDataClient,
//                LOCATION_BOUNDS, null);
//        this.searchBar.setAdapter(this.autocompleteAdapter);
        this.searchBar.setAdapter(suggestionsAdapter);
        this.searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FitLocation location = suggestionsAdapter.getItem(position);
                if(location != null) {
                    // Hide soft keyboard
                    InputMethodManager manager = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getActivity().getWindow()
                            .getCurrentFocus().getWindowToken(), 0);
                    Marker marker = locationHashMap.get(location);
                    centerLocation(marker.getPosition(), 15);
                    marker.showInfoWindow();
                }
            }
        });
        this.searchBar.setHint(R.string.maps_hint);
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
            this.gMap.setInfoWindowAdapter(new FitInfoWindowAdapter(getContext()));
            this.displayLocations(); // Display available locations on map.
        }
    }

    /**
     * Method that converts vector to bitmap.
     */
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