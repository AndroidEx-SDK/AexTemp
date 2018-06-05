package com.androidex.temperaturetesting.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.androidex.temperaturetesting.Config.Constant;
import com.androidex.temperaturetesting.R;
import com.androidex.temperaturetesting.event.Event;
import com.androidex.temperaturetesting.utils.WifiTools;
import com.androidex.temperaturetesting.xinterface.WifiStateCallBack;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2018/4/24.
 */

public class WifiAlert extends Dialog implements WifiStateCallBack,Constant {
    private TextView msgText;
    private Context context;
    private Handler handler;

    public WifiAlert(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public WifiAlert(@NonNull Context context, int themeResId, Handler handler) {
        super(context, themeResId);
        this.context = context;
        this.handler = handler;
    }

    protected WifiAlert(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifialert);
        msgText = findViewById(R.id.msg);
    }

    @Override
    public void onCallBack(final String msg) {
        if(msg.equals("Success")){
            EventBus.getDefault().post(new Event(EVENT_WHAT_SSID_RESULT,true));
        }else if(msg.equals("Error")){
            EventBus.getDefault().post(new Event(EVENT_WHAT_SSID_RESULT,false));
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgText.setText(msg);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WifiTools.connectSSID(context, WifiAlert.this); //连接指定WIFI
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
