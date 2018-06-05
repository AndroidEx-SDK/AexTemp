package com.androidex.temperaturetesting.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.androidex.temperaturetesting.Config.DeviceConfig;
import com.androidex.temperaturetesting.xinterface.WifiIpCallBack;
import com.androidex.temperaturetesting.xinterface.WifiStateCallBack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/4/24.
 * 注意：设置完静态IP后要通过该程序去连接WIFI，否则设置无效
 */

public class WifiTools {
    public static boolean isConnectSSID(Context context) {
        Context myContext = context;
        if (myContext == null) {
            throw new NullPointerException("context is null");
        }
        WifiManager wifiMgr = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager = (ConnectivityManager) myContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(wifiInfo.isConnected()){
                Log.i("xiao_","已经连接SSID = "+wifiMgr.getConnectionInfo().getSSID());
                return wifiMgr.getConnectionInfo().getSSID().equals(DeviceConfig.SSID);
            }else{
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean connectSSID(Context context,WifiStateCallBack callBack) throws Exception {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        callBack.onCallBack("正在打开WIFI...");
        //打开
        if(!wifiMgr.isWifiEnabled()){
            wifiMgr.setWifiEnabled(true);
        }
        while(!wifiMgr.isWifiEnabled()){
            Thread.sleep(500);
        }
        //扫描WIFI
        callBack.onCallBack("正在扫描热点...");
        wifiMgr.startScan();
        List<ScanResult> array;
        boolean isWait = true;
        while(isWait){
            array = wifiMgr.getScanResults();
            for(int i=0;i<array.size();i++){
                Log.i("xiao_",array.get(i).SSID+" -- "+DeviceConfig.SSID);
                if(array.get(i).SSID.equals("CANWiFi-II")){
                    isWait = false;
                    break;
                }
            }
            Thread.sleep(200);
        }
        //连接
        callBack.onCallBack("正在连接热点...");
        WifiConfiguration wifiConfiguration = createWifiConfig(wifiMgr);
        int rid = wifiMgr.addNetwork(wifiConfiguration);
        boolean enable = wifiMgr.enableNetwork(rid, true);
        Log.i("xiao_","正在连接热点 "+enable);
        int i = 0;
        while (i<30){ //30s
            i++;
            if(isConnectSSID(context)){
                callBack.onCallBack("Success");
                return true;
            }
            Thread.sleep(200);
        }
        callBack.onCallBack("Error");
        return false;
    }

    private static WifiConfiguration createWifiConfig(WifiManager wifiManager){
        deleteExist(wifiManager);
        WifiConfiguration config = new  WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        //指定对应的SSID
        config.SSID = DeviceConfig.SSID;
        config.preSharedKey = DeviceConfig.PASSWORD;
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;

        //指定ip
        try {
            Class<?> ipAssignment = config.getClass().getMethod("getIpAssignment").invoke(config).getClass();
            Object staticConf = config.getClass().getMethod("getStaticIpConfiguration").invoke(config);
            config.getClass().getMethod("setIpAssignment", ipAssignment).invoke(config, Enum.valueOf((Class<Enum>) ipAssignment, "STATIC"));
            if (staticConf == null) {
                Class<?> staticConfigClass = Class.forName("android.net.StaticIpConfiguration");
                staticConf = staticConfigClass.newInstance();
            }
            Constructor<?> laConstructor = LinkAddress.class.getConstructor(InetAddress.class, int.class);
            LinkAddress linkAddress = (LinkAddress) laConstructor.newInstance(InetAddress.getByName(DeviceConfig.IP), maskStr2InetMask("255.255.255.0"));
            staticConf.getClass().getField("ipAddress").set(staticConf, linkAddress);
            staticConf.getClass().getField("gateway").set(staticConf, InetAddress.getByName("192.168.1.1"));
            config.getClass().getMethod("setStaticIpConfiguration", staticConf.getClass()).invoke(config, staticConf);
        }catch (Exception e){
            e.printStackTrace();
        }
        return config;
    }

    private static void deleteExist(WifiManager wifiManager) {
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals(DeviceConfig.SSID)) {
                wifiManager.removeNetwork(config.networkId);
            }
        }
    }



