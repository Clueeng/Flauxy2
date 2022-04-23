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

@ModuleInfo(name = "Fly", displayName = "Fly", key = 0, cat = Category.Movement)
public class Fly extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus");
    int ticks = 0;
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 10, 0.1);

    public Fly(){
        addSettings(mode);
    }

    @Override
    public void onEvent(Event ev){
        //if(!this.isToggled()) return;
        if(ev instanceof EventMotion){
            switch(mode.getMode()){
                case "Verus":{
                    if(ticks <= 1){
                        MoveUtils.damage(MoveUtils.Bypass.VERUS);
                        mc.thePlayer.motionY = 0.42f;
                    }else{
                        if(ticks < 50){
                            mc.thePlayer.motionY = -0.08f;
                            MoveUtils.strafe(3f);
                        }else{
                            if(ticks <=  51) MoveUtils.strafe(0);
                            float fallDistance = mc.gameSettings.keyBindJump.pressed ? 0.25f : 1.15f;
                            if(mc.thePlayer.fallDistance > fallDistance){
                                mc.thePlayer.jump();
                                mc.thePlayer.fallDistance = 0;
                            }
                        }
                    }
                    ticks++;
                    break;
                }
                case "Vanilla":{
                    mc.thePlayer.motionY = 0;
                    if(mc.gameSettings.keyBindSneak.pressed && !mc.gameSettings.keyBindJump.pressed){
                        mc.thePlayer.motionY -= 0.42f;
                    }else if(mc.gameSettings.keyBindJump.pressed && !mc.gameSettings.keyBindSneak.pressed){
                        mc.thePlayer.motionY += 0.42f;
                    }

                    if(mc.thePlayer.isMoving()){
                        MoveUtils.strafe(1.45f);
                    } else {
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;
                    }
                    break;
                }
            }
        }
    }

}
