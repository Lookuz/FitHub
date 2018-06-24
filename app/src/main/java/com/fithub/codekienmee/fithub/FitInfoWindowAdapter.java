package com.fithub.codekienmee.fithub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

import java.util.HashMap;

public class FitInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View infoWindow; // View to be displayed on infoWindow.
    // HashMap that maps a marker to it's respective FitLocation data.
    private HashMap<Marker, FitLocation> locationHashMap;

    public FitInfoWindowAdapter(Context context, HashMap<Marker, FitLocation> locationHashMap) {
        this.infoWindow = LayoutInflater.from(context).inflate(R.layout.maps_info_window,null);
        this.locationHashMap = locationHashMap;
    }

    /**
     * Method that initializes the contents of the info window to be displayed.
     */
    private View initInfoWindow(Marker marker, View view) {

        FitLocation location = this.locationHashMap.get(marker);

        ((TextView) view.findViewById(R.id.info_window_title)).setText(location.getLocationName());
        ((TextView) view.findViewById(R.id.info_window_address))
                .setText(location.getLocationAddress());
        ((TextView) view.findViewById(R.id.info_window_phone)).setText(location.getPhoneNumber());
        ((TextView) view.findViewById(R.id.info_window_website)).setText(location.getUrl());
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
