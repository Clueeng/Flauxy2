package uwu.flauxy.event.impl;

import lombok.Getter;
import uwu.flauxy.event.Event;

public class EventRender3D extends Event {
    @Getter
    private float particalTicks;

    public EventRender3D(float particleTicks) {
        this.particalTicks = particleTicks;
    }

}
