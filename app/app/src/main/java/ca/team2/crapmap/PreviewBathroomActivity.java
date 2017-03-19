package ca.team2.crapmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PreviewBathroomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_bathroom);

        Intent intent = this.getIntent();
        Bathroom bathroom = (Bathroom)intent.getSerializableExtra("bathroom");
        Log.i("bundle", bathroom.toString());
    }
}