    //=============== ip =====================
    public static boolean initWifiIP(Context context, WifiIpCallBack callBack){
        callBack.onCallBack("正在初始化IP");
        String nowip = getWifiIp(context);
        if(nowip.equals(DeviceConfig.IP)){
            callBack.onCallBack("Success");
            return true;
        }else{
            return changeWifiConfiguration(context,callBack);

        }
    }

    private static boolean changeWifiConfiguration(Context context,WifiIpCallBack callBack) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wm.isWifiEnabled()) {
            // wifi is disabled
            callBack.onCallBack("Error");
            return false;
        }
        WifiConfiguration wifiConf = null;
        WifiInfo connectionInfo = wm.getConnectionInfo();
        List<WifiConfiguration> configuredNetworks = wm.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration conf : configuredNetworks) {
                if (conf.networkId == connectionInfo.getNetworkId()) {
                    wifiConf = conf;
                    break;
                }
            }
        }
        if (wifiConf == null) {
            // wifi is not connected
            callBack.onCallBack("Error");
            return false;
        }
        try {
            Class<?> ipAssignment = wifiConf.getClass().getMethod("getIpAssignment").invoke(wifiConf).getClass();
            Object staticConf = wifiConf.getClass().getMethod("getStaticIpConfiguration").invoke(wifiConf);
            wifiConf.getClass().getMethod("setIpAssignment", ipAssignment).invoke(wifiConf, Enum.valueOf((Class<Enum>) ipAssignment, "STATIC"));
            if (staticConf == null) {
                Class<?> staticConfigClass = Class.forName("android.net.StaticIpConfiguration");
                staticConf = staticConfigClass.newInstance();
            }
            Constructor<?> laConstructor = LinkAddress.class.getConstructor(InetAddress.class, int.class);
            LinkAddress linkAddress = (LinkAddress) laConstructor.newInstance(InetAddress.getByName(DeviceConfig.IP), maskStr2InetMask("255.255.255.0"));
            staticConf.getClass().getField("ipAddress").set(staticConf, linkAddress);
            staticConf.getClass().getField("gateway").set(staticConf, InetAddress.getByName("192.168.1.1"));
            wifiConf.getClass().getMethod("setStaticIpConfiguration", staticConf.getClass()).invoke(wifiConf, staticConf);
            boolean result = wm.updateNetwork(wifiConf) != -1; //apply the setting
            Log.i("xiao_","result = "+result);
            if (result) result = wm.saveConfiguration(); //Save it
            if (result) wm.reassociate(); // reconnect with the new static IP
            callBack.onCallBack("Success");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            callBack.onCallBack("Error");
        }
        return false;
    }

    public static String getWifiIp(Context context){
        WifiManager wm=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(!wm.isWifiEnabled())
            wm.setWifiEnabled(true);
        WifiInfo wi=wm.getConnectionInfo();
        int ipAdd=wi.getIpAddress();
        String ip=intToIp(ipAdd);
        return ip;
    }
    private static String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    private static int maskStr2InetMask(String maskStr) {
        StringBuffer sb ;
        String str;
        int inetmask = 0;
        int count = 0;
    	/*
    	 * check the subMask format
    	 */
        Pattern pattern = Pattern.compile("(^((\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$)|^(\\d|[1-2]\\d|3[0-2])$");
        if (pattern.matcher(maskStr).matches() == false) {
            return 0;
        }

        String[] ipSegment = maskStr.split("\\.");
        for(int n =0; n<ipSegment.length;n++) {
            sb = new StringBuffer(Integer.toBinaryString(Integer.parseInt(ipSegment[n])));
            str = sb.reverse().toString();
            count=0;
            for(int i=0; i<str.length();i++) {
                i=str.indexOf("1",i);
                if(i==-1)
                    break;
                count++;
            }
            inetmask+=count;
        }
        return inetmask;
    }
}
