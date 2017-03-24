package ca.team2.crapmap;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class NewBathroomActivity extends AppCompatActivity {
    private String[] times = {"0000", "0100", "0200", "0300", "0400", "0500", "0600", "0700",
            "0800", "0900", "1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700", "1800",
            "1900", "2000", "2100", "2200", "2300", "2400"};
    private Button submit;
    private EditText name;
    private CheckBox requiresPurchase;
    private CheckBox[] days;
    private Spinner[] startTimes;
    private Spinner[] endTimes;
    private double latitude, longitude;
    private String baseApiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bathroom);
        submit = (Button)findViewById(R.id.new_bathroom_submit);
        name = (EditText)findViewById(R.id.new_bathroom_name);
        requiresPurchase = (CheckBox)findViewById(R.id.new_bathroom_requires_purchase);

        ArrayAdapter<String> timeSpinnerAdapter = new ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, times);
        timeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        days = new CheckBox[7];
        startTimes = new Spinner[7];
        endTimes = new Spinner[7];

        days[0] = (CheckBox)findViewById(R.id.monday);
        startTimes[0] = (Spinner)findViewById(R.id.mondayStart);
        startTimes[0].setAdapter(timeSpinnerAdapter);
        endTimes[0] = (Spinner)findViewById(R.id.mondayEnd);
        endTimes[0].setAdapter(timeSpinnerAdapter);

        days[1] = (CheckBox)findViewById(R.id.tuesday);
        startTimes[1] = (Spinner)findViewById(R.id.tuesdayStart);
        startTimes[1].setAdapter(timeSpinnerAdapter);
        endTimes[1] = (Spinner)findViewById(R.id.tuesdayEnd);
        endTimes[1].setAdapter(timeSpinnerAdapter);

        days[2] = (CheckBox)findViewById(R.id.wednesday);
        startTimes[2] = (Spinner)findViewById(R.id.wednesdayStart);
        startTimes[2].setAdapter(timeSpinnerAdapter);
        endTimes[2] = (Spinner)findViewById(R.id.wednesdayEnd);
        endTimes[2].setAdapter(timeSpinnerAdapter);

        days[3] = (CheckBox)findViewById(R.id.thursday);
        startTimes[3] = (Spinner)findViewById(R.id.thursdayStart);
        startTimes[3].setAdapter(timeSpinnerAdapter);
        endTimes[3] = (Spinner)findViewById(R.id.thursdayEnd);
        endTimes[3].setAdapter(timeSpinnerAdapter);

        days[4] = (CheckBox)findViewById(R.id.friday);
        startTimes[4] = (Spinner)findViewById(R.id.fridayStart);
        startTimes[4].setAdapter(timeSpinnerAdapter);
        endTimes[4] = (Spinner)findViewById(R.id.fridayEnd);
        endTimes[4].setAdapter(timeSpinnerAdapter);

        days[5] = (CheckBox)findViewById(R.id.saturday);
        startTimes[5] = (Spinner)findViewById(R.id.saturdayStart);
        startTimes[5].setAdapter(timeSpinnerAdapter);
        endTimes[5] = (Spinner)findViewById(R.id.saturdayEnd);
        endTimes[5].setAdapter(timeSpinnerAdapter);

        days[6] = (CheckBox)findViewById(R.id.sunday);
        startTimes[6] = (Spinner)findViewById(R.id.sundayStart);
        startTimes[6].setAdapter(timeSpinnerAdapter);
        endTimes[6] = (Spinner)findViewById(R.id.sundayEnd);
        endTimes[6].setAdapter(timeSpinnerAdapter);

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        baseApiUrl = getIntent().getStringExtra("baseApiUrl");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewBathroom();
            }
        });
    }

    public void createNewBathroom(){
        //TODO: check for invalid times
        ArrayList<Hours> arrayListTimes = new ArrayList<>();
        for(int i=0; i<7; i++){
            if(days[i].isChecked()){
                double open = Double.parseDouble(startTimes[i].getSelectedItem().toString());
                double close = Double.parseDouble(endTimes[i].getSelectedItem().toString());
                arrayListTimes.add(new Hours(
                        days[i].getText().toString(),
                        open/100,
                        close/100
                ));
            }
        }

        PostNewBathroom postNewBathroom = new PostNewBathroom(baseApiUrl + "bathroom",
                name.getText().toString(), latitude, longitude, requiresPurchase.isChecked(),
                arrayListTimes.toArray());
        String response = null;
        try{
            response = (String)postNewBathroom.execute().get();
        }
        catch(Exception e){}

        Intent resultIntent = new Intent();
        resultIntent.putExtra("response", response);
        //TODO: send bad response if request failed
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
