package uwu.noctura.event.impl;

import net.minecraft.entity.Entity;
import uwu.noctura.event.Event;

public class EventPostRenderPlayer extends Event {
    private Entity entity;
    private float partialTicks;

    public EventPostRenderPlayer(Entity entity, float partialTicks) {
        this.entity = entity;
        this.partialTicks = partialTicks;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}