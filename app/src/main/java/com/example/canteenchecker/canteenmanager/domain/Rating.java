package com.example.canteenchecker.canteenmanager.domain;

public class Rating {

    public Rating(int ratingId, String username, String remark, int ratingPoints, long timestamp) {
        this.ratingId = ratingId;
        this.username = username;
        this.remark = remark;
        this.ratingPoints = ratingPoints;
        this.timestamp = timestamp;
    }

    int ratingId;
    String username;
    String remark;
    int ratingPoints;
    long timestamp;
}
