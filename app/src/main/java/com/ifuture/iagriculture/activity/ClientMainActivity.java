package com.ifuture.iagriculture.activity;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ifuture.iagriculture.bottombar.BaseFragment;
import com.ifuture.iagriculture.bottombar.BottomBarPanel;
import com.ifuture.iagriculture.bottombar.BottomBarPanel.BottomPanelCallback;
import com.ifuture.iagriculture.bottombar.Constant;
import com.ifuture.iagriculture.bottombar.HeadControlPanel;
import com.ifuture.iagriculture.fragment.FragmentGreenHouse;
import com.ifuture.iagriculture.fragment.FragmentHome;
import com.ifuture.iagriculture.fragment.FragmentToalData;
import com.ifuture.iagriculture.fragment.FragmentVideo;

import com.jeremyfeinstein.slidingmenu.lib.*;
import com.jeremyfeinstein.slidingmenu.lib.app.*;
import com.ifuture.iagriculture.slidemenu.*;
import com.ifuture.iagriculture.R;

/** 
 * @CopyRight: 王辰浩 2015~2025
 * @Author Feather Hunter(猎羽)
 * @qq: 975559549
 * @Version: 2.10
 * @Date: 2015/12/25
 * @Description: 登陆之后的控制主界面。用于几个页面碎片(fragment)的切换。并且与后台Service进行交互，来完成各种控制功能。
 *        详细项目介绍：
 *        1.onCreate初始化了UI界面，动态注册了广播接收器用于接受后台Service发来的信息
 *        2.通过底部的按钮,切换几个fragment(页面)
 *        3.如果按下了返回键，会调用onKeyDown函数进行处理：解除注册的Receiver和发送“连接中断”给Service。并且返回到ClientActivity
 *        4.Receiver等待Service的广播，并将处理的结果通过setHandler发送给FragmentIHome进行相应的显示。
 *        5.如下方法列表中4~10用于切换多个Fragment
 *        
 *        通过底部栏进行fragment切换的详细讲解：
 *        1. 所有fragment都是BaseFragment的子类。getFragment()用于通过Basement获得相应的子fragment,所以在Basement的
 *           newInstance方法中需要编写相应代码。具体内容看一下ClientMainActivity的getFragment方法和Basement的newInstance
 *           方法内容就可以明白了。具体请咨询qq975559549
 *       
 * @Function List:
 *      1. void onCreate 		//判断当前的工作模式,动态注册广播接收器
 *      2. class ContrlReceiver //接收器,更新温度等数据信息,显示连接和认证信息
 *      3. void initUI() 		//初始化界面
 *      4. void setTabSelection //开启一个Fragment事务,并切换Fragment
 *      5. void switchFragment  //切换Fragment
 *      6. void attachFragment  
 *      7. void commitTransactions 
 *      8. void setDefaultFirstFragment
 *      9. FragmentTransaction ensureTransaction
 *      10.Fragment getFragment
 *      11.void detachFragment(Fragment f)
 *      12.void setHandler(Handler handler); //用于和FragmentIHome通信
 *      13.public boolean onKeyDown(int keyCode, KeyEvent event); //用于处理返回键等按下后的时间
 * @history:
 *    v2.10 2016/1/8 解决了手机待机导致程序崩溃BUG，解决了反复在登陆界面和控制界面切换导致控制界面显示出错BUG
 *    v3.0  2016/4/11 新增侧边滑动菜单栏
 **/

public class ClientMainActivity extends SlidingFragmentActivity implements BottomPanelCallback, HeadControlPanel.HeadPanelCallback {

	/** 侧滑菜单 */
	private SlidingMenu slidemenu;
	/** 左边菜单、右边菜单 */
	private LeftMenuFragment mLeftMenu;
	private RightMenuFragment mRightMenu;
	/** 动画类 */
	private SlidingMenu.CanvasTransformer mTransformer;

	/*头边框、底边框*/
	BottomBarPanel bottomPanel = null;
	HeadControlPanel headPanel = null;
	LinearLayout warningLayout = null;
	TextView warningTexview	   = null;
	
	private FragmentManager fragmentManager = null;
	private FragmentTransaction fragmentTransaction = null;
	FragmentHome fragmentIHome;
	FragmentVideo fragmentVideo;
	FragmentToalData fragmentToalData;
	FragmentGreenHouse fragmentGreenHouse;

