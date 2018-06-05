package com.androidex.temperaturetesting.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.androidex.temperaturetesting.Config.Constant;
import com.androidex.temperaturetesting.event.Event;
import com.androidex.temperaturetesting.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2018/4/24.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener,Constant{
    private View v = null;
    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            onMessage(msg);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        v = LayoutInflater.from(this).inflate(bindView(), null);
        setContentView(v);
        initView(v);
        EventBus.getDefault().register(this);
        mainThread();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusMessage(Event event) {
        onEvent(event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    protected void mainThread(){

    }
    protected void onEvent(Event event){
    }
    protected void initView(View v){
    }
    protected abstract int bindView();
    @Override
    public void onClick(View view) {

    }

    protected void onMessage(Message msg){

    }
    protected void showL(String msg){
        Log.i("xiao_",msg);
    }

    public void showToast(final boolean type, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showCustomToast(BaseActivity.this, type, msg);
            }
        });
    }
}
