package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.ifuture.carcontrl_client.R;

public class AirControler extends Activity implements NumberPicker.OnValueChangeListener, NumberPicker.OnScrollListener,NumberPicker.Formatter {

    NumberPicker tempPicker = null;
    NumberPicker windPicker = null;

    Button tempCheckButton = null;
    Button windCheckButton = null;
    Button workModeButton  = null;
    Button iceButton = null;
    Button inAirButton = null;
    Button powerButton = null;
//    Button windHeadButton = null, windTailButton = null, windHTButton = null, windTRmIceButton = null, windRmIceButton = null;
    Button windDireModeButton = null;
    TextView iceTextView = null;
    TextView inAirTextView = null;
    TextView workModeTextView = null;
    TextView windDirTextView = null;
    TextView windValueTextView = null;
    TextView tempValueTextView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_air_controler);

        tempPicker = (NumberPicker)findViewById(R.id.temp_picker);
        windPicker = (NumberPicker)findViewById(R.id.wind_picker);
        initTempPicker();
        iceTextView = (TextView) findViewById(R.id.iceMode_value);    //get ice mode value
        inAirTextView = (TextView) findViewById(R.id.inAirMode_value);//get inair mode value
        workModeTextView = (TextView) findViewById(R.id.workMode_value); //get workmode
        windDirTextView = (TextView) findViewById(R.id.windDir_value);
        windValueTextView = (TextView) findViewById(R.id.wind_value); //风力大小
        tempValueTextView = (TextView) findViewById(R.id.temp_value); //温度大小
        /*设置温度确认按键*/
        tempCheckButton = (Button) findViewById(R.id.temp_check);
        tempCheckButton.setOnClickListener(new tempCheckButtonListener());

        /*设置风力确认按键*/
        windCheckButton = (Button) findViewById(R.id.wind_check);
        windCheckButton.setOnClickListener(new windCheckButtonListener());

        /*工作模式按键*/
        workModeButton = (Button) findViewById(R.id.work_mode);
        workModeButton.setOnClickListener(new workModeBuButtonListener());

        /*制冷模式按键*/
        iceButton = (Button) findViewById(R.id.ice_mode);
        iceButton.setOnClickListener(new iceButtonListener());

        /*进气模式按键*/
        inAirButton = (Button) findViewById(R.id.air_mode);
        inAirButton.setOnClickListener(new inAirButtonListener());

        /*开关空调*/
        powerButton = (Button) findViewById(R.id.power_onauto_off);
        powerButton.setOnClickListener(new powerOnButtonListener());

        /*风向模式*/
        windDireModeButton = (Button) findViewById(R.id.wind_dir_mode);
        windDireModeButton.setOnClickListener(new windDirButtonListener());
