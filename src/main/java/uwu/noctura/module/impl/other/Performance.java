package uwu.noctura.module.impl.other;

import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;

@ModuleInfo(name = "Performance", cat = Category.Other, key = -1, displayName = "Performance")
public class Performance extends Module {

    public BooleanSetting noRounded = new BooleanSetting("No Rounded",true);

    public Performance(){
        addSettings(noRounded);
    }

}
