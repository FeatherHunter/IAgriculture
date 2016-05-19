package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.ifuture.iagriculture.zxing.BarCodeTestActivity;

public class DatabaseTestActivity extends Activity {

    Button create, delete, clear, insert, query, queryallday, clearallday, switchButton, clearalltable;
    Button twoDCode;
    EditText year, month, day, hour, minute, second;
    EditText temp, humi;
    DatabaseOperation databaseOperation;
    String accountString = null;
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
        clearalltable = (Button) findViewById(R.id.dbtest_clearalltable);
        twoDCode = (Button) findViewById(R.id.twoDCode_test_button);
        twoDCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DatabaseTestActivity.this, BarCodeTestActivity.class);
                startActivity(intent);
            }
        });

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
        clearalltable.setOnClickListener(new ClearAllTableButton());

        SharedPreferences apSharedPreferences = getSharedPreferences("saved", Activity.MODE_PRIVATE);
        accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
    }
    class CreateButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation(accountString);
            databaseOperation.createDatabase(DatabaseTestActivity.this);//创建数据库
        }
    }

    class DeleteButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation(accountString);
            databaseOperation.deleteTable(DatabaseTestActivity.this);//删除数据库
        }
    }
    class ClearButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation(accountString);
            databaseOperation.clearTableToday(DatabaseTestActivity.this);//清除数据库
        }
    }

    class ClearAlltodayButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation(accountString);
            databaseOperation.clearTableAllday(DatabaseTestActivity.this);//清除数据库
        }
    }

    class ClearAllTableButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation(accountString);
            databaseOperation.clearTableAllday(DatabaseTestActivity.this);//清除数据库
            databaseOperation.clearTableToday(DatabaseTestActivity.this);//清除数据库
            databaseOperation.clearTableArea(DatabaseTestActivity.this);//清除数据库
            databaseOperation.clearTableTerminal(DatabaseTestActivity.this);//清除数据库
            databaseOperation.clearTableDevice(DatabaseTestActivity.this);//清除数据库
            databaseOperation.clearTableGHouse(DatabaseTestActivity.this);//清除数据库
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
                DatabaseOperation tempOperation = new DatabaseOperation(accountString);
//                tempOperation.switchTodayToAllday(DatabaseTestActivity.this, lasthour, nowhour);
            }
        }
    }

    class InsertButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation(accountString);
            int yearInt = Integer.parseInt(year.getText().toString());
            int monthInt = Integer.parseInt(month.getText().toString());
            int dayInt = Integer.parseInt(day.getText().toString());
            int hourInt = Integer.parseInt(hour.getText().toString());
            int minuteInt = Integer.parseInt(minute.getText().toString());
            int secondInt = Integer.parseInt(second.getText().toString());

            float tempFloat = Float.parseFloat(temp.getText().toString());
            float humiFloat = Float.parseFloat(humi.getText().toString());
            databaseOperation.insertToday(DatabaseTestActivity.this, "1", hourInt, minuteInt, secondInt, tempFloat, "temperature");
        }
    }

    class QueryButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation(accountString);
            databaseOperation.queryToday(DatabaseTestActivity.this);
        }
    }

    class QueryAlldayButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            databaseOperation = new DatabaseOperation(accountString);
            databaseOperation.queryAllday(DatabaseTestActivity.this);
        }
    }
}
