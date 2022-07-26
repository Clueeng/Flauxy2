package uwu.flauxy.module.impl.visuals;

import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;

@ModuleInfo(name = "Emote", displayName = "Emote", key = 0, cat = Category.Visuals)
public class Emote extends Module {

    public int heilY;
    public int hithing = 0;
    public ModeSetting mode = new ModeSetting("Mode", "Dab", "Dab", "Heil", "Jerk", "Hi");

    public Emote() {
        addSettings(mode);
    }
    // ModelBiped.java

    @Override
    public void onEnable() {
        super.onEnable();
        heilY = 0;
    }
}
