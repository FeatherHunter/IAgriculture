package com.ifuture.iagriculture.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.ifuture.iagriculture.Calendar.TodayTime;
import com.ifuture.iagriculture.activity.ClientMainActivity;
import com.ifuture.iagriculture.Instruction.Instruction;
import com.ifuture.iagriculture.activity.DatabaseTestActivity;
import com.ifuture.iagriculture.bottombar.BaseFragment;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.sqlite.DatabaseOperation;

import java.util.Calendar;

public class FragmentVideo extends BaseFragment implements ViewSwitcher.ViewFactory{

	DatabaseOperation databaseOperation = null;
	ClientMainActivity mainActivity;

	TextView dateView = null;

	Button testButton;
	Button ledButton;
	ImageView videoImageView;

//	private RecvReceiver recvReceiver;
	private String RECV_ACTION = "android.intent.action.ANSWER";

	Boolean videoOkFlag = false;
	private ContrlReceiver contrlReceiver;
	private String CONTRL_ACTION = "android.intent.action.EDIT";

	AnimationDrawable animon = null;
	AnimationDrawable animoff = null;

	private LinearLayout linearLayout;
	private ImageSwitcher imageSwitcher;

	//原图
	private Integer[] images = {R.drawable.video_greenhouse1, R.drawable.video_greenhouse2, R.drawable.video_greenhouse3, R.drawable.video_greenhouse4};
	//缩略图
	private Integer[] sImages = {R.drawable.video_greenhouse_1, R.drawable.video_greenhouse_2, R.drawable.video_greenhouse_3, R.drawable.video_greenhouse_4};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_video, container, false);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		videoOkFlag = true;

		SharedPreferences apSharedPreferences1 = getActivity().getSharedPreferences("saved", Activity.MODE_PRIVATE);
		String accountString  = apSharedPreferences1.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
		databaseOperation = new DatabaseOperation(accountString); //使用用户名创建数据库
		//databaseOperation.deleteTable(getActivity());
		databaseOperation.createDatabase(getActivity());//创建数据库

		linearLayout = (LinearLayout) getActivity().findViewById(R.id.fragment_video_linearlayout);
		imageSwitcher = (ImageSwitcher) getActivity().findViewById(R.id.fragment_video_imageswitcher);

		dateView = (TextView) getActivity().findViewById(R.id.fragment_video_date);
		TodayTime todaytime = new TodayTime();
		todaytime.update();
		dateView.setText(""+todaytime.getYear()+"/"+todaytime.getMonth()+"/"+todaytime.getDay()+" ");

		imageSwitcher.setFactory(this); //设置工厂
		imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in)); //设置淡入的动画
		imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out)); //设置淡出的动画
		imageSwitcher.setImageResource(images[0]); //默认选择题一个图片

		for(int i = 0; i < sImages.length; i++)
		{
			linearLayout.addView(getImageView(i));
		}

//		testButton = (Button) getActivity().findViewById(R.id.ctrl_test_button);
//		ledButton = (Button) getActivity().findViewById(R.id.ctrl_led_button);
//		testButton.setOnClickListener(new testButtonListener());
//		ledButton.setOnClickListener(new lampButtonListener());
//
//		videoImageView = (ImageView) getActivity().findViewById(R.id.ctrl_video_imageview);
//
//		/* -------------------------------------------------------
//	     *  demo
//		 * -------------------------------------------------------*/
//		SharedPreferences demotemp = getActivity().getSharedPreferences("demo", Activity.MODE_PRIVATE);
//		if(demotemp.getString("demo", "").equals("on"))
//		{
//			videoImageView.setImageResource(R.drawable.demo_on8);
//			ledButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
//		}
//		else
//		{
//			videoImageView.setImageResource(R.drawable.demo_on1);
//			ledButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_default));
//		}
//
//		animon = new AnimationDrawable();
//		Drawable drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_on1);
//		animon.addFrame(drawable, 30);
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_on3);
//		animon.addFrame(drawable, 30);
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_on4);
//		animon.addFrame(drawable, 30);
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_on5);
//		animon.addFrame(drawable, 30);
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_on6);
//		animon.addFrame(drawable, 30);
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_on7);
//		animon.addFrame(drawable, 30);
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_on8);
//		animon.addFrame(drawable, 30);
//
//		animon.setOneShot(true); //not设置为loop
//
//		animoff = new AnimationDrawable();
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_on8);
//		animoff.addFrame(drawable, 50);
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_off1);
//		animoff.addFrame(drawable, 50);
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_off2);
//		animoff.addFrame(drawable, 50);
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_off3);
//		animoff.addFrame(drawable, 50);
//		drawable = ContextCompat.getDrawable(getActivity(),R.drawable.demo_on1);
//		animoff.addFrame(drawable, 50);
//
//		animoff.setOneShot(true); //not设置为loop

