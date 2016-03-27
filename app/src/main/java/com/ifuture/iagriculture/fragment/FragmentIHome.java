package com.ifuture.iagriculture.fragment;

import android.app.Activity;
import android.content.Intent;
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

import com.ifuture.iagriculture.activity.ClientMainActivity;
import com.ifuture.iagriculture.Instruction.Instruction;
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
	Button balcony_win1, balcony_win2, balcony_win3, balcony_win4;
	TextView weather_value;

	TextView bedroom_tempValue,bedroom_humiValue;
	Button bedroom_led1, bedroom_led2, bedroom_led3;
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
		/*要在onCreateView之后得到空间才是有效的*/
		bedroom_tempValue = (TextView) getActivity().findViewById(R.id.bedroom_tempValue);
		bedroom_humiValue = (TextView) getActivity().findViewById(R.id.bedroom_humiValue);
//		bedroom_led1 = (Button) getActivity().findViewById(R.id.bedroom_led1_button);
//		bedroom_led2 = (Button) getActivity().findViewById(R.id.bedroom_led2_button);
//		bedroom_led3 = (Button) getActivity().findViewById(R.id.bedroom_led3_button);
//		IHome_button = (Button) getActivity().findViewById(R.id.ihome_button);
//		bedroom_tempControl = (Button)getActivity().findViewById(R.id.bedroom_tempControl);

		/*demo*/
//		balcony_win1 = (Button) getActivity().findViewById(R.id.balcony_win1);
//		balcony_win2 = (Button) getActivity().findViewById(R.id.balcony_win2);
//		balcony_win3 = (Button) getActivity().findViewById(R.id.balcony_win3);
//		balcony_win4 = (Button) getActivity().findViewById(R.id.balcony_win4);
//		bedroom_win1 = (Button) getActivity().findViewById(R.id.bedroom_win1_button);
//		bedroom_win2 = (Button) getActivity().findViewById(R.id.bedroom_win2_button);
//		kitchen_win1 = (Button) getActivity().findViewById(R.id.kitchen_win1_button);
//		kitchen_win2 = (Button) getActivity().findViewById(R.id.kitchen_win2_button);
//		kitchen_door = (Button) getActivity().findViewById(R.id.kitchen_door_button);
//		bedroom_door = (Button) getActivity().findViewById(R.id.bedroom_door_button);
//		bedroom_humiControl = (Button) getActivity().findViewById(R.id.bedroom_humiControl);
//		/*设置监听器*/
//		bedroom_led1.setOnClickListener(new LampButtonListener());
//		bedroom_led2.setOnClickListener(new LampButtonListener());
//		bedroom_led3.setOnClickListener(new LampButtonListener());
//		IHome_button.setOnClickListener(new IHomeButtonListener());
		//bedroom_tempControl.setOnClickListener(new tempCtrlButtonListener());
		/*set demo listenner*/
//		balcony_win1.setOnClickListener(new demoButtonListener());
//		balcony_win2.setOnClickListener(new demoButtonListener());
//		balcony_win3.setOnClickListener(new demoButtonListener());
//		balcony_win4.setOnClickListener(new demoButtonListener());
//		bedroom_win1.setOnClickListener(new demoButtonListener());
//		bedroom_win2.setOnClickListener(new demoButtonListener());
//		kitchen_win1.setOnClickListener(new demoButtonListener());
//		kitchen_win2.setOnClickListener(new demoButtonListener());
//		kitchen_door.setOnClickListener(new demoButtonListener());
//		bedroom_door.setOnClickListener(new demoButtonListener());
//		bedroom_humiControl.setOnClickListener(new demoButtonListener());
	}



	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mainActivity = (ClientMainActivity) activity;
		mainActivity.setIHomeHandler(ihomeHandler);
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

