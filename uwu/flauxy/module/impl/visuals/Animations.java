package uwu.flauxy.module.impl.visuals;

import uwu.flauxy.Flauxy;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "Animations", displayName = "Animations", key = 0, cat = Category.Visuals)
public class Animations extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Old", "Swank", "Swong", "Exhibition", "Stella", "Smooth", "Sigma", "Ethernal", "Dortware", "Mega", "Punch", "Plain", "Up", "Skid", "Jello", "Flux", "Astolfo", "Old Sigma", "Autumn", "Slide", "Rare", "Jigsaw", "Luna");
    public ModeSetting hit = new ModeSetting("Hit", "Vanilla", "Vanilla", "Smooth");
    public NumberSetting scale = new NumberSetting("Scale", 1, 0.5, 2, 0.05);

    public Animations() {
        addSettings(mode, hit, scale);
    }

    public static Animations instance() {
        return Flauxy.INSTANCE.getModuleManager().getModule(Animations.class);
    }
}
