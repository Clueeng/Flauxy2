package uwu.flauxy.event.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;
import uwu.flauxy.event.Event;

public class EventSendPacket extends Event {
    public Packet packet;

    @Getter @Setter
    public boolean cancelled;

    public void cancel(){
        setCancelled(true);
    }

    public EventSendPacket(Packet inputPacket) {
        this.packet = inputPacket;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> T getPacket() {
        return (T)this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
