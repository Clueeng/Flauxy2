package uwu.flauxy.module.impl.movement;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.event.impl.EventSendPacket;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;
import java.util.LinkedList;

@ModuleInfo(name = "BoatFly", displayName = "Boat Fly", cat = Category.Movement, key = -1)
public class BoatFly extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Redesky", "Redesky", "Vulcan");
    public ModeSetting redeMode = new ModeSetting("Redesky Mode", "Test", "Test", "Normal", "idk").setCanShow(m -> mode.is("Redesky"));
    public NumberSetting redeTimer = new NumberSetting("Timer", 1, 0.1, 10, 0.1).setCanShow(m -> mode.is("Redesky"));

    public BoatFly(){
        addSettings(mode, redeMode, redeTimer);
    }

    int ticks = 0, flyingTicks = 0;
    boolean shouldFly = false;
    private LinkedList<Packet> packets = new LinkedList<>();

    @Override
    public void onEnable() {
        flyingTicks = 0;
        ticks = 0;
        if(!isPlayerInsideBoat()){
            Wrapper.instance.log("You're not in a boat dumbass");
            this.toggle();
        }
    }

    public void onEvent(Event e){
        switch(mode.getMode()){
            case "Redesky":{
                Redesky(e);
                break;
            }
        }
    }

    @Override
    public void onDisable() {
        switch(mode.getMode()){
            case "Redesky":{
                switch(redeMode.getMode()){
                    case "idk":{
                        for(int i = 0; i < packets.size(); i++){
                            PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                        }
                        break;
                    }
                }
                break;
            }
        }
        stopFlying();
        mc.timer.timerSpeed = 1.0F;
    }

    public void Redesky(Event e){
        switch(redeMode.getMode()){

            case "idk":{
                if(e instanceof EventReceivePacket){
                    EventReceivePacket ev = (EventReceivePacket) e;
                    if(ev.getPacket() instanceof S12PacketEntityVelocity){
                        ev.setCancelled(true);
                    }

                }
                if(e instanceof EventSendPacket){

                    EventSendPacket ev = (EventSendPacket) e;
                }
                if(e instanceof EventUpdate){
                    if(isPlayerInsideBoat()){
                        PacketUtil.packetNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                        shouldFly = true;
                    }
                    if(shouldFly){// String langIn, int viewIn, EntityPlayer.EnumChatVisibility chatVisibilityIn, boolean enableColorsIn, int modelPartFlagsIn
                        mc.timer.timerSpeed = 0.7f;
                        startFlying(0.04f);
                        flyingTicks++;
                        if(flyingTicks % 4 == 0){
                            PacketUtil.packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.02, mc.thePlayer.posZ, false));
                            PacketUtil.packetNoEvent(new C19PacketResourcePackStatus("1111111111111111111111111111111", C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                        }
                    }else{
                        stopFlying();
                        flyingTicks = 0;
                    }
                    ticks++;
                }
                break;
            }

            case "Normal":{
                if(e instanceof EventReceivePacket){
                    EventReceivePacket ev = (EventReceivePacket) e;
                    if(ev.getPacket() instanceof S12PacketEntityVelocity){
                        ev.setCancelled(true);
                    }
                }
                if(e instanceof EventUpdate){
                    if(isPlayerInsideBoat()){
                        PacketUtil.packetNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                        shouldFly = true;
                    }
                    if(shouldFly){
                        mc.timer.timerSpeed = (float) redeTimer.getValue();
                        startFlying(0.015f);
                        flyingTicks++;
                    }else{
                        stopFlying();
                        flyingTicks = 0;
                    }
                    ticks++;
                }
                break;
            }

            case "Test":{
                if(e instanceof EventUpdate){
                    if(isPlayerInsideBoat()){
                        PacketUtil.packetNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                        shouldFly = true;
                    }
                    if(shouldFly){
                        if(flyingTicks < 10){
                            mc.thePlayer.motionY += 0.03f;
                        }
                        mc.timer.timerSpeed = (float) redeTimer.getValue();
                        startFlying(0.20f);
                        if(flyingTicks >= 50 * redeTimer.getValue()){
                            Wrapper.instance.log("Disabling fly");
                            shouldFly = false;
                        }
                        flyingTicks++;
                    }else{

                        stopFlying();
                        flyingTicks = 0;
                    }
                    ticks++;
                }
                break;
            }
        }
    }

    public boolean isPlayerRiding(){
        if(mc.thePlayer.ridingEntity != null){
            return true;
        }
        return false;
    }

    public boolean isPlayerInsideBoat(){
        if(isPlayerRiding() && mc.thePlayer.ridingEntity instanceof EntityBoat){
            return true;
        }
        return false;
    }

    public void startFlying(float speed){
        mc.thePlayer.capabilities.allowFlying = true;
        mc.thePlayer.capabilities.isFlying = true;
        mc.thePlayer.capabilities.setFlySpeed(speed);
    }
    public void stopFlying(){
        mc.thePlayer.capabilities.allowFlying = false;
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.capabilities.setFlySpeed(0.05F);
    }
}
