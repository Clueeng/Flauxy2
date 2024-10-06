package uwu.noctura.event.impl.packet;

import net.minecraft.network.Packet;
import uwu.noctura.event.Event;
import uwu.noctura.event.EventDirection;

public class EventPacket extends Event<EventPacket> {

    private Packet packet;

    public EventPacket(Packet packet) {
        this.packet = packet;
    }


    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }


    public boolean isIncoming() {

        return getDirection() == EventDirection.INCOMING;
    }
    public boolean isOutgoing() {
        return getDirection() == EventDirection.OUTGOING;
    }
}