package com.ifuture.iagriculture.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ifuture.iagriculture.LineChartShow;
import com.ifuture.iagriculture.R;
import com.ifuture.iagriculture.activity.ClientMainActivity;
import com.ifuture.iagriculture.bottombar.BaseFragment;

import java.io.File;
import java.util.ArrayList;

public class FragmentToalData extends BaseFragment{

	ClientMainActivity mainActivity;

	private RecvReceiver recvReceiver;
	private String RECV_ACTION = "android.intent.action.ANSWER";
	TextView tempCATextview;//C当前温度 for air空气
	TextView tempCGTextview;//C当前温度 for air
	TextView humiCATextview;//C当前湿度 for ground 土壤
	TextView humiCGTextview;//C当前湿度 for ground

	private LineChart tempLineChart;
	private LineChart humiLineChart;
	private PieChart nutritionChart;
	boolean isDayButton = true;

	float today[]     = {5.1f, 4.0f, 3.6f, 4.3f, 5f, 5.9f, 8f,   9.3f,  10.7f, 13.8f, 14.7f, 15.1f, 16.9f, 17.5f, 16.7f, 15.1f, 13.0f, 11.0f, 10.5f, 8.9f, 7.3f, 6.8f, 6.1f, 5.9f};
	float yesterday[] = {7.3f, 5.2f, 4.3f, 5.8f, 6f, 7.2f, 9.7f, 11.3f, 11.5f, 15.3f, 16.7f, 17.1f, 18.9f, 18.8f, 18.7f, 17.1f, 16.0f, 15.0f,  8.5f, 7.2f, 5.3f, 4.8f, 3.7f, 2.4f};
	float weekMax[] = {18f, 19.5f, 21.3f, 17.1f, 16f, 15.8f, 19.9f};
	float weekAvg[] = {12.4f, 15.3f, 13.5f, 11.8f, 13.7f, 9.6f, 13.1f};
	float weekMin[] = {3.4f, 5.3f, 2.1f, 6.7f, 2.9f, 2.3f, 6.3f};

	float todayGHumi[]  = {41f, 41.3f, 42f, 42f, 43.3f, 43.1f, 43.3f, 44f,  44.7f, 44.5f, 44.2f, 44.9f, 44.8f, 44.5f, 44.7f, 44.1f, 45f, 45.0f, 45.5f, 45f, 45.7f, 45.3f, 45f, 45.5f};
	float ydayGHumi[]   = {40f, 40.3f, 41f, 42f, 40.3f, 41.1f, 40.3f, 42f,  41.7f, 41.5f, 41.2f, 40.9f, 40.8f, 40.5f, 41.7f, 40.1f, 40f, 41.0f, 42.5f, 43f, 42.7f, 41.3f, 41f, 41.5f};
	float todayAHumi[]  = {31f, 31.3f, 32f, 32f, 33.3f, 33.1f, 33.3f, 34f,  34.7f, 34.5f, 34.2f, 34.9f, 34.8f, 34.5f, 34.7f, 34.1f, 35f, 35.0f, 35.5f, 34f, 35.7f, 35f, 35f, 35.9f};
	float ydayAHumi[]   = {30f, 30.3f, 31f, 32f, 30.3f, 31.1f, 30.3f, 32f,  31.7f, 31.5f, 31.2f, 30.9f, 30.8f, 30.5f, 31.7f, 30.1f, 30f, 31.0f, 35.5f, 35f, 35f, 35.3f, 34f, 35.5f};

//    float weekMaxHumi[] = {18f, 19.5f, 21.3f, 17.1f, 16f, 15.8f, 19.9f};
//    float weekAvgHumi[] = {12.4f, 15.3f, 13.5f, 11.8f, 13.7f, 9.6f, 13.1f};
//    float weekMinHumi[] = {3.4f, 5.3f, 2.1f, 6.7f, 2.9f, 2.3f, 6.3f};

