package uwu.noctura.module.impl.player;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.Wrapper;

@ModuleInfo(name = "AntiVoid", displayName = "Anti Void", key = 0, cat = Category.Player)
public class AntiVoid extends Module {

    public ModeSetting mode  = new ModeSetting("Mode", "Basic", "Basic", "Flag", "Packet", "Matrix");
    public NumberSetting val = new NumberSetting("Distance", 3, 0, 10, 0.5);
    public BooleanSetting helper = new BooleanSetting("Smart Catch", true).setCanShow(m ->  mode.is("Basic"));
    
    private BlockPos lastSafePos;
    private boolean Jumped = false;
    int ticks=0;
    int ranTicks;

    public AntiVoid() {
        this.addSettings(mode, val, helper);
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
                    overVoid = true;
                }
            }
            if(mc.thePlayer.fallDistance > val.getValue() && mc.thePlayer.motionY < -0.2f && overVoid){
                Jumped = true;
                resetFall();
            }
            if(!Jumped){
                ranTicks = 0;
                fps = mc.gameSettings.limitFramerate;
            }
            if(Jumped && overVoid && lastSafePos != null){
                ticks++;
                switch(mode.getMode()){
                    case "Basic":{
                        mc.thePlayer.setPosition(lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ());
                        smartCatch();
                        Jumped = false;
                        break;
                    }
                    case "Flag":{
                        mc.thePlayer.setPosition(lastSafePos.getX(), lastSafePos.getY() + 0.01, lastSafePos.getZ());
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
                        mc.gameSettings.limitFramerate = 10;
                        //mc.thePlayer.motionY -= 0.23f;
                        ServerData data = mc.getCurrentServerData();
                        mc.setServerData(new ServerData("woa", "woa.woa", false));
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition( lastSafePos.getX(), lastSafePos.getY() + 1, lastSafePos.getZ(), true ));
                        mc.setServerData(data);

                        mc.thePlayer.motionY += 0.02f;
                        if(ticks % 10 == 0){
                            mc.thePlayer.setPosition(lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ());
                            Wrapper.instance.log("a");
                            mc.gameSettings.limitFramerate = (int) fps;
                            Jumped = false;
                        }
                        break;
                    }
                }

                Wrapper.instance.log("Tried catching you");
                overVoid = false;
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
