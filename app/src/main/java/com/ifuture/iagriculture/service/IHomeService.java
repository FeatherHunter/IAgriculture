package com.ifuture.iagriculture.service;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.ifuture.iagriculture.Instruction.Instruction;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;

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
 * @Version:2.10
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

	private boolean getTHthread = false;
	private boolean stopallthread = false; //停止所有线程
	private String message = null; //4096字节的指令缓冲区
	
	private ServiceReceiver serviceReceiver;
	private String SERVICE_ACTION = "android.intent.action.MAIN";

	//private String IGServerIP = "192.168.191.1";
	private String IGServerIP = "139.129.19.115";
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
		thread = new Thread(demo_revMsgRunnable);
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
		databaseOperation = new DatabaseOperation();
		databaseOperation.createDatabase(this);//创建数据库
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
		//System.out.println(tempString + "send");

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
					break;
				}
				/*tcp连接成功,用户信息没有准备好,认证成功---不满足其中一项则进行处理*/
				while ((isConnected == true) && (!accountReady) && (isAuthed == true)) {
					try {
						Thread.sleep(500);  //睡眠
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (stopallthread) { //终止所有线程的标志
					break;
				}
				if (accountReady && (isConnected == false))//身份确定并且没有连接
				{
					System.out.println("正在连接服务器.....");
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
					/*断开连接后,重新连接和身份认证*/
					try {
						/*检测链接是否超时的线程*/
						Thread thread = new Thread(connectOvertimeRunnable);
						thread.start();
						serverSocket = new Socket(IGServerIP, IGServerPort);
						/*得到输入流、输出流*/
						outputStream = serverSocket.getOutputStream();
						inputStream = serverSocket.getInputStream();
						isConnected = true; //連接成功

						/*告诉activity重新连接成功*/
						intent.setAction(intent.ACTION_EDIT);
						intent.putExtra("type", "disconnect");
						intent.putExtra("disconnect", "connected");
						sendBroadcast(intent);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						isConnected = false; //連接失敗
						try {
							Thread.sleep(2500);  //失败后等待3s连接
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						isConnected = false; //连接失败
						try {
							Thread.sleep(1500);  //失败后等待3s连接
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
							Thread.sleep(2500);  //身份验证失败后等待3s
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
			if(isConnected == false)
			{
				System.out.println("connect error!");
				isConnected = false; //連接失敗
				Intent cintent = new Intent();
				cintent.setAction(cintent.ACTION_ANSWER);

				cintent.putExtra("result", "res_login");
				cintent.putExtra("res_login", "connect error");

				sendBroadcast(cintent);
			}

		}
	};

	Runnable demo_revMsgRunnable = new Runnable() {

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
		String terminalString;
		String deviceString;
		String tempString;
		String timeString;
		int i = index;
		int msgLength = msg.length();
		int startIndex;
		while(i < msgLength)
		{
			if(msg.charAt(i) == Instruction.CMD_SEP) break;
			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
			i++;
		}
		if((i >= msgLength) || (i+1 >= msgLength)) return - 1; //错误
		i++;

		/*=======获取终端ID号===============*/
		startIndex = i;
		while(i < msgLength)
		{
			if(msg.charAt(i) == Instruction.CMD_SEP) break;
			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
			i++;
		}
		if((i >= msgLength) || (i+1 >= msgLength)) return - 1; //错误
		i++;
		terminalString = msg.substring(startIndex, i-1); //获取终端ID号
		System.out.println("get terminal："+terminalString);
		/*=======获取终端ID号===============*/
		startIndex = i;
		while(i < msgLength)
		{
			if(msg.charAt(i) == Instruction.CMD_SEP) break;
			if(msg.charAt(i) == Instruction.CMD_HEAD) return i - index; //又找到一个头，说明之前数据无效
			i++;
		}
		if((i >= msgLength) || (i+1 >= msgLength)) return - 1; //错误
		i++;
		deviceString = msg.substring(startIndex, i-1); //获取设备ID号
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
			System.out.println(""+year+month+day+hour+mintue+second);

			databaseOperation.insertToday(this, hour, mintue, second, Float.parseFloat(tempString), 0);//暂时使用0
			/* -------------------------------------------------------
			 *  通过SharedPreferences保存温度数据
		 	 * -------------------------------------------------------*/
			apSharedPreferences = getSharedPreferences("tempdata", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = apSharedPreferences.edit();//用putString的方法保存数据
			editor.putString("temperature", tempString);
			editor.commit();

			broadcastUpdateTemp(tempString);//将温度数据广播出去
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

	private void broadcastUpdateTemp(String tempString)
	{
		Intent intent = new Intent();
		intent.setAction(intent.ACTION_ANSWER);
		intent.putExtra("update", "temp");
		intent.putExtra("temp", tempString);
		sendBroadcast(intent);
	}

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

				/*身份认证成功*/
				intent.setAction(intent.ACTION_EDIT);
				intent.putExtra("type", "disconnect");
				intent.putExtra("disconnect", "authed");
				sendBroadcast(intent);
			}
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
	 * 	 监听发送给Service的广播 
	 *  用于转发信息给服务器
	 **/
	private class ServiceReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String typeString = intent.getStringExtra("type");
			if(typeString.equals("send"))
			{
				String msgString = intent.getStringExtra("send");
				sendMsg(msgString);
			}
			else if(typeString.equals("fragment"))
			{
//				String ihomeMode = intent.getStringExtra("ihome");
//				String videoMode = intent.getStringExtra("video");
//				if(ihomeMode.equals("start")) //开启iHome fragment
//				{
//					getTHthread = true; //开启温度湿度更新线程
//					/*请求温度*/
//					try {
//						/*需要发送的指令,byte数组*/
//						byte typeBytes[] = {Instruction.COMMAND_CONTRL,Instruction.COMMAND_SEPERATOR};
//						byte accountBytes[] = account.getBytes("UTF-8");//得到标准的UTF-8编码
//						byte twoBytes[] = {Instruction.COMMAND_SEPERATOR,Instruction.CTL_GET,Instruction.COMMAND_SEPERATOR,
//								Instruction.RES_TEMP, Instruction.COMMAND_SEPERATOR};
//						String IDString = new String("10000");
//						byte TempIDBytes[] = IDString.getBytes("UTF-8");
//						//byte TempIDBytes[] = {'1','0'};
//						byte threeBytes[] = {Instruction.COMMAND_SEPERATOR, Instruction.COMMAND_END};
//						byte temp_buffer[] = new byte[typeBytes.length + accountBytes.length+twoBytes.length
//								+TempIDBytes.length+threeBytes.length];
//						/*合并到一个byte数组中*/
//						int start = 0;
//						System.arraycopy(typeBytes    ,0,temp_buffer,start, typeBytes.length);
//						start+=typeBytes.length;
//						System.arraycopy(accountBytes ,0,temp_buffer,start, accountBytes.length);
//						start+=accountBytes.length;
//						System.arraycopy(twoBytes     ,0,temp_buffer,start, twoBytes.length);
//						start+=twoBytes.length;
//						System.arraycopy(TempIDBytes,0,temp_buffer,start, TempIDBytes.length);
//						start+=TempIDBytes.length;
//						System.arraycopy(threeBytes   ,0,temp_buffer,start, threeBytes.length);
//
//						outputStream.write(temp_buffer, 0, temp_buffer.length);//发送指令
//						outputStream.flush();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						isConnected = false; //断开连接
//						isAuthed = false;    //认证失效
//					}
//					try {
//						Thread.sleep(150);//先休眠一秒等待链接
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					/*请求湿度*/
//					try {
//						/*需要发送的指令(获取湿度),byte数组*/
//						byte typeBytes[] = {Instruction.COMMAND_CONTRL,Instruction.COMMAND_SEPERATOR};
//						byte accountBytes[] = account.getBytes("UTF-8");//得到标准的UTF-8编码
//						byte twoBytes[] = {Instruction.COMMAND_SEPERATOR,Instruction.CTL_GET, Instruction.COMMAND_SEPERATOR,
//								Instruction.RES_HUMI, Instruction.COMMAND_SEPERATOR};
//						String IDString = new String("10000");
//						byte HumiIDBytes[] = IDString.getBytes("UTF-8");
//						byte threeBytes[] = {Instruction.COMMAND_SEPERATOR, Instruction.COMMAND_END};
//						byte humi_buffer[] = new byte[typeBytes.length + accountBytes.length+twoBytes.length
//								+HumiIDBytes.length+threeBytes.length];
//						/*合并到一个byte数组中*/
//						int start = 0;
//						System.arraycopy(typeBytes    ,0,humi_buffer,start, typeBytes.length);
//						start+=typeBytes.length;
//						System.arraycopy(accountBytes ,0,humi_buffer,start, accountBytes.length);
//						start+=accountBytes.length;
//						System.arraycopy(twoBytes     ,0,humi_buffer,start, twoBytes.length);
//						start+=twoBytes.length;
//						System.arraycopy(HumiIDBytes,0,humi_buffer,start, HumiIDBytes.length);
//						start+=HumiIDBytes.length;
//						System.arraycopy(threeBytes   ,0,humi_buffer,start, threeBytes.length);
//
//						outputStream.write(humi_buffer, 0, humi_buffer.length);//发送指令
//						outputStream.flush();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						isConnected = false; //断开连接
//						isAuthed = false;    //认证失效
//					}
//				}
//				else//关闭iHome fragment
//				{
//					getTHthread = false; //关闭温度湿度更新线程
//				}
//
//				if(videoMode.equals("start")) //开启video fragment
//				{
//					getTHthread = true; //开启温度湿度更新线程
//					/*显示图片防止过于感到延迟*/
//					Intent pintent = new Intent();
//					pintent.setAction(pintent.ACTION_EDIT);
//					pintent.putExtra("type", "videofinish");
//					pintent.putExtra("videofinish","mnt/sdcard/camera0.jpg");
//					sendBroadcast(pintent);
//
//					/*开始传送视频*/
//					try {
//						/*需要发送的指令,byte数组*/
//						byte typeBytes[] = {Instruction.COMMAND_CONTRL,Instruction.COMMAND_SEPERATOR};
//						byte accountBytes[] = account.getBytes("UTF-8");//得到标准的UTF-8编码
//						byte twoBytes[] = {Instruction.COMMAND_SEPERATOR,Instruction.CTL_VIDEO,Instruction.COMMAND_SEPERATOR,
//								Instruction.VIDEO_START, Instruction.COMMAND_SEPERATOR};
//						String IDString = new String("20000");
//						byte VideoIDBytes[] = IDString.getBytes("UTF-8");
//						byte threeBytes[] = {Instruction.COMMAND_SEPERATOR, Instruction.COMMAND_END};
//						byte video_buffer[] = new byte[typeBytes.length + accountBytes.length+twoBytes.length
//								+VideoIDBytes.length+threeBytes.length];
//						/*合并到一个byte数组中*/
//						int start = 0;
//						System.arraycopy(typeBytes    ,0,video_buffer,start, typeBytes.length);
//						start+=typeBytes.length;
//						System.arraycopy(accountBytes ,0,video_buffer,start, accountBytes.length);
//						start+=accountBytes.length;
//						System.arraycopy(twoBytes     ,0,video_buffer,start, twoBytes.length);
//						start+=twoBytes.length;
//						System.arraycopy(VideoIDBytes,0,video_buffer,start, VideoIDBytes.length);
//						start+=VideoIDBytes.length;
//						System.arraycopy(threeBytes   ,0,video_buffer,start, threeBytes.length);
//
//						outputStream.write(video_buffer, 0, video_buffer.length);//发送指令
//						outputStream.flush();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						isConnected = false; //断开连接
//						isAuthed = false;    //认证失效
//					}
//				}
//				else
//				{
//					getTHthread = false; //关闭温度湿度更新线程
//					/*结束传送视频*/
//					try {
//						/*需要发送的指令,byte数组*/
//						byte typeBytes[] = {Instruction.COMMAND_CONTRL,Instruction.COMMAND_SEPERATOR};
//						byte accountBytes[] = account.getBytes("UTF-8");//得到标准的UTF-8编码
//						byte twoBytes[] = {Instruction.COMMAND_SEPERATOR,Instruction.CTL_VIDEO,Instruction.COMMAND_SEPERATOR,
//								Instruction.VIDEO_STOP, Instruction.COMMAND_SEPERATOR};
//						String IDString = new String("20000");
//						byte VideoIDBytes[] = IDString.getBytes("UTF-8");
//						byte threeBytes[] = {Instruction.COMMAND_SEPERATOR, Instruction.COMMAND_END};
//						byte video_buffer[] = new byte[typeBytes.length + accountBytes.length+twoBytes.length
//								+VideoIDBytes.length+threeBytes.length];
//						/*合并到一个byte数组中*/
//						int start = 0;
//						System.arraycopy(typeBytes    ,0,video_buffer,start, typeBytes.length);
//						start+=typeBytes.length;
//						System.arraycopy(accountBytes ,0,video_buffer,start, accountBytes.length);
//						start+=accountBytes.length;
//						System.arraycopy(twoBytes     ,0,video_buffer,start, twoBytes.length);
//						start+=twoBytes.length;
//						System.arraycopy(VideoIDBytes,0,video_buffer,start, VideoIDBytes.length);
//						start+=VideoIDBytes.length;
//						System.arraycopy(threeBytes   ,0,video_buffer,start, threeBytes.length);
//
//						outputStream.write(video_buffer, 0, video_buffer.length);//发送指令
//						outputStream.flush();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						isConnected = false; //断开连接
//						isAuthed = false;    //认证失效
//					}
//				}//end of video mode

			}
			else if(typeString.equals("ClientMainBack"))
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
		}
		
	}
	//	/**
//	 * @Function: serverConnectRunnable;
//	 * @Description:
//	 *      用于连接服务器或者控制中心，并且断线的时候重新连接。
//	 *      1. 关闭socket
//	 *      2. 解除动态注册的Receiver
//	 **/
//	Runnable serverConnectRunnable = new Runnable() {
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			while(true)
//			{
//				if(stopallthread)
//				{
//					break;
//				}
//				/*tcp连接成功,用户信息没有准备好,认证成功---不满足其中一项则进行处理*/
//				while((isConnected == true)&&(!accountReady)&&(isAuthed == true))
//				{
//					try {
//						Thread.sleep(500);  //睡眠
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				if(stopallthread)
//				{
//					break;
//				}
//				if(accountReady && (isConnected == false))//身份确定并且没有连接
//				{
//					System.out.println("正在重新连接");
//					/*正在重新连接*/
//					Intent intent = new Intent();
//					intent.setAction(intent.ACTION_EDIT);
//					intent.putExtra("type", "disconnect");
//					intent.putExtra("disconnect", "connecting");
//					sendBroadcast(intent);
//
//					/*之前有过socket连接,先关闭，再开启新的*/
//					if(serverSocket != null)
//					{
//						try {
//							serverSocket.close();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					isTestWifi = true;
//					iswified = false;
//					/*检测是否在wifi内能连接到用户*/
//					if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)//wifi已经打开
//					{
//						SocketChannel socketChannel = null;
//						try {
//							socketChannel = SocketChannel.open();
//							socketChannel.configureBlocking(false);
//							socketChannel.connect(new InetSocketAddress(contrlCenterString, generalPort));
//
//							Thread.sleep(1000);  //睡眠500ms
//							if(!socketChannel.finishConnect())
//							{
//								iswified = false;
//							}
//							else {
//								iswified = true;
//							}
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}finally{
//							try {
//								if(socketChannel != null)//关闭
//									socketChannel.close();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}//end of connecting stm32 by wifi
//					isTestWifi = false;
//					if(stopallthread)
//					{
//						break;
//					}
//					if(iswified == true)
//					{
//						/*断开连接后,重新连接和身份认证*/
//						try {
//							serverSocket = new Socket(contrlCenterString, generalPort);
//							/*得到输入流、输出流*/
//							outputStream = serverSocket.getOutputStream();
//							inputStream = serverSocket.getInputStream();
//							isConnected = true; //連接成功
//
//							/*告诉activity重新连接成功*/
//							intent.setAction(intent.ACTION_EDIT);
//							intent.putExtra("type", "disconnect");
//							intent.putExtra("disconnect", "connected");
//							sendBroadcast(intent);
//						} catch (UnknownHostException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							isConnected = false; //連接失敗
//							try {
//								Thread.sleep(2500);  //失败后等待3s连接
//							} catch (InterruptedException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							isConnected = false; //连接失败
//							try {
//								Thread.sleep(2500);  //失败后等待3s连接
//							} catch (InterruptedException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//						}
//
//					}//end of connecting ContrlCenter
//					else {
//						/*断开连接后,重新连接和身份认证*/
//						try {
//							serverSocket = new Socket(serverString, generalPort);
//							/*得到输入流、输出流*/
//							outputStream = serverSocket.getOutputStream();
//							inputStream = serverSocket.getInputStream();
//							isConnected = true; //連接成功
//
//							/*告诉activity重新连接成功*/
//							intent.setAction(intent.ACTION_EDIT);
//							intent.putExtra("type", "disconnect");
//							intent.putExtra("disconnect", "connected");
//							sendBroadcast(intent);
//						} catch (UnknownHostException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							isConnected = false; //連接失敗
//							try {
//								Thread.sleep(2500);  //失败后等待3s连接
//							} catch (InterruptedException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							isConnected = false; //连接失败
//							try {
//								Thread.sleep(2500);  //失败后等待3s连接
//							} catch (InterruptedException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//						}
//
//					}//end of connecting server
//				}
//				if(stopallthread)
//				{
//					break;
//				}
//				/*在连接成功和用户身份确定的时候，进行身份认证*/
//				if(isConnected && accountReady && (isAuthed == false))
//				{
//					System.out.println("正在重新验证"+isConnected+accountReady+isAuthed);
//					/*通知activity正在验证信息*/
//					Intent intent = new Intent();
//					intent.setAction(intent.ACTION_EDIT);
//					intent.putExtra("type", "disconnect");
//					intent.putExtra("disconnect", "authing");
//					sendBroadcast(intent);
//					/*用户身份验证请求*/
//
//					try {
//						Thread.sleep(1000);  //身份验证失败后等待1s
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}//end of 身份认证
//
//			}
//
//		}
//	};

//	/**
//	 * @Description:用于定时得到温度,湿度,灯初始信息---起到心跳的作用
//	 **/
//	Runnable getTempHumiRunnable = new Runnable() {
//
//		boolean selectflag = true;
//		public void run() {
//			// TODO Auto-generated method stub
//			while(true)
//			{
//				if(stopallthread)
//				{
//					break;
//				}
//				while(isAuthed == false)//等待重新链接和身份认证
//				{
//					try {
//						Thread.sleep(1000);//先休眠一秒等待链接
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				if(getTHthread == false)
//				{
//					try {
//						Thread.sleep(500);//先休眠一秒等待链接
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					continue;
//				}
//				if(selectflag)
//				{
//					selectflag = !selectflag;//每次交替检查一次温度和湿度
//
//				}
//				else {
//					selectflag = !selectflag;//每次交替检查一次温度和湿度
//
//
//				}
//				try {
//					Thread.sleep(10000);      //10s获得一次
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//		}
//
//
//	};
}
