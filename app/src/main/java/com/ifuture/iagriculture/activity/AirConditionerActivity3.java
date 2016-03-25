package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.feather.adapter.OptionAdapter;
import com.ifuture.carcontrl_client.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AirConditionerActivity3 extends Activity {

    private ListView listView = null;
    List<Map<String, Object>> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_conditioner);
        listView = (ListView) findViewById(R.id.air_conditioner_list);
        list = getData();
        listView.setAdapter(new OptionAdapter(this, list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(list.get(position).get("option_name").equals("S6空调控制"))
                {
                    Intent intentMain = new Intent();
                    intentMain.setClass(AirConditionerActivity3.this, AirControler.class);
                    AirConditionerActivity3.this.startActivity(intentMain);
                }
                else if(list.get(position).get("option_name").equals("S7空调控制"))
                {
                    Intent intentMain = new Intent();
                    intentMain.setClass(AirConditionerActivity3.this, AirControler.class);
                    AirConditionerActivity3.this.startActivity(intentMain);
                }
                else if(list.get(position).get("option_name").equals("G4空调控制"))
                {
                    Intent intentMain = new Intent();
                    intentMain.setClass(AirConditionerActivity3.this, AirControler.class);
                    AirConditionerActivity3.this.startActivity(intentMain);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("option_picture", R.drawable.feather);
        map.put("option_name", "S6空调控制");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("option_picture", R.drawable.feather);
        map.put("option_name", "S7空调控制");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("option_picture", R.drawable.feather);
        map.put("option_name", "G4空调控制");
        list.add(map);

        return list;
    }
}

