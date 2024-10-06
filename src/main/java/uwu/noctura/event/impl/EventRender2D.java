package uwu.noctura.event.impl;

import uwu.noctura.event.Event;

public class EventRender2D extends Event {
    private float particalTicks;

    public EventRender2D(float particleTicks) {
        this.particalTicks = particleTicks;
    }

    public float getParticalTicks() {
        return particalTicks;
    }

}
