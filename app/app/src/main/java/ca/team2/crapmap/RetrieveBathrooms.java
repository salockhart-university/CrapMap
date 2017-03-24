package ca.team2.crapmap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by geoffreycaven on 2017-03-15.
 */

//TODO make this more general so can be reused for all request types

public class RetrieveBathrooms extends AsyncTask {

    private MapsActivity activity;
    private String surl;
    private RequestType requestType;
    private ProgressDialog pDialog;
    private InputStream is;
    private JSONArray jArr;
    private String json;
    private ArrayList<Bathroom> results;

    public RetrieveBathrooms(String url, RequestType requestType, MapsActivity activity) {
        this.activity = activity;
        this.requestType = requestType;
        this.surl = url;
    }

    @Override
    protected void onPreExecute() {
        Log.i("retrieveBathrooms", "preExecuting");
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Finding bathrooms near you...");
        pDialog.show();
        pDialog.setCancelable(false);
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
           URL url = new URL(this.surl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(requestType.toString());
            connection.setDoInput(true);
            connection.connect();
            is = connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Async Exception", "" + e);
            return null;
        }

        return parseJSON();
    }

    private ArrayList<Bathroom> parseJSON() {
        ArrayList<Bathroom> resultsList = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            jArr = new JSONArray(json);

            for (int i = 0; i < jArr.length(); i++) {
                JSONObject curr = jArr.getJSONObject(i);
                JSONArray reviewArr = curr.getJSONArray("reviews");
                JSONArray hoursArr = curr.getJSONArray("hours");
                ArrayList<Review> reviewList = new ArrayList<Review>();
                Hours[] hours = new Hours[7];

                for (int j = 0; j < reviewArr.length(); j++) {
                    JSONObject review = reviewArr.getJSONObject(j);
                    JSONObject stars = review.getJSONObject("stars");
                    reviewList.add(
                            new Review(
                                    stars.getInt("cleanliness"),
                                    stars.getInt("accessibility"),
                                    stars.getInt("availability"),
                                    review.getString("review")
                            )
                    );
                }

                for (int j = 0; j < hoursArr.length(); j++) {
                    JSONObject hoursForDay = hoursArr.getJSONObject(j);
                    String day = hoursForDay.getString("day");
                    Hours hoursObj;
                    try {
                        double open = hoursForDay.getDouble("open");
                        double close = hoursForDay.getDouble("close");
                        hoursObj = new Hours(day, open/100, close/100);
                    } catch (Exception e) {
                        hoursObj = new Hours(day);
                    }
                    hours[j] = hoursObj;
                }

                resultsList.add(
                        new Bathroom(
                                curr.getString("_id"),
                                curr.getString("name"),
                                curr.getBoolean("requiresPurchase"),
                                curr.getJSONObject("location").getString("lat"),
                                curr.getJSONObject("location").getString("long"),
                                reviewList,
                                hours
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Async Exception", "" + e);
            return null;
        }

        Log.i("bathrooms", resultsList.toString());
        return resultsList;
    }

    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        pDialog.dismiss();
        activity.bathroomCallback(result);
    }



}
