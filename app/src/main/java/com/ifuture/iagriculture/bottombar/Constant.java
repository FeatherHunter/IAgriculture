package com.ifuture.iagriculture.bottombar;

public class Constant {  
    //Btn的标识  
    public static final int BTN_FLAG_HOME            = 0x01;
    public static final int BTN_FLAG_AREA_STATICS    = 0x01 << 1;
    public static final int BTN_FLAG_GHOUSE_STATICS  = 0x01 << 2;
    public static final int BTN_FLAG_VIDEO           = 0x01 << 3;
    public static final int BTN_FLAG_CSERVICE        = 0x01 << 4;
    public static final int BTN_FLAG_GREENHOUSE      = 0x01 << 5;

    //Fragment的标识  
    public static final String FRAGMENT_FLAG_HOME          = "农场";
    public static final String FRAGMENT_FLAG_GREENHOUSE    = "大棚数据";
    public static final String FRAGMENT_FLAG_AREA_STATICS  = "统计";
    public static final String FRAGMENT_FLAG_GHOUSE_STATICS= "大棚统计";
    public static final String FRAGMENT_FLAG_VIDEO         = "控制";
    public static final String FRAGMENT_FLAG_CSERVICE      = "服务";

    //底部各个Fragment显示文字
    public static final String FRAGMENT_TEXT_HOME          = "农场";
    public static final String FRAGMENT_TEXT_AREA_STATICS  = "统计";
    public static final String FRAGMENT_TEXT_GHOUSE_STATICS= "大棚统计";
    public static final String FRAGMENT_TEXT_VIDEO         = "控制";
    public static final String FRAGMENT_TEXT_CSERVICE      = "服务";


}  
