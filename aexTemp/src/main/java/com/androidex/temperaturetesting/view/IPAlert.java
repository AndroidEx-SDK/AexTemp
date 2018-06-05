package com.androidex.temperaturetesting.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.androidex.temperaturetesting.Config.Constant;
import com.androidex.temperaturetesting.R;
import com.androidex.temperaturetesting.event.Event;
import com.androidex.temperaturetesting.utils.WifiTools;
import com.androidex.temperaturetesting.xinterface.WifiIpCallBack;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2018/4/24.
 */

public class IPAlert extends Dialog implements WifiIpCallBack ,Constant{
    private TextView msgText;
    private Context context;
    private Handler handler;

    public IPAlert(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public IPAlert(@NonNull Context context, int themeResId,Handler handler) {
        super(context, themeResId);
        this.context = context;
        this.handler = handler;
    }

    protected IPAlert(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
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
    public void show() {
        super.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WifiTools.initWifiIP(context,IPAlert.this);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onCallBack(final String msg) {
        if(msg.equals("Success")){
            EventBus.getDefault().post(new Event(EVENT_WHAT_IP_RESULT,true));
        }else if(msg.equals("Error")){
            EventBus.getDefault().post(new Event(EVENT_WHAT_IP_RESULT,false));
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgText.setText(msg);
            }
        });
    }
}
