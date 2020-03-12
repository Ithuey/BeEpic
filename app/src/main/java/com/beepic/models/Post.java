package com.beepic.models;

public class Post {


    private String interest;

    public Post(String interest) {
        this.interest = interest;
    }

    public Post() {

    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    @Override
    public String toString() {
        return "Post{" +
                "interest='" + interest + '\'' +
                '}';
    }
}
