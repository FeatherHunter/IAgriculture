package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ifuture.iagriculture.Calendar.TodayTime;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;
import com.ifuture.iagriculture.sqlite.DayDatabaseHelper;

public class DatabaseTestActivity extends Activity {

    Button create, delete, clear, insert, query, queryallday, clearallday, switchButton;
    EditText year, month, day, hour, minute, second;
    EditText temp, humi;
    DatabaseOperation databaseOperation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);
        create = (Button) findViewById(R.id.dbtest_create);
        delete = (Button) findViewById(R.id.dbtest_delete);
        clear = (Button) findViewById(R.id.dbtest_clear);
        insert = (Button) findViewById(R.id.dbtest_insert);
        query = (Button) findViewById(R.id.dbtest_search);
        queryallday = (Button) findViewById(R.id.dbtest_searchallday);
        clearallday = (Button) findViewById(R.id.dbtest_clearallday);
        switchButton = (Button) findViewById(R.id.dbtest_switch);

        year = (EditText) findViewById(R.id.dbtest_year);
        month = (EditText) findViewById(R.id.dbtest_month);
        day = (EditText) findViewById(R.id.dbtest_day);
        hour = (EditText) findViewById(R.id.dbtest_hour);
        minute = (EditText) findViewById(R.id.dbtest_minute);
        second = (EditText) findViewById(R.id.dbtest_second);

        temp = (EditText) findViewById(R.id.dbtest_temp);
        humi = (EditText) findViewById(R.id.dbtest_humi);

        create.setOnClickListener(new CreateButton());
        delete.setOnClickListener(new DeleteButton());
        clear.setOnClickListener(new ClearButton());
        insert.setOnClickListener(new InsertButton());
        query.setOnClickListener(new QueryButton());
        queryallday.setOnClickListener(new QueryAlldayButton());
        clearallday.setOnClickListener(new ClearAlltodayButton());
        switchButton.setOnClickListener(new SwitchButton());
    }
    class CreateButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation();
            databaseOperation.createDatabase(DatabaseTestActivity.this);//创建数据库
        }
    }

    class DeleteButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation();
            databaseOperation.deleteTable(DatabaseTestActivity.this);//删除数据库
        }
    }
    class ClearButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation();
            databaseOperation.clearTableToday(DatabaseTestActivity.this);//清除数据库
        }
    }

    class ClearAlltodayButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation();
            databaseOperation.clearTableAllday(DatabaseTestActivity.this);//清除数据库
        }
    }

    class SwitchButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            /* -------------------------------------------------------
	     	 *  将today表数据添加到allday表中
		     * -------------------------------------------------------*/
            SharedPreferences apSharedPreferences  = getSharedPreferences("today_To_allday", Activity.MODE_PRIVATE);
            int lasthour = apSharedPreferences.getInt("hour", 0);
            TodayTime nowTime = new TodayTime();
            nowTime.update();
            int nowhour = nowTime.getHour();
            if(lasthour < nowhour)
            {
                DatabaseOperation tempOperation = new DatabaseOperation();
                tempOperation.switchTodayToAllday(DatabaseTestActivity.this, lasthour, nowhour);
            }
        }
    }

    class InsertButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation();
            int yearInt = Integer.parseInt(year.getText().toString());
            int monthInt = Integer.parseInt(month.getText().toString());
            int dayInt = Integer.parseInt(day.getText().toString());
            int hourInt = Integer.parseInt(hour.getText().toString());
            int minuteInt = Integer.parseInt(minute.getText().toString());
            int secondInt = Integer.parseInt(second.getText().toString());

            float tempFloat = Float.parseFloat(temp.getText().toString());
            float humiFloat = Float.parseFloat(humi.getText().toString());
            databaseOperation.insertToday(DatabaseTestActivity.this, hourInt, minuteInt, secondInt, tempFloat, humiFloat);
        }
    }

    class QueryButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation();
            databaseOperation.queryToday(DatabaseTestActivity.this);
        }
    }

    class QueryAlldayButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation();
            databaseOperation.queryAllday(DatabaseTestActivity.this);
        }
    }
}
