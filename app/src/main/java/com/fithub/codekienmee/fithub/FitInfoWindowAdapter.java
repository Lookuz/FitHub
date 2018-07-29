package com.fithub.codekienmee.fithub;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * Custom InfoWindowAdapter for display FitLocation on Maps.
 */
public class FitInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View infoWindow; // View to be displayed on infoWindow.
    private Context context;
    private FitUser user;

    public FitInfoWindowAdapter(Context context, FitUser user) {
        this.infoWindow = LayoutInflater.from(context).inflate(R.layout.maps_info_window,null);
        this.user = user;
        this.context = context;
    }

    /**
     * Method that initializes the contents of the info window to be displayed.
     */
    private View initInfoWindow(Marker marker, View view) {

        final FitLocation location = (FitLocation) marker.getTag();

        ((TextView) view.findViewById(R.id.info_window_title)).setText(location.getLocationName());
        ((TextView) view.findViewById(R.id.info_window_address))
                .setText(location.getLocationAddress());
        ((TextView) view.findViewById(R.id.info_window_phone)).setText(location.getPhoneNumber());
        ((TextView) view.findViewById(R.id.info_window_website)).setText(location.getWebsiteURL());
        ProgressBar crowdLevel = view.findViewById(R.id.info_window_crowd_bar);
        crowdLevel.setMax(location.getMaxCrowd());
        crowdLevel.setProgress(location.getCurrCrowd());

        return this.infoWindow;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return initInfoWindow(marker, this.infoWindow);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return this.infoWindow;
    }
}
