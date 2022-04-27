package uwu.flauxy.module.impl.movement;

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

@ModuleInfo(name = "Speed", displayName = "Speed", key = Keyboard.KEY_X, cat = Category.Movement)
public class Speed extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "NCP");
    public ModeSetting ncpMode = new ModeSetting("NCP Mode", "Funcraft", "Funcraft", "Hypixel").setCanShow(m -> mode.is("NCP"));
    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Hop", "Hop").setCanShow(m -> mode.is("Verus"));
    public NumberSetting speed = new NumberSetting("Speed", 0.6, 0.2, 2, 0.05).setCanShow(m -> mode.is("Vanilla"));

    public Speed(){
        addSettings(mode, ncpMode, verusMode, speed);
    }

    public void onEvent(Event event){
        if(event instanceof EventMotion){
            switch(mode.getMode()){
                case "NCP":{
                    switch(ncpMode.getMode()){
                        case "Funcraft":{
                            mc.timer.timerSpeed = 1.25f;
                            if(mc.thePlayer.onGround){
                                MoveUtils.strafe(MoveUtils.getMotion() * 1.724);
                                mc.thePlayer.motionY = 0.42f;
                                float speed = 0.0320f;
                                if(mc.thePlayer.speedInAir < speed){
                                   // Wrapper.instance.log(mc.thePlayer.speedInAir + "");
                                    if(mc.thePlayer.speedInAir < 0.025){
                                        mc.thePlayer.speedInAir = (float) (0.025f + (Math.random() / 100));
                                    }
                                    mc.thePlayer.speedInAir += 0.0101f;
                                }else{
                                    mc.thePlayer.speedInAir = speed;
                                }
                                if(mc.thePlayer.jumpMovementFactor > 0.022){
                                    mc.thePlayer.jumpMovementFactor -= 0.002f;
                                }

                            }else{
                                mc.thePlayer.motionX *= 0.988f;
                                mc.thePlayer.motionZ *= 0.988f;
                                mc.thePlayer.speedInAir -= 0.00019f;
                                if(mc.thePlayer.fallDistance > 0.4 && mc.thePlayer.fallDistance < 0.41){
                                    mc.thePlayer.motionY -= 0.1f;
                                }
                                if(mc.thePlayer.hurtTime > 4){
                                    mc.thePlayer.speedInAir+=0.006f;
                                }
                                MoveUtils.strafe();
                            }
                            break;
                        }
                        case "Hypixel":{
                            if(mc.thePlayer.onGround){
                                mc.thePlayer.motionY = 0.42f;
                            }else{

                            }

                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onDisable() {

        mc.timer.timerSpeed = 1.0f;
        mc.thePlayer.jumpMovementFactor = 0.02F;
        mc.thePlayer.speedInAir = 0.02F;
    }
}
