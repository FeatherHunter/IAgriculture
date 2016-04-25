package com.ifuture.iagriculture.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ifuture.iagriculture.Device.Device;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.activity.ClientMainActivity;
import com.ifuture.iagriculture.bottombar.BaseFragment;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;

import java.util.ArrayList;

/**======================================================================================================
 * @CopyRight: 王辰浩 2016~2026
 * @Author Feather Hunter(猎羽)
 * @qq:975559549
 * @Version:1.0 
 * @Date: 2016/4/15
 * @Description: 大棚的Fragment界面
 * @Function List:
 *   1. void onAttach(Activity activity); //用于绑定ClientMainActivity和handler
 *   2. Handler communicationHandler;     //用于处理和ClientMainActivity的通信
 *=======================================================================================================*/

public class FragmentGreenHouse extends BaseFragment{

	ClientMainActivity mainActivity; //主activty

	private RecvReceiver recvReceiver;
	private String RECV_ACTION = "android.intent.action.ANSWER";
	TextView tempCurrenAirTextview;//C当前温度 for air
	TextView humiCurrenAirTextview;//C当前湿度 for air

	ImageView videoImageView;

	Button videoImageButton;
	boolean video_start = false;

	Boolean videoOkFlag = false;
	private ContrlReceiver contrlReceiver;
	private String CONTRL_ACTION = "android.intent.action.EDIT";

	/* -----------------------------------------
	 *    记录当前fragmeent表示的地区号，设备号
	 * -----------------------------------------*/
	String areaNumString = null;
	String greenHouseNumString = null;

	ArrayList<Device> deviceList = new ArrayList<Device>();   //设备列表

	DatabaseOperation databaseOperation = null; //数据库操作类

	@Override
	public void onCreate(Bundle savedInstanceState)
	 {
		 super.onCreate(savedInstanceState);
//		  Bundle bundle = getArguments();
//		  if (bundle != null)
//		  {
//			  areaNumString = bundle.getString("area");
//			  greenHouseNumString = bundle.getString("greenhouse");
//		  }
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_greenhouse, container, false);
	}

	/**-----------------------------------------------------------
	 *  @Function: public void onAttach(Activity activity)
	 *  @Description: 设置大棚handler，处理信息
	 *----------------------------------------------------------*/
	@Override
	public void onAttach(Activity activity){
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mainActivity = (ClientMainActivity) activity;
		//mainActivity.setGreenHouseHandler(greenHouseHandler);
	}
//	/**-----------------------------------------------------------
//	 *  @Handler:
//	 *  @Description: 处理ClientMainActivity传递来的信息
//	 *    例如：接收到地区号,大棚号
//	 *----------------------------------------------------------*/
//	public Handler greenHouseHandler = new Handler()
//	{
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			Bundle bundle = msg.getData();
//			areaNumString = bundle.getString("area");
//			greenHouseNumString = bundle.getString("greenhouse");
//		}
//	};

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		areaNumString = mainActivity.areaNumString;
		greenHouseNumString =  mainActivity.greenhouseNumString;

		/* -----------------------------------------------------------------
	     *             整个大棚当前温度湿度的实时显示，这里用于获取控件
	     * -----------------------------------------------------------------*/
		tempCurrenAirTextview = (TextView) getActivity().findViewById(R.id.gh_air_ctemp_value);//C当前温度 for air
		humiCurrenAirTextview = (TextView) getActivity().findViewById(R.id.gh_air_chumi_value);//C当前湿度 for air

		/* -----------------------------------------------------------------
	     *             利用用户名创建or获得数据库
	     * -----------------------------------------------------------------*/
		SharedPreferences apSharedPreferences = getActivity().getSharedPreferences("saved", Activity.MODE_PRIVATE);
		String accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
		databaseOperation = new DatabaseOperation(accountString); //使用用户名创建数据库
		databaseOperation.createDatabase(getActivity());//创建数据库

		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;

		String devices[] = databaseOperation.queryDevicePerGHouse(getActivity(), Integer.parseInt(areaNumString), greenHouseNumString); //查询设备号
		int deviceCount = 0;
		for(int i = 0; devices[i]!=null; i++)
		{
			deviceCount++;
		}
