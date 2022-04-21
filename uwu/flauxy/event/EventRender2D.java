package uwu.flauxy.event;

import com.darkmagician6.eventapi.events.Event;

public class EventRender2D implements Event {
    private float particalTicks;

    public EventRender2D(float particleTicks) {
        this.particalTicks = particleTicks;
    }

    public float getParticalTicks() {
        return particalTicks;
    }

}
