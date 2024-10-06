package uwu.noctura.module.impl.movement;

import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.NumberSetting;

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