	private boolean isConnected = false;
	private ContrlReceiver contrlReceiver;
	private String CONTRL_ACTION = "android.intent.action.EDIT";

	public String areaNumString = null;
	public String greenhouseNumString = null;

	/**
	 * 大棚fragment与activty通信的handler
	 * */
	public Handler greenhouseHandler;
/*	private MessageFragment messageFragment;
	private ContactsFragment contactsFragment;
	private NewsFragment newsFragment;
	private SettingFragment settingFragment;*/
	
	public static String currFragTag = "";

	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};
	/**
	 * @Function: protected void onCreate
	 * @Description:
	 *      ClientMainActivity创建后的操作
	 *      1.initUI()初始化UI界面，包括底层栏
	 *      2.获取从ClientActivity传递的信息，确定处于内网模式还是外网模式。
	 *      3.动态注册Receiver(用于将从Service接受的结果发送给IHome Fragment并做出相应改变)
	 **/
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_client_main);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //状态栏
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		/*初始化界面*/
		currFragTag = "";
		initUI();

		fragmentManager = getFragmentManager();
		setDefaultFirstFragment(Constant.FRAGMENT_FLAG_HOME);

		warningLayout = (LinearLayout) findViewById(R.id.panel_offline_layout);
		warningTexview = (TextView)findViewById(R.id.panel_offline_text);

		/*------------------------------------------------------
		 *             动态注册receiver
		 *------------------------------------------------------- */
		try {
			contrlReceiver = new ContrlReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(CONTRL_ACTION);
			registerReceiver(contrlReceiver, filter);//注册
		} catch (IllegalArgumentException  e) {
			// TODO: handle exception
			System.out.println("had been registerReceiver");
		}
		/*默认开启IHome界面*/
		//onBottomPanelClick(Constant.BTN_FLAG_IHOME);
		System.out.println("onCreate");

		/*------------------------------------------------------
		 *      侧边滑动菜单的准备工作
		 *------------------------------------------------------- */
		initAnimation();
		slidemenu = getSlidingMenu();
		setBehindContentView(R.layout.slidemenu_left_frag);         //设置左边滑动菜单
		//slidemenu.setSecondaryMenu(R.layout.slidemenu_right_frag);
		if (savedInstanceState == null) {
			mLeftMenu = new LeftMenuFragment();
			mRightMenu = new RightMenuFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.menu_left_frag, mLeftMenu, "Left").commit();
			//getSupportFragmentManager().beginTransaction().replace(R.id.menu_right_frag, mRightMenu, "Right").commit();
		}
		slidemenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidemenu.setFadeEnabled(false);
		slidemenu.setBehindScrollScale(0.25f);
		slidemenu.setFadeDegree(0.25f);

		// 配置背景图片
		slidemenu.setBackgroundResource(R.color.mygreen4);

