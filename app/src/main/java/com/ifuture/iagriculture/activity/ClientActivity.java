package com.ifuture.iagriculture.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ifuture.iagriculture.bottombar.BottomBarPanel;
import com.ifuture.iagriculture.service.IHomeService;
import com.ifuture.iagriculture.R;

/** 
 * @CopyRight: 王辰浩 2015~2025
 * @Author Feather Hunter(猎羽)
 * @qq: 975559549
 * @Version: 1.0 
 * @Date: 2015/12/25
 * @Description: 登陆界面,用于输入账号密码后启动后台Service进行身份认证登录。
 *        详细项目介绍：
 *        1.onCreate设置好各个组件
 *        2.监听到登陆按键后，启动后台Service和Receiver，在登陆成功后跳转到ClientMainActivity的主控界面
 *       
 * @FunctionList:
 *      1. void onCreate 					//初始化组件，并且准备dialog
 *      2. LoginButtonListener  			//监听登录建，动态注册Receiver，并且启动后台Service服务
 *      3. Runnable loginOvertimeRunnable 	//用于连接超时时候关闭dialog
 *      4. AuthReceiver         			//接收广播，处理登陆成功和登录失败的情况。
 *      5. void onDestroy()     			//解除Receiver，并且关闭后台服务
 * @history:
 *    v1.0 完成基础登录的功能
 *
 * @Debug: 1.第二次登陆后验证成功却依然卡在ClientActivity的界面，是因为没有解除isOnPaused的屏蔽作用
 **/

public class ClientActivity extends Activity {

	private ImageView logoImageView;
	private EditText client_account, client_password;
	private Button client_login, client_bluetooth;
	private BottomBarPanel bottomBarPanel;
	
	private String accountString;     //账户
	private String passwordString;    //密码
	private Boolean isAuthed = false;
	private Boolean isOnPaused = false;
	private Boolean isDealLoginError = false;
	private String AUTH_ACTION = "android.intent.action.ANSWER";
	
	private Handler handler = new Handler();
	private AuthReceiver authReceiver = null;//广播接收器
	private ProgressDialog dialog; //登录的进度条
	private boolean firstSwitch = true;//第一次转换到ClientMainActivity，才需要刷新界面,
									//可能同时收到多个登陆成功广播，导致多次切换
	
	private Intent serviceIntent; //服务Intent
	private WifiManager wifiManager; //优先开启wifi模式
	private Toast toast;             //自定义Toast

	private TextView icon_text = null;
	//IHomeService.ServiceBinder serviceBinder;//IHomeService中的binder
	SharedPreferences apSharedPreferences = null; //用于保存和获取账号和密码
	/**
	 *  @Function:void onCreate
	 *  @author:Feather Hunter
	 *  @Description:
	 *  	获得各个组件ID，并且初始化dialog（用于登录）
	 *  @calls:
	 *     1. new LoginButtonListener(); //登录键监听
	 *  @Input:
	 *  @Return:
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //状态栏
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		
		logoImageView = (ImageView)findViewById(R.id.icar_logo);
		icon_text = (TextView)findViewById(R.id.igreens_name);
		/* *
		 *  设置产品logo文字的字体以及加粗
		 * */
		Typeface type = Typeface.createFromAsset(getAssets(), "kaiti.ttf");
		icon_text.setTypeface(type); //设置字体
		icon_text.setText("爱绿");
		icon_text.getPaint().setFakeBoldText(true);//加粗

		/* *
		 *  获取登录等按键
		 * */
		client_account = (EditText) findViewById(R.id.client_account);
		client_password = (EditText) findViewById(R.id.client_password);
		client_login = (Button) findViewById(R.id.client_login);
		client_bluetooth = (Button) findViewById(R.id.client_bluetooth);
		client_login.setOnClickListener(new LoginButtonListener());
		client_bluetooth.setOnClickListener(new BluetoothButtonListener());
		//client_bluetooth.setVisibility(View.GONE );

		/* -------------------------------------------------------
		 *  打开wifi的请求
		 * -------------------------------------------------------*/
		wifiManager = (WifiManager) ClientActivity.this.getSystemService(Service.WIFI_SERVICE);
		if(wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED)//wifi没有打开
		{
			wifiManager.setWifiEnabled(true);//打开wifi
		}
		toast = Toast.makeText(getApplicationContext(), "tip:和终端在同一WIFI内不消耗流量", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 0);//toast显示在上方
		toast.show();

