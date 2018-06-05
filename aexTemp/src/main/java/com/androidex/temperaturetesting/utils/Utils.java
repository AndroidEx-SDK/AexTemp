package com.androidex.temperaturetesting.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.temperaturetesting.R;
import com.androidex.temperaturetesting.bean.AllData;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2018/4/24.
 */

public class Utils {
    public static void showCustomToast(Context context, boolean type, String msg) {
        Toast toast = new Toast(context);
        View contentView = View.inflate(context, R.layout.custom_toast_layout, null);
        ImageView iv_icon = (ImageView) contentView.findViewById(R.id.iv_icon);
        TextView tv_info = (TextView) contentView.findViewById(R.id.tv_info);
        if (type) {
            iv_icon.setImageResource(R.drawable.utils_toast_ok);
        } else {
            iv_icon.setImageResource(R.drawable.utils_toast_error);
        }
        tv_info.setText(msg);
        toast.setView(contentView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static String bytesToHexFun2(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }
        return new String(buf);
    }

    public static AllData getDate(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("config_data",Context.MODE_PRIVATE);
        AllData allData = new AllData();
        String keys = sharedPreferences.getString("str_key","");
        if(keys.length()<=0){
            return null;
        }else{
            String s[] = keys.split(",");
            allData.keys = s;
            String values[] = new String[s.length];
            for(int i=0;i<s.length;i++){
                File file = new File(FileUtils.getAppFilesDir(context)+"/"+s[i]+".txt");
                if(file.exists()){
                    values[i] = FileUtils.readFile(file.toString());
                }
            }
            allData.values = values;
            return allData;
        }
    }

    public static void saveData(Context context,int data){
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        addDate(context,strDate);
        File file = new File(FileUtils.getAppFilesDir(context)+"/"+strDate+".txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(file.exists()){
            FileUtils.writeStringToFile(data+"",file.toString(),true);
        }
    }

    private static void addDate(Context context,String date){
        SharedPreferences sharedPreferences = context.getSharedPreferences("config_data",Context.MODE_PRIVATE);
        String keys = sharedPreferences.getString("str_key","");
        if(keys.length()<=0){
            sharedPreferences.edit().putString("str_key",date).commit();
        }else{
            String s[] = keys.split(",");
            boolean isfind = false;
            for(int i=0;i<s.length;i++){
                if(s[i].equals(date)){
                    isfind = true;
                }
            }
            if(!isfind){
                sharedPreferences.edit().putString("str_key",keys+","+date).commit();
            }
        }
    }

    public static int getMax(ArrayList<Integer> array){
        int max = array.get(0);
        for(int x=1;x<array.size();x++){
            if(array.get(x)>max)
                max=array.get(x);
        }
        return max;
    }
    public static int getMin(ArrayList<Integer> array){
        int min = array.get(0);
        for (int x=1;x<array.size();x++){
            if(array.get(x)<min)
                min=array.get(x);
        }
        return min;
    }
}
