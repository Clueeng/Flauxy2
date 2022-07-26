package uwu.flauxy.module.impl.player;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBarrier;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.packet.EventPacket;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;

@ModuleInfo(name = "Nofall", displayName = "No Fall", key = -1, cat = Category.Player)
public class Nofall extends Module {

    public static ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Spoof", "Spoof2", "Edit", "Verus", "Collision", "Collision Silent", "Spartan", "AAC");;
    boolean can;

    public Nofall() {
        this.addSettings(mode);
    }

    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            EventMotion event = (EventMotion) e;
            if (mode.is("Spoof")) {
                if (mc.thePlayer.fallDistance >= 2.75 && e.getType().equals(EventType.PRE)) {
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer(true));
                }
            }

            if (mode.is("Packet") && (mc.thePlayer.fallDistance >= 2.75)) {
                PacketUtil.sendSilentPacket(new C03PacketPlayer(true));
            }

            if (mode.is("Spoof2")) {
                if (mc.thePlayer.fallDistance > 2) {
                    PacketUtil.sendPacket(new C03PacketPlayer(true));
                    mc.thePlayer.fallDistance = 0;
                }
            }

            if (mode.is("Edit")) {
                if (mc.thePlayer.fallDistance > mc.thePlayer.fallDistance) {
                    mc.thePlayer.fallDistance = 0.0f;
                }
                if (mc.thePlayer.motionY < 0.0 && mc.thePlayer.fallDistance > 2.124 && !MoveUtils.isOverVoid()  && !mc.thePlayer.isSpectator() && !mc.thePlayer.capabilities.allowFlying) {
                    final double motionY = mc.thePlayer.motionY;
                    final double fallingDist = mc.thePlayer.fallDistance - mc.thePlayer.fallDistance;
                    final double realDist = fallingDist + -((motionY - 0.08) * 0.9800000190734863);
                    if (realDist >= 3.0) {
                        event.setOnGround(true);
                        mc.thePlayer.fallDistance = 0.0f;

                    }
                }
            }

            if (mode.is("Verus")) {
                if (mc.thePlayer.fallDistance >= 3 && e.getType().equals(EventType.PRE)) {
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer(true));
                    mc.thePlayer.fallDistance = 0;
                }
            }


            if (mode.is("Collision") && !MoveUtils.isOverVoid()) {
                if (mc.thePlayer.fallDistance - mc.thePlayer.motionY >= 3 && !Flauxy.INSTANCE.getModuleManager().getModule("Speed").isToggled()) {
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.fallDistance = 0;
                    event.setOnGround(true);
                }
            }

            if (mode.is("Collision Silent")) {
                if (mc.thePlayer.fallDistance > 2) {
                    PacketUtil.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook((mc.thePlayer.posX + mc.thePlayer.lastTickPosX) / 2, (mc.thePlayer.posY - (mc.thePlayer.posY % (1 / 64.0))), (mc.thePlayer.posZ + mc.thePlayer.lastTickPosZ) / 2, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                    mc.thePlayer.fallDistance = 0;
                }
            }
        }

        if (e instanceof EventPacket) {
            EventPacket ep = (EventPacket) e;
            if (mode.is("Verus")) {
                if (mc.thePlayer.fallDistance >= 2.75) {
                    if (ep.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                        ep.setCancelled(true);
                    }
                }
                if (ep.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                    ep.setCancelled(true);
                }
                if (ep.getPacket() instanceof C0FPacketConfirmTransaction) {
                    ep.setCancelled(true);
                }
            }

            if (mode.is("Spartan") && !MoveUtils.isOverVoid()) {
                if (ep.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer c03 = (C03PacketPlayer) ep.getPacket();
                    if (!can && mc.thePlayer.fallDistance >= 3.4 && !mc.thePlayer.onGround) {
                        c03.setOnGround(false);
                        can = true;
                    }
                    if (can) {
                        c03.setOnGround(true);
                        mc.thePlayer.fallDistance = 0;
                        can = true;
                    }
                }
            }
        }
    }




}
