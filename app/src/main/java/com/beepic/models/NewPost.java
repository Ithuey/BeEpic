package com.beepic.models;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public class NewPost {

    private String caption;
    private String date_created;
    private String image_path;
    private String post_id;
    private String user_id;
    private String interest;

    public NewPost(String caption, String date_created, String image_path, String post_id, String user_id, String interest) {
        this.caption = caption;
        this.date_created = date_created;
        this.image_path = image_path;
        this.post_id = post_id;
        this.user_id = user_id;
        this.interest = interest;
    }

    public NewPost() {

    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }


    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return post_id;
    }

    public void setPhoto_id(String photo_id) {
        this.post_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    @Override
    public String toString() {
        return "NewPost{" +
                "caption='" + caption + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", photo_id='" + post_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", interest='" + interest + '\'' +
                '}';
    }
}