// 获取xml的RelativeLayout
		RelativeLayout ghLayout = (RelativeLayout) getActivity().findViewById(R.id.gh_device_layout);

		int i;
		for (i = 0; i < deviceCount/2; i++) {
			// 每行都有一个linearlayout
			LinearLayout linearLayout = new LinearLayout(getActivity());
			linearLayout.setId(i + 10);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout.LayoutParams lLayoutlayoutParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			linearLayout.setLayoutParams(lLayoutlayoutParams);

			View view1 = View.inflate(getActivity(), R.layout.greenhouse_device, null);
			View view2 = View.inflate(getActivity(), R.layout.greenhouse_device, null);

			//每一个设备框的大小
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					width/2 - 10, 400);
			//将linearLayout加入到relative布局中
			RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

			view1.setLayoutParams(layoutParams);
			view2.setLayoutParams(layoutParams);

			/*将第一个设备加入到list中*/
			TextView tempValue = (TextView) view1.findViewById(R.id.device_temp);
			TextView humiValue = (TextView) view1.findViewById(R.id.device_humi);
			Switch warmDeviceState = (Switch) view1.findViewById(R.id.device_warm_switch);
			Switch irriDeviceState = (Switch) view1.findViewById(R.id.device_irrigation_switch);

			Device device1 = new Device(devices[i*2], tempValue, humiValue, warmDeviceState, irriDeviceState);
			if(device1 != null)
			   deviceList.add(device1);

			tempValue = (TextView) view2.findViewById(R.id.device_temp);
			humiValue = (TextView) view2.findViewById(R.id.device_humi);
			warmDeviceState = (Switch) view2.findViewById(R.id.device_warm_switch);
			irriDeviceState = (Switch) view2.findViewById(R.id.device_irrigation_switch);

			Device device2 = new Device(devices[i*2+1], tempValue, humiValue, warmDeviceState, irriDeviceState);
			if(device2 != null)
				deviceList.add(device2);

			// 添加到每行的linearlayout中
			linearLayout.addView(view1);
			linearLayout.addView(view2);

			// 每个linearlayout都在前一个的下面，第一个在顶,不处理
			if (i > 0) {
				relativeParams.addRule(RelativeLayout.BELOW, i + 10 - 1);
			}

			// 把每个linearlayout加到relativelayout中
			ghLayout.addView(linearLayout, relativeParams);
		}
		if(deviceCount%2 == 1)
		{
			// 每行都有一个linearlayout
			LinearLayout linearLayout = new LinearLayout(getActivity());
			linearLayout.setId(i + 10);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout.LayoutParams lLayoutlayoutParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			linearLayout.setLayoutParams(lLayoutlayoutParams);

			View view1 = View.inflate(getActivity(), R.layout.greenhouse_device, null);

			//每一个设备框的大小
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					width/2 - 10, 400);
			//将linearLayout加入到relative布局中
			RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

			view1.setLayoutParams(layoutParams);

			/*将第一个设备加入到list中*/
			TextView tempValue = (TextView) view1.findViewById(R.id.device_temp);
			TextView humiValue = (TextView) view1.findViewById(R.id.device_humi);
			Switch warmDeviceState = (Switch) view1.findViewById(R.id.device_warm_switch);
			Switch irriDeviceState = (Switch) view1.findViewById(R.id.device_irrigation_switch);

			deviceList.add(new Device(devices[i*2], tempValue, humiValue, warmDeviceState, irriDeviceState));


			// 添加到每行的linearlayout中
			linearLayout.addView(view1);

			relativeParams.addRule(RelativeLayout.BELOW, i + 10 - 1);


			// 把每个linearlayout加到relativelayout中
			ghLayout.addView(linearLayout, relativeParams);
		}
//		greenHouseDeviceLayout = (RelativeLayout) getActivity().findViewById(R.id.gh_device_layout);
//
//		View view = View.inflate(getActivity(), R.layout.greenhouse_device, null);
//		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(500, 300,
//				Gravity.LEFT);
//
//		params.bottomMargin = 50 ;
//		params.rightMargin = 50;
//
//		greenHouseDeviceLayout.addView(view, params);
//		TextView temp = (TextView) view.findViewById(R.id.device_temp);
//		temp.setText("24");

