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
import android.widget.TextView;

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
	private int RESULT_OK = Activity.RESULT_OK;

	//显示哪些步骤没有完成，帮助用户进行各类操作
	TextView helpArea = null;
	TextView helpGreenhouse = null;
	TextView helpTerminal = null;
	TextView helpDevice = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.fragment_home, container, false); //获得该fragment的布局文件

		init_listview(view);

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
		 *             设置Listview周围的padding
		 *----------------------------------------------*/
		float density = getResources().getDisplayMetrics().density;
		int padding = (int) (1 * density);
		listView.setPadding(padding, padding, padding, padding);
	}

	/**--------------------------------------------------------------------
	 *  @author: 王辰浩
	 *  @function: ArrayList<ListViewItem> getListDate()
	 *  @description:
	 *  1.获取地区/大棚列表
	 *  2.查询地区，大棚，终端，设备是否全部绑定成功，提示用户进行操作
	 *
	 *---------------------------------------------------------------------*/
	public ArrayList<ListViewItem> getListDate()
	{
		boolean gHouseIsFind = true;
		boolean terminalIsFind = true;
		ArrayList<ListViewItem> list = new ArrayList<ListViewItem>();

		String areaNames[] = databaseOperation.queryAreaName(getActivity());
		/*--------------------------------------
		 *  处理是否查询到地区的警告
		 *--------------------------------------*/
		if(areaNames[0] == null) //没有查询到地区
		{
			helpArea.setTextColor(ContextCompat.getColor(getActivity(),R.color.redincorrect)); //提示错误
			helpArea.setText("  请先“创建地区”   右下角悬浮按钮进行相关操作");
			helpArea.setVisibility(View.VISIBLE);//显示
		}
		else//查询到了
		{
			helpArea.setVisibility(View.GONE);//显示
		}
		/*--------------------------------------
		 *  遍历获得每个地区下面的大棚并且检查是否存在终端以及设备
		 *--------------------------------------*/
		gHouseIsFind = true;
		terminalIsFind = true;
		for(int i = 0; areaNames[i] != null; i++)
		{
			String terminal[] = databaseOperation.queryTerminalPerArea(getActivity(), i);//查询该地区是否存在终端
			/*--------------------------------------
		     *  处理是否查询到终端的警告
		     *--------------------------------------*/
			if(terminal[0] == null)//不存在
			{
				list.add(new ListViewItem(ListViewItem.SECTION, areaNames[i], true));//在地区中显示不存在终端的警告

				if(terminalIsFind == true)
				{
					helpTerminal.setTextColor(ContextCompat.getColor(getActivity(), R.color.redincorrect)); //提示错误
					helpTerminal.setText("  请先“绑定终端”   存在地区没有绑定终端");
					helpTerminal.setVisibility(View.VISIBLE);//显示
					terminalIsFind = false;
				}
			}
			else
			{
				list.add(new ListViewItem(ListViewItem.SECTION, areaNames[i], false));//存在终端、不需要警告
			}
			String greenHouseNums[] = databaseOperation.queryGHousePerArea(getActivity(), i); //i就为当前的地区号
			/*--------------------------------------
		     *  处理是否在该地区查询到大棚的警告
		     *--------------------------------------*/
			if(greenHouseNums[0] == null) //没有查询到地区
			{
				if(gHouseIsFind == true)
				{
					helpGreenhouse.setTextColor(ContextCompat.getColor(getActivity(),R.color.redincorrect)); //提示错误
					helpGreenhouse.setText("  请先“绑定大棚”   存在地区没有绑定大棚");
					helpGreenhouse.setVisibility(View.VISIBLE);//显示
					gHouseIsFind = false;
				}
			}
			for(int j = 0; greenHouseNums[j] != null; j++)
			{
				String devices[] = databaseOperation.queryDevicePerGHouse(getActivity(), i, greenHouseNums[j]);//查询该大棚是否存在设备
				/*--------------------------------------
		         *  处理是否在该大棚查询到设备
		         *--------------------------------------*/
				if(devices[0] == null)//不存在
				{
					list.add(new ListViewItem(ListViewItem.ITEM, "大棚"+greenHouseNums[j], ""+i, greenHouseNums[j], true));
				}
				else
				{
					list.add(new ListViewItem(ListViewItem.ITEM, "大棚"+greenHouseNums[j], ""+i, greenHouseNums[j], false));
				}
			}
		}
		if(terminalIsFind == true)//所有地区都拥有终端
		{
			helpTerminal.setVisibility(View.GONE);//显示
		}
		if(gHouseIsFind == true)//所有地区都拥有大棚
		{
			helpGreenhouse.setVisibility(View.GONE);//显示
		}
		return list;
	}
	/**
	 *  @author: 王辰浩
	 *  @function: refreshListView()
	 *  @description: 获得地区、大棚表的List的Item用于显示
	 **/
	public void refreshListView() {
		System.out.println("refreshListView");

		adapter.refresh(getListDate()); //显示列表
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

		/*--------------------------------------------
		 *             初始化数据库
		 *----------------------------------------------*/
		SharedPreferences apSharedPreferences = getActivity().getSharedPreferences("saved", Activity.MODE_PRIVATE);
		String accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
		databaseOperation = new DatabaseOperation(accountString); //使用用户名创建数据库
		databaseOperation.createDatabase(getActivity());//创建数据库
		/*--------------------------------------------
		 *             获得warning的texview
		 *----------------------------------------------*/
		helpArea = (TextView) getActivity().findViewById(R.id.home_help_area); //绑定地区
		helpGreenhouse = (TextView) getActivity().findViewById(R.id.home_help_greenhouse);//绑定大棚
		helpTerminal = (TextView) getActivity().findViewById(R.id.home_help_terminal);//绑定终端
		helpDevice = (TextView) getActivity().findViewById(R.id.home_help_device);//绑定设备
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

		/*-----------------------------------------------
		 *             配置适配器和按键监听器
		 *----------------------------------------------*/
		adapter = new HomeListViewAdapter(getActivity(), getListDate());//获得数据
		listView.setAdapter(adapter);//设置适配器
		listView.setOnItemClickListener(getListenerForListView());

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
		else if(requestCode == REQUEST_TERM) { //为绑定终端的返回值
			System.out.println("REQUEST_TERM");
			if (resultCode == RESULT_OK) { //创建成功
				System.out.println("RESULT_OK");
				refreshListView(); //刷新显示
			}
		}
		else if(requestCode == REQUEST_DEVICE) { //为绑定设备的返回值
			System.out.println("REQUEST_DEVICE");
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