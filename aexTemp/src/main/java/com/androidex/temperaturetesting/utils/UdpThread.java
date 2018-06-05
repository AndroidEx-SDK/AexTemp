package com.androidex.temperaturetesting.utils;

import android.util.Log;

import com.androidex.temperaturetesting.Config.Constant;
import com.androidex.temperaturetesting.event.Event;

import org.greenrobot.eventbus.EventBus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/4/26.
 */

public class UdpThread extends Thread implements Constant{
    public static boolean Runstate = false;
    DatagramSocket receiveSocket;
    private boolean isRun = false;
    public UdpThread(){
        try {
            isRun = true;
            receiveSocket = new DatagramSocket(8001);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        Runstate = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage8001("27000000000181000000008200");
            }
        },0,3000);
        while (isRun){
            byte datas[] = new byte[13];
            DatagramPacket packet = new DatagramPacket(datas, datas.length);
            try {
                receiveSocket.receive(packet);
                byte[] data = packet.getData();
                if(data!=null && data.length>0){
                    int temp = data[7];
                    EventBus.getDefault().post(new Event(EVENT_WHAT_SERVICE_MESSAGE,temp));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Runstate = false;
    }

    public void sendMessage8001(String data){
        try {
            InetAddress serverAddress = InetAddress.getByName("192.168.1.11");
            byte[] bytes = toBytes(data);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddress, 4001);
            receiveSocket.send(packet);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private  byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    public void stopRun(){
        isRun = false;
    }
}
