package ca.team2.crapmap.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Calendar;

import ca.team2.crapmap.R;
import ca.team2.crapmap.model.Bathroom;
import ca.team2.crapmap.model.Hours;

/**
 * Created by lockhart on 2017-04-01.
 */

public class BathroomInfoAdapter implements GoogleMap.InfoWindowAdapter {

    public Activity activity;

    public BathroomInfoAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = activity.getLayoutInflater().inflate(R.layout.info_window_bathroom, null);
        Bathroom curr = (Bathroom) marker.getTag();
        if (curr == null) {
            return null;
        }
        TextView name_preview = (TextView) v.findViewById(R.id.bathroom_name_preview);
        RatingBar cleanliness_preview = (RatingBar) v.findViewById(R.id.cleanliness_preview);
        RatingBar accessibility_preview = (RatingBar) v.findViewById(R.id.accessibility_preview);
        RatingBar availability_preview = (RatingBar) v.findViewById(R.id.availability_preview);
        TextView number_reviews = (TextView) v.findViewById(R.id.number_reviews);
        TextView hours_preview = (TextView) v.findViewById(R.id.open_status_preview);
        TextView req_purchase = (TextView) v.findViewById(R.id.requires_purchase_preview);
        name_preview.setText(marker.getTitle());
        if (curr.getReviews().size() == 0) {
            number_reviews.setText("No Reviews");
        } else {
            String reviewNum = curr.getReviews().size() == 1 ? "1 Review" : curr.getReviews().size() + " Reviews";
            number_reviews.setText(reviewNum);
            float[] ratings = curr.getAvgRatings();
            cleanliness_preview.setRating(ratings[0]);
            accessibility_preview.setRating(ratings[1]);
            availability_preview.setRating(ratings[2]);
        }
        if (curr.getRequiresPurchase()) {
            req_purchase.setText(activity.getResources().getString(R.string.label_requires_purchase));
        } else {
            req_purchase.setHeight(0);
        }

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        for (Hours hours : curr.getHours()) {
            Log.i("hours", " " + hours);
        }
        Hours currHours = curr.getHoursForDay(day);

        if (currHours != null) {
            Log.i("currHours", currHours.toString());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            double combo = hour + (min / 100);
            Log.i("combo", "" + combo);
            if (currHours.getOpen() <= combo && currHours.getClose() >= combo) {
                hours_preview.setText(activity.getResources().getString(R.string.bathroom_open));
                hours_preview.setTextColor(activity.getResources().getColor(R.color.colorGreen));
            } else {
                hours_preview.setText(activity.getResources().getString(R.string.bathroom_closed));
                hours_preview.setTextColor(activity.getResources().getColor(R.color.colorRed));
            }
        } else if (curr.hasAnyHours()) {
            hours_preview.setText(activity.getResources().getString(R.string.bathroom_closed));
            hours_preview.setTextColor(activity.getResources().getColor(R.color.colorRed));
        } else {
            hours_preview.setHeight(0);
        }

        return v;
    }
}
