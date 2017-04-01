package ca.team2.crapmap.service;

/**
 * Created by lockhart on 2017-04-01.
 */

public interface RequestHandler<T> {

    void callback(T result);
}
