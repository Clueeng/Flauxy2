package uwu.flauxy.module.impl.other;

import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;

@ModuleInfo(name = "Performance", cat = Category.Other, key = -1, displayName = "Performance")
public class Performance extends Module {

    public BooleanSetting noRounded = new BooleanSetting("No Rounded",true);

    public Performance(){
        addSettings(noRounded);
    }

}
