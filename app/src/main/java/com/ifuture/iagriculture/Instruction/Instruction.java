package com.ifuture.iagriculture.Instruction;

import android.content.Context;
import android.content.Intent;

/**
 * @CopyRight: 王辰浩 2015~2025
 * @Author Feather Hunter(猎羽)
 * @qq: 975559549
 * @Version: 1.1 2016/1/10
 *            2.0 2016/4/14
 * @Date: 2016/4/14
 * @Description: IHome自制的通信协议。包含了其中所有指令。以及包含得到相应指令的转换方法。
 **/

public class Instruction {
    public static final char CMD_HEAD   = 'H';  //管理指令
    public static final char CMD_SEP    = '/';//31单元分隔符
    public static final char CMD_END    = 'E'; //30,一个指令结束

    public static final char CMD_HEART  = '0'; //心跳指令
    public static final char CMD_MAN    = 'M';  //管理指令
    public static final char CMD_CTRL   = 'C';  //控制指令
    public static final char CMD_RES    = 'R';  //结果指令
    public static final char CMD_BAND   = 'D';

    /*心跳类：1.心跳指令*/
    public static final char HEART_BEAT = 'B';

    /*管理类：1.登录指令*/
    public static final char MAN_LOGIN  = 'L';//管理-登录

    /*控制类：1.灯具*/
    public static final char CTRL_LAMP     = 'a';
    public static final char CTRL_MOTOR    = 'b';
    public static final char CTRL_PUMP     = 'c';
    public static final char CTRL_AUTOTEMP = 'd';
    public static final char CTRL_SETTEMP  = 'e';
    public static final char CTRL_AUTOHUMI = 'f';
    public static final char CTRL_SETHUMI  = 'g';

    /*结果类-：1.登录结果 2.温度 3.湿度 4.温湿度 5.终端离线 6.设备离线*/
    public static final char RES_LOGIN  = 'L';//结果-登录
    public static final char RES_TEMP   = 'P';//结果-温度
    public static final char RES_HUMI   = 'U';
    public static final char RES_TOFFL  = 'T';

    public static final char RES_BAREA  = 'A';
    public static final char RES_BTERM  = 'B';
    public static final char RES_BGHOU  = 'C';
    public static final char RES_BDEVC  = 'D';

    /*绑定指令*/
    public static final char BAND_AREA   =   'A';
    public static final char BAND_TERM   =   'T';
    public static final char BAND_GHOUS  =   'G';
    public static final char BAND_DEVICE =   'D';

    /*登陆成功/失败*/
    public static final char LOGIN_SUCCESS  = '1';//登录成功
    public static final char LOGIN_FAILED   = '0'; //登录失败

    /*灯开关*/
    public static final char BAND_SUCCESS   = '1';
    public static final char BAND_FAILED    = '0';

    /*灯开关*/
    public static final char LAMP_ON        = '1';
    public static final char LAMP_OFF       = '0';

    public static final char AUTOTEMP_ON   = '1';
    public static final char AUTOTEMP_OFF  = '0';

    public static final char AUTOHUMI_ON   = '1';
    public static final char AUTOHUMI_OFF  = '0';

    /** ---------------------------------------------------------------------
     *   @Function:    loginMsg
     *   @param account  账户
     *   @param password 密码
     *   @return string : 登录的指令
     * ---------------------------------------------------------------------*/
    public static String loginMsg(String account, String password)
    {
        String msg = ""+CMD_HEAD + CMD_MAN + MAN_LOGIN + account + CMD_SEP + password + CMD_END;
        return msg;
    }

    /** ---------------------------------------------------------------------
     *   @Function:    ctrlLamp
     *   @param areaNum           终端号
     *   @param greenHouseNumber  大棚号
     *   @param deviceNum         设备号
     *   @param lamp_on        灯的开/关
     *   @return 绑定地区号和大棚号的指令
     * ---------------------------------------------------------------------*/
    public static String ctrlLamp(String areaNum, String greenHouseNumber, String deviceNum, boolean lamp_on)
    {
        String msg;
        if(lamp_on)
        {
            msg = ""+ CMD_HEAD + CMD_CTRL + CTRL_LAMP + areaNum + CMD_SEP + greenHouseNumber + CMD_SEP + deviceNum + CMD_SEP + LAMP_ON + CMD_END;
        }
        else
        {
            msg = ""+ CMD_HEAD + CMD_CTRL + CTRL_LAMP + areaNum + CMD_SEP + greenHouseNumber + CMD_SEP + deviceNum + CMD_SEP + LAMP_OFF + CMD_END;
        }
        return msg;
    }

    /** ---------------------------------------------------------------------
     *   @Function:    bandArea
     *   @param areaNum  地区号
     *   @param areaName 地区名
     *   @return string : 创建地区号和地区名的指令
     * ---------------------------------------------------------------------*/
    public static String bandArea(String areaNum, String areaName)
    {
        String msg = "" + CMD_HEAD + CMD_BAND + BAND_AREA + areaNum + CMD_SEP + areaName + CMD_END;
        return msg;
    }

