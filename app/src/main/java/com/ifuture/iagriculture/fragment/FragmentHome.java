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
import com.ifuture.iagriculture.activity.BandDeviceDialog;
import com.ifuture.iagriculture.activity.BandTerminalDialog;
import com.ifuture.iagriculture.activity.ClientMainActivity;
import com.ifuture.iagriculture.activity.CreateAreaDialog;
import com.ifuture.iagriculture.activity.CreateGHouseDialog;
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

public class FragmentHome extends BaseFragment{

	DatabaseOperation databaseOperation = null; //数据库帮助操作的类

	boolean buttonFloatIsGone = true; //悬浮按钮显示

	ButtonFloat totalButtonFloat = null;
	ButtonFloatSmall addAreaButtonFloatSmall = null;
	ButtonFloatSmall addGHouseButtonFloatSmall = null;       //大棚悬浮按钮
	ButtonFloatSmall addTerminalButtonFloatSmall = null;
	ButtonFloatSmall addDeviceButtonFloatSmall = null;

	ButtonRectangle addAreaButtonRectangle = null;
	ButtonRectangle addGHouseButtonRectangle = null;        //大棚悬浮按钮旁文本按钮
	ButtonRectangle addTerminalButtonRectangle = null;
	ButtonRectangle addDeviceButtonRectangle = null;

	private PinnedSectionListView listView;
	private HomeListViewAdapter adapter;