//		view = View.inflate(getActivity(), R.layout.greenhouse_device, null);
//		params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
//				Gravity.BOTTOM|Gravity.LEFT);
//
//		params.bottomMargin = 50 ;
//		params.rightMargin = 50;
//
//		greenHouseDeviceLayout.addView(view, params);
//		temp = (TextView) view.findViewById(R.id.device_temp);
//		temp.setText("28");
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
//		/* -------------------------------------------------------
//	     *  通过SharedPreferences获取当前温度等数据,显示出来。
//	     *  用于fragment切换时候的数据保存
//		 * -------------------------------------------------------*/
//		apSharedPreferences = getActivity().getSharedPreferences("tempdata", Activity.MODE_PRIVATE);
//		tempCATextview.setText(apSharedPreferences.getString("temperature", "") + "℃"); //第2个参数是value的默认值
//		tempCGTextview.setText(apSharedPreferences.getString("temperature", "")+"℃"); //第2个参数是value的默认值
//
//		/* -------------------------------------------------------
//	     *                      视频监控相关
//		 * -------------------------------------------------------*/
//		videoImageButton = (Button) getActivity().findViewById(R.id.igreen_video_start_button);
//		videoImageButton.setOnClickListener(new videoImageButtonListener());
//
//		videoImageView = (ImageView) getActivity().findViewById(R.id.igreen_video_imageview);
//
//		/* -------------------------------------------------------
//	     *  通过SharedPreferences获取当前视频状态
//	     *  用于fragment切换时候的数据保存
//		 * -------------------------------------------------------*/
//		apSharedPreferences = getActivity().getSharedPreferences("demo", Activity.MODE_PRIVATE);
//		if(apSharedPreferences.getString("demo", "").equals("on"))
//		{
//			videoImageView.setImageResource(R.drawable.demo_on8);
//		}
//		else
//		{
//			videoImageView.setImageResource(R.drawable.demo_on1);
//		}
//		videoRelativelayout = (RelativeLayout) getActivity().findViewById(R.id.igreen_video_dislayout);
	}

	class staticsButtonListenner implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent tempIntent = new Intent();

			//getActivity().startActivity(tempIntent);
		}
	}

	/**------------------------------------------------------------------------
	 * @Function: private class ContrlReceiver extends BroadcastReceiver
	 * @Description:
	 *      接受来自Service的信息，并且转发给相应fragment来改变相应组件内容
	 *------------------------------------------------------------------------*/
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
				System.out.println("RecvReceiver=======================RecvReceiver====================");
				String areaString = intent.getStringExtra("area");
				String greenhouseString = intent.getStringExtra("greenhouse");
				String deviceString = intent.getStringExtra("device");
				if(areaString!=null && greenhouseString!=null)
				{
					if(areaString == areaNumString && greenhouseString == greenHouseNumString)
					{
						if(typeString.equals("temp"))/*发送给第一个ihome fragment*/
						{
							String tempString = intent.getStringExtra("temp");
							tempCurrenAirTextview.setText(tempString);
						}
						else if(typeString.equals("humi"))/*发送给第一个ihome fragment*/
						{
							String humiString = intent.getStringExtra("humi");
							humiCurrenAirTextview.setText(humiString);
						}
					}
				}//end of 地区号，大棚号
			}//end of typeString
		}//onReceive
	}

	class videoImageButtonListener implements  OnClickListener{

		@Override
		public void onClick(View v) {
//			if(v.getId() == R.id.igreen_video_start_button)
//			{
//				if(video_start == true)//关闭视频
//				{
//					video_start = false;
//					videoImageButton.setBackground(getResources().getDrawable(R.drawable.igreen_video_unselected));
//					videoRelativelayout.setVisibility(View.GONE);
//				}else { //开启视频
//					video_start = true;
//					videoImageButton.setBackground(getResources().getDrawable(R.drawable.igreen_video_selected));
//					videoRelativelayout.setVisibility(View.VISIBLE);
//				}
//			}
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
//			String typeString = intent.getStringExtra("type");
//			/* -----------------------------------------
//			 * 处理主activity接收到的广播
//			 * -----------------------------------------*/
//			if (typeString.equals("wifi_internet")) {
//				String stateString = intent.getStringExtra("wifi_internet");
//				if (stateString.equals("disconnect")) {
//					videoOkFlag = false;
//					videoImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.color.black));
//				} else if (stateString.equals("connect")) {
//				} else if (stateString.equals("error")) {
//					videoOkFlag = false;
//					videoImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.color.black));
//				} else if (stateString.equals("authed")) {
//					videoOkFlag = true;
//					/* -------------------------------------------------------
//	     			 *  demo
//		 			 * -------------------------------------------------------*/
//					SharedPreferences demotemp = getActivity().getSharedPreferences("demo", Activity.MODE_PRIVATE);
//					if(demotemp.getString("demo", "").equals("on"))
//					{
//						videoImageView.setImageResource(R.drawable.demo_on8);
//					}
//					else
//					{
//						videoImageView.setImageResource(R.drawable.demo_on1);
//					}
//				}
//			}
		}
	}
	
}