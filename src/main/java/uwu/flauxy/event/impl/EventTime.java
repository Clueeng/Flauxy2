package uwu.flauxy.event.impl;

import uwu.flauxy.event.Event;

public class EventTime extends Event {

    public int time;
    public boolean isUsingMode;

    public void setTime(int time){
        this.time = time;
    }
    public int getTime(){
        return time;
    }
    public void setMode(boolean m){
        this.isUsingMode = m;
    }
    public boolean getUsingMode(){
        return isUsingMode;
    }

}
