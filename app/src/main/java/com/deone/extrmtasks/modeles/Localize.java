package com.deone.extrmtasks.modeles;

import androidx.annotation.NonNull;

public class Localize {
    private String codepostal;
    private String city;
    private String state;
    private String country;
    private String address;
    private String latitude;
    private String longitude;

    public Localize() {
    }

    public Localize(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Localize(String codepostal, String city, String state,
                    String country, String address, String latitude, String longitude) {
        this.codepostal = codepostal;
        this.city = city;
        this.state = state;
        this.country = country;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCodepostal() {
        return codepostal;
    }

    public void setCodepostal(String codepostal) {
        this.codepostal = codepostal;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nCp = "+this.codepostal+
                "\nCity = "+this.city+
                "\nState = "+this.state+
                "\nCountry = "+this.country+
                "\nAddress = "+this.address;
    }
}