//		slidemenu.setSecondaryShadowDrawable(R.drawable.rightshadow); // 设置右边菜单的阴影
//		slidemenu.setShadowDrawable(R.drawable.shadow); // 设置阴影图片
//		slidemenu.setShadowWidthRes(R.dimen.shadow_width); // 设置阴影图片的宽度
//		slidemenu.setBehindOffsetRes(R.dimen.slidingmenu_offset); // 显示主界面的宽度
//		slidemenu.setFadeDegree(0f); // SlidingMenu滑动时的渐变程度
//		slidemenu.setBehindScrollScale(0f);
//		slidemenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN); // 设置滑动的屏幕范围，该设置为全屏区域都可以滑动
//		slidemenu.setMode(SlidingMenu.LEFT_RIGHT); // 设置菜单同时兼具左右滑动
//		slidemenu.setBehindCanvasTransformer(mTransformer); // 设置动画

		// 设置专场动画效果
		slidemenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			    @Override
			    public void transformCanvas(Canvas canvas, float percentOpen) {
				       float scale = (float) (percentOpen * 0.25 + 0.75);
				       canvas.scale(scale, scale, -canvas.getWidth() / 2,
					            canvas.getHeight() / 2);
		   }
		});

		slidemenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override

			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.25);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});

	}

	private static Interpolator interp = new Interpolator() {
		@Override
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t + 1.0f;
		}
	};
	/**
	 * 初始化菜单滑动的效果动画
	 */
	private void initAnimation() {
		mTransformer = new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				canvas.scale(interp.getInterpolation(percentOpen), interp.getInterpolation(percentOpen), canvas.getWidth() / 2, canvas.getHeight() / 2);
				//canvas.translate(0, canvas.getHeight() * (1 - interp.getInterpolation(percentOpen))); //平移动画
				//canvas.scale(percentOpen, 1, 0, 0); //缩放动画
			}

		};
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		contrlReceiver = new ContrlReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CONTRL_ACTION);
		registerReceiver(contrlReceiver, filter);//注册 

		System.out.println("onRestart");
	}

	protected void onStart(){
		super.onStart();
		System.out.println("onStart");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("onPause");
	}

	//接收器,更新温度等数据信息,显示连接和认证信息
	/**
	 * @Function: private class ContrlReceiver extends BroadcastReceiver
	 * @Description:
	 *      接受来自Service的信息，并且转发给相应fragment来改变相应组件内容
	 **/
	private class ContrlReceiver extends BroadcastReceiver{

		public ContrlReceiver() {
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String typeString = intent.getStringExtra("type");
			Message msgMessage = new Message();
			Bundle bundle = new Bundle();
			/* -----------------------------------------
			 * 处理主activity接收到的广播
			 * -----------------------------------------*/
			if(typeString.equals("wifi_internet"))
			{
				String stateString = intent.getStringExtra("wifi_internet");
				if(stateString.equals("disconnect"))
				{
					warningLayout.setVisibility(View.VISIBLE);
					warningTexview.setText("网络连接不可用，请检查相关设置。");
				}
				else if(stateString.equals("connect"))
				{
					warningLayout.setVisibility(View.VISIBLE);
					warningTexview.setText("登录中...");
				}
				else if(stateString.equals("error"))
				{
					warningLayout.setVisibility(View.VISIBLE);
					warningTexview.setText("服务器维护中");
				}
				else if(stateString.equals("authed"))
				{
					warningLayout.setVisibility(View.GONE);
					Toast.makeText(ClientMainActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
				}
			}
//			else if(typeString.equals("temp"))/*发送给第一个ihome fragment*/
//			{
//				bundle.putString("type", "temp");
//				String IDString = intent.getStringExtra("temp");
//				bundle.putString("temp", IDString);
//				bundle.putString(IDString, intent.getStringExtra(IDString));
//				msgMessage.setData(bundle);
//				if(currFragTag.equals(Constant.FRAGMENT_FLAG_IHOME))
//				{
//					ihomeHandler.sendMessage(msgMessage);
//				}
//				else if(currFragTag.equals(Constant.FRAGMENT_FLAG_VIDEO))
//				{
//					videoHandler.sendMessage(msgMessage);
//				}
//
//			}
//			/*更新温度信息*/
//			else if(typeString.equals("humi"))
//			{
//				bundle.putString("type", "humi");
//				String IDString = intent.getStringExtra("humi");
//				bundle.putString("humi", IDString);
//				bundle.putString(IDString, intent.getStringExtra(IDString));
//				msgMessage.setData(bundle);
//				if(currFragTag.equals(Constant.FRAGMENT_FLAG_IHOME))
//				{
//					ihomeHandler.sendMessage(msgMessage);
//				}
//				else if(currFragTag.equals(Constant.FRAGMENT_FLAG_VIDEO))
//				{
//					videoHandler.sendMessage(msgMessage);
//				}
//			}
//			/*灯的状态*/
//			else if(typeString.equals("ledon"))
//			{
//				bundle.putString("type", "ledon");
//				bundle.putString("ledon", intent.getStringExtra("ledon"));
//				msgMessage.setData(bundle);
//				if(currFragTag.equals(Constant.FRAGMENT_FLAG_IHOME))
//				{
//					ihomeHandler.sendMessage(msgMessage);
//				}
//				else if(currFragTag.equals(Constant.FRAGMENT_FLAG_VIDEO))
//				{
//					videoHandler.sendMessage(msgMessage);
//				}
//			}
//			/*灯的状态*/
//			else if(typeString.equals("ledoff"))
//			{
//				bundle.putString("type", "ledoff");
//				String ledString = intent.getStringExtra("ledoff");
//				bundle.putString("ledoff", ledString);
//				msgMessage.setData(bundle);
//				if(currFragTag.equals(Constant.FRAGMENT_FLAG_IHOME))
//				{
//					ihomeHandler.sendMessage(msgMessage);
//				}
//				else if(currFragTag.equals(Constant.FRAGMENT_FLAG_VIDEO))
//				{
//					videoHandler.sendMessage(msgMessage);
//				}
//			}
//			/*显示连接和认证身份情况*/
//			else if(typeString.equals("disconnect"))
//			{
//				String stateString = intent.getStringExtra("disconnect");
//				if(stateString.equals("authing"))
//				{
//					Toast.makeText(ClientMainActivity.this, "正在验证信息...", Toast.LENGTH_SHORT).show();
//				}
//				else if(stateString.equals("connecting"))
//				{
//					Toast.makeText(ClientMainActivity.this, "正在连接服务器...", Toast.LENGTH_SHORT).show();
//				}
//				else if(stateString.equals("connected"))
//				{
//					Toast.makeText(ClientMainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
//				}
//				else if(stateString.equals("authed"))
//				{
//					Toast.makeText(ClientMainActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
//				}
//				else if(stateString.equals("video connecting"))
//				{
//					Toast.makeText(ClientMainActivity.this, "正在连接视频服务器...", Toast.LENGTH_SHORT).show();
//				}
//				else if(stateString.equals("video success"))
//				{
//					Toast.makeText(ClientMainActivity.this, "视频连接成功", Toast.LENGTH_SHORT).show();
//				}
//
//			}
//			/*发送IHome mode开启状况*/
//			else if(typeString.equals("ihome"))
//			{
//				bundle.putString("type", "ihome");
//				String modeString = intent.getStringExtra("ihome");
//				bundle.putString("ihome", modeString);
//				msgMessage.setData(bundle);
//				ihomeHandler.sendMessage(msgMessage);
//				Toast.makeText(ClientMainActivity.this, modeString, Toast.LENGTH_SHORT).show();
//			}
//			else if(typeString.equals("videofinish"))
//			{
//				bundle.putString("type", "videofinish");
//				String operationString = intent.getStringExtra("videofinish");
//				bundle.putString("videofinish", operationString);
//				msgMessage.setData(bundle);
//				videoHandler.sendMessage(msgMessage);
//			}
//			else if(typeString.equals("tempCtrl")) //ihomefragment让其切换到空调遥控器fragment
//			{
//				setTabSelection(Constant.FRAGMENT_FLAG_CONTRL); //切换到遥控Fragment
//			}

		}


	}

	/*
	*
	* 			getMenuInflater().inflate(R.menu.menu_main, menu);原来是 R.menu.client
	*
	*
	* */
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu_main, menu);
//		return true;
//	}
	private void initUI(){
		bottomPanel = (BottomBarPanel)findViewById(R.id.bottom_layout);
		if(bottomPanel != null){
			bottomPanel.initBottomPanel();
			bottomPanel.setBottomCallback(this);
		}

		headPanel = (HeadControlPanel)findViewById(R.id.head_layout);
		if(headPanel != null){
			headPanel.initHeadPanel();
			headPanel.setHeadCallback(this);
		}

	}

	/**-------------------------------------------------------------------------------
	 * @Function: switchGreenHouse
	 * @Description: 打开显示大棚数据的fragment,并且将地区号、大棚号发送给该fragment
	 * @param areaNum 打开的地区号
	 * @param greenhouseNum 打开的大棚号
	 *
	 *--------------------------------------------------------------------------------*/
	public void switchGreenHouse(String areaNum, String greenhouseNum)
	{
		String tag = "";
		tag = Constant.FRAGMENT_FLAG_GREENHOUSE;
		areaNumString = areaNum;
		greenhouseNumString = greenhouseNum;
		setTabSelection(tag); //切换Fragment
		headPanel.setMiddleTitle(tag);//切换标题
		//sendMsgToGHouseFrag(areaNum, greenhouseNum);
	}

	/**
	 * 处理BottomControlPanel的回调
	 */
	@Override
	public void onBottomPanelClick(int itemId) {
		// TODO Auto-generated method stub
		String tag = "";
		if((itemId & Constant.BTN_FLAG_HOME) != 0){
			tag = Constant.FRAGMENT_FLAG_HOME;
		}else if((itemId & Constant.BTN_FLAG_STATICS) != 0){
			tag = Constant.FRAGMENT_FLAG_STATICS;
		}else if((itemId & Constant.BTN_FLAG_VIDEO) != 0){
			tag = Constant.FRAGMENT_FLAG_VIDEO;
		}else if((itemId & Constant.BTN_FLAG_CSERVICE) != 0){
			tag = Constant.FRAGMENT_FLAG_CSERVICE;
		}
		setTabSelection(tag); //切换Fragment
		headPanel.setMiddleTitle(tag);//切换标题
	}

	/**-----------------------------------------------------------------------
	 * @Function: public void onHeadPanelClick(int itemId)
	 * @Description: 处理HeadControlPanel的回调
	 * @param itemId 获取的ID用于标示是总结的数据还是详细数据
	 *-----------------------------------------------------------------------*/
	@Override
	public void onHeadPanelClick(int itemId) {
		// TODO Auto-generated method stub
		String tag = "";
		if((itemId & Constant.BTN_FLAG_HOME) != 0){  //为简略数据
			tag = Constant.FRAGMENT_FLAG_HOME;
		}else if((itemId & Constant.BTN_FLAG_TOTAL_DATA) != 0){ //为详细数据
			tag = Constant.FRAGMENT_FLAG_TOTAL_DATA;
		}
		setTabSelection(tag); //切换Fragment
	}
	
	/**设置选中的Tag
	 * @param tag
	 */
	public  void setTabSelection(String tag) {
		// 开启一个Fragment事务
		fragmentTransaction = fragmentManager.beginTransaction();
		if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_HOME)){

			Intent intent = new Intent();
			intent.putExtra("type", "fragment");
			intent.putExtra("ihome", "start");
			intent.putExtra("video", "stop");
			intent.setAction(intent.ACTION_MAIN);
			this.sendBroadcast(intent);

		   if (fragmentIHome == null) {
			   fragmentIHome = new FragmentHome();
			} 
		 }
		else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_VIDEO)){

			Intent intent = new Intent();
			intent.putExtra("type", "fragment");
			intent.putExtra("ihome", "stop");
			intent.putExtra("video", "start");
			intent.setAction(intent.ACTION_MAIN);
			this.sendBroadcast(intent);

			if (fragmentVideo == null) {
				fragmentVideo = new FragmentVideo();
			} 
		}
		else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_STATICS)){
			//System.out.println("===================HeadPanelClick=====================");
			if (fragmentToalData == null) {
				fragmentToalData = new FragmentToalData();
			}
		}
		else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_TOTAL_DATA)){
			//System.out.println("===================HeadPanelClick=====================");
			if (fragmentToalData == null) {
				fragmentToalData = new FragmentToalData();
			}
		}
		/* -----------------------------------------
		 *  创建需要显示的某大棚数据
		 * -----------------------------------------*/
		else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_GREENHOUSE)){
			if (fragmentGreenHouse == null) {
				fragmentGreenHouse = new FragmentGreenHouse();
			}
		}
		else
		{
			Intent intent = new Intent();
			intent.putExtra("type", "fragment");
			intent.putExtra("ihome", "stop");
			intent.putExtra("video", "stop");
			intent.setAction(intent.ACTION_MAIN);
			this.sendBroadcast(intent);
		}
        /*
		else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_NEWS)){
			if (newsFragment == null) {
				newsFragment = new NewsFragment();
			}
			
		}else if(TextUtils.equals(tag,Constant.FRAGMENT_FLAG_SETTING)){
			if (settingFragment == null) {
				settingFragment = new SettingFragment();
			}
		}else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_SIMPLE)){
			if (simpleFragment == null) {
				simpleFragment = new SimpleFragment();
			} 
			
		}*/
		switchFragment(tag);
		 
	}

