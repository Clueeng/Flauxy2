package uwu.flauxy.module.impl.visuals;

import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "NoRender", key = -1, cat = Category.Visuals, displayName = "No Render")
public class NoRender extends Module {

    public BooleanSetting tweakFire = new BooleanSetting("Tweak Fire",true);
    public BooleanSetting disableFire = new BooleanSetting("Disable Fire",false).setCanShow(f -> tweakFire.isEnabled());
    public NumberSetting fireOpacity = new NumberSetting("Fire Opacity",1.0, 0.0,100.0, 1).setCanShow(f -> tweakFire.isEnabled() && !disableFire.isEnabled());
    public NumberSetting fireHeight = new NumberSetting("Fire Height",0.3, 0.0,1.0, 0.05).setCanShow(f -> tweakFire.isEnabled() && !disableFire.isEnabled());


    public NoRender(){
        addSettings(tweakFire, disableFire, fireOpacity, fireHeight);
    }

}
