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

    public int getRatingId() {
        return ratingId;
    }

    public String getUsername() {
        return username;
    }

    public String getRemark() {
        return remark;
    }

    public int getRatingPoints() {
        return ratingPoints;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
