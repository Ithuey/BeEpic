package com.beepic.models;

public class UserAccountSettings {

    private String display_name;
    private long followers;
    private long interest;
    private String profile_photo;
    private String username;

    public UserAccountSettings(String display_name, long followers, long interest, String profile_photo, String username) {
        this.display_name = display_name;
        this.followers = followers;
        this.interest = interest;
        this.profile_photo = profile_photo;
        this.username = username;
    }

    public UserAccountSettings() {

    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getInterest() {
        return interest;
    }

    public void setInterest(long interest) {
        this.interest = interest;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "display_name='" + display_name + '\'' +
                ", followers=" + followers +
                ", interest=" + interest +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
