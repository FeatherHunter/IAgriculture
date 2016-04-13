package com.ifuture.iagriculture.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @Copyright: 华韵科技有限公司
 * @Author: 2016/3/30 王辰浩
 * @Description:
 *            DatabaseHelper作为方位SQLite的助手类，提供两个方面功能。
 *            第一：getReadableDatabase(),getWritableDatabase()可以获得SQLiteDatabase对象,通过该对象对数据进行操作。
 *            第二：提供onCreate，onUpgrade两个回调函数，允许我们在创建和升级数据库时，进行相应操作。
 * @History: 2016/3/30 初始的onCreate，onUpgrade和构造函数
 *
 * @Bug集: 创建语句缺少结尾的')' : db.execSQL("create table day(hour int, minute int, second int, temperature  float, humidity float)");
 */
public class DayDatabaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    public static final String tableAreaName  = "area";
    public static final String tableTerminalName  = "terminal";
    public static final String tableTodayName  = "today";
    public static final String tableAlldayName = "allday";
    public static final String year        = "year";
    public static final String month       = "month";
    public static final String day         = "day";
    public static final String hour        = "hour";
    public static final String minute      = "minute";
    public static final String second      = "second";
    public static final String temperature = "temperature";
    public static final String humidity    = "humidity";
    //SQLiteOpenHelper子类中必须的构造函数
    public DayDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DayDatabaseHelper(Context context, String name, int version){
        this(context, name, null, version);
    }

    public DayDatabaseHelper(Context context, String name){
        this(context, name, VERSION);
    }
    @Override
    /** 第一次创建数据库时候执行
     *  实际上是在第一次得到SQLiteDatabase对象的时候才会调用这个方法：也就是执行getReadable的时候执行
     * */
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Create a Database table 'today' and 'year'");
        //执行创建表的语句
        createTodayTable(db);
        createAlldayTable(db);
        createAreaTable(db);
    }
    public void createTodayTable(SQLiteDatabase db)
    {
        db.execSQL("create table today(hour int, minute int, second int, temperature  float, humidity float, primary key(hour,minute,second))"); //
    }
    public void createAlldayTable(SQLiteDatabase db)
    {
        db.execSQL("create table allday(year int, month int, day int, hour int, temperature  float, humidity float, primary key(year,month,day,hour))");
    }

    public void createAreaTable(SQLiteDatabase db)
    {
        db.execSQL("create table area(area int primary key, name varchar(20))");
    }

    public void createTerminalTable(SQLiteDatabase db)
    {
        db.execSQL("create table terminal(area int, terminal varchar(20), primary key(area,terminal))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Update the Database for current day");
    }
}
