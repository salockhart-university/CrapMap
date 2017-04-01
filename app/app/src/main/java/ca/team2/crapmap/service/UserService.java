package ca.team2.crapmap.service;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import ca.team2.crapmap.model.User;
import ca.team2.crapmap.util.RequestType;

/**
 * Created by lockhart on 2017-04-01.
 */

public class UserService {

    private static final String BASE_URL = Request.BASE_URL;
    private static final String BASE_USER = BASE_URL + "user/";
    private static final String LOGIN_USER = BASE_USER + "login";

    public static void register(Activity activity, String dialogText, String name, String username, String password, final RequestHandler<User> handler) {
        try {
            URL url = new URL(BASE_USER);

            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("username", username);
            body.put("password", password);

            Request request = new Request(RequestType.POST, url, body, new RequestHandler<String>() {
                @Override
                public void callback(String result) {
                    if (result == null) {
                        handler.callback(null);
                        return;
                    }
                    try {
                        handler.callback(JSONParser.parseUser(new JSONObject(result)));
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

    public static void login(Activity activity, String dialogText, String username, String password, final RequestHandler<JSONObject> handler) {
        try {
            URL url = new URL(LOGIN_USER);

            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", password);

            Request request = new Request(RequestType.POST, url, body, new RequestHandler<String>() {
                @Override
                public void callback(String result) {
                    if (result == null) {
                        handler.callback(null);
                        return;
                    }
                    try {
                        handler.callback(new JSONObject(result));
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
