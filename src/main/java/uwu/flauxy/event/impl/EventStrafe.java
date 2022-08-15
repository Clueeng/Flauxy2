package uwu.flauxy.event.impl;

import lombok.Getter;
import lombok.Setter;
import uwu.flauxy.event.Event;

public class EventStrafe extends Event {

    @Getter @Setter
    float yaw;
    public EventStrafe(float yaw){
        this.yaw = yaw;
    }

}
