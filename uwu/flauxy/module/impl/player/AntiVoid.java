package uwu.flauxy.module.impl.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;

@ModuleInfo(name = "AntiVoid", displayName = "Anti Void", key = 0, cat = Category.Player)
public class AntiVoid extends Module {

    public ModeSetting mode  = new ModeSetting("Mode", "Packet", "Packet", "Verus" , "Jump");
    public NumberSetting val = new NumberSetting("Distance", 3, 0, 10, 0.5);
    
    private BlockPos lastSafePos;
    private boolean Jumped = false;

    public AntiVoid() {
        this.addSettings(mode, val);
    }
    
    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            switch (mode.getMode()) {
                case "Packet":
                    if(mc.thePlayer.onGround){
                        lastSafePos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    }

                    if (mc.thePlayer.fallDistance >= val.getValue() && MoveUtils.isOverVoid()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ(), true));
                        mc.thePlayer.fallDistance = 0;

                    }
                    break;
                case "Verus":
                    if(mc.thePlayer.onGround){
                        lastSafePos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    }

                    if (mc.thePlayer.fallDistance >= val.getValue() && MoveUtils.isOverVoid()) {
                        mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ(), true));
                    }
                    break;
                case "Jump":
                    if (mc.thePlayer.fallDistance >= val.getValue() && MoveUtils.isOverVoid() && !Jumped){
                        mc.thePlayer.jump();
                        mc.thePlayer.fallDistance = 0;
                        Jumped = true;
                    }else if(mc.thePlayer.fallDistance < val.getValue()){
                        Jumped = false;
                    }if(Jumped){
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ(), true));
                }
            }
        }
    }
}
