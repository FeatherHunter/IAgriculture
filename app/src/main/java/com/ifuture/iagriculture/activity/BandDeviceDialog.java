package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.ifuture.iagriculture.Instruction.Instruction;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;
import com.ifuture.iagriculture.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

public class BandDeviceDialog extends Activity {

    ButtonRectangle checkButton, cancelButton; //确定和取消
    Button scanButton;
    private Spinner areaSpinner = null;        //地区号spinner
    private Spinner gHouseSpinner = null;      //大棚号spinner

    private List<String> arealist = null;
    private List<String> ghouselist = null;

    private EditText deviceNumEditText = null;
    DatabaseOperation databaseOperation = null; //数据库操作类
    private ArrayAdapter<String> adapter = null;

    private int RESULT_ERR = 0;

    private int area_number = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //去除标题栏
        setContentView(R.layout.activity_band_device_dialog);

        //获取控件
        areaSpinner = (Spinner) findViewById(R.id.bandDevice_area_spinner);
        gHouseSpinner  = (Spinner) findViewById(R.id.bandDevice_ghouse_spinner);
        deviceNumEditText = (EditText) findViewById(R.id.bandDevice_device_num);
        checkButton = (ButtonRectangle) findViewById(R.id.bandDevice_check_button);
        cancelButton = (ButtonRectangle) findViewById(R.id.bandDevice_cancel_button);
        scanButton = (Button) findViewById(R.id.bandDevice_scan_button); //二维码扫描Button

        deviceNumEditText.clearFocus();  //edittext失去焦点
        deviceNumEditText.setSelected(false);

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


        /* -----------------------------------------------------------------
	     *             存在地区时，显示地区号0的所有大棚号
	     * -----------------------------------------------------------------*/
        if(areas[0] != null)
        {
            String ghouses[] = databaseOperation.queryGHousePerArea(this, 0); //获得地区名
            ghouselist = new ArrayList<String>();
            for(int i = 0; ghouses[i] != null; i++)
            {
                ghouselist.add(""+ghouses[i]);    //spinner获取显示内容
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ghouselist);//添加arealist链表
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//为适配器设置下拉列表下拉时的菜单样式。
            gHouseSpinner.setAdapter(adapter);
            gHouseSpinner.setOnItemSelectedListener(new gHouseSpinnerOnItemSelectedListener()); //设置监听器

        }
        checkButton.setOnClickListener(new buttonListener());
        cancelButton.setOnClickListener(new buttonListener());
        scanButton.setOnClickListener(new scanListener());
    }

    class areaSpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if(area_number != position)
            {
                area_number = position;
                /* -----------------------------------------------------------------
	             *             大棚号列表：添加到spinner中显示
	             * -----------------------------------------------------------------*/
                String ghouses[] = databaseOperation.queryGHousePerArea(BandDeviceDialog.this, position); //获得地区名
                ghouselist = new ArrayList<String>();
                for(int i = 0; ghouses[i] != null; i++)
                {
                    ghouselist.add(""+ghouses[i]);    //spinner获取显示内容
                }
                adapter = new ArrayAdapter<String>(BandDeviceDialog.this, android.R.layout.simple_spinner_item, ghouselist);//添加arealist链表
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//为适配器设置下拉列表下拉时的菜单样式。
                gHouseSpinner.setAdapter(adapter);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class gHouseSpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    /**--------------------------------------------------
     *     扫描二维码监听器的Button监听器
     *--------------------------------------------*/
    class scanListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.bandDevice_scan_button)
            {
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(BandDeviceDialog.this,CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        }
    }

    /**--------------------------------------------------
     *      处理二维码扫描界面得到的结果
     *--------------------------------------------*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");

            deviceNumEditText.clearFocus();
            deviceNumEditText.setSelected(false);
            if(scanResult.length()  < 3)
            {
                Toast.makeText(BandDeviceDialog.this, "不是正确的二维码！", Toast.LENGTH_SHORT).show();
                return;
            }
            if(scanResult.substring(0,2).equals("设备"))
            {
                deviceNumEditText.setText(scanResult.substring(2));
                Toast.makeText(BandDeviceDialog.this, "扫描成功", Toast.LENGTH_SHORT).show();
            }
            else if(scanResult.substring(0,2).equals("终端"))
            {
                Toast.makeText(BandDeviceDialog.this, "终端二维码,请在“绑定终端”中进行绑定。或者选择正确的设备二维码。", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(BandDeviceDialog.this, "不是正确的二维码！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class buttonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.bandDevice_check_button)
            {
                String deviceString = deviceNumEditText.getText().toString();
                String gHouseString = gHouseSpinner.getItemAtPosition(gHouseSpinner.getSelectedItemPosition()).toString();
                if(deviceString.equals("")) //为空
                {
                    Toast.makeText(BandDeviceDialog.this, "设备号不能为空", Toast.LENGTH_SHORT).show();
                }
                else if(deviceString.length() > 32)
                {
                    Toast.makeText(BandDeviceDialog.this, "亲，设备号最多32个", Toast.LENGTH_SHORT).show();
                }
                else if(area_number == -1)
                {
                    Toast.makeText(BandDeviceDialog.this, "需要选择地区！", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(deviceString.matches("[0-9]+")) //只包含0-9
                    {
                       /* ---------------------------------------------------------------------
	                    *               让服务器绑定地区号、大棚号和设备号
	                    *               @by 广播发送给后台Service--转发给服务器
	                    * ---------------------------------------------------------------------*/
                        Intent intent = new Intent();
                        intent.putExtra("type", "send");
                        String msg = Instruction.bandDevice(Integer.toString(area_number), gHouseString, deviceString); //将地区号和地区名转为发送的信息
                        intent.putExtra("send", msg);
                        intent.setAction(intent.ACTION_MAIN);
                        sendBroadcast(intent);

                        /* ---------------------------------------------------------------------
	                     *                            数据库中绑定设备
	                     * ---------------------------------------------------------------------*/
                        databaseOperation.insertDevice(BandDeviceDialog.this, area_number, gHouseString, deviceString); //增加终端
                        intent = new Intent();
                        setResult(RESULT_OK, intent);   //创建成功
                        finish();
                    }
                    else
                    {
                        Toast.makeText(BandDeviceDialog.this, "终端号只能有数字", Toast.LENGTH_SHORT).show();
                    }

                }

            }else if(id == R.id.bandDevice_cancel_button)
            {
                Intent intent = new Intent();
                setResult(RESULT_ERR, intent);   //取消绑定
                finish();
            }
        }
    }
}
