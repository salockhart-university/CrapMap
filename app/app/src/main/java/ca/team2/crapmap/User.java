package ca.team2.crapmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by geoffreycaven on 2017-03-15.
 */

public class User implements Serializable {
    String _id, name, username;

    public User(JSONObject user) throws JSONException {
        this._id = user.getString("_id");
        this.name = user.getString("name");
        this.username = user.getString("username");
    }

    public User(String _id, String name, String username) {
        this._id = _id;
        this.name = name;
        this.username = username;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
