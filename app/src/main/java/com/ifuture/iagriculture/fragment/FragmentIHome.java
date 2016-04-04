package com.ifuture.iagriculture.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ifuture.iagriculture.activity.ClientActivity;
import com.ifuture.iagriculture.activity.ClientMainActivity;
import com.ifuture.iagriculture.Instruction.Instruction;
//import com.ifuture.iagriculture.activity.StatisticsActivity;
import com.ifuture.iagriculture.bottombar.*;
import com.ifuture.iagriculture.R;

import java.io.UnsupportedEncodingException;


/** 
 * @CopyRight: 王辰浩 2015~2025
 * @Author Feather Hunter(猎羽)
 * @qq:975559549
 * @Version:1.0 
 * @Date:2015/12/25
 * @Description: IHome的Fragment界面
 * @Function List:
 *   1. void onAttach(Activity activity); //用于绑定ClientMainActivity和handler
 *   2. Handler communicationHandler;     //用于处理和ClientMainActivity的通信
 **/

public class FragmentIHome extends BaseFragment{

	ClientMainActivity mainActivity;
	private RecvReceiver recvReceiver;
	private String RECV_ACTION = "android.intent.action.ANSWER";
	TextView tempCATextview;//C当前温度 for air空气
	TextView tempCGTextview;//C当前温度 for air
	TextView humiCATextview;//C当前湿度 for ground 土壤
	TextView humiCGTextview;//C当前湿度 for ground

	SharedPreferences apSharedPreferences = null;

	Button balcony_win1, balcony_win2, balcony_win3, balcony_win4;
	TextView weather_value;

	TextView bedroom_tempValue,bedroom_humiValue;
	Button bedroom_led1, bedroom_led2, bedroom_led3;
	Button staticsButton;
	Button bedroom_win1, bedroom_win2;
	Button bedroom_door;
	Button bedroom_humiControl, bedroom_tempControl;

	TextView kitchen_smokeState, kitchen_fireState;
	Button kitchen_win1, kitchen_win2;
	Button kitchen_door;

	Button IHome_button;
	boolean ihome_mode = false;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.ihome_fragment1, container, false);
	}
	
	

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		/* -------------------------------------------------------
		 *  动态注册receiver
		 * -------------------------------------------------------*/
		try {
			recvReceiver = new RecvReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(RECV_ACTION);
			getActivity().registerReceiver(recvReceiver, filter);//注册
		} catch (IllegalArgumentException  e) {
			// TODO: handle exception
			System.out.println("fragmentIHome registerReceiver");
		}
		/*要在onCreateView之后得到空间才是有效的*/
		//bedroom_tempValue = (TextView) getActivity().findViewById(R.id.bedroom_tempValue);
		//bedroom_humiValue = (TextView) getActivity().findViewById(R.id.bedroom_humiValue);
		tempCATextview = (TextView) getActivity().findViewById(R.id.igreen_fragment_catemp);//C当前温度 for air空气
		tempCGTextview = (TextView) getActivity().findViewById(R.id.igreen_fragment_cahumi);//C当前温度 for air
		humiCATextview = (TextView) getActivity().findViewById(R.id.igreen_fragment_cgtemp);//C当前湿度 for ground 土壤
		humiCGTextview = (TextView) getActivity().findViewById(R.id.igreen_fragment_cghumi);//C当前湿度 for ground

		/* -------------------------------------------------------
	     *  通过SharedPreferences获取当前温度等数据,显示出来。
	     *  用于fragment切换时候的数据保存
		 * -------------------------------------------------------*/
		apSharedPreferences = getActivity().getSharedPreferences("tempdata", Activity.MODE_PRIVATE);
		tempCATextview.setText(apSharedPreferences.getString("temperature", "")+"℃"); //第2个参数是value的默认值
		tempCGTextview.setText(apSharedPreferences.getString("temperature", "")+"℃"); //第2个参数是value的默认值
	}

	class staticsButtonListenner implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent tempIntent = new Intent();

			//tempIntent.setClass(getActivity(), StatisticsActivity.class);
			//getActivity().startActivity(tempIntent);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mainActivity = (ClientMainActivity) activity;
		mainActivity.setIHomeHandler(ihomeHandler);
	}

	/**
	 * @Function: private class ContrlReceiver extends BroadcastReceiver
	 * @Description:
	 *      接受来自Service的信息，并且转发给相应fragment来改变相应组件内容
	 **/
	private class RecvReceiver extends BroadcastReceiver {

		public RecvReceiver() {
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String typeString = intent.getStringExtra("update");
			if(typeString != null)
			{
				if(typeString.equals("temp"))/*发送给第一个ihome fragment*/
				{
					String tempString = intent.getStringExtra("temp");
					tempCATextview.setText(tempString+"℃");
					tempCGTextview.setText(tempString+"℃");
				}
			}
		}//onReceive


	}
	/**
	 *  处理Activity传递来的信息
	 */
	public Handler ihomeHandler = new Handler()
	{
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		    Bundle bundle = msg.getData();
		    String typeString = bundle.getString("type");
		    if(typeString.equals("ihome"))
		    {
		    	String mode = bundle.getString("ihome");
		    	if(mode.equals("start"))
		    	{
		    		IHome_button.setTextColor(0xff00cc00);
		    		ihome_mode = true;
		    	}
		    	else {
		    		IHome_button.setTextColor(0xffbfbfbf);
		    		ihome_mode = false;
				}
		    }
		    else if(typeString.equals("temp"))/*设置温度*/
			{
		    	String IDString = bundle.getString("temp");//获取设备ID
		    	if(IDString.equals("10000"))
		    	{
		    		bedroom_tempValue.setText(bundle.getString("10000"));
		    	}
			}
		    else if(typeString.equals("humi"))/*设置湿度*/
			{
		    	String IDString = bundle.getString("humi");
		    	if(IDString.equals("10000"))
		    	{
		    		bedroom_humiValue.setText(bundle.getString("10000"));
		    	}
			}
		    else if(typeString.equals("ledon"))/*设置灯*/
			{
		    	if( bundle.getString("ledon").equals("0"))
		    	{
		    		//bedroom_led1.setText("台灯1");
					bedroom_led1.setTextColor(Color.RED);
		    	}
		    	else if( bundle.getString("ledon").equals("1"))
		    	{
					//bedroom_led2.setText("壁灯");
					bedroom_led2.setTextColor(Color.RED);
		    	}
		    	else if( bundle.getString("ledon").equals("2"))
		    	{
					//bedroom_led3.setText("台灯2");
					bedroom_led3.setTextColor(Color.RED);
		    	}
			}
		    else if(typeString.equals("ledoff"))
		    {
		    	if( bundle.getString("ledoff").equals("0"))
		    	{
					//bedroom_led1.setText("台灯1");
					bedroom_led1.setTextColor(getResources().getColor(R.color.text_color_default));
		    	}
		    	else if( bundle.getString("ledoff").equals("1"))
		    	{
					//bedroom_led2.setText("吊灯");
					bedroom_led2.setTextColor(getResources().getColor(R.color.text_color_default));
		    	}
		    	else if( bundle.getString("ledoff").equals("2"))
		    	{
					//bedroom_led3.setText("台灯2");
					bedroom_led3.setTextColor(getResources().getColor(R.color.text_color_default));
		    	}
		    }

		}
	};

	class tempCtrlButtonListener implements  OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setAction(intent.ACTION_EDIT);
			intent.putExtra("type", "tempCtrl");
			getActivity().sendBroadcast(intent);
		}
	}
	
}