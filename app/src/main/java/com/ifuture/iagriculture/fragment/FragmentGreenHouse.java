package com.ifuture.iagriculture.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ifuture.iagriculture.Device.Device;
import com.ifuture.iagriculture.Instruction.Instruction;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.activity.ClientMainActivity;
import com.ifuture.iagriculture.activity.SettingActivity;
import com.ifuture.iagriculture.bottombar.BaseFragment;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;
import java.util.ArrayList;
import java.util.Hashtable;

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

	TextView deviceWarmSum = null;   		//取暖设备总数
	TextView deviceWarmOnSum = null; 		//打开取暖设备数量
	TextView deviceWarmOffSum = null;		//关闭取暖设备数量
	TextView deviceIrrigationSum = null;	//灌溉设备总数
	TextView deviceIrrigationOnSum = null;  //打开灌溉设备数量
	TextView deviceIrrigationOffSum = null; //关闭灌溉设备数量

	boolean autoTempOnFlag = false;
	boolean autoHumiOnFlag = false;

	//记录各个设备数目
	int warmSum = 0;
	int warmOnSum = 0;
	int irriSum = 0;
	int irriOnSum = 0;

	Button settingButton;
	android.widget.Switch autoTempSwitch = null;//智能温度开关
	android.widget.Switch autoHumiSwitch = null;//智能湿度开关

	private String CONTRL_ACTION = "android.intent.action.EDIT";

	/* -----------------------------------------
	 *    记录当前fragmeent表示的地区号，设备号
	 * -----------------------------------------*/
	String areaNumString = null;
	String greenHouseNumString = null;

	Hashtable<String, Device> deviceHashtable = new Hashtable<String, Device>();
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

		/* --------------------------------------------------------------------
	     *                          设备总体情况显示控件获取
	     * --------------------------------------------------------------------*/
		deviceWarmSum = (TextView) getActivity().findViewById(R.id.gh_device_warm_totalsum);
		deviceWarmOnSum = (TextView) getActivity().findViewById(R.id.gh_device_warm_onsum);
		deviceWarmOffSum = (TextView) getActivity().findViewById(R.id.gh_device_warm_offsum);

		deviceIrrigationSum = (TextView) getActivity().findViewById(R.id.gh_device_irrigation_totalsum);
		deviceIrrigationOnSum = (TextView) getActivity().findViewById(R.id.gh_device_irrigation_onsum);
		deviceIrrigationOffSum = (TextView) getActivity().findViewById(R.id.gh_device_irrigation_offsum);

		/* -----------------------------------------------------------------
	     *             整个大棚当前温度湿度的实时显示，这里用于获取控件
	     * -----------------------------------------------------------------*/
		tempCurrenAirTextview = (TextView) getActivity().findViewById(R.id.gh_air_ctemp_value);//C当前温度 for air
		humiCurrenAirTextview = (TextView) getActivity().findViewById(R.id.gh_air_chumi_value);//C当前湿度 for air

		settingButton = (Button) getActivity().findViewById(R.id.gh_setting_button); //进行设置的按钮
		settingButton.setOnClickListener(new settingButtonListenner()); //设置按钮监听器

		autoTempSwitch = (android.widget.Switch) getActivity().findViewById(R.id.gh_air_autotemp_switch);//温控开关
		autoHumiSwitch = (android.widget.Switch) getActivity().findViewById(R.id.gh_air_autohumi_switch);//湿控开关

