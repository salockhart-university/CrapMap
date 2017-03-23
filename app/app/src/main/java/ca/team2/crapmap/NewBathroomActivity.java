package ca.team2.crapmap;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class NewBathroomActivity extends AppCompatActivity {
    private Button submit;
    private EditText name;
    private CheckBox requiresPurchase;
    private double latitude, longitude;
    private String baseApiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bathroom);

        submit = (Button)findViewById(R.id.new_bathroom_submit);
        name = (EditText)findViewById(R.id.new_bathroom_name);
        requiresPurchase = (CheckBox)findViewById(R.id.new_bathroom_requires_purchase);
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        baseApiUrl = getIntent().getStringExtra("baseApiUrl");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNewBathroomCreated();
            }
        });
    }

    public void onNewBathroomCreated() {
        //this should after a successful response from the server after submitting new bathroom
        //will let the map view know to refresh the markers from the server
        Intent resultIntent = new Intent();
        PostNewBathroom postNewBathroom = new PostNewBathroom(baseApiUrl + "bathroom",
            name.toString(), latitude, longitude, requiresPurchase.isChecked());
        postNewBathroom.execute();
        //TODO: send response back
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
