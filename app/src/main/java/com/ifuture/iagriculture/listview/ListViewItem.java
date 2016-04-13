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

    public ListViewItem(int type, String text) {
        this.type = type;
        this.text = text;
    }


    public ListViewItem(int type, String text, int sectionPosition, int listPosition) {
        super();
        this.type = type;
        this.text = text;
        this.sectionPosition = sectionPosition;
        this.listPosition = listPosition;
    }

    @Override public String toString() {
        return text;
    }
    public static ArrayList<ListViewItem> getData(Context context){
        ArrayList<ListViewItem>  list=new ArrayList<ListViewItem>();

        SharedPreferences apSharedPreferences = context.getSharedPreferences("saved", Activity.MODE_PRIVATE);
        String accountString  = apSharedPreferences.getString("account", ""); // 使用getString方法获得value，注意第2个参数是value的默认值
        DatabaseOperation databaseOperation = new DatabaseOperation(accountString); //使用用户名创建数据库
        databaseOperation.createDatabase(context);//创建数据库

        String areaNames[] = databaseOperation.queryAreaName(context);
        for(int i = 0; areaNames[i] != null; i++)
        {
            list.add(new ListViewItem(ListViewItem.SECTION, areaNames[i]));
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
