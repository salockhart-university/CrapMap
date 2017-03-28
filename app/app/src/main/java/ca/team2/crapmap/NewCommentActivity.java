package ca.team2.crapmap;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.EditText;

public class NewCommentActivity extends AppCompatActivity {

    private RatingBar cleanliness;
    private RatingBar accessibility;
    private RatingBar availability;
    private EditText comment;
    private ImageButton save;

    private String baseApiUrl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);

        cleanliness = (RatingBar) findViewById(R.id.rbCleanliness);
        accessibility = (RatingBar) findViewById(R.id.rbAccessibility);
        availability = (RatingBar) findViewById(R.id.rbAccessibility);
        comment = (EditText) findViewById(R.id.tbComment);
        save = (ImageButton) findViewById(R.id.btSave);

        baseApiUrl = getIntent().getStringExtra("baseApiUrl");

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

        PostNewComment postNewComment = new PostNewComment(baseApiUrl, clean, avail, access, commentString);
        String response = null;
        try{
            response = (String) postNewComment.execute().get();
        }
        catch(Exception e){}

        Intent resultIntent = new Intent();
        resultIntent.putExtra("response", response);
        //TODO: send bad response if request failed
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }

}
