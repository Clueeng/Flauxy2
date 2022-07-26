package uwu.flauxy.event.impl;

import net.minecraft.network.Packet;
import uwu.flauxy.event.Event;

public class EventReceivePacket extends Event {
    public Packet packet;

    @SuppressWarnings("unchecked")
    public <T extends Packet> T getPacket() {
        return (T)packet;
    }
    public void setPacket(Packet packet) {
        this.packet = packet;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    public boolean cancelled;
    public boolean isCancelled() {
        return cancelled;
    }
    public EventReceivePacket(Packet packetIn) {
        this.packet = packetIn;
    }

}