	Button tempDayButton = null; //当日温度的按钮
	Button tempWeekButton = null;//一周温度的按钮

	int darkred;
	int slateblue;
	int sienna;
	int mediumvioletred;
	int white;
	int black;
	int limegreen;
	int dodgerblue;
	int violet;
	int lightcoral;
	int orange;
	int gold;
	int deepskyblue;
	int indigo;
	int palegreen;
	int rosybrown;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.totaldata_fragment, container, false);
	}
	
	

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		/*要在onCreateView之后得到空间才是有效的*/
		darkred         = ContextCompat.getColor(getActivity(), R.color.darkred);
		slateblue       = ContextCompat.getColor(getActivity(), R.color.slateblue);
		sienna          = ContextCompat.getColor(getActivity(), R.color.sienna);
		mediumvioletred = ContextCompat.getColor(getActivity(), R.color.mediumvioletred);
		white           = ContextCompat.getColor(getActivity(), R.color.white);
		black           = ContextCompat.getColor(getActivity(), R.color.black);
		limegreen       = ContextCompat.getColor(getActivity(), R.color.limegreen);
		dodgerblue      = ContextCompat.getColor(getActivity(), R.color.dodgerblue);
		violet          = ContextCompat.getColor(getActivity(), R.color.violet);
		lightcoral      = ContextCompat.getColor(getActivity(), R.color.lightcoral);
		orange          = ContextCompat.getColor(getActivity(), R.color.orange);
		gold            = ContextCompat.getColor(getActivity(), R.color.gold);
		deepskyblue     = ContextCompat.getColor(getActivity(), R.color.deepskyblue);
		indigo          = ContextCompat.getColor(getActivity(), R.color.indigo);
		palegreen       = ContextCompat.getColor(getActivity(), R.color.palegreen);
		rosybrown       = ContextCompat.getColor(getActivity(), R.color.rosybrown);

		//tempCATextview = (TextView) getActivity().findViewById(R.id.td_fragment_catemp);//C当前温度 for air空气
		tempCGTextview = (TextView) getActivity().findViewById(R.id.td_fragment_cahumi);//C当前温度 for air
		//humiCATextview = (TextView) getActivity().findViewById(R.id.td_fragment_cgtemp);//C当前湿度 for ground 土壤
		humiCGTextview = (TextView) getActivity().findViewById(R.id.td_fragment_cghumi);//C当前湿度 for ground

		tempLineChart  = (LineChart) getActivity().findViewById(R.id.temp_line_chart);
		humiLineChart  = (LineChart) getActivity().findViewById(R.id.humi_line_chart);
		nutritionChart = (PieChart) getActivity().findViewById(R.id.nutrition_pie_chart); //获取营养物质饼状图
		//      mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");
		showDayTemp(); //显示温度
		showDayHumi(); //显示湿度
		showNutrition(); //显示营养成分
		tempDayButton = (Button)getActivity().findViewById(R.id.button_statics_temp_day);  //获取当日温度的控件
		tempWeekButton = (Button)getActivity().findViewById(R.id.button_statics_temp_week);//获取本周温度的控件

		tempDayButton.setOnClickListener(new tempDayOrWeekButtonListener());
		tempWeekButton.setOnClickListener(new tempDayOrWeekButtonListener());

		/* -------------------------------------------------------
		 *  动态注册receiver
		 * -------------------------------------------------------*/
		try {
			recvReceiver = new RecvReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(RECV_ACTION);
			getActivity().registerReceiver(recvReceiver, filter);//注册
		} catch (IllegalArgumentException  e) {
			// TODO: handle exception
			System.out.println("fragmentIHome registerReceiver");
		}


	}

	class tempDayOrWeekButtonListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.button_statics_temp_day)  //当前为day按键
			{
				if(!isDayButton)//当前为一周温度，需要切换为当日温度
				{
					tempDayButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
					tempDayButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.dodgerblue));

					tempWeekButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
					tempWeekButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));

					showDayTemp();
					isDayButton = true;
				}

			}
			else if(v.getId() == R.id.button_statics_temp_week) //当前为week按键
			{
				if(isDayButton)//当前为当日温度
				{
					tempWeekButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
					tempWeekButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.dodgerblue));

					tempDayButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
					tempDayButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));

					showWeekTemp();
					isDayButton = false;
				}

			}
		}//end of onClick
	}
	private void showNutrition()
	{
		PieData mPieData = getNutPieData(4, 100, "all");
		showNutChart(nutritionChart, mPieData);
	}
	private void showDayHumi()
	{
		LineData humiLineData = getHumiDayLineData(24);
		LineChartShow lineChartShow = new LineChartShow(2000, "今日湿度");
		showChart(humiLineChart, humiLineData, ContextCompat.getColor(getActivity(), R.color.whitesmoke), lineChartShow);
	}
	private void showDayTemp()
	{
		LineData mLineData = getLineData(24);
		LineChartShow lineChartShow = new LineChartShow(2000, "今日温度");
		showChart(tempLineChart, mLineData, ContextCompat.getColor(getActivity(), R.color.whitesmoke), lineChartShow);
	}
	private void showWeekTemp()
	{
		LineData weekLineData = getWeekLineData();
		LineChartShow lineChartShow = new LineChartShow(1000, "一周温度");
		showChart(tempLineChart, weekLineData, ContextCompat.getColor(getActivity(), R.color.whitesmoke), lineChartShow);
	}
	// 设置显示的样式
	private void showChart(LineChart lineChart, LineData lineData, int color, LineChartShow lineChartShow) {
		lineChart.setDrawBorders(false);  //是否在折线图上添加边框
		// no description text
		lineChart.setDescription(lineChartShow.descriptionString);// 数据描述
		// 如果没有数据的时候，会显示这个，类似listview的emtpyview
		lineChart.setNoDataTextDescription("You need to provide data for the chart.");

		// enable / disable grid background
		lineChart.setDrawGridBackground(false); // 是否显示表格颜色
		lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

		// enable touch gestures
		lineChart.setTouchEnabled(true); // 设置是否可以触摸
		// enable scaling and dragging
		lineChart.setDragEnabled(true);// 是否可以拖拽
		lineChart.setScaleEnabled(false);// 是否可以缩放
		// if disabled, scaling can be done on x- and y-axis separately
		lineChart.setPinchZoom(false);//
		lineChart.setBackgroundColor(color);// 设置背景
		// add data
		lineChart.setData(lineData); // 设置数据

		// get the legend (only possible after setting data)
		Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的

		// modify the legend ...
		// mLegend.setPosition(Le gendPosition.LEFT_OF_CHART);
		mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
		mLegend.setFormSize(6f);// 字体
		mLegend.setTextColor(Color.BLACK);// 颜色
		// mLegend.setTypeface(mTf);// 字体

		lineChart.animateX(lineChartShow.animateXTime); // 立即执行的动画,x轴
	}

	/**
	 * 生成一个数据
	 * @param count 表示图表中有多少个坐标点
	 * @return
	 */
	private LineData getHumiDayLineData(int count) {

		ArrayList<String> xValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			// x轴显示的数据，这里默认使用数字下标显示
			xValues.add("" + i +":00");
		}

		// y轴的数据
		ArrayList<Entry> yTodayGValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			float value = (float)todayGHumi[i];
			yTodayGValues.add(new Entry(value, i));
		}

		ArrayList<Entry> yYesterdayGValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			float value = (float)ydayGHumi[i];
			yYesterdayGValues.add(new Entry(value, i));
		}

        /*空气湿度*/
		ArrayList<Entry> yTodayAValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			float value = (float)todayAHumi[i];
			yTodayAValues.add(new Entry(value, i));
		}

		ArrayList<Entry> yYdayAValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			float value = (float)ydayAHumi[i];
			yYdayAValues.add(new Entry(value, i));
		}

		// create a dataset and give it a type
		// y轴的数据集合
		LineDataSet todayGHumiDataSet = new LineDataSet(yTodayGValues, "今天土壤湿度" /*显示在比例图上*/);
		LineDataSet ydayGHumiDataSet = new LineDataSet(yYesterdayGValues, "昨天土壤湿度" /*显示在比例图上*/);
		LineDataSet todayAHumiDataSet = new LineDataSet(yTodayAValues, "今天空气湿度" /*显示在比例图上*/);
		LineDataSet ydayAHumiDataSet = new LineDataSet(yYdayAValues, "昨天空气湿度" /*显示在比例图上*/);
		// mLineDataSet.setFillAlpha(110);
		// mLineDataSet.setFillColor(Color.RED);

		int todayGHumiColor = ContextCompat.getColor(getActivity(), R.color.darkslategray);
		int ydayGHumiColor = ContextCompat.getColor(getActivity(), R.color.darkseagreen);
		int todayAHumiColor = ContextCompat.getColor(getActivity(), R.color.dodgerblue);
		int ydayAHumiColor = ContextCompat.getColor(getActivity(), R.color.lightslategray);
		//用y轴的集合来设置参数
		todayGHumiDataSet.setLineWidth(1.75f); // 线宽
		todayGHumiDataSet.setCircleSize(3f);// 显示的圆形大小
		todayGHumiDataSet.setDrawCubic(true); //平滑
		todayGHumiDataSet.setColor(todayGHumiColor);// 显示颜色
		todayGHumiDataSet.setCircleColor(todayGHumiColor);// 圆形的颜色
		todayGHumiDataSet.setHighLightColor(todayGHumiColor); // 高亮的线的颜色

		ydayGHumiDataSet.setLineWidth(1.75f); // 线宽
		ydayGHumiDataSet.setCircleSize(3f);// 显示的圆形大小
		ydayGHumiDataSet.setDrawCubic(true); //平滑
		ydayGHumiDataSet.setColor(ydayGHumiColor);// 显示颜色
		ydayGHumiDataSet.setCircleColor(ydayGHumiColor);// 圆形的颜色
		ydayGHumiDataSet.setHighLightColor(ydayGHumiColor); // 高亮的线的颜色

        /*设置空气湿度*/
		todayAHumiDataSet.setLineWidth(1.75f); // 线宽
		todayAHumiDataSet.setCircleSize(3f);// 显示的圆形大小
		todayAHumiDataSet.setDrawCubic(true); //平滑
		todayAHumiDataSet.setColor(todayAHumiColor);// 显示颜色
		todayAHumiDataSet.setCircleColor(todayAHumiColor);// 圆形的颜色
		todayAHumiDataSet.setHighLightColor(todayAHumiColor); // 高亮的线的颜色

		ydayAHumiDataSet.setLineWidth(1.75f); // 线宽
		ydayAHumiDataSet.setCircleSize(3f);// 显示的圆形大小
		ydayAHumiDataSet.setDrawCubic(true); //平滑
		ydayAHumiDataSet.setColor(ydayAHumiColor);// 显示颜色
		ydayAHumiDataSet.setCircleColor(ydayAHumiColor);// 圆形的颜色
		ydayAHumiDataSet.setHighLightColor(ydayAHumiColor); // 高亮的线的颜色

		ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();

		lineDataSets.add(todayGHumiDataSet); //添加土壤湿度DataSet
		//lineDataSets.add(ydayGHumiDataSet); //添加DataSet

		lineDataSets.add(todayAHumiDataSet); //添加土壤湿度DataSet
		//lineDataSets.add(ydayAHumiDataSet); //添加DataSet

		LineData lineData = new LineData(xValues, lineDataSets);  //使用ArrayList设置生成数据。

		return lineData;
	}


	/**
	 * 生成一个数据
	 * @param count 表示图表中有多少个坐标点
	 * @param
	 * @return
	 */
	private LineData getLineData(int count) {

		ArrayList<String> xValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			// x轴显示的数据，这里默认使用数字下标显示
			xValues.add("" + i +":00");
		}

		// y轴的数据
		ArrayList<Entry> yTodayValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			float value = (float)today[i];
			yTodayValues.add(new Entry(value, i));
		}

		ArrayList<Entry> yYesterdayValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			float value = (float)yesterday[i];
			yYesterdayValues.add(new Entry(value, i));
		}

		// create a dataset and give it a type
		// y轴的数据集合
		LineDataSet todayLineDataSet = new LineDataSet(yTodayValues, "今天" /*显示在比例图上*/);
		LineDataSet yesterdayLineDataSet = new LineDataSet(yYesterdayValues, "昨天" /*显示在比例图上*/);
		// mLineDataSet.setFillAlpha(110);
		// mLineDataSet.setFillColor(Color.RED);

		int todayLineDataColor = ContextCompat.getColor(getActivity(), R.color.dodgerblue);
		int yesterdayLineDataColor = ContextCompat.getColor(getActivity(), R.color.lightskyblue);
		//用y轴的集合来设置参数
		todayLineDataSet.setLineWidth(1.75f); // 线宽
		todayLineDataSet.setCircleSize(3f);// 显示的圆形大小
		todayLineDataSet.setDrawCubic(true); //平滑
		todayLineDataSet.setColor(todayLineDataColor);// 显示颜色
		todayLineDataSet.setCircleColor(todayLineDataColor);// 圆形的颜色
		todayLineDataSet.setHighLightColor(todayLineDataColor); // 高亮的线的颜色

		yesterdayLineDataSet.setLineWidth(1.75f); // 线宽
		yesterdayLineDataSet.setCircleSize(3f);// 显示的圆形大小
		yesterdayLineDataSet.setDrawCubic(true); //平滑
		yesterdayLineDataSet.setColor(yesterdayLineDataColor);// 显示颜色
		yesterdayLineDataSet.setCircleColor(yesterdayLineDataColor);// 圆形的颜色
		yesterdayLineDataSet.setHighLightColor(yesterdayLineDataColor); // 高亮的线的颜色

		ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();

		lineDataSets.add(todayLineDataSet); //添加DataSet
		lineDataSets.add(yesterdayLineDataSet); //添加DataSet

		LineData lineData = new LineData(xValues, lineDataSets);  //使用ArrayList设置生成数据。

		return lineData;
	}

	/**
	 * 生成一个数据
	 * @param
	 * @param
	 * @return
	 */
	private LineData getWeekLineData() {

		int count = 7;
		ArrayList<String> xValues = new ArrayList<>();
//        for (int i = 1; i <= count; i++) {
//            // x轴显示的数据，这里默认使用数字下标显示
//            xValues.add("" + i +":00");
//        }
		xValues.add("一");
		xValues.add("二");
		xValues.add("三");
		xValues.add("四");
		xValues.add("五");
		xValues.add("六");
		xValues.add("日");

		// y轴的数据
		ArrayList<Entry> yWeekMaxValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			float value = (float)weekMax[i];
			yWeekMaxValues.add(new Entry(value, i));
		}

		ArrayList<Entry> yWeekAvgValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			float value = (float)weekAvg[i];
			yWeekAvgValues.add(new Entry(value, i));
		}

		ArrayList<Entry> yWeekMinValues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			float value = (float)weekMin[i];
			yWeekMinValues.add(new Entry(value, i));
		}

		// create a dataset and give it a type
		// y轴的数据集合
		LineDataSet maxLineDataSet = new LineDataSet(yWeekMaxValues, "最高温" /*显示在比例图上*/);
		LineDataSet avgLineDataSet = new LineDataSet(yWeekAvgValues, "最低温" /*显示在比例图上*/);
		LineDataSet minLineDataSet = new LineDataSet(yWeekMinValues, "平均值" /*显示在比例图上*/);
		// mLineDataSet.setFillAlpha(110);
		// mLineDataSet.setFillColor(Color.RED);

		//用y轴的集合来设置参数
		maxLineDataSet.setLineWidth(1.75f); // 线宽
		maxLineDataSet.setCircleSize(3f);// 显示的圆形大小
		maxLineDataSet.setDrawCubic(true); //平滑
		maxLineDataSet.setColor(ContextCompat.getColor(getActivity(), R.color.lightcoral));// 显示颜色
		maxLineDataSet.setCircleColor(ContextCompat.getColor(getActivity(), R.color.lightcoral));// 圆形的颜色
		maxLineDataSet.setHighLightColor(ContextCompat.getColor(getActivity(), R.color.lightcoral)); // 高亮的线的颜色

		avgLineDataSet.setLineWidth(1.75f); // 线宽
		avgLineDataSet.setCircleSize(3f);// 显示的圆形大小
		avgLineDataSet.setDrawCubic(true); //平滑
		avgLineDataSet.setColor(ContextCompat.getColor(getActivity(), R.color.dodgerblue));// 显示颜色
		avgLineDataSet.setCircleColor(ContextCompat.getColor(getActivity(), R.color.dodgerblue));// 圆形的颜色
		avgLineDataSet.setHighLightColor(ContextCompat.getColor(getActivity(), R.color.dodgerblue)); // 高亮的线的颜色

		minLineDataSet.setLineWidth(1.75f); // 线宽
		minLineDataSet.setCircleSize(3f);// 显示的圆形大小
		minLineDataSet.setDrawCubic(true); //平滑
		minLineDataSet.setColor(ContextCompat.getColor(getActivity(), R.color.cadetblue));// 显示颜色
		minLineDataSet.setCircleColor(ContextCompat.getColor(getActivity(), R.color.cadetblue));// 圆形的颜色
		minLineDataSet.setHighLightColor(ContextCompat.getColor(getActivity(), R.color.cadetblue)); // 高亮的线的颜色

		ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();

		lineDataSets.add(maxLineDataSet); //添加DataSet
		lineDataSets.add(avgLineDataSet); //添加DataSet
		lineDataSets.add(minLineDataSet); //添加DataSet

		LineData lineData = new LineData(xValues, lineDataSets);  //使用ArrayList设置生成数据。

		return lineData;
	}



