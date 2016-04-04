package com.ifuture.iagriculture.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifuture.iagriculture.activity.ClientMainActivity;
import com.ifuture.iagriculture.Instruction.Instruction;
import com.ifuture.iagriculture.activity.DatabaseTestActivity;
import com.ifuture.iagriculture.bottombar.BaseFragment;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class FragmentVideo extends BaseFragment{

	DatabaseOperation databaseOperation = null;
	ClientMainActivity mainActivity;
	//Button video_button;
	ImageView cameraiImageView;
	private boolean selection = true;

	TextView bedroom_tempValue,bedroom_humiValue;
	Button bedroom_led1, bedroom_led2, bedroom_led3;
	Button bedroom_win1, bedroom_win2;
	Button bedroom_door;
	Button bedroom_humiControl, bedroom_tempControl;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.video_fragment, container, false);
	}
	
	

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		databaseOperation = new DatabaseOperation();
		//databaseOperation.deleteTable(getActivity());
		databaseOperation.createDatabase(getActivity());//创建数据库
		/*要在onCreateView之后得到空间才是有效的*/
		//video_button = (Button) getActivity().findViewById(R.id.video_button);
		cameraiImageView = (ImageView) getActivity().findViewById(R.id.camera_jpg);

		bedroom_tempValue = (TextView) getActivity().findViewById(R.id.room_tempValue);
		bedroom_humiValue = (TextView) getActivity().findViewById(R.id.room_humiValue);
		bedroom_led1 = (Button) getActivity().findViewById(R.id.room_led1_button);
		bedroom_led2 = (Button) getActivity().findViewById(R.id.room_led2_button);
		bedroom_led3 = (Button) getActivity().findViewById(R.id.room_led3_button);
		bedroom_tempControl = (Button)getActivity().findViewById(R.id.room_tempControl);
		bedroom_win1 = (Button) getActivity().findViewById(R.id.room_win1_button);
		bedroom_win2 = (Button) getActivity().findViewById(R.id.room_win2_button);
		bedroom_door = (Button) getActivity().findViewById(R.id.room_door_button);
		bedroom_humiControl = (Button) getActivity().findViewById(R.id.room_humiControl);
		/*设置监听器*/
		bedroom_led1.setOnClickListener(new LampButtonListener());
		bedroom_led2.setOnClickListener(new LampButtonListener());
		bedroom_led3.setOnClickListener(new LampButtonListener());
		/*demo*/
		bedroom_win1.setOnClickListener(new demoButtonListener());
		bedroom_win2.setOnClickListener(new demoButtonListener());
		bedroom_door.setOnClickListener(new demoButtonListener());
		bedroom_humiControl.setOnClickListener(new demoButtonListener());
		//video_button.setOnClickListener(new ButtonListener());
		//videoStart(); //start video
	}

	/**
	 *  @Description: 将FragmentVideo和ClientMainActivity的handler绑定起来，便于通信。
	 */

	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mainActivity = (ClientMainActivity) activity;
		//mainActivity.setVideoHandler(videoHandler);
	}
//	/**
//	 *  处理Activity传递来的信息
//	 *  打开图片进行显示
//	 */
//	public Handler videoHandler = new Handler()
//	{
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			Bundle bundle = msg.getData();
//			String typeString = bundle.getString("type");
//			if(typeString.equals("videofinish"))
//			{
//				String path = bundle.getString("videofinish");
//				File file = new File(path);
//				if(file.exists()) {
//					BitmapFactory.Options options = new BitmapFactory.Options();
//					options.inSampleSize = 2;
//					Bitmap bm = BitmapFactory.decodeFile(path, options);
//					cameraiImageView.setImageBitmap(bm);
//					//Toast.makeText(getActivity(), "file found", Toast.LENGTH_SHORT).show();
//				}else {
//					//Toast.makeText(getActivity(), "file readme.txt not found", Toast.LENGTH_SHORT).show();
//				}
//
//			}
//			else if(typeString.equals("temp"))/*设置温度*/
//			{
//				String IDString = bundle.getString("temp");//获取设备ID
//				if(IDString.equals("10000"))
//				{
//					bedroom_tempValue.setText(bundle.getString("10000"));
//				}
//			}
//			else if(typeString.equals("humi"))/*设置湿度*/
//			{
//				String IDString = bundle.getString("humi");
//				if(IDString.equals("10000"))
//				{
//					bedroom_humiValue.setText(bundle.getString("10000"));
//				}
//			}
//			else if(typeString.equals("ledon"))/*设置灯*/
//			{
//				if( bundle.getString("ledon").equals("0"))
//				{
//					//bedroom_led1.setText("台灯1");
//					bedroom_led1.setTextColor(Color.RED);
//				}
//				else if( bundle.getString("ledon").equals("1"))
//				{
//					//bedroom_led2.setText("壁灯");
//					bedroom_led2.setTextColor(Color.RED);
//				}
//				else if( bundle.getString("ledon").equals("2"))
//				{
//					//bedroom_led3.setText("台灯2");
//					bedroom_led3.setTextColor(Color.RED);
//				}
//			}
//			else if(typeString.equals("ledoff"))
//			{
//				if( bundle.getString("ledoff").equals("0"))
//				{
//					//bedroom_led1.setText("台灯1");
//					bedroom_led1.setTextColor(getResources().getColor(R.color.text_color_default));
//				}
//				else if( bundle.getString("ledoff").equals("1"))
//				{
//					//bedroom_led2.setText("吊灯");
//					bedroom_led2.setTextColor(getResources().getColor(R.color.text_color_default));
//				}
//				else if( bundle.getString("ledoff").equals("2"))
//				{
//					//bedroom_led3.setText("台灯2");
//					bedroom_led3.setTextColor(getResources().getColor(R.color.text_color_default));
//				}
//			}
//
//		}
//	};

	/**
	 * 灯按键监听器
	 * @Descrption： 如果当前按钮的颜色为绿色则表示需要开启灯
	 */

	class LampButtonListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(getActivity(), DatabaseTestActivity.class);
			getActivity().startActivity(intent);
		}

	}

	/**
	 * 用于演示各个开关的用途，包括门窗等
	 */
	class demoButtonListener implements  OnClickListener{

		@Override
		public void onClick(View v) {
			databaseOperation.querySecToday(getActivity(), 1, 1, 2);
			//databaseOperation.queryMinuteToday(getActivity(), 0, 1);
			//databaseOperation.queryHourToday(getActivity(), 2);
		}
	}
	
	
}
