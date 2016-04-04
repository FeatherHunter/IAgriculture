package com.ifuture.iagriculture.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ifuture.iagriculture.Calendar.TodayTime;

/**
 * @Copyright: 华韵科技有限公司
 * @Class: DatabaseOperation
 * @Author: Created by 王辰浩 on 2016/3/30.
 * @Description: 进行数据库的相关操作，分为两大类表。
 *       第一类： 当天温度，湿度等按秒记录的精准数据
 *       第二类： 以年月日，基本单位为小时的粗略数据
 *
 * @Function List:
 *       1. void createDatabase(Context context)// 创建数据库
 *       2. boolean tabbleIsExist(SQLiteDatabase db, String tableName) //判断数据库是否存在
 *       3. void updateDatabase(Context context)                       //更新数据库
 *       4. void clearTableToday(Context context)                      //清除today表
 *       5. void clearTableAllday(Context context)                     //清除allday表
 *       6. void insertToday(Context context, int hour, int min, int sec, float temp, float humi)               //插入完整数据->today
 *       7. void insertToday(Context context, int hour, int min, int sec, float tempOrHumi, String optionString)//插入温度or湿度->today
 *       8. boolean recordExitsToday(SQLiteDatabase db, int hour, int min, int sec)                             //查询today表中是否存在某记录
 *       9. void updateRecordToday(Context context, int hour, int min, int sec, float temp, float humi)         //更新today表数据
 *       10.void updateRecordToday(Context context, int hour, int min, int sec,  float tempOrHumi, String optionString)//更新today表温度or湿度
 *       11.float[] querySecToday(Context context, int hour, int min, int sec)      //在today表中查询数据（秒为单位）
 *       12.float[] queryMinuteToday(Context context, int hour, int min)            //在today表中查询数据（分钟为单位）
 *       13.float[] queryHourToday(Context context, int hour)                       //在today表中查询数据（小时为单位）
 *       14.void queryToday(Context context) //显示today表中所有数据（用于调试）
 *       15.void insertAllday(Context context, int year, int month, int day, int hour, float temp, float humi)               //插入全部数据到allday表
 *       16.void insertAllday(Context context, int year, int month, int day, int hour, float tempOrHumi, String optionString)//插入温度or湿度到allday表
 *       17.void updateRecordAllday(Context context, int year, int month, int day, int hour, float temp, float humi)               //更新today表的数据
 *       18.void updateRecordAllday(Context context, int year, int month, int day, int hour, float tempOrHumi, String optionString)//更新today表中的温度or湿度
 *       19.boolean recordExitsAllday(SQLiteDatabase db, int year, int month, int day, int hour)    //判断Allday表中是否存在相应记录
 *       20.float[] queryHourPerYear(Context context, int year, int month, int day, int hour)   //在allday表中查询数据  （hour为单位）
 *       21.float[] queryDayPerYear(Context context, int year, int month, int day)              //在allday表中查询平均值（day为单位）
 *       22.float[] queryMaxDayPerYear(Context context, int year, int month, int day)           //在allday表中查询最大值（day为单位）
 *       23.float[] queryMinDayPerYear(Context context, int year, int month, int day)           //在allday表中查询最小值（day为单位）
 *       24.float[] queryMonthPerYear(Context context, int year, int month)                     //在allday表中查询数据  （month为单位）
 *       25.void queryAllday(Context context) //显示allday表中所有数据（用于调试）
 *       26.void deleteTable(Context context) //删除today表
 *       27.void switchTodayToAllday(Context context, int start, int end) //将today表中start到end-1的数据转换到allday表中
 *
 * @History: 2016/3/30-4/3 完成today表的增删改查的功能
 *            2016/4/3      year表的注册
 *
 * @Debug: 1. SQL语句中，where使用and或者or分隔如："hour=? and minute=? and second=?" 而不是 "hour=? , minute=? ,second=?"
 *          2. cursor.getString(0)刚开始的下标为-1，记得先 cursor.moveToNext();
 */
public class DatabaseOperation {

    private String databaseName = "igreen_db";
    DayDatabaseHelper dbHelper  = null;
    public String tableNameString   = DayDatabaseHelper.tableTodayName;
    public String tableAlldayString = DayDatabaseHelper.tableAlldayName;
    public String yearString        = DayDatabaseHelper.year;
    public String monthString       = DayDatabaseHelper.month;
    public String dayString         = DayDatabaseHelper.day;
    public String hourString        = DayDatabaseHelper.hour;
    public String minuteString      = DayDatabaseHelper.minute;
    public String secondString      = DayDatabaseHelper.second;
    public String tempString        = DayDatabaseHelper.temperature;
    public String humiString        = DayDatabaseHelper.humidity;