//            mChart = (PieChart) findViewById(R.id.spread_pie_chart);
//            PieData mPieData = getPieData(4, 100);
//            showChart(mChart, mPieData);


	private void showNutChart(PieChart pieChart, PieData pieData) {
		//pieChart.setHoleColorTransparent(true);

		pieChart.setHoleRadius(60f);  //半径
		pieChart.setTransparentCircleRadius(0f); // 半透明圈
		pieChart.setHoleRadius(30f);  //实心圆

		pieChart.setDescription("营养元素");

		// mChart.setDrawYValues(true);
		pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字

		pieChart.setDrawHoleEnabled(true);

		pieChart.setRotationAngle(125); // 初始旋转角度

		// draws the corresponding description value into the slice
		// mChart.setDrawXValues(true);

		// enable rotation of the chart by touch
		pieChart.setRotationEnabled(false); // 可以手动旋转

		// display percentage values
		pieChart.setUsePercentValues(true);  //显示成百分比
		// mChart.setUnit(" €");
		// mChart.setDrawUnitsInChart(true);

		// add a selection listener
//      mChart.setOnChartValueSelectedListener(this);
		// mChart.setTouchEnabled(false);

//      mChart.setOnAnimationListener(this);

		//pieChart.setCenterText("土壤营养元素");  //饼状图中间的文字

		//设置数据
		pieChart.setData(pieData);

		// undo all highlights
//      pieChart.highlightValues(null);
//      pieChart.invalidate();

		Legend mLegend = pieChart.getLegend();  //设置比例图
		mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);  //最右边显示