//	/**---------------------------------------------------------------------------------------------
//	 * @Function: private void sendMsgToGHouseFrag(String areaNum, String greenHouseNum)
//	 * @param areaNum 地区号
//	 * @param greenHouseNum 大棚号
//	 * @Description:
//	 *        发送地区号、大棚号给GreenHouseFragment
//	 *----------------------------------------------------------------------------------------------*/
//	private void sendMsgToGHouseFrag(String areaNum, String greenHouseNum)
//	{
//		Message msgMessage = new Message();
//		Bundle bundle = new Bundle();
//		bundle.putString("area", areaNum);
//		bundle.putString("greenhouse", greenHouseNum);
//		msgMessage.setData(bundle);
//		if(greenhouseHandler != null)
//		greenhouseHandler.sendMessage(msgMessage);
//	}

	/**---------------------------------------------------------------------------------------------
	 * @Function: public void setIHomeHandler(Handler handler)
	 * @param handler
	 * @Description:
	 *        用于设置FragmentGreenHouse(具体大棚数据的fragment)和ClientMainActivity通信的handler
	 *----------------------------------------------------------------------------------------------*/
	public void setGreenHouseHandler(Handler handler)
	{
		greenhouseHandler = handler;
	}
	
	/**切换fragment 
	 * @param tag
	 */
	private  void switchFragment(String tag){
		if(TextUtils.equals(tag, currFragTag)){
			return;
		}
		//把上一个fragment detach掉
		if(currFragTag != null && !currFragTag.equals("")){
			detachFragment(getFragment(currFragTag));
			//hideFragment(getFragment(currFragTag));
		}
		attachFragment(R.id.main_window, getFragment(tag), tag);
		commitTransactions(tag);
	}
