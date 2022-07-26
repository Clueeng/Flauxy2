package uwu.flauxy.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

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
        boolean finBol = false;
        if(p instanceof C03PacketPlayer || p instanceof C03PacketPlayer.C04PacketPlayerPosition || p instanceof C0FPacketConfirmTransaction){
            finBol = true;
        }

        return finBol;
    }
}