//      mLegend.setForm(LegendForm.LINE);  //设置比例图的形状，默认是方形
		mLegend.setXEntrySpace(7f);
		mLegend.setYEntrySpace(5f);

		pieChart.animateXY(1000, 1000);  //设置动画
		// mChart.spin(2000, 0, 360);
	}

	/**
	 *
	 * @param count 分成几部分
	 * @param range
	 */
	private PieData getNutPieData(int count, float range, String optionString) {

		ArrayList<String> xValues = null;
		ArrayList<Entry> yValues = null;
		PieDataSet pieDataSet = null;
		PieData pieData = null;
		if(optionString.equals("all"))
		{
			xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容

			xValues.add("N:氮");
			xValues.add("P:磷");
			xValues.add("K:钾");
			xValues.add("Ca:钙");
			xValues.add("Mg:镁");
			xValues.add("S:硫");
			xValues.add("Fe:铁");
			xValues.add("Mn:锰");
			xValues.add("B:硼");
			xValues.add("Zn:锌");
			xValues.add("Cu:铜");
			xValues.add("Mo:钼");
			xValues.add("CI:氯");

			yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据

			// 饼图数据
			/**
			 * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
			 * 所以 14代表的百分比就是14%
			 */
			float N = 1870;
			float P = 735;
			float K = 534;
			float Ca = 191.3f;
			float Mg = 170;
			float S = 210;
			float Fe = 34;
			float Mn = 17;
			float B = 50;
			float Zn = 60;
			float Cu = 21;
			float Mo = 1.4f;
			float CI = 70;

			yValues.add(new Entry(N, 0));
			yValues.add(new Entry(P, 1));
			yValues.add(new Entry(K, 2));
			yValues.add(new Entry(Ca, 3));
			yValues.add(new Entry(Mg, 4));
			yValues.add(new Entry(S, 5));
			yValues.add(new Entry(Fe, 6));
			yValues.add(new Entry(Mn, 7));
			yValues.add(new Entry(B, 8));
			yValues.add(new Entry(Zn, 9));
			yValues.add(new Entry(Cu, 10));
			yValues.add(new Entry(Mo, 11));
			yValues.add(new Entry(CI, 12));


			//y轴的集合
			pieDataSet = new PieDataSet(yValues, "植物营养元素"/*显示在比例图上*/);
			pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离

			ArrayList<Integer> colors = new ArrayList<Integer>();

			// 饼图颜色
			colors.add(dodgerblue);
			colors.add(sienna);
			colors.add(limegreen);
			colors.add(slateblue);
			colors.add(violet);
			colors.add(lightcoral);
			colors.add(orange);
			colors.add(gold);
			colors.add(deepskyblue);
			colors.add(darkred);
			colors.add(indigo);
			colors.add(palegreen);
			colors.add(rosybrown);

			pieDataSet.setColors(colors);
			pieDataSet.setValueTextColor(white);

			DisplayMetrics metrics = getResources().getDisplayMetrics();
			float px = 5 * (metrics.densityDpi / 160f);
			pieDataSet.setSelectionShift(px); // 选中态多出的长度
			pieData = new PieData(xValues, pieDataSet);

		}
		return pieData;
	}

	/**
	 * @Function: private class ContrlReceiver extends BroadcastReceiver
	 * @Description:
	 *      接受来自Service的信息，并且转发给相应fragment来改变相应组件内容
	 **/
	private class RecvReceiver extends BroadcastReceiver {

		public RecvReceiver() {
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String typeString = intent.getStringExtra("update");
			if(typeString != null)
			{
				if(typeString.equals("temp"))/*发送给第一个ihome fragment*/
				{
					String tempString = intent.getStringExtra("temp");
					//tempCATextview.setText(tempString+"℃");
					//tempCGTextview.setText(tempString+"℃");
				}
			}

		}//onReceive


	}
	
	
}
