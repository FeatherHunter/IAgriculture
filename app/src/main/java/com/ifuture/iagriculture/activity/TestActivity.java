package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CalendarView;

import com.ifuture.iagriculture.R;

import java.util.Calendar;

public class TestActivity extends Activity {

    CalendarView calendar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activityest);

        calendar = (CalendarView)findViewById(R.id.calendarView);
    }
}
