package ca.team2.crapmap;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostNewBathroom extends AsyncTask {
    private String stringUrl, name;
    private double latitude, longitude;
    private boolean requiresPurchase;

    public PostNewBathroom(String stringUrl, String name, double latitude, double longitude,
        boolean requiresPurchase){
        this.stringUrl = stringUrl;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.requiresPurchase = requiresPurchase;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setRequiresPurchase(boolean requiresPurchase){
        this.requiresPurchase = requiresPurchase;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            URL url = new URL(this.stringUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();

            JSONObject body = new JSONObject();
            JSONObject location = new JSONObject();
            JSONObject[] times = new JSONObject[7];
            //TODO: populate times
            body.put("name", name);
            location.put("lat", latitude);
            location.put("long", longitude);
            body.put("location", location);
            body.put("requiresPurchase", requiresPurchase);
            body.put("hours", times);

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuffer response = new StringBuffer();
            while((input = reader.readLine()) != null){
                response.append(input);
            }
            reader.close();

            return response;
        }
        catch(Exception e){
            return null;
        }
    }
}
