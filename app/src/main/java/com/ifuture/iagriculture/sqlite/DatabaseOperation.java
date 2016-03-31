package com.ifuture.iagriculture.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @Class: DatabaseOperation
 * @Author: Created by 王辰浩 on 2016/3/30.
 * @Description: 进行数据库的相关操作，分为两大类表。
 *       第一类： 当天温度，湿度等按秒记录的精准数据
 *       第二类： 以年月日，基本单位为小时的粗略数据
 * @Debug: 1. SQL语句中，where使用and或者or分隔如："hour=? and minute=? and second=?" 而不是 "hour=? , minute=? ,second=?"
 *          2. cursor.getString(0)刚开始的下标为-1，记得先 cursor.moveToNext();
 */
public class DatabaseOperation {

    private String databaseName = "igreen_db";
    DayDatabaseHelper dbHelper  = null;
    public String tableNameString   = DayDatabaseHelper.tableName;
    public String hourString        = DayDatabaseHelper.hour;
    public String minuteString      = DayDatabaseHelper.minute;
    public String secondString      = DayDatabaseHelper.second;
    public String tempString        = DayDatabaseHelper.temperature;
    public String humiString        = DayDatabaseHelper.humidity;

    /*
     *  创建数据库，如果表不存在则创建相应表
     * */
    public void createDatabase(Context context)
    {
        System.out.println("createDatabase");
        dbHelper = new DayDatabaseHelper(context,databaseName);//创建一个DatabaseHelper对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();//只有调用了DatabaseHelper对象的getReadableDatabase()方法，
                                                           // 或者是getWritableDatabase()方法之后，才会创建，或打开一个数据库
//        if(!exits(db, tableNameString))//如果表不存在
//        {
//            dbHelper.onCreate(db);
//        }
    }
    /*
     *  判断表是否存在
     * */
    public boolean exits(SQLiteDatabase db, String table){
        boolean exits = false;
        String sql = "select * from "+table;
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.getCount()!=0){
            exits = true;
        }
        return exits;
    }
    /*
     *  更新数据库
     * */
    public void updateDatabase(Context context)
    {
        dbHelper = new DayDatabaseHelper(context,databaseName,2);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
    }
    /**
     * =============================================================================================
     * @Description
     *  关于当天各方面数据的操作：增，删，改，查
     * =============================================================================================
     * */
    public void insertToday(Context context, int hour, int min, int sec, float temp, float humi){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(recordExitsToday(db, hour, min, sec)) //已经存在

        {

        }
        //生成ContentValues对象
        ContentValues values = new ContentValues();
        //想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
        values.put(hourString, hour);
        values.put(minuteString,min);
        values.put(secondString,sec);
        values.put(tempString,temp);
        values.put(humiString,humi);
        //调用insert方法，就可以将数据插入到数据库当中
        db.insert(tableNameString, null, values);
    }

    private boolean recordExitsToday(SQLiteDatabase db, int hour, int min, int sec)
    {
        Cursor cursor = db.rawQuery("select * from today where hour=? and minute=? and second=?", new String[]{""+hour, ""+min,""+sec});
        if(cursor.getCount() != 0)//存在
        {
            return true;
        }
        return false; //不存在
    }

    public void updateRecordToday(Context context, int hour, int min, int sec, float temp, float humi)
    {
        // TODO Auto-generated method stub
        //得到一个可写的SQLiteDatabase对象
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(hourString, hour);
        values.put(minuteString,min);
        values.put(secondString,sec);
        values.put(tempString,temp);
        values.put(humiString,humi);
        //第一个参数是要更新的表名
        //第二个参数是一个ContentValeus对象
        //第三个参数是where子句
        db.update(tableNameString, values, "hour=? and minute=? and second=?", new String[]{"" + hour, "" + min, "" + sec});
    }
    /**
     * @Description:
     *      查询当前数据，精准到秒
     * @param hour,min,sec
     * @return 查询到的温度，湿度
     * */
    public float[] querySecToday(Context context, int hour, int min, int sec)
    {
        System.out.println("querySecPerDay");

        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableNameString, new String[]{tempString,humiString}, "hour=? and minute=? and second=?", new String[]{""+hour,""+min,""+sec}, null, null, null);

        cursor.moveToNext();
        float temp = cursor.getFloat(cursor.getColumnIndex(tempString));
        float humi = cursor.getFloat(cursor.getColumnIndex(humiString));
        System.out.println("temp:" + temp + " humi:" + humi);
        return new float[]{temp, humi};
    }
    /**
     * @Description:
     *      查询当前数据，精准到分钟
     * @param hour,min
     * @return 查询到的温度，湿度
     * */
    public float[] queryMinuteToday(Context context, int hour, int min)
    {
        System.out.println("queryMinutePerDay");

        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();                                                                   //"hour      =  hour   and   minute        =  min"
        Cursor cursor = db.query(tableNameString, new String[]{"avg(temperature), avg(humidity)"}, null, null, "hour, minute", hourString + "=" + hour + " and " + minuteString + "=" + min, null);

        cursor.moveToNext(); //一开始下标为-1
        float temp = Float.parseFloat(cursor.getString(0));
        float humi = Float.parseFloat(cursor.getString(1));
        System.out.println("temp:" + temp + " humi:" + humi);
        return new float[]{temp, humi};
    }
    /**
     * @Description:
     *      查询当前数据，精准到小时
     * @param hour
     * @return 查询到的温度，湿度
     * */
    public float[] queryHourToday(Context context, int hour)
    {
        System.out.println("queryHourPerDay");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();                                                                   //"hour      =  hour   and   minute        =  min"
        Cursor cursor = db.query(tableNameString, new String[]{"avg(temperature), avg(humidity)"}, null, null, "hour", hourString + "=" + hour, null);

        cursor.moveToNext(); //一开始下标为-1
        float temp = Float.parseFloat(cursor.getString(0));
        float humi = Float.parseFloat(cursor.getString(1));
        System.out.println("temp:" + temp + " humi:" + humi);
        return new float[]{temp, humi};
    }
    /**
     * =============================================================================================
     * @Description
     *  关于年月日表中各方面数据的操作：增，删，改，查
     * =============================================================================================
     * */
    public void insert2day(Context context, int hour, int min, int sec, float temp, float humi){
        //生成ContentValues对象
        ContentValues values = new ContentValues();
        //想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
        values.put(hourString, hour);
        values.put(minuteString,min);
        values.put(secondString,sec);
        values.put(tempString,temp);
        values.put(humiString,humi);
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //调用insert方法，就可以将数据插入到数据库当中
        db.insert(tableNameString, null, values);
    }

    public void updateRecord2day(Context context, int hour, int min, int sec, float temp, float humi)
    {
        // TODO Auto-generated method stub
        //得到一个可写的SQLiteDatabase对象
        dbHelper = new DayDatabaseHelper(context,"test_mars_db");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(hourString, hour);
        values.put(minuteString,min);
        values.put(secondString,sec);
        values.put(tempString,temp);
        values.put(humiString,humi);
        //第一个参数是要更新的表名
        //第二个参数是一个ContentValeus对象
        //第三个参数是where子句
        db.update(tableNameString, values, "hour=? and minute=? and second=?", new String[]{"" + hour, "" + min, "" + sec});
    }
    /**
     * @Description:
     *      查询当前数据，精准到秒
     * @param:
     * @return 查询到的温度，湿度
     * */
    public void  queryHourPerYear(Context context, int year, int month, int day, int hour)
    {
//        System.out.println("querySecPerDay");
//
//        dbHelper = new DayDatabaseHelper(context, databaseName);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(tableNameString, new String[]{tempString,humiString}, "hour=? and minute=? and second=?", new String[]{""+hour,""+min,""+sec}, null, null, null);
//
//        cursor.moveToNext();
//        float temp = cursor.getFloat(cursor.getColumnIndex(tempString));
//        float humi = cursor.getFloat(cursor.getColumnIndex(humiString));
//        System.out.println("temp:" + temp + " humi:" + humi);
//        return new float[]{temp, humi};
    }
    /**
     * @Description:
     *      查询当前数据，精准到分钟
     * @param:
     * @return 查询到的温度，湿度
     * */
    public void  queryDayPerYear(Context context, int year, int month, int day)
    {
//        System.out.println("queryMinutePerDay");
//
//        dbHelper = new DayDatabaseHelper(context, databaseName);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();                                                                   //"hour      =  hour   and   minute        =  min"
//        Cursor cursor = db.query(tableNameString, new String[]{"avg(temperature), avg(humidity)"}, null, null, "hour, minute", hourString + "=" + hour + " and " + minuteString + "=" + min, null);
//
//        cursor.moveToNext(); //一开始下标为-1
//        float temp = Float.parseFloat(cursor.getString(0));
//        float humi = Float.parseFloat(cursor.getString(1));
//        System.out.println("temp:" + temp + " humi:" + humi);
//        return new float[]{temp, humi};
    }
    /**
     * @Description:
     *      查询当前数据，精准到小时
     * @param
     * @return 查询到的温度，湿度
     * */
    public void queryMonthPerYear(Context context, int year, int month)
    {
//        System.out.println("queryHourPerDay");
//        dbHelper = new DayDatabaseHelper(context, databaseName);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();                                                                   //"hour      =  hour   and   minute        =  min"
//        Cursor cursor = db.query(tableNameString, new String[]{"avg(temperature), avg(humidity)"}, null, null, "hour", hourString + "=" + hour, null);
//
//        cursor.moveToNext(); //一开始下标为-1
//        float temp = Float.parseFloat(cursor.getString(0));
//        float humi = Float.parseFloat(cursor.getString(1));
//        System.out.println("temp:" + temp + " humi:" + humi);
//        return new float[]{temp, humi};
    }

}
