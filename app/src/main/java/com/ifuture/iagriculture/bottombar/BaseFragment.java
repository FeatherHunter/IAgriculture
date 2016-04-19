
package com.ifuture.iagriculture.bottombar;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifuture.iagriculture.fragment.FragmentGreenHouse;
import com.ifuture.iagriculture.fragment.FragmentHome;
import com.ifuture.iagriculture.fragment.FragmentToalData;
import com.ifuture.iagriculture.fragment.FragmentVideo;

/**
 * 	@Description: Fragment父类
 *  @新增Fragment方法:
 *  	1.  创建新Fragment，继承Basement。
 *  	2.  创建新Fragment的布局文件xml
 *  	3.  BaseFragment的方法newInstance中，增加新Fragment
 * 		4.  ClientMainActivity中方法setTabSelection增加相应语句
 */

public class BaseFragment extends Fragment {
	private static final String TAG = "BaseFragment";
	protected FragmentManager mFragmentManager = null;
	protected FragmentTransaction mFragmentTransaction = null;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.i(TAG, "onAttach...");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate...");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onCreateView...");
//		View v = inflater.inflate(R.layout.messages_layout, container, false);
		
		return 	super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onActivityCreated...");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStart...");
		super.onStart();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onResume...");
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onPause...");
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStop...");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroyView...");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroy...");
		super.onDestroy();
	}

	/**--------------------------------------------------------------------------------
	 * newInstance
	 * @param context
	 * @param tag 表明是什么Fragment
	 * @return 返回获得的Fragment，如FragmentVideo
	 *-------------------------------------------------------------------------------*/
	public static BaseFragment newInstance(Context context,String tag){
		BaseFragment baseFragment =  null;
		if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_HOME)){
			baseFragment = new FragmentHome();
		}
		else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_STATICS)){//统计数据，暂时用 FragmentToalData();
			baseFragment = new FragmentToalData();
		}
		else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_VIDEO)){//视频
			baseFragment = new FragmentVideo();
		}
		else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_TOTAL_DATA)){//详细数据
			baseFragment = new FragmentToalData();
		}
		else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_GREENHOUSE)){//具体大棚的界面
			baseFragment = new FragmentGreenHouse();
		}
		/*
		}else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_NEWS)){
			baseFragment = new NewsFragment();
		}else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_SETTING)){
			baseFragment = new SettingFragment();
		}
		*/
		
		return baseFragment;
		
	}

}