//	/**
//	 *  隐藏当前的Fragment
//	 **/
//	private void hideFragment(Fragment f){
//		if(f != null && !f.isHidden()){
//			ensureTransaction();
//			fragmentTransaction.hide(f);
//		}
//	}
	
	private void attachFragment(int layout, Fragment f, String tag){
		if(f != null){
			if(f.isDetached()){
			//if(f.isHidden()){
				ensureTransaction();
				//fragmentTransaction.show(f);
				fragmentTransaction.attach(f);
				
			}else if(!f.isAdded()){
				ensureTransaction();
				fragmentTransaction.add(layout, f, tag);
			}
		}
	}
	
	private void commitTransactions(String tag){
		if (fragmentTransaction != null && !fragmentTransaction.isEmpty()) {
			fragmentTransaction.commit();
			currFragTag = tag;
			fragmentTransaction = null;
		}
	}

	/**----------------------------------------------------------------
	 * @Function: private void setDefaultFirstFragment(String tag)
	 * @Description:
	 *        用于刚打开时，设置默认的fragment
	 *----------------------------------------------------------------*/
	private void setDefaultFirstFragment(String tag){
		//Log.i("yan", "setDefaultFirstFragment enter... currFragTag = " + currFragTag);
		setTabSelection(tag);
		bottomPanel.defaultBtnChecked();
		headPanel.setMiddleTitle(tag);//切换标题
		//Log.i("yan", "setDefaultFirstFragment exit...");
	}
	
	
	private FragmentTransaction ensureTransaction( ){
		if(fragmentTransaction == null){
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		}

		return fragmentTransaction;
	}
	
	private Fragment getFragment(String tag){
		
		Fragment fragment = fragmentManager.findFragmentByTag(tag);
		
		if(fragment == null){
			fragment = BaseFragment.newInstance(getApplicationContext(), tag);
		}
//		if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_GREENHOUSE))//具体大棚的界面，设置
//		{
//			Bundle bundle = new Bundle();
//			bundle.putString("area", areaNumString);
//			bundle.putString("greenhouse", greenhouseNumString);
//			fragment.setArguments(bundle);
//
//		}
		return fragment;
	}
	private void detachFragment(Fragment f){
		
		if(f != null && !f.isDetached()){
			ensureTransaction();
			fragmentTransaction.detach(f);
		}
	}
	
	
	/**
	 *  @author: feather
	 *  @description: 判断是否按下返回键，如果按下则进行相应处理
	 **/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println("onKeyDown");
		if(keyCode == KeyEvent.KEYCODE_BACK) //按下返回键
		{
			/*---------------------------------------------
			 *        告诉ClientActivity 我们要回到主界面
			 *--------------------------------------------*/
			System.out.println("KEYCODE_BACK");
			Intent intent = new Intent();
			intent.putExtra("type", "ClientMainBack");
			intent.setAction(intent.ACTION_MAIN);
			this.sendBroadcast(intent);
			
			unregisterReceiver(contrlReceiver);//解除注册的Receiver
			
			boolean res =super.onKeyDown(keyCode, event);
			//this.onDestory();
			return res;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("onStop");
	}
	
	protected void onDestory(){
		super.onDestroy();
		System.out.println("onDestory");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
	}

}