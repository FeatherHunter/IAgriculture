package com.ifuture.iagriculture.Device;

import android.widget.Switch;
import android.widget.TextView;

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
}
