package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonRectangle;
import com.ifuture.iagriculture.Instruction.Instruction;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;

public class SettingActivity extends Activity {

    String areaNum = null;
    String greenhouseNum = null;

    NumberPicker tempMaxPicker, tempMinPicker;
    ButtonRectangle tempCheck;

    NumberPicker humiMaxPicker, humiMinPicker;
    ButtonRectangle humiCheck;

    DatabaseOperation databaseOperation = null;//数据库操作类

    int tempMax = 0;//上限
    int tempMin = 0;//下限
    int humiMax = 0;
    int humiMin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //去除标题栏
        setContentView(R.layout.activity_setting);

        tempMaxPicker = (NumberPicker)findViewById(R.id.setting_tempmax_picker);
        tempMinPicker = (NumberPicker)findViewById(R.id.setting_tempmin_picker);
        humiMaxPicker = (NumberPicker)findViewById(R.id.setting_humimax_picker);
        humiMinPicker = (NumberPicker)findViewById(R.id.setting_humimin_picker);
        //使得numberpicker无法编辑
        tempMaxPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        tempMinPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        humiMaxPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        humiMinPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        tempCheck = (ButtonRectangle)findViewById(R.id.setting_temp_button);
        humiCheck = (ButtonRectangle)findViewById(R.id.setting_humi_button);

        Intent intent = getIntent();
        areaNum = intent.getStringExtra("area");
        greenhouseNum = intent.getStringExtra("greenhouse");

        if((areaNum == null)||(greenhouseNum == null))
        {
            Toast.makeText(SettingActivity.this, "致命错误：获取地区号和大棚号失败！请退出该界面", Toast.LENGTH_SHORT).show();
        }
        else
        {

        }

        tempMaxPicker.setMaxValue(30);
        tempMaxPicker.setMinValue(0);
        tempMaxPicker.setValue(25);
        tempMinPicker.setMaxValue(30);
        tempMinPicker.setMinValue(0);
        tempMinPicker.setValue(18);

        humiMaxPicker.setMaxValue(100);
        humiMaxPicker.setMinValue(0);
        humiMaxPicker.setValue(60);
        humiMinPicker.setMaxValue(100);
        humiMinPicker.setMinValue(0);
        humiMinPicker.setValue(45);

        tempCheck.setOnClickListener(new checkButtonListenner());
        humiCheck.setOnClickListener(new checkButtonListenner());

        /* -----------------------------------------------------------------
	     *             利用用户名创建or获得数据库
	     * -----------------------------------------------------------------*/
        SharedPreferences apSharedPreferences = getSharedPreferences("saved", Activity.MODE_PRIVATE);
        String accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
        databaseOperation = new DatabaseOperation(accountString); //使用用户名创建数据库
        databaseOperation.createDatabase(this);//创建数据库

    }

    class checkButtonListenner implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.setting_temp_button)//温度
            {
                tempMax = tempMaxPicker.getValue();//上限
                tempMin = tempMinPicker.getValue();//下限
                if(tempMin <= tempMax)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                    dialog.setTitle("确定更改温度范围？");
                    dialog.setMessage("温度上限："+tempMax+"\n温度下限："+tempMin);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseOperation.updateTempLimitGHouse(SettingActivity.this, Integer.parseInt(areaNum), greenhouseNum,
                                    tempMax, tempMin); //更新温度的上限和下限

                            //发送设置的温度到服务器
                            Instruction.broadcastMsgToServer(SettingActivity.this,
                                    Instruction.setTempLimit(areaNum, greenhouseNum, tempMax+"", tempMin+""));
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else
                {
                    Toast.makeText(SettingActivity.this, "温度下限不能高于温度上限", Toast.LENGTH_SHORT).show();
                }
            }//end of 温度
            else if(v.getId() == R.id.setting_humi_button)//湿度
            {
                humiMax = humiMaxPicker.getValue();//上限
                humiMin = humiMinPicker.getValue();//下限
                if(humiMin <= humiMax)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                    dialog.setTitle("确定更改湿度范围？");
                    dialog.setMessage("湿度上限："+humiMax+"\n湿度下限："+humiMin);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseOperation.updateHumiLimitGHouse(SettingActivity.this, Integer.parseInt(areaNum), greenhouseNum,
                                    humiMax, humiMin); //更新温度的上限和下限

                            //发送设置的温度到服务器
                            Instruction.broadcastMsgToServer(SettingActivity.this,
                                    Instruction.setHumiLimit(areaNum, greenhouseNum, humiMax+"", humiMin+""));
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else
                {
                    Toast.makeText(SettingActivity.this, "湿度下限不能高于湿度上限", Toast.LENGTH_SHORT).show();
                }
            }//end of else if(v.getId() == R.id.setting_humi_button)
        }
    }
}
