package ca.team2.crapmap.service;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;

import ca.team2.crapmap.util.RequestType;

/**
 * Created by lockhart on 2017-04-01.
 */

public class BathroomService {

    private static final String BASE_URL = "https://crap-map-server.herokuapp.com/";
    private static final String GET_BATHROOMS = BASE_URL + "bathroom/?lat=%f&long=%f&radius=%d";

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
}
