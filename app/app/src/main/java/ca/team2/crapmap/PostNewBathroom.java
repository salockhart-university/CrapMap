package ca.team2.crapmap;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostNewBathroom extends AsyncTask {
    private String stringUrl, name;
    private double latitude, longitude;
    private boolean requiresPurchase;
    private String[] hours;

    public PostNewBathroom(String stringUrl, String name, double latitude, double longitude,
        boolean requiresPurchase, String[] hours){
        this.stringUrl = stringUrl;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.requiresPurchase = requiresPurchase;
        this.hours = hours;
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

    public void setHours(String[] hours){
        this.hours = hours;
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
            JSONArray jsonTimes = new JSONArray();
            for(int i=0; i<hours.length; i++){
                JSONObject time = new JSONObject();
                String[] hoursSplit = hours[i].split(" ");
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

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            printout.writeBytes(body.toString());
            printout.flush();
            printout.close();

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            JSONObject json = new JSONObject(sb.toString());

            return json.toString();
        }
        catch(Exception e){
            return null;
        }
    }
}
