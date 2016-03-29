package com.ifuture.iagriculture.bottombar;

public class Constant {  
    //Btn的标识  
    public static final int BTN_FLAG_IHOME    = 0x01;  
    public static final int BTN_FLAG_CONTRL   = 0x01 << 1;  
    public static final int BTN_FLAG_VIDEO    = 0x01 << 2;  
    public static final int BTN_FLAG_CSERVICE = 0x01 << 3;

    //public static final int BTN_FLAG_SIMPLE_DATA = 0x01 << 4;
    public static final int BTN_FLAG_TOTAL_DATA  = 0x01 << 5;

    //Fragment的标识  
    public static final String FRAGMENT_FLAG_IHOME = "温室大棚";
    public static final String FRAGMENT_FLAG_CONTRL = "控制";
    public static final String FRAGMENT_FLAG_VIDEO = "视频";   
    public static final String FRAGMENT_FLAG_CSERVICE = "客服";   


    //public static final String FRAGMENT_FLAG_SIMPLE_DATA= "简略数据";
    public static final String FRAGMENT_FLAG_TOTAL_DATA= "详细数据";

    //底部各个Fragment显示文字
    public static final String FRAGMENT_TEXT_HOUSE= "大棚";
    public static final String FRAGMENT_TEXT_CONTRL = "控制";
    public static final String FRAGMENT_TEXT_VIDEO = "视频";
    public static final String FRAGMENT_TEXT_CSERVICE = "客服";


}  
