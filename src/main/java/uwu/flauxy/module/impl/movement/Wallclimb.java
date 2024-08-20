package uwu.flauxy.module.impl.movement;

import org.lwjgl.system.CallbackI;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;

@ModuleInfo(name = "Wallclimb", displayName = "Wall Climb", key = -1, cat = Category.Movement)
public class Wallclimb extends Module {

    ModeSetting mode = new ModeSetting("Mode","Ladder","Ladder");
    public Wallclimb(){
        addSettings(mode);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventMotion){
            if(mc.thePlayer.isCollidedHorizontally){
                switch (mode.getMode()){
                    case "Ladder":{
                        mc.thePlayer.motionY = 0.11760000228882461; // 0.2
                        break;
                    }
                }
            }
        }
    }
}
