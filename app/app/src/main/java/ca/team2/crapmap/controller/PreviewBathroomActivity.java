package ca.team2.crapmap.controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.team2.crapmap.R;
import ca.team2.crapmap.model.Bathroom;
import ca.team2.crapmap.model.Review;

public class PreviewBathroomActivity extends AppCompatActivity {

    public Bathroom bathroom;
    private List<Review> reviewList = new ArrayList<Review>();

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
        FloatingActionButton fab_newComment = (FloatingActionButton) findViewById(R.id.fab_newComment);
        fab_newComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNewCommentView();
            }
        });

        reviewList = bathroom.getReviews();
        loadBaseStats();//load initial data
        generateReviewModule();//generate listview contents
    }


    //Main method to handle the loading of comments/ratings
    public void loadBaseStats(){

        bathroom_name.setText(bathroom.getName());
        avg_cleanliness.setRating(bathroom.getAvgRatings()[0]);
        avg_availability.setRating(bathroom.getAvgRatings()[1]);
        avg_accessibility.setRating(bathroom.getAvgRatings()[2]);

    }

    //sets adapter for listview, calls custom ReviewAdapter class
    public void generateReviewModule(){
        //ArrayList<Review> br = bathroom.getReviews();
        ArrayAdapter<Review> arrayAdapter = new ReviewAdapter();//ArrayAdapter<Review>(this, R.layout.review_row, br);
        review_listView.setAdapter(arrayAdapter);


    }

    private class ReviewAdapter extends ArrayAdapter<Review> {
        public ReviewAdapter(){
            super(PreviewBathroomActivity.this, R.layout.review_row, bathroom.getReviews());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //check for null & create if needed
            View itemView = convertView;
            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.review_row,parent,false);
            }

            //get current review
            Review currentReview = reviewList.get(position);

            //fill row
            TextView user = (TextView) itemView.findViewById(R.id.text_username);
            RatingBar access = (RatingBar) itemView.findViewById(R.id.rb_access);
            RatingBar avail = (RatingBar) itemView.findViewById(R.id.rb_avail);
            RatingBar clean = (RatingBar) itemView.findViewById(R.id.rb_clean);
            TextView comment = (TextView) itemView.findViewById(R.id.text_comment);
            //user.setText("Anon");//TODO temp solution
            if(currentReview.getUser() != null) {
                if (currentReview.getUser().getUsername() == "") {
                    user.setText("Anon");//If no name given, print anon
                } else {
                    user.setText(currentReview.getUser().getUsername());
                }
            }

            access.setRating(currentReview.getAccessibility());
            avail.setRating(currentReview.getAvailability());
            clean.setRating(currentReview.getCleanliness());
            comment.setText(currentReview.getReview());
            return itemView;
            //return super.getView(position, convertView, parent);
        }
    }

    public void loadNewCommentView(){
        Intent intent = new Intent(PreviewBathroomActivity.this, NewCommentActivity.class);
        if (bathroom != null) {
            String bathroomID = bathroom.getId();
            String url = ""; //getIntent().getStringExtra("baseApiUrl") + getIntent().getStringExtra("id") + "/review";
            intent.putExtra("bathroomID", bathroomID);
            intent.putExtra("reviewURL", url);
            startActivity(intent);
        }
    }


}
