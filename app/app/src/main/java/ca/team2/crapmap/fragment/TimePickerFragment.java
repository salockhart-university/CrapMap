package ca.team2.crapmap.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends android.support.v4.app.DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private TextView textView = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void setTextView(TextView textView){
        this.textView = textView;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        textView.setText(String.format("%02d", hourOfDay) + String.format("%02d", minute));
    }
}