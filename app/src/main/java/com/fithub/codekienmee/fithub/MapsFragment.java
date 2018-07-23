package com.fithub.codekienmee.fithub;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private ImageButton favouriteLocation;
    private ExecutorService executorService;
    private boolean userPermission;
    private FusedLocationProviderClient locationProviderClient; // Client that gets locations
//     Adapter that sets autocomplete features and filters for Google Locations.
//    private PlaceAutocompleteAdapter autocompleteAdapter;
    // HashMap that maps each marker to it's respective FitLocation data.
    private HashMap<FitLocation, Marker> locationHashMap;
    // List that stores all the known available locations.
    private List<FitLocation> locationList;
    private DatabaseReference locationsDB;
    private FitUser user;
    private SuggestionsAdapter suggestionsAdapter;

    private class SyncLocations extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            // Read locations from DB.
            locationsDB = FirebaseDatabase.getInstance().getReference("FitLocations");
            locationsDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<FitLocation> locationList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        FitLocation location = ds.getValue(FitLocation.class);
                        locationList.add(location);
                        suggestionsAdapter.add(location);
                        Log.d("LocationList: ", locationList.size() + " elements");
                        Log.d("SuggestionsList: ", suggestionsAdapter.getCount() + " elements");
                    }
                    MapsFragment.this.locationList = locationList;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (suggestionsAdapter != null) {
                suggestionsAdapter.notifyDataSetChanged();
            }

            suggestionsAdapter.setNotifyOnChange(true);
            searchBar.setAdapter(suggestionsAdapter);
            Log.d("SuggestionsAdapter: ", "Set");
            searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        centerLocation(marker.getPosition(), 12);
                        marker.showInfoWindow();
                    }
                }
            });
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userPermission = false;
        this.user = ((MainPageActivity) getActivity()).getUser();
        this.locationHashMap = new HashMap<>();
        this.locationList = new ArrayList<>();
        this.suggestionsAdapter = new SuggestionsAdapter(getContext(),
                this.locationList);

        new SyncLocations().execute();

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
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_maps_service,
                container, false);

        this.mapView = view.findViewById(R.id.map_view);
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
                            R.drawable.ic_fithub_location_icon_monochrome)));
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
        this.searchBar = view.findViewById(R.id.map_search_bar);
        this.suggestionsAdapter.setNotifyOnChange(true);

        this.searchBar.setHint(R.string.maps_hint);
        this.centerUserLocation = view.findViewById(R.id.map_center_button);
        this.favouriteLocation = view.findViewById(R.id.map_favourites_button);

        this.centerUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrLocation();
            }
        });
        this.favouriteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: show list of favourite locations.
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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