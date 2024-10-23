package uwu.noctura.module.impl.falses;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.timer.Timer;

@ModuleInfo(name = "TickFreezer", displayName = "Tick Freezer", cat = Category.False, key = -1)
public class TickFreezer extends Module {

    NumberSetting msTime = new NumberSetting("Time", 1000, 0, 15000, 50);
    NumberSetting frequency = new NumberSetting("Frequency", 100, 0, 10000, 50);
    Timer timer = new Timer();

    public TickFreezer(){
        addSettings(msTime, frequency);
    }

    long enabledtime;
    long restartTime;

    @Override
    public void onEnable() {
        enabledtime = System.currentTimeMillis();
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRender2D){
            //if(timer.hasTimeElapsed(frequency.getValue(), true)){
            if(restartTime <= 1){
                timer.reset();
                while(Math.abs(enabledtime - System.currentTimeMillis()) < msTime.getValue() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
                    restartTime++;
                }
                enabledtime = System.currentTimeMillis();
                Wrapper.instance.log("Starting freeze");
                if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
                    this.toggle();
                }
            }
            if(timer.hasTimeElapsed(frequency.getValue(), false)){
                restartTime = 0;
            }
            //}
        }
    }
}
