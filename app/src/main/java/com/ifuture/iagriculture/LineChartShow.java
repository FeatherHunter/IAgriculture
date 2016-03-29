package com.ifuture.iagriculture;

/**
 * Created by feather on 2016/3/29.
 */
public class LineChartShow {
    public int animateXTime;
    public String descriptionString;

    public LineChartShow()
    {
        animateXTime = 0;
        descriptionString = "";
    }
    public LineChartShow(int animateTime, String descriptionString)
    {
        this.animateXTime = animateTime;
        this.descriptionString = descriptionString;
    }
}
