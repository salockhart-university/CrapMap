package ca.team2.crapmap.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import ca.team2.crapmap.util.RequestType;

/**
 * Created by lockhart on 2017-04-01.
 */

public class Request extends AsyncTask<Void, Void, String> {

    private RequestType method;
    private URL url;
    private JSONObject body = null;
    private HashMap<String, String> headers = null;
    private RequestHandler handler;

    private ProgressDialog pDialog = null;

    Request(RequestType method, URL url, JSONObject body, HashMap<String, String> headers, RequestHandler handler) {
        this.method = method;
        this.url = url;
        this.body = body;
        this.headers = headers;
        this.handler = handler;
    }

    Request(RequestType method, URL url, JSONObject body, RequestHandler handler) {
        this.method = method;
        this.url = url;
        this.body = body;
        this.handler = handler;
    }

    Request(RequestType method, URL url, HashMap<String, String> headers, RequestHandler handler) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.handler = handler;
    }

    Request(RequestType method, URL url, RequestHandler handler) {
        this.method = method;
        this.url = url;
        this.handler = handler;
    }

    public void setProgressDialog(Activity activity, String dialogText) {
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(dialogText);
        pDialog.setCancelable(false);
    }

    private InputStream getRequest() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.addRequestProperty(key, headers.get(key));
            }
        }

        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.connect();
        return connection.getInputStream();
    }

    private InputStream postRequest() throws IOException {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/json");

        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.addRequestProperty(key, headers.get(key));
            }
        }

        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.connect();

        DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
        printout.writeBytes(body.toString());
        printout.flush();
        printout.close();

        return connection.getInputStream();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (pDialog != null) {
            pDialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        InputStream is;
        try {
            switch (this.method) {
                case GET:
                    is = getRequest();
                    break;
                case POST:
                    is = postRequest();
                    break;
                default:
                    return null;
            }
            return inputStreamToString(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (pDialog != null) {
            pDialog.dismiss();
        }
        handler.callback(result);
    }

    private static String inputStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }
}
