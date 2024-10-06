package uwu.noctura.event.impl;

import lombok.Getter;
import uwu.noctura.event.Event;

public class EventRender3D extends Event {
    @Getter
    private float particalTicks;

    public EventRender3D(float particleTicks) {
        this.particalTicks = particleTicks;
    }

}
