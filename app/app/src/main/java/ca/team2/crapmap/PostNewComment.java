package ca.team2.crapmap;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostNewComment extends AsyncTask {
    private float cleanliness, accessibility, availability;
    private String comment, stringUrl;
    String bathroomId, userToken;

    //TODO: add user and bathroom parameters
    public PostNewComment(String stringUrl, float cleanliness, float accessibility, float availability, String comment, String userToken) {
        this.stringUrl = stringUrl;
        this.cleanliness = cleanliness;
        this.accessibility = accessibility;
        this.availability = availability;
        this.comment = comment;
        this.userToken = userToken;
    }

    public float getCleanliness() {
        return cleanliness;
    }

    public void setCleanliness(float cleanliness) {
        this.cleanliness = cleanliness;
    }

    public float getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(float accessibility) {
        this.accessibility = accessibility;
    }

    public float getAvailability() {
        return availability;
    }

    public void setAvailability(float availability) {
        this.availability = availability;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStringUrl() {
        return stringUrl;
    }

    public void setStringUrl(String stringUrl) {
        this.stringUrl = stringUrl;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try{
            URL url = new URL(this.stringUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            if (this.userToken != null) {
                connection.addRequestProperty("Authorization", userToken);
            }
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();

            JSONObject body = new JSONObject();
            JSONObject stars = new JSONObject();

            stars.put("cleanliness", cleanliness);
            stars.put("availability", availability);
            stars.put("accessibility", accessibility);

            body.put("stars", stars);
            body.put("review", comment);

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
