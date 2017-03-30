package ca.team2.crapmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class PreviewBathroomActivity extends AppCompatActivity {

    private float avgClean, avgAccess , avgAvail ;
    private String comment;
    String bathroomId;
    public Bathroom bathroom;

    private RatingBar avg_accessibility, avg_availability, avg_cleanliness;
    private TextView bathroom_name;
    private ListView review_listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_bathroom);

        Intent intent = this.getIntent();
        bathroom = (Bathroom)intent.getSerializableExtra("bathroom");
        Log.i("bundle", bathroom.toString());
        // cleanliness = bathroom.getReviews().get(0).getCleanliness();
        avg_accessibility = (RatingBar) findViewById(R.id.rating_average_accessibility);
        avg_availability = (RatingBar) findViewById(R.id.rating_average_availability);
        avg_cleanliness = (RatingBar) findViewById(R.id.rating_average_cleanliness);
        bathroom_name = (TextView) findViewById(R.id.text_bathroom);
        review_listView = (ListView) findViewById(R.id.review_listView);

        loadReviews();
    }


    //Main method to handle the loading of comments/ratings
    public void loadReviews(){
        int reviewCount = bathroom.getReviews().size();
        avgClean = 0;
        avgAccess = 0;
        avgAvail = 0;
        
        for(int i = 0; i < reviewCount ; i++){
            Review tempReview = bathroom.getReviews().get(i);
            float cleanlinessT, accessibilityT, availabilityT;
            String commentT, userNameT;
            cleanlinessT = tempReview.getCleanliness();
            accessibilityT = tempReview.getAccessibility();
            availabilityT = tempReview.getAvailability();
            commentT = tempReview.getReview();
            userNameT = tempReview.getUser().toString();
            avgClean += cleanlinessT;
            avgAccess += accessibilityT;
            avgAvail += availabilityT;
            generateReviewModule(cleanlinessT, accessibilityT, availabilityT, commentT, userNameT);
        }

        avgClean /= reviewCount;
        avgAccess /= reviewCount;
        avgAvail /= reviewCount;

        bathroom_name.setText(bathroom.getName());
        avg_cleanliness.setRating(avgClean);
        avg_accessibility.setRating(avgAccess);
        avg_availability.setRating(avgAvail);
    }

    public void generateReviewModule(float cleanliness, float accessibility, float availability, String comment, String userName){
        //TODO figure out how to place these
    }


}
