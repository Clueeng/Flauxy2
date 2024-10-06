package uwu.noctura.module.impl.ghost;

import org.lwjgl.input.Mouse;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.NumberSetting;

@ModuleInfo(name = "FastPlace", displayName = "FastPlace", key = -1, cat = Category.Ghost)
public class FastPlace extends Module {

    public NumberSetting delay = new NumberSetting("Delay", 1.0, 0.0, 4.0, 1);
    public NumberSetting holdDelay = new NumberSetting("Hold Delay",0.0, 0.0,10,1);

    public int heldTicks;

    public FastPlace(){
        addSettings(delay, holdDelay);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            if(Mouse.isButtonDown(1)){
                heldTicks++;
            }else{
                heldTicks = 0;
            }
        }
    }

    @Override
    public void onDisable() {
    }
}
