package uwu.flauxy.module.impl.player;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;

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
    
    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            if(mc.thePlayer.onGround){
                lastSafePos = mc.thePlayer.getPosition();
            }

            if(mc.thePlayer.fallDistance > val.getValue()){
                Jumped = true;
                resetFall();
            }
            if(!Jumped){
                ranTicks = 0;
                fps = mc.gameSettings.limitFramerate;
            }
            if(Jumped){
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
