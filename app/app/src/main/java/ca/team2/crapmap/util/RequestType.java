package ca.team2.crapmap.util;

import static android.os.FileObserver.DELETE;

/**
 * Created by geoffreycaven on 2017-03-15.
 */

public enum RequestType {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE");

    private String value;
    private RequestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
