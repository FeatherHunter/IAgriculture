package com.ifuture.iagriculture.service;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.ifuture.iagriculture.Calendar.TodayTime;
import com.ifuture.iagriculture.Instruction.Instruction;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;
import com.ifuture.iagriculture.sqlite.DayDatabaseHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @CopyRight: 王辰浩 2015~2025
 * @qq:975559549
 * @Author Feather Hunter(猎羽)
 * @Version:2.10 (883)
 * @Date:2016/1/10
 * @Description: 后台service服务,用于与服务器之间的通信。支持wifi模式和ethernet
 *              并将收到的信息经过处理后，广播给各个activity。
 * @Function list:
 *     1. int onStartCommand(Intent intent, int flags, int startId)//处理其他activity发送过来的数据
 *     2. authRunnable           //认证线程，发送身份信息给服务器
 *     3. serverConnectRunnable  //连接服务器的线程
 *     4. allInfoFlushRunnable   //发送请求以得到温度等所有参数的数据
 *     5. revMsgRunnable         //无限循环接收服务器发来的信息
 *     6. void onDestroy()       //关闭socket
 * @History:
 *     v1.0  @date 2015/12/25 Service启动后，自动连接服务器，连接成功后，负责与服务器间通信。会自动短线重连
 *     v2.10 @date 2016/1/10  增加wifi内连接控制中心的功能，失败后尝试连接服务器。
 *     v2.24 @date 2016/1/20  重做指令解析，修复若干小bug
 **/

public class IHomeService extends Service{

	Socket serverSocket;
	OutputStream outputStream; //ouput to server
	InputStream inputStream;   //input from server
	
	String account, password;
	private boolean accountReady = false;  //是否获得了明确的用户帐户信息和密码
	
	public boolean isConnected = false;  //通用连接 是否连接成功
	private boolean isAuthed   = false;  //是否验证成功
	private boolean isTestWifi = false;  //正在测试wifi内能否连接上控制中心，此时停止TCP接受信息线程
	private boolean iswified   = false;  //是否wifi内连接控制中心成功
	private Boolean isDealLoginError = false;

	private boolean getTHthread = false;
	private boolean stopallthread = false; //停止所有线程
	private String message = null; //4096字节的指令缓冲区
	
	private ServiceReceiver serviceReceiver;
	private String SERVICE_ACTION = "android.intent.action.MAIN";

	private String IGServerIP = "192.168.191.1";
	//private String IGServerIP = "139.129.19.115";
	private int IGServerPort = 8080;
	private FileOutputStream jpegOutputStream = null;
	
	/*wifi模式相关*/
	private WifiManager wifiManager;

	DatabaseOperation databaseOperation = null; //数据库操作
	SharedPreferences apSharedPreferences = null;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @Function: public void onCreate();
	 * @Description:
	 *      1. 创建连接线程：用于wifi内直接连接控制中心或者通过ethernet连接服务器
	 *      2. 创建接受信息线程： 接受目标的信息
	 *      3. 创建更新温度湿度等数据的线程，同时作为心跳函数。
	 *      4. 动态注册receiver-接受来自其他模块的信息，并将其转发给服务器。
	 *      5. 获取wifimanager管理器。
	 *      6. 创建数据库，因为Service本身就是Context，所以参数为this
	 **/
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("service's onCreate");
		stopallthread = false; //允许所有线程
		Thread thread = new Thread(serverConnectRunnable);//连接服务器
		thread.start();	
		/*开启接受信息的线程*/
		thread = new Thread(revMsgRunnable);
		thread.start();
//		/*开启更新数据和心跳*/
//		thread = new Thread(getTempHumiRunnable);
//		thread.start();
		
