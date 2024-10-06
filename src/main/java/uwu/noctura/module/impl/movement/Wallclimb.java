package uwu.noctura.module.impl.movement;

import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.ModeSetting;

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