//		autoTempSwitch.setOnCheckedChangeListener(new autoSwitchOnCheckedChangeListenner());//设置自动温控监听器
//		autoHumiSwitch.setOnCheckedChangeListener(new autoSwitchOnCheckedChangeListenner());//设置自动湿控监听器

		autoTempSwitch.setOnCheckedChangeListener(new autoSwitchOnCheckedChangeListenner()); //设置自动温控监听器
		autoHumiSwitch.setOnCheckedChangeListener(new autoSwitchOnCheckedChangeListenner()); //设置自动温控监听器

		LoadDeviceAsyncTask loadDeviceAsyncTask = new LoadDeviceAsyncTask();//显示设备列表
		loadDeviceAsyncTask.execute();

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

	}


	private class LoadDeviceAsyncTask extends AsyncTask{

		int deviceCount = 0;
		String devices[];
		@Override
		protected Object doInBackground(Object[] params) {
		/* -----------------------------------------------------------------
	     *             利用用户名创建or获得数据库
	     * -----------------------------------------------------------------*/
			SharedPreferences apSharedPreferences = getActivity().getSharedPreferences("saved", Activity.MODE_PRIVATE);
			String accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
			databaseOperation = new DatabaseOperation(accountString); //使用用户名创建数据库
			databaseOperation.createDatabase(getActivity());//创建数据库

			devices = databaseOperation.queryDevicePerGHouse(getActivity(), Integer.parseInt(areaNumString), greenHouseNumString); //查询设备号
			deviceCount = 0;
			for(int i = 0; devices[i]!=null; i++)
			{
				deviceCount++;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object o) {
			super.onPostExecute(o);
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			int width = dm.widthPixels;

			warmSum = deviceCount; //取暖设备数目
			irriSum = deviceCount; //灌溉设备数目
			warmOnSum = irriOnSum = 0;
			deviceWarmSum.setText(""+warmSum); //总数
			deviceIrrigationSum.setText(""+irriSum);
			deviceWarmOffSum.setText(""+(warmSum-warmOnSum)); //关闭的数量
			deviceIrrigationOffSum.setText(""+(irriSum-irriOnSum));
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
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width/2 - 10, 500);
				//将linearLayout加入到relative布局中
				RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

				view1.setLayoutParams(layoutParams);
				view2.setLayoutParams(layoutParams);

			/* -----------------------------------------------------------------
	         *             获取设备1加入到HashTable中
	         *             获取温度、湿度switch并且设置监听器
	         * -----------------------------------------------------------------*/
				TextView tempValue = (TextView) view1.findViewById(R.id.device_temp);
				TextView humiValue = (TextView) view1.findViewById(R.id.device_humi);
				Switch warmDeviceState = (Switch) view1.findViewById(R.id.device_warm_switch);
				Switch irriDeviceState = (Switch) view1.findViewById(R.id.device_irrigation_switch);

				warmDeviceState.setOnCheckedChangeListener(new switchWarmOnCheckedChangeListener(devices[i*2]));
				Device device1 = new Device(devices[i*2], tempValue, humiValue, warmDeviceState, irriDeviceState);
				deviceHashtable.put(devices[i*2],device1);

			/* -----------------------------------------------------------------
	         *             获取设备1加入到HashTable中
	         *             获取温度、湿度switch并且设置监听器
	         * -----------------------------------------------------------------*/
				tempValue = (TextView) view2.findViewById(R.id.device_temp);
				humiValue = (TextView) view2.findViewById(R.id.device_humi);
				warmDeviceState = (Switch) view2.findViewById(R.id.device_warm_switch);
				irriDeviceState = (Switch) view2.findViewById(R.id.device_irrigation_switch);

				warmDeviceState.setOnCheckedChangeListener(new switchWarmOnCheckedChangeListener(devices[i*2+1]));
				Device device2 = new Device(devices[i*2+1], tempValue, humiValue, warmDeviceState, irriDeviceState);
				deviceHashtable.put(devices[i*2+1],device2);

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
				// 每行的linearlayout
				LinearLayout linearLayout = new LinearLayout(getActivity());
				linearLayout.setId(i + 10);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout.LayoutParams lLayoutlayoutParams = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				linearLayout.setLayoutParams(lLayoutlayoutParams);

				View view1 = View.inflate(getActivity(), R.layout.greenhouse_device, null);

				//每一个设备框的大小
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width/2 - 10, 500);
				//将linearLayout加入到relative布局中
				RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

				view1.setLayoutParams(layoutParams);

			/*将第一个设备加入到list中*/
				TextView tempValue = (TextView) view1.findViewById(R.id.device_temp);
				TextView humiValue = (TextView) view1.findViewById(R.id.device_humi);

			/* -----------------------------------------------------------------
	         *             获取设备的switch并且设置监听器
	         * -----------------------------------------------------------------*/
				Switch warmDeviceState = (Switch) view1.findViewById(R.id.device_warm_switch);
				Switch irriDeviceState = (Switch) view1.findViewById(R.id.device_irrigation_switch);

				warmDeviceState.setOnCheckedChangeListener(new switchWarmOnCheckedChangeListener(devices[i*2]));
				Device device = new Device(devices[i*2], tempValue, humiValue, warmDeviceState, irriDeviceState);
				deviceHashtable.put(devices[i*2],device);

				// 添加到每行的linearlayout中
				linearLayout.addView(view1);
				relativeParams.addRule(RelativeLayout.BELOW, i + 10 - 1);
				// 把每个linearlayout加到relativelayout中
				ghLayout.addView(linearLayout, relativeParams);
			}
		}
	}

	/**------------------------------------------------------------------------
	 * @类名: autoSwitchOnCheckedChangeListenner
	 * @描述:
	 *      监听“智能开关”的状态，并且发送相应指令
	 *------------------------------------------------------------------------*/
	class autoSwitchOnCheckedChangeListenner implements CompoundButton.OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(buttonView.getId() == R.id.gh_air_autotemp_switch)//温度
			{
				if(autoTempOnFlag)//智能温度已经开启，发送关闭指令
				{
					//关闭自动温控
					Instruction.broadcastMsgToServer(getActivity(), Instruction.autoTemp(areaNumString, greenHouseNumString, false));
				}
				else
				{
					//开启自动温控
					Instruction.broadcastMsgToServer(getActivity(), Instruction.autoTemp(areaNumString, greenHouseNumString, true));
				}
				autoTempSwitch.setOnCheckedChangeListener(null); //先注销监听器
				autoTempSwitch.setChecked(autoTempOnFlag);
				autoTempSwitch.setOnCheckedChangeListener(new autoSwitchOnCheckedChangeListenner());//设置监听器
			}
			else if(buttonView.getId() == R.id.gh_air_autohumi_switch)//温度
			{
				if(autoHumiOnFlag)
				{
					//开启自动温控
					Instruction.broadcastMsgToServer(getActivity(), Instruction.autoHumi(areaNumString, greenHouseNumString, true));
				}
				else
				{
					//关闭自动温控
					Instruction.broadcastMsgToServer(getActivity(), Instruction.autoHumi(areaNumString, greenHouseNumString, false));
				}
				autoHumiSwitch.setOnCheckedChangeListener(null); //先注销监听器
				autoHumiSwitch.setChecked(autoHumiOnFlag);
				autoHumiSwitch.setOnCheckedChangeListener(new autoSwitchOnCheckedChangeListenner());//设置监听器
			}

		}
	}

	/**------------------------------------------------------------------------
	 * @Function: switchWarmOnCheckedChangeListener
	 * @Description:
	 *      监听各个独立设备的取暖器switch的开关状态
	 *------------------------------------------------------------------------*/
	class switchWarmOnCheckedChangeListener implements Switch.OnCheckedChangeListener {

		String deviceNum = null;
		public switchWarmOnCheckedChangeListener(String deviceNum)
		{
			Log.d("Debug", deviceNum);
			this.deviceNum = deviceNum;
		}

//		@Override
//		public void onCheck(Switch view, boolean check) {
//			if(deviceNum == null) return;
//			/* -----------------------------------------
//			 *      打开设备（发送指令给服务器，通过Service）
//			 * -----------------------------------------*/
//			if(check) {
//				//Log.d("Debug", "check is true");
//				warmOnSum++;
//				deviceWarmOnSum.setText(""+warmOnSum);//设置开启数量
//				deviceWarmOffSum.setText(""+(warmSum-warmOnSum));//显示已经关闭的取暖器数量
//				broadcastMsgToServer(Instruction.ctrlLamp(areaNumString, greenHouseNumString, deviceNum, true));
//
//			}
//			/* -----------------------------------------
//			 *      关闭取暖灯
//			 * -----------------------------------------*/
//			else {
//				//Log.d("Debug", "check is false");
//				warmOnSum--;
//				deviceWarmOnSum.setText(""+warmOnSum);//已经开启的取暖器数量
//				deviceWarmOffSum.setText(""+(warmSum-warmOnSum));//显示已经关闭的取暖器数量
//				broadcastMsgToServer(Instruction.ctrlLamp(areaNumString, greenHouseNumString, deviceNum, false));
//			}
//		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(deviceNum == null) return;
			/* -----------------------------------------
			 *      打开设备（发送指令给服务器，通过Service）
			 * -----------------------------------------*/
			if(isChecked) {
				//Log.d("Debug", "check is true");
				warmOnSum++;
				deviceWarmOnSum.setText(""+warmOnSum);//设置开启数量
				deviceWarmOffSum.setText(""+(warmSum-warmOnSum));//显示已经关闭的取暖器数量
				broadcastMsgToServer(Instruction.ctrlLamp(areaNumString, greenHouseNumString, deviceNum, true));

			}
			/* -----------------------------------------
			 *      关闭取暖灯
			 * -----------------------------------------*/
			else {
				//Log.d("Debug", "check is false");
				warmOnSum--;
				deviceWarmOnSum.setText(""+warmOnSum);//已经开启的取暖器数量
				deviceWarmOffSum.setText(""+(warmSum-warmOnSum));//显示已经关闭的取暖器数量
				broadcastMsgToServer(Instruction.ctrlLamp(areaNumString, greenHouseNumString, deviceNum, false));
			}
		}
	}

	/**-------------------------------------------------------------------
	 * 	 @Function: private void broadcastMsgToServer(String msg)
	 * 	 @Description: 发送控制等信息给广播
	 * 	 @param msg 需要发送的信息
	 *----------------------------------------------------------------------*/
	private void broadcastMsgToServer(String msg)
	{
		Intent intent = new Intent();
		intent.setAction(intent.ACTION_MAIN);
		intent.putExtra("type", "send");
		intent.putExtra("send", msg);
		getActivity().sendBroadcast(intent);
	}

	class settingButtonListenner implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("area", areaNumString);              //地区
			intent.putExtra("greenhouse", greenHouseNumString);  //大棚
			intent.setClass(getActivity(), SettingActivity.class);

			startActivity(intent);//开启设置的activity
			//getActivity().startActivity(tempIntent);
		}
	}

	/**------------------------------------------------------------------------
	 * @Function: private class ContrlReceiver extends BroadcastReceiver
	 * @Description:
	 *      接受来自Service的信息，并且转发给相应fragment来改变相应组件内容
	 *
	 * @Debug :
	 *  1. areaString.equals(areaNumString) ---写成areaString == areaNumString
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
				String areaString = intent.getStringExtra("area");
				String greenhouseString = intent.getStringExtra("greenhouse");
				String deviceString = intent.getStringExtra("device");
				if(areaString!=null && greenhouseString!=null)
				{
					if(areaString.equals(areaNumString) && greenhouseString.equals(greenHouseNumString))
					{
						if(typeString.equals("temp"))/*发送给第一个ihome fragment*/
						{
							String tempString = intent.getStringExtra("temp");
							tempCurrenAirTextview.setText(tempString);

							/* ------------------------------------------------------
			 				 *      通过设备号获取Device，并且改变其中的值
			                 * --------------------------------------------------*/
							Device device = deviceHashtable.get(deviceString);
							device.getTempValue().setText(tempString);
						}
						else if(typeString.equals("humi"))/*发送给第一个ihome fragment*/
						{
							String humiString = intent.getStringExtra("humi");
							humiCurrenAirTextview.setText(humiString);
						}
						else if(typeString.equals("autohumi")) //返回“智能湿度控制”状态
						{
							boolean switchState = intent.getBooleanExtra("switch", autoHumiOnFlag);
							autoHumiOnFlag = switchState;

							autoHumiSwitch.setOnCheckedChangeListener(null); //先注销监听器
							autoHumiSwitch.setChecked(autoHumiOnFlag); //开关 开/关
							autoHumiSwitch.setOnCheckedChangeListener(new autoSwitchOnCheckedChangeListenner());//设置监听器
						}
						else if(typeString.equals("autotemp"))//返回“智能湿度控制”状态
						{
							boolean switchState = intent.getBooleanExtra("switch", autoTempOnFlag);
							autoTempOnFlag = switchState;

							autoTempSwitch.setOnCheckedChangeListener(null); //先注销监听器
							autoTempSwitch.setChecked(autoTempOnFlag); //开关 开/关
							autoTempSwitch.setOnCheckedChangeListener(new autoSwitchOnCheckedChangeListenner());//设置监听器
						}
					}
				}//end of 地区号，大棚号
			}//end of typeString
		}//onReceive
	}

	
}