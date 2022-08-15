package uwu.flauxy.module.impl.visuals;

import uwu.flauxy.Flauxy;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "Animations", displayName = "Animations", key = 0, cat = Category.Visuals)
public class Animations extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Smooth", "Smooth", "Up", "Punch", "Vanilla", "Knock", "Swipe", "idk", "Test");
    public ModeSetting hit = new ModeSetting("Hit", "Smooth", "Vanilla", "Smooth");
    public NumberSetting slow = new NumberSetting("Slow", 6, 6, 18, 1);
    public NumberSetting scale = new NumberSetting("Scale", 1, 0.5, 2, 0.05);

    public NumberSetting x = new NumberSetting("X", 0.0, -2.0, 2.0, 0.1).setCanShow(m -> mode.is("Custom"));
    public NumberSetting y = new NumberSetting("Y", 0.0, -2.0, 2.0, 0.1).setCanShow(m -> mode.is("Custom"));
    public NumberSetting Zoom = new NumberSetting("Zoom",0.0, -2.0, 2.0F, 0.1).setCanShow(m -> mode.is("Custom"));
    public BooleanSetting spin = new BooleanSetting("Spin", false).setCanShow(m -> mode.is("Custom"));
    public BooleanSetting smooth = new BooleanSetting("Smoother", true).setCanShow(m -> mode.is("Custom"));


    public Animations() {
        addSettings(mode, hit, slow, x, y, Zoom, spin, smooth, scale);
    }

    public static Animations instance() {
        return Flauxy.INSTANCE.getModuleManager().getModule(Animations.class);
    }
}
