package ca.team2.crapmap.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.team2.crapmap.model.Bathroom;
import ca.team2.crapmap.model.Hours;
import ca.team2.crapmap.model.Review;
import ca.team2.crapmap.model.User;

/**
 * Created by lockhart on 2017-04-01.
 */

public class JSONParser {

    static ArrayList<Bathroom> parseBathroomList(JSONArray bathrooms) throws JSONException {
        ArrayList<Bathroom> resultsList = new ArrayList<>();

        for (int i = 0; i < bathrooms.length(); i++) {
            JSONObject curr = bathrooms.getJSONObject(i);
            resultsList.add(parseBathroom(curr));
        }

        Log.i("bathrooms", resultsList.toString());
        return resultsList;
    }

    static Bathroom parseBathroom(JSONObject bathroom) throws JSONException {

        JSONArray reviewArr = bathroom.getJSONArray("reviews");
        JSONArray hoursArr = bathroom.getJSONArray("hours");

        return new Bathroom(
                bathroom.getString("_id"),
                bathroom.getString("name"),
                bathroom.getBoolean("requiresPurchase"),
                bathroom.getJSONObject("location").getString("lat"),
                bathroom.getJSONObject("location").getString("long"),
                parseReviewList(reviewArr),
                parseHoursList(hoursArr)
        );
    }

    static Hours[] parseHoursList(JSONArray hoursArr) throws JSONException {
        Hours[] hours = new Hours[7];
        for (int j = 0; j < hoursArr.length(); j++) {
            JSONObject hoursForDay = hoursArr.getJSONObject(j);
            hours[j] = parseHours(hoursForDay);
        }
        return hours;
    }

    static Hours parseHours(JSONObject hours) throws JSONException {
        String day = hours.getString("day");
        Hours hoursObj;
        try {
            double open = hours.getDouble("open");
            double close = hours.getDouble("close");
            hoursObj = new Hours(day, open / 100, close / 100);
        } catch (Exception e) {
            hoursObj = new Hours(day);
        }
        return hoursObj;
    }

    static ArrayList<Review> parseReviewList(JSONArray reviewArr) throws JSONException {
        ArrayList<Review> reviewList = new ArrayList<Review>();

        for (int j = 0; j < reviewArr.length(); j++) {
            JSONObject review = reviewArr.getJSONObject(j);
            reviewList.add(parseReview(review));
        }
        return reviewList;
    }

    static Review parseReview(JSONObject review) throws JSONException {
        JSONObject stars = review.getJSONObject("stars");
        User user = null;
        if (!review.isNull("user")) {
            JSONObject userObj = review.getJSONObject("user");
            user = new User(userObj);
        }
        return new Review(
                stars.getInt("cleanliness"),
                stars.getInt("accessibility"),
                stars.getInt("availability"),
                review.getString("review"),
                user
        );
    }

    static User parseUser(JSONObject user) throws JSONException {
        return new User(user);
    }
}