//        windHeadButton = (Button) findViewById(R.id.wind_dir_head);
//        windTailButton = (Button) findViewById(R.id.wind_dir_tail);
//        windHTButton = (Button) findViewById(R.id.wind_dir_ht);
//        windTRmIceButton = (Button) findViewById(R.id.wind_dir_tailrmice);
//        windRmIceButton = (Button) findViewById(R.id.wind_dir_removeice);
//
//        windHeadButton.setOnClickListener(new windDirButtonListener());
//        windTailButton.setOnClickListener(new windDirButtonListener());
//        windHTButton.setOnClickListener(new windDirButtonListener());
//        windTRmIceButton.setOnClickListener(new windDirButtonListener());
//        windRmIceButton.setOnClickListener(new windDirButtonListener());

    }


    /**
     * @Function: private void sendMsg2Service(String msg);
     * @Description: 将需要发送给服务器的信息发送给Service，通过Service转发给服务器
     *                  使用广播(broadcast)与Service通信
     * @Input: String msg; 需要发送的信息
     */
    private void sendMsg2Service(String msg)
    {
        /*发送广播给Service，让其发送信息给服务器*/
        Intent intent = new Intent();
        intent.putExtra("type", "send");
        intent.putExtra("send", msg);
        intent.setAction(intent.ACTION_MAIN);
        AirControler.this.sendBroadcast(intent);
    }

    /**
     * @Class: tempCheckButtonListener
     * @Description: 将用户选择的温度发送给服务器
     */
    class tempCheckButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int tempValue = tempPicker.getValue();

            switch (tempValue)
            {
//                case 32: windValueTextView.setTextColor(getResources().getColor(R.color.temp_32));break;
//                case 31: windValueTextView.setTextColor(getResources().getColor(R.color.temp_31));break;
//                case 30: windValueTextView.setTextColor(getResources().getColor(R.color.temp_30));break;
//                case 29: windValueTextView.setTextColor(getResources().getColor(R.color.temp_29));break;
//                case 28: windValueTextView.setTextColor(getResources().getColor(R.color.temp_28));break;
//                case 27: windValueTextView.setTextColor(getResources().getColor(R.color.temp_27));break;
//                case 26: windValueTextView.setTextColor(getResources().getColor(R.color.temp_26));break;
//                case 25: windValueTextView.setTextColor(getResources().getColor(R.color.temp_25));break;
//                case 24: windValueTextView.setTextColor(getResources().getColor(R.color.temp_24));break;
//                case 23: windValueTextView.setTextColor(getResources().getColor(R.color.temp_23));break;
//                case 22: windValueTextView.setTextColor(getResources().getColor(R.color.temp_22));break;
//                case 21: windValueTextView.setTextColor(getResources().getColor(R.color.temp_21));break;
//                case 20: windValueTextView.setTextColor(getResources().getColor(R.color.temp_20));break;
//                case 19: windValueTextView.setTextColor(getResources().getColor(R.color.temp_19));break;
//                case 18: windValueTextView.setTextColor(getResources().getColor(R.color.temp_18));break;
//                case 17: windValueTextView.setTextColor(getResources().getColor(R.color.temp_17));break;
//                case 16: windValueTextView.setTextColor(getResources().getColor(R.color.temp_16));break;
                case 32: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_32));break;
                case 31: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_31));break;
                case 30: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_30));break;
                case 29: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_29));break;
                case 28: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_28));break;
                case 27: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_27));break;
                case 26: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_26));break;
                case 25: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_25));break;
                case 24: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_24));break;
                case 23: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_23));break;
                case 22: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_22));break;
                case 21: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_21));break;
                case 20: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_20));break;
                case 19: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_19));break;
                case 18: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_18));break;
                case 17: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_17));break;
                case 16: tempValueTextView.setTextColor(ContextCompat.getColor(AirControler.this, R.color.temp_16));break;
            }
            tempValueTextView.setText(""+tempValue);
            String msg = Instruction.TEMP_CTRL+(char)tempValue;
            sendMsg2Service(msg);
            Toast.makeText(AirControler.this, "设置温度"+msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @Class: windCheckButtonListener
     * @Description: 将用户选择的风力发送给服务器
     */
    class windCheckButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int windValue = windPicker.getValue();
            windValueTextView.setText(""+windValue);
            String msg = Instruction.WIND_CTRL+(char)windValue;
            sendMsg2Service(msg);
            Toast.makeText(AirControler.this, "设置风力"+msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @Class: iceButtonListener
     * @Description: 开启/关闭 制冷
     */
    class iceButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(iceTextView.getText().equals("制冷"))
            {
                iceTextView.setText("关闭");
                String msg = Instruction.ICE_CTRL+(char)Instruction.ICE_OFF;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前制冷模式:关闭"+msg, Toast.LENGTH_SHORT).show();
            }
            else
            {
                iceTextView.setText("制冷");
                String msg = Instruction.ICE_CTRL+(char)Instruction.ICE_ON;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前制冷模式:制冷"+msg, Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * @Class: inAirButtonListener
     * @Description: 进风模式切换：内循环/外循环
     */
    class inAirButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(inAirTextView.getText().equals("内循环"))
            {
                inAirTextView.setText("外循环");
                String msg = Instruction.AIR_CTRL+(char)Instruction.AIR_OUTCYCLE;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前进气模式:外循环"+msg, Toast.LENGTH_SHORT).show();
            }
            else
            {
                inAirTextView.setText("内循环");
                String msg = Instruction.AIR_CTRL+(char)Instruction.AIR_INCYCLE;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前进气模式:内循环"+msg, Toast.LENGTH_SHORT).show();

            }
        }
    }
    /**
     * @Class: workModeBuButtonListener
     * @Description: 工作模式切换：自动和手动
     */
    class workModeBuButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(workModeTextView.getText().equals("自动"))
            {
                workModeTextView.setText("手动");
                String msg = Instruction.WORK_CTRL+(char)Instruction.WORK_MODE_MANU;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前工作模式:手动"+msg, Toast.LENGTH_SHORT).show();
            }
            else
            {
                workModeTextView.setText("自动");
                String msg = Instruction.WORK_CTRL+(char)Instruction.WORK_MODE_AUTO;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前工作模式:自动"+msg, Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * @Class: powerOnButtonListener
     * @Description: 电源按键：开启(默认为自动模式)/关闭
     */
    class powerOnButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(powerButton.getText().equals("开启"))
            {
                powerButton.setText("OFF");
                workModeTextView.setText("自动");
                String msg = Instruction.WORK_CTRL+(char)Instruction.WORK_MODE_AUTO;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "开启:自动模式"+msg, Toast.LENGTH_SHORT).show();
            }
            else
            {
                powerButton.setText("开启");
                String msg = Instruction.WORK_CTRL+(char)Instruction.WORK_MODE_OFF;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "关闭"+msg, Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * @Class: windDirButtonListener
     * @Description: 改变风向模式。共五种模式: 吹头，吹脚，吹头/吹脚，吹脚/除霜，除霜
     */
    class windDirButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(workModeTextView.getText().equals("除霜"))
            {
                workModeTextView.setText("吹头");
                String msg = Instruction.WINDDIR_CTRL+(char)Instruction.WIND_HEAD;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前吹风模式:吹头"+msg, Toast.LENGTH_SHORT).show();
            }
            else if(workModeTextView.getText().equals("吹头"))
            {
                windDirTextView.setText("吹头/吹脚");
                String msg = Instruction.WINDDIR_CTRL+(char)Instruction.WIND_HT;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前吹风模式:吹头/吹脚"+msg, Toast.LENGTH_SHORT).show();
            }
            else if(workModeTextView.getText().equals("吹头/吹脚"))
            {
                windDirTextView.setText("吹脚");
                String msg = Instruction.WINDDIR_CTRL+(char)Instruction.WIND_TAIL;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前吹风模式:吹脚"+msg, Toast.LENGTH_SHORT).show();
            }
            else if(workModeTextView.getText().equals("吹脚"))
            {
                windDirTextView.setText("吹脚/除霜");
                String msg = Instruction.WINDDIR_CTRL+(char)Instruction.WIND_TAIL_RMICE;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前吹风模式:吹脚/除霜"+msg, Toast.LENGTH_SHORT).show();
            }
            else if(workModeTextView.getText().equals("吹脚/除霜"))
            {
                windDirTextView.setText("除霜");
                String msg = Instruction.WINDDIR_CTRL+(char)Instruction.WIND_REMOVE_ICE;
                sendMsg2Service(msg);
                Toast.makeText(AirControler.this, "当前吹风模式:除霜"+msg, Toast.LENGTH_SHORT).show();
            }
//            if(v == windHeadButton)
//            {
//                workModeTextView.setText("吹头");
//                String msg = Instruction.WINDDIR_CTRL+(char)Instruction.WIND_HEAD;
//                sendMsg2Service(msg);
//                Toast.makeText(AirControler.this, "当前吹风模式:吹头"+msg, Toast.LENGTH_SHORT).show();
//            }
//            else if(v == windTailButton)
//            {
//                windDirTextView.setText("吹脚");
//                String msg = Instruction.WINDDIR_CTRL+(char)Instruction.WIND_TAIL;
//                sendMsg2Service(msg);
//                Toast.makeText(AirControler.this, "当前吹风模式:吹脚"+msg, Toast.LENGTH_SHORT).show();
//
//            }
//            else if(v == windHTButton)
//            {
//                windDirTextView.setText("吹头/吹脚");
//                String msg = Instruction.WINDDIR_CTRL+(char)Instruction.WIND_HT;
//                sendMsg2Service(msg);
//                Toast.makeText(AirControler.this, "当前吹风模式:吹头/吹脚"+msg, Toast.LENGTH_SHORT).show();
//
//            }
//            else if(v == windTRmIceButton)
//            {
//                windDirTextView.setText("吹脚/除霜");
//                String msg = Instruction.WINDDIR_CTRL+(char)Instruction.WIND_TAIL_RMICE;
//                sendMsg2Service(msg);
//                Toast.makeText(AirControler.this, "当前吹风模式:吹脚/除霜"+msg, Toast.LENGTH_SHORT).show();
//
//            }
//            else if(v == windRmIceButton)
//            {
//                windDirTextView.setText("除霜");
//                String msg = Instruction.WINDDIR_CTRL+(char)Instruction.WIND_REMOVE_ICE;
//                sendMsg2Service(msg);
//                Toast.makeText(AirControler.this, "当前吹风模式:除霜"+msg, Toast.LENGTH_SHORT).show();
//
//            }
        }
    }


    /**
     * @Function: private  void initTempPicker()
     * @Description: 初始化温度和风力的数字选择器
     */
    private  void initTempPicker()
    {
        tempPicker.setFormatter(this);
        tempPicker.setOnValueChangedListener(this);
        tempPicker.setMaxValue(32);
        tempPicker.setValue(22);
        tempPicker.setMinValue(16);

        windPicker.setFormatter(this);
        windPicker.setOnValueChangedListener(this);
        windPicker.setMaxValue(4);
        windPicker.setValue(0);
        windPicker.setMinValue(0);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
    }

    @Override
    public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch (scrollState) {
            case NumberPicker.OnScrollListener.SCROLL_STATE_FLING:
                Toast.makeText(this, "后续滑动(飞呀飞，根本停下来)", Toast.LENGTH_LONG)
                        .show();
                break;
            case NumberPicker.OnScrollListener.SCROLL_STATE_IDLE:
                Toast.makeText(this, "不滑动", Toast.LENGTH_LONG).show();
                break;
            case NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                Toast.makeText(this, "滑动中", Toast.LENGTH_LONG)
                        .show();
                break;
        }
    }

    @Override
    public String format(int value) {
        String tmpStr = String.valueOf(value);

        return tmpStr;
    }
}
