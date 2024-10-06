package uwu.noctura.module.impl.ghost;

import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.NumberSetting;

@ModuleInfo(name = "NoClickDelay", displayName = "No Click Delay", cat = Category.Ghost, key = -1)
public class NoClickDelay extends Module {
    public NumberSetting delay = new NumberSetting("Delay",10,0,10,1);
    public NoClickDelay(){
        addSettings(delay);
    }
}
