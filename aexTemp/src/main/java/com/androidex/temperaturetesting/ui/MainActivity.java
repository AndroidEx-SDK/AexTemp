package com.androidex.temperaturetesting.ui;
import android.content.Intent;

import com.androidex.temperaturetesting.Config.DeviceConfig;
import com.androidex.temperaturetesting.R;
import com.androidex.temperaturetesting.base.BaseActivity;
import com.androidex.temperaturetesting.event.Event;
import com.androidex.temperaturetesting.utils.WifiTools;
import com.androidex.temperaturetesting.view.IPAlert;
import com.androidex.temperaturetesting.view.WifiAlert;

public class MainActivity extends BaseActivity {
    private WifiAlert wifiAlert;
    @Override
    protected int bindView() {
        return R.layout.activity_main;
    }

    @Override
    protected void mainThread() {
        wifiAlert = new WifiAlert(this,R.style.Dialog,mHandler);
        wifiAlert.show();
    }

    @Override
    protected void onEvent(Event event) {
        if(event.what == EVENT_WHAT_SSID_RESULT){
            if(wifiAlert!=null){
                wifiAlert.cancel();
                wifiAlert = null;
            }
            boolean result = (boolean) event.msg;
            if(result){
                if(WifiTools.getWifiIp(this).equals(DeviceConfig.IP)){
                    showToast(true,"连接成功");
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                }else{
                    showToast(true,"连接失败，请确认ip是否被占用");
                }
            }else{
                showToast(false,"连接失败，请确认ip是否被占用");
            }
            this.finish();
        }
    }
}
