package uwu.flauxy.event.impl;

import com.darkmagician6.eventapi.events.Event;
import uwu.flauxy.event.Event;

public class EventRender2D implements Event {
    private float particalTicks;

    public EventRender2D(float particleTicks) {
        this.particalTicks = particleTicks;
    }

    public float getParticalTicks() {
        return particalTicks;
    }

}
