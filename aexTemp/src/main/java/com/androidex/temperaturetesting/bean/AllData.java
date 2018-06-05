package com.androidex.temperaturetesting.bean;

import android.util.Log;

/**
 * Created by Administrator on 2018/4/27.
 */

public class AllData {
    public String[] keys;
    public String[] values;

    public void showDate(){
        if(keys!=null){
            Log.i("xiao_","===============k e y s===================");
            for(int i=0;i<keys.length;i++){
                Log.i("xiao_",keys[i]);
            }
            Log.i("xiao_","===============E n d===================");
        }

        if(values!=null){
            Log.i("xiao_","===============V a l u e s===================");
            for(int i=0;i<values.length;i++){
                Log.i("xiao_",values[i]);
            }
            Log.i("xiao_","===============E n d===================");
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
