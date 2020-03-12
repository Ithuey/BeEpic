package com.beepic.Interest;

import android.widget.CheckBox;

public class Interest {

    private String Title;
    private int Thumbnail;
    private boolean isSelected;

    public Interest() {


    }

    public Interest(String title, int thumbnail, boolean isSelected) {
        Title = title;
        Thumbnail = thumbnail;
        this.isSelected = isSelected;
    }

    public String getTitle() {
        return Title;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "Interest{" +
                "Title='" + Title + '\'' +
                ", Thumbnail=" + Thumbnail +
                ", isSelected=" + isSelected +
                '}';
    }
}
