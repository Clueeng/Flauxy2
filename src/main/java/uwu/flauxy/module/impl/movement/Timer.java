package uwu.flauxy.module.impl.movement;

import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "Timer", displayName = "Timer", key = -1, cat = Category.Movement)
public class Timer extends Module {

    public NumberSetting speed = new NumberSetting("Speed", 1.0, 0.01, 10, 0.01);

    public Timer(){
        addSettings(speed);
    }

    public void onEvent(Event e){
        if(e instanceof EventMotion){
            this.setDisplayName("Timer §f" + speed.getValue());
            mc.timer.timerSpeed = (float)speed.getValue();

        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
    }
}
