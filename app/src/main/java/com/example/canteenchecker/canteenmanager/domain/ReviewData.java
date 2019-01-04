package com.example.canteenchecker.canteenmanager.domain;

public class ReviewData {

    private final float averageRating;
    private final int totalRatings;
    private final int ratingsOne;
    private final int ratingsTwo;
    private final int ratingsThree;
    private final int ratingsFour;
    private final int ratingsFive;
    private final int totalRatingsOfMostCommonGrade;
    private final int[] countsPerGrade;

    public ReviewData(float averageRating, int totalRatings, int ratingsOne, int ratingsTwo, int ratingsThree, int ratingsFour, int ratingsFive, int[] countsPerGrade) {
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
        this.ratingsOne = ratingsOne;
        this.ratingsTwo = ratingsTwo;
        this.ratingsThree = ratingsThree;
        this.ratingsFour = ratingsFour;
        this.ratingsFive = ratingsFive;
        this.countsPerGrade = countsPerGrade;

        totalRatingsOfMostCommonGrade = Math.max(Math.max(Math.max(Math.max(ratingsOne, ratingsTwo), ratingsThree), ratingsFour), ratingsFive);
    }

    public float getAverageRating() {
        return averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public int getRatingsOne() {
        return ratingsOne;
    }

    public int getRatingsTwo() {
        return ratingsTwo;
    }

    public int getRatingsThree() {
        return ratingsThree;
    }

    public int getRatingsFour() {
        return ratingsFour;
    }

    public int getRatingsFive() {
        return ratingsFive;
    }

    public int getTotalRatingsOfMostCommonGrade() {
        return totalRatingsOfMostCommonGrade;
    }

    public int[] getCountsPerGrade() { return countsPerGrade; }

}
