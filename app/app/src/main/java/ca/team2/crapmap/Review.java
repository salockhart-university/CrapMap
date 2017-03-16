package ca.team2.crapmap;

/**
 * Created by geoffreycaven on 2017-03-15.
 */

public class Review {
    private User user;
    private int stars;
    private String review;

    public Review(int start, String review) {
        this.user = null;
        this.stars = stars;
        this.review = review;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

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
                ", stars=" + stars +
                ", review='" + review + '\'' +
                '}';
    }
}