//	/**
//	 * 灯按键监听器
//	 * @Descrption： 如果当前按钮的颜色为绿色则表示需要开启灯
//	 */
//
//	class LampButtonListener implements OnClickListener{
//
//		@Override
//		public void onClick(View view) {
//			// TODO Auto-generated method stub
//			int lampid = view.getId();
//			byte type = Instruction.COMMAND_CONTRL;
//			byte subtype = Instruction.CTL_LAMP;
//			byte operator = 0;
//			String IDString = new String("");
//
//			int textColor = getResources().getColor(R.color.text_color_default);
//			switch(lampid)			{
//				case R.id.bedroom_led1_button:
//					{
//						IDString = new String("0");
//						int currentColor = bedroom_led1.getCurrentTextColor();
//						if(currentColor == textColor)
//						{
//							operator = Instruction.LAMP_ON;
//						}
//						else
//						{
//							operator = Instruction.LAMP_OFF;
//						}
//
//					}
//					break;
//				case R.id.bedroom_led2_button:
//					{
//
//						IDString = new String("1");
//						int currentColor = bedroom_led2.getCurrentTextColor();
//						if(currentColor == textColor)
//						{
//							operator = Instruction.LAMP_ON;
//						}
//						else
//						{
//							operator = Instruction.LAMP_OFF;
//						}
//
//					}
//				    break;
//				case R.id.bedroom_led3_button:
//					{
//
//						IDString = new String("2");
//						int currentColor = bedroom_led3.getCurrentTextColor();
//						if(currentColor == textColor)
//						{
//							operator = Instruction.LAMP_ON;
//						}
//						else
//						{
//							operator = Instruction.LAMP_OFF;
//						}
//
//					}
//					break;
//			}//end of switch
//
//			try {
//				/*需要发送的指令,byte数组*/
//				byte typeBytes[] = {type,Instruction.COMMAND_SEPERATOR};
//				byte subtypeBytes[] = {Instruction.COMMAND_SEPERATOR,subtype, Instruction.COMMAND_SEPERATOR};
//				byte operatorBytes[] = {operator, Instruction.COMMAND_SEPERATOR};
//				byte IDBytes[] = IDString.getBytes("UTF-8");
//				byte endBytes[] = {Instruction.COMMAND_SEPERATOR, Instruction.COMMAND_END};
//				byte buffer[] = new byte[subtypeBytes.length+operatorBytes.length
//				                       +IDBytes.length+endBytes.length];
//
//				/*转换account后面所有指令*/
//				int start = 0;
//				System.arraycopy(subtypeBytes ,0,buffer,start, subtypeBytes.length);
//				start+=subtypeBytes.length;
//				System.arraycopy(operatorBytes ,0,buffer,start, operatorBytes.length);
//				start+=operatorBytes.length;
//				System.arraycopy(IDBytes,0,buffer,start, IDBytes.length);
//				start+=IDBytes.length;
//				System.arraycopy(endBytes   ,0,buffer,start, endBytes.length);
//
//				/*发送广播给Service，让其发送信息给服务器*/
//				Intent intent = new Intent();
//				intent.putExtra("type", "send");
//				intent.putExtra("onefield", typeBytes);
//				intent.putExtra("twofield", buffer);
//				intent.setAction(intent.ACTION_MAIN);
//				getActivity().sendBroadcast(intent);
//
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//	}

	class tempCtrlButtonListener implements  OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setAction(intent.ACTION_EDIT);
			intent.putExtra("type", "tempCtrl");
			getActivity().sendBroadcast(intent);
		}
	}