	private int REQUEST_AREA   = 0;
	private int REQUEST_TERM   = 1;
	private int REQUEST_GHOUSE = 2;
	private int REQUEST_DEVICE = 3;
	private int RESULT_OK = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.fragment_home, container, false); //获得该fragment的布局文件
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
			String greenHouseNums[] = databaseOperation.queryGHousePerArea(getActivity(), i); //i就为当前的地区号
			for(int j = 0; greenHouseNums[j] != null; j++)
			{
				list.add(new ListViewItem(ListViewItem.ITEM, "大棚"+greenHouseNums[j], ""+i, greenHouseNums[j]));
			}
		}
		adapter.refresh(list);
	}


	/**------------------------------------------------------------------------------------------
	 *  @Function: OnItemClickListener
	 *  @description: listview的按键监听器
	 *        根据选择的item所在的地区号和大棚号，
	 *        调用ClientMainActivity的switchGreenHouse进行fragment切换
	 *----------------------------------------------------------------------------------------*/
	private AdapterView.OnItemClickListener getListenerForListView() {
		// TODO Auto-generated method stub
		return new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				// TODO Auto-generated method stub
				if(position>0){
					ListViewItem item=adapter.getItem(position);
					if(item.type==ListViewItem.ITEM){
						/*----------------------------------------------------------------------
						 *  调用ClientMainActivity的switchGreenHouse进行fragment切换(先强制类型转换)
						 *---------------------------------------------------------------------*/
						((ClientMainActivity)getActivity()).switchGreenHouse(item.areaNum, item.greenhouseNum);
						((ClientMainActivity)getActivity()).areaNumString = item.areaNum;
						((ClientMainActivity)getActivity()).greenhouseNumString = item.greenhouseNum;
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
		addAreaButtonFloatSmall = (ButtonFloatSmall) getActivity().findViewById(R.id.home_bfSmall_addArea);         //绑定区域按钮
		addGHouseButtonFloatSmall = (ButtonFloatSmall) getActivity().findViewById(R.id.home_bfSmall_addGHouse);     //绑定大棚按钮
		addTerminalButtonFloatSmall = (ButtonFloatSmall) getActivity().findViewById(R.id.home_bfSmall_addTerminal); //绑定终端按钮
		addDeviceButtonFloatSmall = (ButtonFloatSmall) getActivity().findViewById(R.id.home_bfSmall_addDevice);

		addAreaButtonRectangle = (ButtonRectangle) getActivity().findViewById(R.id.home_addArea_text);
		addGHouseButtonRectangle = (ButtonRectangle) getActivity().findViewById(R.id.home_addGhouse_text);         //绑定大棚文本按钮
		addTerminalButtonRectangle = (ButtonRectangle) getActivity().findViewById(R.id.home_addTerminal_text);
		addDeviceButtonRectangle = (ButtonRectangle) getActivity().findViewById(R.id.home_addDevice_text);

		totalButtonFloat.setOnClickListener(new buttonFloatTotalListenner()); //设置总开关的监听器

		addAreaButtonFloatSmall.setOnClickListener(new buttonFloatSmallListenner());     //设置创建区域按钮
		addGHouseButtonFloatSmall.setOnClickListener(new buttonFloatSmallListenner());   //设置绑定大棚按钮
		addTerminalButtonFloatSmall.setOnClickListener(new buttonFloatSmallListenner()); //设置绑定终端按钮
		addDeviceButtonFloatSmall.setOnClickListener(new buttonFloatSmallListenner());   //设置绑定设备按钮

		addAreaButtonRectangle.setOnClickListener(new buttonRectangleListenner());

	}

	/**
	 * @Function: private void setBFGone()
	 * @Description:
	 *      隐藏：所有悬浮按钮
	 **/
	private void setBFGone()
	{
		addAreaButtonFloatSmall.setVisibility(View.GONE);
		addGHouseButtonFloatSmall.setVisibility(View.GONE);  //绑定大棚悬浮按钮
		addTerminalButtonFloatSmall.setVisibility(View.GONE);
		addDeviceButtonFloatSmall.setVisibility(View.GONE);

		addAreaButtonRectangle.setVisibility(View.GONE);
		addGHouseButtonRectangle.setVisibility(View.GONE);   //绑定大棚文本按钮
		addTerminalButtonRectangle.setVisibility(View.GONE);
		addDeviceButtonRectangle.setVisibility(View.GONE);

		buttonFloatIsGone = true;
	}

	/**------------------------------------------------------------------
	 * @Function: private void setBFVisibility()
	 * @Description:
	 *      显示: 所有悬浮按钮
	 *-------------------------------------------------------------------*/
	private void setBFVisibility()
	{
		addAreaButtonFloatSmall.setVisibility(View.VISIBLE);
		addGHouseButtonFloatSmall.setVisibility(View.VISIBLE);
		addTerminalButtonFloatSmall.setVisibility(View.VISIBLE);
		addDeviceButtonFloatSmall.setVisibility(View.VISIBLE);

		addAreaButtonRectangle.setVisibility(View.VISIBLE);
		addGHouseButtonRectangle.setVisibility(View.VISIBLE);
		addTerminalButtonRectangle.setVisibility(View.VISIBLE);
		addDeviceButtonRectangle.setVisibility(View.VISIBLE);

		buttonFloatIsGone = false;
	}

	/**-----------------------------------------------------------------
	 * @Function: class buttonFloatTotalListenner
	 * @Description:
	 *      悬浮按钮的总开关
	 *-----------------------------------------------------------------*/
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
			else if(id == R.id.home_bfSmall_addGHouse)
			{
				Intent intent = new Intent(getActivity(), CreateGHouseDialog.class);
				startActivityForResult(intent, REQUEST_GHOUSE); //打开创建地区的对话框，REQUEST_AREA是标志
			}
			else if(id == R.id.home_bfSmall_addDevice)
			{
				Intent intent = new Intent(getActivity(), BandDeviceDialog.class);
				startActivityForResult(intent, REQUEST_DEVICE); //打开创建地区的对话框，REQUEST_AREA是标志
			}
			setBFGone();
		}
	}

	/**-----------------------------------------------------------------------------------------------
	 * @Function: public void onActivityResult(int requestCode, int resultCode, Intent data)
	 * @Description:
	 * 		处理打开的dialog类型的activity的返回值，用于刷新列表之类的操作
	 *----------------------------------------------------------------------------------------------*/
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_AREA) { //为创建地区的返回值
			System.out.println("REQUEST_AREA");
			if (resultCode == RESULT_OK) { //创建成功
				System.out.println("RESULT_OK");
				refreshListView(); //刷新显示
			}
		}
		else if(requestCode == REQUEST_GHOUSE) { //创建大棚的dialog返回值(activity)
			System.out.println("REQUEST_GHOUSE");
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
	
}