package com.ifuture.iagriculture.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.bottombar.BaseFragment;

public class FragmentContrl extends BaseFragment{

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.home_fragment, container, false);
    }
}
