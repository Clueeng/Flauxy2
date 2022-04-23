package uwu.flauxy.module.impl.movement;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.MoveUtils;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_G, cat = Category.Movement)
public class Fly extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus");

    public Fly(){
        addSettings(mode);
    }

    @Override
    public void onEvent(Event ev){
        //if(!this.isToggled()) return;
        if(ev instanceof EventMotion){
            switch(mode.getMode()){
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
