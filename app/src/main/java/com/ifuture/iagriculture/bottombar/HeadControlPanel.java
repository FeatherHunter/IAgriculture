package com.ifuture.iagriculture.bottombar;

/**
 * Created by feather on 2016/3/29.
 */

import com.ifuture.iagriculture.R;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HeadControlPanel extends RelativeLayout implements RadioGroup.OnCheckedChangeListener{
    private Context mContext;
    private TextView mMidleTitle;
    private TextView mRightTitle;
    private RadioGroup radioGroup;
    private RadioButton rightButton;
    private RadioButton leftButton;

    private HeadPanelCallback mHeadCallback = null; //HeadPanel的回调函数（标题栏）

    private static final float middle_title_size = 19f;
    private static final float right_title_size = 15f;
    private static final int default_background_color = Color.WHITE;
            //Color.rgb(255, 255, 255);

    public HeadControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        mMidleTitle = (TextView) findViewById(R.id.midle_title);
        mRightTitle = (TextView) findViewById(R.id.right_title);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        leftButton = (RadioButton) findViewById(R.id.rb_left);
        rightButton = (RadioButton) findViewById(R.id.rb_right);
        setBackgroundColor(default_background_color);
    }

    public void initHeadPanel() {

        if (mMidleTitle != null) {
            setMiddleTitle(Constant.FRAGMENT_FLAG_HOME);
        }
        radioGroup.setOnCheckedChangeListener(this);
    }

    /**
     * @Function: public void setMiddleTitle(String s)
     * @Description: 切换Fragment的时候设置中间的标题
     * */
    public void setMiddleTitle(String s) {
        if(s.equals(Constant.FRAGMENT_FLAG_HOME) )
        {
            /*---------------------------------------------------
             *    农场界面上方标题栏（该界面用于选择具体大棚）
             *-------------------------------------------------*/
            radioGroup.setVisibility(View.INVISIBLE);
            mMidleTitle.setVisibility(View.VISIBLE); //显示“农场”
            mRightTitle.setVisibility(View.INVISIBLE);
            mMidleTitle.setText(s);
            mMidleTitle.setTextSize(middle_title_size);
            //leftButton.setChecked(true); //总结/详细中左边按键处于选中状态

        }
        else if(s.equals(Constant.FRAGMENT_FLAG_GREENHOUSE) )
        {
            /*---------------------------------------------------
             *    农场界面（此时进入具体大棚）
             *-------------------------------------------------*/
            radioGroup.setVisibility(View.VISIBLE);
            mMidleTitle.setVisibility(View.INVISIBLE);
            mRightTitle.setVisibility(View.VISIBLE);
            mMidleTitle.setText(s);
            mMidleTitle.setTextSize(middle_title_size);
            leftButton.setChecked(true); //总结/详细中左边按键处于选中状态
        }
        else if(s.equals(Constant.FRAGMENT_FLAG_AREA_STATICS) )
        {
            /*---------------------------------------------------
             *    地区数据统计界面上方标题栏
             *-------------------------------------------------*/
            radioGroup.setVisibility(View.INVISIBLE);
            mRightTitle.setVisibility(View.INVISIBLE);
            mMidleTitle.setVisibility(View.VISIBLE);
            mMidleTitle.setText(s);
            mMidleTitle.setTextSize(middle_title_size);
        }
        else if(s.equals(Constant.FRAGMENT_FLAG_GHOUSE_STATICS) )
        {
            /*---------------------------------------------------
             *    地区大棚数据统计界面上方标题栏
             *-------------------------------------------------*/
            radioGroup.setVisibility(View.VISIBLE);
            mMidleTitle.setVisibility(View.INVISIBLE);
            mRightTitle.setVisibility(View.VISIBLE);
            mMidleTitle.setText(s);
            mMidleTitle.setTextSize(middle_title_size);
            rightButton.setChecked(true); //总结/详细中右边按键处于选中状态
        }
        else if(s.equals(Constant.FRAGMENT_FLAG_VIDEO) )
        {
            radioGroup.setVisibility(View.INVISIBLE);
            mRightTitle.setVisibility(View.INVISIBLE);
            mMidleTitle.setVisibility(View.VISIBLE);
            mMidleTitle.setText(s);
            mMidleTitle.setTextSize(middle_title_size);
        }
        else if(s.equals(Constant.FRAGMENT_FLAG_CSERVICE) )
        {
            radioGroup.setVisibility(View.INVISIBLE);
            mRightTitle.setVisibility(View.INVISIBLE);
            mMidleTitle.setVisibility(View.VISIBLE);
            mMidleTitle.setText(s);
            mMidleTitle.setTextSize(middle_title_size);
        }

    }

    public void setHeadCallback(HeadPanelCallback bottomCallback){
        mHeadCallback = bottomCallback;
    }

    /**---------------------------------------------------------------------------
     * @Function: public void onCheckedChanged(RadioGroup group, int checkedId)
     * @Description: 主界面 “总结/详细”中groupbutton的监听
     * --------------------------------------------------------------------------*/
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int index = -1;
        int radioButtonId = group.getCheckedRadioButtonId();
        System.out.println("ID:" + radioButtonId+" "+R.id.rb_left+"/"+R.id.rb_right);
        switch(radioButtonId){
            case R.id.rb_left:
                index = Constant.BTN_FLAG_GREENHOUSE;
                //mMsgBtn.setChecked(Constant.BTN_FLAG_IHOME);
                break;
            case R.id.rb_right:
                index = Constant.BTN_FLAG_GHOUSE_STATICS;
                //mContactsBtn.setChecked(Constant.BTN_FLAG_CONTRL);
                break;
            default:break;
        }
        if(mHeadCallback != null){
            mHeadCallback.onHeadPanelClick(index);
        }
    }

    public interface HeadPanelCallback{
        public void onHeadPanelClick(int itemId);
    }



}


