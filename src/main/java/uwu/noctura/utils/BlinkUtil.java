package uwu.noctura.utils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.utils.timer.Timer;
import java.util.LinkedList;
public class BlinkUtil {

    public static void blink(EventSendPacket e, LinkedList<Packet> packet, Timer timer, long time){
        if(e.getPacket() instanceof C00PacketKeepAlive || e.getPacket() instanceof C0FPacketConfirmTransaction || e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook ||
                e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook){
            packet.add(e.getPacket());
            e.setCancelled(true);
        }
        if(timer.hasTimeElapsed(time, true)){
            if(!packet.isEmpty()){
                PacketUtil.packetNoEvent(packet.poll());
                packet.clear();
            }
        }
    }

    public static void blink(EventSendPacket e, LinkedList<Packet> packet, boolean shouldBlink){
        if(e.getPacket() instanceof C00PacketKeepAlive || e.getPacket() instanceof C0FPacketConfirmTransaction || e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook ||
                e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook){
            packet.add(e.getPacket());
            e.setCancelled(true);
        }
        if(shouldBlink){
            if(!packet.isEmpty()){
                PacketUtil.packetNoEvent(packet.poll());
                packet.clear();
            }
        }
    }

}
