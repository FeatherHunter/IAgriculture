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

    public final int type;
    public final String text;

    public int sectionPosition;
    public int listPosition;
    public String areaNum = null;
    public String greenhouseNum = null;

    public ListViewItem(int type, String text) {
        this.type = type;
        this.text = text;
    }

    public ListViewItem(int type, String text, String areaNum, String greenhouseNum) {
        super();
        this.type = type;
        this.text = text;
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
            list.add(new ListViewItem(ListViewItem.SECTION, areaNames[i]));
            String greenHouseNums[] = databaseOperation.queryGHousePerArea(context, i); //i就为当前的地区号
            for(int j = 0; greenHouseNums[j] != null; j++)
            {
                list.add(new ListViewItem(ListViewItem.ITEM, "大棚"+greenHouseNums[j], ""+i, greenHouseNums[j]));
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
}
