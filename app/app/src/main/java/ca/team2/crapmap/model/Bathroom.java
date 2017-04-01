package ca.team2.crapmap.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by geoffreycaven on 2017-03-15.
 */

public class Bathroom implements Serializable {
    private String id;
    private String name;
    private Boolean requiresPurchase;
    private transient LatLng location;
    //how to deal with images?
    //TODO deal with images
    private ArrayList<Review> reviews;
    private Hours[] hours = new Hours[7];

    public Bathroom(String id, String name, Boolean requiresPurchase, String lat, String lng, ArrayList<Review> reviews, Hours[] hours) {
        this.id = id;
        this.name = name;
        this.requiresPurchase = requiresPurchase;
        this.location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        this.reviews = reviews;
        this.hours = hours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequiresPurchase() {
        return requiresPurchase;
    }

    public void setRequiresPurchase(Boolean requiresPurchase) {
        this.requiresPurchase = requiresPurchase;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public Hours[] getHours() {
        return hours;
    }

    public void setHours(Hours[] hours) {
        this.hours = hours;
    }

    public Boolean isOpen() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        for (Hours hours : getHours()) {
            Log.i("hours", " " + hours);
        }
        Hours currHours = getHoursForDay(day);
        if (currHours != null)
            Log.i("currHours", currHours.toString());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        double combo = hour + (min / 100.0);
        Log.i("combo", "" + combo);
        return currHours != null && currHours.getOpen() <= combo && currHours.getClose() >= combo;
    }

    public Boolean hasAnyHours() {
        for (Hours dayHours : this.hours) {
            if (dayHours != null) {
                return true;
            }
        }
        return false;
    }

    public Hours getHoursForDay(int day) {
        for (Hours dayHours : this.hours) {
            if (dayHours != null && dayHours.getDay_of_week() == day) {
                return dayHours;
            }
        }
        return null;
    }

    public float[] getAvgRatings() {
        float clean = 0, avail = 0, access = 0;
        for (Review curr : reviews) {
            clean += curr.getCleanliness();
            avail += curr.getAvailability();
            access += curr.getAccessibility();
        }
        if (reviews.size() != 0) {
            clean /= reviews.size();
            avail /= reviews.size();
            access /= reviews.size();
        }
        float[] ret = {clean, avail, access};
        return ret;
    }


    @Override
    public String toString() {
        return "Bathroom{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", requiresPurchase=" + requiresPurchase +
                ", location=" + location +
                ", reviews=" + reviews.toString() +
                ", hours=" + hours.toString() +
                '}';
    }
}
