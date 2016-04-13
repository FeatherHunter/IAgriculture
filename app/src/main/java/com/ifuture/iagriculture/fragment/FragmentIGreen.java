package com.ifuture.iagriculture.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.bottombar.BaseFragment;

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

public class FragmentIGreen extends BaseFragment{

	private RecvReceiver recvReceiver;
	private String RECV_ACTION = "android.intent.action.ANSWER";
	TextView tempCATextview;//C当前温度 for air空气
	TextView tempCGTextview;//C当前温度 for air
	TextView humiCATextview;//C当前湿度 for ground 土壤
	TextView humiCGTextview;//C当前湿度 for ground

	ImageView videoImageView;
	RelativeLayout videoRelativelayout;

	SharedPreferences apSharedPreferences = null;
	Button videoImageButton;
	boolean video_start = false;

	Boolean videoOkFlag = false;
	private ContrlReceiver contrlReceiver;
	private String CONTRL_ACTION = "android.intent.action.EDIT";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.ihome_fragment1, container, false);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		video_start = false; //默认视频关闭

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

		try {
			/*动态注册receiver*/
			contrlReceiver = new ContrlReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(CONTRL_ACTION);
			getActivity().registerReceiver(contrlReceiver, filter);//注册
		} catch (IllegalArgumentException  e) {
			// TODO: handle exception
			System.out.println("had been registerReceiver");
		}

		/*要在onCreateView之后得到空间才是有效的*/
		//bedroom_tempValue = (TextView) getActivity().findViewById(R.id.bedroom_tempValue);
		//bedroom_humiValue = (TextView) getActivity().findViewById(R.id.bedroom_humiValue);
		tempCATextview = (TextView) getActivity().findViewById(R.id.igreen_fragment_catemp);//C当前温度 for air空气
		tempCGTextview = (TextView) getActivity().findViewById(R.id.igreen_fragment_cgtemp);//C当前温度 for air
		humiCATextview = (TextView) getActivity().findViewById(R.id.igreen_fragment_cahumi);//C当前湿度 for ground 土壤
		humiCGTextview = (TextView) getActivity().findViewById(R.id.igreen_fragment_cghumi);//C当前湿度 for ground

		/* -------------------------------------------------------
	     *  通过SharedPreferences获取当前温度等数据,显示出来。
	     *  用于fragment切换时候的数据保存
		 * -------------------------------------------------------*/
		apSharedPreferences = getActivity().getSharedPreferences("tempdata", Activity.MODE_PRIVATE);
		tempCATextview.setText(apSharedPreferences.getString("temperature", "") + "℃"); //第2个参数是value的默认值
		tempCGTextview.setText(apSharedPreferences.getString("temperature", "")+"℃"); //第2个参数是value的默认值

		/* -------------------------------------------------------
	     *                      视频监控相关
		 * -------------------------------------------------------*/
		videoImageButton = (Button) getActivity().findViewById(R.id.igreen_video_start_button);
		videoImageButton.setOnClickListener(new videoImageButtonListener());

		videoImageView = (ImageView) getActivity().findViewById(R.id.igreen_video_imageview);

		/* -------------------------------------------------------
	     *  通过SharedPreferences获取当前温度等数据,显示出来。
	     *  用于fragment切换时候的数据保存
		 * -------------------------------------------------------*/
		apSharedPreferences = getActivity().getSharedPreferences("demo", Activity.MODE_PRIVATE);
		if(apSharedPreferences.getString("demo", "").equals("on"))
		{
			videoImageView.setImageResource(R.drawable.demo_on8);
		}
		else
		{
			videoImageView.setImageResource(R.drawable.demo_on1);
		}
		videoRelativelayout = (RelativeLayout) getActivity().findViewById(R.id.igreen_video_dislayout);
	}

	class staticsButtonListenner implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent tempIntent = new Intent();

			//getActivity().startActivity(tempIntent);
		}
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
				else if(typeString.equals("humi"))/*发送给第一个ihome fragment*/
				{
					String humiString = intent.getStringExtra("humi");
					humiCATextview.setText(humiString+"%");
					humiCGTextview.setText(humiString+"%");
				}
			}
		}//onReceive


	}

	class tempCtrlButtonListener implements  OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setAction(intent.ACTION_EDIT);
			intent.putExtra("type", "tempCtrl");
			getActivity().sendBroadcast(intent);
		}
	}

	class videoImageButtonListener implements  OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.igreen_video_start_button)
			{
				if(video_start == true)//关闭视频
				{
					video_start = false;
					videoImageButton.setBackground(getResources().getDrawable(R.drawable.igreen_video_unselected));
					videoRelativelayout.setVisibility(View.GONE);
				}else { //开启视频
					video_start = true;
					videoImageButton.setBackground(getResources().getDrawable(R.drawable.igreen_video_selected));
					videoRelativelayout.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	/**
	 * @Function: private class ContrlReceiver extends BroadcastReceiver
	 * @Description:
	 *      接受来自Service的信息，并且转发给相应fragment来改变相应组件内容
	 **/
	private class ContrlReceiver extends BroadcastReceiver {

		public ContrlReceiver() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String typeString = intent.getStringExtra("type");
			/* -----------------------------------------
			 * 处理主activity接收到的广播
			 * -----------------------------------------*/
			if (typeString.equals("wifi_internet")) {
				String stateString = intent.getStringExtra("wifi_internet");
				if (stateString.equals("disconnect")) {
					videoOkFlag = false;
					videoImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.color.black));
				} else if (stateString.equals("connect")) {
				} else if (stateString.equals("error")) {
					videoOkFlag = false;
					videoImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.color.black));
				} else if (stateString.equals("authed")) {
					videoOkFlag = true;
					/* -------------------------------------------------------
	     			 *  demo
		 			 * -------------------------------------------------------*/
					SharedPreferences demotemp = getActivity().getSharedPreferences("demo", Activity.MODE_PRIVATE);
					if(demotemp.getString("demo", "").equals("on"))
					{
						videoImageView.setImageResource(R.drawable.demo_on8);
					}
					else
					{
						videoImageView.setImageResource(R.drawable.demo_on1);
					}
				}
			}
		}
	}
	
}