		/* -------------------------------------------------------
		 *  通过SharedPreferences获取保存的账号和密码
		 * -------------------------------------------------------*/
		apSharedPreferences = getSharedPreferences("saved", Activity.MODE_PRIVATE);
		accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
		passwordString = apSharedPreferences.getString("password", "");
		/*设置账号密码*/
		client_account.setText(accountString);
		client_password.setText(passwordString);

		/* -------------------------------------------------------
		 *  准备"正在登陆..."的提示框
		 * -------------------------------------------------------*/
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在登录中...");
		dialog.setCancelable(false);
	}
    /**
	 *  @Class:LoginButtonListener
	 *  @author:Feather Hunter
	 *  @Description:
	 *  	登录按键监听，动态注册authReceiver用于接受后台Service发送的广播
	 *  	启动了Service并且显示了dialog，开启了超市倒计时计数器。在一定时间
	 *  	内未登录成功，会提示失败。
	 */
    class LoginButtonListener implements OnClickListener
    {
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
    		accountString = client_account.getText().toString();
    		passwordString = client_password.getText().toString();

			/* *
		 	 *  检查是否输入账号和密码
		 	 * */
			if(accountString.equals("") || passwordString.equals(""))
			{
				Toast.makeText(ClientActivity.this,"请输入账户/密码", Toast.LENGTH_SHORT).show();
				return;
			}
    		/*动态注册receiver*/
    		authReceiver = new AuthReceiver();
    		IntentFilter filter = new IntentFilter();
    		filter.addAction(AUTH_ACTION);
    		registerReceiver(authReceiver, filter);//注册
    		
    		/*绑定service, 利用connection建立与service的联系*/
    		serviceIntent = new Intent();
    		serviceIntent.putExtra("command", "auth");
    		serviceIntent.putExtra("account", accountString);
    		serviceIntent.putExtra("password", passwordString);
    		serviceIntent.setClass(ClientActivity.this, IHomeService.class);

    		//bindService(serviceIntent, connection, BIND_AUTO_CREATE); //绑定service,并且自动创建service
    		startService(serviceIntent); //开启服务
    		dialog.show(); //显示登陆进度条
			isDealLoginError = false;
    		/*超时处理*/
    		Thread thread = new Thread(loginOvertimeRunnable);
    		thread.start();
    	}	
    }

	/**
	 *  @Class: BluetoothButtonListener
	 *  @author: 王辰浩
	 *  @Description: 用于直接进入测试模式，不连接服务器。
	 */
    class BluetoothButtonListener implements OnClickListener
    {
    	public void onClick(View v) {
			accountString = client_account.getText().toString();
			passwordString = client_password.getText().toString();

			SharedPreferences.Editor editor = apSharedPreferences.edit();//用putString的方法保存数据
			editor.putString("account", accountString);
			editor.putString("password", passwordString);//提交当前数据
			editor.commit();

			Intent intent = new Intent();
			intent.setClass(ClientActivity.this, ClientMainActivity.class);
			ClientActivity.this.startActivity(intent);

//			Intent intent = new Intent();
//			intent.setClass(ClientActivity.this, DatabaseTestActivity.class);
//			ClientActivity.this.startActivity(intent);
    	}	
    }
    
    /**
	 *  @Object: loginOvertimeRunnable
	 *  @Description:
	 *  	启动登陆后的计时功能，在一定时间内没有连接成功则一定失败了
	 */
    Runnable loginOvertimeRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			isDealLoginError = false;  //没有处理过错误
			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/* *******************************************************
		 	 *  一定时间后 没有认证成功&&没有处理登录错误：提示登录超时
		 	 * *******************************************************/
			if ((isAuthed == false) && (isDealLoginError == false)) {

				dialog.dismiss();
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(ClientActivity.this, "登陆超时", Toast.LENGTH_SHORT).show();
						stopService();
					}
				});//end of handler

			}//end of if
		}//end of run
	};

	/**
	 *  @Function: private void stopService()
	 *  @Description:
	 *  	向后台Service发送停止所有线程的命令
	 *      并且关闭Service
	 */
	private void stopService()
	{
		serviceIntent = new Intent();
		serviceIntent.putExtra("command", "stop");
		serviceIntent.setClass(ClientActivity.this, IHomeService.class);
		startService(serviceIntent); //发送停止指令
		stopService(serviceIntent);  //关闭后台连接服务
	}
	
	/**
	 *  @Class: AuthReceiver
	 *  @Description:
	 *  	若登陆成功,发送登录模式给ClientMainActivity,并且切换到ClientMainActivity
	 */
	private class AuthReceiver extends BroadcastReceiver{

		int failed_conter; //多次接到失败信息只显示一次
		public AuthReceiver() {
			// TODO Auto-generated constructor stub
			failed_conter = 0; //初始化
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			  /* -------------------------------------------------------
		 	   *  处理Service返回的登录结果信息
		 	   * -------------------------------------------------------*/
			  String resultString = intent.getStringExtra("result");
			  if(resultString != null) //不为空
			  {
				  /* -------------------------------------------------------
		 	   	   *  登陆后从主界面返回，不考虑onPaused的屏蔽作用
		 	   	   * -------------------------------------------------------*/
				  if(resultString.equals("relogin"))
				  {
					 /*要开始重新登录*/
					  System.out.println("========================relogin======================");
					  firstSwitch = true;
					  isOnPaused = false;
					  stopService();//结束服务
				  }
				  /* -------------------------------------------------------
		 	   	   *  处于暂停状态不接收广播
		 	   	   * -------------------------------------------------------*/
				  if(isOnPaused == true)
				  {
					  return;
				  }
				  if(resultString.equals("res_login"))
				  {
					  if(intent.getStringExtra("res_login").equals("success"))
					  {
						  if(firstSwitch == false) return;
						  firstSwitch = false;

						/* -------------------------------------------------------
		 	   			 *  登陆成功保存账号和密码
		 	   			 * -------------------------------------------------------*/
//						apSharedPreferences = getSharedPreferences("saved",
//								Activity.MODE_PRIVATE);//实例化SharedPreferences.Editor对象（第二步）
						  SharedPreferences.Editor editor = apSharedPreferences.edit();//用putString的方法保存数据
						  editor.putString("account", accountString);
						  editor.putString("password", passwordString);//提交当前数据
						  editor.commit();

						/*切换到主控界面*/
						  Intent tempIntent = new Intent();

						  tempIntent.putExtra("mode", 1);//选择模式：2为蓝牙模式
						  tempIntent.putExtra("account", "");
						  tempIntent.setClass(ClientActivity.this, ClientMainActivity.class);
						  ClientActivity.this.startActivity(tempIntent);

						  isAuthed = true;
						  failed_conter = 0; //清除失败显示计数器

						  dialog.dismiss(); //登陆成功，解除进度条
					  }
					  else if(intent.getStringExtra("res_login").equals("failed"))
					  {
						  if(isDealLoginError == false) //登录失败
						  {
							  isDealLoginError = true;  //完成登陆错误处理，超时定时器不需要重复显示
							  Toast.makeText(ClientActivity.this, "账号/密码验证失败", Toast.LENGTH_SHORT).show();
							  dialog.dismiss(); //解除进度条
						  }
					  }
					  else if(intent.getStringExtra("res_login").equals("connect error"))
					  {
						  System.out.println("connect error");
						  firstSwitch = true;
						  stopService();//结束服务
						  if(isDealLoginError == false) //登录失败
						  {
							  isDealLoginError = true;  //完成登陆错误处理，超时定时器不需要重复显示
							  Toast.makeText(ClientActivity.this, "连接服务器失败:请检查网络或服务器正在维护", Toast.LENGTH_LONG).show();
							  dialog.dismiss(); //解除进度条
						  }
					  }
				  }
				  else if(resultString.equals("res_internet"))
				  {
					  if(intent.getStringExtra("res_internet").equals("disconnect"))
					  {
						  System.out.println("Client res_internet");
						  firstSwitch = true;
						  stopService();//结束服务
						  if(isDealLoginError == false) //登录失败
						  {
							  isDealLoginError = true;  //完成登陆错误处理，超时定时器不需要重复显示
							  Toast.makeText(ClientActivity.this, "网络不可用，请检查相关设置", Toast.LENGTH_SHORT).show();
							  dialog.dismiss(); //解除进度条
						  }
					  }
				  }
			  }//if result = null

		}//end of onReceive
	}

	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 0, 0, "退出");
    	menu.add(1, 1, 1, "设置");
    	menu.add(2, 2, 2, "帮助");
        //getMenuInflater().inflate(R.menu.client, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
	@Override
	protected void onPause()
	{
		super.onPause();
		System.out.println("ClientActivity onPause");
		isOnPaused = true;

	}
	protected void onRestart()
	{
		super.onRestart();
		System.out.println("ClientActivity onRestart");
		isOnPaused = false;

	}
	/**
	 *  @Function: protected void onDestroy()
	 *  @Description:
	 *  	销毁activity时，接触广播接收器的注册，并且停止后台Service
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(authReceiver != null)
		{
			unregisterReceiver(authReceiver); //解除receiver的注册
		}
		/*停止后台服务*/
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(ClientActivity.this, IHomeService.class);
		stopService(serviceIntent);
		//unbindService(connection);//解除绑定
	}

}