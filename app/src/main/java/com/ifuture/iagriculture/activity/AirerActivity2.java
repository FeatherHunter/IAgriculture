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


public class AirerActivity2 extends Activity {

    private ListView listView = null;
    List<Map<String, Object>> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airer);
        listView = (ListView) findViewById(R.id.air_list);
        list = getData();
        listView.setAdapter(new OptionAdapter(this, list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(list.get(position).get("option_name").equals("奔驰C 180L"))
                {
                    Intent intentMain = new Intent();
                    intentMain.setClass(AirerActivity2.this, AirConditionerActivity3.class);
                    AirerActivity2.this.startActivity(intentMain);
                }
                else if(list.get(position).get("option_name").equals("宝马520Li"))
                {
                    Intent intentMain = new Intent();
                    intentMain.setClass(AirerActivity2.this, AirConditionerActivity3.class);
                    AirerActivity2.this.startActivity(intentMain);
                }
                else if(list.get(position).get("option_name").equals("奇瑞 艾瑞泽M7"))
                {
                    Intent intentMain = new Intent();
                    intentMain.setClass(AirerActivity2.this, AirConditionerActivity3.class);
                    AirerActivity2.this.startActivity(intentMain);
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
        map.put("option_picture", R.drawable.benchi_c);
        map.put("option_name", "奔驰C 180L");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("option_picture", R.drawable.baoma_5);
        map.put("option_name", "宝马520Li");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("option_picture", R.drawable.qirui_m7);
        map.put("option_name", "奇瑞 艾瑞泽M7");
        list.add(map);

        return list;
    }
}

