package uwu.noctura.module.impl.other;

import uwu.noctura.Noctura;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.NumberSetting;

@ModuleInfo(name = "NoFOV", displayName = "No FOV", key = -1, cat = Category.Other)
public class NoFOV extends Module {

    public BooleanSetting noChange = new BooleanSetting("No FOV", true);
    public NumberSetting fovMultiplier = new NumberSetting("Multiplier", 1, 0.5, 2, 0.05);

    public NoFOV(){
        addSettings(noChange, fovMultiplier);
    }

    public static float getMultiplier(){
        NoFOV n = Noctura.INSTANCE.getModuleManager().getModule(NoFOV.class);
        return n.isToggled() ? (float) (n.noChange.isEnabled() ? 1.0f : n.fovMultiplier.getValue()) : 1.0f;
    }

    public static boolean stopFovChange(){
        NoFOV n = Noctura.INSTANCE.getModuleManager().getModule(NoFOV.class);
        return n.isToggled() && n.noChange.isEnabled();
    }

}
