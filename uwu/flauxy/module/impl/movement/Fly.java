package uwu.flauxy.module.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_G, cat = Category.Movement)
public class Fly extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus");

    @EventTarget
    public void onMotion(EventMotion ev){
        //if(!this.isToggled()) return;
        switch(mode.getMode()){
            case "Vanilla":{
                getMc().thePlayer.motionY = 0;
                getMc().thePlayer.onGround = true;
                break;
            }
        }
    }

}
