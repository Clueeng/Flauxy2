package uwu.flauxy.module.impl.ghost;

import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "NoClickDelay", displayName = "No Click Delay", cat = Category.Ghost, key = -1)
public class NoClickDelay extends Module {
    public NumberSetting delay = new NumberSetting("Delay",10,0,10,1);
    public NoClickDelay(){
        addSettings(delay);
    }
}