    /** ---------------------------------------------------------------------
     *   @Function:    bandTerminal
     *   @param areaNum     地区号
     *   @param terminalNum 终端号
     *   @return string : 绑定地区号和终端号的指令
     * ---------------------------------------------------------------------*/
    public static String bandTerminal(String areaNum, String terminalNum)
    {
        String msg = "" + CMD_HEAD + CMD_BAND + BAND_TERM + areaNum + CMD_SEP + terminalNum + CMD_END;
        return msg;
    }

    /** ---------------------------------------------------------------------
     *   @Function:    bandGHouse
     *   @param areaNum 地区号
     *   @param gHouseNum 大棚号
     *   @return string : 绑定地区号和大棚号的指令
     * ---------------------------------------------------------------------*/
    public static String bandGHouse(String areaNum, String gHouseNum)
    {
        String msg = "" + CMD_HEAD + CMD_BAND + BAND_GHOUS + areaNum + CMD_SEP + gHouseNum + CMD_END;
        return msg;
    }

    /** ---------------------------------------------------------------------
     *   @Function:    bandDevice
     *   @param areaNum   地区号
     *   @param gHouseNum 大棚号
     *   @param deviceNum 设备号
     *   @return string : 绑定设备号
     * ---------------------------------------------------------------------*/
    public static String bandDevice(String areaNum, String gHouseNum, String deviceNum)
    {
        String msg = "" + CMD_HEAD + CMD_BAND + BAND_DEVICE + areaNum + CMD_SEP + gHouseNum + CMD_SEP + deviceNum + CMD_END;
        return msg;
    }

    /**-------------------------------------------------------------------
     * 	 @Function: private void broadcastMsgToServer(Context context,String msg)
     * 	 @Description: 发送控制等信息给广播
     * 	 @param msg 需要发送的信息
     *----------------------------------------------------------------------*/
    static public void broadcastMsgToServer(Context context, String msg)
    {
        Intent intent = new Intent();
        intent.setAction(intent.ACTION_MAIN);
        intent.putExtra("type", "send");
        intent.putExtra("send", msg);
        context.sendBroadcast(intent);
    }

    /** ---------------------------------------------------------------------
     *   @Function:    autoTemp
     *   @param areaNum   地区号
     *   @param gHouseNum 大棚号
     *   @param on        自动温控开/关
     *   @return string : 绑定设备号
     * ---------------------------------------------------------------------*/
    public static String autoTemp(String areaNum, String gHouseNum, boolean on)
    {
        String msg = null;
        if(on)
        {
            msg = "" + CMD_HEAD + CMD_CTRL + CTRL_AUTOTEMP + areaNum + CMD_SEP + gHouseNum + CMD_SEP + AUTOTEMP_ON + CMD_END;
        }
        else
        {
            msg = "" + CMD_HEAD + CMD_CTRL + CTRL_AUTOTEMP + areaNum + CMD_SEP + gHouseNum + CMD_SEP + AUTOTEMP_OFF+ CMD_END;
        }
        return msg;
    }

    /** ---------------------------------------------------------------------
     *   @Function:    autoHumi
     *   @param areaNum   地区号
     *   @param gHouseNum 大棚号
     *   @param on        自动温控开/关
     *   @return string : 绑定设备号
     * ---------------------------------------------------------------------*/
    public static String autoHumi(String areaNum, String gHouseNum, boolean on)
    {
        String msg = null;
        if(on)
        {
            msg = "" + CMD_HEAD + CMD_CTRL + CTRL_AUTOHUMI + areaNum + CMD_SEP + gHouseNum + CMD_SEP + AUTOHUMI_ON + CMD_END;
        }
        else
        {
            msg = "" + CMD_HEAD + CMD_CTRL + CTRL_AUTOHUMI + areaNum + CMD_SEP + gHouseNum + CMD_SEP + AUTOHUMI_OFF+ CMD_END;
        }
        return msg;
    }

    /** ---------------------------------------------------------------------
     *   @Function:    setTempLimit
     *   @描述： 设置大棚温度的范围
     *   @param areaNum   地区号
     *   @param gHouseNum 大棚号
     *   @return string : 指令msg
     * ---------------------------------------------------------------------*/
    public static String setTempLimit(String areaNum, String gHouseNum, String max, String min)
    {
        String msg = null;

        msg = "" + CMD_HEAD + CMD_CTRL + CTRL_SETTEMP + areaNum + CMD_SEP + gHouseNum + CMD_SEP + max + CMD_SEP + min + CMD_END;

        return msg;
    }

    /** ---------------------------------------------------------------------
     *   @Function:    setHumiLimit
     *   @描述： 设置大棚湿度的范围
     *   @param areaNum   地区号
     *   @param gHouseNum 大棚号
     *   @return string : 指令msg
     * ---------------------------------------------------------------------*/
    public static String setHumiLimit(String areaNum, String gHouseNum, String max, String min)
    {
        String msg = null;

        msg = "" + CMD_HEAD + CMD_CTRL + CTRL_SETHUMI + areaNum + CMD_SEP + gHouseNum + CMD_SEP + max + CMD_SEP + min + CMD_END;

        return msg;
    }

}
