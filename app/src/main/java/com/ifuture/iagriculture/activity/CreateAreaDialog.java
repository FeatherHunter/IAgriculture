package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;


/**
 * @CopyRight: 王辰浩 2016~2026
 * @Author Feather Hunter(猎羽)
 * @Version: 1.0
 * @Date: 2016/4/10
 * @Description:
 * 		  在主界面点击创建地区后进入的dialog版的activity，用于创建地区号。
 *
 * @Function List:
 *      1.
 * @history:
 *    v1.0
 **/
public class CreateAreaDialog extends Activity {


    ButtonRectangle checkButton, cancelButton;
    TextView deviceNumText = null;
    EditText deviceStringText = null;

    int device_num;

    DatabaseOperation databaseOperation = null;

    private int RESULT_OK = 1;
    private int RESULT_ERR = 0;


    /* ---------------------------------------------------------------------
	 *               获取控件，得到activity传来的地区号，设置监听器
	 * ---------------------------------------------------------------------*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //去除标题栏
        setContentView(R.layout.activity_create_area_dialog);

        /* -----------------------------------------------------------------
	     *             利用用户名创建or获得数据库
	     * -----------------------------------------------------------------*/
        SharedPreferences apSharedPreferences = getSharedPreferences("saved", Activity.MODE_PRIVATE);
        String accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
        databaseOperation = new DatabaseOperation(accountString); //使用用户名创建数据库
        databaseOperation.createDatabase(this);//创建数据库

        device_num = getIntent().getIntExtra("area_number", -1);

        checkButton = (ButtonRectangle) findViewById(R.id.cArea_check_button);
        cancelButton = (ButtonRectangle) findViewById(R.id.cArea_cancel_button);
        deviceNumText = (TextView) findViewById(R.id.cArea_device_number);    //地区号
        deviceStringText = (EditText) findViewById(R.id.cArea_device_name);   //地区名
        deviceNumText.setText(""+device_num);                                      //设置地区号

        checkButton.setOnClickListener(new buttonListener());
        cancelButton.setOnClickListener(new buttonListener());
    }

    class buttonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.cArea_check_button)
            {
                String context = deviceStringText.getText().toString();
                if(context.equals("")) //为空
                {
                    Toast.makeText(CreateAreaDialog.this, "地区名不能为空", Toast.LENGTH_SHORT).show();
                }
                else if(context.length() > 20)
                {
                    Toast.makeText(CreateAreaDialog.this, "地区名太长了，试着买个10寸的手机吧", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    databaseOperation.insertArea(CreateAreaDialog.this, device_num, context); //增加地区
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);   //创建成功
                    finish();
                }

            }else if(id == R.id.cArea_cancel_button)
            {
                Intent intent = new Intent();
                setResult(RESULT_ERR, intent);   //取消创建
                finish();
            }
        }
    }
}
