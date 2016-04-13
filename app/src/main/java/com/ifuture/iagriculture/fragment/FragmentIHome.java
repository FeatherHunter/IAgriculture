package com.ifuture.iagriculture.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ImageView;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonFloatSmall;
import com.gc.materialdesign.views.ButtonRectangle;
import com.hb.views.PinnedSectionListView;
import com.ifuture.iagriculture.activity.BandTerminalDialog;
import com.ifuture.iagriculture.activity.CreateAreaDialog;
import com.ifuture.iagriculture.adapter.HomeListViewAdapter;
import com.ifuture.iagriculture.bottombar.*;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.listview.ListViewItem;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;

import java.util.ArrayList;

/** 
 * @CopyRight: 王辰浩 2016~2026
 * @Author Feather Hunter(猎羽)
 * @qq:975559549
 * @Version:1.0 
 * @Date: 2016/4/12
 * @Description: IHome的Fragment界面。用于选择地区、大棚，也可以绑定地区，大棚和设备号。
 * @FunctionList:
 *   1. onCreateView; //初始化布局和listview
 *   2. init_listview
 *   2. Handler communicationHandler;     //用于处理和ClientMainActivity的通信
 **/

public class FragmentIHome extends BaseFragment{

	//private RecvReceiver recvReceiver;
	private String RECV_ACTION = "android.intent.action.ANSWER";


	DatabaseOperation databaseOperation = null; //数据库帮助操作的类
	ImageView videoImageView;

	boolean buttonFloatIsGone = true; //悬浮按钮显示

	SharedPreferences apSharedPreferences = null;

	Boolean videoOkFlag = false;
	private ContrlReceiver contrlReceiver;
	private String CONTRL_ACTION = "android.intent.action.EDIT";

	ButtonFloat totalButtonFloat = null;
	ButtonFloatSmall addAreaButtonFloatSmall = null;
	ButtonFloatSmall addTerminalButtonFloatSmall = null;
	ButtonFloatSmall addDeviceButtonFloatSmall = null;

	ButtonRectangle addAreaButtonRectangle = null;
	ButtonRectangle addTerminalButtonRectangle = null;
	ButtonRectangle addDeviceButtonRectangle = null;

	private PinnedSectionListView listView;
	private HomeListViewAdapter adapter;

