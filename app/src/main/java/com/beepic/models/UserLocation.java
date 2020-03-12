package com.beepic.models;

import java.util.ArrayList;

public class UserLocation {

    private ArrayList userLocations;

    private String userLocationString;

    public UserLocation(ArrayList userLocations, String userLocationString) {
        this.userLocations = userLocations;
        this.userLocationString = userLocationString;
    }

    public UserLocation() {

    }

    public ArrayList getUserLocations() {
        return userLocations;
    }

    public void setUserLocations(ArrayList userLocations) {
        this.userLocations = userLocations;
    }

    public String getUserLocationString() {
        return userLocationString;
    }

    public void setUserLocationString(String userLocationString) {
        this.userLocationString = userLocationString;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "userLocations=" + userLocations +
                ", userLocationString='" + userLocationString + '\'' +
                '}';
    }
}

