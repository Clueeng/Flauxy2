package uwu.flauxy.module.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_G, cat = Category.Movement)
public class Fly extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus");

    public Fly(){
        addSettings(mode);
    }

    @EventTarget
    public void onMotion(EventMotion ev){
        //if(!this.isToggled()) return;
        switch(mode.getMode()){
            case "Vanilla":{
                if(!mc.gameSettings.keyBindJump.pressed && !mc.gameSettings.keyBindSneak.pressed){
                    mc.thePlayer.motionY = 0;
                }else if(mc.gameSettings.keyBindSneak.pressed && !mc.gameSettings.keyBindJump.pressed){
                    mc.thePlayer.motionY = -0.42f;
                }else if(mc.gameSettings.keyBindJump.pressed && !mc.gameSettings.keyBindSneak.pressed){
                    mc.thePlayer.motionY = 0.42f;
                }
                if(mc.thePlayer.isMoving()){
                    MoveUtils.strafe(0.65f);
                } else {
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                }
                break;
            }
        }
    }

}