	private int REQUEST_AREA = 0;
	private int REQUEST_TERM = 1;
	private int RESULT_OK = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.home_fragment, container, false); //获得该fragment的布局文件
		/*--------------------------------------------
		 *             初始化listview
		 *----------------------------------------------*/
		init_listview(view);
		System.out.println("onCreateView");

		return view;
	}
	/**
	 *  @Function: init_listview
	 *  @description: 初始化listview: 消除滚动条，配置适配器和监听器，设置padding
	 **/
	private void init_listview(View view){

		listView = (PinnedSectionListView)view.findViewById(R.id.home_listview); //获得listview
		/*-----------------------------------------------
		 *             消除滚动条
		 *-----------------------------------------------*/
		listView.setFastScrollEnabled(false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			listView.setFastScrollAlwaysVisible(false);
		}
		/*-----------------------------------------------
		 *             配置适配器和按键监听器
		 *----------------------------------------------*/
		adapter = new HomeListViewAdapter(getActivity(), ListViewItem.getData(getActivity()));
		listView.setAdapter(adapter);//设置适配器
		listView.setOnItemClickListener(getListenerForListView());

		/*-----------------------------------------------
		 *             设置Listview周围的padding
		 *----------------------------------------------*/
		float density = getResources().getDisplayMetrics().density;
		int padding = (int) (1 * density);
		listView.setPadding(padding, padding, padding, padding);
	}

	public void refreshListView() {
		System.out.println("refreshListView");
		ArrayList<ListViewItem> list = new ArrayList<ListViewItem>();

		String areaNames[] = databaseOperation.queryAreaName(getActivity());
		for(int i = 0; areaNames[i] != null; i++)
		{
			list.add(new ListViewItem(ListViewItem.SECTION, areaNames[i]));
		}
		adapter.refresh(list);
	}


	/**
	 *  @Function: OnItemClickListener
	 *  @description: listview的按键监听器
	 **/
	private AdapterView.OnItemClickListener getListenerForListView() {
		// TODO Auto-generated method stub
		return new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				// TODO Auto-generated method stub
				if(position>0){
					ListViewItem item=adapter.getItem(position-1);
					if(item.type==ListViewItem.ITEM){
						System.out.println("" + item.text);
					}
				}
			}//end of onItemClick
		};//end of OnItemClickListener()
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		SharedPreferences apSharedPreferences = getActivity().getSharedPreferences("saved", Activity.MODE_PRIVATE);
		String accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
		databaseOperation = new DatabaseOperation(accountString); //使用用户名创建数据库
		databaseOperation.createDatabase(getActivity());//创建数据库

		/*---------------------------------------------------------------------------
		 *                 右下角悬浮按钮 （获取）
		 *---------------------------------------------------------------------------*/
		totalButtonFloat = (ButtonFloat) getActivity().findViewById(R.id.buttonFloat_total);
		addAreaButtonFloatSmall = (ButtonFloatSmall) getActivity().findViewById(R.id.home_bfSmall_addArea);
		addTerminalButtonFloatSmall = (ButtonFloatSmall) getActivity().findViewById(R.id.home_bfSmall_addTerminal);
		addDeviceButtonFloatSmall = (ButtonFloatSmall) getActivity().findViewById(R.id.home_bfSmall_addDevice);

		addAreaButtonRectangle = (ButtonRectangle) getActivity().findViewById(R.id.home_addArea_text);
		addTerminalButtonRectangle = (ButtonRectangle) getActivity().findViewById(R.id.home_addTerminal_text);
		addDeviceButtonRectangle = (ButtonRectangle) getActivity().findViewById(R.id.home_addDevice_text);

		totalButtonFloat.setOnClickListener(new buttonFloatTotalListenner()); //设置总开关的监听器

		addAreaButtonFloatSmall.setOnClickListener(new buttonFloatSmallListenner()); //设置创建区域按钮
		addAreaButtonRectangle.setOnClickListener(new buttonRectangleListenner());

		addTerminalButtonFloatSmall.setOnClickListener(new buttonFloatSmallListenner()); //设置创建区域按钮


