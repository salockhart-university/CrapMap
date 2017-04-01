package ca.team2.crapmap.service;

import android.app.Activity;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

import ca.team2.crapmap.util.RequestType;

/**
 * Created by lockhart on 2017-04-01.
 */

public class UserService {

    private static final String BASE_URL = Request.BASE_URL;
    private static final String REGISTER_USER = BASE_URL + "user/";

    public static void register(Activity activity, String dialogText, String name, String username, String password, final RequestHandler handler) {
        try {
            URL url = new URL(REGISTER_USER);

            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("username", username);
            body.put("password", password);

            Request request = new Request(RequestType.POST, url, body, new RequestHandler() {
                @Override
                public void callback(Object result) {
                    handler.callback(result);
                }
            });
            request.setProgressDialog(activity, dialogText);
            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
