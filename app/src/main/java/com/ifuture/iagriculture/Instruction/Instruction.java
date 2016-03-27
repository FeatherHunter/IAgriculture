package com.ifuture.iagriculture.Instruction;

/** 
 * @CopyRight: 王辰浩 2015~2025
 * @Author Feather Hunter(猎羽)
 * @qq: 975559549
 * @Version: 1.1
 * @Date: 2016/1/10
 * @Description: IHome自制的通信协议。包含了其中所有指令。
 **/

public class Instruction {

    public static final String TEMP_CTRL = ""+(char)0x52+(char)0x41;
    public static final String WINDDIR_CTRL = ""+(char)0x52+(char)0x42;
    public static final String WIND_CTRL = ""+(char)0x52+(char)0x43;
    public static final String WORK_CTRL = ""+(char)0x52+(char)0x44;
    public static final String AIR_CTRL = ""+(char)0x52+(char)0x45;
    public static final String ICE_CTRL = ""+(char)0x52+(char)0x46;

    public static final char WIND_HEAD = 1;
    public static final char WIND_HT = 2;
    public static final char WIND_TAIL = 3;
    public static final char WIND_TAIL_RMICE = 4;
    public static final char WIND_REMOVE_ICE = 5;

    public static final char WORK_MODE_AUTO = 1;
    public static final char WORK_MODE_ECON = 2;
    public static final char WORK_MODE_MANU = 3;
    public static final char WORK_MODE_OFF = 4;

    public static final byte AIR_OUTCYCLE = 1;
    public static final byte AIR_INCYCLE = 2;

    public static final char ICE_ON = 1;
    public static final char ICE_OFF = 2;


    public static final char CMD_PULSE = 'P'; //心跳指令
    public static final char CMD_MAN   = 'M';  //管理指令
    public static final char CMD_CTRL  = 'C';  //控制指令
    public static final char CMD_RES   = 'R';  //结果指令

    public static final char CMD_VIDEO = 4;  //视频指令

    public static final char MAN_LOGIN      = 'L'; //管理-登录
    public static final char CTL_LAMP       = 21; //控制-灯
    public static final char CTL_GET        = 22; //控制-获取
    public static final char CTL_IHome      = 23; //控制-IHome
    public static final char CTL_VIDEO      = 24; //控制-视频
    public static final char RES_LOGIN      = 'L'; //结果-登录
    public static final char RES_LAMP       = 33; //结果-灯
    public static final char RES_TEMP       = 34; //结果-温度
    public static final char RES_HUMI       = 35; //结果-湿度
    public static final char RES_IHome      = 36; //结果-IHome
    public static final char RES_VIDEO      = 37; //结果-视频
    public static final char VIDEO_START    = 41; //视频-开启
    public static final char VIDEO_STOP     = 42; //视频-关闭
    public static final char LOGIN_SUCCESS  = '1';  //登录成功
    public static final char LOGIN_FAILED   = 2;  //登录失败
    public static final char LAMP_ON        = 1;  //灯开启
    public static final char LAMP_OFF       = 2;  //灯关闭
    public static final char IHome_START    = 1;  //IHome模式开启
    public static final char IHome_STOP     = 2;  //IHome模式关闭
    public static final char VIDEO_OK       = 1;  //视频OK
    public static final char VIDEO_ERROR    = 2;  //视频错误


    public static final char CMD_HEAD   = 'H';  //管理指令
    public static final char CMD_SEP    = '/';//31单元分隔符
    public static final char CMD_END    = 'E'; //30,一个指令结束



    public static String loginMsg(String account, String password)
    {
        String msg = ""+CMD_HEAD + CMD_MAN + MAN_LOGIN + account + CMD_SEP + password + CMD_END;
        return msg;
    }
}
