package uwu.flauxy.event.impl;

import lombok.Getter;
import lombok.Setter;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;

public class EventMotion extends Event {

    @Getter
    @Setter
    public double x, y, z;
    @Getter
    @Setter
    public float yaw, pitch, prevYaw, prevPitch;
    @Getter
    @Setter
    public boolean onGround;

    public boolean isPre(){
        return type == EventType.PRE;
    }

    public boolean isPost(){
        return type == EventType.POST;
    }
    public EventMotion(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }



}
