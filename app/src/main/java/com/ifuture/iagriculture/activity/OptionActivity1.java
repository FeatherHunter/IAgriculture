package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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


public class OptionActivity1 extends Activity {

    private ListView listView = null;
    List<Map<String, Object>> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option1);
        listView = (ListView) findViewById(R.id.option_list);
        list = getData();
        listView.setAdapter(new OptionAdapter(this, list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(list.get(position).get("option_name").equals("车载空调"))
                {
                    Intent intentMain = new Intent();
                    intentMain.setClass(OptionActivity1.this, AirerActivity2.class);
                    OptionActivity1.this.startActivity(intentMain);
                }
                else if(list.get(position).get("option_name").equals("家居控制"))
                {
                    Intent intent = new Intent();

                    intent.putExtra("mode", 2);//选择模式：2为蓝牙模式
                    intent.putExtra("account", "");
                    intent.setClass(OptionActivity1.this, ClientMainActivity.class);
                    OptionActivity1.this.startActivity(intent);
                }
            }
        });
    }

    /**
     *  @author: feather
     *  @description: 判断是否按下返回键，如果按下则进行相应处理
     **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK) //按下返回键
        {
            Intent intent = new Intent();
            intent.putExtra("type", "ClientMainBack");
            intent.setAction(intent.ACTION_MAIN);
            this.sendBroadcast(intent);

            boolean res =super.onKeyDown(keyCode, event);
            //this.onDestory();
            return res;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("option_picture", R.drawable.car_airer);
        map.put("option_name", "车载空调");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("option_picture", R.drawable.car_ihome);
        map.put("option_name", "家居控制");
        list.add(map);

        return list;
    }
}