    /**
     *  创建数据库，如果表不存在则创建相应表
     * */
    public void createDatabase(Context context)
    {
        System.out.println("createDatabase");
        dbHelper = new DayDatabaseHelper(context,databaseName);//创建一个DatabaseHelper对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();//只有调用了DatabaseHelper对象的getReadableDatabase()方法，
                                                           // 或者是getWritableDatabase()方法之后，才会创建，或打开一个数据库
        /*---------------------------------------------------------------
         * 检查表是否存在，不存在则新建表
         *---------------------------------------------------------------*/
        if(!tabbleIsExist(db, tableNameString))//如果today表不存在
        {
            dbHelper.createTodayTable(db);
        }
        if(!tabbleIsExist(db, tableAlldayString))
        {
            dbHelper.createAlldayTable(db);
        }
    }
    /**
     * 判断某张表是否存在
     * @param tableName 表名
     * @return
     */
    public boolean tabbleIsExist(SQLiteDatabase db, String tableName){
        boolean result = false;
        if(tableName == null){
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }

    /**
     *  更新数据库
     **/
    public void updateDatabase(Context context)
    {
        dbHelper = new DayDatabaseHelper(context,databaseName,2);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
    }

    /**
     *  清除表today的数据
     **/
    public void clearTableToday(Context context)
    {
        System.out.println("clear Table Today");
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from " + tableNameString);
    }
    /**
     *  清除表allday的数据
     **/
    public void clearTableAllday(Context context)
    {
        System.out.println("clear Table Allday");
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from " + tableAlldayString);
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
        System.out.println("insertToday");
        if(recordExitsToday(db, hour, min, sec)) //已经存在则更新
        {
            updateRecordToday(context, hour, min, sec, temp, humi); //更新数据
        }
        else//不存在，则插入
        {
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
    }

    public void insertToday(Context context, int hour, int min, int sec, float tempOrHumi, String optionString){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertToday optionString");
        if(recordExitsToday(db, hour, min, sec)) //已经存在则更新
        {
            updateRecordToday(context, hour, min, sec, tempOrHumi, optionString); //更新数据
        }
        else//不存在，则插入
        {
            /*插入湿度*/
            if(optionString.equals(humiString))
            {
                ContentValues values = new ContentValues();//生成ContentValues对象
                //想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
                values.put(hourString, hour);
                values.put(minuteString,min);
                values.put(secondString,sec);
                values.put(humiString,tempOrHumi);
                db.insert(tableNameString, null, values);//将数据插入到数据库当中
            }
            else if(optionString.equals(tempString))//更新温度
            {
                ContentValues values = new ContentValues();//生成ContentValues对象
                //想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
                values.put(hourString, hour);
                values.put(minuteString,min);
                values.put(secondString,sec);
                values.put(tempString,tempOrHumi);
                db.insert(tableNameString, null, values);//将数据插入到数据库当中
            }
        }
    }

    private boolean recordExitsToday(SQLiteDatabase db, int hour, int min, int sec)
    {
        Cursor cursor = db.rawQuery("select * from today where hour=? and minute=? and second=?", new String[]{"" + hour, "" + min, "" + sec});
        if(cursor.getCount() != 0)//存在
        {
            return true;
        }
        return false; //不存在
    }

    public void updateRecordToday(Context context, int hour, int min, int sec, float temp, float humi)
    {
        System.out.println("updateRecordToday");
        // TODO Auto-generated method stub
        //得到一个可写的SQLiteDatabase对象
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(tempString,temp);
        values.put(humiString,humi);
        //第一个参数是要更新的表名
        //第二个参数是一个ContentValeus对象
        //第三个参数是where子句
        db.update(tableNameString, values, "hour=? and minute=? and second=?", new String[]{"" + hour, "" + min, "" + sec});
    }
    public void updateRecordToday(Context context, int hour, int min, int sec,  float tempOrHumi, String optionString)
    {
        // TODO Auto-generated method stub
        //得到一个可写的SQLiteDatabase对象
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /*更新湿度*/
        if(optionString.equals(humiString))
        {
            ContentValues values = new ContentValues();//生成ContentValues对象
            values.put(humiString,tempOrHumi);
            //第一个参数是要更新的表名
            //第二个参数是一个ContentValeus对象
            //第三个参数是where子句
            db.update(tableNameString, values, "hour=? and minute=? and second=?", new String[]{"" + hour, "" + min, "" + sec});
        }
        else if(optionString.equals(tempString))//更新温度
        {
            ContentValues values = new ContentValues();//生成ContentValues对象
            values.put(tempString,tempOrHumi);
            db.update(tableNameString, values, "hour=? and minute=? and second=?", new String[]{"" + hour, "" + min, "" + sec});
        }
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
        Cursor cursor = db.query(tableNameString, new String[]{tempString, humiString}, "hour=? and minute=? and second=?", new String[]{"" + hour, "" + min, "" + sec}, null, null, null);

        float f[] = new float[2];
        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
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

        float f[] = new float[2];
        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }
    /**
     * @Description:
     *      查询当前数据，精准到小时
     * @param hour
     * @return 查询到的温度，湿度
     * */
    public float[] queryHourToday(Context context, int hour)
    {
        float f[] = new float[2];
        System.out.println("queryHourPerDay");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();                                                                   //"hour      =  hour   and   minute        =  min"
        Cursor cursor = db.query(tableNameString, new String[]{"avg(temperature), avg(humidity)"}, null, null, "hour", hourString + "=" + hour, null);

        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }

    public void queryToday(Context context)
    {
        System.out.println("queryToday");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();                                                                   //"hour      =  hour   and   minute        =  min"
        Cursor cursor = db.rawQuery("select * from today", null);

        while(cursor.moveToNext())
        {
            System.out.println(+cursor.getInt(0)+":"+cursor.getInt(1)+":"+cursor.getInt(2)+" temp/humi:  " + cursor.getFloat(3) + "/" + cursor.getFloat(4));
        }

    }
    /**
     * =============================================================================================
     * @Description
     *  关于年月日表中各方面数据的操作：增，删，改，查
     * =============================================================================================
     * */
    public void insertAllday(Context context, int year, int month, int day, int hour, float temp, float humi){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertAllday");
        System.out.println(""+year+"/"+month+"/"+day+"/"+hour);
        if(recordExitsAllday(db, year, month, day, hour)) //已经存在则更新
        {
            updateRecordAllday(context, year, month, day, hour, temp, humi); //更新数据
        }
        else//不存在，则插入
        {
            //生成ContentValues对象
            ContentValues values = new ContentValues();
            //想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
            values.put(yearString , year);
            values.put(monthString,month);
            values.put(dayString  ,  day);
            values.put(hourString , hour);
            values.put(tempString , temp);
            values.put(humiString , humi);
            //调用insert方法，就可以将数据插入到数据库当中
            db.insert(tableAlldayString, null, values);
        }
    }

    public void insertAllday(Context context, int year, int month, int day, int hour, float tempOrHumi, String optionString){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertAllday optionString");

        if(recordExitsAllday(db, year, month, day, hour)) //已经存在则更新
        {
            updateRecordAllday(context, year, month, day, hour, tempOrHumi, optionString); //更新数据
        }
        else//不存在，则插入
        {
            /*插入湿度*/
            if(optionString.equals(humiString))
            {
                ContentValues values = new ContentValues();//生成ContentValues对象
                values.put(yearString,  year);
                values.put(monthString,month);
                values.put(dayString  ,  day);
                values.put(hourString , hour);
                values.put(humiString , tempOrHumi);
                //调用insert方法，就可以将数据插入到数据库当中
                db.insert(tableAlldayString, null, values);
            }
            else if(optionString.equals(tempString))//更新温度
            {
                ContentValues values = new ContentValues();//生成ContentValues对象
                //想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
                values.put(yearString,  year);
                values.put(monthString,month);
                values.put(dayString  ,  day);
                values.put(hourString , hour);
                values.put(tempString,tempOrHumi);
                db.insert(tableAlldayString, null, values);//将数据插入到数据库当中
            }
        }
    }

    public void updateRecordAllday(Context context, int year, int month, int day, int hour, float temp, float humi)
    {
        System.out.println("updateRecordAllday");
        // TODO Auto-generated method stub
        //得到一个可写的SQLiteDatabase对象
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(tempString,temp);
        values.put(humiString,humi);
        //第一个参数是要更新的表名
        //第二个参数是一个ContentValeus对象
        //第三个参数是where子句
        db.update(tableAlldayString, values, "year=? and month=? and day=? and hour=?", new String[]{"" + year, "" + month, "" + day, "" + hour});
    }
    public void updateRecordAllday(Context context, int year, int month, int day, int hour, float tempOrHumi, String optionString)
    {
        System.out.println("updateRecordAllday");
        //得到一个可写的SQLiteDatabase对象
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /*更新湿度*/
        if(optionString.equals(humiString))
        {
            ContentValues values = new ContentValues();//生成ContentValues对象
            values.put(humiString,tempOrHumi);
            //第一个参数是要更新的表名
            //第二个参数是一个ContentValeus对象
            //第三个参数是where子句
            db.update(tableAlldayString, values, "year=? and month=? and day=? and hour=?", new String[]{""+year, ""+month, ""+day, ""+hour});
        }
        else if(optionString.equals(tempString))//更新温度
        {
            ContentValues values = new ContentValues();//生成ContentValues对象
            values.put(tempString,tempOrHumi);
            db.update(tableAlldayString, values, "year=? and month=? and day=? and hour=?", new String[]{""+year, ""+month, ""+day, ""+hour});
        }
    }

    private boolean recordExitsAllday(SQLiteDatabase db, int year, int month, int day, int hour)
    {
        Cursor cursor = db.rawQuery("select * from allday where year=? and month=? and day=? and hour=?", new String[]{"" + year, "" + month, "" + day, "" + hour});
        if(cursor.getCount() != 0)//存在
        {
            return true;
        }
        return false; //不存在
    }

    /**
     * @Description:
     *      查询当前数据，精准到秒
     * @param:
     * @return 查询到的温度，湿度
     * */
    public float[] queryHourPerYear(Context context, int year, int month, int day, int hour)
    {
        float f[] = new float[2];
        System.out.println("queryHourPerYear");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableAlldayString, new String[]{tempString, humiString}, "year=? and month=? and day=? and hour=?", new String[]{""+year, ""+month, ""+day, ""+hour}, null, null, null);

        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }
    /**
     * @Description: queryDayPerYear
     *      查询当前数据，精准到分钟
     * @param context 传入activity.this即可，或者Service中直接传入this
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 日平均温度、湿度
     * */
    public float[]  queryDayPerYear(Context context, int year, int month, int day)
    {
        float f[] = new float[2];
        System.out.println("queryDayPerYear");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(tableAlldayString, new String[]{"avg(temperature), avg(humidity)"},  null, null,
                "year, month, day", yearString+"="+year +" and "+ monthString+"="+month +" and "+ dayString+"="+day, null);
        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }

    /**
     * @Description: queryMaxDayPerYear
     *      查询（某年某月某日）的最高温度、湿度---以day为最小单位
     * @param context 传入activity.this即可，或者Service中直接传入this
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 日最高温度、湿度
     * */
    public float[]  queryMaxDayPerYear(Context context, int year, int month, int day)
    {
        float f[] = new float[2];
        System.out.println("queryMaxDayPerYear");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(tableAlldayString, new String[]{"max(temperature), max(humidity)"},  null, null,
                "year, month, day", yearString+"="+year +" and "+ monthString+"="+month +" and "+ dayString+"="+day, null);
        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }

    /**
     * @Description: queryMaxDayPerYear
     *      查询（某年某月某日）的最低温度、湿度---以day为最小单位
     * @param context 传入activity.this即可，或者Service中直接传入this
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 日最低温度、湿度
     * */
    public float[]  queryMinDayPerYear(Context context, int year, int month, int day)
    {
        float f[] = new float[2];
        System.out.println("queryMinDayPerYear");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(tableAlldayString, new String[]{"min(temperature), min(humidity)"},  null, null,
                "year, month, day", yearString+"="+year +" and "+ monthString+"="+month +" and "+ dayString+"="+day, null);
        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }

    /**
     * @Description:
     *      查询某年某月的平均温度、湿度---以month为最小单位
     * @param context 传入activity.this即可，或者Service中直接传入this
     * @param year  年
     * @param month 月
     * @return 月平均温度，湿度
     * */
    public float[] queryMonthPerYear(Context context, int year, int month)
    {
        float f[] = new float[2];
        System.out.println("queryMonthPerYear");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(tableAlldayString, new String[]{"avg(temperature), avg(humidity)"},  null, null,
                "year, month", yearString+"="+year +" and "+ monthString+"="+month, null);
        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }

    public void queryAllday(Context context)
    {
        System.out.println("queryAllday");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from allday", null);

        while(cursor.moveToNext())
        {
            System.out.println("date:"+cursor.getInt(0)+"/"+cursor.getInt(1)+"/"+cursor.getInt(2)+" Time:"+cursor.getInt(3)+ "temp:" + cursor.getFloat(4) + " humi:" + cursor.getFloat(5));
        }

    }

    public void deleteTable(Context context)
    {
        System.out.println("delete table");
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("drop table "+tableNameString);
    }

    public void switchTodayToAllday(Context context, int start, int end)//不包含end
    {
        TodayTime nowTime = new TodayTime();
        nowTime.update();
        int year = nowTime.getYear()%100;
        int month = nowTime.getMonth();
        int day = nowTime.getDay();

        float f[] = null;
        for(int hour = start; hour < end; hour++)
        {
            f = queryHourToday(context, hour); //查询每小时的数据
            if(f != null)//已获取到temp，humi，将其放入allday表中
            {
                insertAllday(context, year, month, day, hour, f[0], f[1]);
            }
        }
    }

}
