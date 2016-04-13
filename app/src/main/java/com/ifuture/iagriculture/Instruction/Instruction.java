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
    public static final char CMD_HEAD   = 'H';  //管理指令
    public static final char CMD_SEP    = '/';//31单元分隔符
    public static final char CMD_END    = 'E'; //30,一个指令结束

    public static final char CMD_HEART  = '0'; //心跳指令
    public static final char CMD_MAN    = 'M';  //管理指令
    public static final char CMD_CTRL   = 'C';  //控制指令
    public static final char CMD_RES    = 'R';  //结果指令

    /*心跳类：1.心跳指令*/
    public static final char HEART_BEAT = 'B';

    /*管理类：1.登录指令*/
    public static final char MAN_LOGIN  = 'L';//管理-登录

    /*控制类：1.灯具*/
    public static final char CTRL_LAMP  = 'A';

    /*结果类-：1.登录结果 2.温度 3.湿度 4.温湿度 5.终端离线 6.设备离线*/
    public static final char RES_LOGIN  = 'L';//结果-登录
    public static final char RES_TEMP   = 'P';//结果-温度
    public static final char RES_HUMI   = 'U';
    public static final char RES_TOFFL  = 'O';
    public static final char RES_DOFFL  = 'D';

    /*登陆成功/失败*/
    public static final char LOGIN_SUCCESS  = '1';//登录成功
    public static final char LOGIN_FAILED   = '0'; //登录失败

    /*灯开关*/
    public static final char LAMP_ON        = '1';
    public static final char LAMP_OFF       = '0';

    public static String loginMsg(String account, String password)
    {
        String msg = ""+CMD_HEAD + CMD_MAN + MAN_LOGIN + account + CMD_SEP + password + CMD_END;
        return msg;
    }

    public static String ctrlLamp(String terminalNumber, String deviceNumber, boolean lamp_on)
    {
        String msg;
        if(lamp_on)
        {
            msg = ""+ CMD_HEAD + CMD_CTRL + CTRL_LAMP + terminalNumber + CMD_SEP + deviceNumber + CMD_SEP + LAMP_ON + CMD_END;
        }
        else
        {
            msg = ""+ CMD_HEAD + CMD_CTRL + CTRL_LAMP + terminalNumber + CMD_SEP + deviceNumber + CMD_SEP + LAMP_OFF + CMD_END;
        }
        return msg;
    }
}
