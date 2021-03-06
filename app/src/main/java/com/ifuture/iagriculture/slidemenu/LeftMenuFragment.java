package com.ifuture.iagriculture.slidemenu;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.activity.ClientMainActivity;
import com.ifuture.iagriculture.wxapi.WXEntryActivity;

public class LeftMenuFragment extends Fragment implements OnClickListener {

	private ClientMainActivity clientMainActivity;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.slidemenu_left_frag, null);
		clientMainActivity = (ClientMainActivity) getActivity();
		view.findViewById(R.id.tab_news).setOnClickListener(this);
		//view.findViewById(R.id.tab_read).setOnClickListener(this);
		view.findViewById(R.id.tab_local).setOnClickListener(this);
		//view.findViewById(R.id.tab_ties).setOnClickListener(this);
		//view.findViewById(R.id.tab_pics).setOnClickListener(this);
		//view.findViewById(R.id.tab_focus).setOnClickListener(this);
		view.findViewById(R.id.tab_vote).setOnClickListener(this);
		view.findViewById(R.id.tab_ugc).setOnClickListener(this);
		view.findViewById(R.id.tab_share).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
//		BaseFragment fragment = null;
		switch (v.getId()) {
		case R.id.tab_share:
			Intent intent = new Intent();
			intent.setClass(getActivity(), WXEntryActivity.class);
			getActivity().startActivity(intent);
			break;
		case R.id.tab_ugc:
			/*---------------------------------------------
			 *        告诉ClientActivity 我们要回到主界面
			 *--------------------------------------------*/
			System.out.println("KEYCODE_BACK");
			Intent intent1 = new Intent();
			intent1.putExtra("type", "ClientMainBack");
			intent1.setAction(intent1.ACTION_MAIN);
			clientMainActivity.sendBroadcast(intent1);

			clientMainActivity.unregisterReceiver(clientMainActivity.getContrlReceiver());//解除注册的Receiver
			clientMainActivity.finish();
			break;
//		case R.id.tab_local:
//			fragment = new LocalFragment();
//			break;
//		case R.id.tab_ties:
//			fragment = new TiesFragment();
//			break;
//		case R.id.tab_pics:
//			fragment = new PicsFragment();
//			break;
//		case R.id.tab_focus:
//			fragment = new FocusFragment();
//			break;
//		case R.id.tab_vote:
//			fragment = new VoteFragment();
//			break;
//		case R.id.tab_ugc:
//			fragment = new UgcFragment();
//			break;
		default:
			break;
		}
//		mAct.switchContent(fragment);
//		fragment = null;
	}

}
