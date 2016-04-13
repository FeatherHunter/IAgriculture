package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;

import java.util.ArrayList;
import java.util.List;

public class BandTerminalDialog extends Activity {

    ButtonRectangle checkButton, cancelButton;
    private Spinner areaSpinner = null;
    private EditText termNumEditText = null;
    DatabaseOperation databaseOperation = null; //数据库操作类
    private List<String> arealist = null;
    private ArrayAdapter<String> adapter;

    private int RESULT_OK = 1;
    private int RESULT_ERR = 0;

    private int area_number = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //去除标题栏
        setContentView(R.layout.activity_band_terminal_dialog);

        //获取控件
        areaSpinner = (Spinner) findViewById(R.id.bandT_area_spinner);
        termNumEditText = (EditText) findViewById(R.id.bandT_terminal_num);
        checkButton = (ButtonRectangle) findViewById(R.id.bandT_check_button);
        cancelButton = (ButtonRectangle) findViewById(R.id.bandT_cancel_button);

        /* -----------------------------------------------------------------
	     *             利用用户名创建or获得数据库
	     * -----------------------------------------------------------------*/
        SharedPreferences apSharedPreferences = getSharedPreferences("saved", Activity.MODE_PRIVATE);
        String accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
        databaseOperation = new DatabaseOperation(accountString); //使用用户名创建数据库
        databaseOperation.createDatabase(this);//创建数据库

        /* -----------------------------------------------------------------
	     *             将地区号和地区名添加到spinner(下拉框)中去
	     * -----------------------------------------------------------------*/
        String areas[] = databaseOperation.queryAreaName(this); //获得地区名
        arealist = new ArrayList<String>();
        for(int i = 0; areas[i] != null; i++)
        {
            arealist.add(""+i+"-"+areas[i]);    //spinner获取显示内容
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arealist);//添加arealist链表
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//为适配器设置下拉列表下拉时的菜单样式。
        areaSpinner.setAdapter(adapter);

        areaSpinner.setOnItemSelectedListener(new areaSpinnerOnItemSelectedListener()); //设置监听器

        checkButton.setOnClickListener(new buttonListener());
        cancelButton.setOnClickListener(new buttonListener());


    }

    class areaSpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            area_number = position;
            System.out.println("POSITION:====" + position);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class buttonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.bandT_check_button)
            {
                String contextString = termNumEditText.getText().toString();
                if(contextString.equals("")) //为空
                {
                    Toast.makeText(BandTerminalDialog.this, "终端号不能为空", Toast.LENGTH_SHORT).show();
                }
                else if(contextString.length() > 16)
                {
                    Toast.makeText(BandTerminalDialog.this, "终端号太长了，16个以内再试试", Toast.LENGTH_SHORT).show();
                }
                else if(area_number == -1)
                {
                    Toast.makeText(BandTerminalDialog.this, "需要选择地区！", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(contextString.matches("[0-9]+")) //只包含0-9
                    {
                        databaseOperation.insertTerminal(BandTerminalDialog.this, area_number, contextString); //增加终端
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);   //创建成功
                        finish();
                    }
                    else
                    {
                        Toast.makeText(BandTerminalDialog.this, "终端号只能有数字", Toast.LENGTH_SHORT).show();
                    }

                }

            }else if(id == R.id.bandT_cancel_button)
            {
                Intent intent = new Intent();
                setResult(RESULT_ERR, intent);   //取消绑定
                finish();
            }
        }
    }
}