		/*动态注册receiver*/
		serviceReceiver = new ServiceReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SERVICE_ACTION);
		registerReceiver(serviceReceiver, filter);//注册
		
		/*wifi相关*/
		wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);//获得wifi

		/* 创建数据库操作类，并且创建数据库
		 * */
		databaseOperation = new DatabaseOperation(account);
		databaseOperation.createDatabase(this);//创建数据库

		/* ------------------------------------------------------------------------------
		 *  若今天第一次打开，则要clear今天的数据库表(today)--通过SharedPreferences
		 * ---------------------------------------------------------------------------*/
		TodayTime todayTime = new TodayTime();
		todayTime.update();
		SharedPreferences apSharedPreferences = this.getSharedPreferences("today", Activity.MODE_PRIVATE);
		int year  = apSharedPreferences.getInt("year", 0);
		int month  = apSharedPreferences.getInt("month", 0);
		int day  = apSharedPreferences.getInt("day", 0);
		if((todayTime.getYear()==year)&&(todayTime.getMonth()==month)&&(todayTime.getDay()==day))
		{ //今天日期与today表日期相符合,不需要clear(不是第一次使用)
			System.out.println("今天日期与today表相符合，不需要clear");
		}else//不符合,clear，今天第一次使用
		{
			System.out.println("Clear TABLE today");
			databaseOperation = new DatabaseOperation(account);
			databaseOperation.clearTableToday(this);//清除数据库
			/*------------------------------------------------------------------------------
			 *  保存今天日期用于确定today表
			 *------------------------------------------------------------------------------*/
			apSharedPreferences = this.getSharedPreferences("today", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = apSharedPreferences.edit();//用putString的方法保存数据
			editor.putInt("year", todayTime.getYear());
			editor.putInt("month", todayTime.getMonth());//提交当前数据
			editor.putInt("day", todayTime.getDay());    //提交当前数据
			editor.commit();
			/* ----------------------------------------------------------------------------
		 	 *  今天第一次打开设置初始时间为0，用于today表数据转换为allday数据
		 	 * ---------------------------------------------------------------------------*/
			apSharedPreferences = getSharedPreferences("today_To_allday", Activity.MODE_PRIVATE);
			editor = apSharedPreferences.edit();//用putString的方法保存数据
			editor.putInt("hour", 0);
			editor.commit();
		}
	}
	/**
	 * @Function: int onStartCommand();
	 * @Description:
	 *      1. 用于接受登陆界面发送来的账号和密码信息用于登录。
	 **/
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent != null)
		{
			String commandString = intent.getStringExtra("command");
			if(commandString.equals("auth"))
			{
				account = intent.getStringExtra("account");
				password = intent.getStringExtra("password");
				accountReady = true; //用户信息准备好了
			}
			/*停止所有线程*/
			else if(commandString.equals("stop"))
			{
				System.out.println("stopallthread");
				stopallthread = true;
			}
			
		}
		return super.onStartCommand(intent, flags, startId);
	}
	/**
	 * @Function: void onDestroy();
	 * @Description:
	 *      用于销毁Service时候的收尾工作。
	 *      1. 关闭socket
	 *      2. 解除动态注册的Receiver 
	 **/
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(serverSocket != null)
		{
			try {
				serverSocket.close(); //关闭socket
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		unregisterReceiver(serviceReceiver); //解除Receiver
	}
	/**
	 * @Function: private void sendMsg(String msg);
	 * @Description:
	 *      用于向服务器发送字符串，转换为byte数组
	 * @Return: true  on success
	 *           false on error
	 **/
	private boolean sendMsg(String msg)
	{
		byte buffer[] = msg.getBytes();
		//String tempString = new String(buffer, 0, buffer.length);
		System.out.println("IHomeService SendMsg");

		try {
			outputStream.write(buffer, 0, buffer.length);
			outputStream.flush();

			/*显示发送的数据，用于测试*/
			String tempString = new String(buffer, 0, buffer.length);
			System.out.println("send Msg:"+tempString+"END");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isConnected = false; //连接失败
			isAuthed = false;    //认证失效
			return false;
		}
		return true;

	}

	/**
	 * @Function: public static boolean isNetworkAvailable(Context context)
	 * @Description: 检查当前网络是否可用
	 * @param context
	 * @return true of false
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
		} else {
			//如果仅仅是用来判断网络连接
			//则可以使用 cm.getActiveNetworkInfo().isAvailable();
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @Function: serverConnectRunnable;
	 * @Description:
	 *      用于连接服务器或者控制中心，并且断线的时候重新连接。
	 *      1. 关闭socket
	 *      2. 解除动态注册的Receiver
	 **/
	Runnable serverConnectRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if (stopallthread) {
					System.out.println("serverConnectRunnable stopallthread---1");
					break;
				}
				/*tcp连接成功,用户信息没有准备好,认证成功---不满足其中一项则进行处理*/
				while ((isConnected == true) && (!accountReady) && (isAuthed == true)) {
					System.out.println("serverConnectRunnable true true true");
					try {
						Thread.sleep(500);  //睡眠
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (stopallthread) { //终止所有线程的标志
					System.out.println("serverConnectRunnable stopallthread---2");
					break;
				}
				if (accountReady && (isConnected == false))//身份确定并且没有连接
				{
					/*正在重新连接*/
					Intent intent = new Intent();
					intent.setAction(intent.ACTION_EDIT);
					intent.putExtra("type", "disconnect");
					intent.putExtra("disconnect", "connecting");
					sendBroadcast(intent);

					/*之前有过socket连接,先关闭，再开启新的*/
					if (serverSocket != null) {
						try {
							serverSocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					/* --------------------------------------------------
					 *               检查网络是否存在问题
					 * --------------------------------------------------*/
					if(isNetworkAvailable(IHomeService.this)){
						System.out.println("正在连接服务器.....");
						/* --------------------------------------------------
					 	 * internet和wifi正常时进行重新连接和身份认证
					     * --------------------------------------------------*/
						Thread thread = new Thread(connectOvertimeRunnable);   //超时处理，如果长时间连接不上表示网络异常
						thread.start();
						isDealLoginError = false;
						try {
							serverSocket = new Socket(IGServerIP, IGServerPort);
						/*得到输入流、输出流*/
							outputStream = serverSocket.getOutputStream();
							inputStream = serverSocket.getInputStream();
							isConnected = true; //連接成功

							/*告诉activity连接成功*/
							intent.setAction(intent.ACTION_EDIT);
							intent.putExtra("type", "wifi_internet");
							intent.putExtra("wifi_internet", "connect");
							sendBroadcast(intent);

						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							isConnected = false; //連接失敗
							isDealLoginError = true;
							System.out.println("UnknownHostException e");
							try {
								Thread.sleep(500);  //失败后等待0.5s连接
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

							isConnected = false; //连接失败
							isDealLoginError = true;
							System.out.println("connect error!");
							Intent cintent = new Intent();
							cintent.setAction(cintent.ACTION_ANSWER);
							cintent.putExtra("result", "res_login");
							cintent.putExtra("res_login", "connect error");
							sendBroadcast(cintent);

							/*告诉activity 服务器可能进入维护*/
							intent.setAction(intent.ACTION_EDIT);
							intent.putExtra("type", "wifi_internet");
							intent.putExtra("wifi_internet", "error");
							sendBroadcast(intent);
							//stopallthread = true; //停止所有线程
							try {
								Thread.sleep(1000);  //失败后等待0.5s连接
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}else{
						System.out.println("网络不可用");
						if (stopallthread) { //终止所有线程的标志
							break;
						}
						/* --------------------------------------------------
					 	 * internet和wifi网络异常提示网络断开
					     * --------------------------------------------------*/
						System.out.println("internet/wifi error!");
						Intent cintent = new Intent();
						cintent.setAction(cintent.ACTION_ANSWER);
						cintent.putExtra("result", "res_internet");
						cintent.putExtra("res_internet", "disconnect");
						sendBroadcast(cintent);

						/*告诉主界面网络不可用*/
						intent.setAction(intent.ACTION_EDIT);
						intent.putExtra("type", "wifi_internet");
						intent.putExtra("wifi_internet", "disconnect");
						sendBroadcast(intent);
						isConnected = false; //连接失败
						isDealLoginError = true;

						try {
							Thread.sleep(1000);  //失败后等待1s连接
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}


				}//end of connecting server
				if (stopallthread) {
					break;
				}
				/*在连接成功和用户身份确定的时候，进行身份认证*/
				if (isConnected && accountReady && (isAuthed == false)) {
					System.out.println("正在重新验证" + isConnected + accountReady + isAuthed);
					/*通知activity正在验证信息*/
					Intent intent = new Intent();
					intent.setAction(intent.ACTION_EDIT);
					intent.putExtra("type", "disconnect");
					intent.putExtra("disconnect", "authing");
					sendBroadcast(intent);
					/*用户身份验证请求*/
					//发送账号密码
					if(!sendMsg(Instruction.loginMsg(account, password)))
					{
						try {
							Thread.sleep(2000);  //发送账号密码认证失败等待2.5s
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					try {
						Thread.sleep(1000);  //身份验证失败后等待1s
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}//end of 身份认证
			}
		}

	};

	/**
	 *  @Object: connectOvertimeRunnable
	 *  @Description:
	 *  	链接计时,超过一定时间没有连接上,则提示失败。
	 */
	Runnable connectOvertimeRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if((isConnected == false) && (isDealLoginError == false))
			{
				isConnected = false; //连接失败
				System.out.println("连接不上服务器");
				Intent cintent = new Intent();
				cintent.setAction(cintent.ACTION_ANSWER);
				cintent.putExtra("result", "res_login");
				cintent.putExtra("res_login", "connect error");
				sendBroadcast(cintent);

				/*告诉主界面 服务器异常*/
				Intent intent = new Intent();
				intent.setAction(intent.ACTION_EDIT);
				intent.putExtra("type", "wifi_internet");
				intent.putExtra("wifi_internet", "disconnect");
				sendBroadcast(intent);
				isConnected = false; //连接失败
			}

		}
	};

	Runnable revMsgRunnable = new Runnable() {

		byte[] buffer = new byte[8096];
		int count = 0;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			message = "";
			while (true) {
				if (stopallthread) {
					break;
				}
				while (isConnected == false)//断开连接，先等待重新链接
				{
					System.out.println("rev msg runnable is sleeeping");
					if (stopallthread) break;

					try {
						Thread.sleep(1000);//先休眠一秒等待链接
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (stopallthread) break;

				int temp = 0;
				try {
					/*得到服务器返回信息*/
					System.out.println("read block====================================:"+temp + " " + count);
					temp = inputStream.read(buffer);
					count++;
					System.out.println("recv:========================================="+temp + " " + count);
					if (temp <= 0) {
						if (temp == -1) {
							System.out.println("temo -1");
							isConnected = false;
							isAuthed = false;
						}
						continue;
					}
				} catch (IOException e) {
					e.printStackTrace();
					isConnected = false;
					isAuthed = false;
				}
				String revmsg = new String(buffer, 0, temp);
				message += revmsg;
				if(message.length() > 8086) //长度过长,直接遗弃
				{
					message = "";
					continue;     //继续等待接收
				}

				System.out.println(message);

				int index = 0;
				int msgLength  = message.length();
				while(index< msgLength)//处理接收到的数据中所有完整的指令，末尾的指令如果不完整等待下一个完整指令
				{
					index = 0;
					msgLength  = message.length();
					/*找到第一个HEAD*/
					while(index < msgLength)
					{
						if(message.charAt(index) == Instruction.CMD_HEAD) break;
						index++;
					}
					if(index != 0) //抛弃指令头的垃圾数据
					{
						if(index == msgLength)//直到数据的结尾都没找到
						{
							message = "";
							break;
						}
						message = message.substring(index);
						continue;
					}

					index += 1;  //跳过第一个CMD_HEAD字节
					if(index >= msgLength) break;

					/**
					 * 结果大类指令:
					 * 	分为如下小类指令：
					 * 	   1. 登录结果指令 RES_LOGIN
					 **/
					if(message.charAt(index) == Instruction.CMD_RES) //检查主要大类指令
					{
						index += 1;
						if(index >= msgLength) break;
						if(message.charAt(index) == Instruction.RES_LOGIN) //检查次类指令
						{
							index += 1;
							if(index >= msgLength) break;
							int res = dealResLogin(message, index); //处理登录信息
							//System.out.println("循环！"+ index + " " + msgLength + " " + res);
							if(res > 0)//正确返回截短
							{
								index += res;
								message = message.substring(index);
							}
							else if(res == 0)//正好处理完
							{
								message = "";
								break; //结束
							}
							//res = -1, 指令不全，什么都不做等待下一部分
						}
						else if(message.charAt(index) == Instruction.RES_TEMP) //检查次类指令
						{
							index += 1;
							if(index >= msgLength) break;
							int res = dealResTemp(message, index); //处理返回的温度信息
							if(res > 0)//正确返回截短
							{
								System.out.println("正确返回截短 " + res);
								index += res;
								message = message.substring(index);
							}
							else if(res == 0)//正好处理完
							{
								System.out.println("正好处理完 " + res);
								message = "";
								break; //结束
							}
							else if(res == -2) break;
							else
							{
								System.out.println("为-1 " + res);
							}
							//res = -1, 指令不全，什么都不做等待下一部分
						}
						else
						{
													/*找到END或者HEAD*/
							while(index < msgLength)
							{
								if((message.charAt(index) == Instruction.CMD_HEAD) || (message.charAt(index) == Instruction.CMD_END) )
								{
									message = message.substring(index);
									break;
								}
								index++;
							}//

						}
					}
					else //什么指令都不是
					{
						/*找到END或者HEAD*/
						while(index < msgLength)
						{
							if((message.charAt(index) == Instruction.CMD_HEAD) || (message.charAt(index) == Instruction.CMD_END) )
							{
								message = message.substring(index);
								break;
							}
							index++;
						}
					}
				}//end of one cmd

			}
		}
	};

	/**
	 * 	 @Function: private int dealResTemp(String msg, int index)
	 * 	 @Input:  String msg:需要处理的信息
	 * 	           int dex:   msg中的偏移量
	 *   @Return: -1: 指令不全 0: 正好处理完全 >0：在String中偏移的值
	 * */
	private int dealResTemp(String msg, int index)
	{
		String areaString;
		String greenHouseString;
		String deviceString;
		String tempString;
		String timeString;
		int i = index;
		int msgLength = msg.length();
		int startIndex;

		/*=======获取地区号===============*/
		startIndex = i;
		while(i < msgLength)
		{
			if(msg.charAt(i) == Instruction.CMD_SEP) break;
			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
			i++;
		}
		if((i >= msgLength) || (i+1 >= msgLength)) return - 1; //错误
		i++;
		areaString = msg.substring(startIndex, i-1); //获取地区号
		System.out.println("get areaString："+areaString);
		/*=======获取大棚号===============*/
		startIndex = i;
		while(i < msgLength)
		{
			if(msg.charAt(i) == Instruction.CMD_SEP) break;
			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
			i++;
		}
		if((i >= msgLength) || (i+1 >= msgLength)) return - 1; //错误
		i++;
		greenHouseString = msg.substring(startIndex, i-1); //获取设备ID号
		System.out.println("get greenHouseString："+greenHouseString);

		/*=======获取设备号===============*/
		startIndex = i;
		while(i < msgLength)
		{
			if(msg.charAt(i) == Instruction.CMD_SEP) break;
			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
			i++;
		}
		if((i >= msgLength) || (i+1 >= msgLength)) return - 1; //错误
		i++;
		deviceString = msg.substring(startIndex, i-1); //获取设备号
		System.out.println("get deviceString："+deviceString);

		/*=======获取温度值===============*/
		startIndex = i;
		while(i < msgLength)
		{
			if(msg.charAt(i) == Instruction.CMD_SEP) break;
			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
			i++;
		}
		if((i >= msgLength) || (i+1 >= msgLength)) return - 1; //错误
		i++;
		tempString = msg.substring(startIndex, i-1); //获取温度值
		System.out.println("get tempString："+tempString);
		/*=======获取时间===============*/
		startIndex = i;
		while(i < msgLength)
		{
			if(msg.charAt(i) == Instruction.CMD_END) break;
			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
			i++;
		}
		timeString = msg.substring(startIndex, i); //获取时间
		System.out.println("timeString："+timeString.length());
		/* -------------------------------------------------------
		 *  拆解事件数据，保存到数据库中
		 * -------------------------------------------------------*/
		if(timeString.length() == 6)
		{
			int year   = timeString.charAt(0);
			int month  = timeString.charAt(1);
			int day    = timeString.charAt(2);
			int hour   = timeString.charAt(3);
			int mintue = timeString.charAt(4);
			int second = timeString.charAt(5);
			System.out.println("" + year + "/" + month + "/" + day + " " + hour + ":" + mintue + ":" + second);


			System.out.println("tempString= " + tempString);
			databaseOperation.insertToday(this, deviceString, hour, mintue, second, Float.parseFloat(tempString), DayDatabaseHelper.temperature);//插入温度
			/* -------------------------------------------------------
	     	 *  通过SharedPreferences保存实时温度数据
	     	 *  用于fragment切换时候的数据保存
		     * -------------------------------------------------------*/
			apSharedPreferences = getSharedPreferences("tempdata", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = apSharedPreferences.edit();//用putString的方法保存数据
			editor.putString("temperature", tempString);
			editor.commit();
			broadcastUpdateTemp(areaString, greenHouseString, deviceString, tempString);//将温度数据广播出去（如具体大棚数据的fragment）

			/* -------------------------------------------------------
	     	 *  将today表数据添加到allday表中
		     * -------------------------------------------------------*/
			apSharedPreferences = getSharedPreferences("today_To_allday", Activity.MODE_PRIVATE);
			int lasthour = apSharedPreferences.getInt("hour", 25);
			TodayTime nowTime = new TodayTime();
			nowTime.update();
			int nowhour = nowTime.getHour();
			if(lasthour < nowhour)
			{
				DatabaseOperation tempOperation = new DatabaseOperation(account);
				tempOperation.switchTodayToAllday(this, deviceString, lasthour, nowhour);
			}
		}

//		for(int count = 2; count > 0; count--)
//		{
//			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
//			i++;
//		}
		if(i > msgLength) return -1; //越界
		if(i == msgLength) return 0;
		return i - index;
	}

	/**-------------------------------------------------------------------
	 * 	 @Function: private void broadcastUpdateTemp(String tempString)
	 * 	 @Description: 广播需要更新的温度给Fragment
	 * 	 @Input:  String tempString 需要广播的温度
	 *----------------------------------------------------------------------*/
	private void broadcastUpdateTemp(String areaNum, String greenHouseNum, String deviceString, String tempString)
	{
		Intent intent = new Intent();
		intent.setAction(intent.ACTION_ANSWER);
		intent.putExtra("update", "temp");
		intent.putExtra("area", areaNum);
		intent.putExtra("greenhouse", greenHouseNum);
		intent.putExtra("device", deviceString);
		intent.putExtra("temp", tempString);
		sendBroadcast(intent);
	}

	/**
	 * 	 @Function: private void broadcastUpdateHumi(String humiString)
	 * 	 @Description: 广播需要更新的湿度给Fragment
	 * 	 @Input:  String humiString 需要广播的湿度
	 * */
	private void broadcastUpdateHumi(String humiString)
	{
		Intent intent = new Intent();
		intent.setAction(intent.ACTION_ANSWER);
		intent.putExtra("update", "humi");
		intent.putExtra("humi", humiString);
		sendBroadcast(intent);
	}

	/**
	 * 	 @Function: private int dealResLogin(String msg, int index)
	 * 	 @Input:  String msg:需要处理的信息
	 * 	           int dex:   msg中的偏移量
	 *   @Return: -1: 指令不全
	 *				0: 正好处理完全
	 *             >0：在String中偏移的值
	 * */
	private int dealResLogin(String msg, int index)
	{
		int i = index;
		int msgLength = msg.length();
		while(i < msgLength)
		{
			if(msg.charAt(i) == Instruction.CMD_SEP) break;
			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
			i++;
		}
		if((i >= msgLength) || (i+1 >= msgLength)) return - 1; //错误
		i++;

		if(msg.charAt(i) == Instruction.LOGIN_SUCCESS)
		{
			if(!isAuthed)//如果没有认证成功
			{
				Intent intent = new Intent();
				intent.setAction(intent.ACTION_ANSWER);
				intent.putExtra("result", "res_login");
				intent.putExtra("res_login", "success");
				sendBroadcast(intent);
				isAuthed = true; //身份认真成功

				/*-------------------------------------------------------
				 *         通知ClientMainActivity认证成功
				 *-------------------------------------------------------*/
				intent.setAction(intent.ACTION_EDIT);
				intent.putExtra("type", "wifi_internet");
				intent.putExtra("wifi_internet", "authed");
				sendBroadcast(intent);
			}
			/*-------------------------------------------------------
		     *         通知ClientMainActivity认证成功
			 *-------------------------------------------------------*/
			Intent intent = new Intent();
			intent.setAction(intent.ACTION_EDIT);
			intent.putExtra("type", "wifi_internet");
			intent.putExtra("wifi_internet", "authed");
			sendBroadcast(intent);
		}
		else if(msg.charAt(i) == Instruction.LOGIN_FAILED)//登录失败
		{
			if(isAuthed)//如果已经认证成功，则失败
			{
				Intent intent = new Intent();
				intent.setAction(intent.ACTION_ANSWER);

				intent.putExtra("result", "res_login");
				intent.putExtra("res_login", "failed");

				sendBroadcast(intent);
				isAuthed = false; //登录失败
			}
		}
		for(int count = 2; count > 0; count--)
		{
			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
			i++;
		}
		if(i > msgLength){
			return -1; //越界
		}
		if(i == msgLength){
			return 0;
		}
		return i - index;
	}

	/** 
	 * @Description:
	 * 	监听发送给Service的广播（来自于ClientmainActivity的重新登录请求，来自于各个fragment需要发送信息给服务器的请求）
	 *  用于转发信息给服务器
	 **/
	private class ServiceReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String typeString = intent.getStringExtra("type");
			if(typeString == null) return;
			if(typeString.equals("ClientMainBack"))
			{
				/*主控界面按下了返回键，需要重新登录*/
				isConnected = false;
				isAuthed = false;
				accountReady = false;
				if(serverSocket != null)//如果有socket连接则解除连接
				{
					try {
						serverSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				/*通知登录activity要进行重新登录*/
				Intent intent1 = new Intent();
				intent1.setAction(intent1.ACTION_ANSWER);
	    		intent1.putExtra("result", "relogin");
	            sendBroadcast(intent1);
			}
			/*---------------------------------
			 *   发送数据给服务器
			 *---------------------------------- */
			else if(typeString.equals("send"))
			{
				System.out.println("IGreen Service send msg to Server");
				String msgString = intent.getStringExtra("send");
				if(msgString != null)
				{
					sendMsg(msgString);
				}
			}
		}//end of OnReiceive
		
	}

}
