package uwu.flauxy.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Longjump", displayName = "Longjump", key = Keyboard.KEY_G, cat = Category.Movement)
public class Longjump extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Verus", "Hypixel", "Funcraft");
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 6, 0.1).setCanShow((m) -> mode.is("Verus"));

    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Damage", "Damage", "Simple", "Normal").setCanShow((m) -> mode.is("Verus"));

    double firstypos;

    public Longjump(){
        addSettings(mode, verusMode, speed);
    }

    int ticks = 0;
    public void onEnable() {
        switch(mode.getMode()){
            case "Verus":{
                ticks = 0;
                switch(verusMode.getMode()){
                    case "Damage":
                        MoveUtils.damage(MoveUtils.Bypass.VERUS);
                        break;
                    case "Normal":
                        firstypos = mc.thePlayer.posY;
                        MoveUtils.strafe(0.334f);
                        mc.thePlayer.jump();
                    case "Simple":{
                        MoveUtils.damage(MoveUtils.Bypass.VERUS);
                    }
                }
            }
        }
    }
    public void onDisable() {
        MoveUtils.motionreset();
        mc.thePlayer.speedInAir = 0.02f;
        mc.thePlayer.jumpMovementFactor = 0.02f;
        switch(mode.getMode()){
            case "Verus":{
                switch(verusMode.getMode()){
                    case "Normal":{
                        MoveUtils.strafe(0.42f);
                        break;
                    }
                }
                break;
            }
        }
    }
    float speedFC = 0f;
    @Override
    public void onEvent(Event ev){
        //if(!this.isToggled()) return;
        if(ev instanceof EventMotion){
            switch(mode.getMode()){
                case "Funcraft":{
                    if(mc.thePlayer.isCollidedVertically){
                        speedFC = 0.75f;
                        mc.thePlayer.jump();
                        mc.thePlayer.speedInAir = 0.27f;
                        mc.thePlayer.jumpMovementFactor = 0.021f;
                    }else{

                        mc.thePlayer.motionX *= 0.75f;
                        mc.thePlayer.motionZ *= 0.75f;
                        MoveUtils.strafe();
                    }

                    break;
                }
                case "Verus":{
                    switch(verusMode.getMode()){
                        case "Damage":{
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                            }
                            int tik = 15;
                            if(ticks < tik){
                                MoveUtils.strafe(speed.getValue());
                                mc.thePlayer.motionY = 0.1;
                            }else{
                                if(ticks < tik+1){
                                    MoveUtils.strafe(0.15f);
                                }
                                if(((Math.round(mc.thePlayer.posY) * 100) / 100) == (int)mc.thePlayer.posY && mc.thePlayer.fallDistance > 0.75){
                                    MoveUtils.strafe(0.27f);
                                    mc.thePlayer.motionY = 0.42f;
                                    mc.thePlayer.fallDistance = 0;

                                }
                            }
                            ticks++;
                            break;
                        }
                        case "Simple":{
                            mc.thePlayer.motionY = 0.10;
                            MoveUtils.strafe(speed.getValue() * 2);
                            break;
                        }
                        case "Normal":{
                            if(mc.thePlayer.ticksExisted % 25 == 0) {
                                mc.thePlayer.motionY = 0.25;
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                                mc.thePlayer.setPosition(mc.thePlayer.posX, firstypos, mc.thePlayer.posZ);
                                MoveUtils.damage(MoveUtils.Bypass.VERUS);
                                MoveUtils.strafe(4);

                            }
                            if(mc.thePlayer.ticksExisted % 4 == 0){
                                mc.thePlayer.motionY = 0.42F;
                                MoveUtils.strafe(0.34f);
                            }else{
                                MoveUtils.strafe(0.12f);
                            }
                            ticks++;
                            break;
                        }
                        case "Highjump":{
                            if(mc.thePlayer.onGround){
                                mc.thePlayer.motionY += 0.21*2;
                            }
                            MoveUtils.strafe();
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

}