//		/* -------------------------------------------------------
//	     *  通过SharedPreferences获取当前温度等数据,显示出来。
//		 * -------------------------------------------------------*/
//		SharedPreferences apSharedPreferences = getActivity().getSharedPreferences("tempdata", Activity.MODE_PRIVATE);
//		tempValue.setText(apSharedPreferences.getString("temperature", "") ); //第2个参数是value的默认值
//		humiValue.setText(apSharedPreferences.getString("humidity", "") ); //第2个参数是value的默认值
//		//video_button.setOnClickListener(new ButtonListener());
//		//videoStart(); //start video

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
	}

	//得到缩略图片，并且添加监听器
	private ImageView getImageView(int i){
		final ImageView imageView = new ImageView(getActivity());
		imageView.setImageResource(sImages[i]); //设置缩略图
		imageView.setId(i); //设置ID
		imageView.setPadding(2,2,2,2);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageSwitcher.setImageResource(images[v.getId()]); //设置资源 //v.getId()获取编号ID
				Toast.makeText(getActivity(), "当前视频：大棚"+v.getId(), Toast.LENGTH_SHORT).show();
			}
		});

		return imageView;
	}

	@Override
	public View makeView() {
		ImageView imageView = new ImageView(getActivity());//得到ImageView
		return imageView;
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
//	 * 灯按键监听器
//	 * @Descrption： 如果当前按钮的颜色为绿色则表示需要开启灯
//	 */
//	class lampButtonListener implements OnClickListener{
//
//		@Override
//		public void onClick(View view) {
//			// TODO Auto-generated method stub
//			if(videoOkFlag == false) return;
//			int lampid = view.getId();
//			int textColor = ContextCompat.getColor(getActivity(), R.color.text_color_default);
//			switch(lampid) {
//				case R.id.ctrl_led_button: {
//					int currentColor = ledButton.getCurrentTextColor();
//					Intent intent = new Intent();
//					intent.setAction(intent.ACTION_MAIN);
//					intent.putExtra("type", "send");
//					if (currentColor == textColor) {   //为绿色，关闭lamp
//						videoImageView.setImageDrawable(animon);  //将动画设置为ImageView背景
//						animon.start();   //开始动画
//
//						//intent.putExtra("send", Instruction.ctrlLamp("10000", "" + (char) 0 + (char) 0 + (char) 0 + (char) 1, false));
//						ledButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
//						//videoImageView.setImageResource(R.drawable.demo_lamp_on);
//						SharedPreferences apSharedPreferences = getActivity().getSharedPreferences("demo", Activity.MODE_PRIVATE);
//						SharedPreferences.Editor editor = apSharedPreferences.edit();//用putString的方法保存数据
//						editor.putString("demo", "on");
//						editor.commit();
//					} else {
//						videoImageView.setImageDrawable(animoff);  //将动画设置为ImageView背景
//						animoff.start();   //开始动画
//
//						//intent.putExtra("send", Instruction.ctrlLamp("10000", "" + (char) 0 + (char) 0 + (char) 0 + (char) 1, true));
//						ledButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_default));
//						//videoImageView.setImageResource(R.drawable.demo_lamp_off);
//						SharedPreferences apSharedPreferences = getActivity().getSharedPreferences("demo", Activity.MODE_PRIVATE);
//						SharedPreferences.Editor editor = apSharedPreferences.edit();//用putString的方法保存数据
//						editor.putString("demo", "off");
//						editor.commit();
//					}
//					getActivity().sendBroadcast(intent);
//				}
//			}
//
//		}//end of onClick
//
//	}
//
//	/**
//	 * 数据库测试按键
//	 * @Descrption： 如果当前按钮的颜色为绿色则表示需要开启灯
//	 */
//	class testButtonListener implements OnClickListener{
//
//		@Override
//		public void onClick(View view) {
//			// TODO Auto-generated method stub
//			Intent intent = new Intent();
//			intent.setClass(getActivity(), DatabaseTestActivity.class);
//			getActivity().startActivity(intent);
//		}
//
//	}

//	/**
//	 * @Function: private class ContrlReceiver extends BroadcastReceiver
//	 * @Description:
//	 *      接受来自Service的信息，并且转发给相应fragment来改变相应组件内容
//	 **/
//	private class RecvReceiver extends BroadcastReceiver {
//
//		public RecvReceiver() {
//			// TODO Auto-generated constructor stub
//		}
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			String typeString = intent.getStringExtra("update");
//			if(typeString != null)
//			{
//				if(typeString.equals("temp"))/*发送给第一个ihome fragment*/
//				{
//					String tempString = intent.getStringExtra("temp");
//					tempValue.setText(tempString+"℃");
//				}
//				else if(typeString.equals("humi"))/*发送给第一个ihome fragment*/
//				{
//					String humiString = intent.getStringExtra("humi");
//					humiValue.setText(humiString+"%");
//				}
//			}
//		}//onReceive
//
//
//	}

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
			Message msgMessage = new Message();
			Bundle bundle = new Bundle();
			/* -----------------------------------------
			 * 处理主activity接收到的广播
			 * -----------------------------------------*/
			if (typeString.equals("wifi_internet")) {
				String stateString = intent.getStringExtra("wifi_internet");
				if (stateString.equals("disconnect")) {
					videoOkFlag = false;
					videoImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.color.black));
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
						videoImageView.setImageResource(R.drawable.demo_on1);
						ledButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
					}
					else
					{
						videoImageView.setImageResource(R.drawable.demo_on8);
						ledButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_default));
					}
				}
			}
		}
	}
	
	
}
