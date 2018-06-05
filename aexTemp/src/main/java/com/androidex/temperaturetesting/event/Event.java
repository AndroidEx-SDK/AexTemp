package com.androidex.temperaturetesting.event;

/**
 * Created by Administrator on 2018/4/24.
 */

public class Event {
    public Event(int what,Object msg){
        this.what = what;
        this.msg = msg;
    }
    public int what;
    public Object msg;
}
