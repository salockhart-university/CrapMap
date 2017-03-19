package ca.team2.crapmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by geoffreycaven on 2017-03-18.
 */

public class ClusterableBathroomMarker implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;

    public ClusterableBathroomMarker(double lat, double lng, String title, String snippet) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }
}
