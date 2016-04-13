package com.ifuture.iagriculture.slidemenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.activity.ClientMainActivity;
import com.ifuture.iagriculture.bottombar.BaseFragment;

public class RightMenuFragment extends Fragment implements OnClickListener {

	private ClientMainActivity mAct;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.slidemenu_right_frag, null);
		mAct = (ClientMainActivity) getActivity();
		view.findViewById(R.id.tab_mynews).setOnClickListener(this);
		view.findViewById(R.id.tab_myread).setOnClickListener(this);
		view.findViewById(R.id.tab_mylocal).setOnClickListener(this);
		view.findViewById(R.id.tab_myties).setOnClickListener(this);
		view.findViewById(R.id.tab_mypics).setOnClickListener(this);
		view.findViewById(R.id.tab_myfocus).setOnClickListener(this);
		view.findViewById(R.id.tab_myvote).setOnClickListener(this);
		view.findViewById(R.id.tab_myugc).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
//		BaseFragment fragment = null;
//		switch (v.getId()) {
//		case R.id.tab_mynews:
//			fragment = new HomeFragment();
//			break;
//		case R.id.tab_myread:
//			fragment = new ReadFragment();
//			break;
//		case R.id.tab_mylocal:
//			fragment = new LocalFragment();
//			break;
//		case R.id.tab_myties:
//			fragment = new TiesFragment();
//			break;
//		case R.id.tab_mypics:
//			fragment = new PicsFragment();
//			break;
//		case R.id.tab_myfocus:
//			fragment = new FocusFragment();
//			break;
//		case R.id.tab_myvote:
//			fragment = new VoteFragment();
//			break;
//		case R.id.tab_myugc:
//			fragment = new UgcFragment();
//			break;
//		default:
//			break;
//		}
//		mAct.switchContent(fragment);
//		fragment = null;
	}

}
