package uwu.flauxy.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.EventSendPacket;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.event.impl.packet.EventMove;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;

@ModuleInfo(name = "Critical", displayName = "Criticals", key = -1, cat = Category.Combat)
public class Criticals extends Module {

    public static ModeSetting mode = new ModeSetting("Mode", "Fake", "Fake", "Low", "Low2", "Jump", "Packet");
    public NumberSetting delay = new NumberSetting("Delay", 250, 0, 750, 50).setCanShow(m -> mode.is("Low") || mode.is("Low2"));

    public static boolean isCrits = false;
    Entity target = null;
    private Timer timer = new Timer();

    public Criticals(){
        addSettings(mode, delay);
    }
    int noCritTick = 0;
    public void onEvent(Event e){
        if(e instanceof EventSendPacket){
            EventSendPacket event = (EventSendPacket) e;
            if(event.getPacket() instanceof C02PacketUseEntity){
                C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();
                if(packet.getAction() == (C02PacketUseEntity.Action.ATTACK)){
                    isCrits = true;
                    target = packet.getEntityFromWorld(mc.theWorld);
                    switch (mode.getMode()){
                        case "Packet":{
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
                            //PacketUtil.packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.001, mc.thePlayer.posZ, false));
                            //PacketUtil.packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                            break;
                        }
                    }
                }
            }
        }
        if(target != null){
            if(e instanceof EventUpdate){
                EventUpdate em = (EventUpdate) e;
                if(!em.isPre())return;
                if(isCrits  && shouldRun()){
                    switch (mode.getMode()){
                        case "Jump":{
                            if(mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround){
                                mc.thePlayer.jump();
                            }
                            isCrits = false;
                            break;
                        }
                        case "Low2":{
                            if(mc.thePlayer.onGround && timer.hasTimeElapsed(delay.getValue(), false)){
                                mc.thePlayer.motionY = 0.08f;
                                isCrits = false;
                            }
                            break;
                        }
                    }
                }
            }
            if(e instanceof EventMove){
                EventMove eventMove = (EventMove) e;
                if(isCrits && shouldRun()){
                    noCritTick=0;
                    switch(mode.getMode()){
                        case "Fake":{
                            mc.thePlayer.onCriticalHit(target);
                            isCrits = false;
                            break;
                        }
                        case "Low":{
                            if(mc.thePlayer.onGround && timer.hasTimeElapsed(delay.getValue(), false)){
                                eventMove.setY(0.18f);
                                isCrits = false;
                            }
                            break;
                        }
                    }
                }else{
                    if(noCritTick <= 1 && shouldRun()){
                        timer.reset();
                        switch(mode.getMode()){
                            case "Low":{
                                eventMove.setY(-0.12f);
                                break;
                            }
                        }
                    }
                    noCritTick++;

                }

            }
        }
    }

    public boolean shouldRun(){
        return !mc.gameSettings.keyBindJump.pressed || mc.thePlayer.isCollidedVertically;
    }

}
