package uwu.flauxy.utils;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventSendPacket;
import uwu.flauxy.event.impl.EventUpdate;

public class PacketUtil {
    private static final int MAX_THREADS = 75;
    static Minecraft mc = Minecraft.getMinecraft();
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);

    public static Future<Packet<?>> packetDelayed(Packet<?> packet, long delay) {
        return executor.submit(() -> {
            Thread.sleep(delay);
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet);
            return packet;
        });
    }

    public static void packetNoEvent(Packet<?> packet) {
        mc.getNetHandler().addToSendQueueNoEvent(packet);
    }


    public static void sendSilentPacket(Packet<?> packet) {

        mc.getNetHandler().addToSendQueueNoEvent(packet);
    }

    public static void sendPacket(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendC04(double x, double y, double z, boolean ground, boolean silent) {
        if (silent) {
            packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
        } else {
            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
        }
    }
    public static boolean isPacketBlinkPacket(Packet p){
        return p instanceof C03PacketPlayer || p instanceof C03PacketPlayer.C04PacketPlayerPosition || p instanceof C0FPacketConfirmTransaction;
    }
    public static boolean isPacketPingSpoof(Packet p){
        return p instanceof C0FPacketConfirmTransaction || p instanceof C00PacketKeepAlive;
    }
    // private LinkedList<Packet> packetsLinked = new LinkedList<>();
    public static void blink(LinkedList<Packet> packetsLinked, Event e, int flyTicks, int pulseDelay, int maxDelay){
        int maxTick = maxDelay; // Integer.MAX_VALUE for infinite blink
        int tickDelay = pulseDelay;
        if(e instanceof EventSendPacket){
            EventSendPacket eventSendPacket = (EventSendPacket) e;
            if(PacketUtil.isPacketBlinkPacket(eventSendPacket.getPacket()) && flyTicks >= 0 && flyTicks < maxTick){
                packetsLinked.add(eventSendPacket.getPacket());
                eventSendPacket.setCancelled(true);
            }
            if(flyTicks % tickDelay == 0 && flyTicks < maxTick){
                for(int i = 0; i < packetsLinked.size() - 1; i++){
                    PacketUtil.packetNoEvent(packetsLinked.get(i));
                }
                packetsLinked.clear();
            }
        }
    }
    public static void blink(LinkedList<Packet> packetsLinked, Event e, int pulseDelay, int maxDelay){
        int maxTick = maxDelay; // Integer.MAX_VALUE for infinite blink
        int tickDelay = pulseDelay;
        int flyTicks = 0;
        if(e instanceof EventUpdate){
            flyTicks++;
        }
        if(e instanceof EventSendPacket){
            EventSendPacket eventSendPacket = (EventSendPacket) e;
            if(PacketUtil.isPacketBlinkPacket(eventSendPacket.getPacket()) && flyTicks >= 0 && flyTicks < maxTick){
                packetsLinked.add(eventSendPacket.getPacket());
                eventSendPacket.setCancelled(true);
            }
            if(flyTicks % tickDelay == 0 && flyTicks < maxTick){
                for(int i = 0; i < packetsLinked.size() - 1; i++){
                    PacketUtil.packetNoEvent(packetsLinked.get(i));
                }
                packetsLinked.clear();
            }
        }
    }

}