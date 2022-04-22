package uwu.flauxy.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.network.Packet;

public class EventSendPacket implements Event {
    public Packet packet;

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
