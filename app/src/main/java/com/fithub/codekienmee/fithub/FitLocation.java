package com.fithub.codekienmee.fithub;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

/**
 * Class that encapsulates data for a fitness center identified by FitHub.
 */
public class FitLocation extends Address {

    private final static String LOCAL_COUNTRY_NAME = "Singapore";
    private final static String LOCAL_COUNTRY_CODE = "+65";

    private String locationAddress;

    public FitLocation(Locale locale, String name, String postalCode, String phoneNumber,
                       double latitude, double longitude, String address, String website) {
        super(locale);
        super.setLatitude(latitude);
        super.setLongitude(longitude);
        super.setFeatureName(name);
        super.setPostalCode(postalCode);
        super.setPhone(phoneNumber);
        super.setUrl(website);
        super.setCountryCode(FitLocation.LOCAL_COUNTRY_CODE);
        super.setCountryName(FitLocation.LOCAL_COUNTRY_NAME);
        this.locationAddress = address;
    }

    public static String getLocalCountryName() {
        return LOCAL_COUNTRY_NAME;
    }

    public static String getLocalCountryCode() {
        return LOCAL_COUNTRY_CODE;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getLocationName() {
        return this.getFeatureName();
    }

    public LatLng getLocationCoordinates() {
        return new LatLng(this.getLatitude(), this.getLongitude());
    }

    public String getLocationAddress() {
        return this.locationAddress.concat("\n" + "Singapore " + this.getPostalCode());
    }

    public String getPhoneNumber() {
        return this.getPhone();
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(this.getPostalCode());
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
