package com.ifuture.iagriculture.Device;

import android.widget.TextView;

import com.gc.materialdesign.views.Switch;

/**
 * Created by feather on 2016/4/19.
 */
public class Device {

    String deviceNum;
    TextView tempValue;
    TextView humiValue;
    Switch warmDeviceState;
    Switch irriDeviceState;

    public Device(String deviceNum, TextView tempValue, TextView humiValue, Switch warmDeviceState, Switch irriDeviceState)
    {
        this.deviceNum = deviceNum;
        this.tempValue = tempValue;
        this.humiValue = humiValue;
        this.warmDeviceState = warmDeviceState;
        this.irriDeviceState = irriDeviceState;
    }

    public TextView getHumiValue() {
        return humiValue;
    }

    public void setHumiValue(TextView humiValue) {
        this.humiValue = humiValue;
    }

    public TextView getTempValue() {
        return tempValue;
    }

    public void setTempValue(TextView tempValue) {
        this.tempValue = tempValue;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public Switch getWarmDeviceState() {
        return warmDeviceState;
    }

    public void setWarmDeviceState(Switch warmDeviceState) {
        this.warmDeviceState = warmDeviceState;
    }

    public Switch getIrriDeviceState() {
        return irriDeviceState;
    }

    public void setIrriDeviceState(Switch irriDeviceState) {
        this.irriDeviceState = irriDeviceState;
    }

}
