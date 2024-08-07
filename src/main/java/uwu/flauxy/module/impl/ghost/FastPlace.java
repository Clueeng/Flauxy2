package uwu.flauxy.module.impl.ghost;

import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "FastPlace", displayName = "FastPlace", key = -1, cat = Category.Ghost)
public class FastPlace extends Module {

    public NumberSetting delay = new NumberSetting("Delay", 1.0, 0.0, 4.0, 1);

    public FastPlace(){
        addSettings(delay);
    }

    @Override
    public void onEvent(Event e) {
    }

    @Override
    public void onDisable() {
    }
}
