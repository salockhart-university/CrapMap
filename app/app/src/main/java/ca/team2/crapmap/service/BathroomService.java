package ca.team2.crapmap.service;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import ca.team2.crapmap.util.RequestType;

/**
 * Created by lockhart on 2017-04-01.
 */

public class BathroomService {

    private static final String BASE_URL = "https://crap-map-server.herokuapp.com/";
    private static final String BASE_BATHROOMS = BASE_URL + "bathroom/";
    private static final String GET_BATHROOMS = BASE_BATHROOMS + "?lat=%f&long=%f&radius=%d";

    public static void getBathrooms(Activity activity, String dialogText, LatLng currentLocation, int radius, final RequestHandler handler) {
        try {
            URL url = new URL(String.format(GET_BATHROOMS, currentLocation.latitude, currentLocation.longitude, radius));
            Request request = new Request(RequestType.GET, url, new RequestHandler() {
                @Override
                public void callback(Object result) {
                    try {
                        handler.callback(JSONParser.parseBathroomList(new JSONArray((String)result)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.callback(null);
                    }
                }
            });
            request.setProgressDialog(activity, dialogText);
            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addBathroom(String name, double latitude, double longitude, boolean requiresPurchase, String[] hours, final RequestHandler handler) {
        try {
            URL url = new URL(BASE_BATHROOMS);

            JSONObject body = new JSONObject();
            JSONObject location = new JSONObject();
            JSONArray jsonTimes = new JSONArray();
            for(int i=0; i<hours.length; i++){
                JSONObject time = new JSONObject();
                String[] hoursSplit = hours[i].split(" ");
                switch (hoursSplit[0]) {
                    case "Monday":
                        hoursSplit[0] = "mon";
                        break;
                    case "Tuesday":
                        hoursSplit[0] = "tues";
                        break;
                    case "Wednesday":
                        hoursSplit[0] = "wed";
                        break;
                    case "Thursday":
                        hoursSplit[0] = "thurs";
                        break;
                    case "Friday":
                        hoursSplit[0] = "fri";
                        break;
                    case "Saturday":
                        hoursSplit[0] = "sat";
                        break;
                    case "Sunday":
                        hoursSplit[0] = "sun";
                        break;
                }
                time.put("day", hoursSplit[0]);
                time.put("open", hoursSplit[1]);
                time.put("close", hoursSplit[2]);
                jsonTimes.put(time);
            }
            body.put("name", name);
            location.put("lat", latitude);
            location.put("long", longitude);
            body.put("location", location);
            body.put("requiresPurchase", requiresPurchase);
            body.put("hours", jsonTimes);

            Request request = new Request(RequestType.POST, url, body, new RequestHandler() {
                @Override
                public void callback(Object result) {
                    handler.callback(result);
                }
            });
            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
