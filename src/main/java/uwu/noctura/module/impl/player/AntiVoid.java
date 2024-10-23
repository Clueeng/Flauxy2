package uwu.noctura.module.impl.player;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.utils.MoveUtils;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.Wrapper;

import java.util.ArrayList;
import java.util.Iterator;

@ModuleInfo(name = "AntiVoid", displayName = "Anti Void", key = 0, cat = Category.Player)
public class AntiVoid extends Module {

    public ModeSetting mode  = new ModeSetting("Mode", "Basic", "Basic", "Flag", "Packet", "Matrix");
    public NumberSetting val = new NumberSetting("Distance", 3, 0, 10, 0.5);
    public BooleanSetting helper = new BooleanSetting("Smart Catch", true).setCanShow(m ->  mode.is("Basic"));
    public BooleanSetting overVoidCheck = new BooleanSetting("Void Only", true);
    
    private BlockPos lastSafePos;
    private boolean Jumped = false;
    int ticks=0;
    int ranTicks;
    ArrayList<Packet> hoverPackets = new ArrayList<>();

    public AntiVoid() {
        this.addSettings(mode, val, helper, overVoidCheck);
    }
    float fps = 0;
    boolean overVoid;
    
    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            if(mc.thePlayer.ticksExisted < 10)return;
            if(mc.thePlayer.onGround){
                lastSafePos = mc.thePlayer.getPosition();
            }
            if(lastSafePos == null){
                Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO,"AntiVoid", "Enable the module on ground"));
                this.toggle();
            }
            for(int i = (int) mc.thePlayer.posY; i > 0; i--){
                IBlockState a = mc.theWorld.getBlockState(new BlockPos(Math.floor(mc.thePlayer.posX),i,Math.floor(mc.thePlayer.posX)));
                if(!a.getBlock().getMaterial().equals(Material.air)){
                    overVoid = overVoidCheck.getValue();
                }
            }
            if(mc.thePlayer.fallDistance > val.getValue() ){
                Jumped = true;
                resetFall();
            }
            if(!Jumped){
                ranTicks = 0;
            }
            if(Jumped && lastSafePos != null){
                ticks++;
                switch(mode.getMode()){
                    case "Basic":{
                        mc.thePlayer.setPosition(lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ());
                        smartCatch();
                        Jumped = false;
                        break;
                    }
                    case "Flag":{
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.01, mc.thePlayer.posZ);
                        //mc.thePlayer.motionY = -0.11760000228882461; // -0.09800000190734863
                        mc.thePlayer.motionY += 0.12;
                        ranTicks++;
                        if(ranTicks > 4){
                            Jumped = false;
                        }
                        break;
                    }
                    case "Packet":{
                        mc.thePlayer.setPosition(lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ());
                        mc.thePlayer.motionY = 0.1;
                        PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition( lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ(), true ));
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition( lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ(), true ));
                        Jumped = false;
                        break;
                    }
                    case "Matrix":{
                        fps += 1;
                        if(fps > 0){
                            mc.thePlayer.motionY = 0;
                            MoveUtils.stopMoving();
                            MoveUtils.cancelMoveInputs();
                            mc.thePlayer.setPosition(lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ());
                        }
                        break;
                    }
                }
                overVoid = false;
            }
        }
        if(e instanceof EventSendPacket){
            EventSendPacket es = (EventSendPacket) e;
            if(mode.is("Matrix")){
                if(fps > 1){
                    if(fps == 3){
                        Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Antivoid", "Catching you in 4 seconds"));
                    }
                    if(fps < 20){
                        if(es.getPacket() instanceof C03PacketPlayer){
                            es.setCancelled(true);
                            hoverPackets.add(es.getPacket());
                        }
                    }else{
                        MoveUtils.enableMoveInputs();
                        Iterator<Packet> iterator = hoverPackets.iterator();
                        while (iterator.hasNext()) {
                            Packet p = iterator.next();
                            PacketUtil.sendSilentPacket(p);
                            iterator.remove();
                        }
                        fps = 0;
                        Jumped = false;
                    }
                }
            }
        }
    }

    public void smartCatch(){
        if(helper.getValue()){
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionX *= -0.5;
            mc.thePlayer.motionZ *= -0.5;
            mc.thePlayer.motionY = 0.42f;
        }
    }

    public void resetFall(){
        mc.thePlayer.fallDistance = 0f;
    }
}
