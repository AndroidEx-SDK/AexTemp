package com.androidex.temperaturetesting.ui;
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.androidex.temperaturetesting.R;
import com.androidex.temperaturetesting.base.BaseActivity;
import com.androidex.temperaturetesting.bean.AllData;
import com.androidex.temperaturetesting.event.Event;
import com.androidex.temperaturetesting.utils.DynamicLineChartManager;
import com.androidex.temperaturetesting.utils.LineChartManager;
import com.androidex.temperaturetesting.utils.UdpThread;
import com.androidex.temperaturetesting.utils.Utils;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by Administrator on 2018/4/25.
 */

public class HomeActivity extends BaseActivity {
    private TextView temp_;
    private UdpThread udpThread;
    private CircleProgress circleProgress;
    private LineChart mLineChart;
    private DynamicLineChartManager dynamicLineChartManager1;
    private LineChartManager lineChartManager;
    private LineChart lsLineChart;

    ArrayList<Float> xValues = new ArrayList<>();
    private List<Integer> colours = new ArrayList<>();
    List<List<Float>> yValues = new ArrayList<>();
    private String Colors[] = {"#009999","#0099CC","#330033","#33FF33","#669933","#990066","#CC3333"};
    private android.os.Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            updateLayout3();
        }
    };

    @Override
    protected int bindView() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView(View v) {
        temp_ = findViewById(R.id.temp_);
        circleProgress = findViewById(R.id.circle_progress);
        circleProgress.setMax(100);
        circleProgress.setSuffixText(" ℃");
        circleProgress.setTextColor(Color.parseColor("#000000"));
        circleProgress.setTextSize(40);
        mLineChart = findViewById(R.id.lineChart);
        dynamicLineChartManager1 = new DynamicLineChartManager(mLineChart,"温度",Color.parseColor("#009999"));
        dynamicLineChartManager1.setDescription("时间");
        dynamicLineChartManager1.setYAxis(50,-50,100);
        lsLineChart = findViewById(R.id.ls_lineChart);
        lineChartManager = new LineChartManager(lsLineChart);
        updateLayout3();
    }

    @Override
    protected void onEvent(Event event) {
        int temp = (int) event.msg;
        Utils.saveData(this,temp);
        updateLayout1(temp);
        updateLayout2(temp);
    }

    private int dMax,dMin,dCount;
    private ArrayList<Integer> lsArray = new ArrayList<>();
    private int count = 0;
    private void updateLayout1(int data){
        if(lsArray.size()<11){
            lsArray.add(data);
        }else {
            lsArray.remove(0);
            lsArray.add(data);
        }
        dMax = Utils.getMax(lsArray)+5;
        dMin = Utils.getMin(lsArray);
        if(dMin>0){
            dMin = 0;
        }else{
            dMin = dMin-5;
        }
        dCount = dMax+(Math.abs(dMin));
        dynamicLineChartManager1.setYAxis(dMax,dMin,dCount);
        dynamicLineChartManager1.addEntry(data);
    }



    private void updateLayout2(int data){
        temp_.setText("温度拾取："+data+" ℃");
        circleProgress.setProgress(data);
        if(data>=40){
            circleProgress.setFinishedColor(Color.parseColor("#ff0000")); //报警
        }else{
            circleProgress.setFinishedColor(Color.parseColor("#0099FF"));
        }
    }

    List<String> names = new ArrayList<>();
    ArrayList<Integer> mindata = new ArrayList<>();
    private void updateLayout3(){
        if(xValues.size()>0 && yValues.size()>0 && names.size()>0 && colours.size()>0){
            if(mindata.size()>0){
                int max = Utils.getMax(mindata)+5;
                int min = Utils.getMin(mindata);
                if(min>0){
                    min = 0;
                }else {
                    min = min - 5;
                }
                lineChartManager.setYAxis(max, min, min+(Math.abs(min)));
            }
            lineChartManager.showLineChart(xValues, yValues, names, colours);
            lineChartManager.setDescription("温度");
        }
    }



    @Override
    protected void mainThread() {
        super.mainThread();
        udpThread = new UdpThread();
        if(!UdpThread.Runstate){
            udpThread.start();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                AllData allData = Utils.getDate(HomeActivity.this);
                if(allData!=null){
                    if(allData.keys!=null && allData.keys.length>0){
                        for(int i=0;i<allData.keys.length && i<7;i++){
                            ArrayList<Integer> math = new ArrayList<>();
                            names.add(allData.keys[i]);
                            colours.add(Color.parseColor(Colors[i]));
                            List<Float> yValue = new ArrayList<>();
                            String values[] = allData.values[i].split(",");
                            if(values.length>0 && values.length<=20){
                                for(int j = 0;j<values.length;j++){
                                    if(values[j].length()>0){
                                        math.add(Integer.valueOf(values[j]));
                                        yValue.add(Float.valueOf(values[j]));
                                    }
                                }
                            }else{
                                for(int j = values.length-20;j<values.length;j++){
                                    if(values[j].length()>0){
                                        math.add(Integer.valueOf(values[j]));
                                        yValue.add(Float.valueOf(values[j]));
                                    }
                                }
                            }
                            yValues.add(yValue);
                            mindata.add(Utils.getMax(math));
                            mindata.add(Utils.getMin(math));
                        }
                        for (int i = 0; i <= 20; i++) {
                            xValues.add((float) i);
                        }

                    }
                }
                mHandler.sendEmptyMessage(0x01);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(0x01);
        if(udpThread!=null){
            udpThread.stopRun();
        }
        super.onDestroy();
    }
}
