package com.fithub.codekienmee.fithub;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Locale;

/**
 * Class that encapsulates data for a fitness center identified by FitHub.
 */
public class FitLocation implements Serializable {

    private String locationAddress;
    private double latitude;
    private double longitude;
    private String locationName;
    private String postalCode;
    private String phoneNumber;
    private String websiteURL;

    public FitLocation() {
        // Default Constructor.
    }

    public FitLocation(String name, String postalCode, String phoneNumber,
                       double latitude, double longitude, String address, String website) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = name;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.websiteURL = website;
        this.locationAddress = address;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public LatLng getLocationCoordinates() {
        return new LatLng(this.latitude, this.longitude);
    }

    public String getLocationAddress() {
        return this.locationAddress;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(this.postalCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FitLocation)) {
            return false;
        } else {
            return (((FitLocation) obj).hashCode() == this.hashCode())? true : false;
        }
    }
}