//	/**
//	 * 智能家居模式开启按键
//	 */
//	class IHomeButtonListener implements OnClickListener{
//
//		@Override
//		public void onClick(View view) {
//			// TODO Auto-generated method stub
//			byte operator = Instruction.IHome_STOP;
//			String IDString = new String("");
//			if(ihome_mode == false)
//			{
//				operator = Instruction.IHome_START;
//			}
//			else
//			{
//				operator = Instruction.IHome_STOP;
//			}
//			/*需要发送的指令,byte数组*/
//			byte typeBytes[] = {Instruction.COMMAND_CONTRL,Instruction.COMMAND_SEPERATOR};
//			byte subtypeBytes[] = {Instruction.COMMAND_SEPERATOR,Instruction.CTL_IHome, Instruction.COMMAND_SEPERATOR};
//			byte operatorBytes[] = {operator, Instruction.COMMAND_SEPERATOR,Instruction.COMMAND_END};
//			byte buffer[] = new byte[subtypeBytes.length+operatorBytes.length];
//
//			/*转换account后面所有指令*/
//			int start = 0;
//			System.arraycopy(subtypeBytes ,0,buffer,start, subtypeBytes.length);
//			start+=subtypeBytes.length;
//			System.arraycopy(operatorBytes ,0,buffer,start, operatorBytes.length);
//
//			/*发送广播给Service，让其发送信息给服务器*/
//			Intent intent = new Intent();
//			intent.putExtra("type", "send");
//			intent.putExtra("onefield", typeBytes);
//			intent.putExtra("twofield", buffer);
//			intent.setAction(intent.ACTION_MAIN);
//			getActivity().sendBroadcast(intent);
//
//		}
//
//	}


//	/**
//	 * 用于演示各个开关的用途，包括门窗等
//	 */
//	class demoButtonListener implements  OnClickListener{
//
//		@Override
//		public void onClick(View v) {
//			int id = v.getId();
//			int textColor = getResources().getColor(R.color.text_color_default);
//			switch(id)			{
//				case R.id.balcony_win1:
//				{
//					int currentColor = balcony_win1.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						balcony_win1.setTextColor(Color.RED);
//					}
//					else
//					{
//						balcony_win1.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//				case R.id.balcony_win2:
//				{
//					int currentColor = balcony_win2.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						balcony_win2.setTextColor(Color.RED);
//					}
//					else
//					{
//						balcony_win2.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//				case R.id.balcony_win3:
//				{
//					int currentColor = balcony_win3.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						balcony_win3.setTextColor(Color.RED);
//					}
//					else
//					{
//						balcony_win3.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//				case R.id.balcony_win4:
//				{
//					int currentColor = balcony_win4.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						balcony_win4.setTextColor(Color.RED);
//					}
//					else
//					{
//						balcony_win4.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//				case R.id.bedroom_win1_button:
//				{
//					int currentColor = bedroom_win1.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						bedroom_win1.setTextColor(Color.RED);
//					}
//					else
//					{
//						bedroom_win1.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//				case R.id.bedroom_win2_button:
//				{
//					int currentColor = bedroom_win2.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						bedroom_win2.setTextColor(Color.RED);
//					}
//					else
//					{
//						bedroom_win2.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//				case R.id.bedroom_door_button:
//				{
//					int currentColor = bedroom_door.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						bedroom_door.setTextColor(Color.RED);
//					}
//					else
//					{
//						bedroom_door.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//				case R.id.kitchen_win1_button:
//				{
//					int currentColor = kitchen_win1.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						kitchen_win1.setTextColor(Color.RED);
//					}
//					else
//					{
//						kitchen_win1.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//				case R.id.kitchen_win2_button:
//				{
//					int currentColor = kitchen_win2.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						kitchen_win2.setTextColor(Color.RED);
//					}
//					else
//					{
//						kitchen_win2.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//				case R.id.kitchen_door_button:
//				{
//					int currentColor = kitchen_door.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						kitchen_door.setTextColor(Color.RED);
//					}
//					else
//					{
//						kitchen_door.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//				case R.id.bedroom_humiControl:
//				{
//					int currentColor = bedroom_humiControl.getCurrentTextColor();
//					if(currentColor == textColor)
//					{
//						bedroom_humiControl.setTextColor(Color.RED);
//					}
//					else
//					{
//						bedroom_humiControl.setTextColor(getResources().getColor(R.color.text_color_default));
//					}
//
//				}
//				break;
//			}//end of switch
//		}
//	}
	
}