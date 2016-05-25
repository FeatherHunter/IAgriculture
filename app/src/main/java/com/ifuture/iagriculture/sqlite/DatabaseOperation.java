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
    public String tableAreaString   = DayDatabaseHelper.tableAreaName;
    public String tableDeviceString = DayDatabaseHelper.tableDeviceName;
    public String tableGHouseString = DayDatabaseHelper.tableGHouseName;
    public String tableTermString   = DayDatabaseHelper.tableTerminalName;
    public String tableTodayString   = DayDatabaseHelper.tableTodayName;
    public String tableAlldayString = DayDatabaseHelper.tableAlldayName;
    public String deviceString      = DayDatabaseHelper.device;
    public String yearString        = DayDatabaseHelper.year;
    public String monthString       = DayDatabaseHelper.month;
    public String dayString         = DayDatabaseHelper.day;
    public String hourString        = DayDatabaseHelper.hour;
    public String minuteString      = DayDatabaseHelper.minute;
    public String secondString      = DayDatabaseHelper.second;
    public String tempString        = DayDatabaseHelper.temperature;
    public String humiString        = DayDatabaseHelper.humidity;

    public DatabaseOperation(String account)
    {
        databaseName = account;
//        databaseName = "igreen_db";
    }
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
        if(!tabbleIsExist(db, tableTodayString))//如果today表不存在
        {
            dbHelper.createTodayTable(db);
        }
        if(!tabbleIsExist(db, tableAlldayString))
        {
            dbHelper.createAlldayTable(db);
        }
        if(!tabbleIsExist(db, tableAreaString)) //地区表不存在
        {
            dbHelper.createAreaTable(db);
        }
        if(!tabbleIsExist(db, tableTermString)) //终端表不存在，创建
        {
            dbHelper.createTerminalTable(db);
        }
        if(!tabbleIsExist(db, tableGHouseString)) //不存在创建大棚表
        {
            dbHelper.createGHouseTable(db);
        }
        if(!tabbleIsExist(db, tableDeviceString)) //不存在创建设备表
        {
            dbHelper.createDeviceTable(db);
        }
    }
    /**--------------------------------------------------------------------------------------------
     * 判断某张表是否存在
     * @param tableName 表名
     * @return
     *---------------------------------------------------------------------------------------------*/
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
        db.execSQL("delete from " + tableTodayString);
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
    /**----------------------------------------
     *  清除table area的数据
     *-----------------------------------------*/
    public void clearTableArea(Context context)
    {
        System.out.println("clearTableArea");
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from " + tableAreaString);
    }

    /**----------------------------------------
     *  清除table terminal的数据
     *-----------------------------------------*/
    public void clearTableTerminal(Context context)
    {
        System.out.println("clearTableTerminal");
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from " + tableTermString);
    }

    /**----------------------------------------
     *  清除table greenhouse的数据
     *-----------------------------------------*/
    public void clearTableGHouse(Context context)
    {
        System.out.println("clearTableGHouse");
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from " + tableGHouseString);
    }
    /**----------------------------------------
     *  清除table device的数据
     *-----------------------------------------*/
    public void clearTableDevice(Context context)
    {
        System.out.println("clearTableDevice");
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from " + tableDeviceString);
    }
    /**
     * =============================================================================================
     * @Description
     *                       设备表的增删改查
     * =============================================================================================
     * */

    public void insertDevice(Context context, int area, String ghouseName, String deviceNum){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertDevice");
        if(recordExitsDevice(db, area, ghouseName, deviceNum)) //已经存在则更新
        {
            updateRecordDevice(context, area, ghouseName, deviceNum); //更新数据
        }
        else//不存在，则插入
        {
            System.out.println("insertDevice");
            ContentValues values = new ContentValues();
            values.put("area",area);
            values.put("greenhouse",ghouseName);
            values.put("device",deviceNum);
            db.insert(tableDeviceString, null, values);//调用insert方法，就可以将数据插入到数据库当中
        }
    }

    private boolean recordExitsDevice(SQLiteDatabase db, int area, String ghouseName,  String deviceNum)
    {
        Cursor cursor = db.rawQuery("select * from device where area=? and greenhouse=? and device=?", new String[]{"" + area, "" + ghouseName, "" + deviceNum});
        if(cursor.getCount() != 0)//存在
        {
            return true;
        }
        return false; //不存在
    }

    public void updateRecordDevice(Context context, int area, String ghouseName,  String deviceNum)
    {
        System.out.println("updateRecordDevice");
        // TODO Auto-generated method stub
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("area", area);
        values.put("greenhouse",ghouseName);
        values.put("device",deviceNum);
        db.update(tableDeviceString, values, "area=? and greenhouse=? and device=?", new String[]{""+area, ""+ghouseName, ""+deviceNum});
    }

    /**------------------------------------------------------
     *  @Function:    queryDevicePerGHouse
     *  @Description: 按照在地区号和大棚号下查找设备号
     *  @param areaNum 地区号
     *  @param gHouseNum 大棚号
     *  @return String[] 查询到的所有设备号
     *------------------------------------------------------*/
    public String[] queryDevicePerGHouse(Context context, int areaNum, String gHouseNum)
    {
        System.out.println("queryDevicePerGHouse");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select device from device where area=" + areaNum + " and greenhouse=" + gHouseNum, null);


        String deviceNums[] = new String[100];
        int i = 0;
        while(cursor.moveToNext())
        {
            deviceNums[i] = cursor.getString(0);
            i++;
        }
        deviceNums[i] = null;
        return deviceNums;
    }

    /**------------------------------------------------------
     *  @Function:    queryAreaGHouseByDevice
     *  @Description: 通过设备号查找其所在地区和大棚号
     *  @param deviceNum 设备号
     *  @return String[] 查询到的所有设备号
     *------------------------------------------------------*/
    public String[] queryAreaGHouseByDevice(Context context, String deviceNum)
    {
        System.out.println("queryAreaGHouseByDevice");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select area,greenhouse from device where device="+deviceNum, null);


        String areaGreenhouse[] = new String[2];
        int i = 0;
        if(cursor.moveToNext())
        {
            areaGreenhouse[0] = cursor.getString(0);
            areaGreenhouse[1] = cursor.getString(1);
            return areaGreenhouse;
        }
        return null;
    }

    /**
     * =============================================================================================
     * @Description
     *                       大棚表的增删改查
     * =============================================================================================
     * */

    public void insertGHouse(Context context, int area, String ghouseName){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertGHouse");
        if(recordExitsGHouse(db, area, ghouseName)) //已经存在则更新
        {
            updateRecordGHouse(context, area, ghouseName); //更新数据
        }
        else//不存在，则插入
        {
            System.out.println("insertGHouse");
            ContentValues values = new ContentValues();
            values.put("area",area);
            values.put("greenhouse",ghouseName);
            values.put("temp_max", 22);
            values.put("temp_min", 22);
            values.put("humi_max", 50);
            values.put("humi_min", 50);
            db.insert(tableGHouseString, null, values);//调用insert方法，就可以将数据插入到数据库当中
        }
    }

    private boolean recordExitsGHouse(SQLiteDatabase db, int area, String ghouseName)
    {
        Cursor cursor = db.rawQuery("select * from greenhouse where area=? and greenhouse=?", new String[]{"" + area, "" + ghouseName});
        if(cursor.getCount() != 0)//存在
        {
            return true;
        }
        return false; //不存在
    }

    public void updateRecordGHouse(Context context, int area, String ghouseName)
    {
        System.out.println("updateRecordGHouse");
        // TODO Auto-generated method stub
        //得到一个可写的SQLiteDatabase对象
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("area",area);
        values.put("greenhouse",ghouseName);
        db.update(tableGHouseString, values, "area=? and greenhouse=?", new String[]{"" + area, "" + ghouseName});
    }

    /**-----------------------------------------------------------------------
     *  @Function:    updateTempLimitGHouse(Context context, int area, String ghouseName, int tempMax, int tempMin)
     *  @Description: 更新大棚表中某大棚的最高温度和最低温度
     *                  用于自动控制
     *  @param area 地区号
     *  @param ghouseName 大棚号
     *  @param tempMax  最高温度
     *  @param tempMin  最低温度
     *  @return String[] 所有大棚号
     *--------------------------------------------------------------------------*/
    public void updateTempLimitGHouse(Context context, int area, String ghouseName, int tempMax, int tempMin)
    {
        System.out.println("updateTempLimitGHouse");
        // TODO Auto-generated method stub
        //得到一个可写的SQLiteDatabase对象
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("temp_max",tempMax);
        values.put("temp_min",tempMin);
        db.update(tableGHouseString, values, "area=? and greenhouse=?", new String[]{"" + area, "" + ghouseName});
    }

    /**-----------------------------------------------------------------------
     *  @Function:    updateHumiLimitGHouse(Context context, int area, String ghouseName, int humiMax, int humiMin)
     *  @Description: 更新大棚表中某大棚的最高温度和最低温度
     *                  用于自动控制
     *  @param area 地区号
     *  @param ghouseName 大棚号
     *  @param humiMax  最高温度
     *  @param humiMin  最低温度
     *  @return String[] 所有大棚号
     *--------------------------------------------------------------------------*/
    public void updateHumiLimitGHouse(Context context, int area, String ghouseName, int humiMax, int humiMin)
    {
        System.out.println("updateHumiLimitGHouse");
        // TODO Auto-generated method stub
        //得到一个可写的SQLiteDatabase对象
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("humi_max",humiMax);
        values.put("humi_min",humiMin);
        db.update(tableGHouseString, values, "area=? and greenhouse=?", new String[]{"" + area, "" + ghouseName});
    }

    /**-----------------------------------------
     *  @Function:    queryTempLimitPerGHouse(Context context, int area, String ghouseName)
     *  @Description: 查询某大棚设定温度的范围
     *  @return int[] int[0]温度上限 int[1]温度下限
     *---------------------------------------*/
    public int[] queryTempLimitPerGHouse(Context context, int areaNum, String ghouseNum)
    {
        System.out.println("queryTempLimitPerGHouse");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select temp_max,temp_min from greenhouse where area="+areaNum+" and greenhouse="+ghouseNum, null);


        int temp[] = new int[2];
        int i;
        for(i= 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            temp[i] = cursor.getInt(0);
        }
        if(i == cursor.getCount() )
            return temp;
        else
            return null;
    }

    /**-----------------------------------------
     *  @Function:    queryHumiLimitPerGHouse(Context context, int area, String ghouseName)
     *  @Description: 查询某大棚设定湿度的范围
     *  @return int[] int[0]湿度上限 int[1]湿度下限
     *---------------------------------------*/
    public int[] queryHumiLimitPerGHouse(Context context, int areaNum, String ghouseNum)
    {
        System.out.println("queryHumiLimitPerGHouse");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select humi_max,humi_min from greenhouse where area="+areaNum+" and greenhouse="+ghouseNum, null);


        int humi[] = new int[2];
        int i;
        for(i= 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            humi[i] = cursor.getInt(0);
        }
        if(i == cursor.getCount() )
            return humi;
        else
            return null;
    }

    /**-----------------------------------------
     *  @Function:    queryGHousePerArea(Context context, int areaNum)
     *  @Description: 查询大棚表中某地区号内的所有大棚号
     *  @param areaNum 需要查询的地区号
     *  @return String[] 所有大棚号
     *---------------------------------------*/
    public String[] queryGHousePerArea(Context context, int areaNum)
    {
        System.out.println("queryGHousePerArea");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select greenhouse from greenhouse where area="+areaNum, null);


        String gHouseNums[] = new String[51];
        int i = 0;
        while(cursor.moveToNext())
        {
            gHouseNums[i] = cursor.getString(0);
            i++;
        }
        gHouseNums[i] = null;
        return gHouseNums;
    }
    /**
     * =============================================================================================
     * @Description
     *                       终端表的增删改查
     * =============================================================================================
     * */

    public void insertTerminal(Context context, int area, String terminalNum){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertTerminal");
        if(recordExitsTerminal(db, area, terminalNum)) //已经存在则不作任何操作
        {
            //已经存在终端，不作任何处理
        }
        else//不存在，则插入
        {
            ContentValues values = new ContentValues();
            values.put("area",area);
            values.put("terminal",terminalNum);
            db.insert(tableTermString, null, values);//调用insert方法，就可以将数据插入到数据库当中
        }
    }

    /**
     * =============================================================================================
     * @Description
     *                       查询某地区下的终端号
     * =============================================================================================
     * */

    public String[] queryTerminalPerArea    (Context context, int areaNum){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select terminal from terminal where area="+areaNum, null);

        String terminal[] = new String[51];
        int i = 0;
        while(cursor.moveToNext())
        {
            terminal[i] = cursor.getString(0);
            i++;
        }
        terminal[i] = null;
        return terminal;
    }

    private boolean recordExitsTerminal(SQLiteDatabase db, int area, String terminalNum)
    {
        Cursor cursor = db.rawQuery("select * from terminal where area=? and terminal=?", new String[]{"" + area, "" + terminalNum});
        if(cursor.getCount() != 0)//存在
        {
            return true;
        }
        return false; //不存在
    }

    /**
     * =============================================================================================
     * @Description
     *                       地区表的增删改查
     * =============================================================================================
     * */

    public void insertArea(Context context, int area, String areaName){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertArea");
        if(recordExitsArea(db, area, areaName)) //已经存在则更新
        {
            updateRecordArea(context, area, areaName); //更新数据
        }
        else//不存在，则插入
        {
            System.out.println("insertArea");
            ContentValues values = new ContentValues();
            values.put("area",area);
            values.put("name",areaName);
            db.insert(tableAreaString, null, values);//调用insert方法，就可以将数据插入到数据库当中
        }
    }

    private boolean recordExitsArea(SQLiteDatabase db, int area, String areaName)
    {
        Cursor cursor = db.rawQuery("select * from area where area=? and name=?", new String[]{"" + area, "" + areaName});
        if(cursor.getCount() != 0)//存在
        {
            return true;
        }
        return false; //不存在
    }

    public void updateRecordArea(Context context, int area, String areaName)
    {
        System.out.println("updateRecordArea");
        // TODO Auto-generated method stub
        //得到一个可写的SQLiteDatabase对象
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("area",area);
        values.put("name",areaName);
        //第一个参数是要更新的表名
        //第二个参数是一个ContentValeus对象
        //第三个参数是where子句
        db.update(tableAreaString, values, "area=? and name=?", new String[]{"" + area, "" + areaName});
    }

    /*-----------------------------------------
     *           查询地区的所有名称
     *---------------------------------------*/
    public String[] queryAreaName(Context context)
    {
        System.out.println("queryAreaName");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select name from area", null);


        String areaNames[] = new String[51];
        int i = 0;
        while(cursor.moveToNext())
        {
            areaNames[i] = cursor.getString(0);
            i++;
        }
        areaNames[i] = null;
        return areaNames;
    }

    /*-----------------------------------------
     *           查询地区的数量
     *---------------------------------------*/
    public int queryAreaCount(Context context)
    {
        System.out.println("queryAreaCount");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select area from area", null);

        int i = 0;
        while(cursor.moveToNext())
        {
            i++;
        }
        return i;
    }

    /**
     * =============================================================================================
     * @Description
     *  关于当天各方面数据的操作：增，删，改，查
     * =============================================================================================
     * */
    public void insertToday(Context context, String deviceNum, int hour, int min, int sec, float temp, float humi){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertToday");
        if(recordExitsToday(db, deviceNum, hour, min, sec)) //已经存在则更新
        {
            updateRecordToday(context,deviceNum, hour, min, sec, temp, humi); //更新数据
        }
        else//不存在，则插入
        {
            //生成ContentValues对象
            ContentValues values = new ContentValues();
            //想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
            values.put(deviceString, deviceNum);
            values.put(hourString, hour);
            values.put(minuteString,min);
            values.put(secondString,sec);
            values.put(tempString,temp);
            values.put(humiString,humi);
            //调用insert方法，就可以将数据插入到数据库当中
            db.insert(tableTodayString, null, values);
        }
    }

    public void insertToday(Context context, String deviceNum, int hour, int min, int sec, float tempOrHumi, String optionString){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertToday optionString");
        if(recordExitsToday(db, deviceNum, hour, min, sec)) //已经存在则更新
        {
            updateRecordToday(context, deviceNum, hour, min, sec, tempOrHumi, optionString); //更新数据
        }
        else//不存在，则插入
        {
            System.out.println("insertToday optionString2");
            /*插入湿度*/
            if(optionString.equals(humiString))
            {
                System.out.println("insertToday optionString3");
                ContentValues values = new ContentValues();//生成ContentValues对象
                //想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
                values.put(deviceString, deviceNum);
                values.put(hourString, hour);
                values.put(minuteString,min);
                values.put(secondString,sec);
                values.put(humiString,tempOrHumi);
                db.insert(tableTodayString, null, values);//将数据插入到数据库当中
            }
            else if(optionString.equals(tempString))//更新温度
            {
                System.out.println("insertToday optionString4");
                ContentValues values = new ContentValues();//生成ContentValues对象
                //想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
                values.put(deviceString, deviceNum);
                values.put(hourString, hour);
                values.put(minuteString,min);
                values.put(secondString,sec);
                values.put(tempString,tempOrHumi);
                db.insert(tableTodayString, null, values);//将数据插入到数据库当中

            }
        }
    }

    private boolean recordExitsToday(SQLiteDatabase db, String deviceNum, int hour, int min, int sec)
    {
        Cursor cursor = db.rawQuery("select * from today where device=? and hour=? and minute=? and second=?", new String[]{deviceNum, "" + hour, "" + min, "" + sec});
        if(cursor.getCount() != 0)//存在
        {
            return true;
        }
        return false; //不存在
    }

    public void updateRecordToday(Context context, String deviceNum, int hour, int min, int sec, float temp, float humi)
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
        db.update(tableTodayString, values, "device=? and hour=? and minute=? and second=?", new String[]{deviceNum, "" + hour, "" + min, "" + sec});
    }
    public void updateRecordToday(Context context,  String deviceNum, int hour, int min, int sec,  float tempOrHumi, String optionString)
    {
        System.out.println("updateRecordToday optionString");
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
            db.update(tableTodayString, values, "device=? and hour=? and minute=? and second=?", new String[]{deviceNum, "" + hour, "" + min, "" + sec});
        }
        else if(optionString.equals(tempString))//更新温度
        {
            ContentValues values = new ContentValues();//生成ContentValues对象
            values.put(tempString,tempOrHumi);
            db.update(tableTodayString, values, "device=? and hour=? and minute=? and second=?", new String[]{deviceNum, "" + hour, "" + min, "" + sec});
        }
    }
    /**
     * @Description:
     *      查询当前数据，精准到秒
     * @param hour,min,sec
     * @return 查询到的温度，湿度
     * */
    public float[] querySecToday(Context context, String deviceNum, int hour, int min, int sec)
    {
        System.out.println("querySecPerDay");

        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableTodayString, new String[]{tempString, humiString}, "device=? and hour=? and minute=? and second=?",
                new String[]{deviceNum, "" + hour, "" + min, "" + sec}, null, null, null);

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
    public float[] queryMinuteToday(Context context, String deviceNum, int hour, int min)
    {
        System.out.println("queryMinutePerDay");

        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();                                                                   //"hour      =  hour   and   minute        =  min"
        Cursor cursor = db.query(tableTodayString, new String[]{"avg(temperature), avg(humidity)"}, null, null,
                "device, hour, minute", deviceString+"="+deviceNum+ " and " +hourString+"="+hour+ " and " + minuteString+"="+min, null);

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
    public float[] queryHourToday(Context context, String deviceNum, int hour)
    {
        float f[] = new float[2];
        //System.out.println("queryHourPerDay");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();                                                                   //"hour      =  hour   and   minute        =  min"
        Cursor cursor = db.query(tableTodayString, new String[]{"avg(temperature), avg(humidity)"}, null, null,
                "device, hour", deviceString+"="+deviceNum+ " and " +hourString+"="+hour, null);

        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }

    /**------------------------------------------------------------------
     * @Description:
     *      按地区号查询某小时的温度、湿度是多少
     * @param hour
     * @return 查询到的温度，湿度
     *------------------------------------------------------------------*/
    public float[] queryHourTodayByArea(Context context, String areaNum, int hour)
    {
        float f[] = new float[2];
        System.out.println("queryHourTodayByArea");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();                                                                   //"hour      =  hour   and   minute        =  min"
        Cursor cursor = db.rawQuery("select temperature from "+tableTodayString+
                " where device in (select device from device where area="+areaNum+") and hour="+hour, null);
        if(cursor.getCount() == 0) return null;//检测是否有数据，没有数据直接返回null，防止求avg得到0值

        cursor = db.rawQuery("select avg(temperature), avg(humidity) from "+tableTodayString+
                " where device in (select device from device where area="+areaNum+") and hour="+hour, null);
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }

    /**------------------------------------------------------------------
     * @Function: queryHourTodayByGHouse
     * @Description:
     *      按地区号、大棚号联合查询某小时的温度、湿度是多少
     * @param hour
     * @return 查询到的温度，湿度
     *------------------------------------------------------------------*/
    public float[] queryHourTodayByGHouse(Context context, String areaNum, String greenhouseNum,int hour)
    {
        float f[] = new float[2];
        System.out.println("queryHourTodayByGHouse");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();                                                                   //"hour      =  hour   and   minute        =  min"
        Cursor cursor = db.rawQuery("select temperature from "+tableTodayString+
                " where device in (select device from device where area="+areaNum+" and greenhouse="+greenhouseNum+") and hour="+hour, null);
        if(cursor.getCount() == 0) return null;//检测是否有数据，没有数据直接返回null，防止求avg得到0值

        cursor = db.rawQuery("select avg(temperature), avg(humidity) from "+tableTodayString+
                " where device in (select device from device where area="+areaNum+" and greenhouse="+greenhouseNum+") and hour="+hour, null);
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
            System.out.println(cursor.getString(0)+cursor.getInt(1)+":"+cursor.getInt(2)+":"+cursor.getInt(3)+" temp/humi:  " + cursor.getFloat(4) + "/" + cursor.getFloat(5));
        }

    }
    /**
     * =============================================================================================
     * @Description
     *  关于年月日表中各方面数据的操作：增，删，改，查
     * =============================================================================================
     * */
    public void insertAllday(Context context, String deviceNum, int year, int month, int day, int hour, float temp, float humi){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertAllday");
        System.out.println(""+year+"/"+month+"/"+day+"/"+hour);
        if(recordExitsAllday(db, deviceNum, year, month, day, hour)) //已经存在则更新
        {
            updateRecordAllday(context, deviceNum, year, month, day, hour, temp, humi); //更新数据
        }
        else//不存在，则插入
        {
            //生成ContentValues对象
            ContentValues values = new ContentValues();
            //想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
            values.put(deviceString, deviceNum);
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

    public void insertAllday(Context context, String deviceNum, int year, int month, int day, int hour, float tempOrHumi, String optionString){
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println("insertAllday optionString");

        if(recordExitsAllday(db, deviceNum, year, month, day, hour)) //已经存在则更新
        {
            updateRecordAllday(context, deviceNum, year, month, day, hour, tempOrHumi, optionString); //更新数据
        }
        else//不存在，则插入
        {
            /*插入湿度*/
            if(optionString.equals(humiString))
            {
                ContentValues values = new ContentValues();//生成ContentValues对象
                values.put(deviceString, deviceNum);
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
                values.put(deviceString, deviceNum);
                values.put(yearString,  year);
                values.put(monthString,month);
                values.put(dayString  ,  day);
                values.put(hourString , hour);
                values.put(tempString,tempOrHumi);
                db.insert(tableAlldayString, null, values);//将数据插入到数据库当中
            }
        }
    }

    public void updateRecordAllday(Context context, String deviceNum, int year, int month, int day, int hour, float temp, float humi)
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
        db.update(tableAlldayString, values, "device=? and year=? and month=? and day=? and hour=?",
                new String[]{deviceNum, ""+year, ""+month, ""+day, ""+hour});
    }
    public void updateRecordAllday(Context context, String deviceNum, int year, int month, int day, int hour, float tempOrHumi, String optionString)
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
            db.update(tableAlldayString, values, "device=? and year=? and month=? and day=? and hour=?",
                    new String[]{deviceNum, ""+year, ""+month, ""+day, ""+hour});
        }
        else if(optionString.equals(tempString))//更新温度
        {
            ContentValues values = new ContentValues();//生成ContentValues对象
            values.put(tempString,tempOrHumi);
            db.update(tableAlldayString, values, "device=? and year=? and month=? and day=? and hour=?",
                    new String[]{deviceNum, ""+year, ""+month, ""+day, ""+hour});
        }
    }

    private boolean recordExitsAllday(SQLiteDatabase db, String deviceNum, int year, int month, int day, int hour)
    {
        Cursor cursor = db.rawQuery("select * from allday where device=? and year=? and month=? and day=? and hour=?",
                new String[]{deviceNum, ""+year, ""+month, ""+day, ""+hour});
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
    public float[] queryHourPerYear(Context context, String deviceNum, int year, int month, int day, int hour)
    {
        float f[] = new float[2];
        System.out.println("queryHourPerYear");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableAlldayString, new String[]{tempString, humiString},
                "device=? and year=? and month=? and day=? and hour=?", new String[]{deviceNum, ""+year, ""+month, ""+day, ""+hour}, null, null, null);

        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }

    /**------------------------------------------------------------------
     * @Description:
     *      按地区号查询年表中某天的某小时温湿度是多少
     * @param hour
     * @return 查询到的温度，湿度
     *------------------------------------------------------------------*/
    public float[] queryHourPerYearByGHouse(Context context, String areaNum, String greenhouseNum,int year, int month, int day, int hour)
    {
        float f[] = new float[2];
        System.out.println("queryHourPerYearByArea");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //"hour      =  hour   and   minute        =  min"
        Cursor cursor = db.rawQuery("select avg(temperature), avg(humidity) from "+tableAlldayString+
                " where device in (select device from device where area="+areaNum+" and greenhouse="+greenhouseNum+") " +
                "and hour="+hour+
                "and year="+year+
                "and month="+month+
                "and day="+day, null);
        if(cursor.getCount() == 0) return null;//检测是否有数据，没有数据直接返回null，防止求avg得到0值

        cursor = db.rawQuery("select avg(temperature), avg(humidity) from "+tableAlldayString+
                " where device in (select device from device where area="+areaNum+" and greenhouse="+greenhouseNum+") " +
                "and hour="+hour+
                "and year="+year+
                "and month="+month+
                "and day="+day, null);

        if(cursor.getCount() == 0) return null;//没有查询到，返回null
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }

    /**------------------------------------------------------------------
     * @Description:
     *      按"地区号"查询年表中"某天的某小时温湿度"是多少
     * @param hour
     * @return 查询到的温度，湿度
     *------------------------------------------------------------------*/
    public float[] queryHourPerYearByArea(Context context, String areaNum, int year, int month, int day, int hour)
    {
        float f[] = new float[2];
        System.out.println("queryHourPerYearByArea");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select temperature, humidity from "+tableAlldayString+
                " where device in (select device from device where area="+areaNum+") " +
                " and hour="+hour+
                " and year="+year+
                " and month="+month+
                " and day="+day, null);
        if(cursor.getCount() == 0) return null;//检测是否有数据，没有数据直接返回null，防止求avg得到0值

        cursor = db.rawQuery("select avg(temperature), avg(humidity) from "+tableAlldayString+
                " where device in (select device from device where area="+areaNum+") " +
                " and hour="+hour+
                " and year="+year+
                " and month="+month+
                " and day="+day, null);

        if(cursor.getCount() == 0) return null;//没有查询到，返回null
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
    public float[]  queryDayPerYear(Context context, String deviceNum, int year, int month, int day)
    {
        float f[] = new float[2];
        System.out.println("queryDayPerYear");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(tableAlldayString, new String[]{"avg(temperature), avg(humidity)"},  null, null,
                "device, year, month, day", deviceString+"="+deviceNum+" and "+yearString+"="+year +" and "+ monthString+"="+month +" and "+ dayString+"="+day, null);
        if(cursor.getCount() == 0) return null;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            f[i] = cursor.getFloat(0);
        }
        System.out.println("temp:" + f[0] + " humi:" + f[1]);
        return f;
    }

    /**------------------------------------------------------------------
     * @Description:
     *      按“地区号”查询年表中某天的“平均温湿度“是多少
     * @param day
     * @return 查询到的温度，湿度
     *------------------------------------------------------------------*/
    public float[] queryDayPerYearByArea(Context context, String areaNum, int year, int month, int day)
    {
        float f[] = new float[2];
        System.out.println("queryDayPerYearByArea");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select temperature, humidity from "+tableAlldayString+
                " where device in (select device from device where area="+areaNum+") " +
                " and year="+year+
                " and month="+month+
                " and day="+day, null);
        if(cursor.getCount() == 0) return null;//检测是否有数据，没有数据直接返回null，防止求avg得到0值

        cursor = db.rawQuery("select avg(temperature), avg(humidity) from "+tableAlldayString+
                " where device in (select device from device where area="+areaNum+") " +
                " and year="+year+
                " and month="+month+
                " and day="+day, null);

        if(cursor.getCount() == 0) return null;//没有查询到，返回null
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
    public float[]  queryMaxDayPerYear(Context context, String deviceNum, int year, int month, int day)
    {
        float f[] = new float[2];
        System.out.println("queryMaxDayPerYear");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(tableAlldayString, new String[]{"max(temperature), max(humidity)"},  null, null,
                "device, year, month, day", deviceString+"="+deviceNum+" and "+yearString+"="+year +" and "+ monthString+"="+month +" and "+ dayString+"="+day, null);
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
    public float[]  queryMinDayPerYear(Context context, String deviceNum, int year, int month, int day)
    {
        float f[] = new float[2];
        System.out.println("queryMinDayPerYear");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(tableAlldayString, new String[]{"min(temperature), min(humidity)"},  null, null,
                "device, year, month, day", deviceString+"="+deviceNum+" and "+yearString+"="+year +" and "+ monthString+"="+month +" and "+ dayString+"="+day, null);
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
    public float[] queryMonthPerYear(Context context, String deviceNum, int year, int month)
    {
        float f[] = new float[2];
        System.out.println("queryMonthPerYear");
        dbHelper = new DayDatabaseHelper(context, databaseName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(tableAlldayString, new String[]{"avg(temperature), avg(humidity)"},  null, null,
                "device, year, month", deviceString+"="+deviceNum+" and "+yearString+"="+year +" and "+ monthString+"="+month, null);
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
            System.out.println("device:"+cursor.getString(0)+"date:"+cursor.getInt(1)+"/"+cursor.getInt(2)+"/"+cursor.getInt(3)
                    +" Time:"+cursor.getInt(4)+ "temp:" + cursor.getFloat(5) + " humi:" + cursor.getFloat(6));
        }

    }

    public void deleteTable(Context context)
    {
        System.out.println("delete table");
        dbHelper = new DayDatabaseHelper(context,databaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("drop table "+tableTodayString);
    }

    public void switchTodayToAllday(Context context, String deviceNum, int start, int end)//不包含end
    {
        TodayTime nowTime = new TodayTime();
        nowTime.update();
        int year = nowTime.getYear()%100;
        int month = nowTime.getMonth();
        int day = nowTime.getDay();

        float f[] = null;
        for(int hour = start; hour < end; hour++)
        {
            f = queryHourToday(context, deviceNum, hour); //查询每小时的数据
            if(f != null)//已获取到temp，humi，将其放入allday表中
            {
                insertAllday(context, deviceNum, year, month, day, hour, f[0], f[1]);
            }
        }
    }

}