//
//		video_start = false; //默认视频关闭
//
//		/* -------------------------------------------------------
//		 *  动态注册receiver
//		 * -------------------------------------------------------*/
//		try {
//			recvReceiver = new RecvReceiver();
//			IntentFilter filter = new IntentFilter();
//			filter.addAction(RECV_ACTION);
//			getActivity().registerReceiver(recvReceiver, filter);//注册
//		} catch (IllegalArgumentException  e) {
//			// TODO: handle exception
//			System.out.println("fragmentIHome registerReceiver");
//		}
//
//		try {
//			/*动态注册receiver*/
//			contrlReceiver = new ContrlReceiver();
//			IntentFilter filter = new IntentFilter();
//			filter.addAction(CONTRL_ACTION);
//			getActivity().registerReceiver(contrlReceiver, filter);//注册
//		} catch (IllegalArgumentException  e) {
//			// TODO: handle exception
//			System.out.println("had been registerReceiver");
//		}
//
//
//		/* -------------------------------------------------------
//	     *  通过SharedPreferences获取当前温度等数据,显示出来。
//	     *  用于fragment切换时候的数据保存
//		 * -------------------------------------------------------*/
//		apSharedPreferences = getActivity().getSharedPreferences("tempdata", Activity.MODE_PRIVATE);
//		tempCATextview.setText(apSharedPreferences.getString("temperature", "") + "℃"); //第2个参数是value的默认值
//		tempCGTextview.setText(apSharedPreferences.getString("temperature", "")+"℃"); //第2个参数是value的默认值
//
//
//		/* -------------------------------------------------------
//	     *  通过SharedPreferences获取当前温度等数据,显示出来。
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

	/**
	 * @Function: private void setBFGone()
	 * @Description:
	 *      将所有悬浮按钮去除
	 **/
	private void setBFGone()
	{
		addAreaButtonFloatSmall.setVisibility(View.GONE);
		addTerminalButtonFloatSmall.setVisibility(View.GONE);
		addDeviceButtonFloatSmall.setVisibility(View.GONE);

		addAreaButtonRectangle.setVisibility(View.GONE);
		addTerminalButtonRectangle.setVisibility(View.GONE);
		addDeviceButtonRectangle.setVisibility(View.GONE);

		buttonFloatIsGone = true;
	}

	/**
	 * @Function: private void setBFVisibility()
	 * @Description:
	 *      将所有悬浮按钮显示出来
	 **/
	private void setBFVisibility()
	{
		addAreaButtonFloatSmall.setVisibility(View.VISIBLE);
		addTerminalButtonFloatSmall.setVisibility(View.VISIBLE);
		addDeviceButtonFloatSmall.setVisibility(View.VISIBLE);

		addAreaButtonRectangle.setVisibility(View.VISIBLE);
		addTerminalButtonRectangle.setVisibility(View.VISIBLE);
		addDeviceButtonRectangle.setVisibility(View.VISIBLE);

		buttonFloatIsGone = false;
	}

	/**
	 * @Function: class buttonFloatTotalListenner
	 * @Description:
	 *      悬浮按钮的总开关
	 **/
	class buttonFloatTotalListenner implements OnClickListener{

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.buttonFloat_total)
			{
				if(buttonFloatIsGone)
				{
					setBFVisibility();
				}
				else
				{
					setBFGone();
				}
			}
		}
	}

	/**
	 * @Function: class buttonFloatSmallListenner
	 * @Description:
	 *      三种功能：
	 *       1. 创建区域
	 *       2. 绑定终端
	 *       3. 绑定设备
	 **/
	class buttonFloatSmallListenner implements OnClickListener{

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.home_bfSmall_addArea)
			{
				Intent intent = new Intent(getActivity(), CreateAreaDialog.class);
				intent.putExtra("area_number", databaseOperation.queryAreaCount(getActivity()));//传入固定好的地区号(已有的地区号+1)
				startActivityForResult(intent, REQUEST_AREA); //打开创建地区的对话框，REQUEST_AREA是标志
			}
			else if(id == R.id.home_bfSmall_addTerminal)
			{
				Intent intent = new Intent(getActivity(), BandTerminalDialog.class);
				startActivityForResult(intent, REQUEST_TERM); //打开创建地区的对话框，REQUEST_AREA是标志
			}
			setBFGone();
		}
	}

	/**
	 * @Function: public void onActivityResult(int requestCode, int resultCode, Intent data)
	 * @Description:
	 * 		处理打开的dialog类型的activity的返回值，用于刷新列表之类的操作
	 **/
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_AREA) { //为创建地区的返回值
			System.out.println("REQUEST_AREA");
			if (resultCode == RESULT_OK) { //创建成功
				System.out.println("RESULT_OK");
				refreshListView(); //刷新显示
			}
		}
	}

	/**
	 * @Function: class buttonRectangleListenner
	 * @Description:
	 *      三种功能：
	 *       1. 创建区域
	 *       2. 绑定终端
	 *       3. 绑定设备
	 **/
	class buttonRectangleListenner implements OnClickListener{
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.home_addArea_text)
			{
				Intent intent = new Intent(getActivity(), CreateAreaDialog.class);
				startActivityForResult(intent, 0);
			}
			setBFGone();
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