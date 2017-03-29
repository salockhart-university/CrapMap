package ca.team2.crapmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PreviewBathroomActivity extends AppCompatActivity {

    private float cleanliness, accessibility, availability;
    private String comment;
    String bathroomId;


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


    public void setComment(String comment) {

        this.comment = comment;
    }

    public String getComment(){
        return comment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_bathroom);

        Intent intent = this.getIntent();
        Bathroom bathroom = (Bathroom)intent.getSerializableExtra("bathroom");
        Log.i("bundle", bathroom.toString());
    }

    //Main method to handle the loading of comments/ratings
    public void loadRatings(){

    }


}
