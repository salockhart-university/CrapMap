package ca.team2.crapmap.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.EditText;

import ca.team2.crapmap.service.BathroomService;
import ca.team2.crapmap.R;
import ca.team2.crapmap.service.RequestHandler;

public class NewCommentActivity extends AppCompatActivity {

    private RatingBar cleanliness;
    private RatingBar accessibility;
    private RatingBar availability;
    private EditText comment;
    private ImageButton save;

    private String baseUrl;
    private String reviewUrl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);

        cleanliness = (RatingBar) findViewById(R.id.rbCleanliness);
        accessibility = (RatingBar) findViewById(R.id.rbAccessibility);
        availability = (RatingBar) findViewById(R.id.rbAvailability);
        comment = (EditText) findViewById(R.id.tbComment);
        save = (ImageButton) findViewById(R.id.btSave);

        baseUrl = getString(R.string.base_api_url);
        reviewUrl = baseUrl + "bathroom/" + getIntent().getStringExtra("bathroomID") + "/review";

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewComment();
            }
        });
    }

    public void createNewComment() {
        float clean = cleanliness.getRating();
        float avail = availability.getRating();
        float access = accessibility.getRating();
        String commentString = comment.getText().toString();

        String bathroomID = getIntent().getStringExtra("bathroomID");

        SharedPreferences settings = getSharedPreferences("LOGIN_TOKEN", 0);
        String userToken = settings.getString("token", null);

        BathroomService.addComment(bathroomID, clean, access, avail, commentString, userToken, new RequestHandler() {
            @Override
            public void callback(Object result) {
                String response = (String) result;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("response", response);
                //TODO: send bad response if request failed
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

}
