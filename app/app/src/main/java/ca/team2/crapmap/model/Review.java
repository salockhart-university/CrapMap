package ca.team2.crapmap.model;

import java.io.Serializable;

/**
 * Created by geoffreycaven on 2017-03-15.
 */

public class Review implements Serializable {
    private User user;
    private int cleanliness;
    private int accessibility;
    private int availability;

    private String review;

    public Review(int cleanliness, int accessibility, int availability, String review, User user) {
        this.user = user;
        this.cleanliness = cleanliness;
        this.accessibility = accessibility;
        this.availability = availability;
        this.review = review;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCleanliness() { return cleanliness; }

    public void setCleanliness(int cleanliness) { this.cleanliness = cleanliness; }

    public int getAvailability() { return availability; }

    public void setAvailability(int availability) { this.availability = availability; }

    public int getAccessibility() { return accessibility; }

    public void setAccessibility(int accessibility) { this.accessibility = accessibility; }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "Review{" +
                "user=" + user +
                ", cleanliness=" + cleanliness +
                ", accessibility=" + accessibility +
                ", availability=" + availability +
                ", review='" + review + '\'' +
                '}';
    }
}
