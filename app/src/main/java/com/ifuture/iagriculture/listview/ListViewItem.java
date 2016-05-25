package com.ifuture.iagriculture.listview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifuture.iagriculture.sqlite.DatabaseOperation;

import java.util.ArrayList;

/**
 *  @Class: public class ListViewItem
 *  @Description: 用于主界面(Home)中选择地区和大棚
 *  @Date: Created by feather on 2016/4/12.
 */
public class ListViewItem {

    public static final int ITEM = 0;
    public static final int SECTION = 1;
    private boolean isWarning = true; //是否进行警告提示

    public final int type;
    public final String text;

    public int sectionPosition;
    public int listPosition;
    public String areaNum = null;
    public String greenhouseNum = null;

    public ListViewItem(int type, String text, boolean isWarning) {
        this.type = type;
        this.text = text;
        this.isWarning = isWarning;
    }

    public ListViewItem(int type, String text, String areaNum, String greenhouseNum, boolean isWarning) {
        super();
        this.type = type;
        this.text = text;
        this.isWarning = isWarning;
        if(type == ITEM)
        {
            this.areaNum = areaNum;
            this.greenhouseNum = greenhouseNum;

        }
    }


//    public ListViewItem(int type, String text, int sectionPosition, int listPosition) {
//        super();
//        this.type = type;
//        this.text = text;
//        this.sectionPosition = sectionPosition;
//        this.listPosition = listPosition;
//    }

    @Override public String toString() {
        return text;
    }

    /**-----------------------------------------------------------------------------
     *  @Function: getData
     *  @description: 获取存储地区号和大棚号的链表，用于设置listview来显示
     *  @return 得到的链表
     *-------------------------------------------------------------------------------*/
    public static ArrayList<ListViewItem> getData(Context context){
        ArrayList<ListViewItem>  list=new ArrayList<ListViewItem>();

        SharedPreferences apSharedPreferences = context.getSharedPreferences("saved", Activity.MODE_PRIVATE);
        String accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
        DatabaseOperation databaseOperation = new DatabaseOperation(accountString); //使用用户名创建数据库
        databaseOperation.createDatabase(context);//创建数据库

        /* -----------------------------------------------------------------
	     *             查询地区名,并且根据地区号获取该地区所有的大棚号
	     * -----------------------------------------------------------------*/
        String areaNames[] = databaseOperation.queryAreaName(context);
        for(int i = 0; areaNames[i] != null; i++)
        {
            String terminal[] = databaseOperation.queryTerminalPerArea(context, Integer.parseInt(areaNames[i]));//查询该地区是否存在终端
            if(terminal[0] == null)//不存在
            {
                list.add(new ListViewItem(ListViewItem.SECTION, areaNames[i], true));//不存在终端
            }
            else
            {
                list.add(new ListViewItem(ListViewItem.SECTION, areaNames[i], false));//存在终端、不需要警告
            }
            String greenHouseNums[] = databaseOperation.queryGHousePerArea(context, i); //i就为当前的地区号
            for(int j = 0; greenHouseNums[j] != null; j++)
            {
                String devices[] = databaseOperation.queryDevicePerGHouse(context, Integer.parseInt(areaNames[i]), greenHouseNums[j]);//查询该大棚是否存在设备
                if(devices[0] == null)//不存在
                {
                    list.add(new ListViewItem(ListViewItem.ITEM, "大棚"+greenHouseNums[j], ""+i, greenHouseNums[j], true));
                }
                else
                {
                    list.add(new ListViewItem(ListViewItem.ITEM, "大棚"+greenHouseNums[j], ""+i, greenHouseNums[j], false));
                }
            }
        }

        return list;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }

    public void setSectionPosition(int sectionPosition) {
        this.sectionPosition = sectionPosition;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }


    public boolean getWarning() {
        return isWarning;
    }

    public void setWarning(boolean warning) {
        isWarning = warning;
    }
}
