package uwu.noctura.module.impl.ghost;

import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.NumberSetting;

@ModuleInfo(name = "Reach", displayName = "Reach", key = -1, cat = Category.Ghost)
public class Reach extends Module {

    public NumberSetting range = new NumberSetting("Reach", 3.0, 3.0, 6.0, 0.05);
    public NumberSetting blockRange = new NumberSetting("Block Reach", 3.0, 3.0, 12.0, 0.1);

    public Reach(){
        addSettings(range, blockRange);
    }

    @Override
    public void onEnable() {

    }
    public static double currentRange = 3.0d;

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            currentRange = range.getValue();
        }
    }

    @Override
    public void onDisable() {
        currentRange = 3.0d;
    }
}
